/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.ExternalCache;
import com.atlassian.vcache.ExternalWriteOperationsUnbuffered;

@PublicApi
public interface StableReadExternalCache<V>
extends ExternalCache<V>,
ExternalWriteOperationsUnbuffered<V> {
}

