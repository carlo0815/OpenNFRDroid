package org.opennfr.opennfrdroid.appwidget;

import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;

import org.opennfr.opennfrdroid.Profile;
import org.opennfr.opennfrdroid.R;
import org.opennfr.opennfrdroid.helpers.ExtendedHashMap;
import org.opennfr.opennfrdroid.helpers.NameValuePair;
import org.opennfr.opennfrdroid.helpers.Python;
import org.opennfr.opennfrdroid.helpers.SimpleHttpClient;
import org.opennfr.opennfrdroid.helpers.enigma2.SimpleResult;
import org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.RemoteCommandRequestHandler;
import org.opennfr.opennfrdroid.service.HttpIntentService;

import java.util.ArrayList;

/**
 * Created by Stephan on 08.12.13.
 */
public class WidgetService extends HttpIntentService {
	public static String TAG = WidgetService.class.getSimpleName();

	public static final int JOB_ID = 1337;

	public static final String KEY_KEYID = "key_id";
	public static final String KEY_WIDGETID = "widget_id";

	public static final String ACTION_ZAP = "org.opennfr.opennfrdroid.appwidget.WidgetService.ACTION_ZAP";
	public static final String ACTION_RCU = "org.opennfr.opennfrdroid.appwidget.WidgetService.ACTION_RCU";

	@Override
	public void onHandleWork(@NonNull Intent intent) {
		String action = intent.getAction();
		if(ACTION_RCU.equals(action))
			doRemoteRequest(intent);
		else if(ACTION_ZAP.equals(action))
			doZapRequest();
	}

	private void doRemoteRequest(Intent intent) {
		setupSSL();

		Profile profile = VirtualRemoteWidgetConfiguration.getWidgetProfile(getApplicationContext(), intent.getIntExtra(KEY_WIDGETID, -1));

		SimpleHttpClient shc = SimpleHttpClient.getInstance(profile);
		RemoteCommandRequestHandler handler = new RemoteCommandRequestHandler();
		ArrayList<NameValuePair> params = new ArrayList<>();
		params.add(new NameValuePair("command", intent.getStringExtra(KEY_KEYID)));
		params.add(new NameValuePair("rcu", "advanced"));
		String xml = handler.get(shc, params);

		if (xml != null) {
			ExtendedHashMap result = handler.parseSimpleResult(xml);
			if (Python.FALSE.equals(result.getString(SimpleResult.KEY_STATE))) {
				String errorText = result.getString(SimpleResult.KEY_STATE_TEXT, getString(R.string.connection_error));
				Log.w(TAG, result.getString(SimpleResult.KEY_STATE_TEXT));
				showToast(errorText);
			}
		} else if (shc.hasError()) {
			Log.w(TAG, shc.getErrorText(getBaseContext()));
			showToast(shc.getErrorText(getBaseContext()));
		}
	}

	private void doZapRequest(){

	}
}
