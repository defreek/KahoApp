package be.kahosl.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WhatsRecentFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "What's Recent");
		
		return inflater.inflate(R.layout.whats_recent_view, container, false);
	}
	
	// TODO : on pause, on create ....
}
