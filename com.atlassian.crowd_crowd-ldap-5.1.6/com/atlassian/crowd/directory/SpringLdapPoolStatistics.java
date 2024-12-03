/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.pool2.factory.PooledContextSource
 */
package com.atlassian.crowd.directory;

import org.springframework.ldap.pool2.factory.PooledContextSource;

public class SpringLdapPoolStatistics {
    private final int numActive;
    private final int numActiveRead;
    private final int numActiveWrite;
    private final int numIdle;
    private final int numIdleRead;
    private final int numIdleWrite;
    private final int numWaiters;

    public SpringLdapPoolStatistics(int numActive, int numActiveRead, int numActiveWrite, int numIdle, int numIdleRead, int numIdleWrite, int numWaiters) {
        this.numActive = numActive;
        this.numActiveRead = numActiveRead;
        this.numActiveWrite = numActiveWrite;
        this.numIdle = numIdle;
        this.numIdleRead = numIdleRead;
        this.numIdleWrite = numIdleWrite;
        this.numWaiters = numWaiters;
    }

    public static SpringLdapPoolStatistics fromPool(PooledContextSource pooledContextSource) {
        return new SpringLdapPoolStatistics(pooledContextSource.getNumActive(), pooledContextSource.getNumActiveRead(), pooledContextSource.getNumActiveWrite(), pooledContextSource.getNumIdle(), pooledContextSource.getNumIdleRead(), pooledContextSource.getNumIdleWrite(), pooledContextSource.getNumWaiters());
    }

    public int getNumActive() {
        return this.numActive;
    }

    public int getNumActiveRead() {
        return this.numActiveRead;
    }

    public int getNumActiveWrite() {
        return this.numActiveWrite;
    }

    public int getNumIdle() {
        return this.numIdle;
    }

    public int getNumIdleRead() {
        return this.numIdleRead;
    }

    public int getNumIdleWrite() {
        return this.numIdleWrite;
    }

    public int getNumWaiters() {
        return this.numWaiters;
    }
}

