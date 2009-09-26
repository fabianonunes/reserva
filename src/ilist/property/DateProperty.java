package ilist.property;

import java.util.Date;

public class DateProperty extends Property<Date> {

	public static final String KEY = "date";
	
	
	public DateProperty(Date date) {
		this.setValue(date);
	}
	
	@Override
	public String getKey() {
		return KEY;
	}

}
