package be.kahosl;

import be.kahosl.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends TabFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "Instellingen");
		
		return inflater.inflate(R.layout.settings_view, container, false);
	}
	
	@Override
	public int getIcon(){
		return R.drawable.ic_menu_settings;
	}
}
