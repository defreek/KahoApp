package be.kahosl;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;
import be.kahosl.addressbook.AddressBookFragment;
import be.kahosl.agenda.AgendaFragment;
import be.kahosl.kdisk.KDiskFragment;
import be.kahosl.whatsrecent.WhatsRecentFragment;

public class KahoslActivity extends Activity implements TabListener {
	
	private RelativeLayout r;
	private FragmentTransaction fTransaction;
	private LinkedHashMap<String, TabFragment> fragments;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kahosl);
        
        // Modules aanmaken
        fragments = new LinkedHashMap<String, TabFragment>(5);
        
        fragments.put("What's Recent?", new WhatsRecentFragment());
        fragments.put("Agenda", new AgendaFragment());
        fragments.put("Adresboek", new AddressBookFragment());
        fragments.put("K-schijf", new KDiskFragment());
        fragments.put("Instellingen", new SettingsFragment());
        
		r = (RelativeLayout) findViewById(R.id.mainLayout);
		fTransaction = getFragmentManager().beginTransaction();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_kahosl, menu);
		return true;
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		fTransaction = getFragmentManager().beginTransaction();
		fTransaction.replace(r.getId(), fragments.get(tab.getTag()));
		fTransaction.commit();
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
