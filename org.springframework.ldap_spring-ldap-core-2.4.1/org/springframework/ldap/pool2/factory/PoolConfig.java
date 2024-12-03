/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.pool2.factory;

public class PoolConfig {
    private int maxIdlePerKey = 8;
    private int maxTotal = -1;
    private int maxTotalPerKey = 8;
    private int minIdlePerKey = 0;
    private boolean blockWhenExhausted = true;
    private String evictionPolicyClassName = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";
    private boolean fairness = false;
    private boolean jmxEnabled = true;
    private String jmxNameBase = null;
    private String jmxNamePrefix = "ldap-pool";
    private boolean lifo = true;
    private long maxWaitMillis = -1L;
    private long minEvictableIdleTimeMillis = 1800000L;
    private int numTestsPerEvictionRun = 3;
    private long softMinEvictableIdleTimeMillis = -1L;
    private boolean testOnBorrow = false;
    private boolean testOnCreate = false;
    private boolean testOnReturn = false;
    private boolean testWhileIdle = false;
    private long timeBetweenEvictionRunsMillis = -1L;

    public void setMaxIdlePerKey(int maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setMaxTotalPerKey(int maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    public void setMinIdlePerKey(int minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public void setEvictionPolicyClassName(String evictionPolicyClassName) {
        this.evictionPolicyClassName = evictionPolicyClassName;
    }

    public void setFairness(boolean fairness) {
        this.fairness = fairness;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public void setJmxNameBase(String jmxNameBase) {
        this.jmxNameBase = jmxNameBase;
    }

    public void setJmxNamePrefix(String jmxNamePrefix) {
        this.jmxNamePrefix = jmxNamePrefix;
    }

    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMaxIdlePerKey() {
        return this.maxIdlePerKey;
    }

    public int getMaxTotal() {
        return this.maxTotal;
    }

    public int getMaxTotalPerKey() {
        return this.maxTotalPerKey;
    }

    public int getMinIdlePerKey() {
        return this.minIdlePerKey;
    }

    public boolean isBlockWhenExhausted() {
        return this.blockWhenExhausted;
    }

    public String getEvictionPolicyClassName() {
        return this.evictionPolicyClassName;
    }

    public boolean isFairness() {
        return this.fairness;
    }

    public boolean isJmxEnabled() {
        return this.jmxEnabled;
    }

    public String getJmxNameBase() {
        return this.jmxNameBase;
    }

    public String getJmxNamePrefix() {
        return this.jmxNamePrefix;
    }

    public boolean isLifo() {
        return this.lifo;
    }

    public long getMaxWaitMillis() {
        return this.maxWaitMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return this.softMinEvictableIdleTimeMillis;
    }

    public boolean isTestOnBorrow() {
        return this.testOnBorrow;
    }

    public boolean isTestOnCreate() {
        return this.testOnCreate;
    }

    public boolean isTestOnReturn() {
        return this.testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return this.testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }
}

