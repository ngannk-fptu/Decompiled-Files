/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.ehcache.wrapper;

import javax.annotation.Nullable;

public interface ValueProcessor {
    public Object wrap(@Nullable Object var1);

    public Object unwrap(@Nullable Object var1);
}

