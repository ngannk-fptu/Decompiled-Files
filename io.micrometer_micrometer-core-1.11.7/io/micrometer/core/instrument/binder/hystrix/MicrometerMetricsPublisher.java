/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.netflix.hystrix.HystrixCircuitBreaker
 *  com.netflix.hystrix.HystrixCollapserKey
 *  com.netflix.hystrix.HystrixCollapserMetrics
 *  com.netflix.hystrix.HystrixCollapserProperties
 *  com.netflix.hystrix.HystrixCommandGroupKey
 *  com.netflix.hystrix.HystrixCommandKey
 *  com.netflix.hystrix.HystrixCommandMetrics
 *  com.netflix.hystrix.HystrixCommandProperties
 *  com.netflix.hystrix.HystrixThreadPoolKey
 *  com.netflix.hystrix.HystrixThreadPoolMetrics
 *  com.netflix.hystrix.HystrixThreadPoolProperties
 *  com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher
 *  com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCollapser
 *  com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand
 *  com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.hystrix;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserMetrics;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCollapser;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.hystrix.MicrometerMetricsPublisherCommand;
import io.micrometer.core.instrument.binder.hystrix.MicrometerMetricsPublisherThreadPool;

@NonNullApi
@NonNullFields
public class MicrometerMetricsPublisher
extends HystrixMetricsPublisher {
    private final MeterRegistry registry;
    private HystrixMetricsPublisher metricsPublisher;

    public MicrometerMetricsPublisher(MeterRegistry registry, HystrixMetricsPublisher metricsPublisher) {
        this.registry = registry;
        this.metricsPublisher = metricsPublisher;
    }

    public HystrixMetricsPublisherThreadPool getMetricsPublisherForThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolMetrics metrics, HystrixThreadPoolProperties properties) {
        HystrixMetricsPublisherThreadPool metricsPublisherForThreadPool = this.metricsPublisher.getMetricsPublisherForThreadPool(threadPoolKey, metrics, properties);
        return new MicrometerMetricsPublisherThreadPool(this.registry, threadPoolKey, metrics, properties, metricsPublisherForThreadPool);
    }

    public HystrixMetricsPublisherCollapser getMetricsPublisherForCollapser(HystrixCollapserKey collapserKey, HystrixCollapserMetrics metrics, HystrixCollapserProperties properties) {
        return this.metricsPublisher.getMetricsPublisherForCollapser(collapserKey, metrics, properties);
    }

    public HystrixMetricsPublisherCommand getMetricsPublisherForCommand(HystrixCommandKey commandKey, HystrixCommandGroupKey commandGroupKey, HystrixCommandMetrics metrics, HystrixCircuitBreaker circuitBreaker, HystrixCommandProperties properties) {
        HystrixMetricsPublisherCommand metricsPublisherForCommand = this.metricsPublisher.getMetricsPublisherForCommand(commandKey, commandGroupKey, metrics, circuitBreaker, properties);
        return new MicrometerMetricsPublisherCommand(this.registry, commandKey, commandGroupKey, metrics, circuitBreaker, metricsPublisherForCommand);
    }
}

