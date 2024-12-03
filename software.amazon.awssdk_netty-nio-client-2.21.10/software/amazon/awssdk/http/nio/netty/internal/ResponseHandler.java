/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.handler.codec.http.FullHttpResponse
 *  io.netty.handler.codec.http.HttpContent
 *  io.netty.handler.codec.http.HttpHeaderNames
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.HttpObject
 *  io.netty.handler.codec.http.HttpResponse
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpUtil
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.ReferenceCounted
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.HttpStatusFamily
 *  software.amazon.awssdk.http.Protocol
 *  software.amazon.awssdk.http.SdkCancellationException
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpMethod
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler
 *  software.amazon.awssdk.utils.FunctionalUtils$UnsafeRunnable
 *  software.amazon.awssdk.utils.async.DelegatingSubscription
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.HttpStatusFamily;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SdkCancellationException;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.ChannelDiagnostics;
import software.amazon.awssdk.http.nio.netty.internal.LastHttpContentSwallower;
import software.amazon.awssdk.http.nio.netty.internal.RequestContext;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2ResetSendingSubscription;
import software.amazon.awssdk.http.nio.netty.internal.nrs.HttpStreamsClientHandler;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpResponse;
import software.amazon.awssdk.http.nio.netty.internal.utils.ChannelUtils;
import software.amazon.awssdk.http.nio.netty.internal.utils.ExceptionHandlingUtils;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.async.DelegatingSubscription;

