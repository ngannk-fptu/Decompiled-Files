/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 */
package software.amazon.awssdk.core.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.CredentialType;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@FunctionalInterface
@SdkPublicApi
public interface Signer {
    public SdkHttpFullRequest sign(SdkHttpFullRequest var1, ExecutionAttributes var2);

    default public CredentialType credentialType() {
        return null;
    }
}

