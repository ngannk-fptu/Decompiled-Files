/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.CompletionException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.NonRetryableException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.internal.InternalCoreExecutionAttribute;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.retry.ClockSkewAdjuster;
import software.amazon.awssdk.core.internal.retry.RateLimitingTokenBucket;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.RetryUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkInternalApi
public class RetryableStageHelper {
    public static final String SDK_RETRY_INFO_HEADER = "amz-sdk-request";
    public static final ExecutionAttribute<Duration> LAST_BACKOFF_DELAY_DURATION = new ExecutionAttribute("LastBackoffDuration");
    private final SdkHttpFullRequest request;
    private final RequestExecutionContext context;
    private final RetryPolicy retryPolicy;
    private final RateLimitingTokenBucket rateLimitingTokenBucket;
    private final HttpClientDependencies dependencies;
    private final List<String> exceptionMessageHistory = new ArrayList<String>();
    private int attemptNumber = 0;
    private SdkHttpResponse lastResponse = null;
    private SdkException lastException = null;

    public RetryableStageHelper(SdkHttpFullRequest request, RequestExecutionContext context, RateLimitingTokenBucket rateLimitingTokenBucket, HttpClientDependencies dependencies) {
        this.request = request;
        this.context = context;
        this.retryPolicy = dependencies.clientConfiguration().option(SdkClientOption.RETRY_POLICY);
        this.dependencies = dependencies;
        this.rateLimitingTokenBucket = rateLimitingTokenBucket != null ? rateLimitingTokenBucket : (this.isRateLimitingEnabled() ? new RateLimitingTokenBucket() : null);
    }

    public void startingAttempt() {
        ++this.attemptNumber;
        this.context.executionAttributes().putAttribute(InternalCoreExecutionAttribute.EXECUTION_ATTEMPT, this.attemptNumber);
    }

    public boolean retryPolicyAllowsRetry() {
        if (this.isInitialAttempt()) {
            return true;
        }
        if (this.lastException instanceof NonRetryableException) {
            return false;
        }
        RetryPolicyContext context = this.retryPolicyContext(true);
        boolean willRetry = this.retryPolicy.aggregateRetryCondition().shouldRetry(context);
        if (!willRetry) {
            this.retryPolicy.aggregateRetryCondition().requestWillNotBeRetried(context);
        }
        return willRetry;
    }

    public SdkException retryPolicyDisallowedRetryException() {
        this.context.executionContext().metricCollector().reportMetric(CoreMetric.RETRY_COUNT, this.retriesAttemptedSoFar(true));
        for (int i = 0; i < this.exceptionMessageHistory.size() - 1; ++i) {
            SdkClientException pastException = SdkClientException.builder().message("Request attempt " + (i + 1) + " failure: " + this.exceptionMessageHistory.get(i)).writableStackTrace(false).build();
            this.lastException.addSuppressed(pastException);
        }
        return this.lastException;
    }

    public Duration getBackoffDelay() {
        Duration result;
        if (this.isInitialAttempt()) {
            result = Duration.ZERO;
        } else {
            RetryPolicyContext context = this.retryPolicyContext(true);
            result = RetryUtils.isThrottlingException(this.lastException) ? this.retryPolicy.throttlingBackoffStrategy().computeDelayBeforeNextRetry(context) : this.retryPolicy.backoffStrategy().computeDelayBeforeNextRetry(context);
        }
        this.context.executionAttributes().putAttribute(LAST_BACKOFF_DELAY_DURATION, result);
        return result;
    }

    public void logBackingOff(Duration backoffDelay) {
        SdkStandardLogger.REQUEST_LOGGER.debug(() -> "Retryable error detected. Will retry in " + backoffDelay.toMillis() + "ms. Request attempt number " + this.attemptNumber, this.lastException);
    }

    public SdkHttpFullRequest requestToSend() {
        return this.request.toBuilder().putHeader(SDK_RETRY_INFO_HEADER, "attempt=" + this.attemptNumber + "; max=" + (this.retryPolicy.numRetries() + 1)).build();
    }

