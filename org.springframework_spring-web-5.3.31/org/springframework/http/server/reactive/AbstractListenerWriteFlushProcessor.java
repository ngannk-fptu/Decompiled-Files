/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Processor
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.springframework.core.log.LogDelegateFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.http.server.reactive.AbstractListenerWriteProcessor;
import org.springframework.http.server.reactive.WriteResultPublisher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractListenerWriteFlushProcessor<T>
implements Processor<Publisher<? extends T>, Void> {
    protected static final Log rsWriteFlushLogger = LogDelegateFactory.getHiddenLog(AbstractListenerWriteFlushProcessor.class);
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    @Nullable
    private Subscription subscription;
    private volatile boolean sourceCompleted;
    @Nullable
    private volatile AbstractListenerWriteProcessor<?> currentWriteProcessor;
    private final WriteResultPublisher resultPublisher;
    private final String logPrefix;

    public AbstractListenerWriteFlushProcessor() {
        this("");
    }

    public AbstractListenerWriteFlushProcessor(String logPrefix) {
        this.logPrefix = logPrefix;
        this.resultPublisher = new WriteResultPublisher(logPrefix + "[WFP] ", () -> {
            AbstractListenerWriteProcessor<?> writeProcessor;
            this.cancel();
            State oldState = this.state.getAndSet(State.COMPLETED);
            if (rsWriteFlushLogger.isTraceEnabled()) {
                rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + (Object)((Object)oldState) + " -> " + this.state));
            }
            if ((writeProcessor = this.currentWriteProcessor) != null) {
                writeProcessor.cancelAndSetCompleted();
            }
            this.currentWriteProcessor = null;
        });
    }

    public String getLogPrefix() {
        return this.logPrefix;
    }

    public final void onSubscribe(Subscription subscription) {
        this.state.get().onSubscribe(this, subscription);
    }

    public final void onNext(Publisher<? extends T> publisher) {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + "onNext: \"write\" Publisher"));
        }
        this.state.get().onNext(this, publisher);
    }

    public final void onError(Throwable ex) {
        State state = this.state.get();
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + "onError: " + ex + " [" + (Object)((Object)state) + "]"));
        }
        state.onError(this, ex);
    }

    public final void onComplete() {
        State state = this.state.get();
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + "onComplete [" + (Object)((Object)state) + "]"));
        }
        state.onComplete(this);
    }

    protected final void onFlushPossible() {
        this.state.get().onFlushPossible(this);
    }

    protected void cancel() {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + "cancel [" + this.state + "]"));
        }
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    public final void subscribe(Subscriber<? super Void> subscriber) {
        this.resultPublisher.subscribe(subscriber);
    }

    protected abstract Processor<? super T, Void> createWriteProcessor();

    protected abstract boolean isWritePossible();

    protected abstract void flush() throws IOException;

    protected abstract boolean isFlushPending();

    protected void flushingFailed(Throwable t) {
        this.cancel();
        this.onError(t);
    }

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + (Object)((Object)oldState) + " -> " + (Object)((Object)newState)));
        }
        return result;
    }

    private void flushIfPossible() {
        boolean result = this.isWritePossible();
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + "isWritePossible[" + result + "]"));
        }
        if (result) {
            this.onFlushPossible();
        }
    }

    private static enum State {
        UNSUBSCRIBED{

            @Override
            public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> processor, Subscription subscription) {
                Assert.notNull((Object)subscription, (String)"Subscription must not be null");
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 1.REQUESTED)) {
                    ((AbstractListenerWriteFlushProcessor)processor).subscription = subscription;
                    subscription.request(1L);
                } else {
                    super.onSubscribe(processor, subscription);
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 1.COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor)processor).resultPublisher.publishComplete();
                } else {
                    ((State)((Object)((AbstractListenerWriteFlushProcessor)processor).state.get())).onComplete(processor);
                }
            }
        }
        ,
        REQUESTED{

            @Override
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> processor, Publisher<? extends T> currentPublisher) {
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 2.RECEIVED)) {
                    Processor<T, Void> writeProcessor = processor.createWriteProcessor();
                    ((AbstractListenerWriteFlushProcessor)processor).currentWriteProcessor = (AbstractListenerWriteProcessor)writeProcessor;
                    currentPublisher.subscribe(writeProcessor);
                    writeProcessor.subscribe((Subscriber)new WriteResultSubscriber(processor));
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 2.COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor)processor).resultPublisher.publishComplete();
                } else {
                    ((State)((Object)((AbstractListenerWriteFlushProcessor)processor).state.get())).onComplete(processor);
                }
            }
        }
        ,
        RECEIVED{

            @Override
            public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                try {
                    processor.flush();
                }
                catch (Throwable ex) {
                    processor.flushingFailed(ex);
                    return;
                }
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 3.REQUESTED)) {
                    if (((AbstractListenerWriteFlushProcessor)processor).sourceCompleted) {
                        this.handleSourceCompleted(processor);
                    } else {
                        Assert.state((((AbstractListenerWriteFlushProcessor)processor).subscription != null ? 1 : 0) != 0, (String)"No subscription");
                        ((AbstractListenerWriteFlushProcessor)processor).subscription.request(1L);
                    }
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                ((AbstractListenerWriteFlushProcessor)processor).sourceCompleted = true;
                if (((AbstractListenerWriteFlushProcessor)processor).state.get() == REQUESTED) {
                    this.handleSourceCompleted(processor);
                }
            }

            private <T> void handleSourceCompleted(AbstractListenerWriteFlushProcessor<T> processor) {
                if (processor.isFlushPending()) {
                    ((AbstractListenerWriteFlushProcessor)processor).changeState(REQUESTED, FLUSHING);
                    ((AbstractListenerWriteFlushProcessor)processor).flushIfPossible();
                } else if (((AbstractListenerWriteFlushProcessor)processor).changeState(REQUESTED, COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor)processor).resultPublisher.publishComplete();
                } else {
                    ((State)((Object)((AbstractListenerWriteFlushProcessor)processor).state.get())).onComplete(processor);
                }
            }
        }
        ,
        FLUSHING{

            @Override
            public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {
                try {
                    processor.flush();
                }
                catch (Throwable ex) {
                    processor.flushingFailed(ex);
                    return;
                }
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 4.COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor)processor).resultPublisher.publishComplete();
                } else {
                    ((State)((Object)((AbstractListenerWriteFlushProcessor)processor).state.get())).onComplete(processor);
                }
            }

            @Override
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            }
        }
        ,
        COMPLETED{

            @Override
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            }

            @Override
            public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable t) {
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            }
        };


        public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> proc, Subscription subscription) {
            subscription.cancel();
        }

        public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            throw new IllegalStateException(this.toString());
        }

        public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable ex) {
            if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, State.COMPLETED)) {
                ((AbstractListenerWriteFlushProcessor)processor).resultPublisher.publishError(ex);
            } else {
                ((State)((Object)((AbstractListenerWriteFlushProcessor)processor).state.get())).onError(processor, ex);
            }
        }

        public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            throw new IllegalStateException(this.toString());
        }

        public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            throw new IllegalStateException(this.toString());
        }

        public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {
        }

        private static class WriteResultSubscriber
        implements Subscriber<Void> {
            private final AbstractListenerWriteFlushProcessor<?> processor;

            public WriteResultSubscriber(AbstractListenerWriteFlushProcessor<?> processor) {
                this.processor = processor;
            }

            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            public void onNext(Void aVoid) {
            }

            public void onError(Throwable ex) {
                if (rsWriteFlushLogger.isTraceEnabled()) {
                    rsWriteFlushLogger.trace((Object)(this.processor.getLogPrefix() + "current \"write\" Publisher failed: " + ex));
                }
                ((AbstractListenerWriteFlushProcessor)this.processor).currentWriteProcessor = null;
                this.processor.cancel();
                this.processor.onError(ex);
            }

            public void onComplete() {
                if (rsWriteFlushLogger.isTraceEnabled()) {
                    rsWriteFlushLogger.trace((Object)(this.processor.getLogPrefix() + "current \"write\" Publisher completed"));
                }
                ((AbstractListenerWriteFlushProcessor)this.processor).currentWriteProcessor = null;
                ((State)((Object)((AbstractListenerWriteFlushProcessor)this.processor).state.get())).writeComplete(this.processor);
            }

            public String toString() {
                return this.processor.getClass().getSimpleName() + "-WriteResultSubscriber";
            }
        }
    }
}

