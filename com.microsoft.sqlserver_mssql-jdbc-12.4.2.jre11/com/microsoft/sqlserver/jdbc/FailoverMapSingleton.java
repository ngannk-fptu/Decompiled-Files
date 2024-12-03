/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.FailoverInfo;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

final class FailoverMapSingleton {
    private static int initialHashmapSize = 5;
    private static HashMap<String, FailoverInfo> failoverMap = new HashMap(initialHashmapSize);
    private static final Lock LOCK = new ReentrantLock();

    private FailoverMapSingleton() {
    }

    private static String concatPrimaryDatabase(String primary, String instance, String database) {
        StringBuilder buf = new StringBuilder();
        buf.append(primary);
        if (null != instance) {
            buf.append("\\");
            buf.append(instance);
        }
        buf.append(";");
        buf.append(database);
        return buf.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static FailoverInfo getFailoverInfo(SQLServerConnection connection, String primaryServer, String instance, String database) {
        LOCK.lock();
        try {
            FailoverInfo fo;
            if (failoverMap.isEmpty()) {
                FailoverInfo failoverInfo = null;
                return failoverInfo;
            }
            String mapKey = FailoverMapSingleton.concatPrimaryDatabase(primaryServer, instance, database);
            if (connection.getConnectionLogger().isLoggable(Level.FINER)) {
                connection.getConnectionLogger().finer(connection.toString() + " Looking up info in the map using key: " + mapKey);
            }
            if (null != (fo = failoverMap.get(mapKey))) {
                fo.log(connection);
            }
            FailoverInfo failoverInfo = fo;
            return failoverInfo;
        }
        finally {
            LOCK.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void putFailoverInfo(SQLServerConnection connection, String primaryServer, String instance, String database, FailoverInfo actualFailoverInfo, boolean actualuseFailover, String failoverPartner) {
        LOCK.lock();
        try {
            FailoverInfo fo = FailoverMapSingleton.getFailoverInfo(connection, primaryServer, instance, database);
            if (null == fo) {
                if (connection.getConnectionLogger().isLoggable(Level.FINE)) {
                    connection.getConnectionLogger().fine(connection.toString() + " Failover map add server: " + primaryServer + "; database:" + database + "; Mirror:" + failoverPartner);
                }
                failoverMap.put(FailoverMapSingleton.concatPrimaryDatabase(primaryServer, instance, database), actualFailoverInfo);
            } else {
                fo.failoverAdd(connection, actualuseFailover, failoverPartner);
            }
        }
        finally {
            LOCK.unlock();
        }
    }
}

