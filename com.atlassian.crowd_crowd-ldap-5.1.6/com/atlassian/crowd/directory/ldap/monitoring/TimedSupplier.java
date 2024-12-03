/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Stopwatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap.monitoring;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TimedSupplier<T>
implements Supplier<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimedSupplier.class);
    private final String operationDescription;
    private final Stopwatch watch;
    private final long thresholdMillis;
    private final Logger log;

    public TimedSupplier(String operationDescription, long thresholdMillis) {
        this(operationDescription, Stopwatch.createUnstarted(), LOGGER, thresholdMillis);
    }

    @VisibleForTesting
    public TimedSupplier(String operationDescription, Stopwatch stopWatch, Logger log, long thresholdMillis) {
        this.operationDescription = operationDescription;
        this.watch = stopWatch;
        this.log = log;
        this.thresholdMillis = thresholdMillis;
    }

    public abstract T timedGet();

    @Override
    public final T get() {
        this.log.debug("Execute operation {}", (Object)this.operationDescription);
        this.watch.start();
        try {
            T t = this.timedGet();
            return t;
        }
        finally {
            this.watch.stop();
            if (this.watch.elapsed(TimeUnit.MILLISECONDS) > this.thresholdMillis) {
                this.log.info("Timed call for {} took {}ms", (Object)this.operationDescription, (Object)this.watch.elapsed(TimeUnit.MILLISECONDS));
            } else if (this.log.isDebugEnabled()) {
                this.log.debug("Timed call for {} took {}ms", (Object)this.operationDescription, (Object)this.watch.elapsed(TimeUnit.MILLISECONDS));
            }
        }
    }
}

