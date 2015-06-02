/*
 * IContextProvidersServiceCallback.aidl
 *
 * Created by Jose Torres on 12/2/14.
 */
package contextproviders.interfaces;

import contextproviders.core.ContextDataResponse;

/**
 * Example of a callback interface used by IContextProvidersService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
oneway interface IContextProvidersServiceCallback {

    /**
     * TODO
     */
    void success(in ContextDataResponse contextDataResponse);

    /**
     * TODO
     */
    void error(int errorCode);
}
