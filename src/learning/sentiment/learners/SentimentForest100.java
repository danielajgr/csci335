package learning.sentiment.learners;

import learning.core.Histogram;
import learning.decisiontree.RandomForest;
import learning.sentiment.core.SentimentAnalyzer;

public class SentimentForest100 extends RandomForest<Histogram<String>, String, String, Integer> {
    public SentimentForest100() {
        super(100, SentimentAnalyzer::allFeatures, Histogram::getCountFor, i -> i + 1);
    }
}
