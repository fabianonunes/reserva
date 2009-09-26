package ilist.property;


public class KeyProperty extends Property<String> {

	public static final String KEY = "key";
	
	
	public KeyProperty(String key) {
		this.setValue(key);
	}
	
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void setValue(String value) {
		if (this.getValue() == null)
			super.setValue(value);
	}

}
