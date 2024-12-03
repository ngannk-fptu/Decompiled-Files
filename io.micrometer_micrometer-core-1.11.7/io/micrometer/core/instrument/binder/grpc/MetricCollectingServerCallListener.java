/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.ForwardingServerCallListener$SimpleForwardingServerCallListener
 *  io.grpc.ServerCall$Listener
 *  io.grpc.Status$Code
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import java.util.function.Consumer;
import java.util.function.Supplier;

class MetricCollectingServerCallListener<Q>
extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Q> {
    private final Counter requestCounter;
    private final Supplier<Status.Code> responseCodeSupplier;
    private final Consumer<Status.Code> responseStatusTiming;

    public MetricCollectingServerCallListener(ServerCall.Listener<Q> delegate, Counter requestCounter, Supplier<Status.Code> responseCodeSupplier, Consumer<Status.Code> responseStatusTiming) {
        super(delegate);
        this.requestCounter = requestCounter;
        this.responseCodeSupplier = responseCodeSupplier;
        this.responseStatusTiming = responseStatusTiming;
    }

    public void onMessage(Q requestMessage) {
        this.requestCounter.increment();
        super.onMessage(requestMessage);
    }

    public void onComplete() {
        this.report(this.responseCodeSupplier.get());
        super.onComplete();
    }

    public void onCancel() {
        this.report(Status.Code.CANCELLED);
        super.onCancel();
    }

    private void report(Status.Code code) {
        this.responseStatusTiming.accept(code);
    }
}

