/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.util.function.Consumer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;
import software.amazon.awssdk.utils.async.DelegatingSubscription;

@SdkProtectedApi
public final class EventListeningSubscriber<T>
extends DelegatingSubscriber<T, T> {
    private static final Logger log = Logger.loggerFor(EventListeningSubscriber.class);
    private final Runnable afterCompleteListener;
    private final Consumer<Throwable> afterErrorListener;
    private final Runnable afterCancelListener;

    public EventListeningSubscriber(Subscriber<T> subscriber, Runnable afterCompleteListener, Consumer<Throwable> afterErrorListener, Runnable afterCancelListener) {
        super(subscriber);
        this.afterCompleteListener = afterCompleteListener;
        this.afterErrorListener = afterErrorListener;
        this.afterCancelListener = afterCancelListener;
    }

    public void onNext(T t) {
        this.subscriber.onNext(t);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        super.onSubscribe(new CancelListeningSubscriber(subscription));
    }

    @Override
    public void onError(Throwable throwable) {
        super.onError(throwable);
        if (this.afterErrorListener != null) {
            this.callListener(() -> this.afterErrorListener.accept(throwable), "Post-onError callback failed. This exception will be dropped.");
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        this.callListener(this.afterCompleteListener, "Post-onComplete callback failed. This exception will be dropped.");
    }

    private void callListener(Runnable listener, String listenerFailureMessage) {
        if (listener != null) {
            try {
                listener.run();
            }
            catch (RuntimeException e) {
                log.error(() -> listenerFailureMessage, e);
            }
        }
    }

    private class CancelListeningSubscriber
    extends DelegatingSubscription {
        protected CancelListeningSubscriber(Subscription s) {
            super(s);
        }

        @Override
        public void cancel() {
            super.cancel();
            EventListeningSubscriber.this.callListener(EventListeningSubscriber.this.afterCancelListener, "Post-cancel callback failed. This exception will be dropped.");
        }
    }
}

