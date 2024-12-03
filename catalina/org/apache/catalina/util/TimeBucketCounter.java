/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class TimeBucketCounter {
    private static final Log log = LogFactory.getLog(TimeBucketCounter.class);
    private static final StringManager sm = StringManager.getManager(TimeBucketCounter.class);
    private final ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap();
    private final int numBits;
    private final double ratio;
    private ScheduledFuture<?> maintenanceFuture;
    private ScheduledFuture<?> monitorFuture;
    private final ScheduledExecutorService executorService;
    private final long sleeptime;

    public TimeBucketCounter(int bucketDuration, ScheduledExecutorService executorService) {
        int pof2;
        this.executorService = executorService;
        int durationMillis = bucketDuration * 1000;
        int bits = 0;
        int bitCheck = pof2 = TimeBucketCounter.nextPowerOf2(durationMillis);
        while (bitCheck > 1) {
            bitCheck = pof2 >> ++bits;
        }
        this.numBits = bits;
        this.ratio = TimeBucketCounter.ratioToPowerOf2(durationMillis);
        int cleanupsPerBucketDuration = durationMillis >= 60000 ? 6 : 3;
        this.sleeptime = durationMillis / cleanupsPerBucketDuration;
        if (this.sleeptime > 0L) {
            this.monitorFuture = executorService.scheduleWithFixedDelay(new MaintenanceMonitor(), 0L, 60L, TimeUnit.SECONDS);
        }
    }

    public final int increment(String identifier) {
        String key = this.getCurrentBucketPrefix() + "-" + identifier;
        AtomicInteger ai = this.map.computeIfAbsent(key, v -> new AtomicInteger());
        return ai.incrementAndGet();
    }

    public final int getCurrentBucketPrefix() {
        return (int)(System.currentTimeMillis() >> this.numBits);
    }

    public int getNumBits() {
        return this.numBits;
    }

    public int getActualDuration() {
        return (int)Math.pow(2.0, this.getNumBits());
    }

    public double getRatio() {
        return this.ratio;
    }

    static double ratioToPowerOf2(int value) {
        double nextPO2 = TimeBucketCounter.nextPowerOf2(value);
        return (double)Math.round(1000.0 * nextPO2 / (double)value) / 1000.0;
    }

    static int nextPowerOf2(int value) {
        int valueOfHighestBit = Integer.highestOneBit(value);
        if (valueOfHighestBit == value) {
            return value;
        }
        return valueOfHighestBit << 1;
    }

    public long getMillisUntilNextBucket() {
        long millis = System.currentTimeMillis();
        long nextTimeBucketMillis = millis + (long)Math.pow(2.0, this.numBits) >> this.numBits << this.numBits;
        long delta = nextTimeBucketMillis - millis;
        return delta;
    }

    public void destroy() {
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        if (this.maintenanceFuture != null) {
            this.maintenanceFuture.cancel(true);
            this.maintenanceFuture = null;
        }
    }

    private class MaintenanceMonitor
    implements Runnable {
        private MaintenanceMonitor() {
        }

        @Override
        public void run() {
            if (TimeBucketCounter.this.sleeptime > 0L && (TimeBucketCounter.this.maintenanceFuture == null || TimeBucketCounter.this.maintenanceFuture.isDone())) {
                if (TimeBucketCounter.this.maintenanceFuture != null && TimeBucketCounter.this.maintenanceFuture.isDone()) {
                    try {
                        TimeBucketCounter.this.maintenanceFuture.get();
                    }
                    catch (InterruptedException | ExecutionException e) {
                        log.error((Object)sm.getString("timebucket.maintenance.error"), (Throwable)e);
                    }
                }
                TimeBucketCounter.this.maintenanceFuture = TimeBucketCounter.this.executorService.scheduleWithFixedDelay(new Maintenance(), TimeBucketCounter.this.sleeptime, TimeBucketCounter.this.sleeptime, TimeUnit.MILLISECONDS);
            }
        }
    }

    private class Maintenance
    implements Runnable {
        private Maintenance() {
        }

        @Override
        public void run() {
            String currentBucketPrefix = String.valueOf(TimeBucketCounter.this.getCurrentBucketPrefix());
            Set keys = TimeBucketCounter.this.map.keySet();
            keys.removeIf(k -> !k.startsWith(currentBucketPrefix));
        }
    }
}

