/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ClientCall
 *  io.grpc.ClientCall$Listener
 *  io.grpc.ForwardingClientCall$SimpleForwardingClientCall
 *  io.grpc.Metadata
 *  io.grpc.Status$Code
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.binder.grpc.MetricCollectingClientCallListener;
import java.util.function.Consumer;

class MetricCollectingClientCall<Q, A>
extends ForwardingClientCall.SimpleForwardingClientCall<Q, A> {
    private final Counter requestCounter;
    private final Counter responseCounter;
    private final Consumer<Status.Code> processingDurationTiming;

    MetricCollectingClientCall(ClientCall<Q, A> delegate, Counter requestCounter, Counter responseCounter, Consumer<Status.Code> processingDurationTiming) {
        super(delegate);
        this.requestCounter = requestCounter;
        this.responseCounter = responseCounter;
        this.processingDurationTiming = processingDurationTiming;
    }

    public void start(ClientCall.Listener<A> responseListener, Metadata metadata) {
        super.start(new MetricCollectingClientCallListener<A>(responseListener, this.responseCounter, this.processingDurationTiming), metadata);
    }

    public void sendMessage(Q requestMessage) {
        this.requestCounter.increment();
        super.sendMessage(requestMessage);
    }
}

