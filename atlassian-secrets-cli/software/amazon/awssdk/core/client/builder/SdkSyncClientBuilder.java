/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.builder;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpClient;

@SdkPublicApi
public interface SdkSyncClientBuilder<B extends SdkSyncClientBuilder<B, C>, C> {
    public B httpClient(SdkHttpClient var1);

    public B httpClientBuilder(SdkHttpClient.Builder var1);
}

