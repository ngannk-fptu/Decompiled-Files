/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.tcp.AbstractChannelInitializer;
import com.hazelcast.nio.tcp.MemberProtocolEncoder;
import com.hazelcast.nio.tcp.SingleProtocolDecoder;
import com.hazelcast.nio.tcp.TcpIpConnection;

public class MemberChannelInitializer
extends AbstractChannelInitializer {
    MemberChannelInitializer(IOService ioService, EndpointConfig config) {
        super(ioService, config);
    }

    @Override
    public void initChannel(Channel channel) {
        TcpIpConnection connection = (TcpIpConnection)channel.attributeMap().get(TcpIpConnection.class);
        OutboundHandler[] outboundHandlers = this.ioService.createOutboundHandlers(EndpointQualifier.MEMBER, connection);
        InboundHandler[] inboundHandlers = this.ioService.createInboundHandlers(EndpointQualifier.MEMBER, connection);
        MemberProtocolEncoder protocolEncoder = new MemberProtocolEncoder(outboundHandlers);
        SingleProtocolDecoder protocolDecoder = new SingleProtocolDecoder(ProtocolType.MEMBER, inboundHandlers, protocolEncoder);
        channel.outboundPipeline().addLast(protocolEncoder);
        channel.inboundPipeline().addLast(protocolDecoder);
    }
}

