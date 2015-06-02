package contextproviders.providers;

import contextproviders.core.ContextDataRequest;
import contextproviders.core.ContextProvidersConstants;
import contextproviders.services.contextdataprovider.IContextDataRequestWorker;

/**
 * TODO
 *
 * Created by Jose Torres on 3/13/15.
 */
public class ContextDataProviderFactory {

    /**
     * TODO
     *
     * @param contextDataRequest
     * @param contextDataRequestTask
     * @return
     */
    public static ContextDataProvider createProvider(ContextDataRequest contextDataRequest,
                                                     IContextDataRequestWorker contextDataRequestTask) {
        ContextDataProvider contextDataProvider;

        if (contextDataRequest.getProviderId() == ContextProvidersConstants.Providers.BAROMETER) {
            contextDataProvider = new BarometerSensor(contextDataRequest, contextDataRequestTask);
        } else if (contextDataRequest.getProviderId() == ContextProvidersConstants.Providers.PHOTOMETER) {
            contextDataProvider = new PhotometerSensor(contextDataRequest, contextDataRequestTask);
        } else if (contextDataRequest.getProviderId() == ContextProvidersConstants.Providers.ACCELEROMETER) {
            contextDataProvider = new AccelerometerSensor(contextDataRequest, contextDataRequestTask);
        } else {
            throw new IllegalArgumentException();
        }

        return contextDataProvider;
    }
}
