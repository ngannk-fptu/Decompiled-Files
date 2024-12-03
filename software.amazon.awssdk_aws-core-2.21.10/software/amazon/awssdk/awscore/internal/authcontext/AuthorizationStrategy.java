/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.signer.Signer
 */
package software.amazon.awssdk.awscore.internal.authcontext;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.signer.Signer;

@Deprecated
@SdkInternalApi
public interface AuthorizationStrategy {
    public Signer resolveSigner();

    public void addCredentialsToExecutionAttributes(ExecutionAttributes var1);
}

