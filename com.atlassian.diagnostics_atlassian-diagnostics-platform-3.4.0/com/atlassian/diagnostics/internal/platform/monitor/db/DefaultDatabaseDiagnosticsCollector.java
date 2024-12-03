/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.PluginKeyStack
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.RemovalCause
 *  com.google.common.cache.RemovalListener
 *  com.google.common.cache.RemovalNotification
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import com.atlassian.diagnostics.internal.platform.monitor.DurationUtils;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseDiagnosticsCollector;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseOperationDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnosticProvider;
import com.atlassian.diagnostics.internal.platform.monitor.db.SqlOperation;
import com.atlassian.diagnostics.internal.platform.plugin.PluginFinder;
import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDatabaseDiagnosticsCollector
implements DatabaseDiagnosticsCollector {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDatabaseDiagnosticsCollector.class);
    private final ClassContextSecurityManager classContextSecurityManager;
    private final DatabaseMonitorConfiguration configuration;
    private final DatabasePoolDiagnosticProvider databasePoolDiagnosticProvider;
    private final Duration poolConnectionLeakTimeout;
    private final DatabaseMonitor databaseMonitor;
    private final boolean findInvoker;
    private final boolean improvedAccuracy;
    private final PluginFinder pluginFinder;
    private final Clock clock;
    private final Cache<Connection, Instant> connectionCache;

    public DefaultDatabaseDiagnosticsCollector(@Nonnull DatabaseMonitorConfiguration configuration, @Nonnull DatabasePoolDiagnosticProvider databasePoolDiagnosticProvider, @Nonnull Clock clock, @Nonnull DatabaseMonitor databaseMonitor, @Nonnull PluginFinder pluginFinder) {
        this.configuration = configuration;
        this.classContextSecurityManager = new ClassContextSecurityManager();
        this.databasePoolDiagnosticProvider = databasePoolDiagnosticProvider;
        this.poolConnectionLeakTimeout = configuration.poolConnectionLeakTimeout();
        this.databaseMonitor = databaseMonitor;
        this.pluginFinder = pluginFinder;
        this.clock = clock;
        this.connectionCache = CacheBuilder.newBuilder().weakKeys().maximumSize(500L).expireAfterAccess(this.poolConnectionLeakTimeout).removalListener((RemovalListener)new LeakedConnectionListener()).build();
        this.findInvoker = configuration.findStaticMethodInvoker();
        this.improvedAccuracy = configuration.staticMethodInvokerImprovedAccuracy();
    }

    @Override
    public boolean isEnabled() {
        return this.configuration.isEnabled();
    }

    @Override
    public void trackConnection(Connection connection) {
        if (DurationUtils.durationOf(this.poolConnectionLeakTimeout).isGreaterThan(Duration.ZERO)) {
            this.connectionCache.put((Object)connection, (Object)this.clock.instant());
        }
    }

    @Override
    public void removeTrackedConnection(Connection connection) {
        if (DurationUtils.durationOf(this.poolConnectionLeakTimeout).isGreaterThan(Duration.ZERO)) {
            this.connectionCache.invalidate((Object)connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T recordExecutionTime(SqlOperation<T> operation, String sql) throws SQLException {
        Ticker ticker = this.startTimingDatabaseOperation(sql);
        long startTime = System.currentTimeMillis();
        try {
            T t = operation.execute();
            return t;
        }
        finally {
            try {
                ticker.close();
                long endTime = System.currentTimeMillis();
                this.raiseAlertIfExecutionExceededThreshold(sql, Duration.ofMillis(endTime - startTime));
            }
            catch (Exception exception) {
                logger.error("Something threw an exception while completing timing of a DB operation. This logging is just a safety net so the application continues to work. The exception was: ", (Throwable)exception);
            }
        }
    }

    @Nonnull
    private Ticker startTimingDatabaseOperation(@Nullable String sql) {
        String pluginKeyFromOsgiStack = PluginKeyStack.getFirstPluginKey();
        Class<?>[] classContext = this.findInvoker && this.improvedAccuracy ? this.classContextSecurityManager.getClassContext() : null;
        Throwable throwable = this.findInvoker && classContext == null ? new Throwable() : null;
        String invokingPluginKey = pluginKeyFromOsgiStack;
        if (invokingPluginKey == null && classContext != null) {
            invokingPluginKey = this.pluginFinder.getInvokingPluginKeyFromClassContext(classContext);
        }
        if (invokingPluginKey == null && throwable != null) {
            invokingPluginKey = this.pluginFinder.getInvokingPluginKeyFromStackTrace(throwable.getStackTrace());
        }
        return Metrics.metric((String)"db.core.executionTime").optionalTag("sql", sql).invokerPluginKey(invokingPluginKey).withAnalytics().startLongRunningTimer();
    }

    private void raiseAlertIfExecutionExceededThreshold(String sql, Duration duration) {
        if (DurationUtils.durationOf(duration).isGreaterThanOrEqualTo(this.configuration.longRunningOperationLimit())) {
            DatabaseOperationDiagnostic diagnostic = new DatabaseOperationDiagnostic(sql, duration, Thread.currentThread().getName());
            this.databaseMonitor.raiseAlertForSlowOperation(Instant.now(), diagnostic);
        }
    }

    private static class ClassContextSecurityManager
    extends SecurityManager {
        private ClassContextSecurityManager() {
        }

        @Override
        @Nullable
        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

    private class LeakedConnectionListener
    implements RemovalListener<Connection, Instant> {
        private LeakedConnectionListener() {
        }

        public void onRemoval(RemovalNotification<Connection, Instant> notification) {
            DatabasePoolDiagnostic databasePoolDiagnostic;
            if (notification.getCause() == RemovalCause.EXPIRED && !(databasePoolDiagnostic = DefaultDatabaseDiagnosticsCollector.this.databasePoolDiagnosticProvider.getDiagnostic()).isEmpty()) {
                DefaultDatabaseDiagnosticsCollector.this.databaseMonitor.raiseAlertForConnectionLeak(DefaultDatabaseDiagnosticsCollector.this.clock.instant(), (Instant)notification.getValue(), databasePoolDiagnostic);
            }
        }
    }
}

