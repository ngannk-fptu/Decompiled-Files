/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ClientCall$Listener
 *  io.grpc.ForwardingClientCallListener$SimpleForwardingClientCallListener
 *  io.grpc.Metadata
 *  io.grpc.Status
 *  io.grpc.Status$Code
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import java.util.function.Consumer;

class MetricCollectingClientCallListener<A>
extends ForwardingClientCallListener.SimpleForwardingClientCallListener<A> {
    private final Counter responseCounter;
    private final Consumer<Status.Code> processingDurationTiming;

    public MetricCollectingClientCallListener(ClientCall.Listener<A> delegate, Counter responseCounter, Consumer<Status.Code> processingDurationTiming) {
        super(delegate);
        this.responseCounter = responseCounter;
        this.processingDurationTiming = processingDurationTiming;
    }

    public void onClose(Status status, Metadata metadata) {
        this.processingDurationTiming.accept(status.getCode());
        super.onClose(status, metadata);
    }

    public void onMessage(A responseMessage) {
        this.responseCounter.increment();
        super.onMessage(responseMessage);
    }
}

