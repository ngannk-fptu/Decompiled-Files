/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.SdkHttpClient;

@ThreadSafe
@SdkPublicApi
public interface SdkHttpService {
    public SdkHttpClient.Builder createHttpClientBuilder();
}

