package be.kahosl.whatsrecent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class WhatsRecentFragment extends TabFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "What's Recent");
		return inflater.inflate(R.layout.whats_recent_view, container, false);
	}

	@Override
	public int getIcon() {
		return R.drawable.ic_menu_whats_recent;
	}
}
