/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ForwardingServerCall$SimpleForwardingServerCall
 *  io.grpc.Metadata
 *  io.grpc.ServerCall
 *  io.grpc.Status
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.Observation$Event
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.micrometer.core.instrument.binder.grpc.GrpcObservationDocumentation;
import io.micrometer.core.instrument.binder.grpc.GrpcServerObservationContext;
import io.micrometer.observation.Observation;

class ObservationGrpcServerCall<ReqT, RespT>
extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {
    private final Observation observation;

    ObservationGrpcServerCall(ServerCall<ReqT, RespT> delegate, Observation observation) {
        super(delegate);
        this.observation = observation;
    }

    public void sendMessage(RespT message) {
        this.observation.event((Observation.Event)GrpcObservationDocumentation.GrpcServerEvents.MESSAGE_SENT);
        super.sendMessage(message);
    }

    public void close(Status status, Metadata trailers) {
        if (status.getCause() != null) {
            this.observation.error(status.getCause());
        }
        GrpcServerObservationContext context = (GrpcServerObservationContext)this.observation.getContext();
        context.setStatusCode(status.getCode());
        super.close(status, trailers);
    }
}

