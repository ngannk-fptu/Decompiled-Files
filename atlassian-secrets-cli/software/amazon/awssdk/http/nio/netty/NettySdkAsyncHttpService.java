/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpService;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;

@SdkPublicApi
public class NettySdkAsyncHttpService
implements SdkAsyncHttpService {
    @Override
    public SdkAsyncHttpClient.Builder createAsyncHttpClientFactory() {
        return NettyNioAsyncHttpClient.builder();
    }
}

