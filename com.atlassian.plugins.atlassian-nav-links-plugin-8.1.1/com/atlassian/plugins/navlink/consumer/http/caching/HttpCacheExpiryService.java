/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.ExpiringValue
 *  javax.annotation.Nullable
 *  org.apache.http.HttpResponse
 */
package com.atlassian.plugins.navlink.consumer.http.caching;

import com.atlassian.failurecache.ExpiringValue;
import javax.annotation.Nullable;
import org.apache.http.HttpResponse;

public interface HttpCacheExpiryService {
    public <V> ExpiringValue<V> createExpiringValueFrom(HttpResponse var1, @Nullable V var2);
}

