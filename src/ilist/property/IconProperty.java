package ilist.property;

import java.awt.image.BufferedImage;

public class IconProperty extends Property<BufferedImage> {

	public static final String KEY = "icon";
	
	
	public IconProperty(BufferedImage icon) {
		this.setValue(icon);
	}
	
	@Override
	public String getKey() {
		return KEY;
	}

}
