package org.opennfr.nfrdroid.fragment.abs;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.evernote.android.state.State;

import org.opennfr.nfrdroid.R;
import org.opennfr.nfrdroid.fragment.dialogs.ActionDialog;
import org.opennfr.nfrdroid.fragment.dialogs.EpgDetailBottomSheet;
import org.opennfr.nfrdroid.helpers.ExtendedHashMap;
import org.opennfr.nfrdroid.helpers.Statics;
import org.opennfr.nfrdroid.helpers.enigma2.Timer;
import org.opennfr.nfrdroid.helpers.enigma2.requesthandler.TimerAddByEventIdRequestHandler;
import org.opennfr.nfrdroid.intents.IntentFactory;


/**
 * @author sreichholf
 */
public abstract class BaseHttpRecyclerEventFragment extends BaseHttpRecyclerFragment implements
        ActionDialog.DialogActionListener {

    @State
    public String mReference;
    @State
    public String mName;
    @State
    public ExtendedHashMap mCurrentItem;

    protected ProgressDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("reference", mReference);
        outState.putString("name", mName);
        outState.putSerializable("currentItem", mCurrentItem);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        mCurrentItem = mMapList.get(position);
        EpgDetailBottomSheet epgDetailBottomSheet = EpgDetailBottomSheet.newInstance(mCurrentItem);
        getMultiPaneHandler().showDialogFragment(epgDetailBottomSheet, "epg_detail_dialog");
    }

    /**
     * @param event
     */
    protected void setTimerById(ExtendedHashMap event) {
        if (mProgress != null) {
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }
        }

        mProgress = ProgressDialog.show(getAppCompatActivity(), "", getText(R.string.saving), true);
        execSimpleResultTask(new TimerAddByEventIdRequestHandler(), Timer.getEventIdParams(event));
    }

    @Override
    public void onSimpleResult(boolean success, ExtendedHashMap result) {
        if (mProgress != null) {
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }
        }
        super.onSimpleResult(success, result);
    }

    /**
     * @param event
     */
    protected void setTimerByEventData(ExtendedHashMap event) {
        Timer.editUsingEvent(getMultiPaneHandler(), event, this);
    }

    public void onDialogAction(int action, Object details, String dialogTag) {
        switch (action) {
            case Statics.ACTION_SET_TIMER:
                setTimerById(mCurrentItem);
                break;
            case Statics.ACTION_EDIT_TIMER:
                setTimerByEventData(mCurrentItem);
                break;
            case Statics.ACTION_FIND_SIMILAR:
                mHttpHelper.findSimilarEvents(mCurrentItem);
                break;
            case Statics.ACTION_IMDB:
                IntentFactory.queryIMDb(getAppCompatActivity(), mCurrentItem);
                break;
        }
    }
}
