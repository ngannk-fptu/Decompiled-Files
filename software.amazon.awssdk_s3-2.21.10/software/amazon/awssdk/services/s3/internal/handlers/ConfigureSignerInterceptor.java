/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.Context$BeforeExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;

@SdkInternalApi
public final class ConfigureSignerInterceptor
implements ExecutionInterceptor {
    public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        executionAttributes.putAttribute(AwsSignerExecutionAttribute.SIGNER_DOUBLE_URL_ENCODE, (Object)Boolean.FALSE);
        executionAttributes.putAttribute(AwsSignerExecutionAttribute.SIGNER_NORMALIZE_PATH, (Object)Boolean.FALSE);
    }
}

