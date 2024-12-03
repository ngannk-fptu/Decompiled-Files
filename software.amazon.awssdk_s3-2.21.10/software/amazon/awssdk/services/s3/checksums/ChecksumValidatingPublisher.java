/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.SdkPublisher
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.exception.RetryableException
 *  software.amazon.awssdk.utils.BinaryUtils
 */
package software.amazon.awssdk.services.s3.checksums;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public final class ChecksumValidatingPublisher
implements SdkPublisher<ByteBuffer> {
    private final Publisher<ByteBuffer> publisher;
    private final SdkChecksum sdkChecksum;
    private final long contentLength;

    public ChecksumValidatingPublisher(Publisher<ByteBuffer> publisher, SdkChecksum sdkChecksum, long contentLength) {
        this.publisher = publisher;
        this.sdkChecksum = sdkChecksum;
        this.contentLength = contentLength;
    }

    public void subscribe(Subscriber<? super ByteBuffer> s) {
        if (this.contentLength > 0L) {
            this.publisher.subscribe((Subscriber)new ChecksumValidatingSubscriber(s, this.sdkChecksum, this.contentLength));
        } else {
            this.publisher.subscribe((Subscriber)new ChecksumSkippingSubscriber(s));
        }
    }

    private static class ChecksumSkippingSubscriber
    implements Subscriber<ByteBuffer> {
        private static final int CHECKSUM_SIZE = 16;
        private final Subscriber<? super ByteBuffer> wrapped;

        ChecksumSkippingSubscriber(Subscriber<? super ByteBuffer> wrapped) {
            this.wrapped = wrapped;
        }

        public void onSubscribe(Subscription s) {
            this.wrapped.onSubscribe(s);
        }

        public void onNext(ByteBuffer byteBuffer) {
            byte[] buf = BinaryUtils.copyBytesFrom((ByteBuffer)byteBuffer);
            this.wrapped.onNext((Object)ByteBuffer.wrap(Arrays.copyOfRange(buf, 0, buf.length - 16)));
        }

        public void onError(Throwable t) {
            this.wrapped.onError(t);
        }

        public void onComplete() {
            this.wrapped.onComplete();
        }
    }

    private static class ChecksumValidatingSubscriber
    implements Subscriber<ByteBuffer> {
        private static final int CHECKSUM_SIZE = 16;
        private final Subscriber<? super ByteBuffer> wrapped;
        private final SdkChecksum sdkChecksum;
        private final long strippedLength;
        private byte[] streamChecksum = new byte[16];
        private long lengthRead = 0L;

        ChecksumValidatingSubscriber(Subscriber<? super ByteBuffer> wrapped, SdkChecksum sdkChecksum, long contentLength) {
            this.wrapped = wrapped;
            this.sdkChecksum = sdkChecksum;
            this.strippedLength = contentLength - 16L;
        }

        public void onSubscribe(Subscription s) {
            this.wrapped.onSubscribe(s);
        }

        public void onNext(ByteBuffer byteBuffer) {
            byte[] buf = BinaryUtils.copyBytesFrom((ByteBuffer)byteBuffer);
            if (this.lengthRead < this.strippedLength) {
                int toUpdate = (int)Math.min(this.strippedLength - this.lengthRead, (long)buf.length);
                this.sdkChecksum.update(buf, 0, toUpdate);
            }
            this.lengthRead += (long)buf.length;
            if (this.lengthRead >= this.strippedLength) {
                int cksumBytesSoFar = Math.toIntExact(this.lengthRead - this.strippedLength);
                int bufChecksumOffset = buf.length > cksumBytesSoFar ? buf.length - cksumBytesSoFar : 0;
                int streamChecksumOffset = buf.length > cksumBytesSoFar ? 0 : cksumBytesSoFar - buf.length;
                int cksumBytes = Math.min(cksumBytesSoFar, buf.length);
                System.arraycopy(buf, bufChecksumOffset, this.streamChecksum, streamChecksumOffset, cksumBytes);
                if (buf.length > cksumBytesSoFar) {
                    this.wrapped.onNext((Object)ByteBuffer.wrap(Arrays.copyOfRange(buf, 0, buf.length - cksumBytesSoFar)));
                } else {
                    this.wrapped.onNext((Object)ByteBuffer.allocate(0));
                }
            } else {
                this.wrapped.onNext((Object)byteBuffer);
            }
        }

        public void onError(Throwable t) {
            this.wrapped.onError(t);
        }

        public void onComplete() {
            byte[] computedChecksum;
            if (this.strippedLength > 0L && !Arrays.equals(computedChecksum = this.sdkChecksum.getChecksumBytes(), this.streamChecksum)) {
                this.onError((Throwable)RetryableException.create((String)String.format("Data read has a different checksum than expected. Was 0x%s, but expected 0x%s. Common causes: (1) You modified a request ByteBuffer before it could be written to the service. Please ensure your data source does not modify the  byte buffers after you pass them to the SDK. (2) The data was corrupted between the client and service. Note: Despite this error, the upload still completed and was persisted in S3.", BinaryUtils.toHex((byte[])computedChecksum), BinaryUtils.toHex((byte[])this.streamChecksum))));
                return;
            }
            this.wrapped.onComplete();
        }
    }
}

