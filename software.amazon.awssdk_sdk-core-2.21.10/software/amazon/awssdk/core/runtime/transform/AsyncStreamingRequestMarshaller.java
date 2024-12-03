/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.core.runtime.transform;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.internal.transform.AbstractStreamingRequestMarshaller;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.StringUtils;

@SdkProtectedApi
public final class AsyncStreamingRequestMarshaller<T>
extends AbstractStreamingRequestMarshaller<T> {
    private final AsyncRequestBody asyncRequestBody;

    private AsyncStreamingRequestMarshaller(Builder builder) {
        super(builder);
        this.asyncRequestBody = builder.asyncRequestBody;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SdkHttpFullRequest marshall(T in) {
        SdkHttpFullRequest.Builder marshalled = this.delegateMarshaller.marshall(in).toBuilder();
        String contentType = marshalled.firstMatchingHeader("Content-Type").orElse(null);
        if (StringUtils.isEmpty((CharSequence)contentType)) {
            marshalled.putHeader("Content-Type", this.asyncRequestBody.contentType());
        }
        this.addHeaders(marshalled, this.asyncRequestBody.contentLength(), this.requiresLength, this.transferEncoding, this.useHttp2);
        return marshalled.build();
    }

    public static final class Builder
    extends AbstractStreamingRequestMarshaller.Builder<Builder> {
        private AsyncRequestBody asyncRequestBody;

        public Builder asyncRequestBody(AsyncRequestBody asyncRequestBody) {
            this.asyncRequestBody = asyncRequestBody;
            return this;
        }

        public <T> AsyncStreamingRequestMarshaller<T> build() {
            return new AsyncStreamingRequestMarshaller(this);
        }
    }
}

