package ilist.item;

import ilist.DataCenter;
import ilist.property.DateProperty;
import ilist.property.DescriptionProperty;
import ilist.property.IconProperty;
import ilist.property.NameProperty;

import java.awt.image.BufferedImage;
import java.util.Date;


public class BasicItem extends Item {	
	
	private String metakey;
	private NameProperty metaName;
	private IconProperty metaIcon;
	
	protected NameProperty name;
	protected DescriptionProperty description;
	protected IconProperty icon;
	protected DateProperty date;
	
	
	public BasicItem(String key) {
		super();
		
		this.metakey = key;
		
		this.name = new NameProperty(null);
		this.description = new DescriptionProperty(null);
		this.icon = new IconProperty(null);
		this.date = new DateProperty(null);
		
		this.addProperty(this.name);
		this.addProperty(this.description);
		this.addProperty(this.icon);
		this.addProperty(this.date);
	}


	public Date getDate() {
		return date.getValue();
	}

	public void setDate(Date date) {
		this.date.setValue(date);
	}

	public BufferedImage getIcon() {
		return icon.getValue();
	}

	public void setIcon(BufferedImage icon) {
		this.icon.setValue(icon);
	}

	public String getName() {
		return name.getValue();
	}

	public void setName(String name) {
		this.name.setValue(name);
	}
	
	public String getDescription() {
		return description.getValue();
	}

	public void setDescription(String desc) {
		this.description.setValue(desc);
	}

	public String getMetaKey() {
		return metakey;
	}

	public BufferedImage getMetaIcon() {
		if (metaIcon == null)
			metaIcon = (IconProperty) DataCenter.getInstance().getMetaProperty(metakey, "icon");
		
		return metaIcon.getValue();
	}

	public String getMetaName() {
		if (metaName == null)
			metaName = (NameProperty) DataCenter.getInstance().getMetaProperty(metakey, "name");
		
		return metaName.getValue();
	}
		
}
