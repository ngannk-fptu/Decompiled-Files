/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.res.StringManager;

public class NioChannel
implements ByteChannel,
ScatteringByteChannel,
GatheringByteChannel {
    protected static final StringManager sm = StringManager.getManager(NioChannel.class);
    protected static final ByteBuffer emptyBuf = ByteBuffer.allocate(0);
    protected final SocketBufferHandler bufHandler;
    protected SocketChannel sc = null;
    protected NioEndpoint.NioSocketWrapper socketWrapper = null;
    private ApplicationBufferHandler appReadBufHandler;
    static final NioChannel CLOSED_NIO_CHANNEL = new NioChannel(SocketBufferHandler.EMPTY){

        @Override
        public void close() throws IOException {
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void reset(SocketChannel channel, NioEndpoint.NioSocketWrapper socketWrapper) throws IOException {
        }

        @Override
        public void free() {
        }

        @Override
        protected ApplicationBufferHandler getAppReadBufHandler() {
            return ApplicationBufferHandler.EMPTY;
        }

        @Override
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return -1;
        }

        @Override
        public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
            return -1L;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            this.checkInterruptStatus();
            throw new ClosedChannelException();
        }

        @Override
        public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
            throw new ClosedChannelException();
        }

        @Override
        public String toString() {
            return "Closed NioChannel";
        }
    };

    public NioChannel(SocketBufferHandler bufHandler) {
        this.bufHandler = bufHandler;
    }

    public void reset(SocketChannel channel, NioEndpoint.NioSocketWrapper socketWrapper) throws IOException {
        this.sc = channel;
        this.socketWrapper = socketWrapper;
        this.bufHandler.reset();
    }

    NioEndpoint.NioSocketWrapper getSocketWrapper() {
        return this.socketWrapper;
    }

    public void free() {
        this.bufHandler.free();
    }

    public boolean flush(boolean block, Selector s, long timeout) throws IOException {
        return true;
    }

    @Override
    public void close() throws IOException {
        this.sc.close();
    }

    public void close(boolean force) throws IOException {
        if (this.isOpen() || force) {
            this.close();
        }
    }

    @Override
    public boolean isOpen() {
        return this.sc.isOpen();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        this.checkInterruptStatus();
        return this.sc.write(src);
    }

    @Override
    public long write(ByteBuffer[] srcs) throws IOException {
        return this.write(srcs, 0, srcs.length);
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        this.checkInterruptStatus();
        return this.sc.write(srcs, offset, length);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return this.sc.read(dst);
    }

    @Override
    public long read(ByteBuffer[] dsts) throws IOException {
        return this.read(dsts, 0, dsts.length);
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return this.sc.read(dsts, offset, length);
    }

    public SocketBufferHandler getBufHandler() {
        return this.bufHandler;
    }

    public SocketChannel getIOChannel() {
        return this.sc;
    }

    public boolean isClosing() {
        return false;
    }

    public boolean isHandshakeComplete() {
        return true;
    }

    public int handshake(boolean read, boolean write) throws IOException {
        return 0;
    }

    public String toString() {
        return super.toString() + ":" + this.sc;
    }

    public int getOutboundRemaining() {
        return 0;
    }

    public boolean flushOutbound() throws IOException {
        return false;
    }

    protected void checkInterruptStatus() throws IOException {
        if (Thread.interrupted()) {
            throw new IOException(sm.getString("channel.nio.interrupted"));
        }
    }

    public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        this.appReadBufHandler = handler;
    }

    protected ApplicationBufferHandler getAppReadBufHandler() {
        return this.appReadBufHandler;
    }
}

