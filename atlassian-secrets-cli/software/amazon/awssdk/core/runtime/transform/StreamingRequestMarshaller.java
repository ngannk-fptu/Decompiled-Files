/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.runtime.transform;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.internal.transform.AbstractStreamingRequestMarshaller;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.StringUtils;

@SdkProtectedApi
public final class StreamingRequestMarshaller<T>
extends AbstractStreamingRequestMarshaller<T> {
    private final RequestBody requestBody;

    private StreamingRequestMarshaller(Builder builder) {
        super(builder);
        this.requestBody = builder.requestBody;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SdkHttpFullRequest marshall(T in) {
        SdkHttpFullRequest.Builder marshalled = this.delegateMarshaller.marshall(in).toBuilder();
        marshalled.contentStreamProvider(this.requestBody.contentStreamProvider());
        String contentType = marshalled.firstMatchingHeader("Content-Type").orElse(null);
        if (StringUtils.isEmpty(contentType)) {
            marshalled.putHeader("Content-Type", this.requestBody.contentType());
        }
        this.addHeaders(marshalled, this.requestBody.optionalContentLength(), this.requiresLength, this.transferEncoding, this.useHttp2);
        return marshalled.build();
    }

    public static final class Builder
    extends AbstractStreamingRequestMarshaller.Builder<Builder> {
        private RequestBody requestBody;

        public Builder requestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public <T> StreamingRequestMarshaller<T> build() {
            return new StreamingRequestMarshaller(this);
        }
    }
}

