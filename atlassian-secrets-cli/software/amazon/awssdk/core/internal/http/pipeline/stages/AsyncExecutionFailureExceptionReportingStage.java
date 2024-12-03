/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.stages.utils.ExceptionReportingUtils;
import software.amazon.awssdk.core.internal.util.ThrowableUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class AsyncExecutionFailureExceptionReportingStage<OutputT>
implements RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> {
    private final RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> wrapped;

    public AsyncExecutionFailureExceptionReportingStage(RequestPipeline<SdkHttpFullRequest, CompletableFuture<OutputT>> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public CompletableFuture<OutputT> execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        CompletableFuture<OutputT> wrappedExecute = this.wrapped.execute(input, context);
        CompletionStage executeFuture = wrappedExecute.handle((o, t) -> {
            if (t != null) {
                Throwable toReport = t;
                if (toReport instanceof CompletionException) {
                    toReport = toReport.getCause();
                }
                toReport = ExceptionReportingUtils.reportFailureToInterceptors(context, toReport);
                throw CompletableFutureUtils.errorAsCompletionException(ThrowableUtils.asSdkException(toReport));
            }
            return o;
        });
        return CompletableFutureUtils.forwardExceptionTo(executeFuture, wrappedExecute);
    }
}

