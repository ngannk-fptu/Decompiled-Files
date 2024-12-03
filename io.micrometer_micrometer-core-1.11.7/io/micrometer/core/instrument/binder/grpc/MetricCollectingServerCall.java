/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ForwardingServerCall$SimpleForwardingServerCall
 *  io.grpc.Metadata
 *  io.grpc.ServerCall
 *  io.grpc.Status
 *  io.grpc.Status$Code
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;

class MetricCollectingServerCall<Q, A>
extends ForwardingServerCall.SimpleForwardingServerCall<Q, A> {
    private final Counter responseCounter;
    private Status.Code responseCode = Status.Code.UNKNOWN;

    public MetricCollectingServerCall(ServerCall<Q, A> delegate, Counter responseCounter) {
        super(delegate);
        this.responseCounter = responseCounter;
    }

    public Status.Code getResponseCode() {
        return this.responseCode;
    }

    public void close(Status status, Metadata responseHeaders) {
        this.responseCode = status.getCode();
        super.close(status, responseHeaders);
    }

    public void sendMessage(A responseMessage) {
        this.responseCounter.increment();
        super.sendMessage(responseMessage);
    }
}

