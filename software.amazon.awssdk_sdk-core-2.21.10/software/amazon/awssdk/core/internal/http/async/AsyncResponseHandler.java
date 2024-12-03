/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpFullResponse$Builder
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.core.internal.http.async;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void onHeaders(SdkHttpResponse response) {
        this.httpResponse = ((SdkHttpFullResponse)response).toBuilder();
    }

    public void onStream(Publisher<ByteBuffer> publisher) {
        publisher.subscribe((Subscriber)new BaosSubscriber(this.streamFuture));
    }

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
                this.httpResponse.content(AbortableInputStream.create((InputStream)new ByteArrayInputStream(baos.toByteArray())));
            }
            try {
                return CompletableFuture.completedFuture(this.responseHandler.handle(this.crc32Validator.apply(this.httpResponse.build()), this.executionAttributes));
            }
            catch (Exception e) {
                return CompletableFutureUtils.failedFuture((Throwable)e);
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

        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(ByteBuffer byteBuffer) {
            this.dataWritten = true;
            try {
                this.baos.write(BinaryUtils.copyBytesFrom((ByteBuffer)byteBuffer));
                this.subscription.request(1L);
            }
            catch (IOException e) {
                this.streamFuture.completeExceptionally(e);
            }
        }

        public void onError(Throwable throwable) {
            this.streamFuture.completeExceptionally(throwable);
        }

        public void onComplete() {
            this.streamFuture.complete(this.dataWritten ? this.baos : null);
        }
    }
}

