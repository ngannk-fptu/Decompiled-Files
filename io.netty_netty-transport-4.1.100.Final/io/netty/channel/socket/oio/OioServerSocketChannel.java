/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.SocketUtils
 *  io.netty.util.internal.logging.InternalLogger
 *  io.netty.util.internal.logging.InternalLoggerFactory
 */
package io.netty.channel.socket.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.oio.DefaultOioServerSocketChannelConfig;
import io.netty.channel.socket.oio.OioServerSocketChannelConfig;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.List;

@Deprecated
public class OioServerSocketChannel
extends AbstractOioMessageChannel
implements ServerSocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioServerSocketChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 1);
    final ServerSocket socket;
    private final OioServerSocketChannelConfig config;

    private static ServerSocket newServerSocket() {
        try {
            return new ServerSocket();
        }
        catch (IOException e) {
            throw new ChannelException("failed to create a server socket", e);
        }
    }

    public OioServerSocketChannel() {
        this(OioServerSocketChannel.newServerSocket());
    }

    public OioServerSocketChannel(ServerSocket socket) {
        super(null);
        ObjectUtil.checkNotNull((Object)socket, (String)"socket");
        boolean success = false;
        try {
            socket.setSoTimeout(1000);
            success = true;
        }
        catch (IOException e) {
            throw new ChannelException("Failed to set the server socket timeout.", e);
        }
        finally {
            block11: {
                if (!success) {
                    try {
                        socket.close();
                    }
                    catch (IOException e) {
                        if (!logger.isWarnEnabled()) break block11;
                        logger.warn("Failed to close a partially initialized socket.", (Throwable)e);
                    }
                }
            }
        }
        this.socket = socket;
        this.config = new DefaultOioServerSocketChannelConfig(this, socket);
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public OioServerSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return !this.socket.isClosed();
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && this.socket.isBound();
    }

    @Override
    protected SocketAddress localAddress0() {
        return SocketUtils.localSocketAddress((ServerSocket)this.socket);
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.socket.bind(localAddress, this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        this.socket.close();
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        if (this.socket.isClosed()) {
            return -1;
        }
        try {
            Socket s = this.socket.accept();
            try {
                buf.add(new OioSocketChannel((Channel)this, s));
                return 1;
            }
            catch (Throwable t) {
                logger.warn("Failed to create a new channel from an accepted socket.", t);
                try {
                    s.close();
                }
                catch (Throwable t2) {
                    logger.warn("Failed to close a socket.", t2);
                }
            }
        }
        catch (SocketTimeoutException socketTimeoutException) {
            // empty catch block
        }
        return 0;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    protected void setReadPending(boolean readPending) {
        super.setReadPending(readPending);
    }

    final void clearReadPending0() {
        super.clearReadPending();
    }
}

