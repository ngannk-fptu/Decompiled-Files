/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.ascii.MemcacheTextDecoder;
import com.hazelcast.nio.ascii.RestApiTextDecoder;
import com.hazelcast.nio.ascii.TextDecoder;
import com.hazelcast.nio.ascii.TextEncoder;
import com.hazelcast.nio.tcp.AbstractChannelInitializer;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TextHandshakeDecoder;

public class TextChannelInitializer
extends AbstractChannelInitializer {
    private final boolean rest;

    public TextChannelInitializer(IOService ioService, EndpointConfig config, boolean rest) {
        super(ioService, config);
        this.rest = rest;
    }

    @Override
    public void initChannel(Channel channel) {
        TcpIpConnection connection = (TcpIpConnection)channel.attributeMap().get(TcpIpConnection.class);
        TextEncoder encoder = new TextEncoder(connection);
        TextDecoder decoder = this.rest ? new RestApiTextDecoder(connection, encoder, true) : new MemcacheTextDecoder(connection, encoder, true);
        TextHandshakeDecoder handshaker = new TextHandshakeDecoder(this.rest ? ProtocolType.REST : ProtocolType.MEMCACHE, decoder);
        channel.outboundPipeline().addLast(encoder);
        channel.inboundPipeline().addLast(handshaker);
    }
}

