package org.opennfr.opennfrdroid.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.opennfr.opennfrdroid.OpenNFRDroid;
import org.opennfr.opennfrdroid.R;
import org.opennfr.opennfrdroid.activities.abs.MultiPaneHandler;
import org.opennfr.opennfrdroid.adapter.recyclerview.ZapAdapter;
import org.opennfr.opennfrdroid.fragment.abs.BaseHttpRecyclerFragment;
import org.opennfr.opennfrdroid.helpers.ExtendedHashMap;
import org.opennfr.opennfrdroid.helpers.NameValuePair;
import org.opennfr.opennfrdroid.helpers.RecyclerViewPauseOnScrollListener;
import org.opennfr.opennfrdroid.helpers.Statics;
import org.opennfr.opennfrdroid.helpers.enigma2.Service;
import org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.ServiceListRequestHandler;
import org.opennfr.opennfrdroid.intents.IntentFactory;
import org.opennfr.opennfrdroid.loader.AsyncListLoader;
import org.opennfr.opennfrdroid.loader.LoaderResult;

import java.util.ArrayList;


/**
 * Created by reichi on 8/30/13.
 * This fragment is actually based on a GridView, it uses some small hacks to trick the ListFragment into working anyways
 * As a GridView is also using a ListAdapter, this avoids having to copy existing code
 */

public class ZapFragment extends BaseHttpRecyclerFragment {
	public static String BUNDLE_KEY_CURRENT_BOUQUET = "currentBouquet";

	private ExtendedHashMap mCurrentBouquet;
	private boolean mWaitingForPicker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mEnableReload = false;
		super.onCreate(savedInstanceState);
		if (mCurrentBouquet == null) {
			mReload = true;
			initTitle("");
			mCurrentBouquet = new ExtendedHashMap();
			mCurrentBouquet.put(Service.KEY_REFERENCE, OpenNFRDroid.getCurrentProfile().getDefaultRef());
			mCurrentBouquet.put(Service.KEY_NAME, OpenNFRDroid.getCurrentProfile().getDefaultRefName());
			mWaitingForPicker = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.card_grid_content, container, false);

		RecyclerView recyclerView = view.findViewById(android.R.id.list);
		recyclerView.setLayoutManager(new GridLayoutManager(getAppCompatActivity(), 3));
		RecyclerViewPauseOnScrollListener listener = new RecyclerViewPauseOnScrollListener(Statics.TAG_PICON, true, true);
		recyclerView.addOnScrollListener(listener);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new ZapAdapter(getContext(), mMapList);
		getRecyclerView().setAdapter(mAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_KEY_CURRENT_BOUQUET, mCurrentBouquet);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void createOptionsMenu(Menu menu, MenuInflater inflater) {
		super.createOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.epgbouquet, menu);
	}


	@Override
	public void onItemClick(RecyclerView rv, View v, int position, long id) {
		String ref = mMapList.get(position).getString(Service.KEY_REFERENCE);
		zapTo(ref);
	}

	@Override
	public boolean onItemLongClick(RecyclerView rv, View v, int position, long id) {
		String ref = mMapList.get(position).getString(Service.KEY_REFERENCE);
		String name = mMapList.get(position).getString(Service.KEY_NAME);
		try {
			startActivity(IntentFactory.getStreamServiceIntent(getAppCompatActivity(), ref, name));
		} catch (ActivityNotFoundException e) {
			showToast(getText(R.string.missing_stream_player));
		}
		return true;
	}

	@NonNull
	@Override
	public Loader<LoaderResult<ArrayList<ExtendedHashMap>>> onCreateLoader(int i, Bundle bundle) {
		return new AsyncListLoader(getAppCompatActivity(), new ServiceListRequestHandler(), false, bundle);
	}

	@Override
	public ArrayList<NameValuePair> getHttpParams(int loader) {
		ArrayList<NameValuePair> params = new ArrayList<>();
		params.add(new NameValuePair("sRef", mCurrentBouquet.getString(Service.KEY_REFERENCE)));

		return params;
	}

	@Override
	protected void reload() {
		if(mCurrentBouquet != null && !mCurrentBouquet.isEmpty())
			super.reload();
		else if(!mWaitingForPicker)
			pickBouquet();
	}

	@Override
	public String getLoadFinishedTitle() {
		if(mCurrentBouquet != null)
			return mCurrentBouquet.getString(Service.KEY_NAME, super.getLoadFinishedTitle());
		return super.getLoadFinishedTitle();
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult<ArrayList<ExtendedHashMap>>> loader,
							   LoaderResult<ArrayList<ExtendedHashMap>> result) {

		mMapList.clear();
		mAdapter.notifyDataSetChanged();
		if (result.isError()) {
			setEmptyText(result.getErrorText());
			return;
		}
		setEmptyText(null);

		ArrayList<ExtendedHashMap> list = result.getResult();
		setCurrentTitle(getLoadFinishedTitle());
		getAppCompatActivity().setTitle(getCurrentTitle());

		if (list.size() == 0) {
			setEmptyText(getText(R.string.no_list_item));
		} else {
			for (ExtendedHashMap service : list) {
				if (!Service.isMarker(service.getString(Service.KEY_REFERENCE)))
					mMapList.add(service);
			}
		}
		mAdapter.notifyDataSetChanged();
		mHttpHelper.onLoadFinished();
	}

	@Override
	protected boolean onItemSelected(int id) {
		switch (id) {
			case R.id.menu_pick_bouquet:
				pickBouquet();
				return true;
		}
		return super.onItemSelected(id);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
			case Statics.REQUEST_PICK_BOUQUET:
				ExtendedHashMap bouquet = (ExtendedHashMap) data.getSerializableExtra(PickServiceFragment.KEY_BOUQUET);
				String reference = bouquet.getString(Service.KEY_REFERENCE, "");
				if (!reference.equals(mCurrentBouquet.getString(Service.KEY_REFERENCE))) {
					mCurrentBouquet = bouquet;
					getRecyclerView().smoothScrollToPosition(0);
				}
				reload();
				mWaitingForPicker = false;
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void pickBouquet() {
		mWaitingForPicker = true;
		PickServiceFragment f = new PickServiceFragment();
		Bundle args = new Bundle();

		ExtendedHashMap data = new ExtendedHashMap();
		data.put(Service.KEY_REFERENCE, "default");

		args.putSerializable(sData, data);
		args.putString("action", Statics.INTENT_ACTION_PICK_BOUQUET);

		f.setArguments(args);
		f.setTargetFragment(this, Statics.REQUEST_PICK_BOUQUET);
		((MultiPaneHandler) getAppCompatActivity()).showDetails(f, true);
	}
}
