package be.kahosl.agenda;


import java.io.Serializable;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AgendaFragment extends Fragment implements TabFragment, Serializable, Parcelable {
	
	private static final long serialVersionUID = 16659811758078154L;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Agenda.readEvents(this.getActivity());
		return inflater.inflate(R.layout.agenda_view, container, false);
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
