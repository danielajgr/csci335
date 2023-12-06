package learning.classifiers;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleBiFunction;

// KnnTest.test() should pass once this is finished.
public class Knn<V, L> implements Classifier<V, L> {
    private ArrayList<Duple<V, L>> data = new ArrayList<>();
    private ToDoubleBiFunction<V, V> distance;
    private int k;

    public Knn(int k, ToDoubleBiFunction<V, V> distance) {
        this.k = k;
        this.distance = distance;
    }

    @Override
    public L classify(V value) {
        // TODO: Find the distance from value to each element of data. Use Histogram.getPluralityWinner()
        //  to find the most popular label.
        PriorityQueue<Duple<L,Double>> queue = new PriorityQueue<>(Comparator.comparing(Duple::getSecond));
        for(int i = 0; i < data.size(); i++) {
            Duple<L,Double> dup = new Duple<>(data.get(i).getSecond(),distance.applyAsDouble(data.get(i).getFirst(),value));
            queue.add(dup);
        }
        Histogram<L> hist = new Histogram<>();
        for(int i = 0; i < queue.size(); i++) {
            hist.bump(queue.remove().getFirst());
        }
        return hist.getPluralityWinner();
    }

    @Override
    public void train(ArrayList<Duple<V, L>> training) {
        // TODO: Add all elements of training to data.
        for(int i = 0; i < training.size(); i++){
            data.add(data.size(),training.get(i));
        }
    }
}
