package be.kahosl.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "Instellingen");
		
		return inflater.inflate(R.layout.settings_view, container, false);
	}
	
	public int getIcon(){
		return android.R.drawable.ic_menu_agenda;
	}
}
