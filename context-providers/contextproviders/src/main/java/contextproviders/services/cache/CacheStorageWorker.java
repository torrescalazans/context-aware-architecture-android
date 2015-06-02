package contextproviders.services.cache;

import android.util.Log;

import contextproviders.core.ContextDataResponse;

/**
 * TODO
 *
 * Created by Jose Torres on 3/13/15.
 */
public class CacheStorageWorker implements Runnable {

    // Sets the log tag
    private static final String TAG = "CacheStorageWorker";

    // Defines a field that contains the calling object of type CacheStorageTask.
    private final ICacheStorageWorker mCacheStorageTask;

    //
    private ContextDataResponse mContextDataResponse;

    /**
     * TODO
     *
     * @param contextDataResponse
     * @param cacheStorageTask
     */
    public CacheStorageWorker(ContextDataResponse contextDataResponse,
                              ICacheStorageWorker cacheStorageTask) {
        Log.v(TAG, "constructor - CacheStorageWorker: " + cacheStorageTask);

        mContextDataResponse = contextDataResponse;
        mCacheStorageTask = cacheStorageTask;
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        Log.v(TAG, "thread - run()");

        /*
         * Stores the current Thread in the the CacheStorageTask instance, so that the instance
         * can interrupt the Thread.
         */
        mCacheStorageTask.setCurrentThread(Thread.currentThread());

        mCacheStorageTask.handleState(CacheStorageTask.CACHE_STORAGE_TASK_STATE_RUNNING);

        try {
            // TODO Database storage here - mContextDataResponse

            mCacheStorageTask.handleState(CacheStorageTask.CACHE_STORAGE_TASK_STATE_FINISHED);
        } catch (Exception e) {
            mCacheStorageTask.handleState(CacheStorageTask.CACHE_STORAGE_TASK_STATE_FAILED);
            e.printStackTrace();
        }
    }
}
