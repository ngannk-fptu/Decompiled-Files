/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.async;

import java.util.ArrayList;
import java.util.List;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;

@SdkProtectedApi
public class BufferingSubscriber<T>
extends DelegatingSubscriber<T, List<T>> {
    private final int bufferSize;
    private List<T> currentBuffer;
    private Subscription subscription;

    public BufferingSubscriber(Subscriber<? super List<T>> subscriber, int bufferSize) {
        super(subscriber);
        this.bufferSize = bufferSize;
        this.currentBuffer = new ArrayList<T>(bufferSize);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        super.onSubscribe(subscription);
    }

    @Override
    public void onNext(T t) {
        this.currentBuffer.add(t);
        if (this.currentBuffer.size() == this.bufferSize) {
            this.subscriber.onNext(this.currentBuffer);
            this.currentBuffer.clear();
        } else {
            this.subscription.request(1L);
        }
    }

    @Override
    public void onComplete() {
        if (this.currentBuffer.size() > 0) {
            this.subscriber.onNext(this.currentBuffer);
        }
        super.onComplete();
    }
}

