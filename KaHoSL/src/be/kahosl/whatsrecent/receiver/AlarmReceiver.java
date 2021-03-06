package be.kahosl.whatsrecent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import be.kahosl.whatsrecent.WhatsRecentListFragment;
import be.kahosl.whatsrecent.service.WhatsRecentDownloaderService;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String DEBUG_TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(DEBUG_TAG, "Recurring alarm; requesting download service.");
		// start the download
		if (!WhatsRecentListFragment.WHATSRECENT_URL.isEmpty()) {
			Intent downloader = new Intent(context,
					WhatsRecentDownloaderService.class);
			downloader.setData(Uri
					.parse(WhatsRecentListFragment.WHATSRECENT_URL));
			context.startService(downloader);
		}
	}

}
