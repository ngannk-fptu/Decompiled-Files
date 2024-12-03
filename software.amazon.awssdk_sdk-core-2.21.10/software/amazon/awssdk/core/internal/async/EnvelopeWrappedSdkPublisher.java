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

import java.util.function.BiFunction;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.SdkPublisher;

@SdkInternalApi
public class EnvelopeWrappedSdkPublisher<T>
implements SdkPublisher<T> {
    private final Publisher<T> wrappedPublisher;
    private final T contentPrefix;
    private final T contentSuffix;
    private final BiFunction<T, T, T> mergeContentFunction;

    private EnvelopeWrappedSdkPublisher(Publisher<T> wrappedPublisher, T contentPrefix, T contentSuffix, BiFunction<T, T, T> mergeContentFunction) {
        this.wrappedPublisher = wrappedPublisher;
        this.contentPrefix = contentPrefix;
        this.contentSuffix = contentSuffix;
        this.mergeContentFunction = mergeContentFunction;
    }

    public static <T> EnvelopeWrappedSdkPublisher<T> of(Publisher<T> wrappedPublisher, T contentPrefix, T contentSuffix, BiFunction<T, T, T> mergeContentFunction) {
        return new EnvelopeWrappedSdkPublisher<T>(wrappedPublisher, contentPrefix, contentSuffix, mergeContentFunction);
    }

    public void subscribe(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber must be non-null on call to subscribe()");
        }
        this.wrappedPublisher.subscribe((Subscriber)new ContentWrappedSubscriber(subscriber));
    }

    private class ContentWrappedSubscriber
    implements Subscriber<T> {
        private final Subscriber<? super T> wrappedSubscriber;
        private boolean prefixApplied = false;
        private boolean suffixApplied = false;

        private ContentWrappedSubscriber(Subscriber<? super T> wrappedSubscriber) {
            this.wrappedSubscriber = wrappedSubscriber;
        }

        public void onSubscribe(Subscription subscription) {
            this.wrappedSubscriber.onSubscribe(subscription);
        }

        public void onNext(T t) {
            Object contentToSend = t;
            if (!this.prefixApplied) {
                this.prefixApplied = true;
                if (EnvelopeWrappedSdkPublisher.this.contentPrefix != null) {
                    contentToSend = EnvelopeWrappedSdkPublisher.this.mergeContentFunction.apply(EnvelopeWrappedSdkPublisher.this.contentPrefix, t);
                }
            }
            this.wrappedSubscriber.onNext(contentToSend);
        }

        public void onError(Throwable throwable) {
            this.wrappedSubscriber.onError(throwable);
        }

        public void onComplete() {
            try {
                if (!this.suffixApplied && this.prefixApplied) {
                    this.suffixApplied = true;
                    if (EnvelopeWrappedSdkPublisher.this.contentSuffix != null) {
                        this.wrappedSubscriber.onNext(EnvelopeWrappedSdkPublisher.this.contentSuffix);
                    }
                }
            }
            finally {
                this.wrappedSubscriber.onComplete();
            }
        }
    }
}

