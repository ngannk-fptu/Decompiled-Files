/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache;

import java.util.concurrent.Future;

public interface CacheRefreshService {
    public Future<?> refreshAll(boolean var1);
}

