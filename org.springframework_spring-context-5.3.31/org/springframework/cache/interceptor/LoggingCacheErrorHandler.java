/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.cache.interceptor;

import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class LoggingCacheErrorHandler
implements CacheErrorHandler {
    private final Log logger;
    private final boolean logStackTraces;

    public LoggingCacheErrorHandler() {
        this(false);
    }

    public LoggingCacheErrorHandler(boolean logStackTraces) {
        this(LogFactory.getLog(LoggingCacheErrorHandler.class), logStackTraces);
    }

    public LoggingCacheErrorHandler(Log logger, boolean logStackTraces) {
        Assert.notNull((Object)logger, (String)"'logger' must not be null");
        this.logger = logger;
        this.logStackTraces = logStackTraces;
    }

    public LoggingCacheErrorHandler(String loggerName, boolean logStackTraces) {
        Assert.notNull((Object)loggerName, (String)"'loggerName' must not be null");
        this.logger = LogFactory.getLog((String)loggerName);
        this.logStackTraces = logStackTraces;
    }

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        this.logCacheError(() -> String.format("Cache '%s' failed to get entry with key '%s'", cache.getName(), key), exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
        this.logCacheError(() -> String.format("Cache '%s' failed to put entry with key '%s'", cache.getName(), key), exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        this.logCacheError(() -> String.format("Cache '%s' failed to evict entry with key '%s'", cache.getName(), key), exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        this.logCacheError(() -> String.format("Cache '%s' failed to clear entries", cache.getName()), exception);
    }

    protected final Log getLogger() {
        return this.logger;
    }

    protected final boolean isLogStackTraces() {
        return this.logStackTraces;
    }

    protected void logCacheError(Supplier<String> messageSupplier, RuntimeException exception) {
        if (this.getLogger().isWarnEnabled()) {
            if (this.isLogStackTraces()) {
                this.getLogger().warn((Object)messageSupplier.get(), (Throwable)exception);
            } else {
                this.getLogger().warn((Object)messageSupplier.get());
            }
        }
    }
}

