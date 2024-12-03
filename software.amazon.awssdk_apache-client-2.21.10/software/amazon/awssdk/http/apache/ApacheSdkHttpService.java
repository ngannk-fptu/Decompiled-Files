/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.SdkHttpClient$Builder
 *  software.amazon.awssdk.http.SdkHttpService
 */
package software.amazon.awssdk.http.apache;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpService;
import software.amazon.awssdk.http.apache.ApacheHttpClient;

@SdkPublicApi
public class ApacheSdkHttpService
implements SdkHttpService {
    public SdkHttpClient.Builder createHttpClientBuilder() {
        return ApacheHttpClient.builder();
    }
}

