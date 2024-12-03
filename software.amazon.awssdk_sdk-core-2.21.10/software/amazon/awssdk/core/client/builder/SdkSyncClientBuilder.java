/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.SdkHttpClient$Builder
 */
package software.amazon.awssdk.core.client.builder;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpClient;

@SdkPublicApi
public interface SdkSyncClientBuilder<B extends SdkSyncClientBuilder<B, C>, C> {
    public B httpClient(SdkHttpClient var1);

    public B httpClientBuilder(SdkHttpClient.Builder var1);
}

