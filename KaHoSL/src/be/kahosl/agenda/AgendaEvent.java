package be.kahosl.agenda;


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
	
	public Date getDate() {
		Date d = new Date();
		d.setTime(dtstart);

		return d;
	}
	
	public AgendaEvent(String title, String description, String location, long dtstart) {
		this.location = location;
		this.title = title;
		this.description = description;
		this.dtstart = dtstart;
	}
}
