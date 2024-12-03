/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import java.net.InetSocketAddress;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.SdkEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.DnsResolverLoader;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelOptions;

@SdkInternalApi
public class BootstrapProvider {
    private final SdkEventLoopGroup sdkEventLoopGroup;
    private final NettyConfiguration nettyConfiguration;
    private final SdkChannelOptions sdkChannelOptions;

    BootstrapProvider(SdkEventLoopGroup sdkEventLoopGroup, NettyConfiguration nettyConfiguration, SdkChannelOptions sdkChannelOptions) {
        this.sdkEventLoopGroup = sdkEventLoopGroup;
        this.nettyConfiguration = nettyConfiguration;
        this.sdkChannelOptions = sdkChannelOptions;
    }

    public Bootstrap createBootstrap(String host, int port, Boolean useNonBlockingDnsResolver) {
        Bootstrap bootstrap = ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(this.sdkEventLoopGroup.eventLoopGroup())).channelFactory(this.sdkEventLoopGroup.channelFactory())).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.nettyConfiguration.connectTimeoutMillis())).option(ChannelOption.SO_KEEPALIVE, this.nettyConfiguration.tcpKeepAlive())).remoteAddress(InetSocketAddress.createUnresolved(host, port));
        if (Boolean.TRUE.equals(useNonBlockingDnsResolver)) {
            bootstrap.resolver(DnsResolverLoader.init(this.sdkEventLoopGroup.datagramChannelFactory()));
        }
        this.sdkChannelOptions.channelOptions().forEach(bootstrap::option);
        return bootstrap;
    }
}

