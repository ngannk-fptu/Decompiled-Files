/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.glassfish.external.statistics.AverageRangeStatistic;
import org.glassfish.external.statistics.impl.StatisticImpl;

public final class AverageRangeStatisticImpl
extends StatisticImpl
implements AverageRangeStatistic,
InvocationHandler {
    private long currentVal = 0L;
    private long highWaterMark = Long.MIN_VALUE;
    private long lowWaterMark = Long.MAX_VALUE;
    private long numberOfSamples = 0L;
    private long runningTotal = 0L;
    private final long initCurrentVal;
    private final long initHighWaterMark;
    private final long initLowWaterMark;
    private final long initNumberOfSamples;
    private final long initRunningTotal;
    private final AverageRangeStatistic as = (AverageRangeStatistic)Proxy.newProxyInstance(AverageRangeStatistic.class.getClassLoader(), new Class[]{AverageRangeStatistic.class}, (InvocationHandler)this);

    public AverageRangeStatisticImpl(long curVal, long highMark, long lowMark, String name, String unit, String desc, long startTime, long sampleTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.currentVal = curVal;
        this.initCurrentVal = curVal;
        this.highWaterMark = highMark;
        this.initHighWaterMark = highMark;
        this.lowWaterMark = lowMark;
        this.initLowWaterMark = lowMark;
        this.initNumberOfSamples = this.numberOfSamples = 0L;
        this.initRunningTotal = this.runningTotal = 0L;
    }

    public synchronized AverageRangeStatistic getStatistic() {
        return this.as;
    }

    @Override
    public synchronized String toString() {
        return super.toString() + NEWLINE + "Current: " + this.getCurrent() + NEWLINE + "LowWaterMark: " + this.getLowWaterMark() + NEWLINE + "HighWaterMark: " + this.getHighWaterMark() + NEWLINE + "Average:" + this.getAverage();
    }

    @Override
    public synchronized Map getStaticAsMap() {
        Map m = super.getStaticAsMap();
        m.put("current", this.getCurrent());
        m.put("lowwatermark", this.getLowWaterMark());
        m.put("highwatermark", this.getHighWaterMark());
        m.put("average", this.getAverage());
        return m;
    }

    @Override
    public synchronized void reset() {
        super.reset();
        this.currentVal = this.initCurrentVal;
        this.highWaterMark = this.initHighWaterMark;
        this.lowWaterMark = this.initLowWaterMark;
        this.numberOfSamples = this.initNumberOfSamples;
        this.runningTotal = this.initRunningTotal;
        this.sampleTime = -1L;
    }

    @Override
    public synchronized long getAverage() {
        if (this.numberOfSamples == 0L) {
            return -1L;
        }
        return this.runningTotal / this.numberOfSamples;
    }

    @Override
    public synchronized long getCurrent() {
        return this.currentVal;
    }

    public synchronized void setCurrent(long curVal) {
        this.currentVal = curVal;
        this.lowWaterMark = curVal >= this.lowWaterMark ? this.lowWaterMark : curVal;
        this.highWaterMark = curVal >= this.highWaterMark ? curVal : this.highWaterMark;
        ++this.numberOfSamples;
        this.runningTotal += curVal;
        this.sampleTime = System.currentTimeMillis();
    }

    @Override
    public synchronized long getHighWaterMark() {
        return this.highWaterMark;
    }

    @Override
    public synchronized long getLowWaterMark() {
        return this.lowWaterMark;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        this.checkMethod(method);
        try {
            result = method.invoke((Object)this, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }
        return result;
    }
}

