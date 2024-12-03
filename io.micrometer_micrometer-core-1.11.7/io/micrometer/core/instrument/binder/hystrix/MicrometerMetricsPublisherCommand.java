/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.netflix.hystrix.HystrixCircuitBreaker
 *  com.netflix.hystrix.HystrixCommandGroupKey
 *  com.netflix.hystrix.HystrixCommandKey
 *  com.netflix.hystrix.HystrixCommandMetrics
 *  com.netflix.hystrix.HystrixEventType
 *  com.netflix.hystrix.metric.HystrixCommandCompletionStream
 *  com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.binder.hystrix;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.metric.HystrixCommandCompletionStream;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@NonNullApi
@NonNullFields
public class MicrometerMetricsPublisherCommand
implements HystrixMetricsPublisherCommand {
    private static final InternalLogger LOG = InternalLoggerFactory.getInstance(MicrometerMetricsPublisherCommand.class);
    private static final String NAME_HYSTRIX_CIRCUIT_BREAKER_OPEN = "hystrix.circuit.breaker.open";
    private static final String NAME_HYSTRIX_EXECUTION = "hystrix.execution";
    private static final String NAME_HYSTRIX_EXECUTION_TERMINAL_TOTAL = "hystrix.execution.terminal";
    private static final String NAME_HYSTRIX_LATENCY_EXECUTION = "hystrix.latency.execution";
    private static final String NAME_HYSTRIX_LATENCY_TOTAL = "hystrix.latency.total";
    private static final String NAME_HYSTRIX_CONCURRENT_EXECUTION_CURRENT = "hystrix.concurrent.execution.current";
    private static final String NAME_HYSTRIX_CONCURRENT_EXECUTION_ROLLING_MAX = "hystrix.concurrent.execution.rolling.max";
    private static final String DESCRIPTION_HYSTRIX_EXECUTION = "Execution results. See https://github.com/Netflix/Hystrix/wiki/Metrics-and-Monitoring#command-execution-event-types-comnetflixhystrixhystrixeventtype for type definitions";
    private static final String DESCRIPTION_HYSTRIX_EXECUTION_TERMINAL_TOTAL = "Sum of all terminal executions. Use this to derive percentages from hystrix.execution";
    private final MeterRegistry meterRegistry;
    private final HystrixCommandMetrics metrics;
    private final HystrixCircuitBreaker circuitBreaker;
    private final Iterable<Tag> tags;
    private final HystrixCommandKey commandKey;
    private HystrixMetricsPublisherCommand metricsPublisherForCommand;

    public MicrometerMetricsPublisherCommand(MeterRegistry meterRegistry, HystrixCommandKey commandKey, HystrixCommandGroupKey commandGroupKey, HystrixCommandMetrics metrics, HystrixCircuitBreaker circuitBreaker, HystrixMetricsPublisherCommand metricsPublisherForCommand) {
        this.meterRegistry = meterRegistry;
        this.metrics = metrics;
        this.circuitBreaker = circuitBreaker;
        this.commandKey = commandKey;
        this.metricsPublisherForCommand = metricsPublisherForCommand;
        this.tags = Tags.of("group", commandGroupKey.name(), "key", commandKey.name());
    }

    public void initialize() {
        this.metricsPublisherForCommand.initialize();
        Gauge.builder(NAME_HYSTRIX_CIRCUIT_BREAKER_OPEN, this.circuitBreaker, c -> c.isOpen() ? 1.0 : 0.0).tags(this.tags).register(this.meterRegistry);
        HashMap eventCounters = new HashMap();
        Arrays.asList(HystrixEventType.values()).forEach(hystrixEventType -> eventCounters.put(hystrixEventType, this.getCounter((HystrixEventType)hystrixEventType)));
        Counter terminalEventCounterTotal = Counter.builder(NAME_HYSTRIX_EXECUTION_TERMINAL_TOTAL).description(DESCRIPTION_HYSTRIX_EXECUTION_TERMINAL_TOTAL).tags(Tags.concat(this.tags, new String[0])).register(this.meterRegistry);
        Timer latencyExecution = ((Timer.Builder)Timer.builder(NAME_HYSTRIX_LATENCY_EXECUTION).tags((Iterable)this.tags)).register(this.meterRegistry);
        Timer latencyTotal = ((Timer.Builder)Timer.builder(NAME_HYSTRIX_LATENCY_TOTAL).tags((Iterable)this.tags)).register(this.meterRegistry);
        HystrixCommandCompletionStream.getInstance((HystrixCommandKey)this.commandKey).observe().subscribe(hystrixCommandCompletion -> {
            long totalLatency = hystrixCommandCompletion.getTotalLatency();
            if (totalLatency >= 0L) {
                latencyTotal.record(totalLatency, TimeUnit.MILLISECONDS);
            } else if (totalLatency < -1L) {
                LOG.warn("received negative totalLatency, event not counted. This indicates a clock skew? {}", hystrixCommandCompletion);
            }
            long executionLatency = hystrixCommandCompletion.getExecutionLatency();
            if (executionLatency >= 0L) {
                latencyExecution.record(executionLatency, TimeUnit.MILLISECONDS);
            } else if (executionLatency < -1L) {
                LOG.warn("received negative executionLatency, event not counted. This indicates a clock skew? {}", hystrixCommandCompletion);
            }
            for (HystrixEventType hystrixEventType : HystrixEventType.values()) {
                int count = hystrixCommandCompletion.getEventCounts().getCount(hystrixEventType);
                if (count <= 0) continue;
                ((Counter)eventCounters.get(hystrixEventType)).increment(count);
                if (!hystrixEventType.isTerminal()) continue;
                terminalEventCounterTotal.increment(count);
            }
        });
        Gauge.builder(NAME_HYSTRIX_CONCURRENT_EXECUTION_CURRENT, this.metrics, HystrixCommandMetrics::getCurrentConcurrentExecutionCount).tags(this.tags).register(this.meterRegistry);
        Gauge.builder(NAME_HYSTRIX_CONCURRENT_EXECUTION_ROLLING_MAX, this.metrics, HystrixCommandMetrics::getRollingMaxConcurrentExecutions).tags(this.tags).register(this.meterRegistry);
    }

    private Counter getCounter(HystrixEventType hystrixEventType) {
        return Counter.builder(NAME_HYSTRIX_EXECUTION).description(DESCRIPTION_HYSTRIX_EXECUTION).tags(Tags.concat(this.tags, "event", hystrixEventType.name().toLowerCase(), "terminal", Boolean.toString(hystrixEventType.isTerminal()))).register(this.meterRegistry);
    }
}

