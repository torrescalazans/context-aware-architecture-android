package contextproviders.services.contextdataprovider;

/**
 * TODO
 *
 * Created by Jose Torres on 3/12/15.
 */
public interface IContextDataRequestWorker {

    /**
     * Sets the Thread that this instance is running on
     *
     * @param currentThread the current Thread
     */
    void setCurrentThread(Thread currentThread);

    /**
     * Sets the actions for each state of the ContextRequestTask instance.
     *
     * @param state The state being handled.
     */
    void handleState(int state);
}
