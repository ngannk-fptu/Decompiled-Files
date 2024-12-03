/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.monitoring;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.monitoring.DispatchingListenerAdapter;
import com.sun.jersey.spi.monitoring.RequestListener;
import com.sun.jersey.spi.monitoring.RequestListenerAdapter;
import com.sun.jersey.spi.monitoring.ResponseListener;
import com.sun.jersey.spi.monitoring.ResponseListenerAdapter;
import java.util.Collections;
import java.util.Set;
import javax.ws.rs.ext.ExceptionMapper;

public final class MonitoringProviderFactory {
    private static final EmptyListener EMPTY_LISTENER = new EmptyListener();

    private MonitoringProviderFactory() {
    }

    public static RequestListener createRequestListener(ProviderServices providerServices) {
        Set<RequestListener> listeners = providerServices.getProvidersAndServices(RequestListener.class);
        RequestListener requestListener = listeners.isEmpty() ? EMPTY_LISTENER : new AggregatedRequestListener(listeners);
        for (RequestListenerAdapter a : providerServices.getProvidersAndServices(RequestListenerAdapter.class)) {
            requestListener = a.adapt(requestListener);
        }
        return requestListener;
    }

    public static DispatchingListener createDispatchingListener(ProviderServices providerServices) {
        Set<DispatchingListener> listeners = providerServices.getProvidersAndServices(DispatchingListener.class);
        DispatchingListener dispatchingListener = listeners.isEmpty() ? EMPTY_LISTENER : new AggregatedDispatchingListener(listeners);
        for (DispatchingListenerAdapter a : providerServices.getProvidersAndServices(DispatchingListenerAdapter.class)) {
            dispatchingListener = a.adapt(dispatchingListener);
        }
        return dispatchingListener;
    }

    public static ResponseListener createResponseListener(ProviderServices providerServices) {
        Set<ResponseListener> listeners = providerServices.getProvidersAndServices(ResponseListener.class);
        ResponseListener responseListener = listeners.isEmpty() ? EMPTY_LISTENER : new AggregatedResponseListener(listeners);
        for (ResponseListenerAdapter a : providerServices.getProvidersAndServices(ResponseListenerAdapter.class)) {
            responseListener = a.adapt(responseListener);
        }
        return responseListener;
    }

    private static class AggregatedDispatchingListener
    implements DispatchingListener {
        private final Set<DispatchingListener> listeners;

        private AggregatedDispatchingListener(Set<DispatchingListener> listeners) {
            this.listeners = Collections.unmodifiableSet(listeners);
        }

        @Override
        public void onSubResource(long id, Class subResource) {
            for (DispatchingListener dispatchingListener : this.listeners) {
                dispatchingListener.onSubResource(id, subResource);
            }
        }

        @Override
        public void onSubResourceLocator(long id, AbstractSubResourceLocator locator) {
            for (DispatchingListener dispatchingListener : this.listeners) {
                dispatchingListener.onSubResourceLocator(id, locator);
            }
        }

        @Override
        public void onResourceMethod(long id, AbstractResourceMethod method) {
            for (DispatchingListener dispatchingListener : this.listeners) {
                dispatchingListener.onResourceMethod(id, method);
            }
        }
    }

    private static class AggregatedResponseListener
    implements ResponseListener {
        private final Set<ResponseListener> listeners;

        private AggregatedResponseListener(Set<ResponseListener> listeners) {
            this.listeners = Collections.unmodifiableSet(listeners);
        }

        @Override
        public void onError(long id, Throwable ex) {
            for (ResponseListener responseListener : this.listeners) {
                responseListener.onError(id, ex);
            }
        }

        @Override
        public void onResponse(long id, ContainerResponse response) {
            for (ResponseListener responseListener : this.listeners) {
                responseListener.onResponse(id, response);
            }
        }

        @Override
        public void onMappedException(long id, Throwable exception, ExceptionMapper mapper) {
            for (ResponseListener responseListener : this.listeners) {
                responseListener.onMappedException(id, exception, mapper);
            }
        }
    }

    private static class AggregatedRequestListener
    implements RequestListener {
        private final Set<RequestListener> listeners;

        private AggregatedRequestListener(Set<RequestListener> listeners) {
            this.listeners = Collections.unmodifiableSet(listeners);
        }

        @Override
        public void onRequest(long id, ContainerRequest request) {
            for (RequestListener requestListener : this.listeners) {
                requestListener.onRequest(id, request);
            }
        }
    }

    private static class EmptyListener
    implements RequestListener,
    ResponseListener,
    DispatchingListener {
        private EmptyListener() {
        }

        @Override
        public void onSubResource(long id, Class subResource) {
        }

        @Override
        public void onSubResourceLocator(long id, AbstractSubResourceLocator locator) {
        }

        @Override
        public void onResourceMethod(long id, AbstractResourceMethod method) {
        }

        @Override
        public void onRequest(long id, ContainerRequest request) {
        }

        @Override
        public void onError(long id, Throwable ex) {
        }

        @Override
        public void onResponse(long id, ContainerResponse response) {
        }

        @Override
        public void onMappedException(long id, Throwable exception, ExceptionMapper mapper) {
        }
    }
}

