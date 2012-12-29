package be.kahosl.whatsrecent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import be.kahosl.whatsrecent.service.WhatsRecentDownloaderService;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(DEBUG_TAG, "Recurring alarm; requesting download service.");
        // start the download
        Intent downloader = new Intent(context, WhatsRecentDownloaderService.class);
        downloader.setData(Uri
                .parse("https://cygnus.cc.kuleuven.be/webapps/tol-data-rs-events-bb_bb60/rs/s/users/e-q0422864/events/?signature=1cB0nxiYffAFaC17CkD4m9esHX4%3D&view=atom"));
        context.startService(downloader);
    }

}
