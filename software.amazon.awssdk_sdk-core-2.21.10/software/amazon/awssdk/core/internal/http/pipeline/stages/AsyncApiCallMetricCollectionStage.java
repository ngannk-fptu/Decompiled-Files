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
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class AsyncApiCallMetricCollectionStage<OutputT>
implements RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> {
    private final RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> wrapped;

    public AsyncApiCallMetricCollectionStage(RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public CompletableFuture<OutputT> execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        MetricCollector metricCollector = context.executionContext().metricCollector();
        MetricUtils.collectServiceEndpointMetrics(metricCollector, input);
        CompletableFuture future = new CompletableFuture();
        long callStart = System.nanoTime();
        CompletableFuture<OutputT> executeFuture = this.wrapped.execute(input, context);
        executeFuture.whenComplete((r, t) -> {
            long duration = System.nanoTime() - callStart;
            metricCollector.reportMetric(CoreMetric.API_CALL_DURATION, (Object)Duration.ofNanos(duration));
            if (t != null) {
                future.completeExceptionally((Throwable)t);
            } else {
                future.complete(r);
            }
        });
        return CompletableFutureUtils.forwardExceptionTo(future, executeFuture);
    }
}

