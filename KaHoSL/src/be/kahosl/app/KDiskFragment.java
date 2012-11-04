package be.kahosl.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class KDiskFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "K-Schijf");
		
		return inflater.inflate(R.layout.kdisk_view, container, false);
	}
	
	public int getIcon(){
		return android.R.drawable.ic_menu_agenda;
	}
}