    public void logSendingRequest() {
        SdkStandardLogger.REQUEST_LOGGER.debug(() -> (this.isInitialAttempt() ? "Sending" : "Retrying") + " Request: " + this.request);
    }

    public void adjustClockIfClockSkew(Response<?> response) {
        ClockSkewAdjuster clockSkewAdjuster = this.dependencies.clockSkewAdjuster();
        if (!response.isSuccess().booleanValue() && clockSkewAdjuster.shouldAdjust(response.exception())) {
            this.dependencies.updateTimeOffset(clockSkewAdjuster.getAdjustmentInSeconds(response.httpResponse()));
        }
    }

    public void attemptSucceeded() {
        this.retryPolicy.aggregateRetryCondition().requestSucceeded(this.retryPolicyContext(false));
        this.context.executionContext().metricCollector().reportMetric(CoreMetric.RETRY_COUNT, this.retriesAttemptedSoFar(false));
    }

    public int getAttemptNumber() {
        return this.attemptNumber;
    }

    public SdkException getLastException() {
        return this.lastException;
    }

    public void setLastException(Throwable lastException) {
        if (lastException instanceof CompletionException) {
            this.setLastException(lastException.getCause());
        } else if (lastException instanceof SdkException) {
            this.lastException = (SdkException)lastException;
            this.exceptionMessageHistory.add(this.lastException.getMessage());
        } else {
            this.lastException = SdkClientException.create("Unable to execute HTTP request: " + lastException.getMessage(), lastException);
            this.exceptionMessageHistory.add(this.lastException.getMessage());
        }
    }

    public void setLastResponse(SdkHttpResponse lastResponse) {
        this.lastResponse = lastResponse;
    }

    private boolean isRateLimitingEnabled() {
        return this.retryPolicy.retryMode() == RetryMode.ADAPTIVE;
    }

    public boolean isFastFailRateLimiting() {
        return Boolean.TRUE.equals(this.retryPolicy.isFastFailRateLimiting());
    }

    public boolean isLastExceptionThrottlingException() {
        if (this.lastException == null) {
            return false;
        }
        return RetryUtils.isThrottlingException(this.lastException);
    }

    public void getSendToken() {
        if (!this.isRateLimitingEnabled()) {
            return;
        }
        boolean acquired = this.rateLimitingTokenBucket.acquire(1.0, this.isFastFailRateLimiting());
        if (!acquired) {
            String errorMessage = "Unable to acquire a send token immediately without waiting. This indicates that ADAPTIVE retry mode is enabled, fast fail rate limiting is enabled, and that rate limiting is engaged because of prior throttled requests. The request will not be executed.";
            throw SdkClientException.create(errorMessage);
        }
    }

    public OptionalDouble getSendTokenNonBlocking() {
        if (!this.isRateLimitingEnabled()) {
            return OptionalDouble.of(0.0);
        }
        return this.rateLimitingTokenBucket.acquireNonBlocking(1.0, this.isFastFailRateLimiting());
    }

    public void updateClientSendingRateForErrorResponse() {
        if (!this.isRateLimitingEnabled()) {
            return;
        }
        if (this.isLastExceptionThrottlingException()) {
            this.rateLimitingTokenBucket.updateClientSendingRate(true);
        }
    }

    public void updateClientSendingRateForSuccessResponse() {
        if (!this.isRateLimitingEnabled()) {
            return;
        }
        this.rateLimitingTokenBucket.updateClientSendingRate(false);
    }

    private boolean isInitialAttempt() {
        return this.attemptNumber == 1;
    }

    private RetryPolicyContext retryPolicyContext(boolean isBeforeAttemptSent) {
        return RetryPolicyContext.builder().request(this.request).originalRequest(this.context.originalRequest()).exception(this.lastException).retriesAttempted(this.retriesAttemptedSoFar(isBeforeAttemptSent)).executionAttributes(this.context.executionAttributes()).httpStatusCode(this.lastResponse == null ? null : Integer.valueOf(this.lastResponse.statusCode())).build();
    }

    private int retriesAttemptedSoFar(boolean isBeforeAttemptSent) {
        return Math.max(0, isBeforeAttemptSent ? this.attemptNumber - 2 : this.attemptNumber - 1);
    }
}

