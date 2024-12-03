/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.google.errorprone.annotations.concurrent.GuardedBy
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promises
 *  io.atlassian.util.concurrent.TimedOutException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.bootstrap.HeartbeatChecker;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyExecutorServiceProvider;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import io.atlassian.util.concurrent.TimedOutException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultSynchronyMonitor
implements SynchronyMonitor {
    private static final Logger log = LoggerFactory.getLogger(DefaultSynchronyMonitor.class);
    private static final long DEFAULT_SYNCHRONY_HEARTBEAT_TIMEOUT_MILLIS = Long.getLong("confluence.synchrony.heartbeat.timeout.millis", 30000L);
    private static final int DEFAULT_HEARTBEAT_DELAY_MILLIS = 3000;
    private final ExecutorService executorService;
    private final BooleanSupplier heartbeatChecker;
    private final LongSupplier timeSupplier;
    private final long heartbeatTimeoutMillis;
    private final int heartbeatDelayMillis;
    private final AtomicReference<Future<Boolean>> heartbeatFuture = new AtomicReference();
    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public DefaultSynchronyMonitor(@ComponentImport SynchronyConfigurationManager configurationManager, SynchronyExecutorServiceProvider executorServiceProvider, @ComponentImport RequestFactory<?> requestFactory) {
        this(executorServiceProvider, new HeartbeatChecker(configurationManager, requestFactory)::isSynchronyUp, System::nanoTime, DEFAULT_SYNCHRONY_HEARTBEAT_TIMEOUT_MILLIS, 3000);
    }

    @VisibleForTesting
    DefaultSynchronyMonitor(SynchronyExecutorServiceProvider executorServiceProvider, BooleanSupplier heartbeatChecker, LongSupplier timeSupplier, long heartbeatTimeoutMillis, int heartbeatDelayMillis) {
        this.executorService = executorServiceProvider.getExecutorService();
        this.heartbeatChecker = heartbeatChecker;
        this.timeSupplier = timeSupplier;
        this.heartbeatTimeoutMillis = heartbeatTimeoutMillis;
        this.heartbeatDelayMillis = heartbeatDelayMillis;
    }

    @Override
    public boolean isSynchronyUp() {
        return this.heartbeatChecker.getAsBoolean();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Promise<Boolean> pollHeartbeat() {
        try {
            log.debug("Starting acquire lock to poll synchrony heartbeat");
            if (!this.lock.tryLock(this.heartbeatTimeoutMillis, TimeUnit.MILLISECONDS)) {
                log.warn("Failed to acquire a lock to poll synchrony heartbeat");
                return Promises.promise((Object)false);
            }
            try {
                log.debug("Acquired lock to poll synchrony heartbeat");
                this.cancelHeartbeat();
                Promise<Boolean> promise = this.pollHeartbeat(this.timeSupplier.getAsLong());
                return promise;
            }
            finally {
                this.lock.unlock();
                log.debug("Lock released after synchrony heartbeat poll created");
            }
        }
        catch (InterruptedException e) {
            log.warn("Thread was interrupted during attempt to acquire a lock to poll synchrony heartbeat");
            Thread.currentThread().interrupt();
            return Promises.promise((Object)false);
        }
    }

    @Override
    public void cancelHeartbeat() {
        block7: {
            try {
                log.debug("Starting acquire lock to cancel synchrony heartbeat");
                if (this.lock.tryLock(this.heartbeatTimeoutMillis, TimeUnit.MILLISECONDS)) {
                    try {
                        log.debug("Acquired lock to cancel synchrony heartbeat");
                        Future<Boolean> future = this.heartbeatFuture.get();
                        if (future != null && !future.isDone() && future.cancel(true)) {
                            log.debug("Cancelled existing Synchrony heartbeat poll.");
                        }
                        this.heartbeatFuture.set(null);
                        break block7;
                    }
                    finally {
                        this.lock.unlock();
                        log.debug("Lock released after synchrony heartbeat cancelled");
                    }
                }
                log.warn("Failed to acquire a lock to cancel synchrony heartbeat");
            }
            catch (InterruptedException e) {
                log.warn("Thread was interrupted during attempt to acquire a lock to cancel synchrony heartbeat check");
                Thread.currentThread().interrupt();
            }
        }
    }

    @GuardedBy(value="lock")
    private Promise<Boolean> pollHeartbeat(long heartbeatStartTime) {
        if (this.isSynchronyUp()) {
            return Promises.promise((Object)true);
        }
        Future<Boolean> future = this.executorService.submit(() -> {
            while (true) {
                long currentTime;
                if ((currentTime = this.timeSupplier.getAsLong()) - heartbeatStartTime > TimeUnit.NANOSECONDS.convert(this.heartbeatTimeoutMillis, TimeUnit.MILLISECONDS)) {
                    throw new TimedOutException(currentTime - heartbeatStartTime, TimeUnit.NANOSECONDS);
                }
                if (this.isSynchronyUp()) {
                    return true;
                }
                log.debug("Rescheduling another heartbeat check in {} milliseconds.", (Object)this.heartbeatDelayMillis);
                Thread.sleep(this.heartbeatDelayMillis);
            }
        });
        this.heartbeatFuture.set(future);
        return Promises.forFuture(future, (Executor)this.executorService).recover(e -> {
            if (e instanceof CancellationException) {
                log.debug("Rescheduled heartbeat check was canceled by another action. This happens when a heartbeat hasn't finished yet but somebody else has already called startup() or restart() again.");
            } else {
                log.warn("Rescheduled heartbeat check failed.", e);
            }
            return false;
        });
    }
}

