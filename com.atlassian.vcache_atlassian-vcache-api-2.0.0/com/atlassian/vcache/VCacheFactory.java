/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.marshalling.api.MarshallingPair
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.Marshaller;
import com.atlassian.vcache.RequestCache;
import com.atlassian.vcache.RequestCacheSettings;
import com.atlassian.vcache.RequestCacheSettingsBuilder;
import com.atlassian.vcache.StableReadExternalCache;
import com.atlassian.vcache.TransactionalExternalCache;

@PublicApi
public interface VCacheFactory {
    public <K, V> JvmCache<K, V> getJvmCache(String var1, JvmCacheSettings var2);

    @Deprecated
    default public <K, V> RequestCache<K, V> getRequestCache(String name) {
        return this.getRequestCache(name, new RequestCacheSettingsBuilder().build());
    }

    public <K, V> RequestCache<K, V> getRequestCache(String var1, RequestCacheSettings var2);

    @Deprecated
    public <V> TransactionalExternalCache<V> getTransactionalExternalCache(String var1, Marshaller<V> var2, ExternalCacheSettings var3);

    public <V> TransactionalExternalCache<V> getTransactionalExternalCache(String var1, MarshallingPair<V> var2, ExternalCacheSettings var3);

    @Deprecated
    public <V> StableReadExternalCache<V> getStableReadExternalCache(String var1, Marshaller<V> var2, ExternalCacheSettings var3);

    public <V> StableReadExternalCache<V> getStableReadExternalCache(String var1, MarshallingPair<V> var2, ExternalCacheSettings var3);

    @Deprecated
    public <V> DirectExternalCache<V> getDirectExternalCache(String var1, Marshaller<V> var2, ExternalCacheSettings var3);

    public <V> DirectExternalCache<V> getDirectExternalCache(String var1, MarshallingPair<V> var2, ExternalCacheSettings var3);
}

