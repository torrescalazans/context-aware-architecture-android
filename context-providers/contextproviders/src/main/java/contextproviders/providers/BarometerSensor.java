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
 * Created by Jose Torres on 3/10/15.
 */
public final class BarometerSensor extends ContextDataProvider {

    // Sets the log tag
    private static final String TAG = "BarometerSensor";

    public BarometerSensor(ContextDataRequest contextDataRequest,
                           IContextDataRequestWorker contextDataRequestTask) {
        mContextDataRequest = contextDataRequest;
        mContextDataRequestTask = contextDataRequestTask;

        Log.v(TAG, "constructor - contextDataRequest.getProviderId(): " + mContextDataRequest.getProviderId());
        Log.v(TAG, "constructor - contextDataRequest.getCacheExpiryTime(): " + mContextDataRequest.getCacheExpiryTime());
    }

    @Override
    public int getProviderId() {
        Log.v(TAG, "getProviderId(): " + ContextProvidersConstants.Providers.BAROMETER);

        return ContextProvidersConstants.Providers.BAROMETER;
    }

    /**
     * TODO
     */
    @Override
    public void processContextDataRequest() {
        Log.v(TAG, "processContextDataRequest");

        ContextDataResponse lastCachedContextData = CacheStorageManager.getInstance().getLastCachedContextData(mContextDataRequest);

        if (lastCachedContextData != null) {
            Log.v(TAG, "processContextDataRequest - valid cached context data found");

            mContextDataResponse = new ContextDataResponse(lastCachedContextData.getProviderId(), true,
                    lastCachedContextData.getAccuracy(), lastCachedContextData.getTimestamp(), lastCachedContextData.getValues());

            mContextDataRequestTask.handleState(ContextDataRequestTask.CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED);
        } else {
            if (!this.registerListener(this)) {
                mContextDataRequestTask.handleState(ContextDataRequestTask.CONTEXT_DATA_REQUEST_TASK_STATE_FAILED);
            }
        }
    }

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

    @Override
    protected void unregisterListener(IContextDataProvider listener) {
        Log.v(TAG, "unregisterListener");

        mSensorManager.unregisterListener(this);
        mSensorManager = null;
        mSensor = null;
    }

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.v(TAG, "onAccuracyChanged");

    }
}
