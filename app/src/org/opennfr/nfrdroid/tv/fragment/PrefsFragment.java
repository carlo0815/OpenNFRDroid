package org.opennfr.nfrdroid.tv.fragment;

import android.os.Bundle;

import androidx.leanback.preference.LeanbackPreferenceFragment;

import org.opennfr.nfrdroid.R;

public class PrefsFragment extends LeanbackPreferenceFragment {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		// Load the preferences from an XML resource
		setPreferencesFromResource(R.xml.preferences, rootKey);
	}
}


