/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.nfrdroid.fragment.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.opennfr.nfrdroid.BuildConfig;
import org.opennfr.nfrdroid.R;
import org.opennfr.nfrdroid.activities.abs.BaseActivity;
import org.opennfr.nfrdroid.activities.abs.MultiPaneHandler;
import org.opennfr.nfrdroid.helpers.ExtendedHashMap;
import org.opennfr.nfrdroid.nfrDroid;

/**
 * @author sre
 */
public class AboutDialog extends ActionDialog {
	public static AboutDialog newInstance() {
		return new AboutDialog();
	}


	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String text = String.format("%s\n\n%s\n\n%s", nfrDroid.VERSION_STRING, getString(R.string.license_gplv3),
				getString(R.string.source_code_link));

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.about)
				.setMessage(text)
				.setCancelable(true);
		if (BuildConfig.FLAVOR.equals("google")) {
			builder.setNeutralButton(R.string.donate, (dialog, which) -> {
				ExtendedHashMap skus = ((BaseActivity) getActivity()).getIabItems();
				DonationDialog d = DonationDialog.newInstance(skus);
				((MultiPaneHandler) getActivity()).showDialogFragment(d, "donate_dialog");
			});
		}
		builder.setNegativeButton(R.string.licenses, (dialog, which) -> {
			nfrDroidAttributionPresenter.newInstance(getContext()).showDialog(getString(R.string.licenses));
		});

		AlertDialog dialog = builder.create();
		dialog.setOnShowListener(dialog1 -> {
			TextView message = getDialog().findViewById(android.R.id.message);
			Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
		});
		return dialog;
	}
}
