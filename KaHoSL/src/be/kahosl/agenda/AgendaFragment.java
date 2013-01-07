package be.kahosl.agenda;


import java.io.Serializable;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AgendaFragment extends Fragment implements TabFragment, Serializable, Parcelable {
	
	private static final long serialVersionUID = 16659811758078154L;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		List<AgendaEvent> agendaEvents = Agenda.readEvents(this.getActivity());
		
		// set the menu
		setHasOptionsMenu(true);
		
		return inflater.inflate(R.layout.agenda_view, container, false);
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
		}
		
		return true;
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
}
