/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.nio.client.AbstractClientExchangeHandler;
import org.apache.http.impl.nio.client.CloseableHttpPipeliningClient;
import org.apache.http.impl.nio.client.InternalIODispatch;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.conn.NHttpClientConnectionManager;

abstract class CloseableHttpAsyncClientBase
extends CloseableHttpPipeliningClient {
    private final Log log = LogFactory.getLog(this.getClass());
    private final NHttpClientConnectionManager connmgr;
    private final Thread reactorThread;
    private final AtomicReference<Status> status;

    public CloseableHttpAsyncClientBase(final NHttpClientConnectionManager connmgr, ThreadFactory threadFactory, final NHttpClientEventHandler handler) {
        this.connmgr = connmgr;
        this.reactorThread = threadFactory != null && handler != null ? threadFactory.newThread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    InternalIODispatch ioEventDispatch = new InternalIODispatch(handler);
                    connmgr.execute(ioEventDispatch);
                }
                catch (Exception ex) {
                    CloseableHttpAsyncClientBase.this.log.error("I/O reactor terminated abnormally", ex);
                }
                finally {
                    CloseableHttpAsyncClientBase.this.status.set(Status.STOPPED);
                }
            }
        }) : null;
        this.status = new AtomicReference<Status>(Status.INACTIVE);
    }

    @Override
    public void start() {
        if (this.status.compareAndSet(Status.INACTIVE, Status.ACTIVE) && this.reactorThread != null) {
            this.reactorThread.start();
        }
    }

    @Override
    public void close() {
        if (this.status.compareAndSet(Status.ACTIVE, Status.STOPPED) && this.reactorThread != null) {
            try {
                this.connmgr.shutdown();
            }
            catch (IOException ex) {
                this.log.error("I/O error shutting down connection manager", ex);
            }
            try {
                this.reactorThread.join();
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return this.status.get() == Status.ACTIVE;
    }

    final void execute(AbstractClientExchangeHandler handler) {
        try {
            if (!this.isRunning()) {
                throw new CancellationException("Request execution cancelled");
            }
            handler.start();
        }
        catch (Exception ex) {
            handler.failed(ex);
        }
    }

    static enum Status {
        INACTIVE,
        ACTIVE,
        STOPPED;

    }
}

