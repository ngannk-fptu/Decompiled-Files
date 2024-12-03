/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.ClientRequestContainer;
import com.sun.jersey.api.client.filter.ContainerListener;
import com.sun.jersey.api.client.filter.OnStartConnectionListener;
import com.sun.jersey.api.client.filter.ReportingInputStream;
import com.sun.jersey.api.client.filter.ReportingOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectionListenerFilter
extends ClientFilter {
    private final OnStartConnectionListener listenerFactory;

    public ConnectionListenerFilter(OnStartConnectionListener listenerFactory) {
        if (listenerFactory == null) {
            throw new IllegalArgumentException("ConnectionListenerFilter can't be initiated without OnStartConnectionListener");
        }
        this.listenerFactory = listenerFactory;
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        ContainerListener listener = this.listenerFactory.onStart(new ClientRequestContainer(request));
        request.setAdapter(new Adapter(request.getAdapter(), listener));
        ClientResponse response = this.getNext().handle(request);
        if (response.hasEntity()) {
            InputStream entityInputStream = response.getEntityInputStream();
            listener.onReceiveStart(response.getLength());
            response.setEntityInputStream(new ReportingInputStream(entityInputStream, listener));
        } else {
            listener.onFinish();
        }
        return response;
    }

    private static final class Adapter
    extends AbstractClientRequestAdapter {
        private final ContainerListener listener;

        Adapter(ClientRequestAdapter cra, ContainerListener listener) {
            super(cra);
            this.listener = listener;
        }

        @Override
        public OutputStream adapt(ClientRequest request, OutputStream out) throws IOException {
            return new ReportingOutputStream(this.getAdapter().adapt(request, out), this.listener);
        }
    }
}

