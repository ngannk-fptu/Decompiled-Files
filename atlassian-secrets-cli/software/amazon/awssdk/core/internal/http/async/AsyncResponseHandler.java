/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.async;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class AsyncResponseHandler<T>
implements TransformingAsyncResponseHandler<T> {
    private volatile CompletableFuture<ByteArrayOutputStream> streamFuture;
    private final HttpResponseHandler<T> responseHandler;
    private final ExecutionAttributes executionAttributes;
    private final Function<SdkHttpFullResponse, SdkHttpFullResponse> crc32Validator;
    private SdkHttpFullResponse.Builder httpResponse;

    public AsyncResponseHandler(HttpResponseHandler<T> responseHandler, Function<SdkHttpFullResponse, SdkHttpFullResponse> crc32Validator, ExecutionAttributes executionAttributes) {
        this.responseHandler = responseHandler;
        this.executionAttributes = executionAttributes;
        this.crc32Validator = crc32Validator;
    }

    @Override
    public void onHeaders(SdkHttpResponse response) {
        this.httpResponse = ((SdkHttpFullResponse)response).toBuilder();
    }

    @Override
    public void onStream(Publisher<ByteBuffer> publisher) {
        publisher.subscribe(new BaosSubscriber(this.streamFuture));
    }

    @Override
    public void onError(Throwable err) {
        if (this.streamFuture == null) {
            this.prepare();
        }
        this.streamFuture.completeExceptionally(err);
    }

    @Override
    public CompletableFuture<T> prepare() {
        this.streamFuture = new CompletableFuture();
        return this.streamFuture.thenCompose(baos -> {
            if (baos != null) {
                this.httpResponse.content(AbortableInputStream.create(new ByteArrayInputStream(baos.toByteArray())));
            }
            try {
                return CompletableFuture.completedFuture(this.responseHandler.handle(this.crc32Validator.apply(this.httpResponse.build()), this.executionAttributes));
            }
            catch (Exception e) {
                return CompletableFutureUtils.failedFuture(e);
            }
        });
    }

    private static class BaosSubscriber
    implements Subscriber<ByteBuffer> {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final CompletableFuture<ByteArrayOutputStream> streamFuture;
        private Subscription subscription;
        private boolean dataWritten = false;

        private BaosSubscriber(CompletableFuture<ByteArrayOutputStream> streamFuture) {
            this.streamFuture = streamFuture;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer byteBuffer) {
            this.dataWritten = true;
            try {
                this.baos.write(BinaryUtils.copyBytesFrom(byteBuffer));
                this.subscription.request(1L);
            }
            catch (IOException e) {
                this.streamFuture.completeExceptionally(e);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            this.streamFuture.completeExceptionally(throwable);
        }

        @Override
        public void onComplete() {
            this.streamFuture.complete(this.dataWritten ? this.baos : null);
        }
    }
}

