/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFactory
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.ReflectiveChannelFactory
 *  io.netty.channel.epoll.EpollDatagramChannel
 *  io.netty.channel.epoll.EpollEventLoopGroup
 *  io.netty.channel.epoll.EpollSocketChannel
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.DatagramChannel
 *  io.netty.channel.socket.nio.NioDatagramChannel
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.FunctionalUtils
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.DelegatingEventLoopGroup;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class ChannelResolver {
    private static final Map<String, String> KNOWN_EL_GROUPS_SOCKET_CHANNELS = new HashMap<String, String>();
    private static final Map<String, String> KNOWN_EL_GROUPS_DATAGRAM_CHANNELS = new HashMap<String, String>();

    private ChannelResolver() {
    }

    public static ChannelFactory<? extends Channel> resolveSocketChannelFactory(EventLoopGroup eventLoopGroup) {
        if (eventLoopGroup instanceof DelegatingEventLoopGroup) {
            return ChannelResolver.resolveSocketChannelFactory(((DelegatingEventLoopGroup)eventLoopGroup).getDelegate());
        }
        if (eventLoopGroup instanceof NioEventLoopGroup) {
            return NioSocketChannel::new;
        }
        if (eventLoopGroup instanceof EpollEventLoopGroup) {
            return EpollSocketChannel::new;
        }
        String socketFqcn = KNOWN_EL_GROUPS_SOCKET_CHANNELS.get(eventLoopGroup.getClass().getName());
        if (socketFqcn == null) {
            throw new IllegalArgumentException("Unknown event loop group : " + eventLoopGroup.getClass());
        }
        return (ChannelFactory)FunctionalUtils.invokeSafely(() -> new ReflectiveChannelFactory(Class.forName(socketFqcn)));
    }

    public static ChannelFactory<? extends DatagramChannel> resolveDatagramChannelFactory(EventLoopGroup eventLoopGroup) {
        if (eventLoopGroup instanceof DelegatingEventLoopGroup) {
            return ChannelResolver.resolveDatagramChannelFactory(((DelegatingEventLoopGroup)eventLoopGroup).getDelegate());
        }
        if (eventLoopGroup instanceof NioEventLoopGroup) {
            return NioDatagramChannel::new;
        }
        if (eventLoopGroup instanceof EpollEventLoopGroup) {
            return EpollDatagramChannel::new;
        }
        String datagramFqcn = KNOWN_EL_GROUPS_DATAGRAM_CHANNELS.get(eventLoopGroup.getClass().getName());
        if (datagramFqcn == null) {
            throw new IllegalArgumentException("Unknown event loop group : " + eventLoopGroup.getClass());
        }
        return (ChannelFactory)FunctionalUtils.invokeSafely(() -> new ReflectiveChannelFactory(Class.forName(datagramFqcn)));
    }

    static {
        KNOWN_EL_GROUPS_SOCKET_CHANNELS.put("io.netty.channel.kqueue.KQueueEventLoopGroup", "io.netty.channel.kqueue.KQueueSocketChannel");
        KNOWN_EL_GROUPS_SOCKET_CHANNELS.put("io.netty.channel.oio.OioEventLoopGroup", "io.netty.channel.socket.oio.OioSocketChannel");
        KNOWN_EL_GROUPS_DATAGRAM_CHANNELS.put("io.netty.channel.kqueue.KQueueEventLoopGroup", "io.netty.channel.kqueue.KQueueDatagramChannel");
        KNOWN_EL_GROUPS_DATAGRAM_CHANNELS.put("io.netty.channel.oio.OioEventLoopGroup", "io.netty.channel.socket.oio.OioDatagramChannel");
    }
}

