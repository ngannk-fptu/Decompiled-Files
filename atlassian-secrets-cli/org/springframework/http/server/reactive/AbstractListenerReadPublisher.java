/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Operators
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Operators;

public abstract class AbstractListenerReadPublisher<T>
implements Publisher<T> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    private volatile long demand;
    private static final AtomicLongFieldUpdater<AbstractListenerReadPublisher> DEMAND_FIELD_UPDATER = AtomicLongFieldUpdater.newUpdater(AbstractListenerReadPublisher.class, "demand");
    @Nullable
    private volatile Subscriber<? super T> subscriber;
    private volatile boolean completionBeforeDemand;
    @Nullable
    private volatile Throwable errorBeforeDemand;

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        this.state.get().subscribe(this, subscriber);
    }

    public final void onDataAvailable() {
        this.logger.trace("I/O event onDataAvailable");
        this.state.get().onDataAvailable(this);
    }

    public void onAllDataRead() {
        this.logger.trace("I/O event onAllDataRead");
        this.state.get().onAllDataRead(this);
    }

    public final void onError(Throwable ex) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("I/O event onError: " + ex);
        }
        this.state.get().onError(this, ex);
    }

    protected abstract void checkOnDataAvailable();

    @Nullable
    protected abstract T read() throws IOException;

    protected abstract void readingPaused();

    protected abstract void discardData();

    private boolean readAndPublish() throws IOException {
        long r;
        while ((r = this.demand) > 0L && !this.state.get().equals((Object)State.COMPLETED)) {
            T data = this.read();
            if (data != null) {
                Subscriber<T> subscriber;
                if (r != Long.MAX_VALUE) {
                    DEMAND_FIELD_UPDATER.addAndGet(this, -1L);
                }
                Assert.state((subscriber = this.subscriber) != null, "No subscriber");
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Data item read, publishing..");
                }
                subscriber.onNext(data);
                continue;
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No more data to read");
            }
            return true;
        }
        return false;
    }

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && this.logger.isTraceEnabled()) {
            this.logger.trace((Object)((Object)oldState) + " -> " + (Object)((Object)newState));
        }
        return result;
    }

    private void changeToDemandState(State oldState) {
        if (this.changeState(oldState, State.DEMAND) && !oldState.equals((Object)State.READING)) {
            this.checkOnDataAvailable();
        }
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
                if (((AbstractListenerReadPublisher)publisher).changeState(this, 1.SUBSCRIBING)) {
                    Throwable ex;
                    Subscription subscription = ((AbstractListenerReadPublisher)publisher).createSubscription();
                    ((AbstractListenerReadPublisher)publisher).subscriber = subscriber;
                    subscriber.onSubscribe(subscription);
                    ((AbstractListenerReadPublisher)publisher).changeState(1.SUBSCRIBING, 1.NO_DEMAND);
                    if (((AbstractListenerReadPublisher)publisher).completionBeforeDemand) {
                        publisher.logger.trace("Completed before demand");
                        ((State)((Object)((AbstractListenerReadPublisher)publisher).state.get())).onAllDataRead(publisher);
                    }
                    if ((ex = ((AbstractListenerReadPublisher)publisher).errorBeforeDemand) != null) {
                        if (publisher.logger.isTraceEnabled()) {
                            publisher.logger.trace("Completed with error before demand: " + ex);
                        }
                        ((State)((Object)((AbstractListenerReadPublisher)publisher).state.get())).onError(publisher, ex);
                    }
                } else {
                    throw new IllegalStateException("Failed to transition to SUBSCRIBING, subscriber: " + subscriber);
                }
            }

            @Override
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
                ((AbstractListenerReadPublisher)publisher).completionBeforeDemand = true;
            }

            @Override
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher)publisher).errorBeforeDemand = ex;
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
                ((AbstractListenerReadPublisher)publisher).completionBeforeDemand = true;
            }

            @Override
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher)publisher).errorBeforeDemand = ex;
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
                        } else {
                            long r;
                            publisher.readingPaused();
                            if (((AbstractListenerReadPublisher)publisher).changeState(4.READING, 4.NO_DEMAND) && (r = ((AbstractListenerReadPublisher)publisher).demand) > 0L) {
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

        @Override
        public final void request(long n) {
            if (AbstractListenerReadPublisher.this.logger.isTraceEnabled()) {
                AbstractListenerReadPublisher.this.logger.trace("Signal request(" + n + ")");
            }
            ((State)((Object)AbstractListenerReadPublisher.this.state.get())).request(AbstractListenerReadPublisher.this, n);
        }

        @Override
        public final void cancel() {
            if (AbstractListenerReadPublisher.this.logger.isTraceEnabled()) {
                AbstractListenerReadPublisher.this.logger.trace("Signal cancel()");
            }
            ((State)((Object)AbstractListenerReadPublisher.this.state.get())).cancel(AbstractListenerReadPublisher.this);
        }
    }
}

