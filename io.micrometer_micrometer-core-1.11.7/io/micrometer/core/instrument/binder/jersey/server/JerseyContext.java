/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.transport.RequestReplyReceiverContext
 *  org.glassfish.jersey.server.ContainerRequest
 *  org.glassfish.jersey.server.ContainerResponse
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.observation.transport.RequestReplyReceiverContext;
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public class JerseyContext
extends RequestReplyReceiverContext<ContainerRequest, ContainerResponse> {
    private RequestEvent requestEvent;

    public JerseyContext(RequestEvent requestEvent) {
        super((carrier, key) -> {
            List requestHeader = carrier.getRequestHeader(key);
            if (requestHeader == null || requestHeader.isEmpty()) {
                return null;
            }
            return (String)requestHeader.get(0);
        });
        this.requestEvent = requestEvent;
        this.setCarrier(requestEvent.getContainerRequest());
    }

    public void setRequestEvent(RequestEvent requestEvent) {
        this.requestEvent = requestEvent;
    }

    public RequestEvent getRequestEvent() {
        return this.requestEvent;
    }
}

