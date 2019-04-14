package org.opennfr.opennfrdroid.fragment.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.opennfr.opennfrdroid.OpenNFRDroid;
import org.opennfr.opennfrdroid.R;
import org.opennfr.opennfrdroid.activities.MainActivity;
import org.opennfr.opennfrdroid.activities.SimpleNoTitleFragmentActivity;
import org.opennfr.opennfrdroid.activities.SimpleToolbarFragmentActivity;
import org.opennfr.opennfrdroid.asynctask.SetPowerStateTask;
import org.opennfr.opennfrdroid.asynctask.SimpleResultTask;
import org.opennfr.opennfrdroid.asynctask.SleepTimerTask;
import org.opennfr.opennfrdroid.fragment.BackupFragment;
import org.opennfr.opennfrdroid.fragment.CurrentServiceFragment;
import org.opennfr.opennfrdroid.fragment.DeviceInfoFragment;
import org.opennfr.opennfrdroid.fragment.EpgBouquetFragment;
import org.opennfr.opennfrdroid.fragment.MediaPlayerFragment;
import org.opennfr.opennfrdroid.fragment.MovieListFragment;
import org.opennfr.opennfrdroid.fragment.MyPreferenceFragment;
import org.opennfr.opennfrdroid.fragment.ProfileListFragment;
import org.opennfr.opennfrdroid.fragment.ScreenShotFragment;
import org.opennfr.opennfrdroid.fragment.ServiceListFragment;
import org.opennfr.opennfrdroid.fragment.SignalFragment;
import org.opennfr.opennfrdroid.fragment.TimerListFragment;
import org.opennfr.opennfrdroid.fragment.VirtualRemotePagerFragment;
import org.opennfr.opennfrdroid.fragment.ZapFragment;
import org.opennfr.opennfrdroid.fragment.dialogs.AboutDialog;
import org.opennfr.opennfrdroid.fragment.dialogs.SendMessageDialog;
import org.opennfr.opennfrdroid.fragment.dialogs.SimpleChoiceDialog;
import org.opennfr.opennfrdroid.fragment.dialogs.SleepTimerDialog;
import org.opennfr.opennfrdroid.helpers.ExtendedHashMap;
import org.opennfr.opennfrdroid.helpers.NameValuePair;
import org.opennfr.opennfrdroid.helpers.Python;
import org.opennfr.opennfrdroid.helpers.SimpleHttpClient;
import org.opennfr.opennfrdroid.helpers.Statics;
import org.opennfr.opennfrdroid.helpers.enigma2.Event;
import org.opennfr.opennfrdroid.helpers.enigma2.Message;
import org.opennfr.opennfrdroid.helpers.enigma2.PowerState;
import org.opennfr.opennfrdroid.helpers.enigma2.SimpleResult;
import org.opennfr.opennfrdroid.helpers.enigma2.SleepTimer;
import org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.MessageRequestHandler;
import org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.SimpleResultRequestHandler;

import java.util.ArrayList;

/**
 * Created by Stephan on 25.12.2015.
 */
public class NavigationHelper implements NavigationView.OnNavigationItemSelectedListener, SetPowerStateTask.PowerStateTaskHandler, SleepTimerTask.SleepTimerTaskHandler, SimpleResultTask.SimpleResultTaskHandler {

    protected static int[] sDialogItemIds = {R.id.menu_navigation_sleeptimer, R.id.menu_navigation_remote, R.id.menu_navigation_settings, R.id.menu_navigation_message, R.id.menu_navigation_power, R.id.menu_navigation_about, R.id.menu_navigation_changelog};

    MainActivity mActivity;
    protected SetPowerStateTask mSetPowerStateTask;
    protected SleepTimerTask mSleepTimerTask;
    protected SimpleResultTask mSimpleResultTask;
    protected SimpleHttpClient mShc;

    protected int mSelectedItemId;

    public NavigationHelper(MainActivity activity) {
        mActivity = activity;
        mSelectedItemId = -1;
        getNavigationView().setNavigationItemSelectedListener(this);
    }

    protected SimpleHttpClient getHttpClient() {
        if (mShc == null)
            mShc = SimpleHttpClient.getInstance();
        return mShc;
    }

    protected MainActivity getMainActivity() {
        return mActivity;
    }

    protected NavigationView getNavigationView() {
        if (mActivity == null)
            return null;
        return (NavigationView) getMainActivity().findViewById(R.id.navigation_view);
    }

    protected View getHeaderView() {
        return getNavigationView().getHeaderView(0);
    }

