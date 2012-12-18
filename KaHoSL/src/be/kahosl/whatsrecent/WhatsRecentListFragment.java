package be.kahosl.whatsrecent;

import be.kahosl.R;
import be.kahosl.TabFragment;
import be.kahosl.whatsrecent.data.WhatsRecentDatabase;
import be.kahosl.whatsrecent.data.WhatsRecentProvider;
import be.kahosl.whatsrecent.service.WhatsRecentDownloaderService;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class WhatsRecentListFragment extends ListFragment implements
		TabFragment, LoaderManager.LoaderCallbacks<Cursor> {
	private static final int WHATSRECENT_LIST_LOADER = 0x01;

	private WhatsRecentCursorAdapter adapter;
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String projection[] = { WhatsRecentDatabase.COL_URL };
		Cursor cursor = getActivity().getContentResolver().query(
				Uri.withAppendedPath(WhatsRecentProvider.CONTENT_URI,
						String.valueOf(id)), projection, null, null, null);
		if (cursor.moveToFirst()) {
			String announcementUrl = cursor.getString(cursor.getColumnIndex(WhatsRecentDatabase.COL_URL));
		
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(announcementUrl));
			startActivity(browserIntent);
		}
		cursor.close();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getLoaderManager().initLoader(WHATSRECENT_LIST_LOADER, null, this);

		adapter = new WhatsRecentCursorAdapter(getActivity().getApplicationContext(), null);

		setListAdapter(adapter);
		setHasOptionsMenu(true);
	}
	
	// options menu

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.whats_recent_menu, menu);

		// refresh menu item
		Intent refreshIntent = new Intent(
				getActivity().getApplicationContext(),
				WhatsRecentDownloaderService.class);
		refreshIntent
				.setData(Uri
						.parse("https://cygnus.cc.kuleuven.be/webapps/tol-data-rs-events-bb_bb60/rs/s/users/e-q0422864/events/?signature=1cB0nxiYffAFaC17CkD4m9esHX4%3D&view=atom"));
		MenuItem refresh = menu.findItem(R.id.refresh_menu_item);
		refresh.setIntent(refreshIntent);

//		// pref menu item
//		Intent prefsIntent = new Intent(getActivity().getApplicationContext(),
//				WhatsRecentPreferencesActivity.class);
//
//		MenuItem preferences = menu.findItem(R.id.settings_menu_item);
//		preferences.setIntent(prefsIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh_menu_item:
			getActivity().startService(item.getIntent());
			break;
		case R.id.delete_menu_item:
			adapter.hideCheckedItems();
			break;
		case R.id.showall_menu_item:
			adapter.showAllItems();
			break;
		case R.id.insertevent_menu_item:
			
			break;
		}
		return true;
	}

	// LoaderManager.LoaderCallbacks<Cursor> methods

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { WhatsRecentDatabase.ID, WhatsRecentDatabase.COL_TITLE,
				WhatsRecentDatabase.COL_URL, WhatsRecentDatabase.COL_COURSE,
				WhatsRecentDatabase.COL_AUTHOR, WhatsRecentDatabase.COL_VISIBLE,
				WhatsRecentDatabase.COL_DATE, WhatsRecentDatabase.COL_TYPE};

		String selection = WhatsRecentDatabase.COL_VISIBLE + "=?";
	    String[] selectionArgs = { "1" };
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				WhatsRecentProvider.CONTENT_URI, projection, selection, selectionArgs, null);
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	public int getIcon() {
		return R.drawable.ic_menu_whats_recent;
	}
}
