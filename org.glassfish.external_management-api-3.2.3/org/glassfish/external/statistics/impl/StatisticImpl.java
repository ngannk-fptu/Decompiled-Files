/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.external.statistics.Statistic;

public abstract class StatisticImpl
implements Statistic {
    private final String statisticName;
    private final String statisticUnit;
    private final String statisticDesc;
    protected long sampleTime = -1L;
    private long startTime;
    public static final String UNIT_COUNT = "count";
    public static final String UNIT_SECOND = "second";
    public static final String UNIT_MILLISECOND = "millisecond";
    public static final String UNIT_MICROSECOND = "microsecond";
    public static final String UNIT_NANOSECOND = "nanosecond";
    public static final String START_TIME = "starttime";
    public static final String LAST_SAMPLE_TIME = "lastsampletime";
    protected final Map<String, Object> statMap = new ConcurrentHashMap<String, Object>();
    protected static final String NEWLINE = System.getProperty("line.separator");

    protected StatisticImpl(String name, String unit, String desc, long start_time, long sample_time) {
        this.statisticName = StatisticImpl.isValidString(name) ? name : "name";
        this.statisticUnit = StatisticImpl.isValidString(unit) ? unit : "unit";
        this.statisticDesc = StatisticImpl.isValidString(desc) ? desc : "description";
        this.startTime = start_time;
        this.sampleTime = sample_time;
    }

    protected StatisticImpl(String name, String unit, String desc) {
        this(name, unit, desc, System.currentTimeMillis(), System.currentTimeMillis());
    }

    public synchronized Map getStaticAsMap() {
        if (StatisticImpl.isValidString(this.statisticName)) {
            this.statMap.put("name", this.statisticName);
        }
        if (StatisticImpl.isValidString(this.statisticUnit)) {
            this.statMap.put("unit", this.statisticUnit);
        }
        if (StatisticImpl.isValidString(this.statisticDesc)) {
            this.statMap.put("description", this.statisticDesc);
        }
        this.statMap.put(START_TIME, this.startTime);
        this.statMap.put(LAST_SAMPLE_TIME, this.sampleTime);
        return this.statMap;
    }

    @Override
    public String getName() {
        return this.statisticName;
    }

    @Override
    public String getDescription() {
        return this.statisticDesc;
    }

    @Override
    public String getUnit() {
        return this.statisticUnit;
    }

    @Override
    public synchronized long getLastSampleTime() {
        return this.sampleTime;
    }

    @Override
    public synchronized long getStartTime() {
        return this.startTime;
    }

    public synchronized void reset() {
        this.startTime = System.currentTimeMillis();
    }

    public synchronized String toString() {
        return "Statistic " + this.getClass().getName() + NEWLINE + "Name: " + this.getName() + NEWLINE + "Description: " + this.getDescription() + NEWLINE + "Unit: " + this.getUnit() + NEWLINE + "LastSampleTime: " + this.getLastSampleTime() + NEWLINE + "StartTime: " + this.getStartTime();
    }

    protected static boolean isValidString(String str) {
        return str != null && str.length() > 0;
    }

    protected void checkMethod(Method method) {
        if (method == null || method.getDeclaringClass() == null || !Statistic.class.isAssignableFrom(method.getDeclaringClass()) || Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("Invalid method on invoke");
        }
    }
}

