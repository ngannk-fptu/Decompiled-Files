/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.RequestToResponsePipeline;
import software.amazon.awssdk.core.internal.http.pipeline.stages.utils.RetryableStageHelper;
import software.amazon.awssdk.core.internal.retry.RateLimitingTokenBucket;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public final class RetryableStage<OutputT>
implements RequestToResponsePipeline<OutputT> {
    private final RequestPipeline<SdkHttpFullRequest, Response<OutputT>> requestPipeline;
    private final HttpClientDependencies dependencies;
    private final RateLimitingTokenBucket rateLimitingTokenBucket;

    public RetryableStage(HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, Response<OutputT>> requestPipeline) {
        this.dependencies = dependencies;
        this.requestPipeline = requestPipeline;
        this.rateLimitingTokenBucket = null;
    }

    @SdkTestInternalApi
    public RetryableStage(HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, Response<OutputT>> requestPipeline, RateLimitingTokenBucket rateLimitingTokenBucket) {
        this.dependencies = dependencies;
        this.requestPipeline = requestPipeline;
        this.rateLimitingTokenBucket = rateLimitingTokenBucket;
    }

    @Override
    public Response<OutputT> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        Response<OutputT> response;
        RetryableStageHelper retryableStageHelper = new RetryableStageHelper(request, context, this.rateLimitingTokenBucket, this.dependencies);
        while (true) {
            retryableStageHelper.startingAttempt();
            if (!retryableStageHelper.retryPolicyAllowsRetry()) {
                throw retryableStageHelper.retryPolicyDisallowedRetryException();
            }
            retryableStageHelper.getSendToken();
            Duration backoffDelay = retryableStageHelper.getBackoffDelay();
            if (!backoffDelay.isZero()) {
                retryableStageHelper.logBackingOff(backoffDelay);
                TimeUnit.MILLISECONDS.sleep(backoffDelay.toMillis());
            }
            try {
                retryableStageHelper.logSendingRequest();
                response = this.requestPipeline.execute(retryableStageHelper.requestToSend(), context);
            }
            catch (IOException | SdkException e) {
                retryableStageHelper.setLastException(e);
                retryableStageHelper.updateClientSendingRateForErrorResponse();
                continue;
            }
            retryableStageHelper.setLastResponse(response.httpResponse());
            if (response.isSuccess().booleanValue()) break;
            retryableStageHelper.adjustClockIfClockSkew(response);
            retryableStageHelper.setLastException(response.exception());
            retryableStageHelper.updateClientSendingRateForErrorResponse();
        }
        retryableStageHelper.updateClientSendingRateForSuccessResponse();
        retryableStageHelper.attemptSucceeded();
        return response;
    }
}

