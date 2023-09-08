package learning.markov;

import learning.core.Histogram;

import java.util.*;

public class MarkovChain<L,S> {
    private LinkedHashMap<L, HashMap<Optional<S>, Histogram<S>>> label2symbol2symbol = new LinkedHashMap<>();

    public Set<L> allLabels() {return label2symbol2symbol.keySet();}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (L language: label2symbol2symbol.keySet()) {
            sb.append(language);
            sb.append('\n');
            for (Map.Entry<Optional<S>, Histogram<S>> entry: label2symbol2symbol.get(language).entrySet()) {
                sb.append("    ");
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue().toString());
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // Increase the count for the transition from prev to next.
    // Should pass SimpleMarkovTest.testCreateChains().
    public void count(Optional<S> prev, L label, S next) {
        // TODO: YOUR CODE HERE
        if (!label2symbol2symbol.containsKey(label)) {
            label2symbol2symbol.put(label, new HashMap<>());
        }
        if (!label2symbol2symbol.get(label).containsKey(prev)){
            label2symbol2symbol.get(label).put(prev,new Histogram<>());
            }
        label2symbol2symbol.get(label).get(prev).bump(next);


    }

    // Returns P(sequence | label)
    // Should pass SimpleMarkovTest.testSourceProbabilities() and MajorMarkovTest.phraseTest()
    //
    // HINT: Be sure to add 1 to both the numerator and denominator when finding the probability of a
    // transition. This helps avoid sending the probability to zero.
    public double probability(ArrayList<S> sequence, L label) {
        // TODO: YOUR CODE HERE
        double prob=1;
        Optional<S> prev= Optional.empty();
        for(int i = 0; i< sequence.size(); i++){
            if(label2symbol2symbol.get(label).containsKey(prev)){
                double num = label2symbol2symbol.get(label).get(prev).getCountFor(sequence.get(i))+1;
                double dom = label2symbol2symbol.get(label).get(prev).getTotalCounts()+1;
                prob*=num/dom;

            }
            prev = Optional.of(sequence.get(i));
        }
        return prob;
    }

    // Return a map from each label to P(label | sequence).
    // Should pass MajorMarkovTest.testSentenceDistributions()
    public LinkedHashMap<L,Double> labelDistribution(ArrayList<S> sequence) {
        // TODO: YOUR CODE HERE
        double total = 0;
        LinkedHashMap<L,Double> map = new LinkedHashMap<>();
        Set<L> keys = label2symbol2symbol.keySet();
        for(L key: keys){
            double prob = probability(sequence, key);
            total+=prob;
            map.put(key, probability(sequence, key));
        }
        for(L key: keys){
            map.put(key, probability(sequence, key)/total);
        }
        return map;
    }

    // Calls labelDistribution(). Returns the label with highest probability.
    // Should pass MajorMarkovTest.bestChainTest()
    public L bestMatchingChain(ArrayList<S> sequence) {
        // TODO: YOUR CODE HERE
        double big = 0;
        L biglabel = null;
        LinkedHashMap<L,Double> map_o_probs = labelDistribution(sequence);
        Set<L> keys = map_o_probs.keySet();
        for(L key: keys){
            if(map_o_probs.get(key) > big){
                big = map_o_probs.get(key);
                biglabel = key;
            }
        }

        return biglabel;
    }
}
