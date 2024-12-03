/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.binder.jvm;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.internal.TimedExecutor;
import io.micrometer.core.instrument.internal.TimedExecutorService;
import io.micrometer.core.instrument.internal.TimedScheduledExecutorService;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@NonNullApi
@NonNullFields
public class ExecutorServiceMetrics
implements MeterBinder {
    private static boolean allowIllegalReflectiveAccess = true;
    private static final InternalLogger log = InternalLoggerFactory.getInstance(ExecutorServiceMetrics.class);
    private static final String DEFAULT_EXECUTOR_METRIC_PREFIX = "";
    @Nullable
    private final ExecutorService executorService;
    private final Iterable<Tag> tags;
    private final String metricPrefix;

    public ExecutorServiceMetrics(@Nullable ExecutorService executorService, String executorServiceName, Iterable<Tag> tags) {
        this(executorService, executorServiceName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public ExecutorServiceMetrics(@Nullable ExecutorService executorService, String executorServiceName, String metricPrefix, Iterable<Tag> tags) {
        this.executorService = executorService;
        this.tags = Tags.concat(tags, "name", executorServiceName);
        this.metricPrefix = ExecutorServiceMetrics.sanitizePrefix(metricPrefix);
    }

    public static Executor monitor(MeterRegistry registry, Executor executor, String executorName, Iterable<Tag> tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public static Executor monitor(MeterRegistry registry, Executor executor, String executorName, String metricPrefix, Iterable<Tag> tags) {
        if (executor instanceof ExecutorService) {
            return ExecutorServiceMetrics.monitor(registry, (ExecutorService)executor, executorName, metricPrefix, tags);
        }
        return new TimedExecutor(registry, executor, executorName, ExecutorServiceMetrics.sanitizePrefix(metricPrefix), tags);
    }

    public static Executor monitor(MeterRegistry registry, Executor executor, String executorName, Tag ... tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public static Executor monitor(MeterRegistry registry, Executor executor, String executorName, String metricPrefix, Tag ... tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorName, metricPrefix, Arrays.asList(tags));
    }

    public static ExecutorService monitor(MeterRegistry registry, ExecutorService executor, String executorServiceName, Iterable<Tag> tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorServiceName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public static ExecutorService monitor(MeterRegistry registry, ExecutorService executor, String executorServiceName, String metricPrefix, Iterable<Tag> tags) {
        if (executor instanceof ScheduledExecutorService) {
            return ExecutorServiceMetrics.monitor(registry, (ScheduledExecutorService)executor, executorServiceName, metricPrefix, tags);
        }
        new ExecutorServiceMetrics(executor, executorServiceName, metricPrefix, tags).bindTo(registry);
        return new TimedExecutorService(registry, executor, executorServiceName, ExecutorServiceMetrics.sanitizePrefix(metricPrefix), tags);
    }

    public static ExecutorService monitor(MeterRegistry registry, ExecutorService executor, String executorServiceName, Tag ... tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorServiceName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public static ExecutorService monitor(MeterRegistry registry, ExecutorService executor, String executorServiceName, String metricPrefix, Tag ... tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorServiceName, metricPrefix, Arrays.asList(tags));
    }

    public static ScheduledExecutorService monitor(MeterRegistry registry, ScheduledExecutorService executor, String executorServiceName, Iterable<Tag> tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorServiceName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public static ScheduledExecutorService monitor(MeterRegistry registry, ScheduledExecutorService executor, String executorServiceName, String metricPrefix, Iterable<Tag> tags) {
        new ExecutorServiceMetrics(executor, executorServiceName, metricPrefix, tags).bindTo(registry);
        return new TimedScheduledExecutorService(registry, executor, executorServiceName, ExecutorServiceMetrics.sanitizePrefix(metricPrefix), tags);
    }

    public static ScheduledExecutorService monitor(MeterRegistry registry, ScheduledExecutorService executor, String executorServiceName, Tag ... tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorServiceName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public static ScheduledExecutorService monitor(MeterRegistry registry, ScheduledExecutorService executor, String executorServiceName, String metricPrefix, Tag ... tags) {
        return ExecutorServiceMetrics.monitor(registry, executor, executorServiceName, metricPrefix, Arrays.asList(tags));
    }

    private static String sanitizePrefix(String metricPrefix) {
        if (StringUtils.isBlank((String)metricPrefix)) {
            return DEFAULT_EXECUTOR_METRIC_PREFIX;
        }
        if (!metricPrefix.endsWith(".")) {
            return metricPrefix + ".";
        }
        return metricPrefix;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        if (this.executorService == null) {
            return;
        }
        String className = this.executorService.getClass().getName();
        if (this.executorService instanceof ThreadPoolExecutor) {
            this.monitor(registry, (ThreadPoolExecutor)this.executorService);
        } else if (this.executorService instanceof ForkJoinPool) {
            this.monitor(registry, (ForkJoinPool)this.executorService);
        } else if (allowIllegalReflectiveAccess) {
            if (className.equals("java.util.concurrent.Executors$DelegatedScheduledExecutorService")) {
                this.monitor(registry, this.unwrapThreadPoolExecutor(this.executorService, this.executorService.getClass()));
            } else if (className.equals("java.util.concurrent.Executors$FinalizableDelegatedExecutorService")) {
                this.monitor(registry, this.unwrapThreadPoolExecutor(this.executorService, this.executorService.getClass().getSuperclass()));
            } else {
                log.warn("Failed to bind as {} is unsupported.", (Object)className);
            }
        } else {
            log.warn("Failed to bind as {} is unsupported or reflective access is not allowed.", (Object)className);
        }
    }

    @Nullable
    private ThreadPoolExecutor unwrapThreadPoolExecutor(ExecutorService executor, Class<?> wrapper) {
        try {
            Field e = wrapper.getDeclaredField("e");
            e.setAccessible(true);
            return (ThreadPoolExecutor)e.get(executor);
        }
        catch (IllegalAccessException | NoSuchFieldException | RuntimeException e) {
            log.info("Cannot unwrap ThreadPoolExecutor for monitoring from {} due to {}: {}", new Object[]{wrapper.getName(), e.getClass().getName(), e.getMessage()});
            return null;
        }
    }

    private void monitor(MeterRegistry registry, @Nullable ThreadPoolExecutor tp) {
        if (tp == null) {
            return;
        }
        FunctionCounter.builder(this.metricPrefix + "executor.completed", tp, ThreadPoolExecutor::getCompletedTaskCount).tags(this.tags).description("The approximate total number of tasks that have completed execution").baseUnit("tasks").register(registry);
        Gauge.builder(this.metricPrefix + "executor.active", tp, ThreadPoolExecutor::getActiveCount).tags(this.tags).description("The approximate number of threads that are actively executing tasks").baseUnit("threads").register(registry);
        Gauge.builder(this.metricPrefix + "executor.queued", tp, tpRef -> tpRef.getQueue().size()).tags(this.tags).description("The approximate number of tasks that are queued for execution").baseUnit("tasks").register(registry);
        Gauge.builder(this.metricPrefix + "executor.queue.remaining", tp, tpRef -> tpRef.getQueue().remainingCapacity()).tags(this.tags).description("The number of additional elements that this queue can ideally accept without blocking").baseUnit("tasks").register(registry);
        Gauge.builder(this.metricPrefix + "executor.pool.size", tp, ThreadPoolExecutor::getPoolSize).tags(this.tags).description("The current number of threads in the pool").baseUnit("threads").register(registry);
        Gauge.builder(this.metricPrefix + "executor.pool.core", tp, ThreadPoolExecutor::getCorePoolSize).tags(this.tags).description("The core number of threads for the pool").baseUnit("threads").register(registry);
        Gauge.builder(this.metricPrefix + "executor.pool.max", tp, ThreadPoolExecutor::getMaximumPoolSize).tags(this.tags).description("The maximum allowed number of threads in the pool").baseUnit("threads").register(registry);
    }

    private void monitor(MeterRegistry registry, ForkJoinPool fj) {
        FunctionCounter.builder(this.metricPrefix + "executor.steals", fj, ForkJoinPool::getStealCount).tags(this.tags).description("Estimate of the total number of tasks stolen from one thread's work queue by another. The reported value underestimates the actual total number of steals when the pool is not quiescent").register(registry);
        Gauge.builder(this.metricPrefix + "executor.queued", fj, ForkJoinPool::getQueuedTaskCount).tags(this.tags).description("An estimate of the total number of tasks currently held in queues by worker threads").register(registry);
        Gauge.builder(this.metricPrefix + "executor.active", fj, ForkJoinPool::getActiveThreadCount).tags(this.tags).description("An estimate of the number of threads that are currently stealing or executing tasks").register(registry);
        Gauge.builder(this.metricPrefix + "executor.running", fj, ForkJoinPool::getRunningThreadCount).tags(this.tags).description("An estimate of the number of worker threads that are not blocked waiting to join tasks or for other managed synchronization threads").register(registry);
    }

    public static void disableIllegalReflectiveAccess() {
        allowIllegalReflectiveAccess = false;
    }
}

