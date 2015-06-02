package contextproviders.services.cache;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import contextproviders.core.ContextDataRequest;
import contextproviders.core.ContextDataResponse;
import contextproviders.services.contextdataprovider.ContextDataRequestTask;

/**
 * TODO
 *
 * Created by Jose Torres on 3/13/15.
 */
public class CacheStorageManager {

    // Sets the log tag
    private static final String TAG = "CacheStorageManager";

    //
    public static final int TASK_FAILED = -1;
    public static final int TASK_RUNNING = 0;
    public static final int TASK_FINISHED = 1;

    // The number of threads to keep in the pool, even if they are idle
    private static int INITIAL_CORE_POOL_SIZE = 1;

    /*
     * Gets the number of active available cores available for the device.
     * This number means the maximum number of threads to allow in the pool
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_AVAILABLE_CORES =
            Runtime.getRuntime().availableProcessors();

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // TODO
    private static CacheStorageManager mInstance = null;

    // A managed pool of background CacheStorageWorker threads
    private final ThreadPoolExecutor mThreadPoolExecutor;

    // A queue of CacheStorageTask tasks. Tasks are handed to the ThreadPool.
    private final Queue<CacheStorageTask> mCacheStorageTaskQueue;

    /*
     * A queue of CacheStorageWorker threads. Threads are managed by the ThreadPool.
     *
     * BlockingQueue implementations are thread-safe
     */
    private final BlockingQueue<Runnable> mCacheStorageWorkerQueue;

    // TODO
    private Map<Integer, Deque<ContextDataResponse>> mInMemoryCache;

    /*
     *
     */
    private CacheStorageManager() {
        //TODO check the queue limit based on configuration file

        /*
         * Creates a work queue for the pool of Thread objects used for revolve cache storage requests,
         * using a linked list queue that blocks when the queue is empty.
         */
        mCacheStorageWorkerQueue = new LinkedBlockingQueue<Runnable>(10);

        /*
         * Creates a work queue for the set of task objects that control cache storage requests,
         * using a linked list queue that blocks when the queue is empty.
         */
        mCacheStorageTaskQueue = new LinkedBlockingQueue<CacheStorageTask>();

        // TODO
        mInMemoryCache = Collections.synchronizedMap(new ConcurrentHashMap<Integer, Deque<ContextDataResponse>>());

        // Creates a new pool of Thread objects for the cache storage worker queue
        mThreadPoolExecutor = new ThreadPoolExecutor(
                INITIAL_CORE_POOL_SIZE,
                NUMBER_OF_AVAILABLE_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mCacheStorageWorkerQueue
                //TODO RejectedExecutionHandler
        );

        Log.v(TAG, "CacheStorageManager - INITIAL_CORE_POOL_SIZE: " + INITIAL_CORE_POOL_SIZE);
        Log.v(TAG, "CacheStorageManager - NUMBER_OF_AVAILABLE_CORES: " + NUMBER_OF_AVAILABLE_CORES);
        Log.v(TAG, "CacheStorageManager - KEEP_ALIVE_TIME: " + KEEP_ALIVE_TIME);
        Log.v(TAG, "CacheStorageManager - KEEP_ALIVE_TIME_UNIT: " + KEEP_ALIVE_TIME_UNIT);
        Log.v(TAG, "CacheStorageManager - mCacheStorageWorkerQueue.remainingCapacity(): " + mCacheStorageWorkerQueue.remainingCapacity());
    }

    /**
     * TODO
     *
     * @return
     */
    public static CacheStorageManager getInstance() {
        synchronized (CacheStorageManager.class) {
            if (mInstance == null) {
                mInstance = new CacheStorageManager();
            }
        }

        return mInstance;
    }

    /**
     * TODO
     *
     * @param cacheStorageTask
     * @param state
     */
    public void handleState(CacheStorageTask cacheStorageTask, int state) {
        Log.v(TAG, "handleState - cacheStorageTask.getWorkerThread(): " + cacheStorageTask.getWorkerThread());
        Log.v(TAG, "handleState - mThreadPoolExecutor: " + mThreadPoolExecutor);

        switch (state) {
            case TASK_RUNNING:
                Log.v(TAG, "handleState - state: TASK_RUNNING");

                // TODO
                break;

            case TASK_FINISHED:
                Log.v(TAG, "handleState - state: TASK_FINISHED");

                // TODO
                break;
            default:
                Log.v(TAG, "handleState - default: " + state);

        }
    }

    /**
     * TODO
     *
     * @param applicationId
     * @param contextDataResponse
     */
    public void storeContextData(String applicationId, ContextDataResponse contextDataResponse) {
        Log.v(TAG, "storeContextData - applicationId: " + applicationId);

        Log.v(TAG, "storeContextData - contextDataResponse.getProviderId(): " + contextDataResponse.getProviderId());
        Log.v(TAG, "storeContextData - contextDataResponse.isCached(): " + contextDataResponse.isCached());
        Log.v(TAG, "storeContextData - contextDataResponse.getAccuracy(): " + contextDataResponse.getAccuracy());
        Log.v(TAG, "storeContextData - contextDataResponse.getTimestamp(): " + contextDataResponse.getTimestamp());
        Log.v(TAG, "storeContextData - contextDataResponse.getValues(): " + Arrays.toString(contextDataResponse.getValues()));

        this.storeInMemory(contextDataResponse);

        CacheStorageTask cacheStorageTask = new CacheStorageTask(applicationId, contextDataResponse);

         /*
         * "Executes" the tasks' cache storage that handles cache storage in the database
         *
         * If no Threads are available in the thread pool, the Runnable waits in the queue.
         */
        mThreadPoolExecutor.execute(cacheStorageTask.getWorkerThread());
    }

