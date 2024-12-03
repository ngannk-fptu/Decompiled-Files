/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.async;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class EmptyPublisher<T>
implements Publisher<T> {
    private static final Subscription SUBSCRIPTION = new Subscription(){

        @Override
        public void request(long l) {
        }

        @Override
        public void cancel() {
        }
    };

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(SUBSCRIPTION);
        subscriber.onComplete();
    }
}

