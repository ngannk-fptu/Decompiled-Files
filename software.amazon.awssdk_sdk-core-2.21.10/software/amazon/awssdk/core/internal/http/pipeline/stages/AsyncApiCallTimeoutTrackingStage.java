/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.ApiCallTimeoutException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTracker;
import software.amazon.awssdk.core.internal.http.timers.TimerUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public class AsyncApiCallTimeoutTrackingStage<OutputT>
implements RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> {
    private final RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> requestPipeline;
    private final SdkClientConfiguration clientConfig;
    private final ScheduledExecutorService scheduledExecutor;

    public AsyncApiCallTimeoutTrackingStage(HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> requestPipeline) {
        this.requestPipeline = requestPipeline;
        this.scheduledExecutor = dependencies.clientConfiguration().option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE);
        this.clientConfig = dependencies.clientConfiguration();
    }

    @Override
    public CompletableFuture<OutputT> execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        CompletableFuture future = new CompletableFuture();
        long apiCallTimeoutInMillis = TimerUtils.resolveTimeoutInMillis(() -> context.requestConfig().apiCallTimeout(), this.clientConfig.option(SdkClientOption.API_CALL_TIMEOUT));
        Supplier<SdkClientException> exceptionSupplier = () -> ApiCallTimeoutException.create(apiCallTimeoutInMillis);
        TimeoutTracker timeoutTracker = TimerUtils.timeAsyncTaskIfNeeded(future, this.scheduledExecutor, exceptionSupplier, apiCallTimeoutInMillis);
        context.apiCallTimeoutTracker(timeoutTracker);
        CompletableFuture<OutputT> executeFuture = this.requestPipeline.execute(input, context);
        executeFuture.whenComplete((r, t) -> {
            if (t != null) {
                future.completeExceptionally((Throwable)t);
            } else {
                future.complete(r);
            }
        });
        return CompletableFutureUtils.forwardExceptionTo(future, executeFuture);
    }
}

