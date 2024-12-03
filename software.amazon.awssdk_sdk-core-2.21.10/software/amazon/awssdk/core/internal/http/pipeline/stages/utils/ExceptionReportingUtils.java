/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages.utils;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.interceptor.DefaultFailedExecutionContext;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class ExceptionReportingUtils {
    private static final Logger log = Logger.loggerFor(ExceptionReportingUtils.class);

    private ExceptionReportingUtils() {
    }

    public static Throwable reportFailureToInterceptors(RequestExecutionContext context, Throwable failure) {
        DefaultFailedExecutionContext modifiedContext = ExceptionReportingUtils.runModifyException(context, failure);
        try {
            context.interceptorChain().onExecutionFailure(modifiedContext, context.executionAttributes());
        }
        catch (Exception exception) {
            log.warn(() -> "Interceptor chain threw an error from onExecutionFailure().", (Throwable)exception);
        }
        return modifiedContext.exception();
    }

    private static DefaultFailedExecutionContext runModifyException(RequestExecutionContext context, Throwable e) {
        DefaultFailedExecutionContext failedContext = DefaultFailedExecutionContext.builder().interceptorContext(context.executionContext().interceptorContext()).exception(e).build();
        return context.interceptorChain().modifyException(failedContext, context.executionAttributes());
    }
}

