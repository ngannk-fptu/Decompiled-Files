/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.interceptor;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.internal.interceptor.TracingSystemSetting;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.SystemSetting;

@SdkInternalApi
public class TraceIdExecutionInterceptor
implements ExecutionInterceptor {
    private static final String TRACE_ID_HEADER = "X-Amzn-Trace-Id";
    private static final String LAMBDA_FUNCTION_NAME_ENVIRONMENT_VARIABLE = "AWS_LAMBDA_FUNCTION_NAME";

    @Override
    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        Optional<String> traceIdHeader = this.traceIdHeader(context);
        if (!traceIdHeader.isPresent()) {
            Optional<String> lambdafunctionName = this.lambdaFunctionNameEnvironmentVariable();
            Optional<String> traceId = this.traceId();
            if (lambdafunctionName.isPresent() && traceId.isPresent()) {
                return (SdkHttpRequest)context.httpRequest().copy(r -> r.putHeader(TRACE_ID_HEADER, (String)traceId.get()));
            }
        }
        return context.httpRequest();
    }

    private Optional<String> traceIdHeader(Context.ModifyHttpRequest context) {
        return context.httpRequest().firstMatchingHeader(TRACE_ID_HEADER);
    }

    private Optional<String> traceId() {
        return TracingSystemSetting._X_AMZN_TRACE_ID.getStringValue();
    }

    private Optional<String> lambdaFunctionNameEnvironmentVariable() {
        return SystemSetting.getStringValueFromEnvironmentVariable(LAMBDA_FUNCTION_NAME_ENVIRONMENT_VARIABLE);
    }
}

