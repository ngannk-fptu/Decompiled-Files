/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.async.listener;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.async.listener.PublisherListener;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public interface AsyncResponseTransformerListener<ResponseT>
extends PublisherListener<ByteBuffer> {
    default public void transformerOnResponse(ResponseT response) {
    }

    default public void transformerOnStream(SdkPublisher<ByteBuffer> publisher) {
    }

    default public void transformerExceptionOccurred(Throwable t) {
    }

    public static <ResponseT, ResultT> AsyncResponseTransformer<ResponseT, ResultT> wrap(AsyncResponseTransformer<ResponseT, ResultT> delegate, AsyncResponseTransformerListener<ResponseT> listener) {
        return new NotifyingAsyncResponseTransformer<ResponseT, ResultT>(delegate, listener);
    }

    @SdkInternalApi
    public static final class NotifyingAsyncResponseTransformer<ResponseT, ResultT>
    implements AsyncResponseTransformer<ResponseT, ResultT> {
        private static final Logger log = Logger.loggerFor(NotifyingAsyncResponseTransformer.class);
        private final AsyncResponseTransformer<ResponseT, ResultT> delegate;
        private final AsyncResponseTransformerListener<ResponseT> listener;

        NotifyingAsyncResponseTransformer(AsyncResponseTransformer<ResponseT, ResultT> delegate, AsyncResponseTransformerListener<ResponseT> listener) {
            this.delegate = (AsyncResponseTransformer)Validate.notNull(delegate, (String)"delegate", (Object[])new Object[0]);
            this.listener = (AsyncResponseTransformerListener)Validate.notNull(listener, (String)"listener", (Object[])new Object[0]);
        }

        @Override
        public CompletableFuture<ResultT> prepare() {
            return this.delegate.prepare();
        }

        @Override
        public void onResponse(ResponseT response) {
            NotifyingAsyncResponseTransformer.invoke(() -> this.listener.transformerOnResponse(response), "transformerOnResponse");
            this.delegate.onResponse(response);
        }

        @Override
        public void onStream(SdkPublisher<ByteBuffer> publisher) {
            NotifyingAsyncResponseTransformer.invoke(() -> this.listener.transformerOnStream(publisher), "transformerOnStream");
            this.delegate.onStream(PublisherListener.wrap(publisher, this.listener));
        }

        @Override
        public void exceptionOccurred(Throwable error) {
            NotifyingAsyncResponseTransformer.invoke(() -> this.listener.transformerExceptionOccurred(error), "transformerExceptionOccurred");
            this.delegate.exceptionOccurred(error);
        }

        static void invoke(Runnable runnable, String callbackName) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                log.error(() -> callbackName + " callback failed. This exception will be dropped.", (Throwable)e);
            }
        }
    }
}

