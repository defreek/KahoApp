package be.kahosl.whatsrecent;

import be.kahosl.R;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class WhatsRecentListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private OnWhatsRecentSelectedListener whatsRecentSelectedListener;
	private static final int WHATSRECENT_LIST_LOADER = 0x01;

	private SimpleCursorAdapter adapter;

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String projection[] = { WhatsRecentDatabase.COL_URL };
		Cursor announcementCursor = getActivity().getContentResolver().query(
				Uri.withAppendedPath(WhatsRecentProvider.CONTENT_URI,
						String.valueOf(id)), projection, null, null, null);
		if (announcementCursor.moveToFirst()) {
			String announcementUrl = announcementCursor.getString(0);
			whatsRecentSelectedListener.onWhatsRecentSelected(announcementUrl);
		}
		announcementCursor.close();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] uiBindFrom = { WhatsRecentDatabase.COL_TITLE };
		int[] uiBindTo = { R.id.title };

		getLoaderManager().initLoader(WHATSRECENT_LIST_LOADER, null, this);

		adapter = new SimpleCursorAdapter(
				getActivity().getApplicationContext(), R.layout.list_item,
				null, uiBindFrom, uiBindTo,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(adapter);
		setHasOptionsMenu(true);
	}

	public interface OnWhatsRecentSelectedListener {
		public void onWhatsRecentSelected(String tutUrl);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			whatsRecentSelectedListener = (OnWhatsRecentSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnWhatsRecentSelectedListener");
		}
	}

	// options menu

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.options_menu, menu);

		// refresh menu item
		Intent refreshIntent = new Intent(
				getActivity().getApplicationContext(),
				WhatsRecentDownloaderService.class);
		refreshIntent
				.setData(Uri
						.parse("https://cygnus.cc.kuleuven.be/webapps/tol-data-rs-events-bb_bb60/rs/s/users/e-q0422864/events/?signature=1cB0nxiYffAFaC17CkD4m9esHX4%3D&view=atom"));
		MenuItem refresh = menu.findItem(R.id.refresh_option_item);
		refresh.setIntent(refreshIntent);

		// pref menu item
		Intent prefsIntent = new Intent(getActivity().getApplicationContext(),
				WhatsRecentPreferencesActivity.class);

		MenuItem preferences = menu.findItem(R.id.settings_option_item);
		preferences.setIntent(prefsIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh_option_item:
			getActivity().startService(item.getIntent());
			break;
		case R.id.settings_option_item:
			getActivity().startActivity(item.getIntent());
			break;
		}
		return true;
	}

	// LoaderManager.LoaderCallbacks<Cursor> methods

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { WhatsRecentDatabase.ID, WhatsRecentDatabase.COL_TITLE };

		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				WhatsRecentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
