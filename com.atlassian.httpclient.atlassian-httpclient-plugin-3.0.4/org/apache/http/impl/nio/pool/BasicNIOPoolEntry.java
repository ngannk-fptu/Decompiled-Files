/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.pool;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.pool.PoolEntry;

@Contract(threading=ThreadingBehavior.SAFE)
public class BasicNIOPoolEntry
extends PoolEntry<HttpHost, NHttpClientConnection> {
    private volatile int socketTimeout;

    public BasicNIOPoolEntry(String id, HttpHost route, NHttpClientConnection conn) {
        super(id, route, conn);
    }

    @Override
    public void close() {
        try {
            NHttpClientConnection connection = (NHttpClientConnection)this.getConnection();
            try {
                int socketTimeout = connection.getSocketTimeout();
                if (socketTimeout <= 0 || socketTimeout > 1000) {
                    connection.setSocketTimeout(1000);
                }
                connection.close();
            }
            catch (IOException ex) {
                connection.shutdown();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public boolean isClosed() {
        return !((NHttpClientConnection)this.getConnection()).isOpen();
    }

    int getSocketTimeout() {
        return this.socketTimeout;
    }

    void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}

