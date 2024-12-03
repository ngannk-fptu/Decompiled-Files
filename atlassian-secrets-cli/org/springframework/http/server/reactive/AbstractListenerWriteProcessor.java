/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.http.server.reactive.WriteResultPublisher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractListenerWriteProcessor<T>
implements Processor<T, Void> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    @Nullable
    private Subscription subscription;
    @Nullable
    private volatile T currentData;
    private volatile boolean subscriberCompleted;
    private final WriteResultPublisher resultPublisher = new WriteResultPublisher();

    @Override
    public final void onSubscribe(Subscription subscription) {
        this.state.get().onSubscribe(this, subscription);
    }

    @Override
    public final void onNext(T data) {
        this.logger.trace("Received onNext data item");
        this.state.get().onNext(this, data);
    }

    @Override
    public final void onError(Throwable ex) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Received onError: " + ex);
        }
        this.state.get().onError(this, ex);
    }

    @Override
    public final void onComplete() {
        this.logger.trace("Received onComplete");
        this.state.get().onComplete(this);
    }

    public final void onWritePossible() {
        this.logger.trace("Received onWritePossible");
        this.state.get().onWritePossible(this);
    }

    public void cancel() {
        this.logger.trace("Received request to cancel");
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    @Override
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
        if (result && this.logger.isTraceEnabled()) {
            this.logger.trace((Object)((Object)oldState) + " -> " + (Object)((Object)newState));
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
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("isWritePossible[" + result + "]");
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
                Assert.notNull((Object)subscription, "Subscription must not be null");
                if (((AbstractListenerWriteProcessor)processor).changeState(this, 1.REQUESTED)) {
                    ((AbstractListenerWriteProcessor)processor).subscription = subscription;
                    subscription.request(1L);
                } else {
                    super.onSubscribe(processor, subscription);
                }
            }
        }
        ,
        REQUESTED{

            @Override
            public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
                if (processor.isDataEmpty(data)) {
                    Assert.state(((AbstractListenerWriteProcessor)processor).subscription != null, "No subscription");
                    ((AbstractListenerWriteProcessor)processor).subscription.request(1L);
                } else {
                    processor.dataReceived(data);
                    ((AbstractListenerWriteProcessor)processor).changeStateToReceived(this);
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor)processor).changeStateToComplete(this);
            }
        }
        ,
        RECEIVED{

            @Override
            public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {
                if (((AbstractListenerWriteProcessor)processor).changeState(this, 3.WRITING)) {
                    Object data = ((AbstractListenerWriteProcessor)processor).currentData;
                    Assert.state(data != null, "No data");
                    try {
                        if (processor.write(data)) {
                            if (((AbstractListenerWriteProcessor)processor).changeState(3.WRITING, 3.REQUESTED)) {
                                ((AbstractListenerWriteProcessor)processor).currentData = null;
                                if (((AbstractListenerWriteProcessor)processor).subscriberCompleted) {
                                    ((AbstractListenerWriteProcessor)processor).changeStateToComplete(3.REQUESTED);
                                } else {
                                    processor.writingPaused();
                                    Assert.state(((AbstractListenerWriteProcessor)processor).subscription != null, "No subscription");
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
                ((AbstractListenerWriteProcessor)processor).subscriberCompleted = true;
            }
        }
        ,
        WRITING{

            @Override
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor)processor).subscriberCompleted = true;
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

