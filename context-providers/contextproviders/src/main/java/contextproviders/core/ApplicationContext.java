package contextproviders.core;

import android.content.Context;
import android.util.Log;

/**
 * TODO
 *
 * Created by Jose Torres on 3/10/15.
 */
public class ApplicationContext {

    // Sets the log tag
    private static final String TAG = "ApplicationContext";

    // TODO
    private static ApplicationContext mInstance = null;

    // TODO
    private Context mContext;

    /**
     * TODO
     *
     * @return
     */
    public static ApplicationContext getInstance() {
        Log.v(TAG, "getInstance()");

        synchronized (ApplicationContext.class) {
            if (mInstance == null) {
                mInstance = new ApplicationContext();
            }
        }

        return mInstance;
    }

    /**
     * TODO
     *
     * @param context
     */
    public void init(Context context) {
        Log.v(TAG, "init()");

        synchronized (ApplicationContext.class) {
            if (mContext == null) {
                mContext = context;
            }
        }
    }

    /**
     * TODO
     *
     * @return
     */
    public Context getContext() {
        Log.v(TAG, "getContext()");

        return mContext;
    }
}
