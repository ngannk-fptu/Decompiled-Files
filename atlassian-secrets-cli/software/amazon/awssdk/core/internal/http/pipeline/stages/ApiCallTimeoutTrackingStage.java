/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.AbortedException;
import software.amazon.awssdk.core.exception.ApiCallTimeoutException;
import software.amazon.awssdk.core.exception.SdkInterruptedException;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.RequestToResponsePipeline;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTracker;
import software.amazon.awssdk.core.internal.http.timers.TimerUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class ApiCallTimeoutTrackingStage<OutputT>
implements RequestToResponsePipeline<OutputT> {
    private final RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped;
    private final SdkClientConfiguration clientConfig;
    private final ScheduledExecutorService timeoutExecutor;
    private final Duration apiCallTimeout;

    public ApiCallTimeoutTrackingStage(HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped) {
        this.wrapped = wrapped;
        this.clientConfig = dependencies.clientConfiguration();
        this.timeoutExecutor = dependencies.clientConfiguration().option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE);
        this.apiCallTimeout = this.clientConfig.option(SdkClientOption.API_CALL_TIMEOUT);
    }

    @Override
    public Response<OutputT> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        try {
            return this.executeWithTimer(request, context);
        }
        catch (Exception e) {
            throw this.translatePipelineException(context, e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Response<OutputT> executeWithTimer(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        Response<OutputT> response;
        long timeoutInMillis = TimerUtils.resolveTimeoutInMillis(context.requestConfig()::apiCallTimeout, this.apiCallTimeout);
        TimeoutTracker timeoutTracker = TimerUtils.timeSyncTaskIfNeeded(this.timeoutExecutor, timeoutInMillis, Thread.currentThread());
        try {
            context.apiCallTimeoutTracker(timeoutTracker);
            response = this.wrapped.execute(request, context);
        }
        finally {
            timeoutTracker.cancel();
        }
        if (timeoutTracker.hasExecuted()) {
            Thread.interrupted();
        }
        return response;
    }

    private Exception translatePipelineException(RequestExecutionContext context, Exception e) {
        if (e instanceof InterruptedException) {
            return this.handleInterruptedException(context, (InterruptedException)e);
        }
        if (ApiCallTimeoutTrackingStage.apiCallTimerExecuted(context)) {
            Thread.interrupted();
        }
        return e;
    }

    private RuntimeException handleInterruptedException(RequestExecutionContext context, InterruptedException e) {
        if (e instanceof SdkInterruptedException) {
            ((SdkInterruptedException)e).getResponseStream().ifPresent(r -> FunctionalUtils.invokeSafely(r::close));
        }
        if (ApiCallTimeoutTrackingStage.apiCallTimerExecuted(context)) {
            Thread.interrupted();
            return this.generateApiCallTimeoutException(context);
        }
        Thread.currentThread().interrupt();
        return AbortedException.create("Thread was interrupted", e);
    }

    private static boolean apiCallTimerExecuted(RequestExecutionContext context) {
        return context.apiCallTimeoutTracker() != null && context.apiCallTimeoutTracker().hasExecuted();
    }

    private ApiCallTimeoutException generateApiCallTimeoutException(RequestExecutionContext context) {
        return ApiCallTimeoutException.create(TimerUtils.resolveTimeoutInMillis(context.requestConfig()::apiCallTimeout, this.apiCallTimeout));
    }
}

