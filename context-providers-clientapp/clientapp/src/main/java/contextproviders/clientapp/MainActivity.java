package contextproviders.clientapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import contextproviders.core.ContextDataRequest;
import contextproviders.core.ContextDataResponse;
import contextproviders.core.ContextProviders;
import contextproviders.core.ContextProvidersConstants;
import contextproviders.interfaces.IContextProvidersService;
import contextproviders.interfaces.IContextProvidersServiceCallback;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    static final String ACTION_CONTEXT_PROVIDERS = "contextproviders.core.CONTEXT_PROVIDERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.buttonStartServices);
        button.setOnClickListener(mStartContextProvidersServiceListener);

        button = (Button)findViewById(R.id.buttonStopServices);
        button.setOnClickListener(mStopContextProvidersServiceListener);

        button = (Button)findViewById(R.id.buttonBindServices);
        button.setOnClickListener(mBindContextProvidersServiceListener);

        button = (Button)findViewById(R.id.buttonUnbindServices);
        button.setOnClickListener(mUnbindContextProvidersServiceListener);

        button = (Button)findViewById(R.id.buttonRequestContext01);
        button.setOnClickListener(mRequestContextData01Listener);

        button = (Button)findViewById(R.id.buttonRequestContext02);
        button.setOnClickListener(mRequestContextData02Listener);

        ContextProviders.getInstance().initialize(getApplicationContext(), "appToken");
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}.
     * <p/>
     * <p>When activity B is launched in front of activity A, this callback will
     * be invoked on A.  B will not be created until A's {@link #onPause} returns,
     * so be sure to not do anything lengthy here.
     * <p/>
     * <p>This callback is mostly used for saving any persistent state the
     * activity is editing, to present a "edit in place" model to the user and
     * making sure nothing is lost if there are not enough resources to start
     * the new activity without first killing this one.  This is also a good
     * place to do things like stop animations and other things that consume a
     * noticeable amount of CPU in order to make the switch to the next activity
     * as fast as possible, or to close resources that are exclusive access
     * such as the camera.
     * <p/>
     * <p>In situations where the system needs more memory it may kill paused
     * processes to reclaim resources.  Because of this, you should be sure
     * that all of your state is saved by the time you return from
     * this function.  In general {@link #onSaveInstanceState} is used to save
     * per-instance state in the activity and this method is used to store
     * global persistent data (in content providers, files, etc.)
     * <p/>
     * <p>After receiving this call you will usually receive a following call
     * to {@link #onStop} (after the next activity has been resumed and
     * displayed), however in some cases there will be a direct call back to
     * {@link #onResume} without going through the stopped state.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onStop
     */
    @Override
    protected void onPause() {
        ContextProviders.getInstance().Disconnect();
        super.onPause();
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        ContextProviders.getInstance().initialize(getApplicationContext(), "appToken");
        super.onResume();
    }

    /**
     * Perform any final cleanup before an activity is destroyed.  This can
     * happen either because the activity is finishing (someone called
     * {@link #finish} on it, or because the system is temporarily destroying
     * this instance of the activity to save space.  You can distinguish
     * between these two scenarios with the {@link #isFinishing} method.
     * <p/>
     * <p><em>Note: do not count on this method being called as a place for
     * saving data! For example, if an activity is editing data in a content
     * provider, those edits should be committed in either {@link #onPause} or
     * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
     * free resources like threads that are associated with an activity, so
     * that a destroyed activity does not leave such things around while the
     * rest of its application is still running.  There are situations where
     * the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to
     * do things that are intended to remain around after the process goes
     * away.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onPause
     * @see #onStop
     * @see #finish
     * @see #isFinishing
     */
    @Override
    protected void onDestroy() {
        ContextProviders.getInstance().Disconnect();
        super.onDestroy();
    }

    private OnClickListener mStartContextProvidersServiceListener = new OnClickListener() {
        public void onClick(View v) {
            // Make sure the service is started. It will continue running
            // until someone calls stopService().
            PackageManager pm = getApplicationContext().getPackageManager();
            Intent implicitIntent = new Intent(ACTION_CONTEXT_PROVIDERS);

            List<ResolveInfo> resolvesInfo = pm.queryIntentServices(implicitIntent, 0);

            // Is somebody else trying to intercept our context services call?
            if (resolvesInfo != null && resolvesInfo.size() == 1) {
                ResolveInfo serviceInfo = resolvesInfo.get(0);
                String packageName = serviceInfo.serviceInfo.packageName;
                String className = serviceInfo.serviceInfo.name;
                ComponentName component = new ComponentName(packageName, className);
                Intent contextProvidersIntent = new Intent();
                contextProvidersIntent.setComponent(component);

                startService(contextProvidersIntent);

                // As part of the sample, tell the user what happened.
                Toast.makeText(MainActivity.this, R.string.start_service,
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private OnClickListener mStopContextProvidersServiceListener = new OnClickListener() {
        public void onClick(View v) {
            // Cancel a previous call to startService().
            // Note that the service will not actually stop at this point if there are
            // still bound clients.

            PackageManager pm = getApplicationContext().getPackageManager();
            Intent implicitIntent = new Intent(ACTION_CONTEXT_PROVIDERS);

            List<ResolveInfo> resolvesInfo = pm.queryIntentServices(implicitIntent, 0);

            // Is somebody else trying to intercept our context services call?
            if (resolvesInfo != null && resolvesInfo.size() == 1) {
                ResolveInfo serviceInfo = resolvesInfo.get(0);
                String packageName = serviceInfo.serviceInfo.packageName;
                String className = serviceInfo.serviceInfo.name;
                ComponentName component = new ComponentName(packageName, className);
                Intent contextProvidersIntent = new Intent();
                contextProvidersIntent.setComponent(component);

                stopService(contextProvidersIntent);

                // As part of the sample, tell the user what happened.
                Toast.makeText(MainActivity.this, R.string.stop_service,
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private OnClickListener mBindContextProvidersServiceListener = new OnClickListener() {
        public void onClick(View v) {
            ContextProviders.getInstance().initialize(getApplicationContext(), "appToken");
        }
    };

    private OnClickListener mUnbindContextProvidersServiceListener = new OnClickListener() {
        public void onClick(View v) {
            ContextProviders.getInstance().Disconnect();
        }
    };

    private OnClickListener mRequestContextData01Listener = new OnClickListener() {
        public void onClick(View v) {
            ContextDataRequest contextDataRequest = new ContextDataRequest(
                ContextProvidersConstants.Providers.BAROMETER,
                ContextProvidersConstants.Cache.EXPIRY_TIME_TWO_SECOND
            );

            ContextProviders.getInstance().requestContextData(contextDataRequest,
                    mContextProvidersCallback);
        }
    };




    private OnClickListener mRequestContextData02Listener = new OnClickListener() {
        public void onClick(View v) {

            ContextDataRequest contextDataRequest = new ContextDataRequest(
                ContextProvidersConstants.Providers.PHOTOMETER,
                ContextProvidersConstants.Cache.EXPIRY_TIME_FIVE_SECOND
            );

            ContextProviders.getInstance().requestContextData(contextDataRequest,
                                                              mContextProvidersCallback);
        }
    };

    /**
     * This implementation is used to receive callbacks from the remote service.
     */
    private IContextProvidersServiceCallback mContextProvidersCallback = new IContextProvidersServiceCallback.Stub() {

        private static final String TAG = "ContextProvidersCallback";

        /**
         * The success handling code
         */
        public void success(ContextDataResponse contextDataResponse) {
            Log.v(TAG, "success - contextDataResponse.getProviderId(): " + contextDataResponse.getProviderId());
            Log.v(TAG, "success - contextDataResponse.isCached(): " + contextDataResponse.isCached());
            Log.v(TAG, "success - contextDataResponse.getAccuracy(): " + contextDataResponse.getAccuracy());
            Log.v(TAG, "success - contextDataResponse.getTimestamp(): " + contextDataResponse.getTimestamp());
            Log.v(TAG, "success - contextDataResponse.getValues(): " + Arrays.toString(contextDataResponse.getValues()));

        }

        /**
         * The error handling code
         */
        public void error(int errorCode) {
            Log.v(TAG, "error - errorCode: " + errorCode);

        }
    };




}
