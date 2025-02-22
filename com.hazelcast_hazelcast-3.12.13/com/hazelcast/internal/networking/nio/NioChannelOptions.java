/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.networking.ChannelOption;
import com.hazelcast.internal.networking.ChannelOptions;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

final class NioChannelOptions
implements ChannelOptions {
    private static final AtomicBoolean SEND_BUFFER_WARNING = new AtomicBoolean();
    private static final AtomicBoolean RECEIVE_BUFFER_WARNING = new AtomicBoolean();
    private final Map<String, Object> values = new ConcurrentHashMap<String, Object>();
    private final Socket socket;

    NioChannelOptions(Socket socket) {
        this.setOption((ChannelOption)ChannelOption.DIRECT_BUF, (Object)false);
        this.socket = socket;
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        try {
            if (option.equals(ChannelOption.TCP_NODELAY)) {
                return (T)Boolean.valueOf(this.socket.getTcpNoDelay());
            }
            if (option.equals(ChannelOption.SO_RCVBUF)) {
                return (T)Integer.valueOf(this.socket.getReceiveBufferSize());
            }
            if (option.equals(ChannelOption.SO_SNDBUF)) {
                return (T)Integer.valueOf(this.socket.getSendBufferSize());
            }
            if (option.equals(ChannelOption.SO_KEEPALIVE)) {
                return (T)Boolean.valueOf(this.socket.getKeepAlive());
            }
            if (option.equals(ChannelOption.SO_REUSEADDR)) {
                return (T)Boolean.valueOf(this.socket.getReuseAddress());
            }
            if (option.equals(ChannelOption.SO_TIMEOUT)) {
                return (T)Integer.valueOf(this.socket.getSoTimeout());
            }
            if (option.equals(ChannelOption.SO_LINGER)) {
                return (T)Integer.valueOf(this.socket.getSoLinger());
            }
            return (T)this.values.get(option.name());
        }
        catch (SocketException e) {
            throw new HazelcastException("Failed to getOption [" + option.name() + "]", e);
        }
    }

    @Override
    public <T> NioChannelOptions setOption(ChannelOption<T> option, T value) {
        Preconditions.checkNotNull(option, "option can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        try {
            if (option.equals(ChannelOption.TCP_NODELAY)) {
                this.socket.setTcpNoDelay((Boolean)value);
            } else if (option.equals(ChannelOption.SO_RCVBUF)) {
                int receiveBufferSize = (Integer)value;
                this.socket.setReceiveBufferSize(receiveBufferSize);
                this.verifyReceiveBufferSize(receiveBufferSize);
            } else if (option.equals(ChannelOption.SO_SNDBUF)) {
                int sendBufferSize = (Integer)value;
                this.socket.setSendBufferSize(sendBufferSize);
                this.verifySendBufferSize(sendBufferSize);
            } else if (option.equals(ChannelOption.SO_KEEPALIVE)) {
                this.socket.setKeepAlive((Boolean)value);
            } else if (option.equals(ChannelOption.SO_REUSEADDR)) {
                this.socket.setReuseAddress((Boolean)value);
            } else if (option.equals(ChannelOption.SO_TIMEOUT)) {
                this.socket.setSoTimeout((Integer)value);
            } else if (option.equals(ChannelOption.SO_LINGER)) {
                int soLinger = (Integer)value;
                if (soLinger >= 0) {
                    this.socket.setSoLinger(true, soLinger);
                }
            } else {
                this.values.put(option.name(), value);
            }
        }
        catch (SocketException e) {
            throw new HazelcastException("Failed to setOption [" + option.name() + "] with value [" + value + "]", e);
        }
        return this;
    }

    private void verifySendBufferSize(int sendBufferSize) throws SocketException {
        if (this.socket.getSendBufferSize() == sendBufferSize) {
            return;
        }
        if (SEND_BUFFER_WARNING.compareAndSet(false, true)) {
            Logger.getLogger(NioChannelOptions.class).log(Level.WARNING, "The configured tcp send buffer size conflicts with the value actually being used by the socket and can lead to sub-optimal performance. Configured " + sendBufferSize + " bytes, actual " + this.socket.getSendBufferSize() + " bytes. On Linux look for kernel parameters 'net.ipv4.tcp_wmem' and 'net.core.wmem_max'.This warning will only be shown once.");
        }
    }

    private void verifyReceiveBufferSize(int receiveBufferSize) throws SocketException {
        if (this.socket.getReceiveBufferSize() == receiveBufferSize) {
            return;
        }
        if (RECEIVE_BUFFER_WARNING.compareAndSet(false, true)) {
            Logger.getLogger(NioChannelOptions.class).log(Level.WARNING, "The configured tcp receive buffer size conflicts with the value actually being used by the socket and can lead to sub-optimal performance. Configured " + receiveBufferSize + " bytes, actual " + this.socket.getReceiveBufferSize() + " bytes. On Linux look for kernel parameters 'net.ipv4.tcp_rmem' and 'net.core.rmem_max'.This warning will only be shown once.");
        }
    }
}

