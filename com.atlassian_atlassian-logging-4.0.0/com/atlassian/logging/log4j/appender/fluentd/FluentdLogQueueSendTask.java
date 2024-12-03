/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.rholder.retry.Attempt
 *  com.github.rholder.retry.RetryException
 *  com.github.rholder.retry.Retryer
 *  com.github.rholder.retry.RetryerBuilder
 *  com.github.rholder.retry.StopStrategies
 *  com.github.rholder.retry.StopStrategy
 *  com.github.rholder.retry.WaitStrategies
 *  com.github.rholder.retry.WaitStrategy
 */
package com.atlassian.logging.log4j.appender.fluentd;

import com.atlassian.logging.log4j.appender.fluentd.FluentdRetryableException;
import com.atlassian.logging.log4j.appender.fluentd.FluentdSender;
import com.atlassian.logging.log4j.appender.fluentd.LoggingEventQueue;
import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FluentdLogQueueSendTask<T>
extends TimerTask {
    private static final int FLUENTD_BATCH_SIZE = 50;
    private final Function<T, Serializable> layout;
    private final LoggingEventQueue<T> loggingEventQueue;
    private final int maxRetryPeriodMs;
    private final int backoffMultiplier;
    private final int maxBackoffMinutes;
    private final Retryer<Void> sendoffRetryer;
    private final FluentdSender fluentdSender;

    public FluentdLogQueueSendTask(Function<T, Serializable> layout, LoggingEventQueue<T> loggingEventQueue, FluentdSender fluentdSender, int maxRetryPeriodMs, int backoffMultiplier, int maxBackoffMinutes) {
        this.layout = layout;
        this.loggingEventQueue = loggingEventQueue;
        this.fluentdSender = fluentdSender;
        this.maxRetryPeriodMs = maxRetryPeriodMs;
        this.backoffMultiplier = backoffMultiplier;
        this.maxBackoffMinutes = maxBackoffMinutes;
        this.sendoffRetryer = this.buildRetryer();
    }

    private Retryer<Void> buildRetryer() {
        StopStrategy stopStrategy = new StopStrategy(){
            private final StopStrategy stopAfterDelay;
            {
                this.stopAfterDelay = StopStrategies.stopAfterDelay((long)FluentdLogQueueSendTask.this.maxRetryPeriodMs, (TimeUnit)TimeUnit.MILLISECONDS);
            }

            public boolean shouldStop(Attempt failedAttempt) {
                if (this.stopAfterDelay.shouldStop(failedAttempt)) {
                    return true;
                }
                return FluentdLogQueueSendTask.this.loggingEventQueue.isFull();
            }
        };
        WaitStrategy waitStrategy = new WaitStrategy(){
            public static final double RANDOM_RANGE_PERCENT = 1.2;
            private final Random RANDOM = new Random();
            private WaitStrategy exponentialWait = WaitStrategies.exponentialWait((long)FluentdLogQueueSendTask.access$200(FluentdLogQueueSendTask.this), (long)FluentdLogQueueSendTask.access$300(FluentdLogQueueSendTask.this), (TimeUnit)TimeUnit.MINUTES);

            public long computeSleepTime(Attempt failedAttempt) {
                long minimum = this.exponentialWait.computeSleepTime(failedAttempt);
                long maximum = (long)((double)minimum * 1.2);
                return minimum + Math.abs(this.RANDOM.nextLong()) % (maximum - minimum);
            }
        };
        return RetryerBuilder.newBuilder().retryIfExceptionOfType(FluentdRetryableException.class).retryIfRuntimeException().withWaitStrategy(waitStrategy).withStopStrategy(stopStrategy).build();
    }

    @Override
    public void run() {
        List<T> eventsToSend = this.loggingEventQueue.retrieveLoggingEvents(50);
        if (eventsToSend.isEmpty()) {
            return;
        }
        try {
            String payload = this.buildPayload(eventsToSend);
            this.sendoffRetryer.call(() -> {
                try {
                    this.fluentdSender.send(payload);
                }
                catch (Exception e) {
                    System.err.println("Error in attempt to send logs to FluentD");
                    e.printStackTrace(System.err);
                    throw e;
                }
                return null;
            });
        }
        catch (RetryException e) {
            System.err.println("FluentD logging failed - " + eventsToSend.size() + " logs lost");
            e.printStackTrace(System.err);
        }
        catch (ExecutionException e) {
            System.err.println("FluentD logging failed for unknown reason");
            e.printStackTrace(System.err);
        }
    }

    public void clean() {
        List<T> loggingEvents = this.loggingEventQueue.retrieveLoggingEvents(50);
        if (loggingEvents.isEmpty()) {
            return;
        }
        if (this.loggingEventQueue.getSize() > 0) {
            System.err.println("There are pending log messages that will be lost");
        }
        try {
            String payload = this.buildPayload(loggingEvents);
            this.fluentdSender.send(payload);
        }
        catch (Exception e) {
            System.err.println("Error in attempt to send logs to FluentD");
        }
    }

    private String buildPayload(List<T> loggingEvents) {
        String payload = loggingEvents.stream().map(this::formatOptionally).filter(Optional::isPresent).map(Optional::get).collect(Collectors.joining(","));
        return "[" + payload + "]";
    }

    private Optional<String> formatOptionally(T loggingEvent) {
        try {
            Serializable formattedEvent = this.layout.apply(loggingEvent);
            return Optional.of(formattedEvent.toString());
        }
        catch (Exception e) {
            System.err.println("Could not format event for logger:" + loggingEvent);
            e.printStackTrace(System.err);
            return Optional.empty();
        }
    }

    static /* synthetic */ int access$200(FluentdLogQueueSendTask x0) {
        return x0.backoffMultiplier;
    }

    static /* synthetic */ int access$300(FluentdLogQueueSendTask x0) {
        return x0.maxBackoffMinutes;
    }
}

