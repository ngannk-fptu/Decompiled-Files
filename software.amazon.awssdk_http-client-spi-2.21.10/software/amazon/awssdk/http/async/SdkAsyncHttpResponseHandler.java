/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.http.async;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkPublicApi
public interface SdkAsyncHttpResponseHandler {
    public void onHeaders(SdkHttpResponse var1);

    public void onStream(Publisher<ByteBuffer> var1);

    public void onError(Throwable var1);
}

