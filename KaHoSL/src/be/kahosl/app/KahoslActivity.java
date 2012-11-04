package be.kahosl.app;

import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

public class KahoslActivity extends Activity implements TabListener {
	
	private RelativeLayout r;
	private FragmentTransaction fTransaction;
	private HashMap<String, Fragment> fragments;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kahosl);
        
        // TODO : volgorde elementen bepalen
        // TODO : Fragments interface met geticon
        
        // Modules aanmaken
        fragments = new HashMap<String, Fragment>(5);
        
        fragments.put("What's Recent?", new WhatsRecentFragment());
        fragments.put("Agenda", new AgendaFragment());
        fragments.put("Adresboek", new AddressBookFragment());
        fragments.put("K-schijf", new KDiskFragment());
        fragments.put("Instellingen", new SettingsFragment());
        
		r = (RelativeLayout) findViewById(R.id.mainLayout);
		fTransaction = getFragmentManager().beginTransaction();
		ActionBar aBar = getActionBar();
	
		// Menu aanmaken
		for(Map.Entry<String, Fragment> e : fragments.entrySet()){
			Tab t = aBar.newTab();
			t.setText(e.getKey());
			//t.setIcon(it.next().getValue().getIcon());
			t.setIcon(R.drawable.ic_menu_agenda);
			t.setTabListener(this);

			aBar.addTab(t);
		}
		
        // TODO : Icon alleen afbeelden
		//aBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
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
		fTransaction.replace(r.getId(), fragments.get(tab.getText()));
		fTransaction.commit();
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
