package be.kahosl.agenda;

import java.util.Calendar;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AgendaFragment extends Fragment implements TabFragment {
	private CalendarView cv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "Agenda");
		
		cv = (CalendarView)container.findViewById(R.id.calCalendar);
		
		return inflater.inflate(R.layout.agenda_view, container, false);
	
		
	}
	
	public int getIcon(){
		return R.drawable.ic_menu_agenda;
	}
	
	public void insertEvent(String title, String location, String description, Calendar startDate, Calendar endDate, boolean fullDay) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, title);
		intent.putExtra(Events.EVENT_LOCATION, location);
		intent.putExtra(Events.DESCRIPTION, description);
		
		// Setting dates
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
						startDate.getTimeInMillis());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
						endDate.getTimeInMillis());

		// Make it a full day event
		intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, fullDay);
		
		startActivity(intent);
	}
}
