/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.ExternalCache;
import com.atlassian.vcache.ExternalWriteOperationsBuffered;

@PublicApi
public interface TransactionalExternalCache<V>
extends ExternalCache<V>,
ExternalWriteOperationsBuffered<V> {
}

