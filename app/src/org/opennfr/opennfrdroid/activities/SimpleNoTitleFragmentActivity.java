/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.opennfrdroid.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import org.opennfr.opennfrdroid.R;

/**
 * @author sre
 * 
 */
public class SimpleNoTitleFragmentActivity extends SimpleFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTheme(R.style.Theme_OpenNFRDroid_NoTitle);
		mThemeSet = true;
		if(!getResources().getBoolean(R.bool.is_tablet))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		super.onCreate(savedInstanceState);
	}
}
