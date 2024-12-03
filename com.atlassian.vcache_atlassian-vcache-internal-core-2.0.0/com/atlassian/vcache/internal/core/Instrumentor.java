/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.RequestCache
 *  com.atlassian.vcache.StableReadExternalCache
 *  com.atlassian.vcache.TransactionalExternalCache
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.RequestCache;
import com.atlassian.vcache.StableReadExternalCache;
import com.atlassian.vcache.TransactionalExternalCache;
import com.atlassian.vcache.internal.core.TransactionControl;

public interface Instrumentor {
    public TransactionControl wrap(TransactionControl var1, String var2);

    public <T> MarshallingPair<T> wrap(MarshallingPair<T> var1, String var2);

    public <K, V> JvmCache<K, V> wrap(JvmCache<K, V> var1);

    public <K, V> RequestCache<K, V> wrap(RequestCache<K, V> var1);

    public <V> DirectExternalCache<V> wrap(DirectExternalCache<V> var1);

    public <V> StableReadExternalCache<V> wrap(StableReadExternalCache<V> var1);

    public <V> TransactionalExternalCache<V> wrap(TransactionalExternalCache<V> var1);
}

