/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

public interface SubscriberContextSupport {
    public Object createRecoveryOperation(String var1, String var2, long var3, int var5);

    public Boolean resolveResponseForRecoveryOperation(Object var1);

    public Object createDestroyQueryCacheOperation(String var1, String var2);
}

