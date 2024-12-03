/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ClientCall$Listener
 *  io.grpc.ForwardingClientCallListener$SimpleForwardingClientCallListener
 *  io.grpc.Metadata
 *  io.grpc.Status
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.Observation$Event
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Status;
import io.micrometer.core.instrument.binder.grpc.GrpcClientObservationContext;
import io.micrometer.core.instrument.binder.grpc.GrpcObservationDocumentation;
import io.micrometer.observation.Observation;

class ObservationGrpcClientCallListener<RespT>
extends ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT> {
    private final Observation observation;

    ObservationGrpcClientCallListener(ClientCall.Listener<RespT> delegate, Observation observation) {
        super(delegate);
        this.observation = observation;
    }

    public void onClose(Status status, Metadata metadata) {
        GrpcClientObservationContext context = (GrpcClientObservationContext)this.observation.getContext();
        context.setStatusCode(status.getCode());
        if (status.getCause() != null) {
            this.observation.error(status.getCause());
        }
        this.observation.stop();
        super.onClose(status, metadata);
    }

    public void onMessage(RespT message) {
        this.observation.event((Observation.Event)GrpcObservationDocumentation.GrpcClientEvents.MESSAGE_RECEIVED);
        super.onMessage(message);
    }
}