@ChannelHandler.Sharable
@SdkInternalApi
public class ResponseHandler
extends SimpleChannelInboundHandler<HttpObject> {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(ResponseHandler.class);
    private static final ResponseHandler INSTANCE = new ResponseHandler();

    private ResponseHandler() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void channelRead0(ChannelHandlerContext channelContext, HttpObject msg) throws Exception {
        RequestContext requestContext = (RequestContext)channelContext.channel().attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).get();
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse)msg;
            SdkHttpFullResponse sdkResponse = SdkHttpFullResponse.builder().headers(ResponseHandler.fromNettyHeaders(response.headers())).statusCode(response.status().code()).statusText(response.status().reasonPhrase()).build();
            channelContext.channel().attr(ChannelAttributeKey.RESPONSE_STATUS_CODE).set((Object)response.status().code());
            channelContext.channel().attr(ChannelAttributeKey.RESPONSE_CONTENT_LENGTH).set((Object)this.responseContentLength(response));
            channelContext.channel().attr(ChannelAttributeKey.KEEP_ALIVE).set((Object)this.shouldKeepAlive(response));
            ChannelUtils.getAttribute(channelContext.channel(), ChannelAttributeKey.CHANNEL_DIAGNOSTICS).ifPresent(ChannelDiagnostics::incrementResponseCount);
            requestContext.handler().onHeaders((SdkHttpResponse)sdkResponse);
        }
        CompletableFuture<Void> ef = ResponseHandler.executeFuture(channelContext);
        if (msg instanceof StreamedHttpResponse) {
            requestContext.handler().onStream((Publisher)new DataCountingPublisher(channelContext, new PublisherAdapter((StreamedHttpResponse)msg, channelContext, requestContext, ef)));
        } else if (msg instanceof FullHttpResponse) {
            ByteBuf fullContent = null;
            try {
                channelContext.pipeline().replace(HttpStreamsClientHandler.class, channelContext.name() + "-LastHttpContentSwallower", (ChannelHandler)LastHttpContentSwallower.getInstance());
                fullContent = ((FullHttpResponse)msg).content();
                ByteBuffer bb = ResponseHandler.copyToByteBuffer(fullContent);
                requestContext.handler().onStream((Publisher)new DataCountingPublisher(channelContext, new FullResponseContentPublisher(channelContext, bb, ef)));
                try {
                    ResponseHandler.validateResponseContentLength(channelContext);
                    ResponseHandler.finalizeResponse(requestContext, channelContext);
                }
                catch (IOException e) {
                    this.exceptionCaught(channelContext, e);
                }
            }
            finally {
                Optional.ofNullable(fullContent).ifPresent(ReferenceCounted::release);
            }
        }
    }

    private Long responseContentLength(HttpResponse response) {
        String length = response.headers().get((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
        if (length == null) {
            return null;
        }
        return Long.parseLong(length);
    }

    private static void validateResponseContentLength(ChannelHandlerContext ctx) throws IOException {
        if (!ResponseHandler.shouldValidateResponseContentLength(ctx)) {
            return;
        }
        Long contentLengthHeader = (Long)ctx.channel().attr(ChannelAttributeKey.RESPONSE_CONTENT_LENGTH).get();
        Long actualContentLength = (Long)ctx.channel().attr(ChannelAttributeKey.RESPONSE_DATA_READ).get();
        if (contentLengthHeader == null) {
            return;
        }
        if (actualContentLength == null) {
            actualContentLength = 0L;
        }
        if (actualContentLength.equals(contentLengthHeader)) {
            return;
        }
        throw new IOException("Response had content-length of " + contentLengthHeader + " bytes, but only received " + actualContentLength + " bytes before the connection was closed.");
    }

    private static boolean shouldValidateResponseContentLength(ChannelHandlerContext ctx) {
        RequestContext requestContext = (RequestContext)ctx.channel().attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).get();
        if (requestContext.executeRequest().request().method() == SdkHttpMethod.HEAD) {
            return false;
        }
        Integer responseStatusCode = (Integer)ctx.channel().attr(ChannelAttributeKey.RESPONSE_STATUS_CODE).get();
        return responseStatusCode == null || responseStatusCode.intValue() != HttpResponseStatus.NOT_MODIFIED.code();
    }

    private static void finalizeResponse(RequestContext requestContext, ChannelHandlerContext channelContext) {
        channelContext.channel().attr(ChannelAttributeKey.RESPONSE_COMPLETE_KEY).set((Object)true);
        ResponseHandler.executeFuture(channelContext).complete(null);
        if (!((Boolean)channelContext.channel().attr(ChannelAttributeKey.KEEP_ALIVE).get()).booleanValue()) {
            ResponseHandler.closeAndRelease(channelContext);
        } else {
            requestContext.channelPool().release(channelContext.channel());
        }
    }

    private boolean shouldKeepAlive(HttpResponse response) {
        if (HttpStatusFamily.of((int)response.status().code()) == HttpStatusFamily.SERVER_ERROR) {
            return false;
        }
        return HttpUtil.isKeepAlive((HttpMessage)response);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        RequestContext requestContext = (RequestContext)ctx.channel().attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).get();
        log.debug(ctx.channel(), () -> "Exception processing request: " + requestContext.executeRequest().request(), cause);
        Throwable throwable = NettyUtils.decorateException(ctx.channel(), cause);
        ResponseHandler.executeFuture(ctx).completeExceptionally(throwable);
        ResponseHandler.runAndLogError(ctx.channel(), () -> "Fail to execute SdkAsyncHttpResponseHandler#onError", () -> requestContext.handler().onError(throwable));
        ResponseHandler.runAndLogError(ctx.channel(), () -> "Could not release channel back to the pool", () -> ResponseHandler.closeAndRelease(ctx));
    }

    public void channelInactive(ChannelHandlerContext handlerCtx) {
        this.notifyIfResponseNotCompleted(handlerCtx);
    }

    public static ResponseHandler getInstance() {
        return INSTANCE;
    }

    private static void closeAndRelease(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        channel.attr(ChannelAttributeKey.KEEP_ALIVE).set((Object)false);
        RequestContext requestContext = (RequestContext)channel.attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).get();
        ctx.close();
        requestContext.channelPool().release(channel);
    }

    private static void runAndLogError(Channel ch, Supplier<String> errorMsg, FunctionalUtils.UnsafeRunnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception e) {
            log.error(ch, errorMsg, e);
        }
    }

    private static Map<String, List<String>> fromNettyHeaders(HttpHeaders headers) {
        return headers.entries().stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    private static ByteBuffer copyToByteBuffer(ByteBuf byteBuf) {
        ByteBuffer bb = ByteBuffer.allocate(byteBuf.readableBytes());
        byteBuf.getBytes(byteBuf.readerIndex(), bb);
        bb.flip();
        return bb;
    }

    private static CompletableFuture<Void> executeFuture(ChannelHandlerContext ctx) {
        return (CompletableFuture)ctx.channel().attr(ChannelAttributeKey.EXECUTE_FUTURE_KEY).get();
    }

    private void notifyIfResponseNotCompleted(ChannelHandlerContext handlerCtx) {
        RequestContext requestCtx = (RequestContext)handlerCtx.channel().attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).get();
        Boolean responseCompleted = (Boolean)handlerCtx.channel().attr(ChannelAttributeKey.RESPONSE_COMPLETE_KEY).get();
        Boolean isStreamingComplete = (Boolean)handlerCtx.channel().attr(ChannelAttributeKey.STREAMING_COMPLETE_KEY).get();
        handlerCtx.channel().attr(ChannelAttributeKey.KEEP_ALIVE).set((Object)false);
        if (!Boolean.TRUE.equals(responseCompleted) && !Boolean.TRUE.equals(isStreamingComplete)) {
            IOException err = new IOException(NettyUtils.closedChannelMessage(handlerCtx.channel()));
            ResponseHandler.runAndLogError(handlerCtx.channel(), () -> "Fail to execute SdkAsyncHttpResponseHandler#onError", () -> requestCtx.handler().onError((Throwable)err));
            ResponseHandler.executeFuture(handlerCtx).completeExceptionally(err);
            ResponseHandler.runAndLogError(handlerCtx.channel(), () -> "Could not release channel", () -> ResponseHandler.closeAndRelease(handlerCtx));
        }
    }

    private static final class DataCountingPublisher
    implements Publisher<ByteBuffer> {
        private final ChannelHandlerContext ctx;
        private final Publisher<ByteBuffer> delegate;

        private DataCountingPublisher(ChannelHandlerContext ctx, Publisher<ByteBuffer> delegate) {
            this.ctx = ctx;
            this.delegate = delegate;
        }

        public void subscribe(final Subscriber<? super ByteBuffer> subscriber) {
            this.delegate.subscribe((Subscriber)new Subscriber<ByteBuffer>(){

                public void onSubscribe(Subscription subscription) {
                    subscriber.onSubscribe(subscription);
                }

                public void onNext(ByteBuffer byteBuffer) {
                    Long responseDataSoFar = (Long)ctx.channel().attr(ChannelAttributeKey.RESPONSE_DATA_READ).get();
                    if (responseDataSoFar == null) {
                        responseDataSoFar = 0L;
                    }
                    ctx.channel().attr(ChannelAttributeKey.RESPONSE_DATA_READ).set((Object)(responseDataSoFar + (long)byteBuffer.remaining()));
                    subscriber.onNext((Object)byteBuffer);
                }

                public void onError(Throwable throwable) {
                    subscriber.onError(throwable);
                }

                public void onComplete() {
                    subscriber.onComplete();
                }
            });
        }
    }

    static class FullResponseContentPublisher
    implements Publisher<ByteBuffer> {
        private final ChannelHandlerContext channelContext;
        private final ByteBuffer fullContent;
        private final CompletableFuture<Void> executeFuture;
        private boolean running = true;
        private Subscriber<? super ByteBuffer> subscriber;

        FullResponseContentPublisher(ChannelHandlerContext channelContext, ByteBuffer fullContent, CompletableFuture<Void> executeFuture) {
            this.channelContext = channelContext;
            this.fullContent = fullContent;
            this.executeFuture = executeFuture;
        }

        public void subscribe(final Subscriber<? super ByteBuffer> subscriber) {
            if (this.subscriber != null) {
                subscriber.onComplete();
                return;
            }
            this.subscriber = subscriber;
            this.channelContext.channel().attr(ChannelAttributeKey.SUBSCRIBER_KEY).set(subscriber);
            subscriber.onSubscribe(new Subscription(){

                public void request(long l) {
                    if (running) {
                        running = false;
                        if (l <= 0L) {
                            subscriber.onError((Throwable)new IllegalArgumentException("Demand must be positive!"));
                        } else {
                            if (fullContent.hasRemaining()) {
                                subscriber.onNext((Object)fullContent);
                            }
                            subscriber.onComplete();
                            executeFuture.complete(null);
                        }
                    }
                }

                public void cancel() {
                    running = false;
                }
            });
        }
    }

    private static class OnCancelSubscription
    extends DelegatingSubscription {
        private final Runnable onCancel;

        private OnCancelSubscription(Subscription subscription, Runnable onCancel) {
            super(subscription);
            this.onCancel = onCancel;
        }

        public void cancel() {
            this.onCancel.run();
            super.cancel();
        }
    }

    static class PublisherAdapter
    implements Publisher<ByteBuffer> {
        private final StreamedHttpResponse response;
        private final ChannelHandlerContext channelContext;
        private final RequestContext requestContext;
        private final CompletableFuture<Void> executeFuture;
        private final AtomicBoolean isDone = new AtomicBoolean(false);

        PublisherAdapter(StreamedHttpResponse response, ChannelHandlerContext channelContext, RequestContext requestContext, CompletableFuture<Void> executeFuture) {
            this.response = response;
            this.channelContext = channelContext;
            this.requestContext = requestContext;
            this.executeFuture = executeFuture;
        }

        public void subscribe(final Subscriber<? super ByteBuffer> subscriber) {
            this.response.subscribe((Subscriber)new Subscriber<HttpContent>(){

                public void onSubscribe(Subscription subscription) {
                    subscriber.onSubscribe((Subscription)new OnCancelSubscription(this.resolveSubscription(subscription), this::onCancel));
                }

                private Subscription resolveSubscription(Subscription subscription) {
                    if (ChannelAttributeKey.getProtocolNow(channelContext.channel()) == Protocol.HTTP2) {
                        return new Http2ResetSendingSubscription(channelContext, subscription);
                    }
                    return subscription;
                }

                private void onCancel() {
                    if (!isDone.compareAndSet(false, true)) {
                        return;
                    }
                    try {
                        SdkCancellationException e = new SdkCancellationException("Subscriber cancelled before all events were published");
                        log.warn(channelContext.channel(), () -> "Subscriber cancelled before all events were published");
                        executeFuture.completeExceptionally((Throwable)e);
                    }
                    finally {
                        ResponseHandler.runAndLogError(channelContext.channel(), () -> "Could not release channel back to the pool", () -> ResponseHandler.closeAndRelease(channelContext));
                    }
                }

                public void onNext(HttpContent httpContent) {
                    if (isDone.get()) {
                        ReferenceCountUtil.release((Object)httpContent);
                        return;
                    }
                    ByteBuffer byteBuffer = ExceptionHandlingUtils.tryCatchFinally(() -> ResponseHandler.copyToByteBuffer(httpContent.content()), this::onError, () -> ((HttpContent)httpContent).release());
                    if (byteBuffer != null) {
                        ExceptionHandlingUtils.tryCatch(() -> subscriber.onNext((Object)byteBuffer), this::notifyError);
                    }
                }

                public void onError(Throwable t) {
                    if (!isDone.compareAndSet(false, true)) {
                        return;
                    }
                    try {
                        ResponseHandler.runAndLogError(channelContext.channel(), () -> String.format("Subscriber %s threw an exception in onError.", subscriber), () -> subscriber.onError(t));
                        this.notifyError(t);
                    }
                    finally {
                        ResponseHandler.runAndLogError(channelContext.channel(), () -> "Could not release channel back to the pool", () -> ResponseHandler.closeAndRelease(channelContext));
                    }
                }

                public void onComplete() {
                    if (!isDone.compareAndSet(false, true)) {
                        return;
                    }
                    try {
                        ResponseHandler.validateResponseContentLength(channelContext);
                        try {
                            ResponseHandler.runAndLogError(channelContext.channel(), () -> String.format("Subscriber %s threw an exception in onComplete.", subscriber), () -> ((Subscriber)subscriber).onComplete());
                        }
                        finally {
                            ResponseHandler.finalizeResponse(requestContext, channelContext);
                        }
                    }
                    catch (IOException e) {
                        this.notifyError(e);
                        ResponseHandler.runAndLogError(channelContext.channel(), () -> "Could not release channel back to the pool", () -> ResponseHandler.closeAndRelease(channelContext));
                    }
                }

                private void notifyError(Throwable throwable) {
                    SdkAsyncHttpResponseHandler handler = requestContext.handler();
                    ResponseHandler.runAndLogError(channelContext.channel(), () -> String.format("SdkAsyncHttpResponseHandler %s threw an exception in onError.", handler), () -> handler.onError(throwable));
                    executeFuture.completeExceptionally(throwable);
                }
            });
        }
    }
}

