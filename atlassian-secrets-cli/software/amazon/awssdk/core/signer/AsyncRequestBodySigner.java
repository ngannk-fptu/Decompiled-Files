/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@FunctionalInterface
@SdkPublicApi
public interface AsyncRequestBodySigner {
    public AsyncRequestBody signAsyncRequestBody(SdkHttpFullRequest var1, AsyncRequestBody var2, ExecutionAttributes var3);
}

