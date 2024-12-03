/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.glassfish.external.statistics.BoundaryStatistic;
import org.glassfish.external.statistics.impl.StatisticImpl;

public final class BoundaryStatisticImpl
extends StatisticImpl
implements BoundaryStatistic,
InvocationHandler {
    private final long lowerBound;
    private final long upperBound;
    private final BoundaryStatistic bs = (BoundaryStatistic)Proxy.newProxyInstance(BoundaryStatistic.class.getClassLoader(), new Class[]{BoundaryStatistic.class}, (InvocationHandler)this);

    public BoundaryStatisticImpl(long lower, long upper, String name, String unit, String desc, long startTime, long sampleTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.upperBound = upper;
        this.lowerBound = lower;
    }

    public synchronized BoundaryStatistic getStatistic() {
        return this.bs;
    }

    @Override
    public synchronized Map getStaticAsMap() {
        Map m = super.getStaticAsMap();
        m.put("lowerbound", this.getLowerBound());
        m.put("upperbound", this.getUpperBound());
        return m;
    }

    @Override
    public synchronized long getLowerBound() {
        return this.lowerBound;
    }

    @Override
    public synchronized long getUpperBound() {
        return this.upperBound;
    }

    @Override
    public synchronized void reset() {
        super.reset();
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

