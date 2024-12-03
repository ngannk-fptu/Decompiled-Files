/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

public interface StripedRunnable
extends Runnable {
    public int getKey();
}

