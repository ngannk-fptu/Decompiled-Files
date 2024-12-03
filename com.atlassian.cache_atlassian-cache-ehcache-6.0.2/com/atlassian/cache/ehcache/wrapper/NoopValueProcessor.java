/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.ehcache.wrapper;

import com.atlassian.cache.ehcache.wrapper.ValueProcessor;
import javax.annotation.Nullable;

public class NoopValueProcessor
implements ValueProcessor {
    @Override
    public Object wrap(@Nullable Object o) {
        return o;
    }

    @Override
    public Object unwrap(@Nullable Object o) {
        return o;
    }
}

