/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.interceptor.Context$FailedExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 */
package software.amazon.awssdk.awscore.interceptor;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.interceptor.HelpfulUnknownHostExceptionInterceptor;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;

@SdkProtectedApi
public class GlobalServiceExecutionInterceptor
implements ExecutionInterceptor {
    private static final HelpfulUnknownHostExceptionInterceptor DELEGATE = new HelpfulUnknownHostExceptionInterceptor();

    public Throwable modifyException(Context.FailedExecution context, ExecutionAttributes executionAttributes) {
        return DELEGATE.modifyException(context, executionAttributes);
    }
}

