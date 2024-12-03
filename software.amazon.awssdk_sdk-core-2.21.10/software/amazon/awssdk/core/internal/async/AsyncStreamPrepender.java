/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.async;

import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class AsyncStreamPrepender<T>
implements Publisher<T> {
    private final Publisher<T> delegate;
    private final T firstItem;

    public AsyncStreamPrepender(Publisher<T> delegate, T firstItem) {
        this.delegate = delegate;
        this.firstItem = firstItem;
    }

    public void subscribe(Subscriber<? super T> s) {
        this.delegate.subscribe((Subscriber)new DelegateSubscriber(s));
    }

    private class DelegateSubscriber
    implements Subscriber<T> {
        private final Subscriber<? super T> subscriber;
        private volatile boolean complete = false;
        private volatile boolean firstRequest = true;

        private DelegateSubscriber(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        public void onSubscribe(final Subscription subscription) {
            this.subscriber.onSubscribe(new Subscription(){
                private final AtomicLong requests = new AtomicLong(0L);
                private volatile boolean cancelled = false;
                private volatile boolean isOutermostCall = true;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void request(long n) {
                    if (this.cancelled) {
                        return;
                    }
                    if (n <= 0L) {
                        subscription.cancel();
                        DelegateSubscriber.this.subscriber.onError((Throwable)new IllegalArgumentException("Requested " + n + " items"));
                    }
                    if (DelegateSubscriber.this.firstRequest) {
                        DelegateSubscriber.this.firstRequest = false;
                        if (n - 1L > 0L) {
                            this.requests.addAndGet(n - 1L);
                        }
                        this.isOutermostCall = false;
                        DelegateSubscriber.this.subscriber.onNext(AsyncStreamPrepender.this.firstItem);
                        this.isOutermostCall = true;
                        if (DelegateSubscriber.this.complete) {
                            DelegateSubscriber.this.subscriber.onComplete();
                            return;
                        }
                    } else {
                        this.requests.addAndGet(n);
                    }
                    if (this.isOutermostCall) {
                        try {
                            long l;
                            this.isOutermostCall = false;
                            while ((l = this.requests.getAndSet(0L)) > 0L) {
                                subscription.request(l);
                            }
                        }
                        finally {
                            this.isOutermostCall = true;
                        }
                    }
                }

                public void cancel() {
                    this.cancelled = true;
                    subscription.cancel();
                }
            });
        }

        public void onNext(T item) {
            this.subscriber.onNext(item);
        }

        public void onError(Throwable t) {
            this.subscriber.onError(t);
        }

        public void onComplete() {
            this.complete = true;
            if (!this.firstRequest) {
                this.subscriber.onComplete();
            }
        }
    }
}

