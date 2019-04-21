package org.opennfr.nfrdroid.asynctask;

import org.opennfr.nfrdroid.R;
import org.opennfr.nfrdroid.nfrDroid;

public class GetLocationsAndTagsTask extends AsyncHttpTaskBase<Void, String, Boolean> {
	public GetLocationsAndTagsTask(AsyncHttpTaskBaseHandler taskHandler) {
		super(taskHandler);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		GetLocationsAndTagsTaskHandler taskHandler = (GetLocationsAndTagsTaskHandler) mTaskHandler.get();
		if (taskHandler == null)
			return false;
		if (nfrDroid.getLocations().size() == 0) {
			if (isCancelled())
				return false;
			publishProgress(taskHandler.getString(R.string.locations) + " - " + taskHandler.getString(R.string.fetching_data));
			nfrDroid.loadLocations(getHttpClient());
		}

		if (nfrDroid.getTags().size() == 0) {
			if (isCancelled())
				return false;
			publishProgress(taskHandler.getString(R.string.tags) + " - " + taskHandler.getString(R.string.fetching_data));
			nfrDroid.loadTags(getHttpClient());
		}

		return true;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		GetLocationsAndTagsTaskHandler taskHandler = (GetLocationsAndTagsTaskHandler) mTaskHandler.get();

		if (isCancelled() || taskHandler == null)
			return;
		taskHandler.onGetLocationsAndTagsProgress(taskHandler.getString(R.string.loading), progress[0]);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		GetLocationsAndTagsTaskHandler taskHandler = (GetLocationsAndTagsTaskHandler) mTaskHandler.get();
		if (isCancelled() || taskHandler == null)
			return;
		taskHandler.onLocationsAndTagsReady();
	}

	public interface GetLocationsAndTagsTaskHandler extends AsyncHttpTaskBaseHandler {
		void onGetLocationsAndTagsProgress(String title, String progress);

		void onLocationsAndTagsReady();
	}
}
