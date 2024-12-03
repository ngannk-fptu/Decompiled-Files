/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.io.IOException;
import java.time.Duration;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.stages.utils.RetryableStageHelper;
import software.amazon.awssdk.core.internal.retry.RateLimitingTokenBucket;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class AsyncRetryableStage<OutputT>
implements RequestPipeline<SdkHttpFullRequest, CompletableFuture<Response<OutputT>>> {
    private final TransformingAsyncResponseHandler<Response<OutputT>> responseHandler;
    private final RequestPipeline<SdkHttpFullRequest, CompletableFuture<Response<OutputT>>> requestPipeline;
    private final ScheduledExecutorService scheduledExecutor;
    private final HttpClientDependencies dependencies;
    private final RateLimitingTokenBucket rateLimitingTokenBucket;

    public AsyncRetryableStage(TransformingAsyncResponseHandler<Response<OutputT>> responseHandler, HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, CompletableFuture<Response<OutputT>>> requestPipeline) {
        this.responseHandler = responseHandler;
        this.dependencies = dependencies;
        this.scheduledExecutor = dependencies.clientConfiguration().option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE);
        this.rateLimitingTokenBucket = new RateLimitingTokenBucket();
        this.requestPipeline = requestPipeline;
    }

    @SdkTestInternalApi
    public AsyncRetryableStage(TransformingAsyncResponseHandler<Response<OutputT>> responseHandler, HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, CompletableFuture<Response<OutputT>>> requestPipeline, RateLimitingTokenBucket rateLimitingTokenBucket) {
        this.responseHandler = responseHandler;
        this.dependencies = dependencies;
        this.scheduledExecutor = dependencies.clientConfiguration().option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE);
        this.requestPipeline = requestPipeline;
        this.rateLimitingTokenBucket = rateLimitingTokenBucket;
    }

    @Override
    public CompletableFuture<Response<OutputT>> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        return new RetryingExecutor(request, context).execute();
    }

    private class RetryingExecutor {
        private final AsyncRequestBody originalRequestBody;
        private final RequestExecutionContext context;
        private final RetryableStageHelper retryableStageHelper;

        private RetryingExecutor(SdkHttpFullRequest request, RequestExecutionContext context) {
            this.originalRequestBody = context.requestProvider();
            this.context = context;
            this.retryableStageHelper = new RetryableStageHelper(request, context, AsyncRetryableStage.this.rateLimitingTokenBucket, AsyncRetryableStage.this.dependencies);
        }

        public CompletableFuture<Response<OutputT>> execute() throws Exception {
            CompletableFuture future = new CompletableFuture();
            this.maybeAttemptExecute(future);
            return future;
        }

        public void maybeAttemptExecute(CompletableFuture<Response<OutputT>> future) {
            long totalDelayMillis;
            this.retryableStageHelper.startingAttempt();
            if (!this.retryableStageHelper.retryPolicyAllowsRetry()) {
                future.completeExceptionally(this.retryableStageHelper.retryPolicyDisallowedRetryException());
                return;
            }
            if (this.retryableStageHelper.getAttemptNumber() > 1) {
                AsyncRetryableStage.this.responseHandler.onError(this.retryableStageHelper.getLastException());
                this.context.requestProvider(this.originalRequestBody);
            }
            Duration backoffDelay = this.retryableStageHelper.getBackoffDelay();
            OptionalDouble tokenAcquireTimeSeconds = this.retryableStageHelper.getSendTokenNonBlocking();
            if (!tokenAcquireTimeSeconds.isPresent()) {
                String errorMessage = "Unable to acquire a send token immediately without waiting. This indicates that ADAPTIVE retry mode is enabled, fast fail rate limiting is enabled, and that rate limiting is engaged because of prior throttled requests. The request will not be executed.";
                future.completeExceptionally(SdkClientException.create(errorMessage));
                return;
            }
            long tokenAcquireTimeMillis = (long)(tokenAcquireTimeSeconds.getAsDouble() * 1000.0);
            if (!backoffDelay.isZero()) {
                this.retryableStageHelper.logBackingOff(backoffDelay);
            }
            if ((totalDelayMillis = backoffDelay.toMillis() + tokenAcquireTimeMillis) > 0L) {
                AsyncRetryableStage.this.scheduledExecutor.schedule(() -> this.attemptExecute(future), totalDelayMillis, TimeUnit.MILLISECONDS);
            } else {
                this.attemptExecute(future);
            }
        }

        private void attemptExecute(CompletableFuture<Response<OutputT>> future) {
            CompletableFuture responseFuture;
            try {
                this.retryableStageHelper.logSendingRequest();
                responseFuture = (CompletableFuture)AsyncRetryableStage.this.requestPipeline.execute(this.retryableStageHelper.requestToSend(), this.context);
                CompletableFutureUtils.forwardExceptionTo(future, responseFuture);
            }
            catch (IOException | SdkException e) {
                this.maybeRetryExecute(future, e);
                return;
            }
            catch (Throwable e) {
                future.completeExceptionally(e);
                return;
            }
            responseFuture.whenComplete((response, exception) -> {
                if (exception != null) {
                    if (exception instanceof Exception) {
                        this.maybeRetryExecute(future, (Exception)exception);
                    } else {
                        future.completeExceptionally((Throwable)exception);
                    }
                    return;
                }
                this.retryableStageHelper.setLastResponse(response.httpResponse());
                if (!response.isSuccess().booleanValue()) {
                    this.retryableStageHelper.adjustClockIfClockSkew((Response<?>)response);
                    this.maybeRetryExecute(future, response.exception());
                    return;
                }
                this.retryableStageHelper.updateClientSendingRateForSuccessResponse();
                this.retryableStageHelper.attemptSucceeded();
                future.complete((Response)response);
            });
        }

        private void maybeRetryExecute(CompletableFuture<Response<OutputT>> future, Exception exception) {
            this.retryableStageHelper.setLastException(exception);
            this.retryableStageHelper.updateClientSendingRateForErrorResponse();
            this.maybeAttemptExecute(future);
        }
    }
}

