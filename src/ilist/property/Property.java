package ilist.property;

public abstract class Property<T> {
	
	private T value;
	

	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public abstract String getKey();
	
	@SuppressWarnings("unchecked")
	public Property<T> getEmptyClone() throws CloneNotSupportedException {
		Property<T> clone = (Property<T>) this.clone();
		clone.setValue(null);
		
		return clone;
	}
	
}
