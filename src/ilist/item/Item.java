package ilist.item;

import ilist.property.Property;

import java.util.ArrayList;
import java.util.List;


public class Item {

	private List<Property<?>> properties = new ArrayList<Property<?>>();
	
	
	public List<Property<?>> getProperties() {
		return new ArrayList<Property<?>>(properties);
	}
	
	public boolean addProperty(Property<?> p) {
		return this.properties.add(p);
	}
	
	public boolean removeProperty(Property<?> p) {
		return this.properties.remove(p);
	}
	
	public List<Property<?>> getProperties(String key) {
		List<Property<?>> result = new ArrayList<Property<?>>();
		for (Property<?> property : this.properties) {
			if (property.getKey().equals(key))
				result.add(property);
		}
		
		return result;
	}
	
	public Property<?> getProperty(String key) {
		for (Property<?> property : this.properties) {
			if (property.getKey().equals(key))
				return property;
		}
		
		return null;
	}
	
}
