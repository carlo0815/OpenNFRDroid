package org.opennfr.nfrdroid.asynctask;

import org.opennfr.nfrdroid.Profile;
import org.opennfr.nfrdroid.helpers.enigma2.DeviceDetector;

import java.util.ArrayList;

public class DetectDevicesTask extends AsyncHttpTaskBase<Void, Void, ArrayList<Profile>> {
	public DetectDevicesTask(AsyncHttpTaskBaseHandler taskHandler) {
		super(taskHandler);
	}

	@Override
	protected ArrayList<Profile> doInBackground(Void... params) {
		return DeviceDetector.getAvailableHosts();
	}

	@Override
	protected void onPostExecute(ArrayList<Profile> profiles) {
		DetectDevicesTaskHandler taskHandler = (DetectDevicesTaskHandler) mTaskHandler.get();
		if (!isCancelled() && taskHandler != null)
			taskHandler.onDevicesDetected(profiles);
	}

	public interface DetectDevicesTaskHandler extends AsyncHttpTaskBaseHandler {
		void onDevicesDetected(ArrayList<Profile> profiles);
	}
}
