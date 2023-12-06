package learning.handwriting.learners;

import core.Duple;
import learning.core.Classifier;
import learning.decisiontree.DTTrainer;
import learning.decisiontree.DecisionTree;
import learning.handwriting.core.Drawing;
import learning.handwriting.core.DrawingPoint;
import learning.handwriting.core.PixelUse;
import learning.handwriting.core.PixelUsePattern;
import learning.handwriting.gui.PixelUseVisualizer;
import learning.handwriting.gui.PixelUser;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DrawingTree implements Classifier<Drawing,String>, PixelUser {
	private DecisionTree<Drawing,String, DrawingPoint, PixelUse> root;
	private int width, height;

	@Override
	public void train(ArrayList<Duple<Drawing, String>> data) {
		this.width = data.get(0).getFirst().getWidth();
		this.height = data.get(0).getFirst().getHeight();
		DTTrainer<Drawing,String,DrawingPoint,PixelUse> trainer = new DTTrainer<>(data, Drawing::allFeatures,
				Drawing::getFeatureValue, PixelUse::successor);
		root = trainer.train();
	}

	@Override
	public String classify(Drawing d) {
		return root.classify(d);
	}

	@Override
	public Optional<JPanel> getVisualization() {
		return Optional.of(new PixelUseVisualizer(this));
	}

	@Override
	public String toString() {
		return root.toString();
	}

	@Override
	public Set<String> getLabels() {
		Set<String> result = new HashSet<>();
		root.addAllLabels(result);
		return result;
	}

	@Override
	public PixelUsePattern getPixelUse(String label) {
		PixelUsePattern pixels = new PixelUsePattern(width, height);
		root.visualize(label, new ArrayList<>(), pixels);
		return pixels;
	}
}