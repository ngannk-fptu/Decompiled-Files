/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.S3SignerExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.Context$BeforeExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.S3SignerExecutionAttribute;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;

@SdkInternalApi
public class DisablePayloadSigningInterceptor
implements ExecutionInterceptor {
    public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        executionAttributes.putAttributeIfAbsent(S3SignerExecutionAttribute.ENABLE_PAYLOAD_SIGNING, (Object)false);
    }
}