    /**
     * TODO
     *
     * @param contextDataRequest
     * @return
     */
    public ContextDataResponse getLastCachedContextData(ContextDataRequest contextDataRequest) {
        Log.v(TAG, "getLastCachedContextData - contextDataRequest.getProviderId(): " + contextDataRequest.getProviderId());
        Log.v(TAG, "getLastCachedContextData - contextDataRequest.getCacheExpiryTime(): " + contextDataRequest.getCacheExpiryTime());

        ContextDataResponse lastCachedContextData = null;

        Deque<ContextDataResponse> providerContextDataResponseDeque = mInMemoryCache.get(contextDataRequest.getProviderId());
        if (providerContextDataResponseDeque != null) {
            ContextDataResponse lastCachedContextDataFound = providerContextDataResponseDeque.getLast();

            long currentTimestampInMillis = System.currentTimeMillis();
            long elapsedTimeInMillis = currentTimestampInMillis - lastCachedContextDataFound.getTimestamp();

            Log.v(TAG, "getLastCachedContextData - lastCachedContextDataFound.getTimestamp(): " + lastCachedContextDataFound.getTimestamp());
            Log.v(TAG, "getLastCachedContextData - currentTimestampInMillis: " + currentTimestampInMillis);
            Log.v(TAG, "getLastCachedContextData - elapsedTimeInMillis: " + elapsedTimeInMillis);

            if (elapsedTimeInMillis < contextDataRequest.getCacheExpiryTime()) {
                Log.v(TAG, "getLastCachedContextData - valid cached context data found!");

                lastCachedContextData = lastCachedContextDataFound;

                Log.v(TAG, "getLastCachedContextData - lastCachedContextData.getProviderId(): " + lastCachedContextData.getProviderId());
                Log.v(TAG, "getLastCachedContextData - lastCachedContextData.isCached(): " + lastCachedContextData.isCached());
                Log.v(TAG, "getLastCachedContextData - lastCachedContextData.getAccuracy(): " + lastCachedContextData.getAccuracy());
                Log.v(TAG, "getLastCachedContextData - lastCachedContextData.getTimestamp(): " + lastCachedContextData.getTimestamp());
                Log.v(TAG, "getLastCachedContextData - lastCachedContextData.getValues(): " + Arrays.toString(lastCachedContextData.getValues()));
            } else {
                Log.v(TAG, "getLastCachedContextData - cached context data expired!");
            }
        } else {
            Log.v(TAG, "getLastCachedContextData - no cached context data for provider: " + contextDataRequest.getProviderId());
        }

        return lastCachedContextData;
    }

    /*
     * TODO synchronized
     */
    private void storeInMemory(ContextDataResponse contextDataResponse) {
        Deque<ContextDataResponse> providerContextDataResponseDeque = mInMemoryCache.get(contextDataResponse.getProviderId());

        if (providerContextDataResponseDeque != null) {
           if (providerContextDataResponseDeque.size() == 10) {
                Log.v(TAG, "storeInMemory - providerContextDataDeque reached the size limit of: " + 10);

                ContextDataResponse oldestCachedContextData = providerContextDataResponseDeque.removeFirst();

                Log.v(TAG, "storeInMemory - oldestCachedContextData.getProviderId(): " + oldestCachedContextData.getProviderId());
                Log.v(TAG, "storeInMemory - oldestCachedContextData.isCached(): " + oldestCachedContextData.isCached());
                Log.v(TAG, "storeInMemory - oldestCachedContextData.getAccuracy(): " + oldestCachedContextData.getAccuracy());
                Log.v(TAG, "storeInMemory - oldestCachedContextData.getTimestamp(): " + oldestCachedContextData.getTimestamp());
                Log.v(TAG, "storeInMemory - oldestCachedContextData.getValues(): " + Arrays.toString(oldestCachedContextData.getValues()));
            }

            providerContextDataResponseDeque.addLast(contextDataResponse);
            mInMemoryCache.put(contextDataResponse.getProviderId(), providerContextDataResponseDeque);
        } else {
            Log.v(TAG, "storeInMemory - creating Deque for provider Id: " + contextDataResponse.getProviderId());

            // TODO Size from cache system config file
            providerContextDataResponseDeque = new LinkedBlockingDeque<>(10);
            providerContextDataResponseDeque.addLast(contextDataResponse);
            mInMemoryCache.put(contextDataResponse.getProviderId(), providerContextDataResponseDeque);
        }

        Log.v(TAG, "storeInMemory - contextDataResponse.getProviderId(): " + contextDataResponse.getProviderId());
        Log.v(TAG, "storeInMemory - contextDataResponse.isCached(): " + contextDataResponse.isCached());
        Log.v(TAG, "storeInMemory - contextDataResponse.getAccuracy(): " + contextDataResponse.getAccuracy());
        Log.v(TAG, "storeInMemory - contextDataResponse.getTimestamp(): " + contextDataResponse.getTimestamp());
        Log.v(TAG, "storeInMemory - contextDataResponse.getValues(): " + Arrays.toString(contextDataResponse.getValues()));

        Log.v(TAG, "storeInMemory - providerContextDataDeque.size(): " + providerContextDataResponseDeque.size());
    }
}
