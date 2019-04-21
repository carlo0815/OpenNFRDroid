package org.opennfr.nfrdroid.widget.helper;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
	private int mSpace;

	public SpacesItemDecoration(int space) {
		mSpace = space;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.left = mSpace;
		outRect.right = mSpace;
		outRect.bottom = mSpace;
		outRect.top = mSpace;
	}
}
