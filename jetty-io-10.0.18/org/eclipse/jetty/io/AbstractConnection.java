/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.Invocable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnection
implements Connection {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConnection.class);
    private final List<Connection.Listener> _listeners = new CopyOnWriteArrayList<Connection.Listener>();
    private final long _created = System.currentTimeMillis();
    private final EndPoint _endPoint;
    private final Executor _executor;
    private final Callback _readCallback;
    private int _inputBufferSize = 2048;

    protected AbstractConnection(EndPoint endPoint, Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("Executor must not be null!");
        }
        this._endPoint = endPoint;
        this._executor = executor;
        this._readCallback = new ReadCallback();
    }

    @Override
    public void addEventListener(EventListener listener) {
        if (listener instanceof Connection.Listener) {
            this._listeners.add((Connection.Listener)listener);
        }
    }

    @Override
    public void removeEventListener(EventListener listener) {
        this._listeners.remove(listener);
    }

    public int getInputBufferSize() {
        return this._inputBufferSize;
    }

    public void setInputBufferSize(int inputBufferSize) {
        this._inputBufferSize = inputBufferSize;
    }

    protected Executor getExecutor() {
        return this._executor;
    }

    protected void failedCallback(Callback callback, Throwable x) {
        Runnable failCallback = () -> {
            try {
                callback.failed(x);
            }
            catch (Exception e) {
                LOG.warn("Failed callback", x);
            }
        };
        switch (Invocable.getInvocationType((Object)callback)) {
            case BLOCKING: {
                try {
                    this.getExecutor().execute(failCallback);
                }
                catch (RejectedExecutionException e) {
                    LOG.debug("Rejected", (Throwable)e);
                    callback.failed(x);
                }
                break;
            }
            case NON_BLOCKING: {
                failCallback.run();
                break;
            }
            case EITHER: {
                Invocable.invokeNonBlocking((Runnable)failCallback);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    public void fillInterested() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("fillInterested {}", (Object)this);
        }
        this.getEndPoint().fillInterested(this._readCallback);
    }

    public void tryFillInterested(Callback callback) {
        this.getEndPoint().tryFillInterested(callback);
    }

    public boolean isFillInterested() {
        return this.getEndPoint().isFillInterested();
    }

    public abstract void onFillable();

    protected void onFillInterestedFailed(Throwable cause) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} onFillInterestedFailed {}", (Object)this, (Object)cause);
        }
        if (this._endPoint.isOpen()) {
            boolean close = true;
            if (cause instanceof TimeoutException) {
                close = this.onReadTimeout(cause);
            }
            if (close) {
                if (this._endPoint.isOutputShutdown()) {
                    this._endPoint.close();
                } else {
                    this._endPoint.shutdownOutput();
                    this.fillInterested();
                }
            }
        }
    }

    protected boolean onReadTimeout(Throwable timeout) {
        return true;
    }

    @Override
    public void onOpen() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("onOpen {}", (Object)this);
        }
        for (Connection.Listener listener : this._listeners) {
            this.onOpened(listener);
        }
    }

    private void onOpened(Connection.Listener listener) {
        try {
            listener.onOpened(this);
        }
        catch (Throwable x) {
            LOG.info("Failure while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    @Override
    public void onClose(Throwable cause) {
        if (LOG.isDebugEnabled()) {
            if (cause == null) {
                LOG.debug("onClose {}", (Object)this);
            } else {
                LOG.debug("onClose {}", (Object)this, (Object)cause);
            }
        }
        for (Connection.Listener listener : this._listeners) {
            this.onClosed(listener);
        }
    }

    private void onClosed(Connection.Listener listener) {
        try {
            listener.onClosed(this);
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.info("Failure while notifying listener {}", (Object)listener, (Object)x);
            }
            LOG.info("Failure while notifying listener {} {}", (Object)listener, (Object)x.toString());
        }
    }

    @Override
    public EndPoint getEndPoint() {
        return this._endPoint;
    }

    @Override
    public void close() {
        this.getEndPoint().close();
    }

    @Override
    public boolean onIdleExpired() {
        return true;
    }

    @Override
    public long getMessagesIn() {
        return -1L;
    }

    @Override
    public long getMessagesOut() {
        return -1L;
    }

    @Override
    public long getBytesIn() {
        return -1L;
    }

    @Override
    public long getBytesOut() {
        return -1L;
    }

    @Override
    public long getCreatedTimeStamp() {
        return this._created;
    }

    public final String toString() {
        return String.format("%s@%h::%s", this.getClass().getSimpleName(), this, this.getEndPoint());
    }

    public String toConnectionString() {
        return String.format("%s@%h", this.getClass().getSimpleName(), this);
    }

    private class ReadCallback
    implements Callback {
        private ReadCallback() {
        }

        public void succeeded() {
            AbstractConnection.this.onFillable();
        }

        public void failed(Throwable x) {
            AbstractConnection.this.onFillInterestedFailed(x);
        }

        public String toString() {
            return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), AbstractConnection.this);
        }
    }
}

