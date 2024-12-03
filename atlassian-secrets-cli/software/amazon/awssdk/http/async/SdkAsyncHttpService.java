/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.async;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;

@ThreadSafe
@SdkPublicApi
public interface SdkAsyncHttpService {
    public SdkAsyncHttpClient.Builder createAsyncHttpClientFactory();
}

