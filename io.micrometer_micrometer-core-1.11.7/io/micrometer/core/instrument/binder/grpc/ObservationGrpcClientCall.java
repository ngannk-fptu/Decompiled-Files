/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ClientCall
 *  io.grpc.ClientCall$Listener
 *  io.grpc.ForwardingClientCall$SimpleForwardingClientCall
 *  io.grpc.Metadata
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.Observation$Event
 *  io.micrometer.observation.Observation$Scope
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.micrometer.core.instrument.binder.grpc.GrpcClientObservationContext;
import io.micrometer.core.instrument.binder.grpc.GrpcObservationDocumentation;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientCallListener;
import io.micrometer.observation.Observation;

class ObservationGrpcClientCall<ReqT, RespT>
extends ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT> {
    private final Observation observation;

    ObservationGrpcClientCall(ClientCall<ReqT, RespT> delegate, Observation observation) {
        super(delegate);
        this.observation = observation;
    }

    public void start(ClientCall.Listener<RespT> responseListener, Metadata metadata) {
        ((GrpcClientObservationContext)this.observation.getContext()).setCarrier(metadata);
        try (Observation.Scope scope = this.observation.start().openScope();){
            super.start(new ObservationGrpcClientCallListener<RespT>(responseListener, this.observation), metadata);
        }
        catch (Throwable ex) {
            this.handleFailure(ex);
            throw ex;
        }
    }

    public void halfClose() {
        try (Observation.Scope scope = this.observation.openScope();){
            super.halfClose();
        }
        catch (Throwable ex) {
            this.handleFailure(ex);
            throw ex;
        }
    }

    public void sendMessage(ReqT message) {
        this.observation.event((Observation.Event)GrpcObservationDocumentation.GrpcClientEvents.MESSAGE_SENT);
        try (Observation.Scope scope = this.observation.openScope();){
            super.sendMessage(message);
        }
    }

    private void handleFailure(Throwable ex) {
        this.observation.error(ex).stop();
    }
}

