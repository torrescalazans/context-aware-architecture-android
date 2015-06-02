/*
 * IContextProvidersSubscriptionCallback.aidl
 *
 * Created by Jose Torres on 12/3/14.
 */
package contextproviders.interfaces;

import contextproviders.core.ContextDataResponse;

/**
 * Example of a callback interface used by IContextProviders to send
 * asynchronous notifications back to its clients.
 * Note that this is a one-way interface so the server does not block waiting for the client.
 */
oneway interface IContextProvidersSubscriptionCallback {

    /**
     * TODO
     */
    void contextDataChanged(in ContextDataResponse contextDataResponse);
}
