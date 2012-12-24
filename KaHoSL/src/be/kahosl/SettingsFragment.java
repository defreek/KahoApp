package be.kahosl;

import java.io.Serializable;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements TabFragment, Serializable, Parcelable {

	private static final long serialVersionUID = 3666477138589152241L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.prefs);
	}	
	
	public int getIcon(){
		return R.drawable.ic_menu_settings;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// Nothing
	}
}
