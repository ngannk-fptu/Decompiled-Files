/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.AbortedException;
import software.amazon.awssdk.core.exception.ApiCallAttemptTimeoutException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkInterruptedException;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.RequestToResponsePipeline;
import software.amazon.awssdk.core.internal.http.timers.TimerUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class TimeoutExceptionHandlingStage<OutputT>
implements RequestToResponsePipeline<OutputT> {
    private final HttpClientDependencies dependencies;
    private final RequestPipeline<SdkHttpFullRequest, Response<OutputT>> requestPipeline;

    public TimeoutExceptionHandlingStage(HttpClientDependencies dependencies, RequestPipeline<SdkHttpFullRequest, Response<OutputT>> requestPipeline) {
        this.dependencies = dependencies;
        this.requestPipeline = requestPipeline;
    }

    @Override
    public Response<OutputT> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        try {
            return this.requestPipeline.execute(request, context);
        }
        catch (Exception e) {
            throw this.translatePipelineException(context, e);
        }
    }

    private Exception translatePipelineException(RequestExecutionContext context, Exception e) {
        if (e instanceof InterruptedException || e instanceof IOException || e instanceof AbortedException || Thread.currentThread().isInterrupted() || e instanceof SdkClientException && this.isCausedByApiCallAttemptTimeout(context)) {
            return this.handleTimeoutCausedException(context, e);
        }
        return e;
    }

    private Exception handleTimeoutCausedException(RequestExecutionContext context, Exception e) {
        if (e instanceof SdkInterruptedException) {
            ((SdkInterruptedException)e).getResponseStream().ifPresent(r -> FunctionalUtils.invokeSafely(r::close));
        }
        if (this.isCausedByApiCallTimeout(context)) {
            return new InterruptedException();
        }
        if (this.isCausedByApiCallAttemptTimeout(context)) {
            Thread.interrupted();
            return this.generateApiCallAttemptTimeoutException(context);
        }
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            return AbortedException.create("Thread was interrupted", e);
        }
        return e;
    }

    private boolean isCausedByApiCallAttemptTimeout(RequestExecutionContext context) {
        return context.apiCallAttemptTimeoutTracker().hasExecuted();
    }

    private boolean isCausedByApiCallTimeout(RequestExecutionContext context) {
        return context.apiCallTimeoutTracker().hasExecuted();
    }

    private ApiCallAttemptTimeoutException generateApiCallAttemptTimeoutException(RequestExecutionContext context) {
        return ApiCallAttemptTimeoutException.create(TimerUtils.resolveTimeoutInMillis(context.requestConfig()::apiCallAttemptTimeout, this.dependencies.clientConfiguration().option(SdkClientOption.API_CALL_ATTEMPT_TIMEOUT)));
    }
}

