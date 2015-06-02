package contextproviders.providers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;

import contextproviders.core.ApplicationContext;
import contextproviders.core.ContextDataRequest;
import contextproviders.core.ContextDataResponse;
import contextproviders.core.ContextProvidersConstants;
import contextproviders.services.cache.CacheStorageManager;
import contextproviders.services.contextdataprovider.ContextDataRequestTask;
import contextproviders.services.contextdataprovider.IContextDataRequestWorker;

/**
 * TODO
 *
 * Created by Jose Torres on 3/15/15.
 */
public class PhotometerSensor extends ContextDataProvider {

    // Sets the log tag
    private static final String TAG = "PhotometerSensor";

    public PhotometerSensor(ContextDataRequest contextDataRequest,
                            IContextDataRequestWorker contextDataRequestTask) {
        mContextDataRequest = contextDataRequest;
        mContextDataRequestTask = contextDataRequestTask;

        Log.v(TAG, "constructor - contextDataRequest.getProviderId(): " + mContextDataRequest.getProviderId());
        Log.v(TAG, "constructor - contextDataRequest.getCacheExpiryTime(): " + mContextDataRequest.getCacheExpiryTime());
    }

    /**
     * A constant describing the sensor identification.
     *
     * @return The sensor identification.
     */
    @Override
    public int getProviderId() {
        Log.v(TAG, "getProviderId(): " + ContextProvidersConstants.Providers.PHOTOMETER);

        return ContextProvidersConstants.Providers.PHOTOMETER;
    }

    /**
     * TODO
     */
    @Override
    public void processContextDataRequest() {
        Log.v(TAG, "processContextDataRequest");

        ContextDataResponse contextDataResponse = CacheStorageManager.getInstance().getLastCachedContextData(mContextDataRequest);

        if (contextDataResponse != null) {
            Log.v(TAG, "processContextDataRequest - valid cached context data found");

            mContextDataResponse = new ContextDataResponse(contextDataResponse.getProviderId(), true, contextDataResponse.getAccuracy(), contextDataResponse.getTimestamp(), contextDataResponse.getValues());
            mContextDataRequestTask.handleState(ContextDataRequestTask.CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED);
        } else {
            if (!this.registerListener(this)) {
                mContextDataRequestTask.handleState(ContextDataRequestTask.CONTEXT_DATA_REQUEST_TASK_STATE_FAILED);
            }
        }
    }

    /**
     * TODO
     *
     * @param listener
     * @return
     */
    @Override
    protected boolean registerListener(IContextDataProvider listener) {
        Log.v(TAG, "registerListener");

        boolean registrationResult = false;

        mSensorManager = (SensorManager) ApplicationContext.getInstance().getContext().getSystemService(Context.SENSOR_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(this.getProviderId());
        if (mSensor != null) {
            registrationResult = mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // Failure! No pressure sensor.
        }

        return registrationResult;
    }

    /**
     * TODO
     *
     * @param listener
     */
    @Override
    protected void unregisterListener(IContextDataProvider listener) {
        Log.v(TAG, "unregisterListener");

        mSensorManager.unregisterListener(this);
        mSensorManager = null;
        mSensor = null;
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link android.hardware.SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link android.hardware.SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link android.hardware.SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v(TAG, "onSensorChanged");

        Log.v(TAG, "onSensorChanged - event.accuracy: " + event.accuracy);

        /*
         * The System.currentTimeMillis() will be stored instead of used event.timestamp
         *
         * Android documentation: "The time in nanosecond at which the event happened"
         *
         * It appears that what you are dealing with is the number of nanoseconds since the
         * operating system started, also known as "uptime".
         *
         * Further info on the issue: http://code.google.com/p/android/issues/detail?id=7981
         */
        Log.v(TAG, "onSensorChanged - event.timestamp: " + event.timestamp);
        Log.v(TAG, "onSensorChanged - event.values: " + Arrays.toString(event.values));

        mContextDataResponse = new ContextDataResponse(this.getProviderId(), false, event.accuracy,
                System.currentTimeMillis(), event.values);

        mContextDataRequestTask.handleState(ContextDataRequestTask.CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED);

        this.unregisterListener(this);
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link android.hardware.SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.v(TAG, "onAccuracyChanged");

    }
}
