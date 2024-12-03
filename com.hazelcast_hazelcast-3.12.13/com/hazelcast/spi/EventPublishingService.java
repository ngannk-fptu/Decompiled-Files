/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

public interface EventPublishingService<E, T> {
    public void dispatchEvent(E var1, T var2);
}

