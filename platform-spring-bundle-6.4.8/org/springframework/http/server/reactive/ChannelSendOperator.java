/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  reactor.core.CoreSubscriber
 *  reactor.core.Scannable
 *  reactor.core.Scannable$Attr
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.core.publisher.Operators
 *  reactor.util.context.Context
 */
package org.springframework.http.server.reactive;

import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

public class ChannelSendOperator<T>
extends Mono<Void>
implements Scannable {
    private final Function<Publisher<T>, Publisher<Void>> writeFunction;
    private final Flux<T> source;

    public ChannelSendOperator(Publisher<? extends T> source, Function<Publisher<T>, Publisher<Void>> writeFunction) {
        this.source = Flux.from(source);
        this.writeFunction = writeFunction;
    }

    @Nullable
    public Object scanUnsafe(Scannable.Attr key) {
        if (key == Scannable.Attr.PREFETCH) {
            return Integer.MAX_VALUE;
        }
        if (key == Scannable.Attr.PARENT) {
            return this.source;
        }
        return null;
    }

    public void subscribe(CoreSubscriber<? super Void> actual) {
        this.source.subscribe((CoreSubscriber)new WriteBarrier(actual));
    }

    private class WriteCompletionBarrier
    implements CoreSubscriber<Void>,
    Subscription {
        private final CoreSubscriber<? super Void> completionSubscriber;
        private final WriteBarrier writeBarrier;
        @Nullable
        private Subscription subscription;

        public WriteCompletionBarrier(CoreSubscriber<? super Void> subscriber, WriteBarrier writeBarrier) {
            this.completionSubscriber = subscriber;
            this.writeBarrier = writeBarrier;
        }

        public void connect() {
            this.completionSubscriber.onSubscribe((Subscription)this);
        }

        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Void aVoid) {
        }

        public void onError(Throwable ex) {
            try {
                this.completionSubscriber.onError(ex);
            }
            finally {
                this.writeBarrier.releaseCachedItem();
            }
        }

        public void onComplete() {
            this.completionSubscriber.onComplete();
        }

        public Context currentContext() {
            return this.completionSubscriber.currentContext();
        }

        public void request(long n) {
        }

        public void cancel() {
            this.writeBarrier.cancel();
            Subscription subscription = this.subscription;
            if (subscription != null) {
                subscription.cancel();
            }
        }
    }

    private class WriteBarrier
    implements CoreSubscriber<T>,
    Subscription,
    Publisher<T> {
        private final WriteCompletionBarrier writeCompletionBarrier;
        @Nullable
        private Subscription subscription;
        @Nullable
        private T item;
        @Nullable
        private Throwable error;
        private boolean completed = false;
        private long demandBeforeReadyToWrite;
        private State state = State.NEW;
        @Nullable
        private Subscriber<? super T> writeSubscriber;

        WriteBarrier(CoreSubscriber<? super Void> completionSubscriber) {
            this.writeCompletionBarrier = new WriteCompletionBarrier(completionSubscriber, this);
        }

        public final void onSubscribe(Subscription s) {
            if (Operators.validate((Subscription)this.subscription, (Subscription)s)) {
                this.subscription = s;
                this.writeCompletionBarrier.connect();
                s.request(1L);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void onNext(T item) {
            if (this.state == State.READY_TO_WRITE) {
                this.requiredWriteSubscriber().onNext(item);
                return;
            }
            WriteBarrier writeBarrier = this;
            synchronized (writeBarrier) {
                if (this.state == State.READY_TO_WRITE) {
                    this.requiredWriteSubscriber().onNext(item);
                } else if (this.state == State.NEW) {
                    Publisher result;
                    this.item = item;
                    this.state = State.FIRST_SIGNAL_RECEIVED;
                    try {
                        result = (Publisher)ChannelSendOperator.this.writeFunction.apply(this);
                    }
                    catch (Throwable ex) {
                        this.writeCompletionBarrier.onError(ex);
                        return;
                    }
                    result.subscribe((Subscriber)this.writeCompletionBarrier);
                } else {
                    if (this.subscription != null) {
                        this.subscription.cancel();
                    }
                    this.writeCompletionBarrier.onError(new IllegalStateException("Unexpected item."));
                }
            }
        }

        private Subscriber<? super T> requiredWriteSubscriber() {
            Assert.state(this.writeSubscriber != null, "No write subscriber");
            return this.writeSubscriber;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void onError(Throwable ex) {
            if (this.state == State.READY_TO_WRITE) {
                this.requiredWriteSubscriber().onError(ex);
                return;
            }
            WriteBarrier writeBarrier = this;
            synchronized (writeBarrier) {
                if (this.state == State.READY_TO_WRITE) {
                    this.requiredWriteSubscriber().onError(ex);
                } else if (this.state == State.NEW) {
                    this.state = State.FIRST_SIGNAL_RECEIVED;
                    this.writeCompletionBarrier.onError(ex);
                } else {
                    this.error = ex;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void onComplete() {
            if (this.state == State.READY_TO_WRITE) {
                this.requiredWriteSubscriber().onComplete();
                return;
            }
            WriteBarrier writeBarrier = this;
            synchronized (writeBarrier) {
                if (this.state == State.READY_TO_WRITE) {
                    this.requiredWriteSubscriber().onComplete();
                } else if (this.state == State.NEW) {
                    Publisher result;
                    this.completed = true;
                    this.state = State.FIRST_SIGNAL_RECEIVED;
                    try {
                        result = (Publisher)ChannelSendOperator.this.writeFunction.apply(this);
                    }
                    catch (Throwable ex) {
                        this.writeCompletionBarrier.onError(ex);
                        return;
                    }
                    result.subscribe((Subscriber)this.writeCompletionBarrier);
                } else {
                    this.completed = true;
                }
            }
        }

        public Context currentContext() {
            return this.writeCompletionBarrier.currentContext();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void request(long n) {
            Subscription s = this.subscription;
            if (s == null) {
                return;
            }
            if (this.state == State.READY_TO_WRITE) {
                s.request(n);
                return;
            }
            WriteBarrier writeBarrier = this;
            synchronized (writeBarrier) {
                block12: {
                    if (this.writeSubscriber != null) {
                        if (this.state == State.EMITTING_CACHED_SIGNALS) {
                            this.demandBeforeReadyToWrite = n;
                            return;
                        }
                        this.state = State.EMITTING_CACHED_SIGNALS;
                        if (this.emitCachedSignals()) {
                            return;
                        }
                        if ((n = n + this.demandBeforeReadyToWrite - 1L) != 0L) break block12;
                        return;
                        finally {
                            this.state = State.READY_TO_WRITE;
                        }
                    }
                }
            }
            s.request(n);
        }

        private boolean emitCachedSignals() {
            if (this.error != null) {
                try {
                    this.requiredWriteSubscriber().onError(this.error);
                }
                finally {
                    this.releaseCachedItem();
                }
                return true;
            }
            Object item = this.item;
            this.item = null;
            if (item != null) {
                this.requiredWriteSubscriber().onNext(item);
            }
            if (this.completed) {
                this.requiredWriteSubscriber().onComplete();
                return true;
            }
            return false;
        }

        public void cancel() {
            Subscription s = this.subscription;
            if (s != null) {
                this.subscription = null;
                try {
                    s.cancel();
                }
                finally {
                    this.releaseCachedItem();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void releaseCachedItem() {
            WriteBarrier writeBarrier = this;
            synchronized (writeBarrier) {
                Object item = this.item;
                if (item instanceof DataBuffer) {
                    DataBufferUtils.release((DataBuffer)item);
                }
                this.item = null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void subscribe(Subscriber<? super T> writeSubscriber) {
            WriteBarrier writeBarrier = this;
            synchronized (writeBarrier) {
                Assert.state(this.writeSubscriber == null, "Only one write subscriber supported");
                this.writeSubscriber = writeSubscriber;
                if (this.error != null || this.completed) {
                    this.writeSubscriber.onSubscribe(Operators.emptySubscription());
                    this.emitCachedSignals();
                } else {
                    this.writeSubscriber.onSubscribe((Subscription)this);
                }
            }
        }
    }

    private static enum State {
        NEW,
        FIRST_SIGNAL_RECEIVED,
        EMITTING_CACHED_SIGNALS,
        READY_TO_WRITE;

    }
}

