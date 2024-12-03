/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.internal.scheme;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.IdentityProperty;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@Immutable
public final class DefaultAuthSchemeOption
implements AuthSchemeOption {
    private final String schemeId;
    private final Map<IdentityProperty<?>, Object> identityProperties;
    private final Map<SignerProperty<?>, Object> signerProperties;

    DefaultAuthSchemeOption(BuilderImpl builder) {
        this.schemeId = Validate.paramNotBlank(builder.schemeId, "schemeId");
        this.identityProperties = new HashMap(builder.identityProperties);
        this.signerProperties = new HashMap(builder.signerProperties);
    }

    public static AuthSchemeOption.Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public String schemeId() {
        return this.schemeId;
    }

    @Override
    public <T> T identityProperty(IdentityProperty<T> property) {
        return (T)this.identityProperties.get(property);
    }

    @Override
    public <T> T signerProperty(SignerProperty<T> property) {
        return (T)this.signerProperties.get(property);
    }

    @Override
    public void forEachIdentityProperty(AuthSchemeOption.IdentityPropertyConsumer consumer) {
        this.identityProperties.keySet().forEach(property -> this.consumeProperty((IdentityProperty)property, consumer));
    }

    private <T> void consumeProperty(IdentityProperty<T> property, AuthSchemeOption.IdentityPropertyConsumer consumer) {
        consumer.accept(property, this.identityProperty(property));
    }

    @Override
    public void forEachSignerProperty(AuthSchemeOption.SignerPropertyConsumer consumer) {
        this.signerProperties.keySet().forEach(property -> this.consumeProperty((SignerProperty)property, consumer));
    }

    private <T> void consumeProperty(SignerProperty<T> property, AuthSchemeOption.SignerPropertyConsumer consumer) {
        consumer.accept(property, this.signerProperty(property));
    }

    @Override
    public AuthSchemeOption.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public String toString() {
        return ToString.builder("AuthSchemeOption").add("schemeId", this.schemeId).add("identityProperties", this.identityProperties).add("signerProperties", this.signerProperties).build();
    }

    public static final class BuilderImpl
    implements AuthSchemeOption.Builder {
        private String schemeId;
        private final Map<IdentityProperty<?>, Object> identityProperties = new HashMap();
        private final Map<SignerProperty<?>, Object> signerProperties = new HashMap();

        private BuilderImpl() {
        }

        private BuilderImpl(DefaultAuthSchemeOption authSchemeOption) {
            this.schemeId = authSchemeOption.schemeId;
            this.identityProperties.putAll(authSchemeOption.identityProperties);
            this.signerProperties.putAll(authSchemeOption.signerProperties);
        }

        @Override
        public AuthSchemeOption.Builder schemeId(String schemeId) {
            this.schemeId = schemeId;
            return this;
        }

        @Override
        public <T> AuthSchemeOption.Builder putIdentityProperty(IdentityProperty<T> key, T value) {
            this.identityProperties.put(key, value);
            return this;
        }

        @Override
        public <T> AuthSchemeOption.Builder putIdentityPropertyIfAbsent(IdentityProperty<T> key, T value) {
            this.identityProperties.putIfAbsent(key, value);
            return this;
        }

        @Override
        public <T> AuthSchemeOption.Builder putSignerProperty(SignerProperty<T> key, T value) {
            this.signerProperties.put(key, value);
            return this;
        }

        @Override
        public <T> AuthSchemeOption.Builder putSignerPropertyIfAbsent(SignerProperty<T> key, T value) {
            this.signerProperties.putIfAbsent(key, value);
            return this;
        }

        @Override
        public AuthSchemeOption build() {
            return new DefaultAuthSchemeOption(this);
        }
    }
}

