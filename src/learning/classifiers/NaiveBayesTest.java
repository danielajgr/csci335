package learning.classifiers;

import core.Duple;
import learning.core.Histogram;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class NaiveBayesTest {
    @Test
    public void test() {
        ArrayList<Duple<String,String>> data = new ArrayList<>();
        String[] values = new String[]{"abad", "defe", "wxyx", "adax"};
        String[] categories = new String[]{"A", "B", "B", "A"};
        for (int i = 0; i < values.length; i++) {
            data.add(new Duple<>(values[i], categories[i]));
        }

        NaiveBayes<String,String,Character> nb = new NaiveBayes<>(s -> {
            Histogram<Character> h = new Histogram<>();
            for (char c: s.toCharArray()) {
                h.bump(c);
            }
            ArrayList<Duple<Character,Integer>> result = new ArrayList<>();
            for (Character c: h) {
                result.add(new Duple<>(c, h.getCountFor(c)));
            }
            return new ArrayList<>(result);
        });

        nb.train(data);
        for (Duple<String,String> datum: data) {
            assertEquals(datum.getSecond(), nb.classify(datum.getFirst()));
        }

        assertEquals("B", nb.classify("yghi"));
        assertEquals("A", nb.classify("aghi"));
    }
}
