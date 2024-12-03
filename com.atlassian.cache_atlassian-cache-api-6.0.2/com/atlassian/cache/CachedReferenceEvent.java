/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nullable
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import javax.annotation.Nullable;

@PublicApi
public interface CachedReferenceEvent<V> {
    @Nullable
    public V getValue();
}

