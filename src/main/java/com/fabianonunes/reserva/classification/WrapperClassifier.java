package com.fabianonunes.reserva.classification;

import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class WrapperClassifier extends Classifier {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns only the toString() method.
	 * 
	 * @return a string describing the classifier
	 */
	public String globalInfo() {
		return toString();
	}

	/**
	 * Returns the capabilities of this classifier.
	 * 
	 * @return the capabilities
	 */
	public Capabilities getCapabilities() {
		weka.core.Capabilities result = new weka.core.Capabilities(this);

		result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
		result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
		result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);

		result.setMinimumNumberInstances(0);

		return result;
	}

	/**
	 * only checks the data against its capabilities.
	 * 
	 * @param i
	 *            the training data
	 */
	public void buildClassifier(Instances i) throws Exception {
		// can classifier handle the data?
		getCapabilities().testWithFail(i);
	}

	/**
	 * Classifies the given instance.
	 * 
	 * @param i
	 *            the instance to classify
	 * @return the classification result
	 */
	public double classifyInstance(Instance i) throws Exception {
		Object[] s = new Object[i.numAttributes()];

		for (int j = 0; j < s.length; j++) {
			if (!i.isMissing(j)) {
				if (i.attribute(j).isNominal())
					s[j] = new String(i.stringValue(j));
				else if (i.attribute(j).isNumeric())
					s[j] = new Double(i.value(j));
			}
		}

		// set class value to missing
		s[i.classIndex()] = null;

		return WekaClassifier.classify(s);
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("1.0");
	}

	/**
	 * Returns only the classnames and what classifier it is based on.
	 * 
	 * @return a short description
	 */
	public String toString() {
		return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.6.4).\n"
				+ this.getClass().getName() + "/WekaClassifier";
	}

}

class WekaClassifier {

	public static double classify(Object[] i) throws Exception {

		double p = Double.NaN;
		p = WekaClassifier.N1e4ad14c0(i);
		return p;
	}

	static double N1e4ad14c0(Object[] i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 7.603278725) {
			p = WekaClassifier.N41f2e41d1(i);
		} else if (((Double) i[0]).doubleValue() > 7.603278725) {
			p = WekaClassifier.N4cc6835110(i);
		}
		return p;
	}

	static double N41f2e41d1(Object[] i) {
		double p = Double.NaN;
		if (i[3] == null) {
			p = 1;
		} else if (((Double) i[3]).doubleValue() <= 26.19057254) {
			p = WekaClassifier.N315e4dbe2(i);
		} else if (((Double) i[3]).doubleValue() > 26.19057254) {
			p = WekaClassifier.N2d4b1fda5(i);
		}
		return p;
	}

	static double N315e4dbe2(Object[] i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 3.528679588) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() > 3.528679588) {
			p = WekaClassifier.N26420e443(i);
		}
		return p;
	}

	static double N26420e443(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() <= 8.741150489) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() > 8.741150489) {
			p = WekaClassifier.N3ab6a5fb4(i);
		}
		return p;
	}

	static double N3ab6a5fb4(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() <= 37.89588278) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() > 37.89588278) {
			p = 0;
		}
		return p;
	}

	static double N2d4b1fda5(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() <= 17.82569021) {
			p = WekaClassifier.N1f1838716(i);
		} else if (((Double) i[2]).doubleValue() > 17.82569021) {
			p = 0;
		}
		return p;
	}

	static double N1f1838716(Object[] i) {
		double p = Double.NaN;
		if (i[3] == null) {
			p = 1;
		} else if (((Double) i[3]).doubleValue() <= 46.87333396) {
			p = WekaClassifier.N6f2192a97(i);
		} else if (((Double) i[3]).doubleValue() > 46.87333396) {
			p = 0;
		}
		return p;
	}

	static double N6f2192a97(Object[] i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 1.613660993) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() > 1.613660993) {
			p = WekaClassifier.N523ce3f8(i);
		}
		return p;
	}

	static double N523ce3f8(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 0;
		} else if (((Double) i[2]).doubleValue() <= 0.643377406) {
			p = 0;
		} else if (((Double) i[2]).doubleValue() > 0.643377406) {
			p = WekaClassifier.N71b98cbb9(i);
		}
		return p;
	}

	static double N71b98cbb9(Object[] i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 6.295946889) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() > 6.295946889) {
			p = 0;
		}
		return p;
	}

	static double N4cc6835110(Object[] i) {
		double p = Double.NaN;
		if (i[3] == null) {
			p = 0;
		} else if (((Double) i[3]).doubleValue() <= 9.585170025) {
			p = WekaClassifier.N7cd7623711(i);
		} else if (((Double) i[3]).doubleValue() > 9.585170025) {
			p = 0;
		}
		return p;
	}

	static double N7cd7623711(Object[] i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() <= 30.24952895) {
			p = WekaClassifier.N207148e912(i);
		} else if (((Double) i[0]).doubleValue() > 30.24952895) {
			p = 0;
		}
		return p;
	}

	static double N207148e912(Object[] i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() <= 2.393050524) {
			p = 1;
		} else if (((Double) i[2]).doubleValue() > 2.393050524) {
			p = WekaClassifier.N6d69c9a213(i);
		}
		return p;
	}

	static double N6d69c9a213(Object[] i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 9.15470725) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() > 9.15470725) {
			p = 0;
		}
		return p;
	}
}
