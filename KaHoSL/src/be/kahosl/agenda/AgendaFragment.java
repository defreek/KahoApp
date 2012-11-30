package be.kahosl.agenda;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.CalendarView;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AgendaFragment extends TabFragment {
	private CalendarView cv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "Agenda");
		
		cv = (CalendarView)container.findViewById(R.id.calCalendar);
		
		
		
		return inflater.inflate(R.layout.agenda_view, container, false);
	}
	
	@Override
	public int getIcon(){
		return R.drawable.ic_menu_agenda;
	}
	
	
}
