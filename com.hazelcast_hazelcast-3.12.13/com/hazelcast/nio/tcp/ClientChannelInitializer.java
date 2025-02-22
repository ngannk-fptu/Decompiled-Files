/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.client.impl.protocol.util.ClientMessageDecoder;
import com.hazelcast.client.impl.protocol.util.ClientMessageEncoder;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.tcp.AbstractChannelInitializer;
import com.hazelcast.nio.tcp.SingleProtocolDecoder;
import com.hazelcast.nio.tcp.TcpIpConnection;

public class ClientChannelInitializer
extends AbstractChannelInitializer {
    ClientChannelInitializer(IOService ioService, EndpointConfig config) {
        super(ioService, config);
    }

    @Override
    public void initChannel(Channel channel) {
        TcpIpConnection connection = (TcpIpConnection)channel.attributeMap().get(TcpIpConnection.class);
        SingleProtocolDecoder protocolDecoder = new SingleProtocolDecoder(ProtocolType.CLIENT, new ClientMessageDecoder(connection, this.ioService.getClientEngine()));
        channel.outboundPipeline().addLast(new ClientMessageEncoder());
        channel.inboundPipeline().addLast(protocolDecoder);
    }
}

