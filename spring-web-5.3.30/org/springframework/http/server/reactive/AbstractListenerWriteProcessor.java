/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Processor
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.springframework.core.log.LogDelegateFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.http.server.reactive.WriteResultPublisher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractListenerWriteProcessor<T>
implements Processor<T, Void> {
    protected static final Log rsWriteLogger = LogDelegateFactory.getHiddenLog(AbstractListenerWriteProcessor.class);
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    @Nullable
    private Subscription subscription;
    @Nullable
    private volatile T currentData;
    private volatile boolean sourceCompleted;
    private volatile boolean readyToCompleteAfterLastWrite;
    private final WriteResultPublisher resultPublisher;
    private final String logPrefix;

    public AbstractListenerWriteProcessor() {
        this("");
    }

    public AbstractListenerWriteProcessor(String logPrefix) {
        this.resultPublisher = new WriteResultPublisher(logPrefix + "[WP] ", this::cancelAndSetCompleted);
        this.logPrefix = StringUtils.hasText((String)logPrefix) ? logPrefix : "";
    }

    public String getLogPrefix() {
        return this.logPrefix;
    }

    public final void onSubscribe(Subscription subscription) {
        this.state.get().onSubscribe(this, subscription);
    }

    public final void onNext(T data) {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace((Object)(this.getLogPrefix() + "onNext: " + data.getClass().getSimpleName()));
        }
        this.state.get().onNext(this, data);
    }

    public final void onError(Throwable ex) {
        State state = this.state.get();
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace((Object)(this.getLogPrefix() + "onError: " + ex + " [" + (Object)((Object)state) + "]"));
        }
        state.onError(this, ex);
    }

    public final void onComplete() {
        State state = this.state.get();
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace((Object)(this.getLogPrefix() + "onComplete [" + (Object)((Object)state) + "]"));
        }
        state.onComplete(this);
    }

    public final void onWritePossible() {
        State state = this.state.get();
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace((Object)(this.getLogPrefix() + "onWritePossible [" + (Object)((Object)state) + "]"));
        }
        state.onWritePossible(this);
    }

    public void cancel() {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace((Object)(this.getLogPrefix() + "cancel [" + this.state + "]"));
        }
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    void cancelAndSetCompleted() {
        State prev;
        this.cancel();
        while ((prev = this.state.get()) != State.COMPLETED) {
            if (!this.state.compareAndSet(prev, State.COMPLETED)) continue;
            if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace((Object)(this.getLogPrefix() + (Object)((Object)prev) + " -> " + this.state));
            }
            if (prev == State.WRITING) break;
            this.discardCurrentData();
            break;
        }
    }

    public final void subscribe(Subscriber<? super Void> subscriber) {
        this.resultPublisher.subscribe(subscriber);
    }

    protected abstract boolean isDataEmpty(T var1);

    protected void dataReceived(T data) {
        T prev = this.currentData;
        if (prev != null) {
            this.discardData(data);
            this.cancel();
            this.onError(new IllegalStateException("Received new data while current not processed yet."));
        }
        this.currentData = data;
    }

    protected abstract boolean isWritePossible();

    protected abstract boolean write(T var1) throws IOException;

    @Deprecated
    protected void writingPaused() {
    }

    protected void writingComplete() {
    }

    protected void writingFailed(Throwable ex) {
    }

    protected abstract void discardData(T var1);

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace((Object)(this.getLogPrefix() + (Object)((Object)oldState) + " -> " + (Object)((Object)newState)));
        }
        return result;
    }

    private void changeStateToReceived(State oldState) {
        if (this.changeState(oldState, State.RECEIVED)) {
            this.writeIfPossible();
        }
    }

    private void changeStateToComplete(State oldState) {
        if (this.changeState(oldState, State.COMPLETED)) {
            this.discardCurrentData();
            this.writingComplete();
            this.resultPublisher.publishComplete();
        } else {
            this.state.get().onComplete(this);
        }
    }

    private void writeIfPossible() {
        boolean result = this.isWritePossible();
        if (!result && rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace((Object)(this.getLogPrefix() + "isWritePossible false"));
        }
        if (result) {
            this.onWritePossible();
        }
    }

    private void discardCurrentData() {
        T data = this.currentData;
        this.currentData = null;
        if (data != null) {
            this.discardData(data);
        }
    }

    private static enum State {
        UNSUBSCRIBED{

            @Override
            public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
                Assert.notNull((Object)subscription, (String)"Subscription must not be null");
                if (((AbstractListenerWriteProcessor)processor).changeState(this, 1.REQUESTED)) {
                    ((AbstractListenerWriteProcessor)processor).subscription = subscription;
                    subscription.request(1L);
                } else {
                    super.onSubscribe(processor, subscription);
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor)processor).changeStateToComplete(this);
            }
        }
        ,
        REQUESTED{

            @Override
            public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
                if (processor.isDataEmpty(data)) {
                    Assert.state((((AbstractListenerWriteProcessor)processor).subscription != null ? 1 : 0) != 0, (String)"No subscription");
                    ((AbstractListenerWriteProcessor)processor).subscription.request(1L);
                } else {
                    processor.dataReceived(data);
                    ((AbstractListenerWriteProcessor)processor).changeStateToReceived(this);
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor)processor).readyToCompleteAfterLastWrite = true;
                ((AbstractListenerWriteProcessor)processor).changeStateToReceived(this);
            }
        }
        ,
        RECEIVED{

            @Override
            public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {
                if (((AbstractListenerWriteProcessor)processor).readyToCompleteAfterLastWrite) {
                    ((AbstractListenerWriteProcessor)processor).changeStateToComplete(3.RECEIVED);
                } else if (((AbstractListenerWriteProcessor)processor).changeState(this, 3.WRITING)) {
                    Object data = ((AbstractListenerWriteProcessor)processor).currentData;
                    Assert.state((data != null ? 1 : 0) != 0, (String)"No data");
                    try {
                        if (processor.write(data)) {
                            if (((AbstractListenerWriteProcessor)processor).changeState(3.WRITING, 3.REQUESTED)) {
                                ((AbstractListenerWriteProcessor)processor).currentData = null;
                                if (((AbstractListenerWriteProcessor)processor).sourceCompleted) {
                                    ((AbstractListenerWriteProcessor)processor).readyToCompleteAfterLastWrite = true;
                                    ((AbstractListenerWriteProcessor)processor).changeStateToReceived(3.REQUESTED);
                                } else {
                                    processor.writingPaused();
                                    Assert.state((((AbstractListenerWriteProcessor)processor).subscription != null ? 1 : 0) != 0, (String)"No subscription");
                                    ((AbstractListenerWriteProcessor)processor).subscription.request(1L);
                                }
                            }
                        } else {
                            ((AbstractListenerWriteProcessor)processor).changeStateToReceived(3.WRITING);
                        }
                    }
                    catch (IOException ex) {
                        processor.writingFailed(ex);
                    }
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor)processor).sourceCompleted = true;
                if (((AbstractListenerWriteProcessor)processor).state.get() == REQUESTED) {
                    ((AbstractListenerWriteProcessor)processor).changeStateToComplete(REQUESTED);
                }
            }
        }
        ,
        WRITING{

            @Override
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor)processor).sourceCompleted = true;
                if (((AbstractListenerWriteProcessor)processor).state.get() == REQUESTED) {
                    ((AbstractListenerWriteProcessor)processor).changeStateToComplete(REQUESTED);
                }
            }
        }
        ,
        COMPLETED{

            @Override
            public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
            }

            @Override
            public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
            }
        };


        public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
            subscription.cancel();
        }

        public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
            processor.discardData(data);
            processor.cancel();
            processor.onError(new IllegalStateException("Illegal onNext without demand"));
        }

        public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {
            if (((AbstractListenerWriteProcessor)processor).changeState(this, State.COMPLETED)) {
                ((AbstractListenerWriteProcessor)processor).discardCurrentData();
                processor.writingComplete();
                ((AbstractListenerWriteProcessor)processor).resultPublisher.publishError(ex);
            } else {
                ((State)((Object)((AbstractListenerWriteProcessor)processor).state.get())).onError(processor, ex);
            }
        }

        public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
            throw new IllegalStateException(this.toString());
        }

        public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {
        }
    }
}

