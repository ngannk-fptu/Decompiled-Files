/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.glassfish.external.statistics.CountStatistic;
import org.glassfish.external.statistics.impl.StatisticImpl;

public final class CountStatisticImpl
extends StatisticImpl
implements CountStatistic,
InvocationHandler {
    private long count = 0L;
    private final long initCount;
    private final CountStatistic cs = (CountStatistic)Proxy.newProxyInstance(CountStatistic.class.getClassLoader(), new Class[]{CountStatistic.class}, (InvocationHandler)this);

    public CountStatisticImpl(long countVal, String name, String unit, String desc, long sampleTime, long startTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.count = countVal;
        this.initCount = countVal;
    }

    public CountStatisticImpl(String name, String unit, String desc) {
        this(0L, name, unit, desc, -1L, System.currentTimeMillis());
    }

    public synchronized CountStatistic getStatistic() {
        return this.cs;
    }

    @Override
    public synchronized Map getStaticAsMap() {
        Map m = super.getStaticAsMap();
        m.put("count", this.getCount());
        return m;
    }

    @Override
    public synchronized String toString() {
        return super.toString() + NEWLINE + "Count: " + this.getCount();
    }

    @Override
    public synchronized long getCount() {
        return this.count;
    }

    public synchronized void setCount(long countVal) {
        this.count = countVal;
        this.sampleTime = System.currentTimeMillis();
    }

    public synchronized void increment() {
        ++this.count;
        this.sampleTime = System.currentTimeMillis();
    }

    public synchronized void increment(long delta) {
        this.count += delta;
        this.sampleTime = System.currentTimeMillis();
    }

    public synchronized void decrement() {
        --this.count;
        this.sampleTime = System.currentTimeMillis();
    }

    @Override
    public synchronized void reset() {
        super.reset();
        this.count = this.initCount;
        this.sampleTime = -1L;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result;
        this.checkMethod(m);
        try {
            result = m.invoke((Object)this, args);
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

