/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.AwsEndpointProviderUtils;

@SdkInternalApi
public final class SecretsManagerRequestSetEndpointInterceptor
implements ExecutionInterceptor {
    @Override
    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (AwsEndpointProviderUtils.endpointIsDiscovered(executionAttributes)) {
            return context.httpRequest();
        }
        Endpoint endpoint = executionAttributes.getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);
        return AwsEndpointProviderUtils.setUri(context.httpRequest(), executionAttributes.getAttribute(SdkExecutionAttribute.CLIENT_ENDPOINT), endpoint.url());
    }
}

