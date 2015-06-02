package contextproviders.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BootLauncherReceiver
 *
 * Allows the context providers architecture to receive the ACTION_BOOT_COMPLETED that is
 * broadcast after the system finishes booting.
 *
 * If you don't request this permission, you will not receive the broadcast at that time.
 *
 * Though holding this permission does not have any security implications, it can have a
 * negative impact on the user experience by increasing the amount of time it takes the system
 * to start and allowing applications to have themselves running without the user being aware
 * of them.
 *
 * As such, you must explicitly declare your use of this facility to make that visible to
 * the user.
 *
 * Created by Jose Torres on 3/6/15.
 */
public class BootLauncherReceiver extends BroadcastReceiver {

    // Sets the log tag
    private static final String TAG = "BootLauncherReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");

        Intent myIntent = new Intent(context, ContextProvidersServices.class);
        context.startService(myIntent);
    }
}
