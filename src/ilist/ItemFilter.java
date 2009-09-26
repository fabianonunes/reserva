package ilist;

import ilist.item.BasicItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemFilter implements Cloneable {

	private List<String> keys;
	private Date after, before;
	private Integer ratingMin, ratingMax;
	private String description;
	private String name;
	private List<String> tags;

	public ItemFilter() {
		this.reset();
	}

	public ItemFilter clone() {
		ItemFilter clone = new ItemFilter();

		if (after != null)
			clone.setAfter((Date) after.clone());
		if (before != null)
			clone.setBefore((Date) before.clone());
		clone.setRatingMin(ratingMin);
		clone.setRatingMax(ratingMax);
		clone.setDescription(description);
		clone.setName(name);
		clone.setTags(new ArrayList<String>(tags));
		clone.setKeys(new ArrayList<String>(keys));

		return clone;
	}

	public ItemFilter add(ItemFilter filter) {
		for (String tag : filter.getTags()) {
			if (!tags.contains(tag))
				addTag(tag);
		}

		if (keys == null || keys.isEmpty())
			keys = filter.getKeys();
		if (after == null)
			after = filter.getAfter();
		if (before == null)
			before = filter.getBefore();
		if (ratingMin == null)
			ratingMin = filter.getRatingMin();
		if (ratingMax == null)
			ratingMax = filter.getRatingMax();
		if (description == null)
			description = filter.getDescription();
		if (name == null)
			name = filter.getName();

		return this;
	}

	public boolean match(BasicItem item) {

		if (after != null)
			if (item.getDate() != null && item.getDate().before(after))
				return false;
			else if (item.getDate() == null)
				return false;

		if (before != null)
			if (item.getDate() != null && item.getDate().after(before))
				return false;

		if (name != null)
			if (item.getName() != null
					&& !item.getName().toLowerCase().contains(
							name.toLowerCase()))
				return false;
			else if (item.getName() == null)
				return false;

		if (description != null)
			if (item.getDescription() != null
					&& !item.getDescription().toLowerCase().contains(
							description.toLowerCase()))
				return false;
			else if (item.getDescription() == null)
				return false;

		return true;
	}

	public void reset() {
		name = null;
		description = null;
		after = null;
		before = null;
		ratingMax = null;
		ratingMin = null;
		keys = new ArrayList<String>();
		tags = new ArrayList<String>();
	}

	public Date getAfter() {
		return after;
	}

	public void setAfter(Date after) {
		this.after = after;
	}

	public Date getBefore() {
		return before;
	}

	public void setBefore(Date before) {
		this.before = before;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public boolean addKey(String key) {
		return this.keys.add(key);
	}

	public boolean removeKey(String key) {
		return this.keys.remove(key);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRatingMax() {
		return ratingMax;
	}

	public void setRatingMax(Integer ratingMax) {
		this.ratingMax = ratingMax;
	}

	public Integer getRatingMin() {
		return ratingMin;
	}

	public void setRatingMin(Integer ratingMin) {
		this.ratingMin = ratingMin;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean addTag(String tag) {
		return this.tags.add(tag);
	}

	public boolean removeTag(String tag) {
		return this.tags.remove(tag);
	}

}
