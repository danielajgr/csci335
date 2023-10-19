package learning.sentiment.learners;

import core.Duple;
import learning.classifiers.NaiveBayes;
import learning.core.Histogram;

import java.util.ArrayList;

public class SentimentNaive extends NaiveBayes<Histogram<String>, String, String> {
    public SentimentNaive() {
        super(h -> {
            ArrayList<Duple<String,Integer>> allCounts = new ArrayList<>();
            for (String word: h) {
                allCounts.add(new Duple<>(word, h.getCountFor(word)));
            }
            return allCounts;
        });
    }
}
