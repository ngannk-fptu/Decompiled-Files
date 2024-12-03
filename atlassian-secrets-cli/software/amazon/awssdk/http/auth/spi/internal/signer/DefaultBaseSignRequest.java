/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.internal.signer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
abstract class DefaultBaseSignRequest<PayloadT, IdentityT extends Identity>
implements BaseSignRequest<PayloadT, IdentityT> {
    protected final SdkHttpRequest request;
    protected final PayloadT payload;
    protected final IdentityT identity;
    protected final Map<SignerProperty<?>, Object> properties;

    protected DefaultBaseSignRequest(BuilderImpl<?, PayloadT, IdentityT> builder) {
        this.request = Validate.paramNotNull(((BuilderImpl)builder).request, "request");
        this.payload = ((BuilderImpl)builder).payload;
        this.identity = Validate.paramNotNull(((BuilderImpl)builder).identity, "identity");
        this.properties = Collections.unmodifiableMap(new HashMap(((BuilderImpl)builder).properties));
    }

    @Override
    public SdkHttpRequest request() {
        return this.request;
    }

    @Override
    public Optional<PayloadT> payload() {
        return Optional.ofNullable(this.payload);
    }

    @Override
    public IdentityT identity() {
        return this.identity;
    }

    @Override
    public <T> T property(SignerProperty<T> property) {
        return (T)this.properties.get(property);
    }

    @SdkInternalApi
    protected static abstract class BuilderImpl<B extends BaseSignRequest.Builder<B, PayloadT, IdentityT>, PayloadT, IdentityT extends Identity>
    implements BaseSignRequest.Builder<B, PayloadT, IdentityT> {
        private final Map<SignerProperty<?>, Object> properties = new HashMap();
        private SdkHttpRequest request;
        private PayloadT payload;
        private IdentityT identity;

        protected BuilderImpl() {
        }

        protected BuilderImpl(IdentityT identity) {
            this.identity = identity;
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

        @Override
        public B identity(IdentityT identity) {
            this.identity = identity;
            return this.thisBuilder();
        }

        @Override
        public <T> B putProperty(SignerProperty<T> key, T value) {
            this.properties.put(key, value);
            return this.thisBuilder();
        }

        protected B properties(Map<SignerProperty<?>, Object> properties) {
            this.properties.clear();
            this.properties.putAll(properties);
            return this.thisBuilder();
        }

        private B thisBuilder() {
            return (B)this;
        }
    }
}

