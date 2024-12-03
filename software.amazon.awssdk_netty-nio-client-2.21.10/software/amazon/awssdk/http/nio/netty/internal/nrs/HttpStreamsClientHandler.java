/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  io.netty.handler.codec.http.FullHttpResponse
 *  io.netty.handler.codec.http.HttpContent
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.HttpResponse
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpUtil
 *  io.netty.handler.codec.http.LastHttpContent
 *  io.netty.util.ReferenceCountUtil
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.CancelledSubscriber;
import software.amazon.awssdk.http.nio.netty.internal.nrs.DelegateStreamedHttpResponse;
import software.amazon.awssdk.http.nio.netty.internal.nrs.EmptyHttpResponse;
import software.amazon.awssdk.http.nio.netty.internal.nrs.HttpStreamsHandler;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpMessage;

@SdkInternalApi
public class HttpStreamsClientHandler
extends HttpStreamsHandler<HttpResponse, HttpRequest> {
    private int inFlight = 0;
    private int withServer = 0;
    private ChannelPromise closeOnZeroInFlight = null;
    private Subscriber<HttpContent> awaiting100Continue;
    private StreamedHttpMessage awaiting100ContinueMessage;
    private boolean ignoreResponseBody = false;

    public HttpStreamsClientHandler() {
        super(HttpResponse.class, HttpRequest.class);
    }

    @Override
    protected boolean hasBody(HttpResponse response) {
        if (response.status().code() >= 100 && response.status().code() < 200) {
            return false;
        }
        if (response.status().equals((Object)HttpResponseStatus.NO_CONTENT) || response.status().equals((Object)HttpResponseStatus.NOT_MODIFIED)) {
            return false;
        }
        if (HttpUtil.isTransferEncodingChunked((HttpMessage)response)) {
            return true;
        }
        if (HttpUtil.isContentLengthSet((HttpMessage)response)) {
            return HttpUtil.getContentLength((HttpMessage)response) > 0L;
        }
        return true;
    }

    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        if (this.inFlight == 0) {
            ctx.close(future);
        } else {
            this.closeOnZeroInFlight = future;
        }
    }

    @Override
    protected void consumedInMessage(ChannelHandlerContext ctx) {
        --this.inFlight;
        --this.withServer;
        if (this.inFlight == 0 && this.closeOnZeroInFlight != null) {
            ctx.close(this.closeOnZeroInFlight);
        }
    }

    @Override
    protected void receivedOutMessage(ChannelHandlerContext ctx) {
        ++this.inFlight;
    }

    @Override
    protected void sentOutMessage(ChannelHandlerContext ctx) {
        ++this.withServer;
    }

    @Override
    protected HttpResponse createEmptyMessage(HttpResponse response) {
        return new EmptyHttpResponse(response);
    }

    @Override
    protected HttpResponse createStreamedMessage(HttpResponse response, Publisher<HttpContent> stream) {
        return new DelegateStreamedHttpResponse(response, stream);
    }

    @Override
    protected void subscribeSubscriberToStream(StreamedHttpMessage msg, Subscriber<HttpContent> subscriber) {
        if (HttpUtil.is100ContinueExpected((HttpMessage)msg)) {
            this.awaiting100Continue = subscriber;
            this.awaiting100ContinueMessage = msg;
        } else {
            super.subscribeSubscriberToStream(msg, subscriber);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse && this.awaiting100Continue != null && this.withServer == 0) {
            HttpResponse response = (HttpResponse)msg;
            if (response.status().equals((Object)HttpResponseStatus.CONTINUE)) {
                super.subscribeSubscriberToStream(this.awaiting100ContinueMessage, this.awaiting100Continue);
                this.awaiting100Continue = null;
                this.awaiting100ContinueMessage = null;
                if (msg instanceof FullHttpResponse) {
                    ReferenceCountUtil.release((Object)msg);
                } else {
                    this.ignoreResponseBody = true;
                }
            } else {
                this.awaiting100ContinueMessage.subscribe(new CancelledSubscriber());
                this.awaiting100ContinueMessage = null;
                this.awaiting100Continue.onSubscribe((Subscription)new NoOpSubscription());
                this.awaiting100Continue.onComplete();
                this.awaiting100Continue = null;
                super.channelRead(ctx, msg);
            }
        } else if (this.ignoreResponseBody && msg instanceof HttpContent) {
            ReferenceCountUtil.release((Object)msg);
            if (msg instanceof LastHttpContent) {
                this.ignoreResponseBody = false;
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private static class NoOpSubscription
    implements Subscription {
        private NoOpSubscription() {
        }

        public void request(long n) {
        }

        public void cancel() {
        }
    }
}

