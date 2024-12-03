/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.CallOptions
 *  io.grpc.Channel
 *  io.grpc.ClientCall
 *  io.grpc.ClientInterceptor
 *  io.grpc.MethodDescriptor
 *  io.grpc.Status$Code
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.grpc.AbstractMetricCollectingInterceptor;
import io.micrometer.core.instrument.binder.grpc.MetricCollectingClientCall;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class MetricCollectingClientInterceptor
extends AbstractMetricCollectingInterceptor
implements ClientInterceptor {
    private static final String METRIC_NAME_CLIENT_REQUESTS_SENT = "grpc.client.requests.sent";
    private static final String METRIC_NAME_CLIENT_RESPONSES_RECEIVED = "grpc.client.responses.received";
    private static final String METRIC_NAME_CLIENT_PROCESSING_DURATION = "grpc.client.processing.duration";

    public MetricCollectingClientInterceptor(MeterRegistry registry) {
        super(registry);
    }

    public MetricCollectingClientInterceptor(MeterRegistry registry, UnaryOperator<Counter.Builder> counterCustomizer, UnaryOperator<Timer.Builder> timerCustomizer, Status.Code ... eagerInitializedCodes) {
        super(registry, counterCustomizer, timerCustomizer, eagerInitializedCodes);
    }

    @Override
    protected Counter newRequestCounterFor(MethodDescriptor<?, ?> method) {
        return ((Counter.Builder)this.counterCustomizer.apply(MetricCollectingClientInterceptor.prepareCounterFor(method, METRIC_NAME_CLIENT_REQUESTS_SENT, "The total number of requests sent"))).register(this.registry);
    }

    @Override
    protected Counter newResponseCounterFor(MethodDescriptor<?, ?> method) {
        return ((Counter.Builder)this.counterCustomizer.apply(MetricCollectingClientInterceptor.prepareCounterFor(method, METRIC_NAME_CLIENT_RESPONSES_RECEIVED, "The total number of responses received"))).register(this.registry);
    }

    @Override
    protected Function<Status.Code, Timer> newTimerFunction(MethodDescriptor<?, ?> method) {
        return this.asTimerFunction(() -> (Timer.Builder)this.timerCustomizer.apply(MetricCollectingClientInterceptor.prepareTimerFor(method, METRIC_NAME_CLIENT_PROCESSING_DURATION, "The total time taken for the client to complete the call, including network delay")));
    }

    public <Q, A> ClientCall<Q, A> interceptCall(MethodDescriptor<Q, A> methodDescriptor, CallOptions callOptions, Channel channel) {
        AbstractMetricCollectingInterceptor.MetricSet metrics = this.metricsFor(methodDescriptor);
        Consumer<Status.Code> processingDurationTiming = metrics.newProcessingDurationTiming(this.registry);
        return new MetricCollectingClientCall(channel.newCall(methodDescriptor, callOptions), metrics.getRequestCounter(), metrics.getResponseCounter(), processingDurationTiming);
    }
}

