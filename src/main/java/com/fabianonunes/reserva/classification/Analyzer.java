package com.fabianonunes.reserva.classification;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Analyzer {

	public static double check(double vtop, double vmiddle, double vbottom)
			throws Exception {

		Attribute top = new Attribute("top");
		Attribute removed = new Attribute("removed");
		Attribute middle = new Attribute("middle");
		Attribute bottom = new Attribute("bottom");

		FastVector attributes = new FastVector(4);
		attributes.addElement(top);
		attributes.addElement(removed);
		attributes.addElement(middle);
		attributes.addElement(bottom);

		Instances unlabeled = new Instances("relation", attributes, 10);
		unlabeled.setClassIndex(1);

		Instance instance = new Instance(4);
		instance.setValue(top, vtop);
		instance.setValue(middle, vmiddle);
		instance.setValue(bottom, vbottom);
		instance.setValue(removed, 0d);

		instance.setDataset(unlabeled);
		unlabeled.add(instance);

		WekaWrapper wrapper = new WekaWrapper();

		return wrapper.classifyInstance(instance);

	}
}
