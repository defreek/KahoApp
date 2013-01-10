package be.kahosl.agenda;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AgendaFragment extends Fragment implements TabFragment, Serializable, Parcelable {
	private List<AgendaEvent> agendaEvents;
	private ImageView calendarToJournalButton;
	private Button selectedDayMonthYearButton;
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	
	private static final String dateTemplate = "MMMM yyyy";
	private static final String tag = "AgendaFragment";
	private static final long serialVersionUID = 16659811758078154L;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		agendaEvents = Agenda.readEvents(this.getActivity());
		
		// set the menu
		setHasOptionsMenu(true);
		
		// the view
        View abView = inflater.inflate(R.layout.simple_calendar_view, container, false);
		
		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);
		
		selectedDayMonthYearButton = (Button) abView.findViewById(R.id.selectedDayMonthYear);
		selectedDayMonthYearButton.setText("");

		prevMonth = (ImageView) abView.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (month <= 1) {
					month = 12;
					year--;
				} else {
					month--;
				}
				
				//Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
				setGridCellAdapterToDate(month, year);
			}
		});
		
		currentMonth = (Button) abView.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

		nextMonth = (ImageView) abView.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (month > 11) {
					month = 1;
					year++;
				} else {
					month++;
				}
				
				//Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
				setGridCellAdapterToDate(month, year);
			}
		});
		
		calendarView = (GridView) abView.findViewById(R.id.calendar);
		

		// Initialised
		adapter = new GridCellAdapter((this.getActivity()).getApplicationContext(), R.id.calendar_day_gridcell, month, year, agendaEvents);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
		
		return abView;
	}
	
	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter((this.getActivity()).getApplicationContext(), R.id.calendar_day_gridcell, month, year, agendaEvents);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.agenda_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.insertevent_menu_item:
				Agenda.insertEvent(getActivity());
				break;
			case R.id.viewevent_menu_item:
				if (adapter.getClickedEventID() == -1) {
					Toast.makeText(getActivity().getApplicationContext(), "Er zijn geen events op de geselecteerde dag.", Toast.LENGTH_SHORT).show();
				} else {
					Agenda.viewEvent(getActivity(), adapter.getClickedEventID());
				}
				break;
		}
		
		return true;
	}
	
	@Override
	public void onDestroy() {
		Log.d(tag, "Destroying View ...");
		super.onDestroy();
	}
	
	public int getIcon(){
		return R.drawable.ic_menu_agenda;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// Nothing
	}
	
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		
		private final Context _context;
		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		private final int month, year;
		private int daysInMonth, prevMonthDays;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private List<AgendaEvent> agendaEvents;
		private AgendaEvent eventToShow;
		private Button gridcell;
		private TextView num_events_per_day;
	
		private final HashMap eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		
		public int getClickedEventID() {
			if (eventToShow != null) {
				return eventToShow.getId();
			}
			
			return -1;
		}
		
		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId, int month, int year, List<AgendaEvent> allEvents) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			this.month = month;
			this.year = year;
			this.agendaEvents = allEvents;
			
			//Log.d(tag, "==> Passed in Date FOR Month: " + month + " " + "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			//Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			//Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			//Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}
		
		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			//Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			// The number of days to leave blank at
			// the start of this month.
			int trailingSpaces = 0;
			//int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			//String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			//Log.d(tag, "Current Month: " + " " + currentMonthName + " having " + daysInMonth + " days.");

			// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			//Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				//Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				//Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				//Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
			}

			// Compute how much to leave before before the first day of the
			// month.
			// getDay() returns 0 for Sunday.
			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			//Log.d(tag, "Week Day:" + currentWeekDay + " is " + getWeekDayAsString(currentWeekDay));
			//Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			//Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				//Log.d(tag, "PREV MONTH:= " + prevMonth + " => " + getMonthAsString(prevMonth) + " " + String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
				list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				//Log.d(currentMonthName, String.valueOf(i) + " " + getMonthAsString(currentMonth) + " " + yy);
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				//Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		/**
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap findNumberOfEventsPerMonth(int year, int month) {
			HashMap map = new HashMap<String, Integer>();
	
			for (AgendaEvent event : agendaEvents) {
				if (event.getMonth() == (month-1) && event.getYear() == year) {
					String day = DateFormat.format("dd", event.getDate()).toString();
					
					if (map.containsKey(day)) {
						Integer val = (Integer) map.get(day) + 1;
						map.put(day, val);
					} else {
						map.put(day, 1);
					}
				}
			}

			return map;
		}

		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING
			SimpleDateFormat format = new SimpleDateFormat("MMMM");
			
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];
			boolean isEvD = false;
			
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
				Date convertedDate = new Date();
				
			    try {
					convertedDate = dateFormat.parse(theday+"-"+themonth+"-"+theyear);
				} catch (ParseException e1) {
					e1.printStackTrace();
				} 
			    
				
				if (eventsPerMonthMap.containsKey(theday)) {
					// if OS is not new enough use the deprecated function
					if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
						gridcell.setBackground(getResources().getDrawable(R.drawable.calendar_tile_small_event));
					} else {
						gridcell.setBackgroundDrawable(getResources().getDrawable(R.drawable.calendar_tile_small_event));
					}
					
					isEvD = true;
					
					//gridcell.setBackground((Drawable)R.drawable.calendar_tile_small_event);
					/*num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					num_events_per_day.setText(numEvents.toString());*/
				}
			}

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			//Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-" + theyear);

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(Color.LTGRAY);
			}
			
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(Color.BLACK);
			}
			
			if (isEvD && !(format.format(Calendar.getInstance().getTime()).equals(theday))) {
				gridcell.setTextColor(android.graphics.Color.WHITE);
			}
			
			if (day_color[1].equals("BLUE") && format.format(Calendar.getInstance().getTime()).equals(themonth)) {
				gridcell.setTextColor(getResources().getColor(R.color.static_text_color));
			}
			
			return row;
		}
		
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();
			String toShow = "empty";
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
			Date convertedDate = new Date();
			
			
		    try {
				convertedDate = dateFormat.parse(date_month_year);
			} catch (ParseException e1) {
				e1.printStackTrace();
			} 
		    
		    for (AgendaEvent event : agendaEvents) {
		    	// if same day show in the events
		    	if (event.sameDay(convertedDate)) {
		    		if (! toShow.equals("empty")) {
		    			toShow = toShow + ", " + event.getTitle();
		    		} else {
		    			toShow = event.getTitle();
		    		}
		    		
		    		eventToShow = event;
		    		System.out.println(event.getId());
		    	} else {
		    		eventToShow = null;
		    	}
		    }
		    
		    if (toShow.equals("empty")) {
		    	toShow = "geen events op " + date_month_year;
		    }
		    
		    // if landscape show the event in a toast
		    if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_PORTRAIT) {
		    	selectedDayMonthYearButton.setText(toShow);
		    } else {
				Toast.makeText(getActivity().getApplicationContext(), toShow, Toast.LENGTH_SHORT).show();
		    }
		    
			try {
				Date parsedDate = dateFormatter.parse(date_month_year);
				Log.d(tag, "Parsed Date: " + parsedDate.toString());

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}
		
		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}
		
		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}
}
