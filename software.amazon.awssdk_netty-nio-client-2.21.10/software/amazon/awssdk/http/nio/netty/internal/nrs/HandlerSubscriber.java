/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.utils.OrderedWriteChannelHandlerContext;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class HandlerSubscriber<T>
extends ChannelDuplexHandler
implements Subscriber<T> {
    static final long DEFAULT_LOW_WATERMARK = 4L;
    static final long DEFAULT_HIGH_WATERMARK = 16L;
    private final EventExecutor executor;
    private final long demandLowWatermark;
    private final long demandHighWatermark;
    private final AtomicBoolean hasSubscription = new AtomicBoolean();
    private volatile Subscription subscription;
    private volatile ChannelHandlerContext ctx;
    private State state = State.NO_SUBSCRIPTION_OR_CONTEXT;
    private long outstandingDemand = 0L;
    private ChannelFuture lastWriteFuture;

    public HandlerSubscriber(EventExecutor executor, long demandLowWatermark, long demandHighWatermark) {
        this.executor = executor;
        this.demandLowWatermark = demandLowWatermark;
        this.demandHighWatermark = demandHighWatermark;
    }

    public HandlerSubscriber(EventExecutor executor) {
        this(executor, 4L, 16L);
    }

    protected void error(Throwable error) {
        this.doClose();
    }

    protected void complete() {
        this.doClose();
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.verifyRegisteredWithRightExecutor(ctx);
        ctx = OrderedWriteChannelHandlerContext.wrap(ctx);
        switch (this.state) {
            case NO_SUBSCRIPTION_OR_CONTEXT: {
                this.ctx = ctx;
                this.state = State.NO_SUBSCRIPTION;
                break;
            }
            case NO_CONTEXT: {
                this.ctx = ctx;
                this.maybeStart();
                break;
            }
            case COMPLETE: {
                this.state = State.COMPLETE;
                ctx.close();
                break;
            }
            default: {
                throw new IllegalStateException("This handler must only be added to a pipeline once " + (Object)((Object)this.state));
            }
        }
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.verifyRegisteredWithRightExecutor(ctx);
        ctx.fireChannelRegistered();
    }

    private void verifyRegisteredWithRightExecutor(ChannelHandlerContext ctx) {
        if (ctx.channel().isRegistered() && !this.executor.inEventLoop()) {
            throw new IllegalArgumentException("Channel handler MUST be registered with the same EventExecutor that it is created with.");
        }
    }

    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        this.maybeRequestMore();
        ctx.fireChannelWritabilityChanged();
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.state == State.INACTIVE) {
            this.state = State.RUNNING;
            this.maybeRequestMore();
        }
        ctx.fireChannelActive();
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.cancel();
        ctx.fireChannelInactive();
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cancel();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.cancel();
        ctx.fireExceptionCaught(cause);
    }

    private void cancel() {
        switch (this.state) {
            case NO_SUBSCRIPTION: {
                this.state = State.CANCELLED;
                break;
            }
            case RUNNING: 
            case INACTIVE: {
                this.subscription.cancel();
                this.state = State.CANCELLED;
                break;
            }
        }
    }

    public void onSubscribe(Subscription subscription) {
        if (subscription == null) {
            throw new NullPointerException("Null subscription");
        }
        if (!this.hasSubscription.compareAndSet(false, true)) {
            subscription.cancel();
        } else {
            this.subscription = subscription;
            this.executor.execute(new Runnable(){

                @Override
                public void run() {
                    HandlerSubscriber.this.provideSubscription();
                }
            });
        }
    }

    private void provideSubscription() {
        switch (this.state) {
            case NO_SUBSCRIPTION_OR_CONTEXT: {
                this.state = State.NO_CONTEXT;
                break;
            }
            case NO_SUBSCRIPTION: {
                this.maybeStart();
                break;
            }
            case CANCELLED: {
                this.subscription.cancel();
                break;
            }
        }
    }

    private void maybeStart() {
        if (this.ctx.channel().isActive()) {
            this.state = State.RUNNING;
            this.maybeRequestMore();
        } else {
            this.state = State.INACTIVE;
        }
    }

    public void onNext(T t) {
        Validate.notNull(t, (String)"Event must not be null.", (Object[])new Object[0]);
        this.lastWriteFuture = this.ctx.writeAndFlush(t);
        this.lastWriteFuture.addListener((GenericFutureListener)new ChannelFutureListener(){

            public void operationComplete(ChannelFuture future) throws Exception {
                HandlerSubscriber.this.outstandingDemand--;
                HandlerSubscriber.this.maybeRequestMore();
            }
        });
    }

    public void onError(Throwable error) {
        if (error == null) {
            throw new NullPointerException("Null error published");
        }
        this.error(error);
    }

    public void onComplete() {
        if (this.lastWriteFuture == null) {
            this.complete();
        } else {
            this.lastWriteFuture.addListener((GenericFutureListener)new ChannelFutureListener(){

                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    HandlerSubscriber.this.complete();
                }
            });
        }
    }

    private void doClose() {
        this.executor.execute(new Runnable(){

            @Override
            public void run() {
                switch (HandlerSubscriber.this.state) {
                    case NO_SUBSCRIPTION: 
                    case RUNNING: 
                    case INACTIVE: {
                        HandlerSubscriber.this.ctx.close();
                        HandlerSubscriber.this.state = State.COMPLETE;
                        break;
                    }
                }
            }
        });
    }

    private void maybeRequestMore() {
        if (this.outstandingDemand <= this.demandLowWatermark && this.ctx.channel().isWritable()) {
            long toRequest = this.demandHighWatermark - this.outstandingDemand;
            this.outstandingDemand = this.demandHighWatermark;
            this.subscription.request(toRequest);
        }
    }

    static enum State {
        NO_SUBSCRIPTION_OR_CONTEXT,
        NO_SUBSCRIPTION,
        NO_CONTEXT,
        INACTIVE,
        RUNNING,
        CANCELLED,
        COMPLETE;

    }
}

