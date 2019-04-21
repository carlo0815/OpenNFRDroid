package org.opennfr.nfrdroid.fragment.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.opennfr.nfrdroid.R;
import org.opennfr.nfrdroid.activities.abs.BaseActivity;
import org.opennfr.nfrdroid.helpers.ExtendedHashMap;
import org.opennfr.nfrdroid.helpers.Statics;
import org.opennfr.nfrdroid.nfrDroid;

/**
 * Created by Stephan on 29.01.2015.
 */
public class DonationDialog extends ActionDialog {
	private ExtendedHashMap mItems;
	private CharSequence[] mActions;

	private static String KEY_ITEMS = "items";

	public static DonationDialog newInstance(ExtendedHashMap items) {
		DonationDialog d = new DonationDialog();
		Bundle args = new Bundle();
		args.putSerializable(KEY_ITEMS, items);
		d.setArguments(args);
		return d;
	}

	protected void init() {
		mItems = (ExtendedHashMap) getArguments().getSerializable(KEY_ITEMS);
		int i = 0;
		mActions = new CharSequence[mItems.size()];

		for (String sku : nfrDroid.SKU_LIST) {
			String price = mItems.getString(sku);
			if (price == null)
				continue;
			mActions[i] = getString(R.string.donate_sum, price);
			i++;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setRetainInstance(true);
		init();
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.donate)
				.setItems(mActions, (dialog, which) -> {
					BaseActivity ba = (BaseActivity) getActivity();
					ba.purchase(nfrDroid.SKU_LIST[which]);
					finishDialog(Statics.ACTION_NONE, null);
				});
		return builder.create();
	}
}
