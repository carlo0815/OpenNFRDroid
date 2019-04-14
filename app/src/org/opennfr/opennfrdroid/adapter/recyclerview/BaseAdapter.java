package org.opennfr.opennfrdroid.adapter.recyclerview;

import androidx.recyclerview.widget.RecyclerView;

import org.opennfr.opennfrdroid.helpers.ExtendedHashMap;

import java.util.ArrayList;

/**
 * Created by Stephan on 05.05.2015.
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
	protected ArrayList<ExtendedHashMap> mData;

	public BaseAdapter(ArrayList<ExtendedHashMap> data) {
		mData = data;
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}
}
