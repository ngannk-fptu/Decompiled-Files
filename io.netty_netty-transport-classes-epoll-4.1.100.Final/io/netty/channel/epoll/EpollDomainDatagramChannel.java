/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.AddressedEnvelope
 *  io.netty.channel.ChannelConfig
 *  io.netty.channel.ChannelMetadata
 *  io.netty.channel.ChannelOutboundBuffer
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.DefaultAddressedEnvelope
 *  io.netty.channel.unix.DomainDatagramChannel
 *  io.netty.channel.unix.DomainDatagramPacket
 *  io.netty.channel.unix.DomainDatagramSocketAddress
 *  io.netty.channel.unix.DomainSocketAddress
 *  io.netty.channel.unix.IovArray
 *  io.netty.channel.unix.PeerCredentials
 *  io.netty.channel.unix.UnixChannelUtil
 *  io.netty.util.CharsetUtil
 *  io.netty.util.UncheckedBooleanSupplier
 *  io.netty.util.internal.StringUtil
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollDomainDatagramChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.unix.DomainDatagramChannel;
import io.netty.channel.unix.DomainDatagramPacket;
import io.netty.channel.unix.DomainDatagramSocketAddress;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public final class EpollDomainDatagramChannel
extends AbstractEpollChannel
implements DomainDatagramChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(true);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DomainDatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DomainSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private volatile boolean connected;
    private volatile DomainSocketAddress local;
    private volatile DomainSocketAddress remote;
    private final EpollDomainDatagramChannelConfig config = new EpollDomainDatagramChannelConfig(this);

    public EpollDomainDatagramChannel() {
        this(LinuxSocket.newSocketDomainDgram(), false);
    }

    public EpollDomainDatagramChannel(int fd) {
        this(new LinuxSocket(fd), true);
    }

    private EpollDomainDatagramChannel(LinuxSocket socket, boolean active) {
        super(null, socket, active);
    }

    @Override
    public EpollDomainDatagramChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        this.local = (DomainSocketAddress)localAddress;
        this.active = true;
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.active = false;
        this.connected = false;
        this.local = null;
        this.remote = null;
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (super.doConnect(remoteAddress, localAddress)) {
            if (localAddress != null) {
                this.local = (DomainSocketAddress)localAddress;
            }
            this.remote = (DomainSocketAddress)remoteAddress;
            this.connected = true;
            return true;
        }
        return false;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        int maxMessagesPerWrite = this.maxMessagesPerWrite();
        while (maxMessagesPerWrite > 0 && (msg = in.current()) != null) {
            try {
                boolean done = false;
                for (int i = this.config().getWriteSpinCount(); i > 0; --i) {
                    if (!this.doWriteMessage(msg)) continue;
                    done = true;
                    break;
                }
                if (!done) break;
                in.remove();
                --maxMessagesPerWrite;
            }
            catch (IOException e) {
                --maxMessagesPerWrite;
                in.remove((Throwable)e);
            }
        }
        if (in.isEmpty()) {
            this.clearFlag(Native.EPOLLOUT);
        } else {
            this.setFlag(Native.EPOLLOUT);
        }
    }

    private boolean doWriteMessage(Object msg) throws Exception {
        long writtenBytes;
        DomainSocketAddress remoteAddress;
        ByteBuf data;
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            data = (ByteBuf)envelope.content();
            remoteAddress = (DomainSocketAddress)envelope.recipient();
        } else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        if (data.hasMemoryAddress()) {
            long memoryAddress = data.memoryAddress();
            writtenBytes = remoteAddress == null ? (long)this.socket.sendAddress(memoryAddress, data.readerIndex(), data.writerIndex()) : (long)this.socket.sendToAddressDomainSocket(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.path().getBytes(CharsetUtil.UTF_8));
        } else if (data.nioBufferCount() > 1) {
            IovArray array = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
            array.add(data, data.readerIndex(), data.readableBytes());
            int cnt = array.count();
            assert (cnt != 0);
            writtenBytes = remoteAddress == null ? this.socket.writevAddresses(array.memoryAddress(0), cnt) : (long)this.socket.sendToAddressesDomainSocket(array.memoryAddress(0), cnt, remoteAddress.path().getBytes(CharsetUtil.UTF_8));
        } else {
            ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
            writtenBytes = remoteAddress == null ? (long)this.socket.send(nioData, nioData.position(), nioData.limit()) : (long)this.socket.sendToDomainSocket(nioData, nioData.position(), nioData.limit(), remoteAddress.path().getBytes(CharsetUtil.UTF_8));
        }
        return writtenBytes > 0L;
    }

    protected Object filterOutboundMessage(Object msg) {
        AddressedEnvelope e;
        if (msg instanceof DomainDatagramPacket) {
            DomainDatagramPacket packet = (DomainDatagramPacket)msg;
            ByteBuf content = (ByteBuf)packet.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)content) ? new DomainDatagramPacket(this.newDirectBuffer(packet, content), (DomainSocketAddress)packet.recipient()) : msg;
        }
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof AddressedEnvelope && (e = (AddressedEnvelope)msg).content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof DomainSocketAddress)) {
            ByteBuf content = (ByteBuf)e.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)content) ? new DefaultAddressedEnvelope((Object)this.newDirectBuffer(e, content), (SocketAddress)((DomainSocketAddress)e.recipient())) : e;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES);
    }

    @Override
    public boolean isActive() {
        return this.socket.isOpen() && (this.config.getActiveOnOpen() && this.isRegistered() || this.active);
    }

    public boolean isConnected() {
        return this.connected;
    }

    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }

    protected DomainSocketAddress localAddress0() {
        return this.local;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollDomainDatagramChannelUnsafe();
    }

    public PeerCredentials peerCredentials() throws IOException {
        return this.socket.getPeerCredentials();
    }

    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }

    protected DomainSocketAddress remoteAddress0() {
        return this.remote;
    }

    final class EpollDomainDatagramChannelUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        EpollDomainDatagramChannelUnsafe() {
            super(EpollDomainDatagramChannel.this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void epollInReady() {
            assert (EpollDomainDatagramChannel.this.eventLoop().inEventLoop());
            EpollDomainDatagramChannelConfig config = EpollDomainDatagramChannel.this.config();
            if (EpollDomainDatagramChannel.this.shouldBreakEpollInReady((ChannelConfig)config)) {
                this.clearEpollIn0();
                return;
            }
            EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.edgeTriggered(EpollDomainDatagramChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline pipeline = EpollDomainDatagramChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset((ChannelConfig)config);
            this.epollInBefore();
            Throwable exception = null;
            try {
                ByteBuf byteBuf = null;
                try {
                    boolean connected = EpollDomainDatagramChannel.this.isConnected();
                    do {
                        DomainDatagramPacket packet;
                        byteBuf = allocHandle.allocate(allocator);
                        allocHandle.attemptedBytesRead(byteBuf.writableBytes());
                        if (connected) {
                            allocHandle.lastBytesRead(EpollDomainDatagramChannel.this.doReadBytes(byteBuf));
                            if (allocHandle.lastBytesRead() <= 0) {
                                byteBuf.release();
                                break;
                            }
                            packet = new DomainDatagramPacket(byteBuf, (DomainSocketAddress)this.localAddress(), (DomainSocketAddress)this.remoteAddress());
                        } else {
                            DomainDatagramSocketAddress remoteAddress;
                            if (byteBuf.hasMemoryAddress()) {
                                remoteAddress = EpollDomainDatagramChannel.this.socket.recvFromAddressDomainSocket(byteBuf.memoryAddress(), byteBuf.writerIndex(), byteBuf.capacity());
                            } else {
                                ByteBuffer nioData = byteBuf.internalNioBuffer(byteBuf.writerIndex(), byteBuf.writableBytes());
                                remoteAddress = EpollDomainDatagramChannel.this.socket.recvFromDomainSocket(nioData, nioData.position(), nioData.limit());
                            }
                            if (remoteAddress == null) {
                                allocHandle.lastBytesRead(-1);
                                byteBuf.release();
                                break;
                            }
                            DomainDatagramSocketAddress localAddress = remoteAddress.localAddress();
                            if (localAddress == null) {
                                localAddress = (DomainSocketAddress)this.localAddress();
                            }
                            allocHandle.lastBytesRead(remoteAddress.receivedAmount());
                            byteBuf.writerIndex(byteBuf.writerIndex() + allocHandle.lastBytesRead());
                            packet = new DomainDatagramPacket(byteBuf, (DomainSocketAddress)localAddress, (DomainSocketAddress)remoteAddress);
                        }
                        allocHandle.incMessagesRead(1);
                        this.readPending = false;
                        pipeline.fireChannelRead((Object)packet);
                        byteBuf = null;
                    } while (allocHandle.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
                }
                catch (Throwable t) {
                    if (byteBuf != null) {
                        byteBuf.release();
                    }
                    exception = t;
                }
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    pipeline.fireExceptionCaught(exception);
                }
            }
            finally {
                this.epollInFinally((ChannelConfig)config);
            }
        }
    }
}

