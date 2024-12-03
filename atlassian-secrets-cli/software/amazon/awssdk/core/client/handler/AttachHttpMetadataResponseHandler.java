/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.handler;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@SdkProtectedApi
public final class AttachHttpMetadataResponseHandler<T extends SdkResponse>
implements HttpResponseHandler<T> {
    private final HttpResponseHandler<T> delegate;

    public AttachHttpMetadataResponseHandler(HttpResponseHandler<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
        return (T)((SdkResponse)this.delegate.handle(response, executionAttributes)).toBuilder().sdkHttpResponse(response).build();
    }
}

