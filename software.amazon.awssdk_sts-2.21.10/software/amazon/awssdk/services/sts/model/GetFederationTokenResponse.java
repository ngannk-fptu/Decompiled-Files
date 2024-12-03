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
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.FederatedUser;
import software.amazon.awssdk.services.sts.model.StsResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetFederationTokenResponse
extends StsResponse
implements ToCopyableBuilder<Builder, GetFederationTokenResponse> {
    private static final SdkField<Credentials> CREDENTIALS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Credentials").getter(GetFederationTokenResponse.getter(GetFederationTokenResponse::credentials)).setter(GetFederationTokenResponse.setter(Builder::credentials)).constructor(Credentials::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Credentials").build()}).build();
    private static final SdkField<FederatedUser> FEDERATED_USER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("FederatedUser").getter(GetFederationTokenResponse.getter(GetFederationTokenResponse::federatedUser)).setter(GetFederationTokenResponse.setter(Builder::federatedUser)).constructor(FederatedUser::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("FederatedUser").build()}).build();
    private static final SdkField<Integer> PACKED_POLICY_SIZE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PackedPolicySize").getter(GetFederationTokenResponse.getter(GetFederationTokenResponse::packedPolicySize)).setter(GetFederationTokenResponse.setter(Builder::packedPolicySize)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PackedPolicySize").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CREDENTIALS_FIELD, FEDERATED_USER_FIELD, PACKED_POLICY_SIZE_FIELD));
    private final Credentials credentials;
    private final FederatedUser federatedUser;
    private final Integer packedPolicySize;

    private GetFederationTokenResponse(BuilderImpl builder) {
        super(builder);
        this.credentials = builder.credentials;
        this.federatedUser = builder.federatedUser;
        this.packedPolicySize = builder.packedPolicySize;
    }

    public final Credentials credentials() {
        return this.credentials;
    }

    public final FederatedUser federatedUser() {
        return this.federatedUser;
    }

    public final Integer packedPolicySize() {
        return this.packedPolicySize;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.federatedUser());
        hashCode = 31 * hashCode + Objects.hashCode(this.packedPolicySize());
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
        if (!(obj instanceof GetFederationTokenResponse)) {
            return false;
        }
        GetFederationTokenResponse other = (GetFederationTokenResponse)((Object)obj);
        return Objects.equals(this.credentials(), other.credentials()) && Objects.equals(this.federatedUser(), other.federatedUser()) && Objects.equals(this.packedPolicySize(), other.packedPolicySize());
    }

    public final String toString() {
        return ToString.builder((String)"GetFederationTokenResponse").add("Credentials", (Object)this.credentials()).add("FederatedUser", (Object)this.federatedUser()).add("PackedPolicySize", (Object)this.packedPolicySize()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Credentials": {
                return Optional.ofNullable(clazz.cast(this.credentials()));
            }
            case "FederatedUser": {
                return Optional.ofNullable(clazz.cast(this.federatedUser()));
            }
            case "PackedPolicySize": {
                return Optional.ofNullable(clazz.cast(this.packedPolicySize()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetFederationTokenResponse, T> g) {
        return obj -> g.apply((GetFederationTokenResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsResponse.BuilderImpl
    implements Builder {
        private Credentials credentials;
        private FederatedUser federatedUser;
        private Integer packedPolicySize;

        private BuilderImpl() {
        }

        private BuilderImpl(GetFederationTokenResponse model) {
            super(model);
            this.credentials(model.credentials);
            this.federatedUser(model.federatedUser);
            this.packedPolicySize(model.packedPolicySize);
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

        public final FederatedUser.Builder getFederatedUser() {
            return this.federatedUser != null ? this.federatedUser.toBuilder() : null;
        }

        public final void setFederatedUser(FederatedUser.BuilderImpl federatedUser) {
            this.federatedUser = federatedUser != null ? federatedUser.build() : null;
        }

        @Override
        public final Builder federatedUser(FederatedUser federatedUser) {
            this.federatedUser = federatedUser;
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

        @Override
        public GetFederationTokenResponse build() {
            return new GetFederationTokenResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetFederationTokenResponse> {
        public Builder credentials(Credentials var1);

        default public Builder credentials(Consumer<Credentials.Builder> credentials) {
            return this.credentials((Credentials)((Credentials.Builder)Credentials.builder().applyMutation(credentials)).build());
        }

        public Builder federatedUser(FederatedUser var1);

        default public Builder federatedUser(Consumer<FederatedUser.Builder> federatedUser) {
            return this.federatedUser((FederatedUser)((FederatedUser.Builder)FederatedUser.builder().applyMutation(federatedUser)).build());
        }

        public Builder packedPolicySize(Integer var1);
    }
}

