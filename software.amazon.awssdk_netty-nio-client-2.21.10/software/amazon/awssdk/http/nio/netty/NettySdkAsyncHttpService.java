/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient$Builder
 *  software.amazon.awssdk.http.async.SdkAsyncHttpService
 */
package software.amazon.awssdk.http.nio.netty;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpService;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;

@SdkPublicApi
public class NettySdkAsyncHttpService
implements SdkAsyncHttpService {
    public SdkAsyncHttpClient.Builder createAsyncHttpClientFactory() {
        return NettyNioAsyncHttpClient.builder();
    }
}

