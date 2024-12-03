/*
 * Decompiled with CFR 0.152.
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