    protected void clearBackStack() {
        // Pop the backstack completely everytime the user navigates "away"
        // Avoid's "stacking" fragments due to back-button behaviour that feels
        // really mysterious
        FragmentManager fm = getMainActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    protected CharSequence getText(int resId) {
        return mActivity.getText(resId);
    }

    @Override
    public void onPowerStateSet(boolean success, ExtendedHashMap result, String resultText) {
        if (!success) {
            showToast(resultText);
            return;
        }
        boolean isRunning = (Boolean) result.get(PowerState.KEY_IN_STANDBY);
        if (isRunning) {
            showToast(getString(R.string.is_running));
        } else {
            showToast(getString(R.string.in_standby));
        }
    }

    @Override
    public String getString(int resId) {
        return mActivity.getString(resId);
    }

    public void onProfileChanged() {
        mShc = SimpleHttpClient.getInstance();
    }

    protected void setSelectedItem(int itemId) {
        if (isDialogItem(itemId))
            return;
        if(itemId == R.id.menu_navigation_profiles) {
            getNavigationView().setCheckedItem(R.id.menu_none);
            return;
        }
        getNavigationView().setCheckedItem(itemId);
        mSelectedItemId = itemId;
    }

    public void navigateTo(int itemId) {
        onNavigationItemClick(itemId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onNavigationItemClick(item.getItemId());
    }

    protected boolean isDialogItem(int itemId) {
        for (int id : sDialogItemIds)
            if (id == itemId)
                return true;
        return false;
    }

    protected boolean onNavigationItemClick(int itemId) {
        setSelectedItem(itemId);
        Intent intent;
        switch (itemId) {
            case R.id.menu_navigation_timer:
                clearBackStack();
                getMainActivity().showDetails(TimerListFragment.class);
                break;

            case R.id.menu_navigation_movies:
                clearBackStack();
                getMainActivity().showDetails(MovieListFragment.class);
                break;

            case R.id.menu_navigation_services:
                clearBackStack();
                getMainActivity().showDetails(ServiceListFragment.class);
                break;

            case R.id.menu_navigation_device_info:
                clearBackStack();
                getMainActivity().showDetails(DeviceInfoFragment.class);
                break;

            case R.id.menu_navigation_current:
                clearBackStack();
                getMainActivity().showDetails(CurrentServiceFragment.class);
                break;

            case R.id.menu_navigation_remote:
                if (!isTablet()) {
                    intent = new Intent(mActivity, SimpleNoTitleFragmentActivity.class);
                    intent.putExtra("fragmentClass", VirtualRemotePagerFragment.class);
                    mActivity.startActivity(intent);
                } else {
                    clearBackStack();
                    getMainActivity().showDetails(VirtualRemotePagerFragment.class);
                }
                break;

            case R.id.menu_navigation_settings:
                if (!isTablet()) {
                    intent = new Intent(mActivity, SimpleToolbarFragmentActivity.class);
                    intent.putExtra("fragmentClass", MyPreferenceFragment.class);
                    intent.putExtra("titleResource", R.string.settings);
                    mActivity.startActivity(intent);
                } else {
                    clearBackStack();
                    getMainActivity().showDetails(MyPreferenceFragment.class);
                }
                break;

            case R.id.menu_navigation_message:
                getMainActivity().showDialogFragment(SendMessageDialog.newInstance(), "sendmessage_dialog");
                break;

            case R.id.menu_navigation_screenshot:
                clearBackStack();
                getMainActivity().showDetails(ScreenShotFragment.class);
                break;

            case Statics.ITEM_TOGGLE_STANDBY:
                setPowerState(PowerState.STATE_TOGGLE);
                break;

            case Statics.ITEM_RESTART_GUI:
                setPowerState(PowerState.STATE_GUI_RESTART);
                break;

            case Statics.ITEM_REBOOT:
                setPowerState(PowerState.STATE_SYSTEM_REBOOT);
                break;

            case Statics.ITEM_SHUTDOWN:
                setPowerState(PowerState.STATE_SHUTDOWN);
                break;

            case R.id.menu_navigation_power:
                CharSequence[] actions = {getText(R.string.standby), getText(R.string.restart_gui),
                        getText(R.string.reboot), getText(R.string.shutdown)};
                int[] actionIds = {Statics.ITEM_TOGGLE_STANDBY, Statics.ITEM_RESTART_GUI, Statics.ITEM_REBOOT,
                        Statics.ITEM_SHUTDOWN};
                getMainActivity().showDialogFragment(
                        SimpleChoiceDialog.newInstance(getString(R.string.powercontrol), actions, actionIds),
                        "powerstate_dialog");
                break;

            case R.id.menu_navigation_about:
                getMainActivity().showDialogFragment(AboutDialog.newInstance(), "about_dialog");
                break;

            case Statics.ITEM_CHECK_CONN:
                getMainActivity().onProfileChanged(OpenNFRDroid.getCurrentProfile());
                break;

            case R.id.menu_navigation_changelog:
                getMainActivity().showChangeLog(false);
                break;

            case R.id.menu_navigation_sleeptimer:
                getSleepTimer(true);
                break;

            case R.id.menu_navigation_mediaplayer:
                clearBackStack();
                getMainActivity().showDetails(MediaPlayerFragment.class);
                break;

            case R.id.menu_navigation_profiles:
                clearBackStack();
                getMainActivity().showDetails(ProfileListFragment.class);
                break;

            case R.id.menu_navigation_signal:
                clearBackStack();
                getMainActivity().showDetails(SignalFragment.class);
                break;
            case R.id.menu_navigation_zap:
                clearBackStack();
                getMainActivity().showDetails(ZapFragment.class);
                break;
            case Statics.ITEM_RELOAD:
                return false;
            case R.id.menu_navigation_epg:
                clearBackStack();
                Bundle args = new Bundle();

                String ref = OpenNFRDroid.getCurrentProfile().getDefaultRef();
                args.putString(Event.KEY_SERVICE_REFERENCE, ref);

                String name = OpenNFRDroid.getCurrentProfile().getDefaultRefName();
                args.putString(Event.KEY_SERVICE_NAME, name);

                EpgBouquetFragment f = new EpgBouquetFragment();
                f.setArguments(args);
                getMainActivity().showDetails(f);
                break;
            case R.id.menu_navigation_backup:
                clearBackStack();
                getMainActivity().showDetails(BackupFragment.class);
                break;
        }
        getMainActivity().showContent();
        return !isDialogItem(itemId);
    }

    /**
     * @param time
     * @param action
     * @param enabled
     */
    public void onSetSleepTimer(String time, String action, boolean enabled) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("cmd", SleepTimer.CMD_SET));
        params.add(new NameValuePair("time", time));
        params.add(new NameValuePair("action", action));

        if (enabled) {
            params.add(new NameValuePair("enabled", Python.TRUE));
        } else {
            params.add(new NameValuePair("enabled", Python.FALSE));
        }

        execSleepTimerTask(params, false);
    }

