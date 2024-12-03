/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.exception.SdkClientException
 */
package software.amazon.awssdk.auth.signer.internal;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkInternalApi
public final class DigestComputingSubscriber
implements Subscriber<ByteBuffer> {
    private final CompletableFuture<byte[]> digestBytes = new CompletableFuture();
    private final MessageDigest messageDigest;
    private volatile boolean canceled = false;
    private volatile Subscription subscription;
    private final SdkChecksum sdkChecksum;

    public DigestComputingSubscriber(MessageDigest messageDigest, SdkChecksum sdkChecksum) {
        this.messageDigest = messageDigest;
        this.sdkChecksum = sdkChecksum;
        this.digestBytes.whenComplete((r, t) -> {
            if (t instanceof CancellationException) {
                DigestComputingSubscriber digestComputingSubscriber = this;
                synchronized (digestComputingSubscriber) {
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
        DigestComputingSubscriber digestComputingSubscriber = this;
        synchronized (digestComputingSubscriber) {
            if (!this.canceled) {
                this.subscription = subscription;
                subscription.request(Long.MAX_VALUE);
            } else {
                subscription.cancel();
            }
        }
    }

    public void onNext(ByteBuffer byteBuffer) {
        if (!this.canceled) {
            if (this.sdkChecksum != null) {
                ByteBuffer duplicate = byteBuffer.duplicate();
                this.sdkChecksum.update(duplicate);
            }
            this.messageDigest.update(byteBuffer);
        }
    }

    public void onError(Throwable throwable) {
        this.digestBytes.completeExceptionally(throwable);
    }

    public void onComplete() {
        this.digestBytes.complete(this.messageDigest.digest());
    }

    public CompletableFuture<byte[]> digestBytes() {
        return this.digestBytes;
    }

    public static DigestComputingSubscriber forSha256() {
        try {
            return new DigestComputingSubscriber(MessageDigest.getInstance("SHA-256"), null);
        }
        catch (NoSuchAlgorithmException e) {
            throw SdkClientException.create((String)"Unable to create SHA-256 computing subscriber", (Throwable)e);
        }
    }

    public static DigestComputingSubscriber forSha256(SdkChecksum sdkChecksum) {
        try {
            return new DigestComputingSubscriber(MessageDigest.getInstance("SHA-256"), sdkChecksum);
        }
        catch (NoSuchAlgorithmException e) {
            throw SdkClientException.create((String)"Unable to create SHA-256 computing subscriber", (Throwable)e);
        }
    }
}

