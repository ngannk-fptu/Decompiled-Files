/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.WebConnection
 *  javax.websocket.SendHandler
 *  javax.websocket.SendResult
 *  org.apache.coyote.http11.upgrade.UpgradeInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.net.SocketWrapperBase
 *  org.apache.tomcat.util.net.SocketWrapperBase$BlockingMode
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.WebConnection;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;
import org.apache.tomcat.websocket.server.WsServerContainer;
import org.apache.tomcat.websocket.server.WsWriteTimeout;

public class WsRemoteEndpointImplServer
extends WsRemoteEndpointImplBase {
    private static final StringManager sm = StringManager.getManager(WsRemoteEndpointImplServer.class);
    private final Log log = LogFactory.getLog(WsRemoteEndpointImplServer.class);
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private final WebConnection connection;
    private final WsWriteTimeout wsWriteTimeout;
    private volatile SendHandler handler = null;
    private volatile ByteBuffer[] buffers = null;
    private volatile long timeoutExpiry = -1L;

    public WsRemoteEndpointImplServer(SocketWrapperBase<?> socketWrapper, UpgradeInfo upgradeInfo, WsServerContainer serverContainer, WebConnection connection) {
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
        this.connection = connection;
        this.wsWriteTimeout = serverContainer.getTimeout();
    }

    @Override
    protected final boolean isMasked() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean acquireMessagePartInProgressSemaphore(byte opCode, long timeoutExpiry) throws InterruptedException {
        if (opCode != 8) {
            return super.acquireMessagePartInProgressSemaphore(opCode, timeoutExpiry);
        }
        int socketWrapperLockCount = this.socketWrapper.getLock() instanceof ReentrantLock ? ((ReentrantLock)this.socketWrapper.getLock()).getHoldCount() : 1;
        while (!this.messagePartInProgress.tryAcquire()) {
            int i;
            if (timeoutExpiry < System.currentTimeMillis()) {
                return false;
            }
            try {
                this.socketWrapper.setCurrentProcessor((Object)this.connection);
                for (i = 0; i < socketWrapperLockCount; ++i) {
                    this.socketWrapper.getLock().unlock();
                }
                Thread.yield();
            }
            finally {
                for (i = 0; i < socketWrapperLockCount; ++i) {
                    this.socketWrapper.getLock().lock();
                }
                this.socketWrapper.takeCurrentProcessor();
            }
        }
        return true;
    }

    @Override
    protected void doWrite(final SendHandler handler, final long blockingWriteTimeoutExpiry, ByteBuffer ... buffers) {
        if (this.socketWrapper.hasAsyncIO()) {
            final boolean block = blockingWriteTimeoutExpiry != -1L;
            long timeout = -1L;
            if (block) {
                timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                if (timeout <= 0L) {
                    SendResult sr = new SendResult((Throwable)new SocketTimeoutException());
                    handler.onResult(sr);
                    return;
                }
            } else {
                this.handler = handler;
                timeout = this.getSendTimeout();
                if (timeout > 0L) {
                    this.timeoutExpiry = timeout + System.currentTimeMillis();
                    this.wsWriteTimeout.register(this);
                }
            }
            this.socketWrapper.write(block ? SocketWrapperBase.BlockingMode.BLOCK : SocketWrapperBase.BlockingMode.SEMI_BLOCK, timeout, TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE_WITH_COMPLETION, (CompletionHandler)new CompletionHandler<Long, Void>(){

                @Override
                public void completed(Long result, Void attachment) {
                    if (block) {
                        long timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                        if (timeout <= 0L) {
                            this.failed((Throwable)new SocketTimeoutException(), null);
                        } else {
                            handler.onResult(SENDRESULT_OK);
                        }
                    } else {
                        WsRemoteEndpointImplServer.this.wsWriteTimeout.unregister(WsRemoteEndpointImplServer.this);
                        WsRemoteEndpointImplServer.this.clearHandler(null, true);
                    }
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    if (block) {
                        SendResult sr = new SendResult(exc);
                        handler.onResult(sr);
                    } else {
                        WsRemoteEndpointImplServer.this.wsWriteTimeout.unregister(WsRemoteEndpointImplServer.this);
                        WsRemoteEndpointImplServer.this.clearHandler(exc, true);
                        WsRemoteEndpointImplServer.this.close();
                    }
                }
            }, buffers);
        } else if (blockingWriteTimeoutExpiry == -1L) {
            this.handler = handler;
            this.buffers = buffers;
            this.onWritePossible(true);
        } else {
            try {
                for (ByteBuffer buffer : buffers) {
                    long timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                    if (timeout <= 0L) {
                        SendResult sr = new SendResult((Throwable)new SocketTimeoutException());
                        handler.onResult(sr);
                        return;
                    }
                    this.socketWrapper.setWriteTimeout(timeout);
                    this.socketWrapper.write(true, buffer);
                }
                long timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                if (timeout <= 0L) {
                    SendResult sr = new SendResult((Throwable)new SocketTimeoutException());
                    handler.onResult(sr);
                    return;
                }
                this.socketWrapper.setWriteTimeout(timeout);
                this.socketWrapper.flush(true);
                handler.onResult(SENDRESULT_OK);
            }
            catch (IOException e) {
                SendResult sr = new SendResult((Throwable)e);
                handler.onResult(sr);
            }
        }
    }

    @Override
    protected void updateStats(long payloadLength) {
        this.upgradeInfo.addMsgsSent(1L);
        this.upgradeInfo.addBytesSent(payloadLength);
    }

    public void onWritePossible(boolean useDispatch) {
        long timeout;
        ByteBuffer[] buffers = this.buffers;
        if (buffers == null) {
            return;
        }
        boolean complete = false;
        try {
            this.socketWrapper.flush(false);
            while (this.socketWrapper.isReadyForWrite()) {
                complete = true;
                for (ByteBuffer buffer : buffers) {
                    if (!buffer.hasRemaining()) continue;
                    complete = false;
                    this.socketWrapper.write(false, buffer);
                    break;
                }
                if (!complete) continue;
                this.socketWrapper.flush(false);
                complete = this.socketWrapper.isReadyForWrite();
                if (complete) {
                    this.wsWriteTimeout.unregister(this);
                    this.clearHandler(null, useDispatch);
                }
                break;
            }
        }
        catch (IOException | IllegalStateException e) {
            this.wsWriteTimeout.unregister(this);
            this.clearHandler(e, useDispatch);
            this.close();
        }
        if (!complete && (timeout = this.getSendTimeout()) > 0L) {
            this.timeoutExpiry = timeout + System.currentTimeMillis();
            this.wsWriteTimeout.register(this);
        }
    }

    @Override
    protected void doClose() {
        block3: {
            if (this.handler != null) {
                this.clearHandler(new EOFException(), true);
            }
            try {
                this.socketWrapper.close();
            }
            catch (Exception e) {
                if (!this.log.isInfoEnabled()) break block3;
                this.log.info((Object)sm.getString("wsRemoteEndpointServer.closeFailed"), (Throwable)e);
            }
        }
        this.wsWriteTimeout.unregister(this);
    }

    protected long getTimeoutExpiry() {
        return this.timeoutExpiry;
    }

    protected void onTimeout(boolean useDispatch) {
        if (this.handler != null) {
            this.clearHandler(new SocketTimeoutException(), useDispatch);
        }
        this.close();
    }

    @Override
    protected void setTransformation(Transformation transformation) {
        super.setTransformation(transformation);
    }

    void clearHandler(Throwable t, boolean useDispatch) {
        SendHandler sh = this.handler;
        this.handler = null;
        this.buffers = null;
        if (sh != null) {
            if (useDispatch) {
                OnResultRunnable r = new OnResultRunnable(sh, t);
                try {
                    this.socketWrapper.execute((Runnable)r);
                }
                catch (RejectedExecutionException ree) {
                    r.run();
                }
            } else if (t == null) {
                sh.onResult(new SendResult());
            } else {
                sh.onResult(new SendResult(t));
            }
        }
    }

    @Override
    protected Lock getLock() {
        return this.socketWrapper.getLock();
    }

    private static class OnResultRunnable
    implements Runnable {
        private final SendHandler sh;
        private final Throwable t;

        private OnResultRunnable(SendHandler sh, Throwable t) {
            this.sh = sh;
            this.t = t;
        }

        @Override
        public void run() {
            if (this.t == null) {
                this.sh.onResult(new SendResult());
            } else {
                this.sh.onResult(new SendResult(this.t));
            }
        }
    }
}

