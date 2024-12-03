/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.Checksum;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.io.InMemoryPublisher;

@SdkInternalApi
public final class ChecksumSubscriber
implements Subscriber<ByteBuffer> {
    private final CompletableFuture<Publisher<ByteBuffer>> checksumming = new CompletableFuture();
    private final Collection<Checksum> checksums = new ArrayList<Checksum>();
    private volatile boolean canceled = false;
    private volatile Subscription subscription;
    private final List<ByteBuffer> bufferedPayload = new ArrayList<ByteBuffer>();

    public ChecksumSubscriber(Collection<? extends Checksum> consumers) {
        this.checksums.addAll(consumers);
        this.checksumming.whenComplete((r, t) -> {
            if (t instanceof CancellationException) {
                ChecksumSubscriber checksumSubscriber = this;
                synchronized (checksumSubscriber) {
                    this.canceled = true;
                    if (this.subscription != null) {
                        this.subscription.cancel();
                    }
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onSubscribe(Subscription subscription) {
        ChecksumSubscriber checksumSubscriber = this;
        synchronized (checksumSubscriber) {
            if (!this.canceled && this.subscription == null) {
                this.subscription = subscription;
                subscription.request(Long.MAX_VALUE);
            } else {
                subscription.cancel();
            }
        }
    }

    public void onNext(ByteBuffer byteBuffer) {
        if (!this.canceled) {
            this.updateChecksumsAndBuffer(byteBuffer);
        }
    }

    private void updateChecksumsAndBuffer(ByteBuffer buffer) {
        int remaining = buffer.remaining();
        if (remaining <= 0) {
            return;
        }
        byte[] copyBuffer = new byte[remaining];
        buffer.get(copyBuffer);
        this.checksums.forEach(c -> c.update(copyBuffer, 0, remaining));
        this.bufferedPayload.add(ByteBuffer.wrap(copyBuffer));
    }

    public void onError(Throwable throwable) {
        this.checksumming.completeExceptionally(throwable);
    }

    public void onComplete() {
        this.checksumming.complete(new InMemoryPublisher(this.bufferedPayload));
    }

    public CompletableFuture<Publisher<ByteBuffer>> completeFuture() {
        return this.checksumming;
    }
}

