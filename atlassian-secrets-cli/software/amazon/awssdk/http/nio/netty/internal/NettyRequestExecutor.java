/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.Attribute;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.FutureCancelledException;
import software.amazon.awssdk.http.nio.netty.internal.NettyRequestMetrics;
import software.amazon.awssdk.http.nio.netty.internal.OneTimeReadTimeoutHandler;
import software.amazon.awssdk.http.nio.netty.internal.RequestAdapter;
import software.amazon.awssdk.http.nio.netty.internal.RequestContext;
import software.amazon.awssdk.http.nio.netty.internal.ResponseHandler;
import software.amazon.awssdk.http.nio.netty.internal.http2.FlushOnReadHandler;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2StreamExceptionHandler;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2ToHttpInboundAdapter;
import software.amazon.awssdk.http.nio.netty.internal.http2.HttpToHttp2OutboundAdapter;
import software.amazon.awssdk.http.nio.netty.internal.nrs.HttpStreamsClientHandler;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpRequest;
import software.amazon.awssdk.http.nio.netty.internal.utils.ChannelUtils;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public final class NettyRequestExecutor {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(NettyRequestExecutor.class);
    private static final RequestAdapter REQUEST_ADAPTER_HTTP2 = new RequestAdapter(Protocol.HTTP2);
    private static final RequestAdapter REQUEST_ADAPTER_HTTP1_1 = new RequestAdapter(Protocol.HTTP1_1);
    private static final AtomicLong EXECUTION_COUNTER = new AtomicLong(0L);
    private final long executionId = EXECUTION_COUNTER.incrementAndGet();
    private final RequestContext context;
    private CompletableFuture<Void> executeFuture;
    private Channel channel;
    private RequestAdapter requestAdapter;

    public NettyRequestExecutor(RequestContext context) {
        this.context = context;
    }

    public CompletableFuture<Void> execute() {
        Promise<Channel> channelFuture = this.context.eventLoopGroup().next().newPromise();
        this.executeFuture = this.createExecutionFuture(channelFuture);
        this.acquireChannel(channelFuture);
        channelFuture.addListener(this::makeRequestListener);
        return this.executeFuture;
    }

    private void acquireChannel(Promise<Channel> channelFuture) {
        NettyRequestMetrics.ifMetricsAreEnabled(this.context.metricCollector(), metrics -> NettyRequestMetrics.measureTimeTaken(channelFuture, duration -> metrics.reportMetric(HttpMetric.CONCURRENCY_ACQUIRE_DURATION, duration)));
        this.context.channelPool().acquire(channelFuture);
    }

    private CompletableFuture<Void> createExecutionFuture(Promise<Channel> channelPromise) {
        CompletableFuture<Void> metricsFuture = this.initiateMetricsCollection();
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        future.whenComplete((r, t) -> {
            this.verifyMetricsWereCollected(metricsFuture);
            if (t == null) {
                return;
            }
            if (!channelPromise.tryFailure((Throwable)t)) {
                if (!channelPromise.isSuccess()) {
                    return;
                }
                Channel ch = (Channel)channelPromise.getNow();
                try {
                    ch.eventLoop().submit(() -> {
                        Attribute<Long> executionIdKey = ch.attr(ChannelAttributeKey.EXECUTION_ID_KEY);
                        if (ch.attr(ChannelAttributeKey.IN_USE) != null && ch.attr(ChannelAttributeKey.IN_USE).get().booleanValue() && executionIdKey != null) {
                            ch.pipeline().fireExceptionCaught(new FutureCancelledException(this.executionId, (Throwable)t));
                        } else {
                            ch.close().addListener((GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener<Future>)closeFuture -> this.context.channelPool().release(ch)));
                        }
                    });
                }
                catch (Throwable exc) {
                    log.warn(ch, () -> "Unable to add a task to cancel the request to channel's EventLoop", exc);
                }
            }
        });
        return future;
    }

    private CompletableFuture<Void> initiateMetricsCollection() {
        MetricCollector metricCollector = this.context.metricCollector();
        if (!NettyRequestMetrics.metricsAreEnabled(metricCollector)) {
            return null;
        }
        return this.context.channelPool().collectChannelPoolMetrics(metricCollector);
    }

    private void verifyMetricsWereCollected(CompletableFuture<Void> metricsFuture) {
        if (metricsFuture == null) {
            return;
        }
        if (!metricsFuture.isDone()) {
            log.debug(null, () -> "HTTP request metric collection did not finish in time, so results may be incomplete.");
            metricsFuture.cancel(false);
            return;
        }
        metricsFuture.exceptionally(t -> {
            log.debug(null, () -> "HTTP request metric collection failed, so results may be incomplete.", (Throwable)t);
            return null;
        });
    }

    private void makeRequestListener(Future<Channel> channelFuture) {
        if (channelFuture.isSuccess()) {
            this.channel = channelFuture.getNow();
            NettyUtils.doInEventLoop(this.channel.eventLoop(), () -> {
                try {
                    this.configureChannel();
                    this.configurePipeline();
                    this.makeRequest();
                }
                catch (Throwable t) {
                    this.closeAndRelease(this.channel);
                    this.handleFailure(this.channel, () -> "Failed to initiate request to " + this.endpoint(), t);
                }
            });
        } else {
            this.handleFailure(this.channel, () -> "Failed to create connection to " + this.endpoint(), channelFuture.cause());
        }
    }

    private void configureChannel() {
        this.channel.attr(ChannelAttributeKey.EXECUTION_ID_KEY).set(this.executionId);
        this.channel.attr(ChannelAttributeKey.EXECUTE_FUTURE_KEY).set(this.executeFuture);
        this.channel.attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).set(this.context);
        this.channel.attr(ChannelAttributeKey.RESPONSE_COMPLETE_KEY).set(false);
        this.channel.attr(ChannelAttributeKey.STREAMING_COMPLETE_KEY).set(false);
        this.channel.attr(ChannelAttributeKey.RESPONSE_CONTENT_LENGTH).set(null);
        this.channel.attr(ChannelAttributeKey.RESPONSE_DATA_READ).set(null);
        this.channel.attr(ChannelAttributeKey.CHANNEL_DIAGNOSTICS).get().incrementRequestCount();
        this.channel.config().setOption(ChannelOption.AUTO_READ, false);
    }

    private void configurePipeline() throws IOException {
        Protocol protocol = ChannelAttributeKey.getProtocolNow(this.channel);
        ChannelPipeline pipeline = this.channel.pipeline();
        switch (protocol) {
            case HTTP2: {
                pipeline.addLast(new Http2ToHttpInboundAdapter());
                pipeline.addLast(new HttpToHttp2OutboundAdapter());
                pipeline.addLast(Http2StreamExceptionHandler.create());
                this.requestAdapter = REQUEST_ADAPTER_HTTP2;
                break;
            }
            case HTTP1_1: {
                this.requestAdapter = REQUEST_ADAPTER_HTTP1_1;
                break;
            }
            default: {
                throw new IOException("Unknown protocol: " + (Object)((Object)protocol));
            }
        }
        if (protocol == Protocol.HTTP2) {
            pipeline.addLast(FlushOnReadHandler.getInstance());
        }
        pipeline.addLast(new HttpStreamsClientHandler());
        pipeline.addLast(ResponseHandler.getInstance());
        if (!this.channel.isActive()) {
            throw new IOException(NettyUtils.closedChannelMessage(this.channel));
        }
    }

    private void makeRequest() {
        HttpRequest request = this.requestAdapter.adapt(this.context.executeRequest().request());
        this.writeRequest(request);
    }

    private void writeRequest(HttpRequest request) {
        this.channel.pipeline().addFirst(new WriteTimeoutHandler(this.context.configuration().writeTimeoutMillis(), TimeUnit.MILLISECONDS));
        StreamedRequest streamedRequest = new StreamedRequest(request, this.context.executeRequest().requestContentPublisher());
        this.channel.writeAndFlush(streamedRequest).addListener((GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener<Future>)wireCall -> {
            ChannelUtils.removeIfExists(this.channel.pipeline(), WriteTimeoutHandler.class);
            if (wireCall.isSuccess()) {
                NettyRequestMetrics.publishHttp2StreamMetrics(this.context.metricCollector(), this.channel);
                if (this.context.executeRequest().fullDuplex()) {
                    return;
                }
                this.channel.pipeline().addFirst(new ReadTimeoutHandler(this.context.configuration().readTimeoutMillis(), TimeUnit.MILLISECONDS));
                this.channel.read();
            } else {
                this.closeAndRelease(this.channel);
                this.handleFailure(this.channel, () -> "Failed to make request to " + this.endpoint(), wireCall.cause());
            }
        }));
        if (this.shouldExplicitlyTriggerRead()) {
            if (this.is100ContinueExpected()) {
                this.channel.pipeline().addFirst(new OneTimeReadTimeoutHandler(Duration.ofMillis(this.context.configuration().readTimeoutMillis())));
            } else {
                this.channel.pipeline().addFirst(new ReadTimeoutHandler(this.context.configuration().readTimeoutMillis(), TimeUnit.MILLISECONDS));
            }
            this.channel.read();
        }
    }

    private boolean shouldExplicitlyTriggerRead() {
        return this.context.executeRequest().fullDuplex() || this.is100ContinueExpected();
    }

    private boolean is100ContinueExpected() {
        return this.context.executeRequest().request().firstMatchingHeader("Expect").filter(b -> b.equalsIgnoreCase("100-continue")).isPresent();
    }

    private URI endpoint() {
        return this.context.executeRequest().request().getUri();
    }

    private void handleFailure(Channel channel, Supplier<String> msgSupplier, Throwable cause) {
        log.debug(channel, msgSupplier, cause);
        cause = NettyUtils.decorateException(channel, cause);
        this.context.handler().onError(cause);
        this.executeFuture.completeExceptionally(cause);
    }

    private void closeAndRelease(Channel channel) {
        log.trace(channel, () -> String.format("closing and releasing channel %s", channel.id().asLongText()));
        channel.attr(ChannelAttributeKey.KEEP_ALIVE).set(false);
        channel.close();
        this.context.channelPool().release(channel);
    }

    private static class StreamedRequest
    extends DelegateHttpRequest
    implements StreamedHttpRequest {
        private final Publisher<ByteBuffer> publisher;
        private final Optional<Long> requestContentLength;
        private long written = 0L;
        private boolean done;
        private Subscription subscription;

        StreamedRequest(HttpRequest request, Publisher<ByteBuffer> publisher) {
            super(request);
            this.publisher = publisher;
            this.requestContentLength = StreamedRequest.contentLength(request);
        }

        @Override
        public void subscribe(final Subscriber<? super HttpContent> subscriber) {
            this.publisher.subscribe(new Subscriber<ByteBuffer>(){

                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription = subscription;
                    subscriber.onSubscribe(subscription);
                }

                @Override
                public void onNext(ByteBuffer contentBytes) {
                    if (done) {
                        return;
                    }
                    try {
                        int newLimit = this.clampedBufferLimit(contentBytes.remaining());
                        contentBytes.limit(contentBytes.position() + newLimit);
                        ByteBuf contentByteBuf = Unpooled.wrappedBuffer(contentBytes);
                        DefaultHttpContent content = new DefaultHttpContent(contentByteBuf);
                        subscriber.onNext(content);
                        written = written + (long)newLimit;
                        if (!this.shouldContinuePublishing()) {
                            done = true;
                            subscription.cancel();
                            subscriber.onComplete();
                        }
                    }
                    catch (Throwable t) {
                        this.onError(t);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!done) {
                        done = true;
                        subscription.cancel();
                        subscriber.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (!done) {
                        Long expectedContentLength = requestContentLength.orElse(null);
                        if (expectedContentLength != null && written < expectedContentLength) {
                            this.onError(new IllegalStateException("Request content was only " + written + " bytes, but the specified content-length was " + expectedContentLength + " bytes."));
                        } else {
                            done = true;
                            subscriber.onComplete();
                        }
                    }
                }
            });
        }

        private int clampedBufferLimit(int bufLen) {
            return this.requestContentLength.map(cl -> (int)Math.min(cl - this.written, (long)bufLen)).orElse(bufLen);
        }

        private boolean shouldContinuePublishing() {
            return this.requestContentLength.map(cl -> this.written < cl).orElse(true);
        }

        private static Optional<Long> contentLength(HttpRequest request) {
            String value = request.headers().get("Content-Length");
            if (value != null) {
                try {
                    return Optional.of(Long.parseLong(value));
                }
                catch (NumberFormatException e) {
                    log.warn(null, () -> "Unable  to parse 'Content-Length' header. Treating it as non existent.");
                }
            }
            return Optional.empty();
        }
    }

    static class DelegateHttpRequest
    implements HttpRequest {
        protected final HttpRequest request;

        DelegateHttpRequest(HttpRequest request) {
            this.request = request;
        }

        @Override
        public HttpRequest setMethod(HttpMethod method) {
            this.request.setMethod(method);
            return this;
        }

        @Override
        public HttpRequest setUri(String uri) {
            this.request.setUri(uri);
            return this;
        }

        @Override
        public HttpMethod getMethod() {
            return this.request.method();
        }

        @Override
        public HttpMethod method() {
            return this.request.method();
        }

        @Override
        public String getUri() {
            return this.request.uri();
        }

        @Override
        public String uri() {
            return this.request.uri();
        }

        @Override
        public HttpVersion getProtocolVersion() {
            return this.request.protocolVersion();
        }

        @Override
        public HttpVersion protocolVersion() {
            return this.request.protocolVersion();
        }

        @Override
        public HttpRequest setProtocolVersion(HttpVersion version) {
            this.request.setProtocolVersion(version);
            return this;
        }

        @Override
        public HttpHeaders headers() {
            return this.request.headers();
        }

        @Override
        public DecoderResult getDecoderResult() {
            return this.request.decoderResult();
        }

        @Override
        public DecoderResult decoderResult() {
            return this.request.decoderResult();
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
            this.request.setDecoderResult(result);
        }

        public String toString() {
            return this.getClass().getName() + "(" + this.request.toString() + ")";
        }
    }
}

