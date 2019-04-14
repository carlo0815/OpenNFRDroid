package org.opennfr.opennfrdroid.helpers;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.opennfr.opennfrdroid.OpenNFRDroid;
import org.opennfr.opennfrdroid.helpers.enigma2.Event;
import org.opennfr.opennfrdroid.helpers.enigma2.epgsync.EpgDatabase;
import org.opennfr.opennfrdroid.service.HttpIntentService;

/**
 * Created by Stephan on 04.06.2014.
 */
public class SyncService extends HttpIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        setupSSL();
        EpgDatabase epgDatabase = new EpgDatabase();
        OpenNFRDroid.loadCurrentProfile(getApplicationContext());
        String bouquet = intent.getStringExtra(Event.KEY_SERVICE_REFERENCE);
        if(bouquet != null)
            epgDatabase.syncBouquet(getApplicationContext(), bouquet);
    }
}
