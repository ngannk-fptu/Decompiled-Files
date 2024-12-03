/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.beehive;

import com.atlassian.annotations.PublicApi;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.annotation.Nonnull;

@PublicApi
public interface ClusterLock
extends Lock {
    public boolean isHeldByCurrentThread();

    @Override
    @Nonnull
    public Condition newCondition();

    @Override
    public void lockInterruptibly() throws InterruptedException;
}

