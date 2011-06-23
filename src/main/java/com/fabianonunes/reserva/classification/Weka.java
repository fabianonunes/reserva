package com.fabianonunes.reserva.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class Weka {

	public static void main(String[] args) throws Exception {

		File trained = new File("/home/fabiano/Desktop/thumbs.model");

		File dataset = new File("/home/fabiano/Desktop/dataset.arff");

		Instances header = new Instances(new BufferedReader(new FileReader(
				dataset)));
		header.setClassIndex(1);
		
		J48 tree = (J48) SerializationHelper.read(trained.getAbsolutePath());

		Attribute top = new Attribute("top");
		Attribute removed = new Attribute("removed");
		Attribute middle = new Attribute("middle");
		Attribute bottom = new Attribute("bottom");

		FastVector atts = new FastVector(4);
		atts.addElement(top);
		atts.addElement(removed);
		atts.addElement(middle);
		atts.addElement(bottom);

		Instances unlabeled = new Instances("rel", atts, 10);
		unlabeled.setClass(removed);
		unlabeled.setClassIndex(1);

		Instance instance = new Instance(4);
		instance.setValue(top, 0.630743834d);
		instance.setValue(removed, 1d);
		instance.setValue(middle, 35.267206d);
		instance.setValue(bottom, 26.33551d);

		Instance instance2 = new Instance(4);
		instance2.setValue(top, 9d);
		instance2.setValue(removed, 0d);
		instance2.setValue(middle, 3d);
		instance2.setValue(bottom, 5d);

		// instance.setValue(header.attribute(0), 9d);
		// instance.setValue(header.attribute(1), 0d);
		// instance.setValue(header.attribute(2), 3d);
		// instance.setValue(header.attribute(3), 5d);

		instance.setDataset(unlabeled);
		instance2.setDataset(unlabeled);
		
		unlabeled.add(instance2);
		unlabeled.add(instance);
		

		// unlabeled.add(instance);

		WekaWrapper wrapper = new WekaWrapper();

		double pred = tree.classifyInstance(instance);
		System.out.println(wrapper.classifyInstance(instance));
		System.out.println(instance.classValue() + " -> " + pred);
		
		pred = tree.classifyInstance(instance2);
		System.out.println(wrapper.classifyInstance(instance2));
		System.out.println(instance2.classValue() + " -> " + pred);

		// Evaluation eTest = new Evaluation(tree.isTrainingSet);
		// eTest.evaluateModel(tree, isTrainingSet);

		// FileReader fileReader = new FileReader(trained);
		//
		// Instances train = new Instances(fileReader);

	}
}
