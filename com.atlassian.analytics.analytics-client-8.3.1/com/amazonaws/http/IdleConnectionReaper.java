/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.conn.HttpClientConnectionManager
 */
package com.amazonaws.http;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.HttpClientConnectionManager;

@SdkInternalApi
public final class IdleConnectionReaper
extends Thread {
    private static final Log LOG = LogFactory.getLog(IdleConnectionReaper.class);
    private static final int PERIOD_MILLISECONDS = 60000;
    @Deprecated
    private static final int DEFAULT_MAX_IDLE_MILLIS = 60000;
    private static final Map<HttpClientConnectionManager, Long> connectionManagers = new ConcurrentHashMap<HttpClientConnectionManager, Long>();
    private static volatile IdleConnectionReaper instance;
    private volatile boolean shuttingDown;

    private IdleConnectionReaper() {
        super("java-sdk-http-connection-reaper");
        this.setDaemon(true);
    }

    @Deprecated
    public static boolean registerConnectionManager(HttpClientConnectionManager connectionManager) {
        return IdleConnectionReaper.registerConnectionManager(connectionManager, 60000L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static boolean registerConnectionManager(HttpClientConnectionManager connectionManager, long maxIdleInMs) {
        if (instance == null) {
            Class<IdleConnectionReaper> clazz = IdleConnectionReaper.class;
            // MONITORENTER : com.amazonaws.http.IdleConnectionReaper.class
            if (instance == null) {
                instance = new IdleConnectionReaper();
                instance.start();
            }
            // MONITOREXIT : clazz
        }
        if (connectionManagers.put(connectionManager, maxIdleInMs) != null) return false;
        return true;
    }

    public static boolean removeConnectionManager(HttpClientConnectionManager connectionManager) {
        boolean wasRemoved;
        boolean bl = wasRemoved = connectionManagers.remove(connectionManager) != null;
        if (connectionManagers.isEmpty()) {
            IdleConnectionReaper.shutdown();
        }
        return wasRemoved;
    }

    @SdkTestInternalApi
    public static List<HttpClientConnectionManager> getRegisteredConnectionManagers() {
        return new ArrayList<HttpClientConnectionManager>(connectionManagers.keySet());
    }

    public static synchronized boolean shutdown() {
        if (instance != null) {
            instance.markShuttingDown();
            instance.interrupt();
            connectionManagers.clear();
            instance = null;
            return true;
        }
        return false;
    }

    static int size() {
        return connectionManagers.size();
    }

    private void markShuttingDown() {
        this.shuttingDown = true;
    }

    @Override
    public void run() {
        while (!this.shuttingDown) {
            try {
                for (Map.Entry<HttpClientConnectionManager, Long> entry : connectionManagers.entrySet()) {
                    try {
                        entry.getKey().closeIdleConnections(entry.getValue().longValue(), TimeUnit.MILLISECONDS);
                    }
                    catch (Exception t) {
                        LOG.warn((Object)"Unable to close idle connections", (Throwable)t);
                    }
                }
                Thread.sleep(60000L);
            }
            catch (Throwable t) {
                LOG.debug((Object)"Reaper thread: ", t);
            }
        }
        LOG.debug((Object)"Shutting down reaper thread.");
    }
}

