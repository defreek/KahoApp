package be.kahosl.agenda;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

public class Agenda {
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
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
	
	public static void readEvents(Activity ac) {
		Context c = ac.getApplicationContext();
		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		String[] projection = new String[] {
		       CalendarContract.Calendars._ID,
		       CalendarContract.Calendars.ACCOUNT_NAME,
		       CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
		       CalendarContract.Calendars.NAME,
		       CalendarContract.Calendars.CALENDAR_COLOR
		};
		
		String selection = "((" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
		String[] selectionArgs = new String[] {"com.google"}; 

		Cursor calendarCursor = c.getContentResolver().query(uri, projection, selection, selectionArgs, null);
		
	    if (calendarCursor.moveToFirst()) {
	    	long calID = 0;
	        String displayName = null;
	        String accountName = null;
	        String ownerName = null;
	          
	        // Get the field values
	        calID = calendarCursor.getLong(PROJECTION_ID_INDEX);
	        displayName = calendarCursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
	        accountName = calendarCursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
	        ownerName = calendarCursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
	    }
	    
	    calendarCursor.close();
	}
}
