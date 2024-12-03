/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class MakeHttpRequestStage
implements RequestPipeline<SdkHttpFullRequest, Pair<SdkHttpFullRequest, SdkHttpFullResponse>> {
    private final SdkHttpClient sdkHttpClient;

    public MakeHttpRequestStage(HttpClientDependencies dependencies) {
        this.sdkHttpClient = dependencies.clientConfiguration().option(SdkClientOption.SYNC_HTTP_CLIENT);
    }

    @Override
    public Pair<SdkHttpFullRequest, SdkHttpFullResponse> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        InterruptMonitor.checkInterrupted();
        HttpExecuteResponse executeResponse = this.executeHttpRequest(request, context);
        SdkHttpFullResponse httpResponse = (SdkHttpFullResponse)executeResponse.httpResponse();
        return Pair.of(request, httpResponse.toBuilder().content(executeResponse.responseBody().orElse(null)).build());
    }

    private HttpExecuteResponse executeHttpRequest(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        MetricCollector attemptMetricCollector = context.attemptMetricCollector();
        MetricCollector httpMetricCollector = MetricUtils.createHttpMetricsCollector(context);
        ExecutableHttpRequest requestCallable = this.sdkHttpClient.prepareRequest(HttpExecuteRequest.builder().request(request).metricCollector(httpMetricCollector).contentStreamProvider(request.contentStreamProvider().orElse(null)).build());
        context.apiCallTimeoutTracker().abortable(requestCallable);
        context.apiCallAttemptTimeoutTracker().abortable(requestCallable);
        Pair<HttpExecuteResponse, Duration> measuredExecute = MetricUtils.measureDurationUnsafe(requestCallable);
        attemptMetricCollector.reportMetric(CoreMetric.SERVICE_CALL_DURATION, measuredExecute.right());
        return measuredExecute.left();
    }
}

