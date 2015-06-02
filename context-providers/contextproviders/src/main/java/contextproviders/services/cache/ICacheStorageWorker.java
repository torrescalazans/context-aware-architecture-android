package contextproviders.services.cache;

/**
 * TODO
 *
 * Created by Jose Torres on 3/13/15.
 */
public interface ICacheStorageWorker {

    /**
     * Sets the Thread that this instance is running on
     *
     * @param currentThread the current Thread
     */
    void setCurrentThread(Thread currentThread);

    /**
     * Sets the actions for each state of the CacheStorageTask instance.
     *
     * @param state The state being handled.
     */
    void handleState(int state);
}
