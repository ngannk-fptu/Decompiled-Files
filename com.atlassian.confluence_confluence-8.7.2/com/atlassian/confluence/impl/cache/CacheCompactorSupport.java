/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.CacheCompactor
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.confluence.cache.CacheCompactor;

public abstract class CacheCompactorSupport
implements CacheCompactor {
    public void run() {
        this.compact();
    }
}

