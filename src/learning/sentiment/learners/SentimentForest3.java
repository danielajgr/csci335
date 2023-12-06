package learning.sentiment.learners;

import learning.core.Histogram;
import learning.decisiontree.RandomForest;
import learning.sentiment.core.SentimentAnalyzer;

public class SentimentForest3 extends RandomForest<Histogram<String>, String, String, Integer> {
    public SentimentForest3() {
        super(3, SentimentAnalyzer::allFeatures, Histogram::getCountFor, i -> i + 1);
    }
}
