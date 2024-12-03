/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class SequentialSubscriber<T>
implements Subscriber<T> {
    private final Consumer<T> consumer;
    private final CompletableFuture<?> future;
    private Subscription subscription;

    public SequentialSubscriber(Consumer<T> consumer, CompletableFuture<Void> future) {
        this.consumer = consumer;
        this.future = future;
    }

    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1L);
    }

    public void onNext(T t) {
        try {
            this.consumer.accept(t);
            this.subscription.request(1L);
        }
        catch (RuntimeException e) {
            this.subscription.cancel();
            this.future.completeExceptionally(e);
        }
    }

    public void onError(Throwable t) {
        this.future.completeExceptionally(t);
    }

    public void onComplete() {
        this.future.complete(null);
    }
}

