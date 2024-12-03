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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.sts.model.AssumedRoleUser;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.StsResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AssumeRoleWithWebIdentityResponse
extends StsResponse
implements ToCopyableBuilder<Builder, AssumeRoleWithWebIdentityResponse> {
    private static final SdkField<Credentials> CREDENTIALS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Credentials").getter(AssumeRoleWithWebIdentityResponse.getter(AssumeRoleWithWebIdentityResponse::credentials)).setter(AssumeRoleWithWebIdentityResponse.setter(Builder::credentials)).constructor(Credentials::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Credentials").build()}).build();
    private static final SdkField<String> SUBJECT_FROM_WEB_IDENTITY_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SubjectFromWebIdentityToken").getter(AssumeRoleWithWebIdentityResponse.getter(AssumeRoleWithWebIdentityResponse::subjectFromWebIdentityToken)).setter(AssumeRoleWithWebIdentityResponse.setter(Builder::subjectFromWebIdentityToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SubjectFromWebIdentityToken").build()}).build();
    private static final SdkField<AssumedRoleUser> ASSUMED_ROLE_USER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("AssumedRoleUser").getter(AssumeRoleWithWebIdentityResponse.getter(AssumeRoleWithWebIdentityResponse::assumedRoleUser)).setter(AssumeRoleWithWebIdentityResponse.setter(Builder::assumedRoleUser)).constructor(AssumedRoleUser::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AssumedRoleUser").build()}).build();
    private static final SdkField<Integer> PACKED_POLICY_SIZE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PackedPolicySize").getter(AssumeRoleWithWebIdentityResponse.getter(AssumeRoleWithWebIdentityResponse::packedPolicySize)).setter(AssumeRoleWithWebIdentityResponse.setter(Builder::packedPolicySize)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PackedPolicySize").build()}).build();
    private static final SdkField<String> PROVIDER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Provider").getter(AssumeRoleWithWebIdentityResponse.getter(AssumeRoleWithWebIdentityResponse::provider)).setter(AssumeRoleWithWebIdentityResponse.setter(Builder::provider)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Provider").build()}).build();
    private static final SdkField<String> AUDIENCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Audience").getter(AssumeRoleWithWebIdentityResponse.getter(AssumeRoleWithWebIdentityResponse::audience)).setter(AssumeRoleWithWebIdentityResponse.setter(Builder::audience)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Audience").build()}).build();
    private static final SdkField<String> SOURCE_IDENTITY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceIdentity").getter(AssumeRoleWithWebIdentityResponse.getter(AssumeRoleWithWebIdentityResponse::sourceIdentity)).setter(AssumeRoleWithWebIdentityResponse.setter(Builder::sourceIdentity)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceIdentity").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CREDENTIALS_FIELD, SUBJECT_FROM_WEB_IDENTITY_TOKEN_FIELD, ASSUMED_ROLE_USER_FIELD, PACKED_POLICY_SIZE_FIELD, PROVIDER_FIELD, AUDIENCE_FIELD, SOURCE_IDENTITY_FIELD));
    private final Credentials credentials;
    private final String subjectFromWebIdentityToken;
    private final AssumedRoleUser assumedRoleUser;
    private final Integer packedPolicySize;
    private final String provider;
    private final String audience;
    private final String sourceIdentity;

    private AssumeRoleWithWebIdentityResponse(BuilderImpl builder) {
        super(builder);
        this.credentials = builder.credentials;
        this.subjectFromWebIdentityToken = builder.subjectFromWebIdentityToken;
        this.assumedRoleUser = builder.assumedRoleUser;
        this.packedPolicySize = builder.packedPolicySize;
        this.provider = builder.provider;
        this.audience = builder.audience;
        this.sourceIdentity = builder.sourceIdentity;
    }

    public final Credentials credentials() {
        return this.credentials;
    }

    public final String subjectFromWebIdentityToken() {
        return this.subjectFromWebIdentityToken;
    }

    public final AssumedRoleUser assumedRoleUser() {
        return this.assumedRoleUser;
    }

    public final Integer packedPolicySize() {
        return this.packedPolicySize;
    }

    public final String provider() {
        return this.provider;
    }

    public final String audience() {
        return this.audience;
    }

    public final String sourceIdentity() {
        return this.sourceIdentity;
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.credentials());
        hashCode = 31 * hashCode + Objects.hashCode(this.subjectFromWebIdentityToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.assumedRoleUser());
        hashCode = 31 * hashCode + Objects.hashCode(this.packedPolicySize());
        hashCode = 31 * hashCode + Objects.hashCode(this.provider());
        hashCode = 31 * hashCode + Objects.hashCode(this.audience());
        hashCode = 31 * hashCode + Objects.hashCode(this.sourceIdentity());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AssumeRoleWithWebIdentityResponse)) {
            return false;
        }
        AssumeRoleWithWebIdentityResponse other = (AssumeRoleWithWebIdentityResponse)((Object)obj);
        return Objects.equals(this.credentials(), other.credentials()) && Objects.equals(this.subjectFromWebIdentityToken(), other.subjectFromWebIdentityToken()) && Objects.equals(this.assumedRoleUser(), other.assumedRoleUser()) && Objects.equals(this.packedPolicySize(), other.packedPolicySize()) && Objects.equals(this.provider(), other.provider()) && Objects.equals(this.audience(), other.audience()) && Objects.equals(this.sourceIdentity(), other.sourceIdentity());
    }

    public final String toString() {
        return ToString.builder((String)"AssumeRoleWithWebIdentityResponse").add("Credentials", (Object)this.credentials()).add("SubjectFromWebIdentityToken", (Object)this.subjectFromWebIdentityToken()).add("AssumedRoleUser", (Object)this.assumedRoleUser()).add("PackedPolicySize", (Object)this.packedPolicySize()).add("Provider", (Object)this.provider()).add("Audience", (Object)this.audience()).add("SourceIdentity", (Object)this.sourceIdentity()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Credentials": {
                return Optional.ofNullable(clazz.cast(this.credentials()));
            }
            case "SubjectFromWebIdentityToken": {
                return Optional.ofNullable(clazz.cast(this.subjectFromWebIdentityToken()));
            }
            case "AssumedRoleUser": {
                return Optional.ofNullable(clazz.cast(this.assumedRoleUser()));
            }
            case "PackedPolicySize": {
                return Optional.ofNullable(clazz.cast(this.packedPolicySize()));
            }
            case "Provider": {
                return Optional.ofNullable(clazz.cast(this.provider()));
            }
            case "Audience": {
                return Optional.ofNullable(clazz.cast(this.audience()));
            }
            case "SourceIdentity": {
                return Optional.ofNullable(clazz.cast(this.sourceIdentity()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AssumeRoleWithWebIdentityResponse, T> g) {
        return obj -> g.apply((AssumeRoleWithWebIdentityResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsResponse.BuilderImpl
    implements Builder {
        private Credentials credentials;
        private String subjectFromWebIdentityToken;
        private AssumedRoleUser assumedRoleUser;
        private Integer packedPolicySize;
        private String provider;
        private String audience;
        private String sourceIdentity;

        private BuilderImpl() {
        }

        private BuilderImpl(AssumeRoleWithWebIdentityResponse model) {
            super(model);
            this.credentials(model.credentials);
            this.subjectFromWebIdentityToken(model.subjectFromWebIdentityToken);
            this.assumedRoleUser(model.assumedRoleUser);
            this.packedPolicySize(model.packedPolicySize);
            this.provider(model.provider);
            this.audience(model.audience);
            this.sourceIdentity(model.sourceIdentity);
        }

        public final Credentials.Builder getCredentials() {
            return this.credentials != null ? this.credentials.toBuilder() : null;
        }

        public final void setCredentials(Credentials.BuilderImpl credentials) {
            this.credentials = credentials != null ? credentials.build() : null;
        }

        @Override
        public final Builder credentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public final String getSubjectFromWebIdentityToken() {
            return this.subjectFromWebIdentityToken;
        }

        public final void setSubjectFromWebIdentityToken(String subjectFromWebIdentityToken) {
            this.subjectFromWebIdentityToken = subjectFromWebIdentityToken;
        }

        @Override
        public final Builder subjectFromWebIdentityToken(String subjectFromWebIdentityToken) {
            this.subjectFromWebIdentityToken = subjectFromWebIdentityToken;
            return this;
        }

        public final AssumedRoleUser.Builder getAssumedRoleUser() {
            return this.assumedRoleUser != null ? this.assumedRoleUser.toBuilder() : null;
        }

        public final void setAssumedRoleUser(AssumedRoleUser.BuilderImpl assumedRoleUser) {
            this.assumedRoleUser = assumedRoleUser != null ? assumedRoleUser.build() : null;
        }

        @Override
        public final Builder assumedRoleUser(AssumedRoleUser assumedRoleUser) {
            this.assumedRoleUser = assumedRoleUser;
            return this;
        }

        public final Integer getPackedPolicySize() {
            return this.packedPolicySize;
        }

        public final void setPackedPolicySize(Integer packedPolicySize) {
            this.packedPolicySize = packedPolicySize;
        }

        @Override
        public final Builder packedPolicySize(Integer packedPolicySize) {
            this.packedPolicySize = packedPolicySize;
            return this;
        }

        public final String getProvider() {
            return this.provider;
        }

        public final void setProvider(String provider) {
            this.provider = provider;
        }

        @Override
        public final Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public final String getAudience() {
            return this.audience;
        }

        public final void setAudience(String audience) {
            this.audience = audience;
        }

        @Override
        public final Builder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public final String getSourceIdentity() {
            return this.sourceIdentity;
        }

        public final void setSourceIdentity(String sourceIdentity) {
            this.sourceIdentity = sourceIdentity;
        }

        @Override
        public final Builder sourceIdentity(String sourceIdentity) {
            this.sourceIdentity = sourceIdentity;
            return this;
        }

        @Override
        public AssumeRoleWithWebIdentityResponse build() {
            return new AssumeRoleWithWebIdentityResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, AssumeRoleWithWebIdentityResponse> {
        public Builder credentials(Credentials var1);

        default public Builder credentials(Consumer<Credentials.Builder> credentials) {
            return this.credentials((Credentials)((Credentials.Builder)Credentials.builder().applyMutation(credentials)).build());
        }

        public Builder subjectFromWebIdentityToken(String var1);

        public Builder assumedRoleUser(AssumedRoleUser var1);

        default public Builder assumedRoleUser(Consumer<AssumedRoleUser.Builder> assumedRoleUser) {
            return this.assumedRoleUser((AssumedRoleUser)((AssumedRoleUser.Builder)AssumedRoleUser.builder().applyMutation(assumedRoleUser)).build());
        }

        public Builder packedPolicySize(Integer var1);

        public Builder provider(String var1);

        public Builder audience(String var1);

        public Builder sourceIdentity(String var1);
    }
}

