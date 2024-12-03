/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.async.listener;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public interface SubscriberListener<T> {
    default public void subscriberOnNext(T t) {
    }

    default public void subscriberOnComplete() {
    }

    default public void subscriberOnError(Throwable t) {
    }

    default public void subscriptionCancel() {
    }

    public static <T> Subscriber<T> wrap(Subscriber<? super T> delegate, SubscriberListener<? super T> listener) {
        return new NotifyingSubscriber<T>(delegate, listener);
    }

    @SdkInternalApi
    public static final class NotifyingSubscriber<T>
    implements Subscriber<T> {
        private static final Logger log = Logger.loggerFor(NotifyingSubscriber.class);
        private final Subscriber<? super T> delegate;
        private final SubscriberListener<? super T> listener;

        NotifyingSubscriber(Subscriber<? super T> delegate, SubscriberListener<? super T> listener) {
            this.delegate = (Subscriber)Validate.notNull(delegate, (String)"delegate", (Object[])new Object[0]);
            this.listener = (SubscriberListener)Validate.notNull(listener, (String)"listener", (Object[])new Object[0]);
        }

        public void onSubscribe(Subscription s) {
            this.delegate.onSubscribe((Subscription)new NotifyingSubscription(s));
        }

        public void onNext(T t) {
            NotifyingSubscriber.invoke(() -> this.listener.subscriberOnNext(t), "subscriberOnNext");
            this.delegate.onNext(t);
        }

        public void onError(Throwable t) {
            NotifyingSubscriber.invoke(() -> this.listener.subscriberOnError(t), "subscriberOnError");
            this.delegate.onError(t);
        }

        public void onComplete() {
            NotifyingSubscriber.invoke(this.listener::subscriberOnComplete, "subscriberOnComplete");
            this.delegate.onComplete();
        }

        static void invoke(Runnable runnable, String callbackName) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                log.error(() -> callbackName + " callback failed. This exception will be dropped.", (Throwable)e);
            }
        }

        @SdkInternalApi
        final class NotifyingSubscription
        implements Subscription {
            private final Subscription delegateSubscription;

            NotifyingSubscription(Subscription delegateSubscription) {
                this.delegateSubscription = (Subscription)Validate.notNull((Object)delegateSubscription, (String)"delegateSubscription", (Object[])new Object[0]);
            }

            public void request(long n) {
                this.delegateSubscription.request(n);
            }

            public void cancel() {
                NotifyingSubscriber.invoke(NotifyingSubscriber.this.listener::subscriptionCancel, "subscriptionCancel");
                this.delegateSubscription.cancel();
            }
        }
    }
}

