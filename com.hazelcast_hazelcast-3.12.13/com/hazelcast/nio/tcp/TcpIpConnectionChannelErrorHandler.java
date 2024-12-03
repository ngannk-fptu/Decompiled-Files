/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.tcp.TcpIpConnection;
import java.io.EOFException;

public class TcpIpConnectionChannelErrorHandler
implements ChannelErrorHandler {
    private final ILogger logger;

    public TcpIpConnectionChannelErrorHandler(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void onError(Channel channel, Throwable error) {
        if (error instanceof OutOfMemoryError) {
            OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)error);
        }
        if (channel == null) {
            this.logger.severe(error);
        } else {
            TcpIpConnection connection = (TcpIpConnection)channel.attributeMap().get(TcpIpConnection.class);
            if (connection != null) {
                String closeReason = error instanceof EOFException ? "Connection closed by the other side" : "Exception in " + connection + ", thread=" + Thread.currentThread().getName();
                connection.close(closeReason, error);
            } else {
                this.logger.warning("Channel error occured", error);
            }
        }
    }
}

