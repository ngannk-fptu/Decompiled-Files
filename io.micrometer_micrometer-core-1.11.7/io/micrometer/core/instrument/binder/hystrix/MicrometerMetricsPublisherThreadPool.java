/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.netflix.hystrix.HystrixThreadPoolKey
 *  com.netflix.hystrix.HystrixThreadPoolMetrics
 *  com.netflix.hystrix.HystrixThreadPoolProperties
 *  com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.hystrix;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@NonNullApi
@NonNullFields
public class MicrometerMetricsPublisherThreadPool
implements HystrixMetricsPublisherThreadPool {
    private static final String NAME_HYSTRIX_THREADPOOL = "hystrix.threadpool";
    private final MeterRegistry meterRegistry;
    private final HystrixThreadPoolMetrics metrics;
    private final HystrixThreadPoolProperties properties;
    private final HystrixMetricsPublisherThreadPool metricsPublisherForThreadPool;
    private final Tags tags;

    public MicrometerMetricsPublisherThreadPool(MeterRegistry meterRegistry, HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolMetrics metrics, HystrixThreadPoolProperties properties, HystrixMetricsPublisherThreadPool metricsPublisherForThreadPool) {
        this.meterRegistry = meterRegistry;
        this.metrics = metrics;
        this.properties = properties;
        this.metricsPublisherForThreadPool = metricsPublisherForThreadPool;
        this.tags = Tags.of("key", threadPoolKey.name());
    }

    public void initialize() {
        this.metricsPublisherForThreadPool.initialize();
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("threads.active.current.count"), () -> ((HystrixThreadPoolMetrics)this.metrics).getCurrentActiveCount()).description("The approximate number of threads that are actively executing tasks.").tags(this.tags).register(this.meterRegistry);
        FunctionCounter.builder(MicrometerMetricsPublisherThreadPool.metricName("threads.cumulative.count"), this.metrics, HystrixThreadPoolMetrics::getCumulativeCountThreadsExecuted).description("Cumulative count of number of threads since the start of the application.").tags(this.tags.and(Tag.of("type", "executed"))).register(this.meterRegistry);
        FunctionCounter.builder(MicrometerMetricsPublisherThreadPool.metricName("threads.cumulative.count"), this.metrics, HystrixThreadPoolMetrics::getCumulativeCountThreadsRejected).description("Cumulative count of number of threads since the start of the application.").tags(this.tags.and(Tag.of("type", "rejected"))).register(this.meterRegistry);
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("threads.pool.current.size"), () -> ((HystrixThreadPoolMetrics)this.metrics).getCurrentPoolSize()).description("The current number of threads in the pool.").tags(this.tags).register(this.meterRegistry);
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("threads.largest.pool.current.size"), () -> ((HystrixThreadPoolMetrics)this.metrics).getCurrentLargestPoolSize()).description("The largest number of threads that have ever simultaneously been in the pool.").tags(this.tags).register(this.meterRegistry);
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("threads.max.pool.current.size"), () -> ((HystrixThreadPoolMetrics)this.metrics).getCurrentMaximumPoolSize()).description("The maximum allowed number of threads.").tags(this.tags).register(this.meterRegistry);
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("threads.core.pool.current.size"), () -> ((HystrixThreadPoolMetrics)this.metrics).getCurrentCorePoolSize()).description("The core number of threads.").tags(this.tags).register(this.meterRegistry);
        FunctionCounter.builder(MicrometerMetricsPublisherThreadPool.metricName("tasks.cumulative.count"), this.metrics, m -> m.getCurrentCompletedTaskCount().longValue()).description("The approximate total number of tasks since the start of the application.").tags(this.tags.and(Tag.of("type", "completed"))).register(this.meterRegistry);
        FunctionCounter.builder(MicrometerMetricsPublisherThreadPool.metricName("tasks.cumulative.count"), this.metrics, m -> m.getCurrentTaskCount().longValue()).description("The approximate total number of tasks since the start of the application.").tags(this.tags.and(Tag.of("type", "scheduled"))).register(this.meterRegistry);
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("queue.current.size"), () -> ((HystrixThreadPoolMetrics)this.metrics).getCurrentQueueSize()).description("Current size of BlockingQueue used by the thread-pool.").tags(this.tags).register(this.meterRegistry);
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("queue.max.size"), () -> (Number)this.properties.maxQueueSize().get()).description("Max size of BlockingQueue used by the thread-pool.").tags(this.tags).register(this.meterRegistry);
        Gauge.builder(MicrometerMetricsPublisherThreadPool.metricName("queue.rejection.threshold.size"), () -> (Number)this.properties.queueSizeRejectionThreshold().get()).description("Artificial max size at which rejections will occur even if maxQueueSize has not been reached.").tags(this.tags).register(this.meterRegistry);
    }

    private static String metricName(String name) {
        return String.join((CharSequence)".", NAME_HYSTRIX_THREADPOOL, name);
    }
}

