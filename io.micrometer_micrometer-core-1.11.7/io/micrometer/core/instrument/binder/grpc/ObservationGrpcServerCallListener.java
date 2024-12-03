/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ForwardingServerCallListener$SimpleForwardingServerCallListener
 *  io.grpc.ServerCall$Listener
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.Observation$Event
 *  io.micrometer.observation.Observation$Scope
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall;
import io.micrometer.core.instrument.binder.grpc.GrpcObservationDocumentation;
import io.micrometer.observation.Observation;

class ObservationGrpcServerCallListener<RespT>
extends ForwardingServerCallListener.SimpleForwardingServerCallListener<RespT> {
    private final Observation observation;

    ObservationGrpcServerCallListener(ServerCall.Listener<RespT> delegate, Observation observation) {
        super(delegate);
        this.observation = observation;
    }

    public void onMessage(RespT message) {
        this.observation.event((Observation.Event)GrpcObservationDocumentation.GrpcServerEvents.MESSAGE_RECEIVED);
        try (Observation.Scope scope = this.observation.openScope();){
            super.onMessage(message);
        }
    }

    public void onHalfClose() {
        try (Observation.Scope scope = this.observation.openScope();){
            super.onHalfClose();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onCancel() {
        try (Observation.Scope scope = this.observation.openScope();){
            super.onCancel();
        }
        finally {
            this.observation.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onComplete() {
        try (Observation.Scope scope = this.observation.openScope();){
            super.onComplete();
        }
        finally {
            this.observation.stop();
        }
    }

    public void onReady() {
        try (Observation.Scope scope = this.observation.openScope();){
            super.onReady();
        }
    }
}

