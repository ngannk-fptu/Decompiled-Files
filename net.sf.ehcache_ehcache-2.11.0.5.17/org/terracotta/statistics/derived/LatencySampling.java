/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.terracotta.statistics.derived;

import java.util.EnumSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.AbstractSourceStatistic;
import org.terracotta.statistics.jsr166e.ThreadLocalRandom;
import org.terracotta.statistics.observer.ChainedEventObserver;
import org.terracotta.statistics.observer.ChainedOperationObserver;

public class LatencySampling<T extends Enum<T>>
extends AbstractSourceStatistic<ChainedEventObserver>
implements ChainedOperationObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LatencySampling.class);
    private final ThreadLocal<Long> operationStartTime = new ThreadLocal();
    private final Set<T> targetOperations;
    private final int ceiling;

    public LatencySampling(Set<T> targets, double sampling) {
        if (sampling > 1.0 || sampling < 0.0) {
            throw new IllegalArgumentException();
        }
        this.ceiling = (int)(2.147483647E9 * sampling);
        this.targetOperations = EnumSet.copyOf(targets);
    }

    @Override
    public void begin(long time) {
        if (this.sample()) {
            this.operationStartTime.set(time);
        }
    }

    @Override
    public void end(long time, T result) {
        Long start;
        if (this.targetOperations.contains(result) && (start = this.operationStartTime.get()) != null) {
            long latency = time - start;
            if (!this.derivedStatistics.isEmpty()) {
                if (latency < 0L) {
                    LOGGER.info("Dropping {} event with negative latency {} (possible backwards nanoTime() movement)", result, (Object)time);
                } else {
                    for (ChainedEventObserver observer : this.derivedStatistics) {
                        observer.event(time, latency);
                    }
                }
            }
        }
        this.operationStartTime.remove();
    }

    @Override
    public void end(long time, T result, long ... parameters) {
        this.end(time, result);
    }

    private boolean sample() {
        return (double)this.ceiling == 1.0 || ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE) < this.ceiling;
    }
}

