package be.kahosl;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import be.kahosl.addressbook.AddressBookFragment;
import be.kahosl.agenda.AgendaFragment;
import be.kahosl.kdisk.KDiskFragment;
import be.kahosl.whatsrecent.WhatsRecentListFragment;

public class KahoslActivity extends Activity implements TabListener {

	private RelativeLayout r;
	private LinkedHashMap<String, TabFragment> fragments;
	private Fragment active;

	// TODO: first run -> login credentials

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kahosl);
        
        // Modules aanmaken
        fragments = new LinkedHashMap<String, TabFragment>(5);
        
        fragments.put("What's Recent?", new WhatsRecentListFragment());
        fragments.put("Agenda", new AgendaFragment());
        fragments.put("Adresboek", new AddressBookFragment());
        fragments.put("K-schijf", new KDiskFragment());
        fragments.put("Instellingen", new SettingsFragment());
        
		r = (RelativeLayout) findViewById(R.id.mainLayout);
		ActionBar aBar = getActionBar();

		// Tabs aanmaken
		for(Map.Entry<String, TabFragment> e : fragments.entrySet()){
			Tab t = aBar.newTab();

			t.setTag(e.getKey());
			t.setIcon(e.getValue().getIcon());
			t.setTabListener(this);

			aBar.addTab(t);
		}

		// Actionbar instellen
		aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		aBar.setDisplayShowHomeEnabled(false);
		aBar.setDisplayShowTitleEnabled(false);
		aBar.show();
	}
    
	@Override
	public void onBackPressed() {
		// Activity niet afsluiten
		moveTaskToBack(true);
	}


	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		FragmentTransaction fTransaction = getFragmentManager().beginTransaction();

		// Huidig fragment verbergen
		if(active != null)
			fTransaction.hide(active);

		// Nieuw fragment tonen
		active = (Fragment) fragments.get(tab.getTag());
		if(active.isHidden())
			fTransaction.show(active);
		else
			fTransaction.add(r.getId(), active);

		fTransaction.commit();
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// Not implemented
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// Not implemented
	}
}