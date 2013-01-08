package be.kahosl.agenda;


import java.util.Calendar;
import java.util.Date;

public class AgendaEvent {
	private String title;
	private String description;
	private String location;
	private long dtstart;
	
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	public String getLocation() {
		return location;
	}
	
	public int getDay() {
		Date d = new Date();
		d.setTime(dtstart);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		
		return cal.get(Calendar.DATE);
	}
	
	public int getMonth() {
		Date d = new Date();
		d.setTime(dtstart);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		
		return cal.get(Calendar.MONTH);
	}
	
	public int getYear() {
		Date d = new Date();
		d.setTime(dtstart);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		
		return cal.get(Calendar.YEAR);
	}
	
	public Date getDate() {
		Date d = new Date();
		d.setTime(dtstart);

		return d;
	}
	
	public boolean sameDay(Date date) {
		int dateM, dateY, dateD;
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		dateD = cal.get(Calendar.DATE);
		dateM = cal.get(Calendar.MONTH);
		dateY = cal.get(Calendar.YEAR);
		
		if (this.getDay() == dateD && this.getMonth() == dateM && this.getYear() == dateY) {
			return true;
		}
		
		return false;
	}
	
	public AgendaEvent(String title, String description, String location, long dtstart) {
		this.location = location;
		this.title = title;
		this.description = description;
		this.dtstart = dtstart;
	}
}
