package contextproviders.services.contextdataprovider;

import android.util.Log;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import contextproviders.core.ContextDataRequest;
import contextproviders.interfaces.IContextProvidersServiceCallback;
import contextproviders.services.cache.CacheStorageManager;

/**
 * TODO
 *
 * Created by Jose Torres on 12/2/14.
 */
public class ContextDataRequestManager {

    // Sets the log tag
    private static final String TAG = "ContextDataRequestManager";

    // TODO
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
    private static ContextDataRequestManager mInstance = null;

    // A managed pool of background ContextDataRequestWorker threads
    private final ThreadPoolExecutor mThreadPoolExecutor;

    // A queue of ContextDataRequestTask tasks. Tasks are handed to the ThreadPool.
    private final Queue<ContextDataRequestTask> mContextDataRequestTaskWorkQueue;

    /*
     * A queue of ContextDataRequestWorker threads. Threads are managed by the ThreadPool.
     *
     * BlockingQueue implementations are thread-safe
     */
    private final BlockingQueue<Runnable> mContextRequestWorkQueue;

    /*
     * TODO
     */
    private ContextDataRequestManager() {
        //TODO check the queue limit based on configuration file

        /*
         * Creates a work queue for the pool of Thread objects used for revolve context requests,
         * using a linked list queue that blocks when the queue is empty.
         */
        mContextRequestWorkQueue = new LinkedBlockingQueue<Runnable>(10);

        /*
         * Creates a work queue for the set of task objects that control context requests,
         * using a linked list queue that blocks when the queue is empty.
         */
        mContextDataRequestTaskWorkQueue = new LinkedBlockingQueue<ContextDataRequestTask>();

        /*
         * Creates a new pool of Thread objects for the context request work queue
         */
        mThreadPoolExecutor = new ThreadPoolExecutor(
            INITIAL_CORE_POOL_SIZE,
            NUMBER_OF_AVAILABLE_CORES,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            mContextRequestWorkQueue
            //TODO RejectedExecutionHandler
        );

        Log.v(TAG, "ContextRequestManager - INITIAL_CORE_POOL_SIZE: " + INITIAL_CORE_POOL_SIZE);
        Log.v(TAG, "ContextRequestManager - NUMBER_OF_AVAILABLE_CORES: " + NUMBER_OF_AVAILABLE_CORES);
        Log.v(TAG, "ContextRequestManager - KEEP_ALIVE_TIME: " + KEEP_ALIVE_TIME);
        Log.v(TAG, "ContextRequestManager - KEEP_ALIVE_TIME_UNIT: " + KEEP_ALIVE_TIME_UNIT);
        Log.v(TAG, "ContextRequestManager - mContextRequestWorkQueue.remainingCapacity(): " + mContextRequestWorkQueue.remainingCapacity());
    }

    /**
     * The Singleton design pattern to ensure that only one instance of this class is created
     * in order to provide a global point of access that is used by simultaneous context data
     * requests.
     *
     * Thread-safe class with concurrent access support during the context data requests.
     *
     * @return unique ContextDataRequestManager instance
     */
    public static ContextDataRequestManager getInstance() {
        synchronized (ContextDataRequestManager.class) {
            if (mInstance == null) {
                mInstance = new ContextDataRequestManager();
            }
        }

        return mInstance;
    }

    /**
     * TODO
     *
     * @param contextDataRequestTask
     * @param state
     */
    public void handleState(ContextDataRequestTask contextDataRequestTask, int state) {
        Log.v(TAG, "handleState - contextRequestTask.getWorkerThread(): " + contextDataRequestTask.getWorkerThread());
        Log.v(TAG, "handleState - mThreadPoolExecutor: " + mThreadPoolExecutor);

        switch (state) {
            case TASK_FAILED:
                Log.v(TAG, "handleState - state: TASK_FAILED");

                // TODO
                break;
            case TASK_RUNNING:
                Log.v(TAG, "handleState - state: TASK_RUNNING");

                // TODO
                break;

            case TASK_FINISHED:
                Log.v(TAG, "handleState - state: TASK_FINISHED");

                CacheStorageManager.getInstance().storeContextData(contextDataRequestTask.getApplicationId(),
                        contextDataRequestTask.getContextDataProvider().getContextData());
                break;
            default:
                Log.v(TAG, "handleState - default: " + state);

        }
    }

    /**
     * TODO
     *
     * @param applicationId
     * @param contextDataRequest
     * @param callback
     */
    public void requestContextData(String applicationId, ContextDataRequest contextDataRequest, IContextProvidersServiceCallback callback) {
        Log.v(TAG, "requestContextData - applicationId: " + applicationId);
        Log.v(TAG, "requestContextData - contextDataRequest.getProviderId(): " + contextDataRequest.getProviderId());
        Log.v(TAG, "requestContextData - contextDataRequest.getCacheExpiryTime(): " + contextDataRequest.getCacheExpiryTime());
        Log.v(TAG, "requestContextData - callback: " + callback);

        ContextDataRequestTask contextDataRequestTask = new ContextDataRequestTask(applicationId, contextDataRequest, callback);

         /*
         * "Executes" the tasks' context request worker in order to obtain the context data
         * information.
         *
         * If no Threads are available in the thread pool, the Runnable waits in the queue.
         */
        mThreadPoolExecutor.execute(contextDataRequestTask.getWorkerThread());
    }
}
