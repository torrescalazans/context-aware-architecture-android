package contextproviders.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;

import contextproviders.services.contextdataprovider.ContextDataRequestManager;
import contextproviders.interfaces.IContextProvidersService;
import contextproviders.interfaces.IContextProvidersServiceCallback;
import contextproviders.interfaces.IContextProvidersSubscriptionCallback;

/**
 * TODO
 *
 * Created by Jose Torres on 11/28/14.
 */
public class ContextProvidersServices extends Service {

    // Sets the log tag
    private static final String TAG = "ContextProvidersService";

    /**
     * This is a list of callbacks that have been registered with the
     * service. Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    final RemoteCallbackList <IContextProvidersServiceCallback> mCallbacks
            = new RemoteCallbackList<IContextProvidersServiceCallback>();

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");

        ApplicationContext.getInstance().init(getApplicationContext());

        super.onCreate();
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");

        // Unregister all callbacks.
        mCallbacks.kill();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link android.content.Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p/>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p/>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p/>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p/>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link android.os.AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link android.content.Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");

        // Return the context providers services interface
        return mBinder;
    }

    private final IContextProvidersService.Stub mBinder = new IContextProvidersService.Stub() {

        // Sets the log tag
        private static final String TAG = "IContextProvidersService.Stub";

        public void requestContextData(String applicationId,
                                       ContextDataRequest contextDataRequest,
                                       IContextProvidersServiceCallback callback) {

            Log.v(TAG, "requestContextData() :: applicationId: " + applicationId);
            Log.v(TAG, "requestContextData() :: contextDataRequest.getProviderId(): " + contextDataRequest.getProviderId());
            Log.v(TAG, "requestContextData() :: contextDataRequest.getCacheExpiryTime(): " + contextDataRequest.getCacheExpiryTime());
            Log.v(TAG, "requestContextData() :: callback: " + callback);

            ContextDataRequestManager.getInstance().requestContextData(applicationId,
                    contextDataRequest, callback);
        }

        public void subscribeProvider(String applicationId, int providerId, IContextProvidersSubscriptionCallback callback) {
            // TODO if (callback != null) mCallbacks.register(callback);
        }

        public void unsubscribeProvider(String applicationId, int providerId) {
            // TODO if (callback != null) mCallbacks.unregister(callback);
        }
    };
}
