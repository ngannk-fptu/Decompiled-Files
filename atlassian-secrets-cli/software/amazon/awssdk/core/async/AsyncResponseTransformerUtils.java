/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.async;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.listener.AsyncResponseTransformerListener;
import software.amazon.awssdk.utils.Pair;

@SdkProtectedApi
public final class AsyncResponseTransformerUtils {
    private AsyncResponseTransformerUtils() {
    }

    public static <ResponseT, ResultT> Pair<AsyncResponseTransformer<ResponseT, ResultT>, CompletableFuture<Void>> wrapWithEndOfStreamFuture(AsyncResponseTransformer<ResponseT, ResultT> responseTransformer) {
        final CompletableFuture future = new CompletableFuture();
        AsyncResponseTransformer<ResponseT, ResultT> wrapped = AsyncResponseTransformerListener.wrap(responseTransformer, new AsyncResponseTransformerListener<ResponseT>(){

            @Override
            public void transformerExceptionOccurred(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void subscriberOnError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void subscriberOnComplete() {
                future.complete(null);
            }
        });
        return Pair.of(wrapped, future);
    }
}

