package contextproviders.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import contextproviders.interfaces.IContextProvidersService;
import contextproviders.interfaces.IContextProvidersServiceCallback;
import contextproviders.interfaces.IContextProvidersSubscriptionCallback;

/**
 * TODO
 *
 * Created by Jose Torres on 3/10/15.
 */
public class ContextProviders {

    // Sets the log tag
    private static final String TAG = "ContextProviders";

    // Context providers action service string
    private static final String ACTION_CONTEXT_PROVIDERS = "contextproviders.core.CONTEXT_PROVIDERS";

    // TODO
    private static ContextProviders mInstance = null;

    // TODO
    private boolean mIsInitialized;

    // TODO
    private boolean mIsServiceBound;

    // TODO
    private Context mApplicationContext;

    // TODO
    private String mApplicationToken;

    // The context providers interface we will be calling on the remote context providers service
    private IContextProvidersService mContextProvidersService = null;

    /**
     * TODO
     *
     * @return
     */
    public static ContextProviders getInstance() {
        Log.v(TAG, "getInstance()");

        synchronized (ContextProviders.class) {
            if (mInstance == null) {
                mInstance = new ContextProviders();
            }
        }

        return mInstance;
    }

    /**
     * TODO
     *
     * @param applicationContext
     * @param applicationToken
     */
    public void initialize(final Context applicationContext, final String applicationToken) {
        Log.v(TAG, "initialize() - applicationToken: " + applicationToken);

        synchronized (ContextProviders.class) {
            if (!mIsInitialized) {
                mIsInitialized = true;
                mApplicationContext = applicationContext;
                mApplicationToken = applicationToken;

                this.bindContextProvidersService();
            } else {
                Log.v(TAG, "initialize() - context providers service already initialized!");
            }
        }
    }

    // Class for interacting with the interface of the context providers service.
    private ServiceConnection mContextProvidersConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.v(TAG, "onServiceConnected()");

            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mContextProvidersService = IContextProvidersService.Stub.asInterface(service);
            mIsServiceBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.v(TAG, "onServiceDisconnected()");

            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mContextProvidersService = null;
            mApplicationContext = null;
            mApplicationToken = null;
            mIsServiceBound = false;
            mIsInitialized = false;
        }
    };

    // TODO
    private void bindContextProvidersService() {
        Log.v(TAG, "bindContextProvidersService()");

        PackageManager pm = mApplicationContext.getPackageManager();
        Intent implicitIntent = new Intent(ACTION_CONTEXT_PROVIDERS);

        List<ResolveInfo> resolvesInfo = pm.queryIntentServices(implicitIntent, 0);

        // Is somebody else trying to intercept our context providers service call?
        if (resolvesInfo != null && resolvesInfo.size() == 1) {
            ResolveInfo serviceInfo = resolvesInfo.get(0);
            String packageName = serviceInfo.serviceInfo.packageName;
            String className = serviceInfo.serviceInfo.name;
            ComponentName component = new ComponentName(packageName, className);
            Intent contextProvidersIntent = new Intent();
            contextProvidersIntent.setComponent(component);

            mApplicationContext.bindService(contextProvidersIntent,
                    mContextProvidersConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * TODO
     */
    public void disconnect() {
        Log.v(TAG, "disconnect()");

        synchronized (ContextProviders.class) {
            if (mIsInitialized && mIsServiceBound) {
                mApplicationContext.unbindService(mContextProvidersConnection);
            } else {
                Log.v(TAG, "disconnect() - context providers service already disconnected!");
            }
        }
    }

    /**
     * TODO
     */
    public void requestContextData(ContextDataRequest contextDataRequest,
                                   IContextProvidersServiceCallback callback) {
        Log.v(TAG, "requestContextData() - applicationId: " + mApplicationContext.getPackageName());
        Log.v(TAG, "requestContextData() - contextDataRequest.getProviderId(): " + contextDataRequest.getProviderId());
        Log.v(TAG, "requestContextData() - contextDataRequest.getCacheExpiryTime(): " + contextDataRequest.getCacheExpiryTime());
        Log.v(TAG, "requestContextData() - callback: " + callback);

        synchronized (ContextProviders.class) {
            if (mIsInitialized && mIsServiceBound) {
                try {
                    mContextProvidersService.requestContextData(
                            mApplicationContext.getPackageName(),
                            contextDataRequest,
                            callback);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                    e.printStackTrace();
                }
            } else {
                Log.v(TAG, "requestContextData() - context providers service disconnected!");
            }
        }
    }
}
