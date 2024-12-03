/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.async.DelegatingSubscriber
 *  software.amazon.awssdk.utils.async.FlatteningSubscriber
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Optional;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.internal.async.ChunkBuffer;
import software.amazon.awssdk.core.internal.compression.Compressor;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;
import software.amazon.awssdk.utils.async.FlatteningSubscriber;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkInternalApi
public class CompressionAsyncRequestBody
implements AsyncRequestBody {
    private final AsyncRequestBody wrapped;
    private final Compressor compressor;
    private final ChunkBuffer chunkBuffer;

    private CompressionAsyncRequestBody(DefaultBuilder builder) {
        this.wrapped = (AsyncRequestBody)Validate.paramNotNull((Object)builder.asyncRequestBody, (String)"asyncRequestBody");
        this.compressor = (Compressor)Validate.paramNotNull((Object)builder.compressor, (String)"compressor");
        int chunkSize = builder.chunkSize != null ? builder.chunkSize : 131072;
        this.chunkBuffer = (ChunkBuffer)ChunkBuffer.builder().bufferSize(chunkSize).build();
    }

    public void subscribe(Subscriber<? super ByteBuffer> s) {
        Validate.notNull(s, (String)"Subscription MUST NOT be null.", (Object[])new Object[0]);
        SdkPublisher<Iterable<ByteBuffer>> split = this.split(this.wrapped).addTrailingData(() -> Collections.singleton(this.getBufferedDataIfPresent()));
        SdkPublisher<ByteBuffer> flattening = this.flattening(split);
        flattening.map(this.compressor::compress).subscribe(s);
    }

    @Override
    public Optional<Long> contentLength() {
        return this.wrapped.contentLength();
    }

    @Override
    public String contentType() {
        return this.wrapped.contentType();
    }

    private SdkPublisher<Iterable<ByteBuffer>> split(SdkPublisher<ByteBuffer> source) {
        return subscriber -> source.subscribe((Subscriber)new SplittingSubscriber((Subscriber<? super Iterable<ByteBuffer>>)subscriber));
    }

    private Iterable<ByteBuffer> getBufferedDataIfPresent() {
        return this.chunkBuffer.getBufferedData().map(Collections::singletonList).orElse(Collections.emptyList());
    }

    private SdkPublisher<ByteBuffer> flattening(SdkPublisher<Iterable<ByteBuffer>> source) {
        return subscriber -> source.subscribe((Subscriber)new FlatteningSubscriber(subscriber));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    private final class SplittingSubscriber
    extends DelegatingSubscriber<ByteBuffer, Iterable<ByteBuffer>> {
        protected SplittingSubscriber(Subscriber<? super Iterable<ByteBuffer>> subscriber) {
            super(subscriber);
        }

        public void onNext(ByteBuffer byteBuffer) {
            Iterable<ByteBuffer> buffers = CompressionAsyncRequestBody.this.chunkBuffer.split(byteBuffer);
            this.subscriber.onNext(buffers);
        }
    }

    private static final class DefaultBuilder
    implements Builder {
        private AsyncRequestBody asyncRequestBody;
        private Compressor compressor;
        private Integer chunkSize;

        private DefaultBuilder() {
        }

        public CompressionAsyncRequestBody build() {
            return new CompressionAsyncRequestBody(this);
        }

        @Override
        public Builder asyncRequestBody(AsyncRequestBody asyncRequestBody) {
            this.asyncRequestBody = asyncRequestBody;
            return this;
        }

        @Override
        public Builder compressor(Compressor compressor) {
            this.compressor = compressor;
            return this;
        }

        @Override
        public Builder chunkSize(Integer chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }
    }

    public static interface Builder
    extends SdkBuilder<Builder, CompressionAsyncRequestBody> {
        public Builder asyncRequestBody(AsyncRequestBody var1);

        public Builder compressor(Compressor var1);

        public Builder chunkSize(Integer var1);
    }
}

