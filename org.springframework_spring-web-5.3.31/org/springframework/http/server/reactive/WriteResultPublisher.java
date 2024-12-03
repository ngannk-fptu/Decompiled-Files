/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.springframework.core.log.LogDelegateFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Operators
 */
package org.springframework.http.server.reactive;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Operators;

class WriteResultPublisher
implements Publisher<Void> {
    private static final Log rsWriteResultLogger = LogDelegateFactory.getHiddenLog(WriteResultPublisher.class);
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    private final Runnable cancelTask;
    @Nullable
    private volatile Subscriber<? super Void> subscriber;
    private volatile boolean completedBeforeSubscribed;
    @Nullable
    private volatile Throwable errorBeforeSubscribed;
    private final String logPrefix;

    public WriteResultPublisher(String logPrefix, Runnable cancelTask) {
        this.cancelTask = cancelTask;
        this.logPrefix = logPrefix;
    }

    public final void subscribe(Subscriber<? super Void> subscriber) {
        if (rsWriteResultLogger.isTraceEnabled()) {
            rsWriteResultLogger.trace((Object)(this.logPrefix + "got subscriber " + subscriber));
        }
        this.state.get().subscribe(this, subscriber);
    }

    public void publishComplete() {
        State state = this.state.get();
        if (rsWriteResultLogger.isTraceEnabled()) {
            rsWriteResultLogger.trace((Object)(this.logPrefix + "completed [" + (Object)((Object)state) + "]"));
        }
        state.publishComplete(this);
    }

    public void publishError(Throwable t) {
        State state = this.state.get();
        if (rsWriteResultLogger.isTraceEnabled()) {
            rsWriteResultLogger.trace((Object)(this.logPrefix + "failed: " + t + " [" + (Object)((Object)state) + "]"));
        }
        state.publishError(this, t);
    }

    private boolean changeState(State oldState, State newState) {
        return this.state.compareAndSet(oldState, newState);
    }

    private static enum State {
        UNSUBSCRIBED{

            @Override
            void subscribe(WriteResultPublisher publisher, Subscriber<? super Void> subscriber) {
                Assert.notNull(subscriber, (String)"Subscriber must not be null");
                if (publisher.changeState(this, 1.SUBSCRIBING)) {
                    Throwable ex;
                    WriteResultSubscription subscription = new WriteResultSubscription(publisher);
                    publisher.subscriber = subscriber;
                    subscriber.onSubscribe((Subscription)subscription);
                    publisher.changeState(1.SUBSCRIBING, 1.SUBSCRIBED);
                    if (publisher.completedBeforeSubscribed) {
                        ((State)((Object)publisher.state.get())).publishComplete(publisher);
                    }
                    if ((ex = publisher.errorBeforeSubscribed) != null) {
                        ((State)((Object)publisher.state.get())).publishError(publisher, ex);
                    }
                } else {
                    throw new IllegalStateException(this.toString());
                }
            }

            @Override
            void publishComplete(WriteResultPublisher publisher) {
                publisher.completedBeforeSubscribed = true;
                if (SUBSCRIBED == publisher.state.get()) {
                    ((State)((Object)publisher.state.get())).publishComplete(publisher);
                }
            }

            @Override
            void publishError(WriteResultPublisher publisher, Throwable ex) {
                publisher.errorBeforeSubscribed = ex;
                if (SUBSCRIBED == publisher.state.get()) {
                    ((State)((Object)publisher.state.get())).publishError(publisher, ex);
                }
            }
        }
        ,
        SUBSCRIBING{

            @Override
            void request(WriteResultPublisher publisher, long n) {
                Operators.validate((long)n);
            }

            @Override
            void publishComplete(WriteResultPublisher publisher) {
                publisher.completedBeforeSubscribed = true;
                if (SUBSCRIBED == publisher.state.get()) {
                    ((State)((Object)publisher.state.get())).publishComplete(publisher);
                }
            }

            @Override
            void publishError(WriteResultPublisher publisher, Throwable ex) {
                publisher.errorBeforeSubscribed = ex;
                if (SUBSCRIBED == publisher.state.get()) {
                    ((State)((Object)publisher.state.get())).publishError(publisher, ex);
                }
            }
        }
        ,
        SUBSCRIBED{

            @Override
            void request(WriteResultPublisher publisher, long n) {
                Operators.validate((long)n);
            }
        }
        ,
        COMPLETED{

            @Override
            void request(WriteResultPublisher publisher, long n) {
            }

            @Override
            void cancel(WriteResultPublisher publisher) {
            }

            @Override
            void publishComplete(WriteResultPublisher publisher) {
            }

            @Override
            void publishError(WriteResultPublisher publisher, Throwable t) {
            }
        };


        void subscribe(WriteResultPublisher publisher, Subscriber<? super Void> subscriber) {
            throw new IllegalStateException(this.toString());
        }

        void request(WriteResultPublisher publisher, long n) {
            throw new IllegalStateException(this.toString());
        }

        void cancel(WriteResultPublisher publisher) {
            if (publisher.changeState(this, State.COMPLETED)) {
                publisher.cancelTask.run();
            } else {
                ((State)((Object)publisher.state.get())).cancel(publisher);
            }
        }

        void publishComplete(WriteResultPublisher publisher) {
            if (publisher.changeState(this, State.COMPLETED)) {
                Subscriber s = publisher.subscriber;
                Assert.state((s != null ? 1 : 0) != 0, (String)"No subscriber");
                s.onComplete();
            } else {
                ((State)((Object)publisher.state.get())).publishComplete(publisher);
            }
        }

        void publishError(WriteResultPublisher publisher, Throwable t) {
            if (publisher.changeState(this, State.COMPLETED)) {
                Subscriber s = publisher.subscriber;
                Assert.state((s != null ? 1 : 0) != 0, (String)"No subscriber");
                s.onError(t);
            } else {
                ((State)((Object)publisher.state.get())).publishError(publisher, t);
            }
        }
    }

    private static final class WriteResultSubscription
    implements Subscription {
        private final WriteResultPublisher publisher;

        public WriteResultSubscription(WriteResultPublisher publisher) {
            this.publisher = publisher;
        }

        public final void request(long n) {
            if (rsWriteResultLogger.isTraceEnabled()) {
                rsWriteResultLogger.trace((Object)(this.publisher.logPrefix + "request " + (n != Long.MAX_VALUE ? Long.valueOf(n) : "Long.MAX_VALUE")));
            }
            this.getState().request(this.publisher, n);
        }

        public final void cancel() {
            State state = this.getState();
            if (rsWriteResultLogger.isTraceEnabled()) {
                rsWriteResultLogger.trace((Object)(this.publisher.logPrefix + "cancel [" + (Object)((Object)state) + "]"));
            }
            state.cancel(this.publisher);
        }

        private State getState() {
            return (State)((Object)this.publisher.state.get());
        }
    }
}

