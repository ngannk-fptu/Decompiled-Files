/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.Abortable
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.ExecutableHttpRequest
 *  software.amazon.awssdk.http.HttpExecuteRequest
 *  software.amazon.awssdk.http.HttpExecuteResponse
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpRequest;
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
        return Pair.of((Object)request, (Object)httpResponse.toBuilder().content((AbortableInputStream)executeResponse.responseBody().orElse(null)).build());
    }

    private HttpExecuteResponse executeHttpRequest(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        MetricCollector attemptMetricCollector = context.attemptMetricCollector();
        MetricCollector httpMetricCollector = MetricUtils.createHttpMetricsCollector(context);
        ExecutableHttpRequest requestCallable = this.sdkHttpClient.prepareRequest(HttpExecuteRequest.builder().request((SdkHttpRequest)request).metricCollector(httpMetricCollector).contentStreamProvider((ContentStreamProvider)request.contentStreamProvider().orElse(null)).build());
        context.apiCallTimeoutTracker().abortable((Abortable)requestCallable);
        context.apiCallAttemptTimeoutTracker().abortable((Abortable)requestCallable);
        Pair measuredExecute = MetricUtils.measureDurationUnsafe(requestCallable);
        attemptMetricCollector.reportMetric(CoreMetric.SERVICE_CALL_DURATION, measuredExecute.right());
        return (HttpExecuteResponse)measuredExecute.left();
    }
}

