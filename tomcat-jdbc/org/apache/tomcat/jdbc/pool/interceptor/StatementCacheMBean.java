/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.util.concurrent.atomic.AtomicInteger;

public interface StatementCacheMBean {
    public boolean isCachePrepared();

    public boolean isCacheCallable();

    public int getMaxCacheSize();

    public AtomicInteger getCacheSize();

    public int getCacheSizePerConnection();
}

