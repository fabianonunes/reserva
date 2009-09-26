package ilist;

import ilist.item.BasicItem;
import ilist.property.IconProperty;
import ilist.property.KeyProperty;
import ilist.property.NameProperty;
import ilist.property.Property;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DataCenter {

	private static DataCenter instance = new DataCenter();

	public static DataCenter getInstance() {
		return instance;
	}

	public static void setInstance(DataCenter dataCenter) {
		instance = dataCenter;
	}

	private Map<String, List<BasicItem>> items = new HashMap<String, List<BasicItem>>();
	private Map<String, List<Property<?>>> metaProperties = new HashMap<String, List<Property<?>>>();
	private List<ItemFilter> navigationHistory = new ArrayList<ItemFilter>();
	private int indexInNavigationHistory = 0;

	public DataCenter() {
		navigationHistory.add(new ItemFilter());
	}

	public List<String> getPropertiesKeys() {// TODO
		return null;
	}

	public List<BasicItem> getItems() {
		List<BasicItem> result = new ArrayList<BasicItem>();
		for (Collection<BasicItem> list : items.values()) {
			result.addAll(list);
		}

		return result;
	}

	public List<BasicItem> getItems(String key) {
		return items.get(key);
	}

	public boolean addItem(BasicItem item) {
		if (items.containsKey(item.getMetaKey()))
			return items.get(item.getMetaKey()).add(item);

		else {
			List<BasicItem> list = new ArrayList<BasicItem>();
			items.put(item.getMetaKey(), list);
			return list.add(item);
		}
	}

	public boolean removeItem(BasicItem item) {
		if (items.containsKey(item.getMetaKey()))
			return items.get(item.getMetaKey()).remove(item);
		else
			return false;
	}

	public int getNbItems() {
		int result = 0;
		for (Collection<BasicItem> list : items.values()) {
			result = result + list.size();
		}

		return result;
	}

	public int getNbItems(String key) {
		return items.get(key).size();
	}

	public Set<String> getKeys() {
		return metaProperties.keySet();
	}

	public List<Property<?>> getMetaProperties(String key) {
		return new ArrayList<Property<?>>(metaProperties.get(key));
	}

	public boolean addMetaProperty(String key, Property<?> p) {
		if (metaProperties.containsKey(key))
			return metaProperties.get(key).add(p);

		else {
			List<Property<?>> list = new ArrayList<Property<?>>();
			metaProperties.put(key, list);
			return list.add(p);
		}
	}

	public boolean removeMetaProperty(String key, Property<?> p) {
		if (metaProperties.containsKey(key))
			return metaProperties.get(key).remove(p);
		else
			return false;
	}

	public List<Property<?>> getMetaProperties(String key, String propertyKey) {
		List<Property<?>> result = new ArrayList<Property<?>>();

		if (metaProperties.containsKey(key))
			for (Property<?> property : metaProperties.get(key)) {
				if (property.getKey().equals(propertyKey))
					result.add(property);
			}

		return result;
	}

	public Property<?> getMetaProperty(String key, String propertyKey) {
		if (metaProperties.containsKey(key))
			for (Property<?> property : metaProperties.get(key)) {
				if (property.getKey().equals(propertyKey))
					return property;
			}

		return null;
	}

	public void createMetaProperties(String key, String name, BufferedImage icon) {
		if (key == null || key.equals(""))
			throw new IllegalArgumentException("key cannot be null or empty");
		if (metaProperties.keySet().contains(key))
			throw new IllegalArgumentException("key already exist");

		this.addMetaProperty(key, new KeyProperty(key));
		this.addMetaProperty(key, new NameProperty(name));
		this.addMetaProperty(key, new IconProperty(icon));
	}

	public List<String> getTags() {
		List<String> result = new ArrayList<String>();

		return result;
	}

	public List<BasicItem> getItems(ItemFilter filter) {
		List<BasicItem> result = new ArrayList<BasicItem>();

		for (Entry<String, List<BasicItem>> entry : items.entrySet()) {
			if (filter.getKeys().isEmpty()
					|| filter.getKeys().contains(entry.getKey()))
				for (BasicItem item : entry.getValue()) {
					if (filter.match(item))
						result.add(item);
				}
		}

		return result;
	}

	public List<ItemFilter> getNavigationHistory() {
		return new ArrayList<ItemFilter>(navigationHistory);
	}

	public ItemFilter getCurrentItemFilter() {
		return navigationHistory.get(indexInNavigationHistory);
	}

	public ItemFilter navigationAdd(ItemFilter filter) {
		for (int i = indexInNavigationHistory + 1; i < navigationHistory.size(); i++) {
			navigationHistory.remove(i);
		}

		indexInNavigationHistory++;
		navigationHistory.add(filter);

		return filter;
	}

	public ItemFilter navigationNext() {
		if (indexInNavigationHistory < navigationHistory.size() - 1)
			indexInNavigationHistory++;

		return getCurrentItemFilter();
	}

	public ItemFilter navigationPrevious() {
		if (indexInNavigationHistory > 0)
			indexInNavigationHistory--;

		return getCurrentItemFilter();
	}

}
