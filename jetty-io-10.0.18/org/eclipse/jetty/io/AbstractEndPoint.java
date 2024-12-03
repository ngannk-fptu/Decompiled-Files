/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.FillInterest;
import org.eclipse.jetty.io.IdleTimeout;
import org.eclipse.jetty.io.WriteFlusher;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEndPoint
extends IdleTimeout
implements EndPoint {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEndPoint.class);
    private final AtomicReference<State> _state = new AtomicReference<State>(State.OPEN);
    private final long _created = System.currentTimeMillis();
    private volatile Connection _connection;
    private final FillInterest _fillInterest = new FillInterest(){

        @Override
        protected void needsFillInterest() throws IOException {
            AbstractEndPoint.this.needsFillInterest();
        }
    };
    private final WriteFlusher _writeFlusher = new WriteFlusher(this){

        @Override
        protected void onIncompleteFlush() {
            AbstractEndPoint.this.onIncompleteFlush();
        }
    };

    protected AbstractEndPoint(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        SocketAddress local = this.getLocalSocketAddress();
        if (local instanceof InetSocketAddress) {
            return (InetSocketAddress)local;
        }
        return null;
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        SocketAddress remote = this.getRemoteSocketAddress();
        if (remote instanceof InetSocketAddress) {
            return (InetSocketAddress)remote;
        }
        return null;
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return null;
    }

    /*
     * Unable to fully structure code
     */
    protected final void shutdownInput() {
        if (AbstractEndPoint.LOG.isDebugEnabled()) {
            AbstractEndPoint.LOG.debug("shutdownInput {}", (Object)this);
        }
        block10: while (true) {
            s = this._state.get();
            switch (3.$SwitchMap$org$eclipse$jetty$io$AbstractEndPoint$State[s.ordinal()]) {
                case 1: {
                    if (!this._state.compareAndSet(s, State.ISHUTTING)) continue block10;
                    try {
                        this.doShutdownInput();
                    }
                    finally {
                        if (!this._state.compareAndSet(State.ISHUTTING, State.ISHUT)) {
                            if (this._state.get() == State.CLOSED) {
                                this.doOnClose(null);
                            } else {
                                throw new IllegalStateException();
                            }
                        }
                    }
                    return;
                }
                case 2: 
                case 3: {
                    return;
                }
                case 4: {
                    if (!this._state.compareAndSet(s, State.CLOSED)) continue block10;
                    return;
                }
                case 5: {
                    if (this._state.compareAndSet(s, State.CLOSED)) ** break;
                    continue block10;
                    this.doOnClose(null);
                    return;
                }
                case 6: {
                    return;
                }
            }
            break;
        }
        throw new IllegalStateException(s.toString());
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public final void shutdownOutput() {
        if (AbstractEndPoint.LOG.isDebugEnabled()) {
            AbstractEndPoint.LOG.debug("shutdownOutput {}", (Object)this);
        }
        block10: while (true) {
            s = this._state.get();
            switch (3.$SwitchMap$org$eclipse$jetty$io$AbstractEndPoint$State[s.ordinal()]) {
                case 1: {
                    if (!this._state.compareAndSet(s, State.OSHUTTING)) continue block10;
                    try {
                        this.doShutdownOutput();
                    }
                    finally {
                        if (!this._state.compareAndSet(State.OSHUTTING, State.OSHUT)) {
                            if (this._state.get() == State.CLOSED) {
                                this.doOnClose(null);
                            } else {
                                throw new IllegalStateException();
                            }
                        }
                    }
                    return;
                }
                case 2: {
                    if (!this._state.compareAndSet(s, State.CLOSED)) continue block10;
                    return;
                }
                case 3: {
                    if (this._state.compareAndSet(s, State.CLOSED)) ** break;
                    continue block10;
                    this.doOnClose(null);
                    return;
                }
                case 4: 
                case 5: {
                    return;
                }
                case 6: {
                    return;
                }
            }
            break;
        }
        throw new IllegalStateException(s.toString());
    }

    @Override
    public final void close() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("close {}", (Object)this);
        }
        this.close(null);
    }

    /*
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @Override
    public final void close(Throwable failure) {
        State s;
        if (LOG.isDebugEnabled()) {
            LOG.debug("close({}) {}", (Object)failure, (Object)this);
        }
        block5: while (true) {
            s = this._state.get();
            switch (s) {
                case OPEN: 
                case ISHUT: 
                case OSHUT: {
                    if (!this._state.compareAndSet(s, State.CLOSED)) continue block5;
                    this.doOnClose(failure);
                    return;
                }
                case ISHUTTING: 
                case OSHUTTING: {
                    if (this._state.compareAndSet(s, State.CLOSED)) return;
                    continue block5;
                }
                case CLOSED: {
                    return;
                }
            }
            break;
        }
        throw new IllegalStateException(s.toString());
    }

    protected void doShutdownInput() {
    }

    protected void doShutdownOutput() {
    }

    private void doOnClose(Throwable failure) {
        try {
            this.doClose();
        }
        finally {
            if (failure == null) {
                this.onClose();
            } else {
                this.onClose(failure);
            }
        }
    }

    protected void doClose() {
    }

    @Override
    public boolean isOutputShutdown() {
        switch (this._state.get()) {
            case OSHUTTING: 
            case OSHUT: 
            case CLOSED: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInputShutdown() {
        switch (this._state.get()) {
            case ISHUTTING: 
            case ISHUT: 
            case CLOSED: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOpen() {
        return this._state.get() != State.CLOSED;
    }

    @Override
    public long getCreatedTimeStamp() {
        return this._created;
    }

    @Override
    public Connection getConnection() {
        return this._connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this._connection = connection;
    }

    protected void reset() {
        this._state.set(State.OPEN);
        this._writeFlusher.onClose();
        this._fillInterest.onClose();
    }

    @Override
    public void onOpen() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("onOpen {}", (Object)this);
        }
        if (this._state.get() != State.OPEN) {
            throw new IllegalStateException();
        }
    }

    @Override
    public final void onClose() {
        this.onClose(null);
    }

    @Override
    public void onClose(Throwable failure) {
        super.onClose();
        if (failure == null) {
            this._writeFlusher.onClose();
            this._fillInterest.onClose();
        } else {
            this._writeFlusher.onFail(failure);
            this._fillInterest.onFail(failure);
        }
    }

    @Override
    public void fillInterested(Callback callback) {
        this.notIdle();
        this._fillInterest.register(callback);
    }

    @Override
    public boolean tryFillInterested(Callback callback) {
        this.notIdle();
        return this._fillInterest.tryRegister(callback);
    }

    @Override
    public boolean isFillInterested() {
        return this._fillInterest.isInterested();
    }

    @Override
    public void write(Callback callback, ByteBuffer ... buffers) throws WritePendingException {
        this._writeFlusher.write(callback, buffers);
    }

    protected abstract void onIncompleteFlush();

    protected abstract void needsFillInterest() throws IOException;

    public FillInterest getFillInterest() {
        return this._fillInterest;
    }

    public WriteFlusher getWriteFlusher() {
        return this._writeFlusher;
    }

    @Override
    protected void onIdleExpired(TimeoutException timeout) {
        Connection connection = this._connection;
        if (connection != null && !connection.onIdleExpired()) {
            return;
        }
        boolean outputShutdown = this.isOutputShutdown();
        boolean inputShutdown = this.isInputShutdown();
        boolean fillFailed = this._fillInterest.onFail(timeout);
        boolean writeFailed = this._writeFlusher.onFail(timeout);
        if (this.isOpen() && (outputShutdown || inputShutdown) && !fillFailed && !writeFailed) {
            this.close();
        } else {
            LOG.debug("Ignored idle endpoint {}", (Object)this);
        }
    }

    @Override
    public void upgrade(Connection newConnection) {
        Connection oldConnection = this.getConnection();
        ByteBuffer buffer = oldConnection instanceof Connection.UpgradeFrom ? ((Connection.UpgradeFrom)((Object)oldConnection)).onUpgradeFrom() : null;
        oldConnection.onClose(null);
        oldConnection.getEndPoint().setConnection(newConnection);
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} upgrading from {} to {} with {}", new Object[]{this, oldConnection, newConnection, BufferUtil.toDetailString((ByteBuffer)buffer)});
        }
        if (BufferUtil.hasContent((ByteBuffer)buffer)) {
            if (newConnection instanceof Connection.UpgradeTo) {
                ((Connection.UpgradeTo)((Object)newConnection)).onUpgradeTo(buffer);
            } else {
                throw new IllegalStateException("Cannot upgrade: " + newConnection + " does not implement " + Connection.UpgradeTo.class.getName());
            }
        }
        newConnection.onOpen();
    }

    public String toString() {
        return String.format("%s@%x[%s]->[%s]", this.getClass().getSimpleName(), this.hashCode(), this.toEndPointString(), this.toConnectionString());
    }

    public String toEndPointString() {
        return String.format("{l=%s,r=%s,%s,fill=%s,flush=%s,to=%d/%d}", new Object[]{this.getLocalSocketAddress(), this.getRemoteSocketAddress(), this._state.get(), this._fillInterest.toStateString(), this._writeFlusher.toStateString(), this.getIdleFor(), this.getIdleTimeout()});
    }

    public String toConnectionString() {
        Connection connection = this.getConnection();
        if (connection == null) {
            return "<null>";
        }
        if (connection instanceof AbstractConnection) {
            return ((AbstractConnection)connection).toConnectionString();
        }
        return String.format("%s@%x", connection.getClass().getSimpleName(), connection.hashCode());
    }

    private static enum State {
        OPEN,
        ISHUTTING,
        ISHUT,
        OSHUTTING,
        OSHUT,
        CLOSED;

    }
}

