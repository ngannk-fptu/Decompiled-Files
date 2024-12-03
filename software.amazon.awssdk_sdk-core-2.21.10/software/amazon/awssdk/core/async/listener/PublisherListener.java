/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.async.listener;

import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.async.listener.SubscriberListener;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public interface PublisherListener<T>
extends SubscriberListener<T> {
    default public void publisherSubscribe(Subscriber<? super T> subscriber) {
    }

    public static <T> SdkPublisher<T> wrap(SdkPublisher<T> delegate, PublisherListener<T> listener) {
        return new NotifyingPublisher<T>(delegate, listener);
    }

    @SdkInternalApi
    public static final class NotifyingPublisher<T>
    implements SdkPublisher<T> {
        private static final Logger log = Logger.loggerFor(NotifyingPublisher.class);
        private final SdkPublisher<T> delegate;
        private final PublisherListener<T> listener;

        NotifyingPublisher(SdkPublisher<T> delegate, PublisherListener<T> listener) {
            this.delegate = (SdkPublisher)Validate.notNull(delegate, (String)"delegate", (Object[])new Object[0]);
            this.listener = (PublisherListener)Validate.notNull(listener, (String)"listener", (Object[])new Object[0]);
        }

        public void subscribe(Subscriber<? super T> s) {
            NotifyingPublisher.invoke(() -> this.listener.publisherSubscribe(s), "publisherSubscribe");
            this.delegate.subscribe(SubscriberListener.wrap(s, this.listener));
        }

        static void invoke(Runnable runnable, String callbackName) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                log.error(() -> callbackName + " callback failed. This exception will be dropped.", (Throwable)e);
            }
        }
    }
}

