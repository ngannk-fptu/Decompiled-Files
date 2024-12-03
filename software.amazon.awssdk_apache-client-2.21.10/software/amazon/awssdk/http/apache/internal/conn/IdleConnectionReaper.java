/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 */
package software.amazon.awssdk.http.apache.internal.conn;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;

@SdkInternalApi
public final class IdleConnectionReaper {
    private static final Logger log = LoggerFactory.getLogger(IdleConnectionReaper.class);
    private static final IdleConnectionReaper INSTANCE = new IdleConnectionReaper();
    private final Map<HttpClientConnectionManager, Long> connectionManagers;
    private final Supplier<ExecutorService> executorServiceSupplier;
    private final long sleepPeriod;
    private volatile ExecutorService exec;
    private volatile ReaperTask reaperTask;

    private IdleConnectionReaper() {
        this.connectionManagers = Collections.synchronizedMap(new WeakHashMap());
        this.executorServiceSupplier = () -> {
            ExecutorService e = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "idle-connection-reaper");
                t.setDaemon(true);
                return t;
            });
            return e;
        };
        this.sleepPeriod = Duration.ofMinutes(1L).toMillis();
    }

    @SdkTestInternalApi
    IdleConnectionReaper(Map<HttpClientConnectionManager, Long> connectionManagers, Supplier<ExecutorService> executorServiceSupplier, long sleepPeriod) {
        this.connectionManagers = connectionManagers;
        this.executorServiceSupplier = executorServiceSupplier;
        this.sleepPeriod = sleepPeriod;
    }

    public synchronized boolean registerConnectionManager(HttpClientConnectionManager manager, long maxIdleTime) {
        boolean notPreviouslyRegistered = this.connectionManagers.put(manager, maxIdleTime) == null;
        this.setupExecutorIfNecessary();
        return notPreviouslyRegistered;
    }

    public synchronized boolean deregisterConnectionManager(HttpClientConnectionManager manager) {
        boolean wasRemoved = this.connectionManagers.remove(manager) != null;
        this.cleanupExecutorIfNecessary();
        return wasRemoved;
    }

    public static IdleConnectionReaper getInstance() {
        return INSTANCE;
    }

    private void setupExecutorIfNecessary() {
        if (this.exec != null) {
            return;
        }
        ExecutorService e = this.executorServiceSupplier.get();
        this.reaperTask = new ReaperTask(this.connectionManagers, this.sleepPeriod);
        e.execute(this.reaperTask);
        this.exec = e;
    }

    private void cleanupExecutorIfNecessary() {
        if (this.exec == null || !this.connectionManagers.isEmpty()) {
            return;
        }
        this.reaperTask.stop();
        this.reaperTask = null;
        this.exec.shutdownNow();
        this.exec = null;
    }

    private static final class ReaperTask
    implements Runnable {
        private final Map<HttpClientConnectionManager, Long> connectionManagers;
        private final long sleepPeriod;
        private volatile boolean stopping = false;

        private ReaperTask(Map<HttpClientConnectionManager, Long> connectionManagers, long sleepPeriod) {
            this.connectionManagers = connectionManagers;
            this.sleepPeriod = sleepPeriod;
        }

        @Override
        public void run() {
            while (!this.stopping) {
                try {
                    Thread.sleep(this.sleepPeriod);
                    for (Map.Entry<HttpClientConnectionManager, Long> entry : this.connectionManagers.entrySet()) {
                        try {
                            entry.getKey().closeIdleConnections(entry.getValue().longValue(), TimeUnit.MILLISECONDS);
                        }
                        catch (Exception t) {
                            log.warn("Unable to close idle connections", (Throwable)t);
                        }
                    }
                }
                catch (Throwable t) {
                    log.debug("Reaper thread: ", t);
                }
            }
            log.debug("Shutting down reaper thread.");
        }

        private void stop() {
            this.stopping = true;
        }
    }
}

