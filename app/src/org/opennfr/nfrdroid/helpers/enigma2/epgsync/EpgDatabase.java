package org.opennfr.nfrdroid.helpers.enigma2.epgsync;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.opennfr.nfrdroid.DatabaseHelper;
import org.opennfr.nfrdroid.R;
import org.opennfr.nfrdroid.helpers.ExtendedHashMap;
import org.opennfr.nfrdroid.helpers.NameValuePair;
import org.opennfr.nfrdroid.helpers.SimpleHttpClient;
import org.opennfr.nfrdroid.helpers.enigma2.Event;
import org.opennfr.nfrdroid.helpers.enigma2.Service;
import org.opennfr.nfrdroid.helpers.enigma2.requesthandler.EventListRequestHandler;
import org.opennfr.nfrdroid.helpers.enigma2.requesthandler.ServiceListRequestHandler;
import org.opennfr.nfrdroid.nfrDroid;

import java.util.ArrayList;

/**
 * Created by Stephan on 18.04.2014.
 */
public class EpgDatabase {
    public static final String TAG_EPG_DATABASE = EpgDatabase.class.getSimpleName();

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int mId = 1;
    DatabaseHelper mDbh;

    public void syncBouquet(Context context, String reference){
        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context, "visiondroid_picon_sync");

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        mBuilder.setContentTitle(context.getString(R.string.epg_sync))
                .setLargeIcon(bm)
                .setSmallIcon(R.drawable.ic_action_refresh);

        mDbh = DatabaseHelper.getInstance(context);

        SimpleHttpClient shc = SimpleHttpClient.getInstance(nfrDroid.getCurrentProfile());
		ServiceListRequestHandler slh = new ServiceListRequestHandler();
		ArrayList<NameValuePair> args = new ArrayList<>();
		args.add(new NameValuePair("sRef", reference));

		String xml = slh.getList(shc, args);
		ArrayList<ExtendedHashMap> services = new ArrayList<>();
		if(xml != null){
			slh.parseList(xml, services);
            Log.i(TAG_EPG_DATABASE, String.format("Syncing EPG for Bouquet %s with %s services", reference, services.size()));

            int size = services.size();
            int cnt = 0;
			for(ExtendedHashMap service : services){
                mBuilder.setContentText(service.getString(Event.KEY_SERVICE_NAME)).setProgress(size, cnt, false).setOngoing(true);
                mNotifyManager.notify(mId, mBuilder.build());
				syncService(context, service.getString(Service.KEY_REFERENCE));
                cnt++;
			}
            mBuilder.setContentText(context.getString(R.string.epg_sync_finished)).setProgress(0, 0, false).setOngoing(false);
            mNotifyManager.notify(mId, mBuilder.build());
		}
	}

	public void syncService(Context context, String reference){
		SimpleHttpClient shc = SimpleHttpClient.getInstance(nfrDroid.getCurrentProfile());
		EventListRequestHandler elh = new EventListRequestHandler();
		ArrayList<NameValuePair> args = new ArrayList<>();
		args.add(new NameValuePair("sRef", reference));

		String xml = elh.getList(shc, args);
		ArrayList<ExtendedHashMap> events = new ArrayList<>();

        int success = 0;

		if(xml != null){
			elh.parseList(xml, events);
			success += setEvents(events);
		}
        Log.i(TAG_EPG_DATABASE, String.format("Synchronized %s events", success));
    }

	public int setEvents(ArrayList<ExtendedHashMap> events){
        return mDbh.setEvents(events);
	}
}
