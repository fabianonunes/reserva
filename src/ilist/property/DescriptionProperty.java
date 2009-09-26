package ilist.property;

public class DescriptionProperty extends Property<String> {

	public static final String KEY = "description";
	
	
	public DescriptionProperty(String desc) {
		this.setValue(desc);
	}
	
	@Override
	public String getKey() {
		return KEY;
	}
	
}
