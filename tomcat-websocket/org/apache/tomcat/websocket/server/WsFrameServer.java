/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.http11.upgrade.UpgradeInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState
 *  org.apache.tomcat.util.net.SocketEvent
 *  org.apache.tomcat.util.net.SocketWrapperBase
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.server;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsFrameBase;
import org.apache.tomcat.websocket.WsIOException;
import org.apache.tomcat.websocket.WsSession;

public class WsFrameServer
extends WsFrameBase {
    private final Log log = LogFactory.getLog(WsFrameServer.class);
    private static final StringManager sm = StringManager.getManager(WsFrameServer.class);
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private final ClassLoader applicationClassLoader;

    public WsFrameServer(SocketWrapperBase<?> socketWrapper, UpgradeInfo upgradeInfo, WsSession wsSession, Transformation transformation, ClassLoader applicationClassLoader) {
        super(wsSession, transformation);
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
        this.applicationClassLoader = applicationClassLoader;
    }

    private void onDataAvailable() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"wsFrameServer.onDataAvailable");
        }
        if (this.isOpen() && this.inputBuffer.hasRemaining() && !this.isSuspended()) {
            this.processInputBuffer();
        }
        while (this.isOpen() && !this.isSuspended()) {
            this.inputBuffer.mark();
            this.inputBuffer.position(this.inputBuffer.limit()).limit(this.inputBuffer.capacity());
            int read = this.socketWrapper.read(false, this.inputBuffer);
            this.inputBuffer.limit(this.inputBuffer.position()).reset();
            if (read < 0 || this.socketWrapper.isClosed()) {
                throw new EOFException();
            }
            if (read == 0) {
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("wsFrameServer.bytesRead", new Object[]{Integer.toString(read)}));
            }
            this.processInputBuffer();
        }
    }

    @Override
    protected void updateStats(long payloadLength) {
        this.upgradeInfo.addMsgsReceived(1L);
        this.upgradeInfo.addBytesReceived(payloadLength);
    }

    @Override
    protected boolean isMasked() {
        return true;
    }

    @Override
    protected Transformation getTransformation() {
        return super.getTransformation();
    }

    @Override
    protected boolean isOpen() {
        return super.isOpen();
    }

    @Override
    protected Log getLog() {
        return this.log;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void sendMessageText(boolean last) throws WsIOException {
        Thread currentThread = Thread.currentThread();
        ClassLoader cl = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.applicationClassLoader);
            super.sendMessageText(last);
        }
        finally {
            currentThread.setContextClassLoader(cl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void sendMessageBinary(ByteBuffer msg, boolean last) throws WsIOException {
        Thread currentThread = Thread.currentThread();
        ClassLoader cl = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.applicationClassLoader);
            super.sendMessageBinary(msg, last);
        }
        finally {
            currentThread.setContextClassLoader(cl);
        }
    }

    @Override
    protected void resumeProcessing() {
        this.socketWrapper.processSocket(SocketEvent.OPEN_READ, true);
    }

    AbstractEndpoint.Handler.SocketState notifyDataAvailable() throws IOException {
        block6: while (this.isOpen()) {
            switch (this.getReadState()) {
                case WAITING: {
                    if (!this.changeReadState(WsFrameBase.ReadState.WAITING, WsFrameBase.ReadState.PROCESSING)) continue block6;
                    try {
                        return this.doOnDataAvailable();
                    }
                    catch (IOException e) {
                        this.changeReadState(WsFrameBase.ReadState.CLOSING);
                        throw e;
                    }
                }
                case SUSPENDING_WAIT: {
                    if (!this.changeReadState(WsFrameBase.ReadState.SUSPENDING_WAIT, WsFrameBase.ReadState.SUSPENDED)) continue block6;
                    return AbstractEndpoint.Handler.SocketState.SUSPENDED;
                }
            }
            throw new IllegalStateException(sm.getString("wsFrameServer.illegalReadState", new Object[]{this.getReadState()}));
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    private AbstractEndpoint.Handler.SocketState doOnDataAvailable() throws IOException {
        this.onDataAvailable();
        block4: while (this.isOpen()) {
            switch (this.getReadState()) {
                case PROCESSING: {
                    if (!this.changeReadState(WsFrameBase.ReadState.PROCESSING, WsFrameBase.ReadState.WAITING)) continue block4;
                    return AbstractEndpoint.Handler.SocketState.UPGRADED;
                }
                case SUSPENDING_PROCESS: {
                    if (!this.changeReadState(WsFrameBase.ReadState.SUSPENDING_PROCESS, WsFrameBase.ReadState.SUSPENDED)) continue block4;
                    return AbstractEndpoint.Handler.SocketState.SUSPENDED;
                }
            }
            throw new IllegalStateException(sm.getString("wsFrameServer.illegalReadState", new Object[]{this.getReadState()}));
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
}

