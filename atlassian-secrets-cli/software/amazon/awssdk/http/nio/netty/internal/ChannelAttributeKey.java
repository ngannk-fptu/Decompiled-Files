/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.util.AttributeKey;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelDiagnostics;
import software.amazon.awssdk.http.nio.netty.internal.RequestContext;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2MultiplexedChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.http2.PingTracker;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;

@SdkInternalApi
public final class ChannelAttributeKey {
    public static final AttributeKey<CompletableFuture<Protocol>> PROTOCOL_FUTURE = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.protocolFuture");
    public static final AttributeKey<Http2MultiplexedChannelPool> HTTP2_MULTIPLEXED_CHANNEL_POOL = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.http2MultiplexedChannelPool");
    public static final AttributeKey<PingTracker> PING_TRACKER = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.h2.pingTracker");
    public static final AttributeKey<Http2Connection> HTTP2_CONNECTION = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.http2Connection");
    public static final AttributeKey<Integer> HTTP2_INITIAL_WINDOW_SIZE = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.http2InitialWindowSize");
    public static final AttributeKey<Long> MAX_CONCURRENT_STREAMS = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.maxConcurrentStreams");
    public static final AttributeKey<Http2FrameStream> HTTP2_FRAME_STREAM = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.http2FrameStream");
    public static final AttributeKey<ChannelDiagnostics> CHANNEL_DIAGNOSTICS = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.channelDiagnostics");
    public static final AttributeKey<Boolean> STREAMING_COMPLETE_KEY = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.streamingComplete");
    static final AttributeKey<Boolean> KEEP_ALIVE = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.keepAlive");
    static final AttributeKey<RequestContext> REQUEST_CONTEXT_KEY = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.requestContext");
    static final AttributeKey<Subscriber<? super ByteBuffer>> SUBSCRIBER_KEY = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.subscriber");
    static final AttributeKey<Boolean> RESPONSE_COMPLETE_KEY = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.responseComplete");
    static final AttributeKey<Integer> RESPONSE_STATUS_CODE = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.responseStatusCode");
    static final AttributeKey<Long> RESPONSE_CONTENT_LENGTH = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.responseContentLength");
    static final AttributeKey<Long> RESPONSE_DATA_READ = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.responseDataRead");
    static final AttributeKey<CompletableFuture<Void>> EXECUTE_FUTURE_KEY = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.executeFuture");
    static final AttributeKey<Long> EXECUTION_ID_KEY = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.executionId");
    static final AttributeKey<Boolean> IN_USE = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.inUse");
    static final AttributeKey<Boolean> CLOSE_ON_RELEASE = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.closeOnRelease");

    private ChannelAttributeKey() {
    }

    static Protocol getProtocolNow(Channel channel) {
        return (channel.parent() == null ? channel : channel.parent()).attr(PROTOCOL_FUTURE).get().join();
    }
}

