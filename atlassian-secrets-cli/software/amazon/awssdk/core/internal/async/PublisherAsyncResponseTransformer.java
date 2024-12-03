/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.ResponsePublisher;
import software.amazon.awssdk.core.async.SdkPublisher;

@SdkInternalApi
public final class PublisherAsyncResponseTransformer<ResponseT extends SdkResponse>
implements AsyncResponseTransformer<ResponseT, ResponsePublisher<ResponseT>> {
    private volatile CompletableFuture<ResponsePublisher<ResponseT>> future;
    private volatile ResponseT response;

    @Override
    public CompletableFuture<ResponsePublisher<ResponseT>> prepare() {
        CompletableFuture<ResponsePublisher<ResponseT>> f = new CompletableFuture<ResponsePublisher<ResponseT>>();
        this.future = f;
        return f;
    }

    @Override
    public void onResponse(ResponseT response) {
        this.response = response;
    }

    @Override
    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        this.future.complete(new ResponsePublisher<ResponseT>(this.response, publisher));
    }

    @Override
    public void exceptionOccurred(Throwable error) {
        this.future.completeExceptionally(error);
    }
}

