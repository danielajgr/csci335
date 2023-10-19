package learning.som;

import core.Duple;
import learning.handwriting.core.Drawing;
import learning.handwriting.core.FloatDrawing;
import learning.classifiers.SOMRecognizer;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class SelfOrgMapTest {
    final static int TEST_WIDTH = 3;
    final static int TEST_HEIGHT = 4;
    final static int TEST_VALUES = TEST_HEIGHT * TEST_WIDTH;

    FloatDrawing fd1 = setupDrawing(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
    FloatDrawing fd2 = setupDrawing(new double[]{1, 3, 5, 7, 9, 11, 2, 4, 6, 8, 10, 12});

    static int flattenIndex(int x, int y, FloatDrawing f) {
        return x * f.getHeight() + y;
    }

    static FloatDrawing setupDrawing(double[] values) {
        assertEquals(TEST_VALUES, values.length);
        FloatDrawing f = new FloatDrawing(TEST_WIDTH, TEST_HEIGHT);
        for (int x = 0; x < f.getWidth(); x++) {
            for (int y = 0; y < f.getHeight(); y++) {
                f.set(x, y, values[flattenIndex(x, y, f)]);
            }
        }
        return f;
    }

    void testDrawing(FloatDrawing f, double[] testValues) {
        for (int x = 0; x < f.getWidth(); x++) {
            for (int y = 0; y < f.getHeight(); y++) {
                assertEquals(testValues[flattenIndex(x, y, f)], f.get(x, y), 0.001);
            }
        }
    }

    @Test
    public void testAvg() {
        fd1 = FloatDrawing.weightedAverageOf(fd2, fd1, 0.2);
        double[] target = new double[]{1.0, 2.2, 3.4, 4.6, 5.8, 7.0, 6.0, 7.2, 8.4, 9.6, 10.8, 12};
        testDrawing(fd1, target);
    }

    @Test
    public void testEuclideanDistance() {
        assertEquals(110.0, fd1.euclideanDistance(fd2), 0.01);
    }

    @Test
    public void testTrain() {
        SelfOrgMap<FloatDrawing> som = new SelfOrgMap<>(3, () -> new FloatDrawing(2, 2), FloatDrawing::euclideanDistance, FloatDrawing::weightedAverageOf);
        Drawing d1 = new Drawing(2, 2);
        d1.set(0, 0, true);
        d1.set(0, 1, true);
        d1.set(1, 0, false);
        d1.set(1, 1, true);
        FloatDrawing f1 = new FloatDrawing(d1);
        som.train(f1);
        assertEquals(new SOMPoint(0, 0), som.bestFor(f1));
        FloatDrawing n0 = new FloatDrawing(new double[][]{{0.9, 0.9}, {0.0, 0.9}});
        FloatDrawing n1 = new FloatDrawing(new double[][]{{0.4, 0.4}, {0.0, 0.4}});
        FloatDrawing n2 = new FloatDrawing(new double[][]{{0.0, 0.0}, {0.0, 0.0}});

        assertEquals(n0, som.getNode(0, 0));
        assertEquals(n1, som.getNode(0, 1));
        assertEquals(n1, som.getNode(1, 0));
        assertEquals(n2, som.getNode(0, 2));
        assertEquals(n2, som.getNode(2, 0));
        assertEquals(n2, som.getNode(1, 1));
        assertEquals(n2, som.getNode(1, 2));
        assertEquals(n2, som.getNode(2, 1));
        assertEquals(n2, som.getNode(2, 2));

        Drawing d2 = new Drawing(2, 2);
        d1.set(0, 0, false);
        d1.set(0, 1, true);
        d1.set(1, 0, false);
        d1.set(1, 1, false);
        FloatDrawing f2 = new FloatDrawing(d2);
        som.train(f2);
        assertEquals(new SOMPoint(2, 0), som.bestFor(f2));

        FloatDrawing n3 = new FloatDrawing(new double[][]{{0.24, 0.24}, {0.0, 0.24}});
        assertEquals(n0, som.getNode(0, 0));
        assertEquals(n1, som.getNode(0, 1));
        assertEquals(n3, som.getNode(1, 0));
        assertEquals(n2, som.getNode(0, 2));
        assertEquals(n2, som.getNode(2, 0));
        assertEquals(n2, som.getNode(1, 1));
        assertEquals(n2, som.getNode(1, 2));
        assertEquals(n2, som.getNode(2, 1));
        assertEquals(n2, som.getNode(2, 2));
    }

    @Test
    public void testLabel() {
        FloatDrawing n0 = new FloatDrawing(new double[][]{{1.0, 1.0}, {0.0, 1.0}});
        ArrayList<Duple<FloatDrawing,String>> samples = new ArrayList<>();
        samples.add(new Duple<>(new FloatDrawing(new Drawing("2|2|XO|OO")), "A"));
        samples.add(new Duple<>(new FloatDrawing(new Drawing("2|2|XX|OX")), "B"));
        assertEquals("B", SOMRecognizer.findLabelFor(n0, 1, samples, FloatDrawing::euclideanDistance));
    }
}
