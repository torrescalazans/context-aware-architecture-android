package contextproviders.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Jose Torres on 11/28/14.
 *
 * ContextDataResponse object over IPC
 */
public class ContextDataResponse implements Parcelable {

    // Sets the log tag
    private static final String TAG = "ContextDataResponse";

    // TODO
    private int mProviderId;

    // TODO
    private boolean mCached;

    // TODO
    private int mAccuracy;

    // TODO
    private long mTimestamp;

    // TODO
    private float[] mValues;


    public static final Creator<ContextDataResponse> CREATOR;
    static {
        CREATOR = new
                Creator<ContextDataResponse>() {
                    public ContextDataResponse createFromParcel(Parcel in) {
                        return new ContextDataResponse(in);
                    }

                    public ContextDataResponse[] newArray(int size) {
                        return new ContextDataResponse[size];
                    }
                };
    }

    private ContextDataResponse(Parcel in) {
        mProviderId = in.readInt();
        mCached = (in.readInt() == 1);
        mAccuracy = in.readInt();
        mTimestamp = in.readLong();
        mValues = in.createFloatArray();
    }

    /**
     * TODO
     *
     * @param providerId
     * @param cached
     * @param accuracy
     * @param timestamp
     * @param values
     */
    public ContextDataResponse(int providerId, boolean cached, int accuracy, long timestamp, float[] values) {
        mProviderId = providerId;
        mCached = cached;
        mAccuracy = accuracy;
        mTimestamp = timestamp;
        mValues = values;
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
    public boolean isCached() {
        Log.v(TAG, "isCached(): " + mCached);

        return mCached;
    }

    /**
     * TODO
     *
     * @return
     */
    public int getAccuracy() {
        Log.v(TAG, "getAccuracy(): " + mProviderId);

        return mAccuracy;
    }

    /**
     * TODO
     *
     * @return
     */
    public long getTimestamp() {
        Log.v(TAG, "getTimestamp(): " + mTimestamp);

        return mTimestamp;
    }

    /**
     * TODO
     *
     * @return
     */
    public float[] getValues() {
        Log.v(TAG, "getValues(): " + Arrays.toString(mValues));

        return mValues;
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
        dest.writeInt(mCached ? 1 : 0);
        dest.writeInt(mAccuracy);
        dest.writeLong(mTimestamp);
        dest.writeFloatArray(mValues);
    }
}
