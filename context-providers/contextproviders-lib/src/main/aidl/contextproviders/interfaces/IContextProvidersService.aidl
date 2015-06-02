/*
 * IContextProvidersService.aidl
 *
 * Created by Jose Torres on 12/2/14.
 */
package contextproviders.interfaces;

import contextproviders.core.ContextDataRequest;
import contextproviders.interfaces.IContextProvidersServiceCallback;
import contextproviders.interfaces.IContextProvidersSubscriptionCallback;

interface IContextProvidersService {

    /**
     *
     */
    void requestContextData(String applicationId, in ContextDataRequest contextDataRequest, IContextProvidersServiceCallback callback);

    /**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
    void subscribeProvider(String applicationId, int providerId, IContextProvidersSubscriptionCallback callback);

    /**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
    void unsubscribeProvider(String applicationId, int providerId);
}
