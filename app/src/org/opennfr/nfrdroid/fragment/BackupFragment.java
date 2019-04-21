package org.opennfr.nfrdroid.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;

import org.opennfr.nfrdroid.Profile;
import org.opennfr.nfrdroid.R;
import org.opennfr.nfrdroid.activities.abs.BaseActivity;
import org.opennfr.nfrdroid.fragment.abs.BaseFragment;
import org.opennfr.nfrdroid.helpers.backup.BackupData;
import org.opennfr.nfrdroid.helpers.backup.BackupService;
import org.opennfr.nfrdroid.nfrDroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java9.util.stream.StreamSupport;

import static org.opennfr.nfrdroid.helpers.Statics.ITEM_BACKUP_EXPORT;
import static org.opennfr.nfrdroid.helpers.Statics.ITEM_BACKUP_IMPORT;
import static org.opennfr.nfrdroid.helpers.Statics.REQUEST_BACKUP_IMPORT;

/**
 * Created by GAigner on 01/09/18.
 */
public class BackupFragment extends BaseFragment {
	private static final String TAG = BackupFragment.class.getSimpleName();

	private BackupService mBackupService;
	private BackupData mBackupData;

	private List<CheckBox> mProfilesCheckBox = new ArrayList<>();
	private CheckBox mSettingsCheckBox;
	private View mBackupView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initTitles(getString(R.string.backup));
		requestStoragePermission();
	}

	@Override
	public void createOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.backup, menu);
	}

	private void loadBackupData() {
		mBackupData = mBackupService.getBackupData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBackupService = new BackupService(getContext());
		mBackupView = inflater.inflate(R.layout.backup, null);
		mSettingsCheckBox = mBackupView.findViewById(R.id.backup_export_settings);
		loadBackupData();
		refreshView();
		return mBackupView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!hasStoragePermission()) {
			requestStoragePermission();
			return false;
		}

		switch (item.getItemId()) {
			case (ITEM_BACKUP_EXPORT):
				doExport();
				loadBackupData();
				showToast(getString(R.string.backup_export_successful));
				break;
			case ITEM_BACKUP_IMPORT:
				doImport();
				break;
			default:
				return false;
		}
		return false;
	}

	private boolean hasStoragePermission() {
		return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
	}

	private void requestStoragePermission() {
		if (!hasStoragePermission())
			ActivityCompat.requestPermissions(getAppCompatActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, BaseActivity.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE_BACKUP);
	}

	private void refreshView() {
		int currentProfileId = nfrDroid.getCurrentProfile().getId();
		LinearLayout checkboxLayout = mBackupView.findViewById(R.id.layout_backup_profile_dynamic);
		checkboxLayout.removeAllViews();
		mProfilesCheckBox.clear();
		for (Profile profile : mBackupData.getProfiles()) {
			int id = profile.getId();
			String text = profile.getName();
			if (id == currentProfileId) {
				String currentText = getString(R.string.backup_current_profile);
				text += " (" + currentText + ")";
			}
			CheckBox ch = new CheckBox(getContext());
			ch.setText(text);
			ch.setId(id);
			ch.setChecked(true);
			checkboxLayout.addView(ch);
			mProfilesCheckBox.add(ch);
		}
	}

	private void doImport() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("*/*");
		try {
			getActivity().startActivityForResult(intent, REQUEST_BACKUP_IMPORT);
		} catch (ActivityNotFoundException e) {
			showToast(e.getLocalizedMessage());
		}

	}

	private void doExport() {
		if (!mSettingsCheckBox.isChecked()) {
			mBackupData.setSettings(null);
		}
		StreamSupport.stream(mProfilesCheckBox).filter(p -> !p.isChecked()).forEach(checkBox -> {
			mBackupData.getProfiles().remove(StreamSupport.stream(mBackupData.getProfiles()).filter(p -> p.getId() == checkBox.getId()).findFirst().get());
		});
		mBackupService.doExport(mBackupData);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_BACKUP_IMPORT == requestCode && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				try {
					mBackupService.doImport(readTextFromUri(uri));
					loadBackupData();
					refreshView();
					showToast(getString(R.string.backup_import_successful));
				} catch (IOException e) {
					Log.e(TAG, "unable to readTextFromUri:" + uri, e);
					showToast(getString(R.string.backup_import_error));
				}
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String readTextFromUri(Uri uri) throws IOException {
		InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		return stringBuilder.toString();
	}

}
