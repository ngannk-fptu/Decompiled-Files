/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.vcache.TransactionalExternalCache
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.core.TransactionControl
 *  com.atlassian.vcache.internal.core.metrics.MetricsCollector
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.vcache.TransactionalExternalCache;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.TransactionControl;
import com.atlassian.vcache.internal.core.metrics.MetricsCollector;
import java.util.Optional;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NoOpRequestContext
implements RequestContext {
    private static final Logger log = LoggerFactory.getLogger(NoOpRequestContext.class);
    private final Supplier<String> partitionIdentifier;

    public NoOpRequestContext(String partitionIdentifier) {
        this(() -> partitionIdentifier);
    }

    public NoOpRequestContext(Supplier<String> partitionIdentifier) {
        this.partitionIdentifier = partitionIdentifier;
    }

    public @NonNull String partitionIdentifier() {
        return this.partitionIdentifier.get();
    }

    public <T> @NonNull T computeIfAbsent(Object key, Supplier<T> supplier) {
        this.logErrorIfTransactionalCacheDetected(key);
        this.logWarningIfNonMetricsAccessDetected(key);
        return supplier.get();
    }

    public <T> @NonNull Optional<T> get(Object key) {
        return Optional.empty();
    }

    private void logErrorIfTransactionalCacheDetected(Object key) {
        if (key instanceof TransactionalExternalCache || key instanceof TransactionControl) {
            String requestUri = Optional.ofNullable(ServletContextThreadLocal.getRequest()).map(HttpServletRequest::getRequestURI).orElse(null);
            if (ConfluenceSystemProperties.isDevMode()) {
                log.error("Transaction vcache access detected on uninitialised thread. Transaction cache semantics are broken. Request URI is {}", (Object)requestUri, (Object)new Exception());
            } else {
                log.warn("Transaction vcache access detected on uninitialised thread. Request URI is {}", (Object)requestUri);
                if (log.isDebugEnabled()) {
                    log.debug("Transaction vcache access detected on uninitialised thread. Transaction cache semantics are broken. Request URI is {}", (Object)requestUri, (Object)new Exception());
                }
            }
        }
    }

    private void logWarningIfNonMetricsAccessDetected(Object key) {
        if (!(key instanceof MetricsCollector)) {
            log.debug("Attempt to store {} in stubbed request context. This information not be retained in the context.", key);
        }
    }
}