    /**
     *
     */
    protected void getSleepTimer(boolean showDialogOnFinish) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        execSleepTimerTask(params, showDialogOnFinish);
    }

    @Override
    public void onSleepTimerSet(boolean success, ExtendedHashMap result, boolean openDialog, String errorText) {
        if (success) {
            if (openDialog) {
                getMainActivity().showDialogFragment(SleepTimerDialog.newInstance(result), "sleeptimer_dialog");
                return;
            }
            String text = result.getString(SleepTimer.KEY_TEXT);
            showToast(text);
        } else {
            showToast(getString(R.string.error));
        }
    }

    public void execSimpleResultTask(SimpleResultRequestHandler handler, ArrayList<NameValuePair> params) {
        if (mSimpleResultTask != null) {
            mSimpleResultTask.cancel(true);
        }

        mSimpleResultTask = new SimpleResultTask(handler, this);
        mSimpleResultTask.execute(params);
    }

    @Override
    public void onSimpleResult(boolean success, ExtendedHashMap result) {
        String toastText = getString(R.string.get_content_error);
        String stateText = result.getString(SimpleResult.KEY_STATE_TEXT);

        if (stateText != null && !"".equals(stateText)) {
            toastText = stateText;
        } else if (mShc.hasError()) {
            toastText = mShc.getErrorText(getContext());
        }

        showToast(toastText);
    }


    /**
     * @param params
     */
    @SuppressWarnings("unchecked")
    protected void execSleepTimerTask(ArrayList<NameValuePair> params, boolean showDialogOnFinish) {
        if (mSleepTimerTask != null) {
            mSleepTimerTask.cancel(true);
        }

        mSleepTimerTask = new SleepTimerTask(showDialogOnFinish, this);
        mSleepTimerTask.execute(params);
    }

    /**
     * @param state The powerstate to set. For example defined in
     *              <code>helpers.enigma2.PowerState.STATE_*</code>
     */
    protected void setPowerState(String state) {
        if (mSetPowerStateTask != null) {
            mSetPowerStateTask.cancel(true);
        }

        mSetPowerStateTask = new SetPowerStateTask(this);
        mSetPowerStateTask.execute(state);
    }

    /**
     * Send a message to the target device which will be shown on TV
     *
     * @param text    The message text
     * @param type    Type of the message as defined in
     *                <code>helpers.enigma2.Message.STATE_*</code>
     * @param timeout Timeout for the message, 0 means no timeout will occur
     */
    public void onSendMessage(String text, String type, String timeout) {
        ExtendedHashMap msg = new ExtendedHashMap();
        msg.put(Message.KEY_TEXT, text);
        msg.put(Message.KEY_TYPE, type);
        msg.put(Message.KEY_TIMEOUT, timeout);

        execSimpleResultTask(new MessageRequestHandler(), Message.getParams(msg));
    }

    public void setAvailableFeatures() {
        // TODO implement feature-handling for list-navigation
    }

    protected boolean isTablet() {
        return getMainActivity().getResources().getBoolean(R.bool.is_tablet);
    }

    protected void showToast(String toastText) {
        Toast toast = Toast.makeText(getMainActivity(), toastText, Toast.LENGTH_LONG);
        toast.show();
    }

    public void onDialogAction(int action, Object details, String dialogTag) {
        onNavigationItemClick(action);
    }

    @Override
    public Context getContext() {
        return getMainActivity();
    }
}
