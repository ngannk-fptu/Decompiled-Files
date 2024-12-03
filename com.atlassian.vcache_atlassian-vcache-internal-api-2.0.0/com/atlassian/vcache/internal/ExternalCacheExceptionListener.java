/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalCacheException
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.ExternalCacheException;

public interface ExternalCacheExceptionListener {
    public void onThrow(String var1, ExternalCacheException var2);
}

