/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.concurrent;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gate {
    private static final Logger log = LoggerFactory.getLogger(Gate.class);
    private final Clock clock;
    private final Duration minDurationBetweenCalls;
    private final ReentrantLock lock;
    private volatile long nextInvocationTimestamp;

    public Gate(@Nonnull Clock clock, @Nonnull Duration minDurationBetweenCalls) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.minDurationBetweenCalls = Objects.requireNonNull(minDurationBetweenCalls);
        this.lock = new ReentrantLock();
        this.nextInvocationTimestamp = 0L;
    }

    public Gate(@Nonnull Duration minDurationBetweenCalls) {
        this(Clock.systemDefaultZone(), minDurationBetweenCalls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public <T> Optional<T> ifAccessible(@Nonnull Supplier<T> operation) {
        long now = this.clock.millis();
        if (now >= this.nextInvocationTimestamp && this.lock.tryLock()) {
            try {
                if (now >= this.nextInvocationTimestamp) {
                    this.nextInvocationTimestamp = this.clock.millis() + this.minDurationBetweenCalls.toMillis();
                    log.trace("running operation");
                    Optional<T> optional = Optional.ofNullable(operation.get());
                    return optional;
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        log.trace("skipping operation because it already ran in the past {} ms", (Object)this.minDurationBetweenCalls.toMillis());
        return Optional.empty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public void ifAccessible(@Nonnull Runnable operation) {
        long now = this.clock.millis();
        if (now >= this.nextInvocationTimestamp && this.lock.tryLock()) {
            try {
                if (now >= this.nextInvocationTimestamp) {
                    this.nextInvocationTimestamp = this.clock.millis() + this.minDurationBetweenCalls.toMillis();
                    log.trace("running operation");
                    operation.run();
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        log.trace("skipping operation because it already ran in the past {} ms", (Object)this.minDurationBetweenCalls.toMillis());
    }
}

