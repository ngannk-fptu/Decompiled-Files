/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.archive;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.terracotta.statistics.Time;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.archive.SampleSink;
import org.terracotta.statistics.archive.Timestamped;

public class StatisticSampler<T extends Number> {
    private final boolean exclusiveExecutor;
    private final ScheduledExecutorService executor;
    private final Runnable task;
    private ScheduledFuture<?> currentExecution;
    private long period;

    public StatisticSampler(long time, TimeUnit unit, ValueStatistic<T> statistic, SampleSink<? super Timestamped<T>> sink) {
        this(null, time, unit, statistic, sink);
    }

    public StatisticSampler(ScheduledExecutorService executor, long time, TimeUnit unit, ValueStatistic<T> statistic, SampleSink<? super Timestamped<T>> sink) {
        if (executor == null) {
            this.exclusiveExecutor = true;
            this.executor = Executors.newSingleThreadScheduledExecutor(new SamplerThreadFactory());
        } else {
            this.exclusiveExecutor = false;
            this.executor = executor;
        }
        this.period = unit.toNanos(time);
        this.task = new SamplingTask<T>(statistic, sink);
    }

    public synchronized void setPeriod(long time, TimeUnit unit) {
        this.period = unit.toNanos(time);
        if (this.currentExecution != null && !this.currentExecution.isDone()) {
            this.stop();
            this.start();
        }
    }

    public synchronized void start() {
        if (this.currentExecution != null && !this.currentExecution.isDone()) {
            throw new IllegalStateException("Sampler is already running");
        }
        this.currentExecution = this.executor.scheduleAtFixedRate(this.task, this.period, this.period, TimeUnit.NANOSECONDS);
    }

    public synchronized void stop() {
        if (this.currentExecution == null || this.currentExecution.isDone()) {
            throw new IllegalStateException("Sampler is not running");
        }
        this.currentExecution.cancel(false);
    }

    public synchronized void shutdown() throws InterruptedException {
        if (this.exclusiveExecutor) {
            this.executor.shutdown();
            if (!this.executor.awaitTermination(10L, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Exclusive ScheduledExecutorService failed to terminate promptly");
            }
        } else {
            throw new IllegalStateException("ScheduledExecutorService was supplied externally - it must be shutdown directly");
        }
    }

    static class SamplerThreadFactory
    implements ThreadFactory {
        SamplerThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Statistic Sampler");
            t.setDaemon(true);
            return t;
        }
    }

    static class Sample<T>
    implements Timestamped<T> {
        private final T sample;
        private final long timestamp;

        public Sample(long timestamp, T sample) {
            this.sample = sample;
            this.timestamp = timestamp;
        }

        @Override
        public T getSample() {
            return this.sample;
        }

        @Override
        public long getTimestamp() {
            return this.timestamp;
        }

        public String toString() {
            return this.getSample() + " @ " + new Date(this.getTimestamp());
        }
    }

    static class SamplingTask<T extends Number>
    implements Runnable {
        private final ValueStatistic<T> statistic;
        private final SampleSink<Timestamped<T>> sink;

        SamplingTask(ValueStatistic<T> statistic, SampleSink<Timestamped<T>> sink) {
            this.statistic = statistic;
            this.sink = sink;
        }

        @Override
        public void run() {
            this.sink.accept(new Sample<T>(Time.absoluteTime(), this.statistic.value()));
        }
    }
}

