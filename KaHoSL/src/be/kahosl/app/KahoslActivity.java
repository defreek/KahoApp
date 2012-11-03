package be.kahosl.app;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.RelativeLayout;

public class KahoslActivity extends Activity implements TabListener {
	
	private RelativeLayout r;
	private FragmentTransaction fTransaction = null;
	
	// Modules
	private WhatsRecentFragment wr;
	private AgendaFragment agenda;
	private AddressBookFragment addressbook;
	private KDiskFragment kdisk;
	private SettingsFragment settings;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kahosl);
        
        wr = new WhatsRecentFragment();
        
		try {
			r = (RelativeLayout) findViewById(R.id.mainLayout);
			fTransaction = getFragmentManager().beginTransaction();
			ActionBar aBar = getActionBar();
		
			aBar.addTab(aBar.newTab().setText("WR").setTabListener(this));
			aBar.addTab(aBar.newTab().setText("AG").setTabListener(this));
			aBar.addTab(aBar.newTab().setText("AD").setTabListener(this));
			aBar.addTab(aBar.newTab().setText("KS").setTabListener(this));
			aBar.addTab(aBar.newTab().setText("ST").setTabListener(this));
			
			aBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
			aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			aBar.setDisplayShowHomeEnabled(false);
			aBar.setDisplayShowTitleEnabled(false);
			aBar.show();

		} catch (Exception e) {
			Log.wtf("Exc: onCreate KahoslAct", e.getMessage(), e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_kahosl, menu);
		return true;
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals("WR")) {
			if(wr == null)
				wr = new WhatsRecentFragment();
			
			displayFragment(wr);

		} else if (tab.getText().equals("AG")) {
			try {
				r.removeAllViews();
			} catch (Exception e) {
			}
			// TODO : implement agenda
			/* fram2 = new FragMent2();
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram2);
			fragMentTra.commit(); */
		} else if (tab.getText().equals("AD")) {
			try {
				r.removeAllViews();
			} catch (Exception e) {
			}
			addressbook = new AddressBookFragment();
			fTransaction.addToBackStack(null);
			fTransaction = getFragmentManager().beginTransaction();
			fTransaction.add(r.getId(), addressbook);
			fTransaction.commit();
		}
	}
	
	private void displayFragment(Fragment f) {
		try {
			r.removeAllViews();
		} catch (Exception e) {
			Log.wtf("Exc: displayFragment KahoslAct", e.getMessage(), e);
		}

		fTransaction.addToBackStack(null);
		fTransaction = getFragmentManager().beginTransaction();
		fTransaction.add(r.getId(), f);
		fTransaction.commit();
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
