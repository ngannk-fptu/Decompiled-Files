/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  reactor.core.publisher.Operators
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Operators;

public abstract class AbstractListenerReadPublisher<T>
implements Publisher<T> {
    protected static Log rsReadLogger = LogDelegateFactory.getHiddenLog(AbstractListenerReadPublisher.class);
    static final DataBuffer EMPTY_BUFFER = DefaultDataBufferFactory.sharedInstance.allocateBuffer(0);
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    private volatile long demand;
    private static final AtomicLongFieldUpdater<AbstractListenerReadPublisher> DEMAND_FIELD_UPDATER = AtomicLongFieldUpdater.newUpdater(AbstractListenerReadPublisher.class, "demand");
    @Nullable
    private volatile Subscriber<? super T> subscriber;
    private volatile boolean completionPending;
    @Nullable
    private volatile Throwable errorPending;
    private final String logPrefix;

    public AbstractListenerReadPublisher() {
        this("");
    }

    public AbstractListenerReadPublisher(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public String getLogPrefix() {
        return this.logPrefix;
    }

    public void subscribe(Subscriber<? super T> subscriber) {
        this.state.get().subscribe(this, subscriber);
    }

    public final void onDataAvailable() {
        rsReadLogger.trace((Object)(this.getLogPrefix() + "onDataAvailable"));
        this.state.get().onDataAvailable(this);
    }

    public void onAllDataRead() {
        State state = this.state.get();
        if (rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace((Object)(this.getLogPrefix() + "onAllDataRead [" + (Object)((Object)state) + "]"));
        }
        state.onAllDataRead(this);
    }

    public final void onError(Throwable ex) {
        State state = this.state.get();
        if (rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace((Object)(this.getLogPrefix() + "onError: " + ex + " [" + (Object)((Object)state) + "]"));
        }
        state.onError(this, ex);
    }

    protected abstract void checkOnDataAvailable();

    @Nullable
    protected abstract T read() throws IOException;

    protected abstract void readingPaused();

    protected abstract void discardData();

    private boolean readAndPublish() throws IOException {
        long r;
        while ((r = this.demand) > 0L && this.state.get() != State.COMPLETED) {
            T data = this.read();
            if (data == EMPTY_BUFFER) {
                if (!rsReadLogger.isTraceEnabled()) continue;
                rsReadLogger.trace((Object)(this.getLogPrefix() + "0 bytes read, trying again"));
                continue;
            }
            if (data != null) {
                Subscriber<? super T> subscriber;
                if (r != Long.MAX_VALUE) {
                    DEMAND_FIELD_UPDATER.addAndGet(this, -1L);
                }
                Assert.state((subscriber = this.subscriber) != null, "No subscriber");
                if (rsReadLogger.isTraceEnabled()) {
                    rsReadLogger.trace((Object)(this.getLogPrefix() + "Publishing " + data.getClass().getSimpleName()));
                }
                subscriber.onNext(data);
                continue;
            }
            if (rsReadLogger.isTraceEnabled()) {
                rsReadLogger.trace((Object)(this.getLogPrefix() + "No more to read"));
            }
            return true;
        }
        return false;
    }

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace((Object)(this.getLogPrefix() + (Object)((Object)oldState) + " -> " + (Object)((Object)newState)));
        }
        return result;
    }

    private void changeToDemandState(State oldState) {
        if (this.changeState(oldState, State.DEMAND) && oldState != State.READING) {
            this.checkOnDataAvailable();
        }
    }

    private boolean handlePendingCompletionOrError() {
        State state = this.state.get();
        if (state == State.DEMAND || state == State.NO_DEMAND) {
            if (this.completionPending) {
                rsReadLogger.trace((Object)(this.getLogPrefix() + "Processing pending completion"));
                this.state.get().onAllDataRead(this);
                return true;
            }
            Throwable ex = this.errorPending;
            if (ex != null) {
                if (rsReadLogger.isTraceEnabled()) {
                    rsReadLogger.trace((Object)(this.getLogPrefix() + "Processing pending completion with error: " + ex));
                }
                this.state.get().onError(this, ex);
                return true;
            }
        }
        return false;
    }

    private Subscription createSubscription() {
        return new ReadSubscription();
    }

    private static enum State {
        UNSUBSCRIBED{

            @Override
            <T> void subscribe(AbstractListenerReadPublisher<T> publisher, Subscriber<? super T> subscriber) {
                Assert.notNull(publisher, "Publisher must not be null");
                Assert.notNull(subscriber, "Subscriber must not be null");
                if (!((AbstractListenerReadPublisher)publisher).changeState(this, 1.SUBSCRIBING)) {
                    throw new IllegalStateException("Failed to transition to SUBSCRIBING, subscriber: " + subscriber);
                }
                Subscription subscription = ((AbstractListenerReadPublisher)publisher).createSubscription();
                ((AbstractListenerReadPublisher)publisher).subscriber = subscriber;
                subscriber.onSubscribe(subscription);
                ((AbstractListenerReadPublisher)publisher).changeState(1.SUBSCRIBING, 1.NO_DEMAND);
                ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
            }

            @Override
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
                ((AbstractListenerReadPublisher)publisher).completionPending = true;
                ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
            }

            @Override
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher)publisher).errorPending = ex;
                ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
            }
        }
        ,
        SUBSCRIBING{

            @Override
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate((long)n)) {
                    Operators.addCap((AtomicLongFieldUpdater)DEMAND_FIELD_UPDATER, publisher, (long)n);
                    ((AbstractListenerReadPublisher)publisher).changeToDemandState(this);
                }
            }

            @Override
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
                ((AbstractListenerReadPublisher)publisher).completionPending = true;
                ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
            }

            @Override
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher)publisher).errorPending = ex;
                ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
            }
        }
        ,
        NO_DEMAND{

            @Override
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate((long)n)) {
                    Operators.addCap((AtomicLongFieldUpdater)DEMAND_FIELD_UPDATER, publisher, (long)n);
                    ((AbstractListenerReadPublisher)publisher).changeToDemandState(this);
                }
            }
        }
        ,
        DEMAND{

            @Override
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate((long)n)) {
                    Operators.addCap((AtomicLongFieldUpdater)DEMAND_FIELD_UPDATER, publisher, (long)n);
                    ((AbstractListenerReadPublisher)publisher).changeToDemandState(4.NO_DEMAND);
                }
            }

            @Override
            <T> void onDataAvailable(AbstractListenerReadPublisher<T> publisher) {
                if (((AbstractListenerReadPublisher)publisher).changeState(this, 4.READING)) {
                    try {
                        boolean demandAvailable = ((AbstractListenerReadPublisher)publisher).readAndPublish();
                        if (demandAvailable) {
                            ((AbstractListenerReadPublisher)publisher).changeToDemandState(4.READING);
                            ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
                        } else {
                            long r;
                            publisher.readingPaused();
                            if (((AbstractListenerReadPublisher)publisher).changeState(4.READING, 4.NO_DEMAND) && !((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError() && (r = ((AbstractListenerReadPublisher)publisher).demand) > 0L) {
                                ((AbstractListenerReadPublisher)publisher).changeToDemandState(4.NO_DEMAND);
                            }
                        }
                    }
                    catch (IOException ex) {
                        publisher.onError(ex);
                    }
                }
            }
        }
        ,
        READING{

            @Override
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate((long)n)) {
                    Operators.addCap((AtomicLongFieldUpdater)DEMAND_FIELD_UPDATER, publisher, (long)n);
                    ((AbstractListenerReadPublisher)publisher).changeToDemandState(5.NO_DEMAND);
                }
            }

            @Override
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
                ((AbstractListenerReadPublisher)publisher).completionPending = true;
                ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
            }

            @Override
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher)publisher).errorPending = ex;
                ((AbstractListenerReadPublisher)publisher).handlePendingCompletionOrError();
            }
        }
        ,
        COMPLETED{

            @Override
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
            }

            @Override
            <T> void cancel(AbstractListenerReadPublisher<T> publisher) {
            }

            @Override
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
            }

            @Override
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable t) {
            }
        };


        <T> void subscribe(AbstractListenerReadPublisher<T> publisher, Subscriber<? super T> subscriber) {
            throw new IllegalStateException(this.toString());
        }

        <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
            throw new IllegalStateException(this.toString());
        }

        <T> void cancel(AbstractListenerReadPublisher<T> publisher) {
            if (((AbstractListenerReadPublisher)publisher).changeState(this, State.COMPLETED)) {
                publisher.discardData();
            } else {
                ((State)((Object)((AbstractListenerReadPublisher)publisher).state.get())).cancel(publisher);
            }
        }

        <T> void onDataAvailable(AbstractListenerReadPublisher<T> publisher) {
        }

        <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
            if (((AbstractListenerReadPublisher)publisher).changeState(this, State.COMPLETED)) {
                Subscriber s = ((AbstractListenerReadPublisher)publisher).subscriber;
                if (s != null) {
                    s.onComplete();
                }
            } else {
                ((State)((Object)((AbstractListenerReadPublisher)publisher).state.get())).onAllDataRead(publisher);
            }
        }

        <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable t) {
            if (((AbstractListenerReadPublisher)publisher).changeState(this, State.COMPLETED)) {
                publisher.discardData();
                Subscriber s = ((AbstractListenerReadPublisher)publisher).subscriber;
                if (s != null) {
                    s.onError(t);
                }
            } else {
                ((State)((Object)((AbstractListenerReadPublisher)publisher).state.get())).onError(publisher, t);
            }
        }
    }

    private final class ReadSubscription
    implements Subscription {
        private ReadSubscription() {
        }

        public final void request(long n) {
            if (rsReadLogger.isTraceEnabled()) {
                rsReadLogger.trace((Object)(AbstractListenerReadPublisher.this.getLogPrefix() + "request " + (n != Long.MAX_VALUE ? Long.valueOf(n) : "Long.MAX_VALUE")));
            }
            ((State)((Object)AbstractListenerReadPublisher.this.state.get())).request(AbstractListenerReadPublisher.this, n);
        }

        public final void cancel() {
            State state = (State)((Object)AbstractListenerReadPublisher.this.state.get());
            if (rsReadLogger.isTraceEnabled()) {
                rsReadLogger.trace((Object)(AbstractListenerReadPublisher.this.getLogPrefix() + "cancel [" + (Object)((Object)state) + "]"));
            }
            state.cancel(AbstractListenerReadPublisher.this);
        }
    }
}

