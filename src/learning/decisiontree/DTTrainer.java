package learning.decisiontree;

import core.Duple;
import learning.core.Histogram;
import learning.core.Updateable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.lang.Math;
import java.util.Random;

public class DTTrainer<V,L, F, FV extends Comparable<FV>> {
	private ArrayList<Duple<V,L>> baseData;
	private boolean restrictFeatures;
	private Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures;
	private BiFunction<V,F,FV> getFeatureValue;
	private Function<FV,FV> successor;

	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 boolean restrictFeatures, BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		baseData = data;
		this.restrictFeatures = restrictFeatures;
		this.allFeatures = allFeatures;
		this.getFeatureValue = getFeatureValue;
		this.successor = successor;
	}

	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		this(data, allFeatures, false, getFeatureValue, successor);
	}

	// TODO: Call allFeatures.apply() to get the feature list. Then shuffle the list, retaining
	//  only targetNumber features. Should pass DTTest.testReduced().
	public static <V,L, F, FV  extends Comparable<FV>> ArrayList<Duple<F,FV>>
	reducedFeatures(ArrayList<Duple<V,L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					int targetNumber) {
		ArrayList<Duple<F, FV>> featureList = allFeatures.apply(data);
		ArrayList<Duple<F, FV>> newFeatures = new ArrayList<>();
		Collections.shuffle(featureList);
		for (Duple<F, FV> ffvDuple : featureList){
			if (newFeatures.size()< targetNumber){
				newFeatures.add(ffvDuple);
			}
			else{
				break;
			}
		}

		return newFeatures;
	}

	public DecisionTree<V,L,F,FV> train() {
		return train(baseData);
	}

	public static <V,L> int numLabels(ArrayList<Duple<V,L>> data) {
		return data.stream().map(Duple::getSecond).collect(Collectors.toUnmodifiableSet()).size();
	}

	private DecisionTree<V,L,F,FV> train(ArrayList<Duple<V,L>> data) {
		// TODO: Implement the decision tree learning algorithm
		if (numLabels(data) == 1) {
			// TODO: Return a leaf node consisting of the only label in data
			return new DTLeaf<V,L,F,FV>(data.get(0).getSecond());

		} else {
			// TODO: Return an interior node.
			//  If restrictFeatures is false, call allFeatures.apply() to get a complete list
			//  of features and values, all of which you should consider when splitting.
			//  If restrictFeatures is true, call reducedFeatures() to get sqrt(# features)
			//  of possible features/values as candidates for the split. In either case,
			//  for each feature/value combination, use the splitOn() function to break the
			//  data into two parts. Then use gain() on each split to figure out which
			//  feature/value combination has the highest gain. Use that combination, as
			//  well as recursively created left and right nodes, to create the new
			//  interior node.
			//  Note: It is possible for the split to fail; that is, you can have a split
			//  in which one branch has zero elements. In this case, return a leaf node
			//  containing the most popular label in the branch that has elements.
			ArrayList<Duple<F, FV>> stuff = null;
			if (!restrictFeatures) {
				stuff = allFeatures.apply(data);
			} else {
				stuff = reducedFeatures(data, allFeatures, (int) Math.sqrt(data.size()));
			}
			Duple<F, FV> bestCombo = null;
			double bestGain = (Double.NEGATIVE_INFINITY);
			ArrayList<Duple<V,L>> bestLeft= null;
			ArrayList<Duple<V,L>> bestRight= null;

			for (Duple<F, FV> features : stuff) {

				Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> trees = splitOn(data, features.getFirst(), features.getSecond(), getFeatureValue);
				if (gain(data, trees.getFirst(), trees.getSecond()) > bestGain) {
					bestGain = gain(data, trees.getFirst(), trees.getSecond());
					bestCombo = features;
					bestLeft = trees.getFirst();
					bestRight = trees.getSecond();
				}
			}
			if ((bestLeft == null )|| (bestLeft.size() == 0)) {
				L mostPopL = mostPopularLabelFrom(bestRight);
				return new DTLeaf<V,L,F,FV>(mostPopL);
			} else if ((bestRight == null) || (bestRight.size() == 0)) {
				L mostPopL = mostPopularLabelFrom(bestLeft);
				return new DTLeaf<V,L,F,FV>(mostPopL);
			}
			return new DTInterior<V,L,F,FV>
						(bestCombo.getFirst(), bestCombo.getSecond(), train(bestLeft) , train(bestRight), getFeatureValue, successor);

		}
	}

	public static <V,L> L mostPopularLabelFrom(ArrayList<Duple<V, L>> data) {
		Histogram<L> h = new Histogram<>();
		for (Duple<V,L> datum: data) {
			h.bump(datum.getSecond());
		}
		return h.getPluralityWinner();
	}


	// TODO: Generates a new data set by sampling randomly with replacement. It should return
	//    an `ArrayList` that is the same length as `data`, where each element is selected randomly
	//    from `data`. Should pass `DTTest.testResample()`.
	public static <V,L> ArrayList<Duple<V,L>> resample(ArrayList<Duple<V,L>> data) {
		ArrayList<Duple<V,L>> newdata = new ArrayList<>();
		Random rand = new Random();
		for(int i = 0; i < data.size();i++){
			newdata.add(data.get(rand.nextInt(0, data.size())));
		}
		return newdata;
	}

	public static <V,L> double getGini(ArrayList<Duple<V,L>> data) {
		// TODO: Calculate the Gini coefficient:
		//  For each label, calculate its portion of the whole (p_i).
		//  Use of a Histogram<L> for this purpose is recommended.
		//  Gini coefficient is 1 - sum(for all labels i, p_i^2)
		//  Should pass DTTest.testGini().
		double sum = 0;
		double total = 0;
		Histogram<L> labelcounts = new Histogram<>();
		for(int i= 0; i < data.size(); i++){
			L label = data.get(i).getSecond();
			labelcounts.bump(label);
			total+=1;
		}

		for (L labels: labelcounts){

			sum += Math.pow((labelcounts.getCountFor(labels)/total), 2);
		}
		return 1-sum;
	}

	public static <V,L> double gain(ArrayList<Duple<V,L>> parent, ArrayList<Duple<V,L>> child1,
									ArrayList<Duple<V,L>> child2) {
		// TODO: Calculate the gain of the split. Add the gini values for the children.
		//  Subtract that sum from the gini value for the parent. Should pass DTTest.testGain().

		return -(getGini(child1)+getGini(child2)-getGini(parent));
	}

	public static <V,L, F, FV  extends Comparable<FV>> Duple<ArrayList<Duple<V,L>>,ArrayList<Duple<V,L>>> splitOn
			(ArrayList<Duple<V,L>> data, F feature, FV featureValue, BiFunction<V,F,FV> getFeatureValue) {
		// TODO:
		//  Returns a duple of two new lists of training data.
		//  The first returned list should be everything from this set for which
		//  feature has a value less than or equal to featureValue. The second
		//  returned list should be everything else from this list.
		//  Should pass DTTest.testSplit().
		ArrayList<Duple<V,L>> lessThanFeatureVal = new ArrayList<>();
		ArrayList<Duple<V,L>> others = new ArrayList<>();

		for(Duple<V,L> items: data ){
			FV fet = getFeatureValue.apply(items.getFirst(), feature);


			if (fet.compareTo(featureValue)<=0){
				lessThanFeatureVal.add(items);
			}else{
				others.add(items);
			}
		}
		Duple<ArrayList<Duple<V,L>>,ArrayList<Duple<V,L>>> all = new Duple<ArrayList<Duple<V,L>>,ArrayList<Duple<V,L>>>(lessThanFeatureVal, others);
		return all;
	}
}