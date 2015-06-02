package contextproviders.prototype.reader;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import contextproviders.core.ContextDataRequest;
import contextproviders.core.ContextDataResponse;
import contextproviders.core.ContextProviders;
import contextproviders.core.ContextProvidersConstants;
import contextproviders.interfaces.IContextProvidersServiceCallback;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    public static final long REQUEST_DATA_INTERVAL_ONE_SECOND = 1000;
    public static final long REQUEST_DATA_INTERVAL_TWO_SECONDS = 2000;

    public static final long ILLUMINANCE_THRESHOLD = 50;

    public static final int PRESENTATION_MODE_LIGHT = 1;
    public static final int PRESENTATION_MODE_DARK = 2;

    private int mCurrentPresentationMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        ContextProviders.getInstance().initialize(getApplicationContext(), "appToken");

        requestPhotometerData();
        requestAccelerometerData();
    }

    @Override
    protected void onPause() {
        //ContextProviders.getInstance().disconnect();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //ContextProviders.getInstance().initialize(getApplicationContext(), "appToken");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private void requestPhotometerData() {
        Log.v(TAG, "requestPhotometerData()");

        Timer timer = new Timer();
        //Set the schedule function and rate
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG, "requestPhotometerData() - requesting photometer data...");

                //Called each time when 2000 milliseconds (2 second) (the period parameter)
                ContextDataRequest contextDataRequest = new ContextDataRequest(
                    ContextProvidersConstants.Providers.PHOTOMETER,
                    ContextProvidersConstants.Cache.EXPIRY_TIME_FIVE_SECONDS
                );

                ContextProviders.getInstance().requestContextData(contextDataRequest,
                    mRequestPhotometerDataCallback);
            }
        },

        //Set how long before to start calling the TimerTask (in milliseconds)
        REQUEST_DATA_INTERVAL_TWO_SECONDS,
        //Set the amount of time between each execution (in milliseconds)
        REQUEST_DATA_INTERVAL_TWO_SECONDS);
    }

    private void updatePresentationMode(float illuminance) {
        Log.v(TAG, "updatePresentationMode() - illuminance: " + illuminance);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.textview_container);
        TextView textView = (TextView) findViewById(R.id.textview_message);

        if ((illuminance > ILLUMINANCE_THRESHOLD) && (mCurrentPresentationMode != PRESENTATION_MODE_LIGHT)) {
            if ((relativeLayout != null) && (textView != null)) {
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.background_presentation_mode_light));
                textView.setTextColor(getResources().getColor(R.color.textview_presentation_mode_light));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            }
        } else if ((illuminance < ILLUMINANCE_THRESHOLD) && (mCurrentPresentationMode != PRESENTATION_MODE_DARK)) {
            if ((relativeLayout != null) && (textView != null)) {
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.background_presentation_mode_dark));
                textView.setTextColor(getResources().getColor(R.color.textview_presentation_mode_dark));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            }
        }
    }

    /**
     * This implementation is used to receive callbacks from the remote service.
     */
    private IContextProvidersServiceCallback mRequestPhotometerDataCallback = new IContextProvidersServiceCallback.Stub() {

        private static final String TAG = "RequestPhotometerDataCallback";

        /**
         * This is called by the remote service regularly to tell us about
         * new values. Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */
        public void success(final ContextDataResponse contextDataResponse) {
            Log.v(TAG, "success - contextDataResponse.getProviderId(): " + contextDataResponse.getProviderId());
            Log.v(TAG, "success - contextDataResponse.isCached(): " + contextDataResponse.isCached());
            Log.v(TAG, "success - contextDataResponse.getAccuracy(): " + contextDataResponse.getAccuracy());
            Log.v(TAG, "success - contextDataResponse.getTimestamp(): " + contextDataResponse.getTimestamp());
            Log.v(TAG, "success - contextDataResponse.getValues(): " + Arrays.toString(contextDataResponse.getValues()));

            runOnUiThread(new Runnable() {
                public void run() {
                    updatePresentationMode(contextDataResponse.getValues()[0]);
                }
            });
        }

        /**
         * The error handling code
         */
        public void error(int errorCode) {
            Log.v(TAG, "error - errorCode: " + errorCode);

        }
    };

    private void requestAccelerometerData() {
        Log.v(TAG, "requestAccelerometerData()");

        Timer timer = new Timer();
        //Set the schedule function and rate
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG, "requestAccelerometerData() - requesting accelerometer data...");

                //Called each time when 2000 milliseconds (2 second) (the period parameter)
                ContextDataRequest contextDataRequest = new ContextDataRequest(
                    ContextProvidersConstants.Providers.ACCELEROMETER,
                    ContextProvidersConstants.Cache.EXPIRY_TIME_TWO_SECONDS
                );

                ContextProviders.getInstance().requestContextData(contextDataRequest,
                    mRequestAccelerometerDataCallback);
            }
        },

        //Set how long before to start calling the TimerTask (in milliseconds)
        REQUEST_DATA_INTERVAL_TWO_SECONDS,
        //Set the amount of time between each execution (in milliseconds)
        REQUEST_DATA_INTERVAL_ONE_SECOND);
    }

    private void adjustVisualizatonSurface(float[] accelerometerValues) {
        Log.v(TAG, "adjustVisualizatonSurface() - accelerometerValues: " + Arrays.toString(accelerometerValues));

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.textview_container);

        if (relativeLayout != null) {
            //relativeLayout.setRotationX(-accelerometerValues[1]);
            //relativeLayout.setRotationY(-accelerometerValues[0]);
            relativeLayout.setRotationX(2);
            //relativeLayout.setRotationY(2);
        }
    }

    /**
     * This implementation is used to receive callbacks from the remote service.
     */
    private IContextProvidersServiceCallback mRequestAccelerometerDataCallback = new IContextProvidersServiceCallback.Stub() {

        private static final String TAG = "RequestAccelerometerDataCallback";

        /**
         * This is called by the remote service regularly to tell us about
         * new values. Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */
        public void success(final ContextDataResponse contextDataResponse) {
            Log.v(TAG, "success - contextDataResponse.getProviderId(): " + contextDataResponse.getProviderId());
            Log.v(TAG, "success - contextDataResponse.isCached(): " + contextDataResponse.isCached());
            Log.v(TAG, "success - contextDataResponse.getAccuracy(): " + contextDataResponse.getAccuracy());
            Log.v(TAG, "success - contextDataResponse.getTimestamp(): " + contextDataResponse.getTimestamp());
            Log.v(TAG, "success - contextDataResponse.getValues(): " + Arrays.toString(contextDataResponse.getValues()));

            runOnUiThread(new Runnable() {
                public void run() {
                    adjustVisualizatonSurface(contextDataResponse.getValues());
                }
            });
        }

        /**
         * The error handling code
         */
        public void error(int errorCode) {
            Log.v(TAG, "error - errorCode: " + errorCode);

        }
    };
}
