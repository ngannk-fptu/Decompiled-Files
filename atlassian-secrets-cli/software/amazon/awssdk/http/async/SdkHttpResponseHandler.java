/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.async;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkProtectedApi
public interface SdkHttpResponseHandler<T> {
    public void headersReceived(SdkHttpResponse var1);

    public void onStream(Publisher<ByteBuffer> var1);

    public void exceptionOccurred(Throwable var1);

    public T complete();
}

