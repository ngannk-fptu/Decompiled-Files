/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.async;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class ByteArrayAsyncResponseTransformer<ResponseT>
implements AsyncResponseTransformer<ResponseT, ResponseBytes<ResponseT>> {
    private volatile CompletableFuture<byte[]> cf;
    private volatile ResponseT response;

    @Override
    public CompletableFuture<ResponseBytes<ResponseT>> prepare() {
        this.cf = new CompletableFuture();
        return this.cf.thenApply(arr -> ResponseBytes.fromByteArray(this.response, arr));
    }

    @Override
    public void onResponse(ResponseT response) {
        this.response = response;
    }

    @Override
    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        publisher.subscribe(new BaosSubscriber(this.cf));
    }

    @Override
    public void exceptionOccurred(Throwable throwable) {
        this.cf.completeExceptionally(throwable);
    }

    static class BaosSubscriber
    implements Subscriber<ByteBuffer> {
        private final CompletableFuture<byte[]> resultFuture;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private Subscription subscription;

        BaosSubscriber(CompletableFuture<byte[]> resultFuture) {
            this.resultFuture = resultFuture;
        }

        @Override
        public void onSubscribe(Subscription s) {
            if (this.subscription != null) {
                s.cancel();
                return;
            }
            this.subscription = s;
            this.subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer byteBuffer) {
            FunctionalUtils.invokeSafely(() -> this.baos.write(BinaryUtils.copyBytesFrom(byteBuffer)));
            this.subscription.request(1L);
        }

        @Override
        public void onError(Throwable throwable) {
            this.baos = null;
            this.resultFuture.completeExceptionally(throwable);
        }

        @Override
        public void onComplete() {
            this.resultFuture.complete(this.baos.toByteArray());
        }
    }
}

