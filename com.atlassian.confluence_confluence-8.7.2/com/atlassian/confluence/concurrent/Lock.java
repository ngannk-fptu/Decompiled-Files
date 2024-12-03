/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.concurrent;

@Deprecated
public interface Lock {
    public boolean tryLock();

    public void unlock();

    public void lock();
}

