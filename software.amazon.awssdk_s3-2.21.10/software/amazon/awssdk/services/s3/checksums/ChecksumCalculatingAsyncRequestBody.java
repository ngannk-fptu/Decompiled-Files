/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.utils.BinaryUtils
 */
package software.amazon.awssdk.services.s3.checksums;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public class ChecksumCalculatingAsyncRequestBody
implements AsyncRequestBody {
    private final Long contentLength;
    private final AsyncRequestBody wrapped;
    private final SdkChecksum sdkChecksum;

    public ChecksumCalculatingAsyncRequestBody(SdkHttpRequest request, AsyncRequestBody wrapped, SdkChecksum sdkChecksum) {
        this.contentLength = request.firstMatchingHeader("Content-Length").map(Long::parseLong).orElseGet(() -> wrapped.contentLength().orElse(null));
        this.wrapped = wrapped;
        this.sdkChecksum = sdkChecksum;
    }

    public Optional<Long> contentLength() {
        return this.wrapped.contentLength();
    }

    public String contentType() {
        return this.wrapped.contentType();
    }

    public void subscribe(Subscriber<? super ByteBuffer> s) {
        this.sdkChecksum.reset();
        this.wrapped.subscribe((Subscriber)new ChecksumCalculatingSubscriber(s, this.sdkChecksum, this.contentLength));
    }

    private static final class ChecksumCalculatingSubscriber
    implements Subscriber<ByteBuffer> {
        private final AtomicLong contentRead = new AtomicLong(0L);
        private final Subscriber<? super ByteBuffer> wrapped;
        private final SdkChecksum checksum;
        private final Long contentLength;

        ChecksumCalculatingSubscriber(Subscriber<? super ByteBuffer> wrapped, SdkChecksum sdkChecksum, Long contentLength) {
            this.wrapped = wrapped;
            this.checksum = sdkChecksum;
            this.contentLength = contentLength;
        }

        public void onSubscribe(Subscription s) {
            this.wrapped.onSubscribe(s);
        }

        public void onNext(ByteBuffer byteBuffer) {
            int amountToReadFromByteBuffer = this.getAmountToReadFromByteBuffer(byteBuffer);
            if (amountToReadFromByteBuffer > 0) {
                byte[] buf = BinaryUtils.copyBytesFrom((ByteBuffer)byteBuffer, (int)amountToReadFromByteBuffer);
                this.checksum.update(buf, 0, amountToReadFromByteBuffer);
            }
            this.wrapped.onNext((Object)byteBuffer);
        }

        private int getAmountToReadFromByteBuffer(ByteBuffer byteBuffer) {
            if (this.contentLength == null) {
                return byteBuffer.remaining();
            }
            long amountReadSoFar = this.contentRead.getAndAdd(byteBuffer.remaining());
            long amountRemaining = Math.max(0L, this.contentLength - amountReadSoFar);
            if (amountRemaining > (long)byteBuffer.remaining()) {
                return byteBuffer.remaining();
            }
            return Math.toIntExact(amountRemaining);
        }

        public void onError(Throwable t) {
            this.wrapped.onError(t);
        }

        public void onComplete() {
            this.wrapped.onComplete();
        }
    }
}

