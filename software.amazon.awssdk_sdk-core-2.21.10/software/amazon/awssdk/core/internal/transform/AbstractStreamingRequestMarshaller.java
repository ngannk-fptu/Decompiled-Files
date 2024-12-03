/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 */
package software.amazon.awssdk.core.internal.transform;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public abstract class AbstractStreamingRequestMarshaller<T>
implements Marshaller<T> {
    protected final Marshaller<T> delegateMarshaller;
    protected final boolean requiresLength;
    protected final boolean transferEncoding;
    protected final boolean useHttp2;

    protected AbstractStreamingRequestMarshaller(Builder builder) {
        this.delegateMarshaller = builder.delegateMarshaller;
        this.requiresLength = builder.requiresLength;
        this.transferEncoding = builder.transferEncoding;
        this.useHttp2 = builder.useHttp2;
    }

    protected final void addHeaders(SdkHttpFullRequest.Builder marshalled, Optional<Long> contentLength, boolean requiresLength, boolean transferEncoding, boolean useHttp2) {
        if (marshalled.firstMatchingHeader("Content-Length").isPresent()) {
            return;
        }
        if (contentLength.isPresent()) {
            marshalled.putHeader("Content-Length", Long.toString(contentLength.get()));
            return;
        }
        if (requiresLength) {
            throw SdkClientException.create("This API requires Content-Length header to be set. Please set the content length on the RequestBody.");
        }
        if (transferEncoding && !useHttp2) {
            marshalled.putHeader("Transfer-Encoding", "chunked");
        }
    }

    protected static abstract class Builder<BuilderT extends Builder> {
        private Marshaller delegateMarshaller;
        private boolean requiresLength = Boolean.FALSE;
        private boolean transferEncoding = Boolean.FALSE;
        private boolean useHttp2 = Boolean.FALSE;

        protected Builder() {
        }

        public BuilderT delegateMarshaller(Marshaller delegateMarshaller) {
            this.delegateMarshaller = delegateMarshaller;
            return (BuilderT)this;
        }

        public BuilderT requiresLength(boolean requiresLength) {
            this.requiresLength = requiresLength;
            return (BuilderT)this;
        }

        public BuilderT transferEncoding(boolean transferEncoding) {
            this.transferEncoding = transferEncoding;
            return (BuilderT)this;
        }

        public BuilderT useHttp2(boolean useHttp2) {
            this.useHttp2 = useHttp2;
            return (BuilderT)this;
        }
    }
}

