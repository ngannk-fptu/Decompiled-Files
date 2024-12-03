/*
 * Decompiled with CFR 0.152.
 */
package com.github.rholder.retry;

public interface BlockStrategy {
    public void block(long var1) throws InterruptedException;
}

