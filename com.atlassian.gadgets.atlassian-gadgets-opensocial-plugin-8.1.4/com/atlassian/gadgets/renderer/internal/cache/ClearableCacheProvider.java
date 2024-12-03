/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.shindig.common.cache.CacheProvider
 */
package com.atlassian.gadgets.renderer.internal.cache;

import org.apache.shindig.common.cache.CacheProvider;

public interface ClearableCacheProvider
extends CacheProvider {
    public void clear();
}

