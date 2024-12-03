/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.async;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class CombinedResponseAsyncHttpResponseHandler<OutputT>
implements TransformingAsyncResponseHandler<Response<OutputT>> {
    private final TransformingAsyncResponseHandler<OutputT> successResponseHandler;
    private final TransformingAsyncResponseHandler<? extends SdkException> errorResponseHandler;
    private CompletableFuture<SdkHttpResponse> headersFuture;

    public CombinedResponseAsyncHttpResponseHandler(TransformingAsyncResponseHandler<OutputT> successResponseHandler, TransformingAsyncResponseHandler<? extends SdkException> errorResponseHandler) {
        this.successResponseHandler = successResponseHandler;
        this.errorResponseHandler = errorResponseHandler;
    }

    @Override
    public void onHeaders(SdkHttpResponse response) {
        Validate.isTrue(this.headersFuture != null, "onHeaders() invoked without prepare().", new Object[0]);
        this.headersFuture.complete(response);
        SdkStandardLogger.logRequestId(response);
        if (response.isSuccessful()) {
            this.successResponseHandler.onHeaders(response);
        } else {
            this.errorResponseHandler.onHeaders(response);
        }
    }

    @Override
    public void onError(Throwable error) {
        if (this.headersFuture != null) {
            this.headersFuture.completeExceptionally(error);
        }
        this.successResponseHandler.onError(error);
        this.errorResponseHandler.onError(error);
    }

    @Override
    public void onStream(Publisher<ByteBuffer> publisher) {
        Validate.isTrue(this.headersFuture != null, "onStream() invoked without prepare().", new Object[0]);
        Validate.isTrue(this.headersFuture.isDone(), "headersFuture is still not completed when onStream() is invoked.", new Object[0]);
        if (this.headersFuture.isCompletedExceptionally()) {
            return;
        }
        SdkHttpResponse sdkHttpResponse = this.headersFuture.join();
        if (sdkHttpResponse.isSuccessful()) {
            this.successResponseHandler.onStream(publisher);
        } else {
            this.errorResponseHandler.onStream(publisher);
        }
    }

    @Override
    public CompletableFuture<Response<OutputT>> prepare() {
        this.headersFuture = new CompletableFuture();
        CompletableFuture preparedTransformFuture = this.successResponseHandler.prepare();
        CompletableFuture<? extends SdkException> preparedErrorTransformFuture = this.errorResponseHandler == null ? null : this.errorResponseHandler.prepare();
        return this.headersFuture.thenCompose(headers -> {
            SdkHttpFullResponse sdkHttpFullResponse = CombinedResponseAsyncHttpResponseHandler.toFullResponse(headers);
            if (headers.isSuccessful()) {
                return preparedTransformFuture.thenApply(r -> Response.builder().response(r).httpResponse(sdkHttpFullResponse).isSuccess(true).build());
            }
            if (preparedErrorTransformFuture != null) {
                return preparedErrorTransformFuture.thenApply(e -> Response.builder().exception((SdkException)e).httpResponse(sdkHttpFullResponse).isSuccess(false).build());
            }
            return CompletableFuture.completedFuture(Response.builder().httpResponse(sdkHttpFullResponse).isSuccess(false).build());
        });
    }

    private static SdkHttpFullResponse toFullResponse(SdkHttpResponse response) {
        SdkHttpFullResponse.Builder builder = SdkHttpFullResponse.builder().statusCode(response.statusCode());
        response.forEachHeader(builder::putHeader);
        response.statusText().ifPresent(builder::statusText);
        return builder.build();
    }
}

