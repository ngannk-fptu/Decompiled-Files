/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  io.netty.channel.ChannelPromise
 *  io.netty.channel.DefaultChannelPromise
 *  io.netty.handler.codec.http.EmptyHttpHeaders
 *  io.netty.handler.codec.http.FullHttpMessage
 *  io.netty.handler.codec.http.HttpContent
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.LastHttpContent
 *  io.netty.handler.codec.http2.DefaultHttp2DataFrame
 *  io.netty.handler.codec.http2.DefaultHttp2HeadersFrame
 *  io.netty.handler.codec.http2.EmptyHttp2Headers
 *  io.netty.handler.codec.http2.Http2Headers
 *  io.netty.handler.codec.http2.HttpConversionUtil
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.EventExecutor
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.EmptyHttp2Headers;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class HttpToHttp2OutboundAdapter
extends ChannelOutboundHandlerAdapter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (!(msg instanceof HttpMessage) && !(msg instanceof HttpContent)) {
            ctx.write(msg, promise);
            return;
        }
        boolean release = true;
        SimpleChannelPromiseAggregator promiseAggregator = new SimpleChannelPromiseAggregator(promise, ctx.channel(), ctx.executor());
        try {
            boolean endStream = false;
            if (msg instanceof HttpMessage) {
                HttpMessage httpMsg = (HttpMessage)msg;
                Http2Headers http2Headers = HttpConversionUtil.toHttp2Headers((HttpMessage)httpMsg, (boolean)false);
                endStream = msg instanceof FullHttpMessage && !((FullHttpMessage)msg).content().isReadable();
                ctx.write((Object)new DefaultHttp2HeadersFrame(http2Headers), promiseAggregator.newPromise());
            }
            if (!endStream && msg instanceof HttpContent) {
                boolean isLastContent = false;
                EmptyHttpHeaders trailers = EmptyHttpHeaders.INSTANCE;
                EmptyHttp2Headers http2Trailers = EmptyHttp2Headers.INSTANCE;
                if (msg instanceof LastHttpContent) {
                    isLastContent = true;
                    LastHttpContent lastContent = (LastHttpContent)msg;
                    trailers = lastContent.trailingHeaders();
                    http2Trailers = HttpConversionUtil.toHttp2Headers((HttpHeaders)trailers, (boolean)false);
                }
                ByteBuf content = ((HttpContent)msg).content();
                endStream = isLastContent && trailers.isEmpty();
                release = false;
                ctx.write((Object)new DefaultHttp2DataFrame(content, endStream), promiseAggregator.newPromise());
                if (!trailers.isEmpty()) {
                    ctx.write((Object)new DefaultHttp2HeadersFrame((Http2Headers)http2Trailers, true), promiseAggregator.newPromise());
                }
                ctx.flush();
            }
        }
        catch (Throwable t) {
            promiseAggregator.setFailure(t);
        }
        finally {
            if (release) {
                ReferenceCountUtil.release((Object)msg);
            }
            promiseAggregator.doneAllocatingPromises();
        }
    }

    static final class SimpleChannelPromiseAggregator
    extends DefaultChannelPromise {
        private final ChannelPromise promise;
        private int expectedCount;
        private int doneCount;
        private Throwable lastFailure;
        private boolean doneAllocating;

        SimpleChannelPromiseAggregator(ChannelPromise promise, Channel c, EventExecutor e) {
            super(c, e);
            assert (promise != null && !promise.isDone());
            this.promise = promise;
        }

        public ChannelPromise newPromise() {
            assert (!this.doneAllocating) : "Done allocating. No more promises can be allocated.";
            ++this.expectedCount;
            return this;
        }

        public ChannelPromise doneAllocatingPromises() {
            if (!this.doneAllocating) {
                this.doneAllocating = true;
                if (this.doneCount == this.expectedCount || this.expectedCount == 0) {
                    return this.setPromise();
                }
            }
            return this;
        }

        public boolean tryFailure(Throwable cause) {
            if (this.allowFailure()) {
                ++this.doneCount;
                this.lastFailure = cause;
                if (this.allPromisesDone()) {
                    return this.tryPromise();
                }
                return true;
            }
            return false;
        }

        public ChannelPromise setFailure(Throwable cause) {
            if (this.allowFailure()) {
                ++this.doneCount;
                this.lastFailure = cause;
                if (this.allPromisesDone()) {
                    return this.setPromise();
                }
            }
            return this;
        }

        public ChannelPromise setSuccess(Void result) {
            if (this.awaitingPromises()) {
                ++this.doneCount;
                if (this.allPromisesDone()) {
                    this.setPromise();
                }
            }
            return this;
        }

        public boolean trySuccess(Void result) {
            if (this.awaitingPromises()) {
                ++this.doneCount;
                if (this.allPromisesDone()) {
                    return this.tryPromise();
                }
                return true;
            }
            return false;
        }

        private boolean allowFailure() {
            return this.awaitingPromises() || this.expectedCount == 0;
        }

        private boolean awaitingPromises() {
            return this.doneCount < this.expectedCount;
        }

        private boolean allPromisesDone() {
            return this.doneCount == this.expectedCount && this.doneAllocating;
        }

        private ChannelPromise setPromise() {
            if (this.lastFailure == null) {
                this.promise.setSuccess();
                return super.setSuccess(null);
            }
            this.promise.setFailure(this.lastFailure);
            return super.setFailure(this.lastFailure);
        }

        private boolean tryPromise() {
            if (this.lastFailure == null) {
                this.promise.trySuccess();
                return super.trySuccess(null);
            }
            this.promise.tryFailure(this.lastFailure);
            return super.tryFailure(this.lastFailure);
        }
    }
}

