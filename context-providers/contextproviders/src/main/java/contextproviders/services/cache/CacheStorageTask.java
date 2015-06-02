package contextproviders.services.cache;

import android.util.Log;

import java.util.Arrays;

import contextproviders.core.ContextDataResponse;

/**
 * TODO
 *
 * Created by Jose Torres on 3/13/15.
 */
public class CacheStorageTask implements ICacheStorageWorker {

    // Sets the log tag
    private static final String TAG = "CacheStorageTask";

    // Constants for indicating the state of the cache storage
    public static final int CACHE_STORAGE_TASK_STATE_FAILED = -1;
    public static final int CACHE_STORAGE_TASK_STATE_RUNNING = 0;
    public static final int CACHE_STORAGE_TASK_STATE_FINISHED = 1;

    // TODO
    private String mApplicationId;

    // TODO
    private ContextDataResponse mContextDataResponse;

    // The Thread on which this task is currently running.
    private Thread mCurrentThread;

    /*
    * Field containing reference to the runnable object that handle database cache storage
    * operation.
    */
    private Runnable mCacheStorageWorker = null;

    // An object that contains the ThreadPool singleton.
    private static CacheStorageManager mCacheStorageManager;

    /**
     * TODO
     *
     * @param applicationId
     * @param contextDataResponse
     */
    public CacheStorageTask(String applicationId, ContextDataResponse contextDataResponse) {
        mApplicationId = applicationId;
        mContextDataResponse = contextDataResponse;

        mCacheStorageManager = CacheStorageManager.getInstance();
        mCacheStorageWorker = new CacheStorageWorker(mContextDataResponse, this);

        Log.v(TAG, "constructor - mApplicationId: " + mApplicationId);

        Log.v(TAG, "constructor - mContextDataResponse.getProviderId(): " + mContextDataResponse.getProviderId());
        Log.v(TAG, "constructor - mContextDataResponse.isCached(): " + mContextDataResponse.isCached());
        Log.v(TAG, "constructor - mContextDataResponse.getAccuracy(): " + mContextDataResponse.getAccuracy());
        Log.v(TAG, "constructor - mContextDataResponse.getTimestamp(): " + mContextDataResponse.getTimestamp());
        Log.v(TAG, "constructor - mContextDataResponse.getValues(): " + Arrays.toString(mContextDataResponse.getValues()));

        Log.v(TAG, "constructor - mCacheStorageManager: " + mCacheStorageManager);
        Log.v(TAG, "constructor - mCacheStorageWorker: " + mCacheStorageWorker);
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
    public ContextDataResponse getContextDataResponse() {
        return mContextDataResponse;
    }

    /**
     * TODO
     *
     * @return
     */
    public Runnable getWorkerThread() {
        return mCacheStorageWorker;
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
        synchronized(mCacheStorageManager) {
            return mCurrentThread;
        }
    }

    /**
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    public void setCurrentThread(Thread thread) {
        synchronized(mCacheStorageManager) {
            mCurrentThread = thread;
        }
    }

    /**
     * Delegates handling the current state of the task to the CacheStorageManager instance.
     *
     * @param state The state being delegated.
     */
    //@Override
    public void handleState(int state) {
        int cacheStorageManagerState = -1;

        // Converts the decode state to the overall state.
        switch(state) {
            case CACHE_STORAGE_TASK_STATE_FAILED:
                Log.v(TAG, "handleState - state: CACHE_STORAGE_TASK_STATE_FAILED");

                cacheStorageManagerState = CacheStorageManager.TASK_FAILED;
                break;
            case CACHE_STORAGE_TASK_STATE_RUNNING:
                Log.v(TAG, "handleState - state: CACHE_STORAGE_TASK_STATE_RUNNING");

                cacheStorageManagerState = CacheStorageManager.TASK_RUNNING;
                break;
            case CACHE_STORAGE_TASK_STATE_FINISHED:
                Log.v(TAG, "handleState - state: CACHE_STORAGE_TASK_STATE_FINISHED");

                cacheStorageManagerState = CacheStorageManager.TASK_FINISHED;
                break;
            default:
                Log.v(TAG, "handleState - default: " + state);
                break;
        }

        mCacheStorageManager.handleState(this, cacheStorageManagerState);
    }
}
