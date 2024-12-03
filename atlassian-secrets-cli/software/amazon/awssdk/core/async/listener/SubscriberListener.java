/*
 * Decompiled with CFR 0.152.
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
            this.delegate = Validate.notNull(delegate, "delegate", new Object[0]);
            this.listener = Validate.notNull(listener, "listener", new Object[0]);
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.delegate.onSubscribe(new NotifyingSubscription(s));
        }

        @Override
        public void onNext(T t) {
            NotifyingSubscriber.invoke(() -> this.listener.subscriberOnNext(t), "subscriberOnNext");
            this.delegate.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            NotifyingSubscriber.invoke(() -> this.listener.subscriberOnError(t), "subscriberOnError");
            this.delegate.onError(t);
        }

        @Override
        public void onComplete() {
            NotifyingSubscriber.invoke(this.listener::subscriberOnComplete, "subscriberOnComplete");
            this.delegate.onComplete();
        }

        static void invoke(Runnable runnable, String callbackName) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                log.error(() -> callbackName + " callback failed. This exception will be dropped.", e);
            }
        }

        @SdkInternalApi
        final class NotifyingSubscription
        implements Subscription {
            private final Subscription delegateSubscription;

            NotifyingSubscription(Subscription delegateSubscription) {
                this.delegateSubscription = Validate.notNull(delegateSubscription, "delegateSubscription", new Object[0]);
            }

            @Override
            public void request(long n) {
                this.delegateSubscription.request(n);
            }

            @Override
            public void cancel() {
                NotifyingSubscriber.invoke(NotifyingSubscriber.this.listener::subscriptionCancel, "subscriptionCancel");
                this.delegateSubscription.cancel();
            }
        }
    }
}

