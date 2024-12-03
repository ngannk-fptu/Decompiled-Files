/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.http.server.reactive.WriteResultPublisher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractListenerWriteFlushProcessor<T>
implements Processor<Publisher<? extends T>, Void> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final AtomicReference<State> state = new AtomicReference<State>(State.UNSUBSCRIBED);
    @Nullable
    private Subscription subscription;
    private volatile boolean subscriberCompleted;
    private final WriteResultPublisher resultPublisher = new WriteResultPublisher();

    @Override
    public final void onSubscribe(Subscription subscription) {
        this.state.get().onSubscribe(this, subscription);
    }

    @Override
    public final void onNext(Publisher<? extends T> publisher) {
        this.logger.trace("Received onNext publisher");
        this.state.get().onNext(this, publisher);
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

    protected final void onFlushPossible() {
        this.state.get().onFlushPossible(this);
    }

    protected void cancel() {
        this.logger.trace("Received request to cancel");
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    @Override
    public final void subscribe(Subscriber<? super Void> subscriber) {
        this.resultPublisher.subscribe(subscriber);
    }

    protected abstract Processor<? super T, Void> createWriteProcessor();

    protected abstract boolean isWritePossible();

    protected abstract void flush() throws IOException;

    protected abstract boolean isFlushPending();

    protected void flushingFailed(Throwable t) {
    }

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && this.logger.isTraceEnabled()) {
            this.logger.trace((Object)((Object)oldState) + " -> " + (Object)((Object)newState));
        }
        return result;
    }

    private void flushIfPossible() {
        boolean result = this.isWritePossible();
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("isWritePossible[" + result + "]");
        }
        if (result) {
            this.onFlushPossible();
        }
    }

    private static enum State {
        UNSUBSCRIBED{

            @Override
            public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> processor, Subscription subscription) {
                Assert.notNull((Object)subscription, "Subscription must not be null");
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 1.REQUESTED)) {
                    ((AbstractListenerWriteFlushProcessor)processor).subscription = subscription;
                    subscription.request(1L);
                } else {
                    super.onSubscribe(processor, subscription);
                }
            }
        }
        ,
        REQUESTED{

            @Override
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> processor, Publisher<? extends T> currentPublisher) {
                if (((AbstractListenerWriteFlushProcessor)processor).changeState(this, 2.RECEIVED)) {
                    Processor<Void, Void> currentProcessor = processor.createWriteProcessor();
                    currentPublisher.subscribe(currentProcessor);
                    currentProcessor.subscribe(new WriteResultSubscriber(processor));
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
                    if (((AbstractListenerWriteFlushProcessor)processor).subscriberCompleted) {
                        if (processor.isFlushPending()) {
                            ((AbstractListenerWriteFlushProcessor)processor).changeState(3.REQUESTED, 3.FLUSHING);
                            ((AbstractListenerWriteFlushProcessor)processor).flushIfPossible();
                        } else if (((AbstractListenerWriteFlushProcessor)processor).changeState(3.REQUESTED, 3.COMPLETED)) {
                            ((AbstractListenerWriteFlushProcessor)processor).resultPublisher.publishComplete();
                        } else {
                            ((State)((Object)((AbstractListenerWriteFlushProcessor)processor).state.get())).onComplete(processor);
                        }
                    } else {
                        Assert.state(((AbstractListenerWriteFlushProcessor)processor).subscription != null, "No subscription");
                        ((AbstractListenerWriteFlushProcessor)processor).subscription.request(1L);
                    }
                }
            }

            @Override
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                ((AbstractListenerWriteFlushProcessor)processor).subscriberCompleted = true;
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

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Void aVoid) {
            }

            @Override
            public void onError(Throwable ex) {
                this.processor.cancel();
                this.processor.onError(ex);
            }

            @Override
            public void onComplete() {
                if (this.processor.logger.isTraceEnabled()) {
                    this.processor.logger.trace(((AbstractListenerWriteFlushProcessor)this.processor).state + " writeComplete");
                }
                ((State)((Object)((AbstractListenerWriteFlushProcessor)this.processor).state.get())).writeComplete(this.processor);
            }
        }
    }
}

