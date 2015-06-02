package contextproviders.services.contextdataprovider;

import android.util.Log;

import contextproviders.providers.ContextDataProvider;

/**
 * TODO
 *
 * Created by Jose Torres on 12/2/14.
 */
public class ContextDataRequestWorker implements Runnable {

    // Sets the log tag
    private static final String TAG = "ContextRequestWorker";

    // Defines a field that contains the calling object of type ContextDataRequestTask.
    private final IContextDataRequestWorker mContextDataRequestTask;

    // TODO
    private ContextDataProvider mContextDataProvider;

    /**
     * TODO
     *
     * @param contextDataProvider
     * @param contextDataRequestTask
     */
    public ContextDataRequestWorker(ContextDataProvider contextDataProvider,
                                    IContextDataRequestWorker contextDataRequestTask) {
        Log.v(TAG, "constructor - ContextDataRequestWorker: " + contextDataRequestTask);

        mContextDataProvider = contextDataProvider;
        mContextDataRequestTask = contextDataRequestTask;
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
         * Stores the current Thread in the the ContextRequestTask instance, so that the instance
         * can interrupt the Thread.
         */
        mContextDataRequestTask.setCurrentThread(Thread.currentThread());

        mContextDataRequestTask.handleState(ContextDataRequestTask.CONTEXT_DATA_REQUEST_TASK_STATE_RUNNING);

        try {
            mContextDataProvider.processContextDataRequest();
        } catch (Exception e) {
            mContextDataRequestTask.handleState(ContextDataRequestTask.CONTEXT_DATA_REQUEST_TASK_STATE_FAILED);
            e.printStackTrace();
        }
    }
}
