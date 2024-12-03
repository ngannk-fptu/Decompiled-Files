/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.BindableService
 *  io.grpc.Metadata
 *  io.grpc.MethodDescriptor
 *  io.grpc.ServerCall
 *  io.grpc.ServerCall$Listener
 *  io.grpc.ServerCallHandler
 *  io.grpc.ServerInterceptor
 *  io.grpc.ServerServiceDefinition
 *  io.grpc.Status$Code
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.BindableService;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.grpc.AbstractMetricCollectingInterceptor;
import io.micrometer.core.instrument.binder.grpc.MetricCollectingServerCall;
import io.micrometer.core.instrument.binder.grpc.MetricCollectingServerCallListener;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class MetricCollectingServerInterceptor
extends AbstractMetricCollectingInterceptor
implements ServerInterceptor {
    private static final String METRIC_NAME_SERVER_REQUESTS_RECEIVED = "grpc.server.requests.received";
    private static final String METRIC_NAME_SERVER_RESPONSES_SENT = "grpc.server.responses.sent";
    private static final String METRIC_NAME_SERVER_PROCESSING_DURATION = "grpc.server.processing.duration";

    public MetricCollectingServerInterceptor(MeterRegistry registry) {
        super(registry);
    }

    public MetricCollectingServerInterceptor(MeterRegistry registry, UnaryOperator<Counter.Builder> counterCustomizer, UnaryOperator<Timer.Builder> timerCustomizer, Status.Code ... eagerInitializedCodes) {
        super(registry, counterCustomizer, timerCustomizer, eagerInitializedCodes);
    }

    public void preregisterService(BindableService service) {
        this.preregisterService(service.bindService());
    }

    public void preregisterService(ServerServiceDefinition serviceDefinition) {
        this.preregisterService(serviceDefinition.getServiceDescriptor());
    }

    @Override
    protected Counter newRequestCounterFor(MethodDescriptor<?, ?> method) {
        return ((Counter.Builder)this.counterCustomizer.apply(MetricCollectingServerInterceptor.prepareCounterFor(method, METRIC_NAME_SERVER_REQUESTS_RECEIVED, "The total number of requests received"))).register(this.registry);
    }

    @Override
    protected Counter newResponseCounterFor(MethodDescriptor<?, ?> method) {
        return ((Counter.Builder)this.counterCustomizer.apply(MetricCollectingServerInterceptor.prepareCounterFor(method, METRIC_NAME_SERVER_RESPONSES_SENT, "The total number of responses sent"))).register(this.registry);
    }

    @Override
    protected Function<Status.Code, Timer> newTimerFunction(MethodDescriptor<?, ?> method) {
        return this.asTimerFunction(() -> (Timer.Builder)this.timerCustomizer.apply(MetricCollectingServerInterceptor.prepareTimerFor(method, METRIC_NAME_SERVER_PROCESSING_DURATION, "The total time taken for the server to complete the call")));
    }

    public <Q, A> ServerCall.Listener<Q> interceptCall(ServerCall<Q, A> call, Metadata requestHeaders, ServerCallHandler<Q, A> next) {
        AbstractMetricCollectingInterceptor.MetricSet metrics = this.metricsFor(call.getMethodDescriptor());
        Consumer<Status.Code> responseStatusTiming = metrics.newProcessingDurationTiming(this.registry);
        MetricCollectingServerCall<Q, A> monitoringCall = new MetricCollectingServerCall<Q, A>(call, metrics.getResponseCounter());
        return new MetricCollectingServerCallListener(next.startCall(monitoringCall, requestHeaders), metrics.getRequestCounter(), monitoringCall::getResponseCode, responseStatusTiming);
    }
}

