/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.apache;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpService;
import software.amazon.awssdk.http.apache.ApacheHttpClient;

@SdkPublicApi
public class ApacheSdkHttpService
implements SdkHttpService {
    @Override
    public SdkHttpClient.Builder createHttpClientBuilder() {
        return ApacheHttpClient.builder();
    }
}

