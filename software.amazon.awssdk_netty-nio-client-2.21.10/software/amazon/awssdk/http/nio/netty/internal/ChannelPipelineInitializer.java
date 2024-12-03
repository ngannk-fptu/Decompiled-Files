/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.UnpooledByteBufAllocator
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.pool.AbstractChannelPoolHandler
 *  io.netty.channel.pool.ChannelPool
 *  io.netty.handler.codec.http.HttpClientCodec
 *  io.netty.handler.codec.http2.Http2Connection$Listener
 *  io.netty.handler.codec.http2.Http2FrameCodec
 *  io.netty.handler.codec.http2.Http2FrameCodecBuilder
 *  io.netty.handler.codec.http2.Http2FrameLogger
 *  io.netty.handler.codec.http2.Http2MultiplexHandler
 *  io.netty.handler.codec.http2.Http2Settings
 *  io.netty.handler.logging.LogLevel
 *  io.netty.handler.logging.LoggingHandler
 *  io.netty.handler.ssl.SslContext
 *  io.netty.handler.ssl.SslHandler
 *  io.netty.handler.ssl.SslProvider
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.Protocol
 *  software.amazon.awssdk.utils.NumericUtils
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.ChannelDiagnostics;
import software.amazon.awssdk.http.nio.netty.internal.FutureCancelHandler;
import software.amazon.awssdk.http.nio.netty.internal.IdleConnectionReaperHandler;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.OldConnectionReaperHandler;
import software.amazon.awssdk.http.nio.netty.internal.SslCloseCompletionEventHandler;
import software.amazon.awssdk.http.nio.netty.internal.UnusedChannelExceptionHandler;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2GoAwayEventListener;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2PingHandler;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2SettingsFrameHandler;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class ChannelPipelineInitializer
extends AbstractChannelPoolHandler {
    private final Protocol protocol;
    private final SslContext sslCtx;
    private final SslProvider sslProvider;
    private final long clientMaxStreams;
    private final int clientInitialWindowSize;
    private final Duration healthCheckPingPeriod;
    private final AtomicReference<ChannelPool> channelPoolRef;
    private final NettyConfiguration configuration;
    private final URI poolKey;

    public ChannelPipelineInitializer(Protocol protocol, SslContext sslCtx, SslProvider sslProvider, long clientMaxStreams, int clientInitialWindowSize, Duration healthCheckPingPeriod, AtomicReference<ChannelPool> channelPoolRef, NettyConfiguration configuration, URI poolKey) {
        this.protocol = protocol;
        this.sslCtx = sslCtx;
        this.sslProvider = sslProvider;
        this.clientMaxStreams = clientMaxStreams;
        this.clientInitialWindowSize = clientInitialWindowSize;
        this.healthCheckPingPeriod = healthCheckPingPeriod;
        this.channelPoolRef = channelPoolRef;
        this.configuration = configuration;
        this.poolKey = poolKey;
    }

    public void channelCreated(Channel ch) {
        ch.attr(ChannelAttributeKey.CHANNEL_DIAGNOSTICS).set((Object)new ChannelDiagnostics(ch));
        ch.attr(ChannelAttributeKey.PROTOCOL_FUTURE).set(new CompletableFuture());
        ChannelPipeline pipeline = ch.pipeline();
        if (this.sslCtx != null) {
            SslHandler sslHandler = NettyUtils.newSslHandler(this.sslCtx, ch.alloc(), this.poolKey.getHost(), this.poolKey.getPort(), this.configuration.tlsHandshakeTimeout());
            pipeline.addLast(new ChannelHandler[]{sslHandler});
            pipeline.addLast(new ChannelHandler[]{SslCloseCompletionEventHandler.getInstance()});
            if (this.sslProvider == SslProvider.JDK) {
                ch.config().setOption(ChannelOption.ALLOCATOR, (Object)UnpooledByteBufAllocator.DEFAULT);
            }
        }
        if (this.protocol == Protocol.HTTP2) {
            this.configureHttp2(ch, pipeline);
        } else {
            this.configureHttp11(ch, pipeline);
        }
        if (this.configuration.reapIdleConnections()) {
            pipeline.addLast(new ChannelHandler[]{new IdleConnectionReaperHandler(this.configuration.idleTimeoutMillis())});
        }
        if (this.configuration.connectionTtlMillis() > 0) {
            pipeline.addLast(new ChannelHandler[]{new OldConnectionReaperHandler(this.configuration.connectionTtlMillis())});
        }
        pipeline.addLast(new ChannelHandler[]{FutureCancelHandler.getInstance()});
        if (this.protocol == Protocol.HTTP1_1) {
            pipeline.addLast(new ChannelHandler[]{UnusedChannelExceptionHandler.getInstance()});
        }
        pipeline.addLast(new ChannelHandler[]{new LoggingHandler(LogLevel.DEBUG)});
    }

    private void configureHttp2(Channel ch, ChannelPipeline pipeline) {
        Http2FrameCodec codec = Http2FrameCodecBuilder.forClient().headerSensitivityDetector((name, value) -> StringUtils.lowerCase((String)name.toString()).equals("authorization")).initialSettings(Http2Settings.defaultSettings().initialWindowSize(this.clientInitialWindowSize)).frameLogger(new Http2FrameLogger(LogLevel.DEBUG)).build();
        codec.connection().addListener((Http2Connection.Listener)new Http2GoAwayEventListener(ch));
        pipeline.addLast(new ChannelHandler[]{codec});
        ch.attr(ChannelAttributeKey.HTTP2_CONNECTION).set((Object)codec.connection());
        ch.attr(ChannelAttributeKey.HTTP2_INITIAL_WINDOW_SIZE).set((Object)this.clientInitialWindowSize);
        pipeline.addLast(new ChannelHandler[]{new Http2MultiplexHandler((ChannelHandler)new NoOpChannelInitializer())});
        pipeline.addLast(new ChannelHandler[]{new Http2SettingsFrameHandler(ch, this.clientMaxStreams, this.channelPoolRef)});
        if (this.healthCheckPingPeriod == null) {
            pipeline.addLast(new ChannelHandler[]{new Http2PingHandler(5000)});
        } else if (this.healthCheckPingPeriod.toMillis() > 0L) {
            pipeline.addLast(new ChannelHandler[]{new Http2PingHandler(NumericUtils.saturatedCast((long)this.healthCheckPingPeriod.toMillis()))});
        }
    }

    private void configureHttp11(Channel ch, ChannelPipeline pipeline) {
        pipeline.addLast(new ChannelHandler[]{new HttpClientCodec()});
        ((CompletableFuture)ch.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get()).complete(Protocol.HTTP1_1);
    }

    private static class NoOpChannelInitializer
    extends ChannelInitializer<Channel> {
        private NoOpChannelInitializer() {
        }

        protected void initChannel(Channel ch) {
        }
    }
}

