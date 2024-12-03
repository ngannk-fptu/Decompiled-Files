/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@FunctionalInterface
@SdkPublicApi
public interface Presigner {
    public SdkHttpFullRequest presign(SdkHttpFullRequest var1, ExecutionAttributes var2);
}

