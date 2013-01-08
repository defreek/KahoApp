package be.kahosl.agenda;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

public class Agenda {
	private static final int PROJECTION_EVENT_TITLE_INDEX 			= 0;
	private static final int PROJECTION_EVENT_DESCRIPTION_INDEX 	= 1;
	private static final int PROJECTION_EVENT_DSTART_INDEX 			= 2;
	private static final int PROJECTION_EVENT_EVENT_LOCATION_INDEX 	= 3;
	private static final int PROJECTION_EVENT_ID_INDEX 				= 4;
	
	/* insert empty event */
	public static void insertEvent(Activity ac) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		
		intent.setType("vnd.android.cursor.item/event");

		ac.startActivity(intent);
	}
	
	/* insert event with stuff already filled */
	public static void insertEvent(Activity ac, String title, String location, String description, Calendar startDate, Calendar endDate, boolean fullDay) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, title);
		intent.putExtra(Events.EVENT_LOCATION, location);
		intent.putExtra(Events.DESCRIPTION, description);
		
		// Setting dates
		if (startDate != null) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
						startDate.getTimeInMillis());
		}
		
		if (endDate != null) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
						endDate.getTimeInMillis());
		}

		// Make it a full day event
		intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, fullDay);
		ac.startActivity(intent);
	}
	
	public static List<AgendaEvent> readEvents(Activity ac) {
		Context c = ac.getApplicationContext();
		Uri uri = CalendarContract.Events.CONTENT_URI;
		
		String[] eventProjection = new String[] {
				CalendarContract.Events.TITLE,
				CalendarContract.Events.DESCRIPTION,
				CalendarContract.Events.DTSTART,
				CalendarContract.Events.EVENT_LOCATION,
				CalendarContract.Events._ID
		};
      
		Cursor calendarCursor = c.getContentResolver().query(uri, eventProjection, null, null, null);
		List<AgendaEvent> eventList = new ArrayList<AgendaEvent>();
		
	    while (calendarCursor.moveToNext()) {
	        String	eventTitle = null;
	        String 	eventDescription = null;
	        String 	eventLocation = null;
	        long 	eventDate = 0;
	        int 	eventId = 0;
	        
	        // Get the field values
	        eventDescription = 	calendarCursor.getString(PROJECTION_EVENT_DESCRIPTION_INDEX);
	        eventDate = 		Long.parseLong(calendarCursor.getString(PROJECTION_EVENT_DSTART_INDEX));
	        eventTitle = 		calendarCursor.getString(PROJECTION_EVENT_TITLE_INDEX);
	        eventLocation = 	calendarCursor.getString(PROJECTION_EVENT_EVENT_LOCATION_INDEX);
	        eventId = 			Integer.parseInt(calendarCursor.getString(PROJECTION_EVENT_ID_INDEX));
	        
	        if (isSameMonth(eventDate)) {
	        	eventList.add(new AgendaEvent(eventTitle, eventDescription, eventLocation, eventDate, eventId));
	        	//System.out.println(eventTitle + ": " + eventDescription + " ON " + eventDate + " IN " + eventLocation);
	        }
	    }
	    
	    calendarCursor.close();
	    return eventList;
	}
	
	public static void viewEvent(Activity ac, int id) {
		final Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
		final Intent intent = new Intent(Intent.ACTION_VIEW)
		   .setData(uri);
		
		ac.startActivity(intent);
	}
	
	private static boolean isSameMonth(long dateCompare) {
		Calendar calNow = Calendar.getInstance();
		Calendar calCmp = Calendar.getInstance();
		calCmp.setTimeInMillis(dateCompare);

		if (calCmp.get(Calendar.MONTH) < calNow.get(Calendar.MONTH)) {
			return false;
		}
		
		return true;
	}
}
