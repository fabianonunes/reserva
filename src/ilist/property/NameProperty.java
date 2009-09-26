package ilist.property;

public class NameProperty extends Property<String> {

	public static final String KEY = "name";
	
	
	public NameProperty(String name) {
		this.setValue(name);
	}
	
	@Override
	public String getKey() {
		return KEY;
	}

}
