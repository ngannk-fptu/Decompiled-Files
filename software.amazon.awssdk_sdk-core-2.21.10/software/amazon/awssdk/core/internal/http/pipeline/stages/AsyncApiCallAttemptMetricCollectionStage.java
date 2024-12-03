/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.stages.utils.RetryableStageHelper;
import software.amazon.awssdk.core.internal.metrics.SdkErrorType;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class AsyncApiCallAttemptMetricCollectionStage<OutputT>
implements RequestPipeline<SdkHttpFullRequest, CompletableFuture<Response<OutputT>>> {
    private final RequestPipeline<SdkHttpFullRequest, CompletableFuture<Response<OutputT>>> wrapped;

    public AsyncApiCallAttemptMetricCollectionStage(RequestPipeline<SdkHttpFullRequest, CompletableFuture<Response<OutputT>>> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public CompletableFuture<Response<OutputT>> execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        MetricCollector apiCallAttemptMetrics = MetricUtils.createAttemptMetricsCollector(context);
        context.attemptMetricCollector(apiCallAttemptMetrics);
        this.reportBackoffDelay(context);
        CompletableFuture<Response<OutputT>> executeFuture = this.wrapped.execute(input, context);
        CompletionStage metricsCollectedFuture = executeFuture.whenComplete((r, t) -> {
            if (t == null) {
                MetricUtils.collectHttpMetrics(apiCallAttemptMetrics, r.httpResponse());
            }
            if (t != null) {
                this.reportErrorType(context, t.getCause());
            } else if (!Boolean.TRUE.equals(r.isSuccess()) && r.exception() != null) {
                this.reportErrorType(context, r.exception());
            }
        });
        CompletableFutureUtils.forwardExceptionTo((CompletableFuture)metricsCollectedFuture, executeFuture);
        return metricsCollectedFuture;
    }

    private void reportBackoffDelay(RequestExecutionContext context) {
        Duration lastBackoffDelay = context.executionAttributes().getAttribute(RetryableStageHelper.LAST_BACKOFF_DELAY_DURATION);
        if (lastBackoffDelay != null) {
            context.attemptMetricCollector().reportMetric(CoreMetric.BACKOFF_DELAY_DURATION, (Object)lastBackoffDelay);
        }
    }

    private void reportErrorType(RequestExecutionContext context, Throwable t) {
        context.attemptMetricCollector().reportMetric(CoreMetric.ERROR_TYPE, (Object)SdkErrorType.fromException(t).toString());
    }
}

