/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.http.SdkHttpRequest
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.sts.endpoints.internal.AwsEndpointProviderUtils;

@SdkInternalApi
public final class StsRequestSetEndpointInterceptor
implements ExecutionInterceptor {
    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (AwsEndpointProviderUtils.endpointIsDiscovered(executionAttributes)) {
            return context.httpRequest();
        }
        Endpoint endpoint = (Endpoint)executionAttributes.getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);
        return AwsEndpointProviderUtils.setUri(context.httpRequest(), (URI)executionAttributes.getAttribute(SdkExecutionAttribute.CLIENT_ENDPOINT), endpoint.url());
    }
}

