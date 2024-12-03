/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFactory
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.DatagramChannel
 *  io.netty.channel.socket.nio.NioDatagramChannel
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ThreadFactoryBuilder
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.nio.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.nio.netty.internal.utils.ChannelResolver;
import software.amazon.awssdk.utils.ThreadFactoryBuilder;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class SdkEventLoopGroup {
    private final EventLoopGroup eventLoopGroup;
    private final ChannelFactory<? extends Channel> channelFactory;
    private final ChannelFactory<? extends DatagramChannel> datagramChannelFactory;

    SdkEventLoopGroup(EventLoopGroup eventLoopGroup, ChannelFactory<? extends Channel> channelFactory) {
        Validate.paramNotNull((Object)eventLoopGroup, (String)"eventLoopGroup");
        Validate.paramNotNull(channelFactory, (String)"channelFactory");
        this.eventLoopGroup = eventLoopGroup;
        this.channelFactory = channelFactory;
        this.datagramChannelFactory = ChannelResolver.resolveDatagramChannelFactory(eventLoopGroup);
    }

    private SdkEventLoopGroup(DefaultBuilder builder) {
        this.eventLoopGroup = this.resolveEventLoopGroup(builder);
        this.channelFactory = this.resolveSocketChannelFactory(builder);
        this.datagramChannelFactory = this.resolveDatagramChannelFactory(builder);
    }

    public EventLoopGroup eventLoopGroup() {
        return this.eventLoopGroup;
    }

    public ChannelFactory<? extends Channel> channelFactory() {
        return this.channelFactory;
    }

    public ChannelFactory<? extends DatagramChannel> datagramChannelFactory() {
        return this.datagramChannelFactory;
    }

    public static SdkEventLoopGroup create(EventLoopGroup eventLoopGroup, ChannelFactory<? extends Channel> channelFactory) {
        return new SdkEventLoopGroup(eventLoopGroup, channelFactory);
    }

    public static SdkEventLoopGroup create(EventLoopGroup eventLoopGroup) {
        return SdkEventLoopGroup.create(eventLoopGroup, ChannelResolver.resolveSocketChannelFactory(eventLoopGroup));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    private EventLoopGroup resolveEventLoopGroup(DefaultBuilder builder) {
        int numThreads = Optional.ofNullable(builder.numberOfThreads).orElse(0);
        ThreadFactory threadFactory = Optional.ofNullable(builder.threadFactory).orElseGet(() -> new ThreadFactoryBuilder().threadNamePrefix("aws-java-sdk-NettyEventLoop").build());
        return new NioEventLoopGroup(numThreads, threadFactory);
    }

    private ChannelFactory<? extends Channel> resolveSocketChannelFactory(DefaultBuilder builder) {
        return builder.channelFactory;
    }

    private ChannelFactory<? extends DatagramChannel> resolveDatagramChannelFactory(DefaultBuilder builder) {
        return builder.datagramChannelFactory;
    }

    private static ChannelFactory<? extends Channel> defaultSocketChannelFactory() {
        return NioSocketChannel::new;
    }

    private static ChannelFactory<? extends DatagramChannel> defaultDatagramChannelFactory() {
        return NioDatagramChannel::new;
    }

    static /* synthetic */ ChannelFactory access$500() {
        return SdkEventLoopGroup.defaultSocketChannelFactory();
    }

    static /* synthetic */ ChannelFactory access$600() {
        return SdkEventLoopGroup.defaultDatagramChannelFactory();
    }

    private static final class DefaultBuilder
    implements Builder {
        private Integer numberOfThreads;
        private ThreadFactory threadFactory;
        private ChannelFactory<? extends Channel> channelFactory = SdkEventLoopGroup.access$500();
        private ChannelFactory<? extends DatagramChannel> datagramChannelFactory = SdkEventLoopGroup.access$600();

        private DefaultBuilder() {
        }

        @Override
        public Builder numberOfThreads(Integer numberOfThreads) {
            this.numberOfThreads = numberOfThreads;
            return this;
        }

        public void setNumberOfThreads(Integer numberOfThreads) {
            this.numberOfThreads(numberOfThreads);
        }

        @Override
        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public void setThreadFactory(ThreadFactory threadFactory) {
            this.threadFactory(threadFactory);
        }

        @Override
        public Builder channelFactory(ChannelFactory<? extends Channel> channelFactory) {
            this.channelFactory = channelFactory;
            return this;
        }

        public void setChannelFactory(ChannelFactory<? extends Channel> channelFactory) {
            this.channelFactory(channelFactory);
        }

        @Override
        public Builder datagramChannelFactory(ChannelFactory<? extends DatagramChannel> datagramChannelFactory) {
            this.datagramChannelFactory = datagramChannelFactory;
            return this;
        }

        public void setDatagramChannelFactory(ChannelFactory<? extends DatagramChannel> datagramChannelFactory) {
            this.datagramChannelFactory(datagramChannelFactory);
        }

        @Override
        public SdkEventLoopGroup build() {
            return new SdkEventLoopGroup(this);
        }
    }

    public static interface Builder {
        public Builder numberOfThreads(Integer var1);

        public Builder threadFactory(ThreadFactory var1);

        public Builder channelFactory(ChannelFactory<? extends Channel> var1);

        public Builder datagramChannelFactory(ChannelFactory<? extends DatagramChannel> var1);

        public SdkEventLoopGroup build();
    }
}

