/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Operators
 */
package org.springframework.http.server.reactive;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Operators;

class WriteResultPublisher
implements Publisher<Void> {
    private static final Log logger = LogFactory.getLog(WriteResultPublisher.class);
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    @Nullable
    private volatile Subscriber<? super Void> subscriber;
    private volatile boolean completedBeforeSubscribed;
    @Nullable
    private volatile Throwable errorBeforeSubscribed;

    WriteResultPublisher() {
    }

    @Override
    public final void subscribe(Subscriber<? super Void> subscriber) {
        if (logger.isTraceEnabled()) {
            logger.trace(this.state + " subscribe: " + subscriber);
        }
        this.state.get().subscribe(this, subscriber);
    }

    public void publishComplete() {
        if (logger.isTraceEnabled()) {
            logger.trace(this.state + " publishComplete");
        }
        this.state.get().publishComplete(this);
    }

    public void publishError(Throwable t) {
        if (logger.isTraceEnabled()) {
            logger.trace(this.state + " publishError: " + t);
        }
        this.state.get().publishError(this, t);
    }

    private boolean changeState(State oldState, State newState) {
        return this.state.compareAndSet(oldState, newState);
    }

    private static enum State {
        UNSUBSCRIBED{

            @Override
            void subscribe(WriteResultPublisher publisher, Subscriber<? super Void> subscriber) {
                Assert.notNull(subscriber, "Subscriber must not be null");
                if (publisher.changeState(this, 1.SUBSCRIBING)) {
                    Throwable publisherError;
                    WriteResultSubscription subscription = new WriteResultSubscription(publisher);
                    publisher.subscriber = subscriber;
                    subscriber.onSubscribe(subscription);
                    publisher.changeState(1.SUBSCRIBING, 1.SUBSCRIBED);
                    if (publisher.completedBeforeSubscribed) {
                        publisher.publishComplete();
                    }
                    if ((publisherError = publisher.errorBeforeSubscribed) != null) {
                        publisher.publishError(publisherError);
                    }
                } else {
                    throw new IllegalStateException(this.toString());
                }
            }

            @Override
            void publishComplete(WriteResultPublisher publisher) {
                publisher.completedBeforeSubscribed = true;
            }

            @Override
            void publishError(WriteResultPublisher publisher, Throwable ex) {
                publisher.errorBeforeSubscribed = ex;
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
            }

            @Override
            void publishError(WriteResultPublisher publisher, Throwable ex) {
                publisher.errorBeforeSubscribed = ex;
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
            if (!publisher.changeState(this, State.COMPLETED)) {
                ((State)((Object)publisher.state.get())).cancel(publisher);
            }
        }

        void publishComplete(WriteResultPublisher publisher) {
            if (publisher.changeState(this, State.COMPLETED)) {
                Subscriber s = publisher.subscriber;
                Assert.state(s != null, "No subscriber");
                s.onComplete();
            } else {
                ((State)((Object)publisher.state.get())).publishComplete(publisher);
            }
        }

        void publishError(WriteResultPublisher publisher, Throwable t) {
            if (publisher.changeState(this, State.COMPLETED)) {
                Subscriber s = publisher.subscriber;
                Assert.state(s != null, "No subscriber");
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

        @Override
        public final void request(long n) {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)((Object)this.state()) + " request: " + n);
            }
            this.state().request(this.publisher, n);
        }

        @Override
        public final void cancel() {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)((Object)this.state()) + " cancel");
            }
            this.state().cancel(this.publisher);
        }

        private State state() {
            return (State)((Object)this.publisher.state.get());
        }
    }
}

