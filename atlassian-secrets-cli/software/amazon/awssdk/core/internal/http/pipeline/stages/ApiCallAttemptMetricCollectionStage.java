/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.RequestToResponsePipeline;
import software.amazon.awssdk.core.internal.http.pipeline.stages.utils.RetryableStageHelper;
import software.amazon.awssdk.core.internal.metrics.SdkErrorType;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public final class ApiCallAttemptMetricCollectionStage<OutputT>
implements RequestToResponsePipeline<OutputT> {
    private final RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped;

    public ApiCallAttemptMetricCollectionStage(RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Response<OutputT> execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        MetricCollector apiCallAttemptMetrics = MetricUtils.createAttemptMetricsCollector(context);
        context.attemptMetricCollector(apiCallAttemptMetrics);
        this.reportBackoffDelay(context);
        try {
            Response<OutputT> response = this.wrapped.execute(input, context);
            MetricUtils.collectHttpMetrics(apiCallAttemptMetrics, response.httpResponse());
            if (!Boolean.TRUE.equals(response.isSuccess()) && response.exception() != null) {
                this.reportErrorType(context, response.exception());
            }
            return response;
        }
        catch (Exception e) {
            this.reportErrorType(context, e);
            throw e;
        }
    }

    private void reportBackoffDelay(RequestExecutionContext context) {
        Duration lastBackoffDelay = context.executionAttributes().getAttribute(RetryableStageHelper.LAST_BACKOFF_DELAY_DURATION);
        if (lastBackoffDelay != null) {
            context.attemptMetricCollector().reportMetric(CoreMetric.BACKOFF_DELAY_DURATION, lastBackoffDelay);
        }
    }

    private void reportErrorType(RequestExecutionContext context, Exception e) {
        context.attemptMetricCollector().reportMetric(CoreMetric.ERROR_TYPE, SdkErrorType.fromException(e).toString());
    }
}

