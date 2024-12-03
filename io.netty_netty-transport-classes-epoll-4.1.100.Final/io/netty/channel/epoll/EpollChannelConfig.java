/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.DefaultChannelConfig
 *  io.netty.channel.MessageSizeEstimator
 *  io.netty.channel.RecvByteBufAllocator
 *  io.netty.channel.RecvByteBufAllocator$ExtendedHandle
 *  io.netty.channel.WriteBufferWaterMark
 *  io.netty.channel.unix.IntegerUnixChannelOption
 *  io.netty.channel.unix.Limits
 *  io.netty.channel.unix.RawUnixChannelOption
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.unix.IntegerUnixChannelOption;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.RawUnixChannelOption;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class EpollChannelConfig
extends DefaultChannelConfig {
    private volatile long maxBytesPerGatheringWrite = Limits.SSIZE_MAX;

    protected EpollChannelConfig(Channel channel) {
        super(EpollChannelConfig.checkAbstractEpollChannel(channel));
    }

    protected EpollChannelConfig(Channel channel, RecvByteBufAllocator recvByteBufAllocator) {
        super(EpollChannelConfig.checkAbstractEpollChannel(channel), recvByteBufAllocator);
    }

    protected LinuxSocket socket() {
        return ((AbstractEpollChannel)this.channel).socket;
    }

    private static Channel checkAbstractEpollChannel(Channel channel) {
        if (!(channel instanceof AbstractEpollChannel)) {
            throw new IllegalArgumentException("channel is not AbstractEpollChannel: " + channel.getClass());
        }
        return channel;
    }

    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{EpollChannelOption.EPOLL_MODE});
    }

    public <T> T getOption(ChannelOption<T> option) {
        if (option == EpollChannelOption.EPOLL_MODE) {
            return (T)((Object)this.getEpollMode());
        }
        try {
            if (option instanceof IntegerUnixChannelOption) {
                IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
                return (T)Integer.valueOf(((AbstractEpollChannel)this.channel).socket.getIntOpt(opt.level(), opt.optname()));
            }
            if (option instanceof RawUnixChannelOption) {
                RawUnixChannelOption opt = (RawUnixChannelOption)option;
                ByteBuffer out = ByteBuffer.allocate(opt.length());
                ((AbstractEpollChannel)this.channel).socket.getRawOpt(opt.level(), opt.optname(), out);
                return (T)out.flip();
            }
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
        return (T)super.getOption(option);
    }

    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option != EpollChannelOption.EPOLL_MODE) {
            try {
                if (option instanceof IntegerUnixChannelOption) {
                    IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
                    ((AbstractEpollChannel)this.channel).socket.setIntOpt(opt.level(), opt.optname(), (Integer)value);
                    return true;
                }
                if (option instanceof RawUnixChannelOption) {
                    RawUnixChannelOption opt = (RawUnixChannelOption)option;
                    ((AbstractEpollChannel)this.channel).socket.setRawOpt(opt.level(), opt.optname(), (ByteBuffer)value);
                    return true;
                }
            }
            catch (IOException e) {
                throw new ChannelException((Throwable)e);
            }
            return super.setOption(option, value);
        }
        this.setEpollMode((EpollMode)((Object)value));
        return true;
    }

    public EpollChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Deprecated
    public EpollChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    public EpollChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    public EpollChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    public EpollChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
            throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
        }
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    public EpollChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Deprecated
    public EpollChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Deprecated
    public EpollChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    public EpollChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    public EpollChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    public EpollMode getEpollMode() {
        return ((AbstractEpollChannel)this.channel).isFlagSet(Native.EPOLLET) ? EpollMode.EDGE_TRIGGERED : EpollMode.LEVEL_TRIGGERED;
    }

    public EpollChannelConfig setEpollMode(EpollMode mode) {
        ObjectUtil.checkNotNull((Object)((Object)mode), (String)"mode");
        try {
            switch (mode) {
                case EDGE_TRIGGERED: {
                    this.checkChannelNotRegistered();
                    ((AbstractEpollChannel)this.channel).setFlag(Native.EPOLLET);
                    break;
                }
                case LEVEL_TRIGGERED: {
                    this.checkChannelNotRegistered();
                    ((AbstractEpollChannel)this.channel).clearFlag(Native.EPOLLET);
                    break;
                }
                default: {
                    throw new Error();
                }
            }
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
        return this;
    }

    private void checkChannelNotRegistered() {
        if (this.channel.isRegistered()) {
            throw new IllegalStateException("EpollMode can only be changed before channel is registered");
        }
    }

    protected final void autoReadCleared() {
        ((AbstractEpollChannel)this.channel).clearEpollIn();
    }

    protected final void setMaxBytesPerGatheringWrite(long maxBytesPerGatheringWrite) {
        this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
    }

    protected final long getMaxBytesPerGatheringWrite() {
        return this.maxBytesPerGatheringWrite;
    }
}

