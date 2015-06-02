package contextproviders.providers;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import contextproviders.core.ContextDataRequest;
import contextproviders.core.ContextDataResponse;
import contextproviders.services.contextdataprovider.IContextDataRequestWorker;

/**
 * TODO
 *
 * Created by Jose Torres on 3/10/15.
 */
public abstract class ContextDataProvider implements IContextDataProvider {

    // TODO
    protected SensorManager mSensorManager;

    // TODO
    protected Sensor mSensor;

    // TODO
    protected ContextDataRequest mContextDataRequest;

    // TODO
    protected ContextDataResponse mContextDataResponse;

    /**
     * Defines a field that contains the calling object of type ContextDataRequestTask.
     */
    protected IContextDataRequestWorker mContextDataRequestTask;

    /**
     * A constant describing the sensor identification.
     *
     * @return The sensor identification.
     */
    public abstract int getProviderId();

    /**
     * TODO
     */
    public abstract void processContextDataRequest();

    /**
     * TODO
     *
     * @return
     */
    public ContextDataResponse getContextData() {
        return mContextDataResponse;
    };

    /**
     * TODO
     *
     * @param listener
     * @return
     */
    protected abstract boolean registerListener(IContextDataProvider listener);

    /**
     * TODO
     *
     * @param listener
     */
    protected abstract void unregisterListener(IContextDataProvider listener);
}
