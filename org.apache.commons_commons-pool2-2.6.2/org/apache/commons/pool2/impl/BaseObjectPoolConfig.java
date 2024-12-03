/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import org.apache.commons.pool2.BaseObject;
import org.apache.commons.pool2.impl.DefaultEvictionPolicy;
import org.apache.commons.pool2.impl.EvictionPolicy;

public abstract class BaseObjectPoolConfig<T>
extends BaseObject
implements Cloneable {
    public static final boolean DEFAULT_LIFO = true;
    public static final boolean DEFAULT_FAIRNESS = false;
    public static final long DEFAULT_MAX_WAIT_MILLIS = -1L;
    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000L;
    public static final long DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1L;
    public static final long DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS = 10000L;
    public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
    public static final boolean DEFAULT_TEST_ON_CREATE = false;
    public static final boolean DEFAULT_TEST_ON_BORROW = false;
    public static final boolean DEFAULT_TEST_ON_RETURN = false;
    public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
    public static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED = true;
    public static final boolean DEFAULT_JMX_ENABLE = true;
    public static final String DEFAULT_JMX_NAME_PREFIX = "pool";
    public static final String DEFAULT_JMX_NAME_BASE = null;
    public static final String DEFAULT_EVICTION_POLICY_CLASS_NAME = DefaultEvictionPolicy.class.getName();
    private boolean lifo = true;
    private boolean fairness = false;
    private long maxWaitMillis = -1L;
    private long minEvictableIdleTimeMillis = 1800000L;
    private long evictorShutdownTimeoutMillis = 10000L;
    private long softMinEvictableIdleTimeMillis = -1L;
    private int numTestsPerEvictionRun = 3;
    private EvictionPolicy<T> evictionPolicy = null;
    private String evictionPolicyClassName = DEFAULT_EVICTION_POLICY_CLASS_NAME;
    private boolean testOnCreate = false;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean testWhileIdle = false;
    private long timeBetweenEvictionRunsMillis = -1L;
    private boolean blockWhenExhausted = true;
    private boolean jmxEnabled = true;
    private String jmxNamePrefix = "pool";
    private String jmxNameBase = DEFAULT_JMX_NAME_BASE;

    public boolean getLifo() {
        return this.lifo;
    }

    public boolean getFairness() {
        return this.fairness;
    }

    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    public void setFairness(boolean fairness) {
        this.fairness = fairness;
    }

    public long getMaxWaitMillis() {
        return this.maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return this.softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public long getEvictorShutdownTimeoutMillis() {
        return this.evictorShutdownTimeoutMillis;
    }

    public void setEvictorShutdownTimeoutMillis(long evictorShutdownTimeoutMillis) {
        this.evictorShutdownTimeoutMillis = evictorShutdownTimeoutMillis;
    }

    public boolean getTestOnCreate() {
        return this.testOnCreate;
    }

    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public boolean getTestOnBorrow() {
        return this.testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean getTestOnReturn() {
        return this.testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean getTestWhileIdle() {
        return this.testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public EvictionPolicy<T> getEvictionPolicy() {
        return this.evictionPolicy;
    }

    public String getEvictionPolicyClassName() {
        return this.evictionPolicyClassName;
    }

    public void setEvictionPolicy(EvictionPolicy<T> evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    public void setEvictionPolicyClassName(String evictionPolicyClassName) {
        this.evictionPolicyClassName = evictionPolicyClassName;
    }

    public boolean getBlockWhenExhausted() {
        return this.blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public boolean getJmxEnabled() {
        return this.jmxEnabled;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public String getJmxNameBase() {
        return this.jmxNameBase;
    }

    public void setJmxNameBase(String jmxNameBase) {
        this.jmxNameBase = jmxNameBase;
    }

    public String getJmxNamePrefix() {
        return this.jmxNamePrefix;
    }

    public void setJmxNamePrefix(String jmxNamePrefix) {
        this.jmxNamePrefix = jmxNamePrefix;
    }

    @Override
    protected void toStringAppendFields(StringBuilder builder) {
        builder.append("lifo=");
        builder.append(this.lifo);
        builder.append(", fairness=");
        builder.append(this.fairness);
        builder.append(", maxWaitMillis=");
        builder.append(this.maxWaitMillis);
        builder.append(", minEvictableIdleTimeMillis=");
        builder.append(this.minEvictableIdleTimeMillis);
        builder.append(", softMinEvictableIdleTimeMillis=");
        builder.append(this.softMinEvictableIdleTimeMillis);
        builder.append(", numTestsPerEvictionRun=");
        builder.append(this.numTestsPerEvictionRun);
        builder.append(", evictionPolicyClassName=");
        builder.append(this.evictionPolicyClassName);
        builder.append(", testOnCreate=");
        builder.append(this.testOnCreate);
        builder.append(", testOnBorrow=");
        builder.append(this.testOnBorrow);
        builder.append(", testOnReturn=");
        builder.append(this.testOnReturn);
        builder.append(", testWhileIdle=");
        builder.append(this.testWhileIdle);
        builder.append(", timeBetweenEvictionRunsMillis=");
        builder.append(this.timeBetweenEvictionRunsMillis);
        builder.append(", blockWhenExhausted=");
        builder.append(this.blockWhenExhausted);
        builder.append(", jmxEnabled=");
        builder.append(this.jmxEnabled);
        builder.append(", jmxNamePrefix=");
        builder.append(this.jmxNamePrefix);
        builder.append(", jmxNameBase=");
        builder.append(this.jmxNameBase);
    }
}

