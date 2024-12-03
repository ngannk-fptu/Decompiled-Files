/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.http.async;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class SimpleSubscriber
implements Subscriber<ByteBuffer> {
    private final Consumer<ByteBuffer> consumer;
    private final AtomicReference<Subscription> subscription = new AtomicReference();

    public SimpleSubscriber(Consumer<ByteBuffer> consumer) {
        this.consumer = consumer;
    }

    public void onSubscribe(Subscription s) {
        if (s == null) {
            throw new NullPointerException("Subscription MUST NOT be null.");
        }
        if (this.subscription.get() == null) {
            if (this.subscription.compareAndSet(null, s)) {
                s.request(Long.MAX_VALUE);
            } else {
                this.onSubscribe(s);
            }
        } else {
            try {
                s.cancel();
            }
            catch (Throwable t) {
                new IllegalStateException(s + " violated the Reactive Streams rule 3.15 by throwing an exception from cancel.", t).printStackTrace(System.err);
            }
        }
    }

    public void onNext(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            throw new NullPointerException("Element passed to onNext MUST NOT be null.");
        }
        this.consumer.accept(byteBuffer);
    }

    public void onError(Throwable t) {
        if (t == null) {
            throw new NullPointerException("Throwable passed to onError MUST NOT be null.");
        }
    }

    public void onComplete() {
    }
}

