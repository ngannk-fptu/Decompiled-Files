/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.internal.signer;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignedRequest;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
abstract class DefaultBaseSignedRequest<PayloadT>
implements BaseSignedRequest<PayloadT> {
    protected final SdkHttpRequest request;
    protected final PayloadT payload;

    protected DefaultBaseSignedRequest(BuilderImpl<?, PayloadT> builder) {
        this.request = Validate.paramNotNull(((BuilderImpl)builder).request, "request");
        this.payload = ((BuilderImpl)builder).payload;
    }

    @Override
    public SdkHttpRequest request() {
        return this.request;
    }

    @Override
    public Optional<PayloadT> payload() {
        return Optional.ofNullable(this.payload);
    }

    protected static abstract class BuilderImpl<B extends BaseSignedRequest.Builder<B, PayloadT>, PayloadT>
    implements BaseSignedRequest.Builder<B, PayloadT> {
        private SdkHttpRequest request;
        private PayloadT payload;

        protected BuilderImpl() {
        }

        @Override
        public B request(SdkHttpRequest request) {
            this.request = request;
            return this.thisBuilder();
        }

        @Override
        public B payload(PayloadT payload) {
            this.payload = payload;
            return this.thisBuilder();
        }

        private B thisBuilder() {
            return (B)this;
        }
    }
}

