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

public final class AssumeRoleResponse
extends StsResponse
implements ToCopyableBuilder<Builder, AssumeRoleResponse> {
    private static final SdkField<Credentials> CREDENTIALS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Credentials").getter(AssumeRoleResponse.getter(AssumeRoleResponse::credentials)).setter(AssumeRoleResponse.setter(Builder::credentials)).constructor(Credentials::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Credentials").build()}).build();
    private static final SdkField<AssumedRoleUser> ASSUMED_ROLE_USER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("AssumedRoleUser").getter(AssumeRoleResponse.getter(AssumeRoleResponse::assumedRoleUser)).setter(AssumeRoleResponse.setter(Builder::assumedRoleUser)).constructor(AssumedRoleUser::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AssumedRoleUser").build()}).build();
    private static final SdkField<Integer> PACKED_POLICY_SIZE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PackedPolicySize").getter(AssumeRoleResponse.getter(AssumeRoleResponse::packedPolicySize)).setter(AssumeRoleResponse.setter(Builder::packedPolicySize)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PackedPolicySize").build()}).build();
    private static final SdkField<String> SOURCE_IDENTITY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceIdentity").getter(AssumeRoleResponse.getter(AssumeRoleResponse::sourceIdentity)).setter(AssumeRoleResponse.setter(Builder::sourceIdentity)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceIdentity").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CREDENTIALS_FIELD, ASSUMED_ROLE_USER_FIELD, PACKED_POLICY_SIZE_FIELD, SOURCE_IDENTITY_FIELD));
    private final Credentials credentials;
    private final AssumedRoleUser assumedRoleUser;
    private final Integer packedPolicySize;
    private final String sourceIdentity;

    private AssumeRoleResponse(BuilderImpl builder) {
        super(builder);
        this.credentials = builder.credentials;
        this.assumedRoleUser = builder.assumedRoleUser;
        this.packedPolicySize = builder.packedPolicySize;
        this.sourceIdentity = builder.sourceIdentity;
    }

    public final Credentials credentials() {
        return this.credentials;
    }

    public final AssumedRoleUser assumedRoleUser() {
        return this.assumedRoleUser;
    }

    public final Integer packedPolicySize() {
        return this.packedPolicySize;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.assumedRoleUser());
        hashCode = 31 * hashCode + Objects.hashCode(this.packedPolicySize());
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
        if (!(obj instanceof AssumeRoleResponse)) {
            return false;
        }
        AssumeRoleResponse other = (AssumeRoleResponse)((Object)obj);
        return Objects.equals(this.credentials(), other.credentials()) && Objects.equals(this.assumedRoleUser(), other.assumedRoleUser()) && Objects.equals(this.packedPolicySize(), other.packedPolicySize()) && Objects.equals(this.sourceIdentity(), other.sourceIdentity());
    }

    public final String toString() {
        return ToString.builder((String)"AssumeRoleResponse").add("Credentials", (Object)this.credentials()).add("AssumedRoleUser", (Object)this.assumedRoleUser()).add("PackedPolicySize", (Object)this.packedPolicySize()).add("SourceIdentity", (Object)this.sourceIdentity()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Credentials": {
                return Optional.ofNullable(clazz.cast(this.credentials()));
            }
            case "AssumedRoleUser": {
                return Optional.ofNullable(clazz.cast(this.assumedRoleUser()));
            }
            case "PackedPolicySize": {
                return Optional.ofNullable(clazz.cast(this.packedPolicySize()));
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

    private static <T> Function<Object, T> getter(Function<AssumeRoleResponse, T> g) {
        return obj -> g.apply((AssumeRoleResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsResponse.BuilderImpl
    implements Builder {
        private Credentials credentials;
        private AssumedRoleUser assumedRoleUser;
        private Integer packedPolicySize;
        private String sourceIdentity;

        private BuilderImpl() {
        }

        private BuilderImpl(AssumeRoleResponse model) {
            super(model);
            this.credentials(model.credentials);
            this.assumedRoleUser(model.assumedRoleUser);
            this.packedPolicySize(model.packedPolicySize);
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
        public AssumeRoleResponse build() {
            return new AssumeRoleResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, AssumeRoleResponse> {
        public Builder credentials(Credentials var1);

        default public Builder credentials(Consumer<Credentials.Builder> credentials) {
            return this.credentials((Credentials)((Credentials.Builder)Credentials.builder().applyMutation(credentials)).build());
        }

        public Builder assumedRoleUser(AssumedRoleUser var1);

        default public Builder assumedRoleUser(Consumer<AssumedRoleUser.Builder> assumedRoleUser) {
            return this.assumedRoleUser((AssumedRoleUser)((AssumedRoleUser.Builder)AssumedRoleUser.builder().applyMutation(assumedRoleUser)).build());
        }

        public Builder packedPolicySize(Integer var1);

        public Builder sourceIdentity(String var1);
    }
}

