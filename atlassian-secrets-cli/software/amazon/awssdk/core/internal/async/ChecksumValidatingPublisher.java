/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public final class ChecksumValidatingPublisher
implements SdkPublisher<ByteBuffer> {
    private final Publisher<ByteBuffer> publisher;
    private final SdkChecksum sdkChecksum;
    private final String expectedChecksum;

    public ChecksumValidatingPublisher(Publisher<ByteBuffer> publisher, SdkChecksum sdkChecksum, String expectedChecksum) {
        this.publisher = publisher;
        this.sdkChecksum = sdkChecksum;
        this.expectedChecksum = expectedChecksum;
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        this.publisher.subscribe(new ChecksumValidatingSubscriber(s, this.sdkChecksum, this.expectedChecksum));
    }

    private static class ChecksumValidatingSubscriber
    implements Subscriber<ByteBuffer> {
        private final Subscriber<? super ByteBuffer> wrapped;
        private final SdkChecksum sdkChecksum;
        private final String expectedChecksum;
        private String calculatedChecksum = null;

        ChecksumValidatingSubscriber(Subscriber<? super ByteBuffer> wrapped, SdkChecksum sdkChecksum, String expectedChecksum) {
            this.wrapped = wrapped;
            this.sdkChecksum = sdkChecksum;
            this.expectedChecksum = expectedChecksum;
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.wrapped.onSubscribe(s);
        }

        @Override
        public void onNext(ByteBuffer byteBuffer) {
            byteBuffer.mark();
            try {
                this.sdkChecksum.update(byteBuffer);
            }
            finally {
                byteBuffer.reset();
            }
            this.wrapped.onNext(byteBuffer);
        }

        @Override
        public void onError(Throwable t) {
            this.wrapped.onError(t);
        }

        @Override
        public void onComplete() {
            if (this.calculatedChecksum == null) {
                this.calculatedChecksum = BinaryUtils.toBase64(this.sdkChecksum.getChecksumBytes());
                if (!this.expectedChecksum.equals(this.calculatedChecksum)) {
                    this.onError(SdkClientException.create(String.format("Data read has a different checksum than expected. Was %s, but expected %s", this.calculatedChecksum, this.expectedChecksum)));
                    return;
                }
            }
            this.wrapped.onComplete();
        }
    }
}

