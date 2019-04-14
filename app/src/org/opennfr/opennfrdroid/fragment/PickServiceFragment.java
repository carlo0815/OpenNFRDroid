package org.opennfr.opennfrdroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.opennfr.opennfrdroid.R;
import org.opennfr.opennfrdroid.adapter.recyclerview.SimpleTextAdapter;
import org.opennfr.opennfrdroid.fragment.abs.BaseHttpRecyclerFragment;
import org.opennfr.opennfrdroid.helpers.ExtendedHashMap;
import org.opennfr.opennfrdroid.helpers.NameValuePair;
import org.opennfr.opennfrdroid.helpers.enigma2.Event;
import org.opennfr.opennfrdroid.helpers.enigma2.Service;
import org.opennfr.opennfrdroid.loader.AsyncFavListLoader;
import org.opennfr.opennfrdroid.loader.LoaderResult;
import org.opennfr.opennfrdroid.view.recyclerview.DividerItemDecoration;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Stephan on 09.11.2014.
 */
public class PickServiceFragment extends BaseHttpRecyclerFragment {
	public ExtendedHashMap mCurrentBouquet;
	public static final String KEY_BOUQUET = "bouquet";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReload = true;
		super.onCreate(savedInstanceState);
		ExtendedHashMap up = new ExtendedHashMap();
		up.put(Service.KEY_REFERENCE, AsyncFavListLoader.REF_FAVS);
		up.put(Service.KEY_NAME, getString(R.string.services));
		mCurrentBouquet = up;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new SimpleTextAdapter(mMapList, android.R.layout.simple_list_item_1,
				new String[]{Event.KEY_SERVICE_NAME}, new int[]{android.R.id.text1});
		getRecyclerView().setAdapter(mAdapter);
		getRecyclerView().addItemDecoration(new DividerItemDecoration(getAppCompatActivity(), null));
	}

	@Override
	public void onItemClick(RecyclerView parent, View view, int position, long id) {
		mCurrentBouquet = mMapList.get(position);
		Intent data = new Intent();
		data.putExtra(KEY_BOUQUET, mCurrentBouquet);
		finish(Activity.RESULT_OK, data);
	}

	@Override
	public ArrayList<NameValuePair> getHttpParams(int loader) {
		ArrayList<NameValuePair> params = new ArrayList<>();
		params.add(new NameValuePair("bRef", mCurrentBouquet.getString(Service.KEY_REFERENCE)));
		return params;
	}

	@NonNull
	@Override
	public Loader<LoaderResult<ArrayList<ExtendedHashMap>>> onCreateLoader(int i, Bundle args) {
		return new AsyncFavListLoader(getAppCompatActivity(), args);
	}

}
