/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdleConnectionHandler {
    private static final Log LOG = LogFactory.getLog(IdleConnectionHandler.class);
    private Map connectionToAdded = new HashMap();

    public void add(HttpConnection connection) {
        Long timeAdded = new Long(System.currentTimeMillis());
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Adding connection at: " + timeAdded));
        }
        this.connectionToAdded.put(connection, timeAdded);
    }

    public void remove(HttpConnection connection) {
        this.connectionToAdded.remove(connection);
    }

    public void removeAll() {
        this.connectionToAdded.clear();
    }

    public void closeIdleConnections(long idleTime) {
        long idleTimeout = System.currentTimeMillis() - idleTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Checking for connections, idleTimeout: " + idleTimeout));
        }
        Iterator connectionIter = this.connectionToAdded.keySet().iterator();
        while (connectionIter.hasNext()) {
            HttpConnection conn = (HttpConnection)connectionIter.next();
            Long connectionTime = (Long)this.connectionToAdded.get(conn);
            if (connectionTime > idleTimeout) continue;
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Closing connection, connection time: " + connectionTime));
            }
            connectionIter.remove();
            conn.close();
        }
    }
}

