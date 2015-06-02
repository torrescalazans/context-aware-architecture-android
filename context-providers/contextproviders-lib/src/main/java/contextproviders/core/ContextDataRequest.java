package contextproviders.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Jose Torres on 11/28/14.
 *
 * ContextDataRequest object over IPC
 */
public class ContextDataRequest implements Parcelable {

    // Sets the log tag
    private static final String TAG = "ContextDataRequest";

    // TODO
    private int mProviderId;

    // TODO
    private long mCacheExpiryTime;

    public static final Creator<ContextDataRequest> CREATOR;
    static {
        CREATOR = new
                Creator<ContextDataRequest>() {
                    public ContextDataRequest createFromParcel(Parcel in) {
                        return new ContextDataRequest(in);
                    }

                    public ContextDataRequest[] newArray(int size) {
                        return new ContextDataRequest[size];
                    }
                };
    }

    private ContextDataRequest(Parcel in) {
        mProviderId = in.readInt();
        mCacheExpiryTime = in.readLong();
    }

    /**
     * TODO
     *
     * @param providerId
     * @param cacheExpiryTime
     */
    public ContextDataRequest(int providerId, long cacheExpiryTime) {
        mProviderId = providerId;
        mCacheExpiryTime = cacheExpiryTime;
    }

    /**
     * TODO
     *
     * @return
     */
    public int getProviderId() {
        Log.v(TAG, "getProviderId(): " + mProviderId);

        return mProviderId;
    }

    /**
     * TODO
     *
     * @return
     */
    public long getCacheExpiryTime() {
        Log.v(TAG, "getCacheExpiryTime(): " + mCacheExpiryTime);

        return mCacheExpiryTime;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mProviderId);
        dest.writeLong(mCacheExpiryTime);
    }
}
