/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.util.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NegotiatingClientConnection
extends AbstractConnection {
    private static final Logger LOG = LoggerFactory.getLogger(NegotiatingClientConnection.class);
    private final SSLEngine engine;
    private final ClientConnectionFactory connectionFactory;
    private final Map<String, Object> context;
    private String protocol;
    private volatile boolean completed;

    protected NegotiatingClientConnection(EndPoint endPoint, Executor executor, SSLEngine sslEngine, ClientConnectionFactory connectionFactory, Map<String, Object> context) {
        super(endPoint, executor);
        this.engine = sslEngine;
        this.connectionFactory = connectionFactory;
        this.context = context;
    }

    public SSLEngine getSSLEngine() {
        return this.engine;
    }

    public String getProtocol() {
        return this.protocol;
    }

    protected void completed(String protocol) {
        this.protocol = protocol;
        this.completed = true;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        try {
            this.getEndPoint().flush(BufferUtil.EMPTY_BUFFER);
            if (this.completed) {
                this.replaceConnection();
            } else {
                this.fillInterested();
            }
        }
        catch (Throwable x) {
            this.close();
            throw new RuntimeIOException(x);
        }
    }

    @Override
    public void onFillable() {
        block1: {
            int filled;
            do {
                filled = this.fill();
                if (!this.completed && filled >= 0) continue;
                this.replaceConnection();
                break block1;
            } while (filled != 0);
            this.fillInterested();
        }
    }

    private int fill() {
        try {
            return this.getEndPoint().fill(BufferUtil.EMPTY_BUFFER);
        }
        catch (IOException x) {
            LOG.debug("Unable to fill from endpoint", (Throwable)x);
            this.close();
            return -1;
        }
    }

    private void replaceConnection() {
        EndPoint endPoint = this.getEndPoint();
        try {
            endPoint.upgrade(this.connectionFactory.newConnection(endPoint, this.context));
        }
        catch (Throwable x) {
            LOG.debug("Unable to replace connection", x);
            this.close();
        }
    }

    @Override
    public void close() {
        this.getEndPoint().shutdownOutput();
        super.close();
    }
}

