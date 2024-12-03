/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.internal.TypeParameterMatcher
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class HandlerPublisher<T>
extends ChannelDuplexHandler
implements Publisher<T> {
    private static final Object COMPLETE = new Object(){

        public String toString() {
            return "COMPLETE";
        }
    };
    private final EventExecutor executor;
    private final TypeParameterMatcher matcher;
    private final Queue<Object> buffer = new LinkedList<Object>();
    private final AtomicBoolean hasSubscriber = new AtomicBoolean();
    private State state = State.NO_SUBSCRIBER_OR_CONTEXT;
    private volatile Subscriber<? super T> subscriber;
    private ChannelHandlerContext ctx;
    private long outstandingDemand = 0L;
    private Throwable noSubscriberError;

    public HandlerPublisher(EventExecutor executor, Class<? extends T> subscriberMessageType) {
        this.executor = executor;
        this.matcher = TypeParameterMatcher.get(subscriberMessageType);
    }

    protected boolean acceptInboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    protected void cancelled() {
        this.ctx.close();
    }

    protected void requestDemand() {
        this.ctx.read();
    }

    public void subscribe(final Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("Null subscriber");
        }
        if (!this.hasSubscriber.compareAndSet(false, true)) {
            subscriber.onSubscribe(new Subscription(){

                public void request(long n) {
                }

                public void cancel() {
                }
            });
            subscriber.onError((Throwable)new IllegalStateException("This publisher only supports one subscriber"));
        } else {
            this.executor.execute(new Runnable(){

                @Override
                public void run() {
                    HandlerPublisher.this.provideSubscriber(subscriber);
                }
            });
        }
    }

    private void provideSubscriber(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        switch (this.state) {
            case NO_SUBSCRIBER_OR_CONTEXT: {
                this.state = State.NO_CONTEXT;
                break;
            }
            case NO_SUBSCRIBER: {
                this.state = this.buffer.isEmpty() ? State.IDLE : State.BUFFERING;
                subscriber.onSubscribe((Subscription)new ChannelSubscription());
                break;
            }
            case DRAINING: {
                subscriber.onSubscribe((Subscription)new ChannelSubscription());
                break;
            }
            case NO_SUBSCRIBER_ERROR: {
                this.cleanup();
                this.state = State.DONE;
                subscriber.onSubscribe((Subscription)new ChannelSubscription());
                subscriber.onError(this.noSubscriberError);
                break;
            }
        }
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isRegistered()) {
            this.provideChannelContext(ctx);
        }
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.provideChannelContext(ctx);
        ctx.fireChannelRegistered();
    }

    private void provideChannelContext(ChannelHandlerContext ctx) {
        switch (this.state) {
            case NO_SUBSCRIBER_OR_CONTEXT: {
                this.verifyRegisteredWithRightExecutor(ctx);
                this.ctx = ctx;
                this.state = State.NO_SUBSCRIBER;
                break;
            }
            case NO_CONTEXT: {
                this.verifyRegisteredWithRightExecutor(ctx);
                this.ctx = ctx;
                this.state = State.IDLE;
                this.subscriber.onSubscribe((Subscription)new ChannelSubscription());
                break;
            }
        }
    }

    private void verifyRegisteredWithRightExecutor(ChannelHandlerContext ctx) {
        if (!this.executor.inEventLoop()) {
            throw new IllegalArgumentException("Channel handler MUST be registered with the same EventExecutor that it is created with.");
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.state == State.DEMANDING) {
            this.requestDemand();
        }
        ctx.fireChannelActive();
    }

    private void receivedDemand(long demand) {
        switch (this.state) {
            case DRAINING: 
            case BUFFERING: {
                if (!this.addDemand(demand)) break;
                this.flushBuffer();
                break;
            }
            case DEMANDING: {
                this.addDemand(demand);
                break;
            }
            case IDLE: {
                if (!this.addDemand(demand)) break;
                this.state = State.DEMANDING;
                this.requestDemand();
                break;
            }
        }
    }

    private boolean addDemand(long demand) {
        if (demand <= 0L) {
            this.illegalDemand();
            return false;
        }
        if (this.outstandingDemand < Long.MAX_VALUE) {
            this.outstandingDemand += demand;
            if (this.outstandingDemand < 0L) {
                this.outstandingDemand = Long.MAX_VALUE;
            }
        }
        return true;
    }

    private void illegalDemand() {
        this.cleanup();
        this.subscriber.onError((Throwable)new IllegalArgumentException("Request for 0 or negative elements in violation of Section 3.9 of the Reactive Streams specification"));
        this.ctx.close();
        this.state = State.DONE;
    }

    private void flushBuffer() {
        while (!(this.buffer.isEmpty() || this.outstandingDemand <= 0L && this.outstandingDemand != Long.MAX_VALUE)) {
            this.publishMessage(this.buffer.remove());
        }
        if (this.buffer.isEmpty()) {
            if (this.outstandingDemand > 0L) {
                if (this.state == State.BUFFERING) {
                    this.state = State.DEMANDING;
                }
                this.requestDemand();
            } else if (this.state == State.BUFFERING) {
                this.state = State.IDLE;
            }
        }
    }

    private void receivedCancel() {
        switch (this.state) {
            case BUFFERING: 
            case DEMANDING: 
            case IDLE: {
                this.cancelled();
                this.state = State.DONE;
                break;
            }
            case DRAINING: {
                this.state = State.DONE;
                break;
            }
        }
        this.cleanup();
        this.subscriber = null;
    }

    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        if (this.acceptInboundMessage(message)) {
            switch (this.state) {
                case IDLE: {
                    this.buffer.add(message);
                    this.state = State.BUFFERING;
                    break;
                }
                case NO_SUBSCRIBER: 
                case BUFFERING: {
                    this.buffer.add(message);
                    break;
                }
                case DEMANDING: {
                    this.publishMessage(message);
                    break;
                }
                case DRAINING: 
                case DONE: {
                    ReferenceCountUtil.release((Object)message);
                    break;
                }
                case NO_SUBSCRIBER_OR_CONTEXT: 
                case NO_CONTEXT: {
                    throw new IllegalStateException("Message received before added to the channel context");
                }
            }
        } else {
            ctx.fireChannelRead(message);
        }
    }

    private void publishMessage(Object message) {
        if (COMPLETE.equals(message)) {
            this.subscriber.onComplete();
            this.state = State.DONE;
        } else {
            Object next = message;
            this.subscriber.onNext(next);
            if (this.outstandingDemand < Long.MAX_VALUE) {
                --this.outstandingDemand;
                if (this.outstandingDemand == 0L && this.state != State.DRAINING) {
                    this.state = this.buffer.isEmpty() ? State.IDLE : State.BUFFERING;
                }
            }
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.state == State.DEMANDING) {
            this.requestDemand();
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.complete();
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.complete();
    }

    private void complete() {
        switch (this.state) {
            case NO_SUBSCRIBER: 
            case BUFFERING: {
                this.buffer.add(COMPLETE);
                this.state = State.DRAINING;
                break;
            }
            case DEMANDING: 
            case IDLE: {
                this.subscriber.onComplete();
                this.state = State.DONE;
                break;
            }
            case NO_SUBSCRIBER_ERROR: {
                break;
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        switch (this.state) {
            case NO_SUBSCRIBER: {
                this.noSubscriberError = cause;
                this.state = State.NO_SUBSCRIBER_ERROR;
                this.cleanup();
                break;
            }
            case DRAINING: 
            case BUFFERING: 
            case DEMANDING: 
            case IDLE: {
                this.state = State.DONE;
                this.cleanup();
                this.subscriber.onError(cause);
                break;
            }
        }
    }

    private void cleanup() {
        while (!this.buffer.isEmpty()) {
            ReferenceCountUtil.release((Object)this.buffer.remove());
        }
    }

    private class ChannelSubscription
    implements Subscription {
        private ChannelSubscription() {
        }

        public void request(final long demand) {
            HandlerPublisher.this.executor.execute(new Runnable(){

                @Override
                public void run() {
                    HandlerPublisher.this.receivedDemand(demand);
                }
            });
        }

        public void cancel() {
            HandlerPublisher.this.executor.execute(new Runnable(){

                @Override
                public void run() {
                    HandlerPublisher.this.receivedCancel();
                }
            });
        }
    }

    static enum State {
        NO_SUBSCRIBER_OR_CONTEXT,
        NO_CONTEXT,
        NO_SUBSCRIBER,
        NO_SUBSCRIBER_ERROR,
        IDLE,
        BUFFERING,
        DEMANDING,
        DRAINING,
        DONE;

    }
}

