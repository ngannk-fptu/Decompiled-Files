/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.MethodDescriptor
 *  io.grpc.ServiceDescriptor
 *  io.grpc.Status$Code
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.MethodDescriptor;
import io.grpc.ServiceDescriptor;
import io.grpc.Status;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class AbstractMetricCollectingInterceptor {
    private static final String TAG_SERVICE_NAME = "service";
    private static final String TAG_METHOD_NAME = "method";
    private static final String TAG_METHOD_TYPE = "methodType";
    private static final String TAG_STATUS_CODE = "statusCode";
    private final Map<MethodDescriptor<?, ?>, MetricSet> metricsForMethods = new ConcurrentHashMap();
    protected final MeterRegistry registry;
    protected final UnaryOperator<Counter.Builder> counterCustomizer;
    protected final UnaryOperator<Timer.Builder> timerCustomizer;
    protected final Status.Code[] eagerInitializedCodes;

    protected static Counter.Builder prepareCounterFor(MethodDescriptor<?, ?> method, String name, String description) {
        return Counter.builder(name).description(description).baseUnit("messages").tag(TAG_SERVICE_NAME, method.getServiceName()).tag(TAG_METHOD_NAME, method.getBareMethodName()).tag(TAG_METHOD_TYPE, method.getType().name());
    }

    protected static Timer.Builder prepareTimerFor(MethodDescriptor<?, ?> method, String name, String description) {
        return Timer.builder(name).description(description).tag(TAG_SERVICE_NAME, method.getServiceName()).tag(TAG_METHOD_NAME, method.getBareMethodName()).tag(TAG_METHOD_TYPE, method.getType().name());
    }

    protected AbstractMetricCollectingInterceptor(MeterRegistry registry) {
        this(registry, UnaryOperator.identity(), UnaryOperator.identity(), Status.Code.OK);
    }

    protected AbstractMetricCollectingInterceptor(MeterRegistry registry, UnaryOperator<Counter.Builder> counterCustomizer, UnaryOperator<Timer.Builder> timerCustomizer, Status.Code ... eagerInitializedCodes) {
        this.registry = registry;
        this.counterCustomizer = counterCustomizer;
        this.timerCustomizer = timerCustomizer;
        this.eagerInitializedCodes = eagerInitializedCodes;
    }

    public void preregisterService(ServiceDescriptor service) {
        for (MethodDescriptor method : service.getMethods()) {
            this.preregisterMethod(method);
        }
    }

    public void preregisterMethod(MethodDescriptor<?, ?> method) {
        this.metricsFor(method);
    }

    protected final MetricSet metricsFor(MethodDescriptor<?, ?> method) {
        return this.metricsForMethods.computeIfAbsent(method, this::newMetricsFor);
    }

    protected MetricSet newMetricsFor(MethodDescriptor<?, ?> method) {
        return new MetricSet(this.newRequestCounterFor(method), this.newResponseCounterFor(method), this.newTimerFunction(method));
    }

    protected abstract Counter newRequestCounterFor(MethodDescriptor<?, ?> var1);

    protected abstract Counter newResponseCounterFor(MethodDescriptor<?, ?> var1);

    protected Function<Status.Code, Timer> asTimerFunction(Supplier<Timer.Builder> timerTemplate) {
        EnumMap cache = new EnumMap(Status.Code.class);
        Function<Status.Code, Timer> creator = code -> ((Timer.Builder)timerTemplate.get()).tag(TAG_STATUS_CODE, code.name()).register(this.registry);
        Function<Status.Code, Timer> cacheResolver = code -> (Timer)cache.computeIfAbsent((Status.Code)code, creator);
        for (Status.Code code2 : this.eagerInitializedCodes) {
            cacheResolver.apply(code2);
        }
        return cacheResolver;
    }

    protected abstract Function<Status.Code, Timer> newTimerFunction(MethodDescriptor<?, ?> var1);

    protected static class MetricSet {
        private final Counter requestCounter;
        private final Counter responseCounter;
        private final Function<Status.Code, Timer> timerFunction;

        public MetricSet(Counter requestCounter, Counter responseCounter, Function<Status.Code, Timer> timerFunction) {
            this.requestCounter = requestCounter;
            this.responseCounter = responseCounter;
            this.timerFunction = timerFunction;
        }

        public Counter getRequestCounter() {
            return this.requestCounter;
        }

        public Counter getResponseCounter() {
            return this.responseCounter;
        }

        public Consumer<Status.Code> newProcessingDurationTiming(MeterRegistry registry) {
            Timer.Sample timerSample = Timer.start(registry);
            return code -> timerSample.stop(this.timerFunction.apply((Status.Code)code));
        }
    }
}

