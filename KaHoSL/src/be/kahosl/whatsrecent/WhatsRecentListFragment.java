package be.kahosl.whatsrecent;

import java.io.Serializable;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import be.kahosl.KahoslActivity;
import be.kahosl.R;
import be.kahosl.TabFragment;
import be.kahosl.whatsrecent.FilterDialog.OnCloseListDialogListener;
import be.kahosl.whatsrecent.data.WhatsRecentDatabase;
import be.kahosl.whatsrecent.data.WhatsRecentProvider;
import be.kahosl.whatsrecent.receiver.AlarmReceiver;
import be.kahosl.whatsrecent.service.WhatsRecentDownloaderService;

public class WhatsRecentListFragment extends ListFragment implements
		TabFragment, Serializable, Parcelable,
		LoaderManager.LoaderCallbacks<Cursor>, OnCloseListDialogListener,
		OnSharedPreferenceChangeListener {

	private static final long serialVersionUID = -1048473633346164964L;

	private static final int WHATSRECENT_LIST_LOADER = 0x01;

	private WhatsRecentCursorAdapter adapter;
	private FilterDialog filterDialog;

	private String filter = "%";

	public static String WHATSRECENT_URL = "";

	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity().getApplicationContext();

		getLoaderManager().initLoader(WHATSRECENT_LIST_LOADER, null, this);

		adapter = new WhatsRecentCursorAdapter(context, null);

		setListAdapter(adapter);
		setHasOptionsMenu(true);

		filterDialog = FilterDialog.newInstance(this);

		// Telkens wanneer view wordt geopend update uitvoeren, is dit nodig?
		getNewItems();
	}

	private void getNewItems() {
		Intent refreshIntent = new Intent(context,
				WhatsRecentDownloaderService.class);
		refreshIntent.setData(Uri.parse(WHATSRECENT_URL));
		getActivity().startService(refreshIntent);
		
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		preferences.registerOnSharedPreferenceChangeListener(this);

		// "https://cygnus.cc.kuleuven.be/webapps/tol-data-rs-events-bb_bb60/rs/s/users/e-q0422864/events/?signature=1cB0nxiYffAFaC17CkD4m9esHX4%3D&view=atom"F

		WHATSRECENT_URL = preferences.getString("pref_whatsrecenturl", "");
		
		if (WHATSRECENT_URL.isEmpty()) {
			setEmptyText("Gelieve eerst je Toledo-url in te vullen bij instellingen.");
		} else {
			setEmptyText("Geen mededelingen gevonden");
		}

		if (preferences.getBoolean("background_update_key", true)) {
			setRecurringAlarm(context);
		} else {
			cancelRecurringAlarm(context);
		}

		super.onViewCreated(view, savedInstanceState);
	}

	private void cancelRecurringAlarm(Context context) {
		Intent downloader = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getActivity().getSystemService(
				Context.ALARM_SERVICE);
		alarms.cancel(recurringDownload);
	}

	private void setRecurringAlarm(Context context) {

		Calendar updateTime = Calendar.getInstance();
		updateTime.setTimeInMillis(System.currentTimeMillis());
		updateTime.add(Calendar.SECOND, 10);

		Intent downloader = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getActivity().getSystemService(
				Context.ALARM_SERVICE);
		alarms.setRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis(), 5 * 60 * 1000, recurringDownload);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String projection[] = { WhatsRecentDatabase.COL_URL };
		Cursor cursor = getActivity().getContentResolver().query(
				Uri.withAppendedPath(WhatsRecentProvider.CONTENT_URI,
						String.valueOf(id)), projection, null, null, null);
		if (cursor.moveToFirst()) {
			String announcementUrl = cursor.getString(cursor
					.getColumnIndex(WhatsRecentDatabase.COL_URL));

			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(announcementUrl));
			startActivity(browserIntent);
			
		}
		cursor.close();
	}

	// options menu

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.whats_recent_menu, menu);

		// refresh menu item
		Intent refreshIntent = new Intent(
				getActivity().getApplicationContext(),
				WhatsRecentDownloaderService.class);
		refreshIntent.setData(Uri.parse(WHATSRECENT_URL));
		MenuItem refresh = menu.findItem(R.id.refresh_menu_item);
		refresh.setIntent(refreshIntent);
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
			adapter.addCheckedToCalendar(getActivity());
			break;
		case R.id.filter_menu_item:
			showFilterDialog();
			break;
		}
		return true;
	}

	// LoaderManager.LoaderCallbacks<Cursor> methods

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { WhatsRecentDatabase.ID,
				WhatsRecentDatabase.COL_TITLE, WhatsRecentDatabase.COL_URL,
				WhatsRecentDatabase.COL_COURSE, WhatsRecentDatabase.COL_AUTHOR,
				WhatsRecentDatabase.COL_VISIBLE, WhatsRecentDatabase.COL_DATE,
				WhatsRecentDatabase.COL_TYPE };

		String selection = WhatsRecentDatabase.COL_VISIBLE + "=? AND "
				+ WhatsRecentDatabase.COL_TYPE + " LIKE ?";
		String[] selectionArgs = { "1", filter };

		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				WhatsRecentProvider.CONTENT_URI, projection, selection,
				selectionArgs, null);
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

	private void showFilterDialog() {
		FragmentManager manager = getFragmentManager();
		filterDialog.show(manager, "Filter");
	}

	public void onDialogListSelection() {
		String fltr = filterDialog.getSelectedType();

		if (fltr.equals("Taak"))
			filter = "assignment";
		else if (fltr.equals("Inhoud"))
			filter = "content";
		else if (fltr.equals("Mededeling"))
			filter = "announcement";
		else
			filter = "%";

		getLoaderManager().restartLoader(WHATSRECENT_LIST_LOADER, null, this);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		// Nothing
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals("background_update_key") && !WHATSRECENT_URL.isEmpty()) {
			if (sharedPreferences.getBoolean("background_update_key", false)) {
				setRecurringAlarm(context);
			} else {
				cancelRecurringAlarm(context);
			}
		} else if (key.equals("pref_whatsrecenturl")) {
			String url = sharedPreferences.getString("pref_whatsrecenturl", "");

			if (!url.contains("https://cygnus.cc.kuleuven.be/webapps/tol-data-rs-events-bb_bb60/rs/s/users/")) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("pref_whatsrecenturl", "");
				editor.commit();
				
				WHATSRECENT_URL = "";
				
				context.getContentResolver()
				.delete(WhatsRecentProvider.CONTENT_URI, null, null);
				
				Toast.makeText(context, "Geen geldige Toledo-url..", Toast.LENGTH_SHORT)
				.show();
			} else {

				WHATSRECENT_URL = url;

				// empty db
				context.getContentResolver()
						.delete(WhatsRecentProvider.CONTENT_URI, null, null);

				//getNewItems();
			}
		}

	}

}
