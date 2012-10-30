package be.kahosl.app;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

public class KahoslActivity extends Activity implements TabListener {
	
	private RelativeLayout r;
	private WhatsRecentFragment wr;
	private AddressBookFragment addressbook;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kahosl);
        
		try {
			r = (RelativeLayout) findViewById(R.id.mainLayout);
			fragMentTra = getFragmentManager().beginTransaction();
			ActionBar bar = getActionBar();
			
		
			bar.addTab(bar.newTab().setText("WR").setTabListener(this));
			bar.addTab(bar.newTab().setText("AG").setTabListener(this));
			bar.addTab(bar.newTab().setText("AD").setTabListener(this));
			bar.addTab(bar.newTab().setText("KS").setTabListener(this));

			bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			bar.setDisplayShowHomeEnabled(false);
			bar.setDisplayShowTitleEnabled(false);
			bar.show();

		} catch (Exception e) {
			e.getMessage();
		}
		/**
		 * Hiding Action Bar
		 */
	}


	FragmentTransaction fragMentTra = null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_kahosl, menu);
		return true;
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals("WR")) {
			try {
				r.removeAllViews();
			} catch (Exception e) {
			}
			wr = new WhatsRecentFragment();
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(r.getId(), wr);
			fragMentTra.commit();
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
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(r.getId(), addressbook);
			fragMentTra.commit();
		}

	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
