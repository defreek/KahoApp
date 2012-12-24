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
import be.kahosl.addressbook.AddressBookFragment;
import be.kahosl.agenda.AgendaFragment;
import be.kahosl.kdisk.KDiskFragment;
import be.kahosl.whatsrecent.WhatsRecentListFragment;

public class KahoslActivity extends Activity implements TabListener {

	private LinkedHashMap<String, TabFragment> fragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kahosl);
        
        if(savedInstanceState != null) {
        	// Modules ophalen
        	fragments = (LinkedHashMap<String, TabFragment>) savedInstanceState.getSerializable("fragments");
        
        } else {
            // Modules aanmaken
            fragments = new LinkedHashMap<String, TabFragment>(5);
            
            fragments.put("What's Recent?", new WhatsRecentListFragment());
            fragments.put("Agenda", new AgendaFragment());
            fragments.put("Adresboek", new AddressBookFragment());
            fragments.put("K-schijf", new KDiskFragment());
            fragments.put("Instellingen", new SettingsFragment());
        }

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
		
        if(savedInstanceState != null)
        	aBar.setSelectedNavigationItem(savedInstanceState.getInt("selectedTab"));
	}
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("fragments", fragments);
		outState.putInt("selectedTab", getActionBar().getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		// Activity niet afsluiten
		moveTaskToBack(true);
	}


	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
		fTransaction.replace(R.id.fragment_container, (Fragment) fragments.get(tab.getTag()));
		fTransaction.commit();
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// Not implemented
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// Not implemented
	}
}