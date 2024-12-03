/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.listener.PublisherListener
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.crt.CRT
 *  software.amazon.awssdk.crt.http.HttpHeader
 *  software.amazon.awssdk.crt.s3.S3FinishedResponseContext
 *  software.amazon.awssdk.crt.s3.S3MetaRequest
 *  software.amazon.awssdk.crt.s3.S3MetaRequestProgress
 *  software.amazon.awssdk.crt.s3.S3MetaRequestResponseHandler
 *  software.amazon.awssdk.http.SdkCancellationException
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.http.SdkHttpResponse$Builder
 *  software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.async.SimplePublisher
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.listener.PublisherListener;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.http.HttpHeader;
import software.amazon.awssdk.crt.s3.S3FinishedResponseContext;
import software.amazon.awssdk.crt.s3.S3MetaRequest;
import software.amazon.awssdk.crt.s3.S3MetaRequestProgress;
import software.amazon.awssdk.crt.s3.S3MetaRequestResponseHandler;
import software.amazon.awssdk.http.SdkCancellationException;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.async.SimplePublisher;

@SdkInternalApi
public final class S3CrtResponseHandlerAdapter
implements S3MetaRequestResponseHandler {
    private static final software.amazon.awssdk.utils.Logger log = software.amazon.awssdk.utils.Logger.loggerFor(S3CrtResponseHandlerAdapter.class);
    private final CompletableFuture<Void> resultFuture;
    private final SdkAsyncHttpResponseHandler responseHandler;
    private final SimplePublisher<ByteBuffer> responsePublisher = new SimplePublisher();
    private final SdkHttpResponse.Builder respBuilder = SdkHttpResponse.builder();
    private volatile S3MetaRequest metaRequest;
    private final PublisherListener<S3MetaRequestProgress> progressListener;

    public S3CrtResponseHandlerAdapter(CompletableFuture<Void> executeFuture, SdkAsyncHttpResponseHandler responseHandler, PublisherListener<S3MetaRequestProgress> progressListener) {
        this.resultFuture = executeFuture;
        this.responseHandler = responseHandler;
        this.progressListener = progressListener == null ? new NoOpPublisherListener() : progressListener;
    }

    public void onResponseHeaders(int statusCode, HttpHeader[] headers) {
        for (HttpHeader h : headers) {
            this.respBuilder.appendHeader(h.getName(), h.getValue());
        }
        this.respBuilder.statusCode(statusCode);
        this.responseHandler.onHeaders((SdkHttpResponse)this.respBuilder.build());
        this.responseHandler.onStream(this.responsePublisher);
    }

    public int onResponseBody(ByteBuffer bodyBytesIn, long objectRangeStart, long objectRangeEnd) {
        if (bodyBytesIn == null) {
            this.failResponseHandlerAndFuture(new IllegalStateException("ByteBuffer delivered is null"));
            return 0;
        }
        int bytesReceived = bodyBytesIn.remaining();
        CompletableFuture writeFuture = this.responsePublisher.send((Object)bodyBytesIn);
        writeFuture.whenComplete((result, failure) -> {
            if (failure != null) {
                this.failResponseHandlerAndFuture((Throwable)failure);
                return;
            }
            this.metaRequest.incrementReadWindow((long)bytesReceived);
        });
        return 0;
    }

    public void onFinished(S3FinishedResponseContext context) {
        int crtCode = context.getErrorCode();
        int responseStatus = context.getResponseStatus();
        byte[] errorPayload = context.getErrorPayload();
        if (crtCode != 0) {
            this.handleError(crtCode, responseStatus, errorPayload);
        } else {
            this.onSuccessfulResponseComplete();
        }
    }

    private void onSuccessfulResponseComplete() {
        this.responsePublisher.complete().whenComplete((result, failure) -> {
            if (failure != null) {
                this.failResponseHandlerAndFuture((Throwable)failure);
                return;
            }
            this.progressListener.subscriberOnComplete();
            this.completeFutureAndCloseRequest();
        });
    }

    private void completeFutureAndCloseRequest() {
        this.resultFuture.complete(null);
        FunctionalUtils.runAndLogError((Logger)log.logger(), (String)"Exception thrown in S3MetaRequest#close, ignoring", () -> this.metaRequest.close());
    }

    public void cancelRequest() {
        SdkCancellationException sdkClientException = new SdkCancellationException("request is cancelled");
        this.failResponseHandlerAndFuture((Throwable)sdkClientException);
    }

    private void handleError(int crtCode, int responseStatus, byte[] errorPayload) {
        if (S3CrtResponseHandlerAdapter.isErrorResponse(responseStatus) && errorPayload != null) {
            this.onErrorResponseComplete(errorPayload);
        } else {
            SdkClientException sdkClientException = SdkClientException.create((String)("Failed to send the request: " + CRT.awsErrorString((int)crtCode)));
            this.failResponseHandlerAndFuture((Throwable)sdkClientException);
        }
    }

    private void onErrorResponseComplete(byte[] errorPayload) {
        ((CompletableFuture)this.responsePublisher.send((Object)ByteBuffer.wrap(errorPayload)).thenRun(() -> this.responsePublisher.complete())).handle((ignore, throwable) -> {
            if (throwable != null) {
                this.failResponseHandlerAndFuture((Throwable)throwable);
                return null;
            }
            this.completeFutureAndCloseRequest();
            return null;
        });
    }

    private void failResponseHandlerAndFuture(Throwable exception) {
        this.resultFuture.completeExceptionally(exception);
        FunctionalUtils.runAndLogError((Logger)log.logger(), (String)"Exception thrown in SdkAsyncHttpResponseHandler#onError, ignoring", () -> this.responseHandler.onError(exception));
        FunctionalUtils.runAndLogError((Logger)log.logger(), (String)"Exception thrown in S3MetaRequest#close, ignoring", () -> this.metaRequest.close());
    }

    private static boolean isErrorResponse(int responseStatus) {
        return responseStatus != 0;
    }

    public void metaRequest(S3MetaRequest s3MetaRequest) {
        this.metaRequest = s3MetaRequest;
    }

    public void onProgress(S3MetaRequestProgress progress) {
        this.progressListener.subscriberOnNext((Object)progress);
    }

    private static class NoOpPublisherListener
    implements PublisherListener<S3MetaRequestProgress> {
        private NoOpPublisherListener() {
        }
    }
}

