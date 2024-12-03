/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.DemandIgnoringSubscription;
import software.amazon.awssdk.utils.async.StoringSubscriber;

@SdkProtectedApi
public class ByteBufferStoringSubscriber
implements Subscriber<ByteBuffer> {
    private final long minimumBytesBuffered;
    private final AtomicLong bytesBuffered = new AtomicLong(0L);
    private final StoringSubscriber<ByteBuffer> storingSubscriber;
    private final CountDownLatch subscriptionLatch = new CountDownLatch(1);
    private final Phaser phaser = new Phaser(1);
    private Subscription subscription;

    public ByteBufferStoringSubscriber(long minimumBytesBuffered) {
        this.minimumBytesBuffered = Validate.isPositive(minimumBytesBuffered, "Data buffer minimum must be positive");
        this.storingSubscriber = new StoringSubscriber(Integer.MAX_VALUE);
    }

    public TransferResult transferTo(ByteBuffer out) {
        int transferred = 0;
        Optional<StoringSubscriber.Event<ByteBuffer>> next = this.storingSubscriber.peek();
        while (out.hasRemaining() && next.isPresent() && next.get().type() == StoringSubscriber.EventType.ON_NEXT) {
            transferred += this.transfer(next.get().value(), out);
            next = this.storingSubscriber.peek();
        }
        this.addBufferedDataAmount(-transferred);
        if (!next.isPresent()) {
            return TransferResult.SUCCESS;
        }
        switch (next.get().type()) {
            case ON_COMPLETE: {
                return TransferResult.END_OF_STREAM;
            }
            case ON_ERROR: {
                throw next.get().runtimeError();
            }
            case ON_NEXT: {
                return TransferResult.SUCCESS;
            }
        }
        throw new IllegalStateException("Unknown stored type: " + (Object)((Object)next.get().type()));
    }

    public TransferResult blockingTransferTo(ByteBuffer out) {
        try {
            this.subscriptionLatch.await();
            while (true) {
                int currentPhase = this.phaser.getPhase();
                int positionBeforeTransfer = out.position();
                TransferResult result = this.transferTo(out);
                if (result == TransferResult.END_OF_STREAM) {
                    return TransferResult.END_OF_STREAM;
                }
                if (!out.hasRemaining()) {
                    return TransferResult.SUCCESS;
                }
                if (positionBeforeTransfer != out.position()) continue;
                this.phaser.awaitAdvanceInterruptibly(currentPhase);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private int transfer(ByteBuffer in, ByteBuffer out) {
        int amountToTransfer = Math.min(in.remaining(), out.remaining());
        ByteBuffer truncatedIn = in.duplicate();
        truncatedIn.limit(truncatedIn.position() + amountToTransfer);
        out.put(truncatedIn);
        in.position(truncatedIn.position());
        if (!in.hasRemaining()) {
            this.storingSubscriber.poll();
        }
        return amountToTransfer;
    }

    public void onSubscribe(Subscription s) {
        this.storingSubscriber.onSubscribe(new DemandIgnoringSubscription(s));
        this.subscription = s;
        this.subscription.request(1L);
        this.subscriptionLatch.countDown();
    }

    public void onNext(ByteBuffer byteBuffer) {
        this.storingSubscriber.onNext(byteBuffer.duplicate());
        this.addBufferedDataAmount(byteBuffer.remaining());
        this.phaser.arrive();
    }

    public void onError(Throwable t) {
        this.storingSubscriber.onError(t);
        this.phaser.arrive();
    }

    public void onComplete() {
        this.storingSubscriber.onComplete();
        this.phaser.arrive();
    }

    private void addBufferedDataAmount(long amountToAdd) {
        long currentDataBuffered = this.bytesBuffered.addAndGet(amountToAdd);
        this.maybeRequestMore(currentDataBuffered);
    }

    private void maybeRequestMore(long currentDataBuffered) {
        if (currentDataBuffered < this.minimumBytesBuffered) {
            this.subscription.request(1L);
        }
    }

    public static enum TransferResult {
        END_OF_STREAM,
        SUCCESS;

    }
}

