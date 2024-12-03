/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.glassfish.external.statistics.StringStatistic;
import org.glassfish.external.statistics.impl.StatisticImpl;

public final class StringStatisticImpl
extends StatisticImpl
implements StringStatistic,
InvocationHandler {
    private volatile String str = null;
    private final String initStr;
    private final StringStatistic ss = (StringStatistic)Proxy.newProxyInstance(StringStatistic.class.getClassLoader(), new Class[]{StringStatistic.class}, (InvocationHandler)this);

    public StringStatisticImpl(String str, String name, String unit, String desc, long sampleTime, long startTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.str = str;
        this.initStr = str;
    }

    public StringStatisticImpl(String name, String unit, String desc) {
        this("", name, unit, desc, System.currentTimeMillis(), System.currentTimeMillis());
    }

    public synchronized StringStatistic getStatistic() {
        return this.ss;
    }

    @Override
    public synchronized Map getStaticAsMap() {
        Map m = super.getStaticAsMap();
        if (this.getCurrent() != null) {
            m.put("current", this.getCurrent());
        }
        return m;
    }

    @Override
    public synchronized String toString() {
        return super.toString() + NEWLINE + "Current-value: " + this.getCurrent();
    }

    @Override
    public String getCurrent() {
        return this.str;
    }

    public void setCurrent(String str) {
        this.str = str;
        this.sampleTime = System.currentTimeMillis();
    }

    @Override
    public synchronized void reset() {
        super.reset();
        this.str = this.initStr;
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

