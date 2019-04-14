package org.opennfr.opennfrdroid.asynctask;

import org.opennfr.opennfrdroid.helpers.ExtendedHashMap;
import org.opennfr.opennfrdroid.helpers.enigma2.PowerState;
import org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.PowerStateRequestHandler;

/**
 * Created by Stephan on 27.12.2015.
 */
public class SetPowerStateTask extends AsyncHttpTaskBase<String, String, Boolean> {
	protected ExtendedHashMap mResult;

	public SetPowerStateTask(PowerStateTaskHandler taskHandler) {
		super(taskHandler);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		PowerStateRequestHandler handler = new PowerStateRequestHandler();
		String xml = handler.get(getHttpClient(), PowerState.getStateParams(params[0]));

		if (xml != null) {
			if (isCancelled())
				return false;
			ExtendedHashMap result = new ExtendedHashMap();
			handler.parse(xml, result);

			mResult = result;
			return true;
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		PowerStateTaskHandler resultHandler = (PowerStateTaskHandler) mTaskHandler.get();
		if (isCancelled() || resultHandler == null)
			return;

		boolean success = result && mResult != null;
		if (!success)
			mResult = new ExtendedHashMap();
		resultHandler.onPowerStateSet(success, mResult, getErrorText());
	}

	public interface PowerStateTaskHandler extends AsyncHttpTaskBaseHandler {
		void onPowerStateSet(boolean success, ExtendedHashMap result, String resultText);
	}
}
