/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.client.impl.protocol.util.ClientMessageDecoder;
import com.hazelcast.config.MemcacheProtocolConfig;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.networking.ChannelOption;
import com.hazelcast.internal.networking.ChannelOptions;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ascii.MemcacheTextDecoder;
import com.hazelcast.nio.ascii.RestApiTextDecoder;
import com.hazelcast.nio.ascii.TextDecoder;
import com.hazelcast.nio.ascii.TextEncoder;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.UnifiedProtocolEncoder;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.StringUtil;
import java.nio.ByteBuffer;

public class UnifiedProtocolDecoder
extends InboundHandler<ByteBuffer, Void> {
    private final IOService ioService;
    private final UnifiedProtocolEncoder protocolEncoder;
    private final HazelcastProperties props;

    public UnifiedProtocolDecoder(IOService ioService, UnifiedProtocolEncoder protocolEncoder) {
        this.ioService = ioService;
        this.protocolEncoder = protocolEncoder;
        this.props = ioService.properties();
    }

    @Override
    public void handlerAdded() {
        this.initSrcBuffer(3);
    }

    @Override
    public HandlerStatus onRead() throws Exception {
        ((ByteBuffer)this.src).flip();
        try {
            if (((ByteBuffer)this.src).remaining() < 3) {
                HandlerStatus handlerStatus = HandlerStatus.CLEAN;
                return handlerStatus;
            }
            String protocol = this.loadProtocol();
            if ("HZC".equals(protocol)) {
                this.initChannelForCluster();
            } else if ("CB2".equals(protocol)) {
                this.initChannelForClient();
            } else if (RestApiTextDecoder.TEXT_PARSERS.isCommandPrefix(protocol)) {
                RestApiConfig restApiConfig = this.ioService.getRestApiConfig();
                if (!restApiConfig.isEnabledAndNotEmpty()) {
                    throw new IllegalStateException("REST API is not enabled.");
                }
                this.initChannelForText(protocol, true);
            } else if (MemcacheTextDecoder.TEXT_PARSERS.isCommandPrefix(protocol)) {
                MemcacheProtocolConfig memcacheProtocolConfig = this.ioService.getMemcacheProtocolConfig();
                if (!memcacheProtocolConfig.isEnabled()) {
                    throw new IllegalStateException("Memcache text protocol is not enabled.");
                }
                this.initChannelForText(protocol, false);
            } else {
                throw new IllegalStateException("Unknown protocol: " + protocol);
            }
            if (!this.channel.isClientMode()) {
                this.protocolEncoder.signalProtocolEstablished(protocol);
            }
            HandlerStatus handlerStatus = HandlerStatus.CLEAN;
            return handlerStatus;
        }
        finally {
            IOUtil.compactOrClear((ByteBuffer)this.src);
        }
    }

    private String loadProtocol() {
        byte[] protocolBytes = new byte[3];
        ((ByteBuffer)this.src).get(protocolBytes);
        if (UnifiedProtocolDecoder.isTlsHandshake(protocolBytes)) {
            throw new IllegalStateException("TLS handshake header detected, but plain protocol header was expected.");
        }
        return StringUtil.bytesToString(protocolBytes);
    }

    private void initChannelForCluster() {
        this.channel.options().setOption(ChannelOption.SO_SNDBUF, this.props.getInteger(GroupProperty.SOCKET_RECEIVE_BUFFER_SIZE) * 1024);
        TcpIpConnection connection = (TcpIpConnection)this.channel.attributeMap().get(TcpIpConnection.class);
        connection.setType(ConnectionType.MEMBER);
        this.channel.inboundPipeline().replace(this, this.ioService.createInboundHandlers(EndpointQualifier.MEMBER, connection));
    }

    private void initChannelForClient() {
        this.channel.options().setOption(ChannelOption.SO_RCVBUF, this.clientRcvBuf()).setOption(ChannelOption.DIRECT_BUF, false);
        TcpIpConnection connection = (TcpIpConnection)this.channel.attributeMap().get(TcpIpConnection.class);
        this.channel.inboundPipeline().replace(this, new ClientMessageDecoder(connection, this.ioService.getClientEngine()));
    }

    private void initChannelForText(String protocol, boolean restApi) {
        ChannelOptions config = this.channel.options();
        config.setOption(ChannelOption.SO_RCVBUF, this.clientRcvBuf());
        TcpIpConnection connection = (TcpIpConnection)this.channel.attributeMap().get(TcpIpConnection.class);
        TextEncoder encoder = new TextEncoder(connection);
        this.channel.attributeMap().put("textencoder", encoder);
        TextDecoder decoder = restApi ? new RestApiTextDecoder(connection, encoder, false) : new MemcacheTextDecoder(connection, encoder, false);
        decoder.src(IOUtil.newByteBuffer(config.getOption(ChannelOption.SO_RCVBUF), config.getOption(ChannelOption.DIRECT_BUF)));
        ((ByteBuffer)decoder.src()).put(StringUtil.stringToBytes(protocol));
        this.channel.inboundPipeline().replace(this, decoder);
    }

    private int clientRcvBuf() {
        int rcvBuf = this.props.getInteger(GroupProperty.SOCKET_CLIENT_RECEIVE_BUFFER_SIZE);
        if (rcvBuf == -1) {
            rcvBuf = this.props.getInteger(GroupProperty.SOCKET_RECEIVE_BUFFER_SIZE);
        }
        return rcvBuf * 1024;
    }

    private static boolean isTlsHandshake(byte[] protocolBytes) {
        return protocolBytes[0] == 22 && protocolBytes[1] == 3 && protocolBytes[2] >= 0 && protocolBytes[2] <= 3;
    }
}

