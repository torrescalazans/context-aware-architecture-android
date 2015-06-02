package contextproviders.services.contextdataprovider;

import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;

import contextproviders.core.ContextDataRequest;
import contextproviders.core.ContextDataResponse;
import contextproviders.interfaces.IContextProvidersServiceCallback;
import contextproviders.providers.ContextDataProvider;
import contextproviders.providers.ContextDataProviderFactory;

/**
 * Created by Jose Torres on 12/2/14.
 */
public class ContextDataRequestTask implements IContextDataRequestWorker {

    // Sets the log tag
    private static final String TAG = "ContextDataRequestTask";

    // Constants for indicating the state of the request
    public static final int CONTEXT_DATA_REQUEST_TASK_STATE_FAILED = -1;
    public static final int CONTEXT_DATA_REQUEST_TASK_STATE_RUNNING = 0;
    public static final int CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED = 1;

    // TODO
    private String mApplicationId;

    // TODO
    private ContextDataRequest mContextDataRequest;

    // TODO
    private ContextDataProvider mContextDataProvider;

    // TODO
    private IContextProvidersServiceCallback mCallback;

    // The Thread on which this task is currently running.
    private Thread mCurrentThread;

    /*
    * Field containing reference to the runnable object that handle context data request
    * operation.
    */
    private Runnable mContextDataRequestWorker = null;

    // An object that contains the ThreadPool singleton.
    private static ContextDataRequestManager mContextDataRequestManager;

    /**
     * TODO
     *
     * @param applicationId
     * @param contextDataRequest
     * @param callback
     */
    public ContextDataRequestTask(String applicationId, ContextDataRequest contextDataRequest, IContextProvidersServiceCallback callback) {
        mApplicationId = applicationId;
        mContextDataRequest = contextDataRequest;
        mCallback = callback;

        mContextDataRequestManager = ContextDataRequestManager.getInstance();
        mContextDataProvider = ContextDataProviderFactory.createProvider(mContextDataRequest, this);
        mContextDataRequestWorker = new ContextDataRequestWorker(mContextDataProvider, this);

        Log.v(TAG, "constructor - mApplicationId: " + mApplicationId);
        Log.v(TAG, "constructor - mContextDataRequest.getProviderId(): " + mContextDataRequest.getProviderId());
        Log.v(TAG, "constructor - mContextDataRequest.getCacheExpiryTime(): " + mContextDataRequest.getCacheExpiryTime());
        Log.v(TAG, "constructor - mCallback: " + mCallback);
        Log.v(TAG, "constructor - mContextRequestManager: " + mContextDataRequestManager);
        Log.v(TAG, "constructor - mContextRequestWorker: " + mContextDataRequestWorker);
    }

    /**
     * TODO
     *
     * @return
     */
    public String getApplicationId() {
        return mApplicationId;
    }

    /**
     * TODO
     *
     * @return
     */
    public ContextDataRequest getContextDataRequest() {
        return mContextDataRequest;
    }

    /**
     * TODO
     *
     * @return
     */
    public ContextDataProvider getContextDataProvider() {
        return mContextDataProvider;
    }

    /**
     * TODO
     *
     * @return
     */
    public Runnable getWorkerThread() {
        return mContextDataRequestWorker;
    }

    /**
     * Returns the Thread that this Task is running on. The method must first get a lock on a
     * static field, in this case the ThreadPool singleton. The lock is needed because the
     * Thread object reference is stored in the Thread object itself, and that object can be
     * changed by processes outside of this app.
     *
     * @return
     */
    public Thread getCurrentThread() {
        synchronized(mContextDataRequestManager) {
            return mCurrentThread;
        }
    }

    /**
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    public void setCurrentThread(Thread thread) {
        synchronized(mContextDataRequestManager) {
            mCurrentThread = thread;
        }
    }

    /**
     * Delegates handling the current state of the task to the ContextRequestManager instance.
     *
     * @param state The state being delegated.
     */
    //@Override
    public void handleState(int state) {
        int contextDataRequestManagerState = -1;

        // Converts the decode state to the overall state.
        switch(state) {
            case CONTEXT_DATA_REQUEST_TASK_STATE_FAILED:
                Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_FAILED");

                contextDataRequestManagerState = ContextDataRequestManager.TASK_FAILED;

                try {
                    mCallback.error(-1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case CONTEXT_DATA_REQUEST_TASK_STATE_RUNNING:
                Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_RUNNING");

                contextDataRequestManagerState = ContextDataRequestManager.TASK_RUNNING;
                break;
            case CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED:
                Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED");

                contextDataRequestManagerState = ContextDataRequestManager.TASK_FINISHED;

                try {
                    ContextDataResponse tempContextDataResponse = mContextDataProvider.getContextData();

                    Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED - tempContextData.getProviderId(): " + tempContextDataResponse.getProviderId());
                    Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED - tempContextData.isCached(): " + tempContextDataResponse.isCached());
                    Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED - tempContextData.getAccuracy(): " + tempContextDataResponse.getAccuracy());
                    Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED- tempContextData.getTimestamp(): " + tempContextDataResponse.getTimestamp());
                    Log.v(TAG, "handleState - state: CONTEXT_DATA_REQUEST_TASK_STATE_FINISHED - tempContextData.getValues(): " + Arrays.toString(tempContextDataResponse.getValues()));

                    mCallback.success(mContextDataProvider.getContextData());
                } catch (RemoteException e) {
                    e.printStackTrace();

                    try {
                        mCallback.error(-1);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            default:
                Log.v(TAG, "handleState - default: " + state);
                break;
        }

        mContextDataRequestManager.handleState(this, contextDataRequestManagerState);
    }
}
