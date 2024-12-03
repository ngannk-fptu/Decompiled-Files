/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
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

        public void request(long l) {
        }

        public void cancel() {
        }
    };

    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(SUBSCRIPTION);
        subscriber.onComplete();
    }
}

