/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.pipeline.stages.utils.ExceptionReportingUtils;
import software.amazon.awssdk.core.internal.util.ThrowableUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public final class ExecutionFailureExceptionReportingStage<OutputT>
implements RequestPipeline<SdkHttpFullRequest, OutputT> {
    private final RequestPipeline<SdkHttpFullRequest, OutputT> wrapped;

    public ExecutionFailureExceptionReportingStage(RequestPipeline<SdkHttpFullRequest, OutputT> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public OutputT execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        try {
            return this.wrapped.execute(input, context);
        }
        catch (Exception e) {
            Throwable throwable = ExceptionReportingUtils.reportFailureToInterceptors(context, e);
            throw ThrowableUtils.failure(throwable);
        }
    }
}

