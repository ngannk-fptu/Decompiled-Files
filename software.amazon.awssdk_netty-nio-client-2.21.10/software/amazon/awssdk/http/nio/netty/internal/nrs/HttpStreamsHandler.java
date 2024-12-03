/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  io.netty.handler.codec.http.FullHttpMessage
 *  io.netty.handler.codec.http.HttpContent
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.LastHttpContent
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.LinkedList;
import java.util.Queue;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.nrs.HandlerPublisher;
import software.amazon.awssdk.http.nio.netty.internal.nrs.HandlerSubscriber;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpMessage;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@SdkInternalApi
abstract class HttpStreamsHandler<InT extends HttpMessage, OutT extends HttpMessage>
extends ChannelDuplexHandler {
    private static final NettyClientLogger logger = NettyClientLogger.getLogger(HttpStreamsHandler.class);
    private final Queue<Outgoing> outgoing = new LinkedList<Outgoing>();
    private final Class<InT> inClass;
    private final Class<OutT> outClass;
    private InT currentlyStreamedMessage;
    private boolean ignoreBodyRead;
    private boolean sendLastHttpContent;

    HttpStreamsHandler(Class<InT> inClass, Class<OutT> outClass) {
        this.inClass = inClass;
        this.outClass = outClass;
    }

    protected abstract boolean hasBody(InT var1);

    protected abstract InT createEmptyMessage(InT var1);

    protected abstract InT createStreamedMessage(InT var1, Publisher<HttpContent> var2);

    protected void receivedInMessage(ChannelHandlerContext ctx) {
    }

    protected void consumedInMessage(ChannelHandlerContext ctx) {
    }

    protected void receivedOutMessage(ChannelHandlerContext ctx) {
    }

    protected void sentOutMessage(ChannelHandlerContext ctx) {
    }

    protected void subscribeSubscriberToStream(StreamedHttpMessage msg, Subscriber<HttpContent> subscriber) {
        msg.subscribe(subscriber);
    }

    protected void bodyRequested(ChannelHandlerContext ctx) {
    }

    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.inClass.isInstance(msg)) {
            this.receivedInMessage(ctx);
            final HttpMessage inMsg = (HttpMessage)this.inClass.cast(msg);
            if (inMsg instanceof FullHttpMessage) {
                ctx.fireChannelRead((Object)inMsg);
                this.consumedInMessage(ctx);
            } else if (!this.hasBody(inMsg)) {
                ctx.fireChannelRead((Object)this.createEmptyMessage(inMsg));
                this.consumedInMessage(ctx);
                this.ignoreBodyRead = true;
            } else {
                this.currentlyStreamedMessage = inMsg;
                HandlerPublisher<HttpContent> publisher = new HandlerPublisher<HttpContent>(ctx.executor(), HttpContent.class){

                    @Override
                    protected void cancelled() {
                        if (ctx.executor().inEventLoop()) {
                            HttpStreamsHandler.this.handleCancelled(ctx, inMsg);
                        } else {
                            ctx.executor().execute(new Runnable(){

                                @Override
                                public void run() {
                                    HttpStreamsHandler.this.handleCancelled(ctx, inMsg);
                                }
                            });
                        }
                    }

                    @Override
                    protected void requestDemand() {
                        HttpStreamsHandler.this.bodyRequested(ctx);
                        super.requestDemand();
                    }
                };
                ctx.channel().pipeline().addAfter(ctx.name(), ctx.name() + "-body-publisher", (ChannelHandler)publisher);
                ctx.fireChannelRead((Object)this.createStreamedMessage(inMsg, (Publisher<HttpContent>)publisher));
            }
        } else if (msg instanceof HttpContent) {
            this.handleReadHttpContent(ctx, (HttpContent)msg);
        }
    }

    private void handleCancelled(ChannelHandlerContext ctx, InT msg) {
        if (this.currentlyStreamedMessage == msg) {
            this.ignoreBodyRead = true;
            ctx.read();
        }
    }

    private void handleReadHttpContent(ChannelHandlerContext ctx, HttpContent content) {
        boolean lastHttpContent = content instanceof LastHttpContent;
        if (lastHttpContent) {
            logger.debug(ctx.channel(), () -> "Received LastHttpContent " + ctx.channel() + " with ignoreBodyRead as " + this.ignoreBodyRead);
            ctx.channel().attr(ChannelAttributeKey.STREAMING_COMPLETE_KEY).set((Object)true);
        }
        if (!this.ignoreBodyRead) {
            if (lastHttpContent) {
                if (content.content().readableBytes() > 0 || !((LastHttpContent)content).trailingHeaders().isEmpty()) {
                    ctx.fireChannelRead((Object)content);
                } else {
                    ReferenceCountUtil.release((Object)content);
                }
                this.removeHandlerIfActive(ctx, ctx.name() + "-body-publisher");
                this.currentlyStreamedMessage = null;
                this.consumedInMessage(ctx);
            } else {
                ctx.fireChannelRead((Object)content);
            }
        } else {
            ReferenceCountUtil.release((Object)content);
            if (lastHttpContent) {
                this.ignoreBodyRead = false;
                if (this.currentlyStreamedMessage != null) {
                    this.removeHandlerIfActive(ctx, ctx.name() + "-body-publisher");
                }
                this.currentlyStreamedMessage = null;
            }
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.ignoreBodyRead) {
            ctx.read();
        } else {
            ctx.fireChannelReadComplete();
        }
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.outClass.isInstance(msg)) {
            Outgoing out = new Outgoing(this, (HttpMessage)this.outClass.cast(msg), promise);
            this.receivedOutMessage(ctx);
            if (this.outgoing.isEmpty()) {
                this.outgoing.add(out);
                this.flushNext(ctx);
            } else {
                this.outgoing.add(out);
            }
        } else if (msg instanceof LastHttpContent) {
            this.sendLastHttpContent = false;
            ctx.write(msg, promise);
        } else {
            ctx.write(msg, promise);
        }
    }

    protected void unbufferedWrite(final ChannelHandlerContext ctx, final Outgoing out) {
        if (out.message instanceof FullHttpMessage) {
            ctx.writeAndFlush(out.message, out.promise);
            out.promise.addListener((GenericFutureListener)new ChannelFutureListener(){

                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    HttpStreamsHandler.this.executeInEventLoop(ctx, new Runnable(){

                        @Override
                        public void run() {
                            HttpStreamsHandler.this.sentOutMessage(ctx);
                            HttpStreamsHandler.this.outgoing.remove();
                            HttpStreamsHandler.this.flushNext(ctx);
                        }
                    });
                }
            });
        } else if (out.message instanceof StreamedHttpMessage) {
            StreamedHttpMessage streamed = (StreamedHttpMessage)out.message;
            HandlerSubscriber<HttpContent> subscriber = new HandlerSubscriber<HttpContent>(ctx.executor()){

                @Override
                protected void error(Throwable error) {
                    out.promise.tryFailure(error);
                    ctx.close();
                }

                @Override
                protected void complete() {
                    HttpStreamsHandler.this.executeInEventLoop(ctx, new Runnable(){

                        @Override
                        public void run() {
                            HttpStreamsHandler.this.completeBody(ctx);
                        }
                    });
                }
            };
            this.sendLastHttpContent = true;
            ctx.writeAndFlush(out.message);
            ctx.pipeline().addAfter(ctx.name(), ctx.name() + "-body-subscriber", (ChannelHandler)subscriber);
            this.subscribeSubscriberToStream(streamed, (Subscriber<HttpContent>)subscriber);
        }
    }

    private void completeBody(final ChannelHandlerContext ctx) {
        this.removeHandlerIfActive(ctx, ctx.name() + "-body-subscriber");
        if (this.sendLastHttpContent) {
            ChannelPromise promise = this.outgoing.peek().promise;
            ctx.writeAndFlush((Object)LastHttpContent.EMPTY_LAST_CONTENT, promise).addListener((GenericFutureListener)new ChannelFutureListener(){

                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    HttpStreamsHandler.this.executeInEventLoop(ctx, new Runnable(){

                        @Override
                        public void run() {
                            HttpStreamsHandler.this.outgoing.remove();
                            HttpStreamsHandler.this.sentOutMessage(ctx);
                            HttpStreamsHandler.this.flushNext(ctx);
                        }
                    });
                }
            });
        } else {
            this.outgoing.remove().promise.setSuccess();
            this.sentOutMessage(ctx);
            this.flushNext(ctx);
        }
    }

    private void removeHandlerIfActive(ChannelHandlerContext ctx, String name) {
        if (ctx.channel().isActive()) {
            ctx.pipeline().remove(name);
        }
    }

    private void flushNext(ChannelHandlerContext ctx) {
        if (!this.outgoing.isEmpty()) {
            this.unbufferedWrite(ctx, this.outgoing.element());
        } else {
            ctx.fireChannelWritabilityChanged();
        }
    }

    private void executeInEventLoop(ChannelHandlerContext ctx, Runnable runnable) {
        if (ctx.executor().inEventLoop()) {
            runnable.run();
        } else {
            ctx.executor().execute(runnable);
        }
    }

    static class Outgoing {
        final OutT message;
        final ChannelPromise promise;
        final /* synthetic */ HttpStreamsHandler this$0;

        Outgoing(OutT message, ChannelPromise promise) {
            this.this$0 = this$0;
            this.message = message;
            this.promise = promise;
        }
    }
}

