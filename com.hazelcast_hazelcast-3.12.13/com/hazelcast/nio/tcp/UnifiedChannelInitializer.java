/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelInitializer;
import com.hazelcast.internal.networking.ChannelOption;
import com.hazelcast.internal.networking.ChannelOptions;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.tcp.UnifiedProtocolDecoder;
import com.hazelcast.nio.tcp.UnifiedProtocolEncoder;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;

public class UnifiedChannelInitializer
implements ChannelInitializer {
    private final IOService ioService;
    private final HazelcastProperties props;

    public UnifiedChannelInitializer(IOService ioService) {
        this.props = ioService.properties();
        this.ioService = ioService;
    }

    @Override
    public void initChannel(Channel channel) {
        ChannelOptions config = channel.options();
        config.setOption(ChannelOption.DIRECT_BUF, this.props.getBoolean(GroupProperty.SOCKET_BUFFER_DIRECT)).setOption(ChannelOption.TCP_NODELAY, this.props.getBoolean(GroupProperty.SOCKET_NO_DELAY)).setOption(ChannelOption.SO_KEEPALIVE, this.props.getBoolean(GroupProperty.SOCKET_KEEP_ALIVE)).setOption(ChannelOption.SO_SNDBUF, this.props.getInteger(GroupProperty.SOCKET_SEND_BUFFER_SIZE) * 1024).setOption(ChannelOption.SO_RCVBUF, this.props.getInteger(GroupProperty.SOCKET_RECEIVE_BUFFER_SIZE) * 1024).setOption(ChannelOption.SO_LINGER, this.props.getSeconds(GroupProperty.SOCKET_LINGER_SECONDS));
        UnifiedProtocolEncoder encoder = new UnifiedProtocolEncoder(this.ioService);
        UnifiedProtocolDecoder decoder = new UnifiedProtocolDecoder(this.ioService, encoder);
        channel.outboundPipeline().addLast(encoder);
        channel.inboundPipeline().addLast(decoder);
    }
}

