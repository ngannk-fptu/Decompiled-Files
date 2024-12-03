/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpResponse
 */
package software.amazon.awssdk.core.internal.http.async;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkInternalApi
public final class AsyncAfterTransmissionInterceptorCallingResponseHandler<T>
implements TransformingAsyncResponseHandler<T> {
    private final TransformingAsyncResponseHandler<T> delegate;
    private final ExecutionContext context;

    public AsyncAfterTransmissionInterceptorCallingResponseHandler(TransformingAsyncResponseHandler<T> delegate, ExecutionContext context) {
        this.delegate = delegate;
        this.context = context;
    }

    private SdkHttpResponse beforeUnmarshalling(SdkHttpFullResponse response, ExecutionContext context) {
        InterceptorContext interceptorContext = (InterceptorContext)context.interceptorContext().copy(b -> b.httpResponse((SdkHttpResponse)response));
        context.interceptorChain().afterTransmission(interceptorContext, context.executionAttributes());
        interceptorContext = context.interceptorChain().modifyHttpResponse(interceptorContext, context.executionAttributes());
        context.interceptorChain().beforeUnmarshalling(interceptorContext, context.executionAttributes());
        context.interceptorContext(interceptorContext);
        return interceptorContext.httpResponse();
    }

    public void onHeaders(SdkHttpResponse response) {
        this.delegate.onHeaders(this.beforeUnmarshalling((SdkHttpFullResponse)response, this.context));
    }

    public void onError(Throwable error) {
        this.delegate.onError(error);
    }

    public void onStream(Publisher<ByteBuffer> publisher) {
        Optional<Publisher<ByteBuffer>> newPublisher = this.context.interceptorChain().modifyAsyncHttpResponse(this.context.interceptorContext().toBuilder().responsePublisher(publisher).build(), this.context.executionAttributes()).responsePublisher();
        if (newPublisher.isPresent()) {
            this.delegate.onStream(newPublisher.get());
        } else {
            this.delegate.onStream(publisher);
        }
    }

    @Override
    public CompletableFuture<T> prepare() {
        return this.delegate.prepare();
    }
}

