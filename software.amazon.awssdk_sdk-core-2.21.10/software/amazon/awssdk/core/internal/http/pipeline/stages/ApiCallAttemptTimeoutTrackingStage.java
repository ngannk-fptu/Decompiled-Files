/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.utils.FunctionalUtils
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.AbortedException;
import software.amazon.awssdk.core.exception.ApiCallAttemptTimeoutException;
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
public final class ApiCallAttemptTimeoutTrackingStage<OutputT>
implements RequestToResponsePipeline<OutputT> {
    private final RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped;
    private final Duration apiCallAttemptTimeout;
    private final ScheduledExecutorService timeoutExecutor;

    public ApiCallAttemptTimeoutTrackingStage(HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped) {
        this.wrapped = wrapped;
        this.timeoutExecutor = dependencies.clientConfiguration().option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE);
        this.apiCallAttemptTimeout = dependencies.clientConfiguration().option(SdkClientOption.API_CALL_ATTEMPT_TIMEOUT);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Response<OutputT> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        try {
            Response<OutputT> response;
            long timeoutInMillis = TimerUtils.resolveTimeoutInMillis(context.requestConfig()::apiCallAttemptTimeout, this.apiCallAttemptTimeout);
            TimeoutTracker timeoutTracker = TimerUtils.timeSyncTaskIfNeeded(this.timeoutExecutor, timeoutInMillis, Thread.currentThread());
            try {
                context.apiCallAttemptTimeoutTracker(timeoutTracker);
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
        catch (Exception e) {
            throw this.translatePipelineException(context, e);
        }
    }

    private Exception translatePipelineException(RequestExecutionContext context, Exception e) {
        if (e instanceof InterruptedException) {
            return this.handleInterruptedException(context, (InterruptedException)e);
        }
        if (context.apiCallAttemptTimeoutTracker().hasExecuted()) {
            Thread.interrupted();
        }
        return e;
    }

    private RuntimeException handleInterruptedException(RequestExecutionContext context, InterruptedException e) {
        if (e instanceof SdkInterruptedException) {
            ((SdkInterruptedException)e).getResponseStream().ifPresent(r -> FunctionalUtils.invokeSafely(r::close));
        }
        if (context.apiCallAttemptTimeoutTracker().hasExecuted()) {
            Thread.interrupted();
            return this.generateApiCallAttemptTimeoutException(context);
        }
        Thread.currentThread().interrupt();
        return AbortedException.create("Thread was interrupted", e);
    }

    private ApiCallAttemptTimeoutException generateApiCallAttemptTimeoutException(RequestExecutionContext context) {
        return ApiCallAttemptTimeoutException.create(TimerUtils.resolveTimeoutInMillis(context.requestConfig()::apiCallAttemptTimeout, this.apiCallAttemptTimeout));
    }
}

