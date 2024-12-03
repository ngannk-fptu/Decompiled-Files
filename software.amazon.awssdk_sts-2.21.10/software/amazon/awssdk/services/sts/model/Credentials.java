/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.sts.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Credentials
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Credentials> {
    private static final SdkField<String> ACCESS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AccessKeyId").getter(Credentials.getter(Credentials::accessKeyId)).setter(Credentials.setter(Builder::accessKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessKeyId").build()}).build();
    private static final SdkField<String> SECRET_ACCESS_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretAccessKey").getter(Credentials.getter(Credentials::secretAccessKey)).setter(Credentials.setter(Builder::secretAccessKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretAccessKey").build()}).build();
    private static final SdkField<String> SESSION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SessionToken").getter(Credentials.getter(Credentials::sessionToken)).setter(Credentials.setter(Builder::sessionToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SessionToken").build()}).build();
    private static final SdkField<Instant> EXPIRATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("Expiration").getter(Credentials.getter(Credentials::expiration)).setter(Credentials.setter(Builder::expiration)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Expiration").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACCESS_KEY_ID_FIELD, SECRET_ACCESS_KEY_FIELD, SESSION_TOKEN_FIELD, EXPIRATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String sessionToken;
    private final Instant expiration;

    private Credentials(BuilderImpl builder) {
        this.accessKeyId = builder.accessKeyId;
        this.secretAccessKey = builder.secretAccessKey;
        this.sessionToken = builder.sessionToken;
        this.expiration = builder.expiration;
    }

    public final String accessKeyId() {
        return this.accessKeyId;
    }

    public final String secretAccessKey() {
        return this.secretAccessKey;
    }

    public final String sessionToken() {
        return this.sessionToken;
    }

    public final Instant expiration() {
        return this.expiration;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.accessKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretAccessKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sessionToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.expiration());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Credentials)) {
            return false;
        }
        Credentials other = (Credentials)obj;
        return Objects.equals(this.accessKeyId(), other.accessKeyId()) && Objects.equals(this.secretAccessKey(), other.secretAccessKey()) && Objects.equals(this.sessionToken(), other.sessionToken()) && Objects.equals(this.expiration(), other.expiration());
    }

    public final String toString() {
        return ToString.builder((String)"Credentials").add("AccessKeyId", (Object)this.accessKeyId()).add("SecretAccessKey", (Object)(this.secretAccessKey() == null ? null : "*** Sensitive Data Redacted ***")).add("SessionToken", (Object)this.sessionToken()).add("Expiration", (Object)this.expiration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "AccessKeyId": {
                return Optional.ofNullable(clazz.cast(this.accessKeyId()));
            }
            case "SecretAccessKey": {
                return Optional.ofNullable(clazz.cast(this.secretAccessKey()));
            }
            case "SessionToken": {
                return Optional.ofNullable(clazz.cast(this.sessionToken()));
            }
            case "Expiration": {
                return Optional.ofNullable(clazz.cast(this.expiration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Credentials, T> g) {
        return obj -> g.apply((Credentials)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String accessKeyId;
        private String secretAccessKey;
        private String sessionToken;
        private Instant expiration;

        private BuilderImpl() {
        }

        private BuilderImpl(Credentials model) {
            this.accessKeyId(model.accessKeyId);
            this.secretAccessKey(model.secretAccessKey);
            this.sessionToken(model.sessionToken);
            this.expiration(model.expiration);
        }

        public final String getAccessKeyId() {
            return this.accessKeyId;
        }

        public final void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        @Override
        public final Builder accessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
            return this;
        }

        public final String getSecretAccessKey() {
            return this.secretAccessKey;
        }

        public final void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        @Override
        public final Builder secretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
            return this;
        }

        public final String getSessionToken() {
            return this.sessionToken;
        }

        public final void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

        @Override
        public final Builder sessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
            return this;
        }

        public final Instant getExpiration() {
            return this.expiration;
        }

        public final void setExpiration(Instant expiration) {
            this.expiration = expiration;
        }

        @Override
        public final Builder expiration(Instant expiration) {
            this.expiration = expiration;
            return this;
        }

        public Credentials build() {
            return new Credentials(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Credentials> {
        public Builder accessKeyId(String var1);

        public Builder secretAccessKey(String var1);

        public Builder sessionToken(String var1);

        public Builder expiration(Instant var1);
    }
}

