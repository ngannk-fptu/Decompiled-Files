/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import javax.websocket.CloseReason;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.AsyncChannelWrapper;
import org.apache.tomcat.websocket.ReadBufferOverflowException;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsFrameBase;
import org.apache.tomcat.websocket.WsIOException;
import org.apache.tomcat.websocket.WsSession;

public class WsFrameClient
extends WsFrameBase {
    private final Log log = LogFactory.getLog(WsFrameClient.class);
    private static final StringManager sm = StringManager.getManager(WsFrameClient.class);
    private final AsyncChannelWrapper channel;
    private final CompletionHandler<Integer, Void> handler;
    private volatile ByteBuffer response;

    public WsFrameClient(ByteBuffer response, AsyncChannelWrapper channel, WsSession wsSession, Transformation transformation) {
        super(wsSession, transformation);
        this.response = response;
        this.channel = channel;
        this.handler = new WsFrameClientCompletionHandler();
    }

    void startInputProcessing() {
        try {
            this.processSocketRead();
        }
        catch (IOException e) {
            this.close(e);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void processSocketRead() throws IOException {
        block4: while (true) {
            switch (this.getReadState()) {
                case WAITING: {
                    if (!this.changeReadState(WsFrameBase.ReadState.WAITING, WsFrameBase.ReadState.PROCESSING)) continue block4;
                    while (this.response.hasRemaining()) {
                        if (this.isSuspended()) {
                            if (!this.changeReadState(WsFrameBase.ReadState.SUSPENDING_PROCESS, WsFrameBase.ReadState.SUSPENDED)) continue;
                            return;
                        }
                        this.inputBuffer.mark();
                        this.inputBuffer.position(this.inputBuffer.limit()).limit(this.inputBuffer.capacity());
                        int toCopy = Math.min(this.response.remaining(), this.inputBuffer.remaining());
                        int orgLimit = this.response.limit();
                        this.response.limit(this.response.position() + toCopy);
                        this.inputBuffer.put(this.response);
                        this.response.limit(orgLimit);
                        this.inputBuffer.limit(this.inputBuffer.position()).reset();
                        this.processInputBuffer();
                    }
                    this.response.clear();
                    if (this.isOpen()) {
                        this.channel.read(this.response, null, this.handler);
                        return;
                    } else {
                        this.changeReadState(WsFrameBase.ReadState.CLOSING);
                    }
                    return;
                }
                case SUSPENDING_WAIT: {
                    if (this.changeReadState(WsFrameBase.ReadState.SUSPENDING_WAIT, WsFrameBase.ReadState.SUSPENDED)) return;
                    continue block4;
                }
            }
            break;
        }
        throw new IllegalStateException(sm.getString("wsFrameServer.illegalReadState", new Object[]{this.getReadState()}));
    }

    private void close(Throwable t) {
        this.changeReadState(WsFrameBase.ReadState.CLOSING);
        CloseReason cr = t instanceof WsIOException ? ((WsIOException)t).getCloseReason() : new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, t.getMessage());
        this.wsSession.doClose(cr, cr, true);
    }

    @Override
    protected boolean isMasked() {
        return false;
    }

    @Override
    protected Log getLog() {
        return this.log;
    }

    @Override
    protected void resumeProcessing() {
        this.resumeProcessing(true);
    }

    private void resumeProcessing(boolean checkOpenOnError) {
        try {
            this.processSocketRead();
        }
        catch (IOException e) {
            if (checkOpenOnError) {
                if (this.isOpen()) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)sm.getString("wsFrameClient.ioe"), (Throwable)e);
                    }
                    this.close(e);
                }
            }
            this.close(e);
        }
    }

    private class WsFrameClientCompletionHandler
    implements CompletionHandler<Integer, Void> {
        private WsFrameClientCompletionHandler() {
        }

        @Override
        public void completed(Integer result, Void attachment) {
            if (result == -1) {
                if (WsFrameClient.this.isOpen()) {
                    WsFrameClient.this.close(new EOFException());
                }
                return;
            }
            WsFrameClient.this.response.flip();
            this.doResumeProcessing(true);
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            if (WsFrameClient.this.log.isDebugEnabled()) {
                WsFrameClient.this.log.debug((Object)sm.getString("wsFrame.readFailed"), exc);
            }
            if (exc instanceof ReadBufferOverflowException) {
                WsFrameClient.this.response = ByteBuffer.allocate(((ReadBufferOverflowException)exc).getMinBufferSize());
                WsFrameClient.this.response.flip();
                this.doResumeProcessing(false);
            } else {
                WsFrameClient.this.close(exc);
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private void doResumeProcessing(boolean checkOpenOnError) {
            block4: while (true) {
                switch (WsFrameClient.this.getReadState()) {
                    case PROCESSING: {
                        if (!WsFrameClient.this.changeReadState(WsFrameBase.ReadState.PROCESSING, WsFrameBase.ReadState.WAITING)) continue block4;
                        WsFrameClient.this.resumeProcessing(checkOpenOnError);
                        return;
                    }
                    case SUSPENDING_PROCESS: {
                        if (WsFrameClient.this.changeReadState(WsFrameBase.ReadState.SUSPENDING_PROCESS, WsFrameBase.ReadState.SUSPENDED)) return;
                        continue block4;
                    }
                }
                break;
            }
            throw new IllegalStateException(sm.getString("wsFrame.illegalReadState", new Object[]{WsFrameClient.this.getReadState()}));
        }
    }
}

