/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.AbstractConnection
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IteratingCallback
 *  org.eclipse.jetty.util.IteratingCallback$Action
 *  org.slf4j.Logger
 */
package org.eclipse.jetty.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;
import org.slf4j.Logger;

public abstract class ProxyConnection
extends AbstractConnection {
    protected static final Logger LOG = ConnectHandler.LOG;
    private final IteratingCallback pipe = new ProxyIteratingCallback();
    private final ByteBufferPool bufferPool;
    private final ConcurrentMap<String, Object> context;
    private ProxyConnection connection;

    protected ProxyConnection(EndPoint endp, Executor executor, ByteBufferPool bufferPool, ConcurrentMap<String, Object> context) {
        super(endp, executor);
        this.bufferPool = bufferPool;
        this.context = context;
    }

    public ByteBufferPool getByteBufferPool() {
        return this.bufferPool;
    }

    public ConcurrentMap<String, Object> getContext() {
        return this.context;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(ProxyConnection connection) {
        this.connection = connection;
    }

    public void onFillable() {
        this.pipe.iterate();
    }

    protected abstract int read(EndPoint var1, ByteBuffer var2) throws IOException;

    protected abstract void write(EndPoint var1, ByteBuffer var2, Callback var3);

    protected void close(Throwable failure) {
        this.getEndPoint().close(failure);
    }

    public String toConnectionString() {
        EndPoint endPoint = this.getEndPoint();
        return String.format("%s@%x[l:%s<=>r:%s]", ((Object)((Object)this)).getClass().getSimpleName(), ((Object)((Object)this)).hashCode(), endPoint.getLocalSocketAddress(), endPoint.getRemoteSocketAddress());
    }

    private class ProxyIteratingCallback
    extends IteratingCallback {
        private ByteBuffer buffer;
        private int filled;

        private ProxyIteratingCallback() {
        }

        protected IteratingCallback.Action process() {
            this.buffer = ProxyConnection.this.bufferPool.acquire(ProxyConnection.this.getInputBufferSize(), true);
            try {
                int filled = this.filled = ProxyConnection.this.read(ProxyConnection.this.getEndPoint(), this.buffer);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} filled {} bytes", (Object)ProxyConnection.this, (Object)filled);
                }
                if (filled > 0) {
                    ProxyConnection.this.write(ProxyConnection.this.connection.getEndPoint(), this.buffer, (Callback)this);
                    return IteratingCallback.Action.SCHEDULED;
                }
                if (filled == 0) {
                    ProxyConnection.this.bufferPool.release(this.buffer);
                    ProxyConnection.this.fillInterested();
                    return IteratingCallback.Action.IDLE;
                }
                ProxyConnection.this.bufferPool.release(this.buffer);
                ProxyConnection.this.connection.getEndPoint().shutdownOutput();
                return IteratingCallback.Action.SUCCEEDED;
            }
            catch (IOException x) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} could not fill", (Object)ProxyConnection.this, (Object)x);
                }
                ProxyConnection.this.bufferPool.release(this.buffer);
                this.disconnect(x);
                return IteratingCallback.Action.SUCCEEDED;
            }
        }

        public void succeeded() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} wrote {} bytes", (Object)ProxyConnection.this, (Object)this.filled);
            }
            ProxyConnection.this.bufferPool.release(this.buffer);
            super.succeeded();
        }

        protected void onCompleteSuccess() {
        }

        protected void onCompleteFailure(Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} failed to write {} bytes", new Object[]{ProxyConnection.this, this.filled, x});
            }
            ProxyConnection.this.bufferPool.release(this.buffer);
            this.disconnect(x);
        }

        private void disconnect(Throwable x) {
            ProxyConnection.this.close(x);
            ProxyConnection.this.connection.close(x);
        }
    }
}

