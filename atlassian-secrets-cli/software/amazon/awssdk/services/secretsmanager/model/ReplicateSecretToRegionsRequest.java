/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.AddReplicaRegionListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.ReplicaRegionType;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ReplicateSecretToRegionsRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, ReplicateSecretToRegionsRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretId").getter(ReplicateSecretToRegionsRequest.getter(ReplicateSecretToRegionsRequest::secretId)).setter(ReplicateSecretToRegionsRequest.setter(Builder::secretId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()).build();
    private static final SdkField<List<ReplicaRegionType>> ADD_REPLICA_REGIONS_FIELD = SdkField.builder(MarshallingType.LIST).memberName("AddReplicaRegions").getter(ReplicateSecretToRegionsRequest.getter(ReplicateSecretToRegionsRequest::addReplicaRegions)).setter(ReplicateSecretToRegionsRequest.setter(Builder::addReplicaRegions)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AddReplicaRegions").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder(MarshallingType.SDK_POJO).constructor(ReplicaRegionType::builder).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()).build()).build()).build();
    private static final SdkField<Boolean> FORCE_OVERWRITE_REPLICA_SECRET_FIELD = SdkField.builder(MarshallingType.BOOLEAN).memberName("ForceOverwriteReplicaSecret").getter(ReplicateSecretToRegionsRequest.getter(ReplicateSecretToRegionsRequest::forceOverwriteReplicaSecret)).setter(ReplicateSecretToRegionsRequest.setter(Builder::forceOverwriteReplicaSecret)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ForceOverwriteReplicaSecret").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, ADD_REPLICA_REGIONS_FIELD, FORCE_OVERWRITE_REPLICA_SECRET_FIELD));
    private final String secretId;
    private final List<ReplicaRegionType> addReplicaRegions;
    private final Boolean forceOverwriteReplicaSecret;

    private ReplicateSecretToRegionsRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.addReplicaRegions = builder.addReplicaRegions;
        this.forceOverwriteReplicaSecret = builder.forceOverwriteReplicaSecret;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final boolean hasAddReplicaRegions() {
        return this.addReplicaRegions != null && !(this.addReplicaRegions instanceof SdkAutoConstructList);
    }

    public final List<ReplicaRegionType> addReplicaRegions() {
        return this.addReplicaRegions;
    }

    public final Boolean forceOverwriteReplicaSecret() {
        return this.forceOverwriteReplicaSecret;
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    @Override
    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.secretId());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasAddReplicaRegions() ? this.addReplicaRegions() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.forceOverwriteReplicaSecret());
        return hashCode;
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    @Override
    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ReplicateSecretToRegionsRequest)) {
            return false;
        }
        ReplicateSecretToRegionsRequest other = (ReplicateSecretToRegionsRequest)obj;
        return Objects.equals(this.secretId(), other.secretId()) && this.hasAddReplicaRegions() == other.hasAddReplicaRegions() && Objects.equals(this.addReplicaRegions(), other.addReplicaRegions()) && Objects.equals(this.forceOverwriteReplicaSecret(), other.forceOverwriteReplicaSecret());
    }

    public final String toString() {
        return ToString.builder("ReplicateSecretToRegionsRequest").add("SecretId", this.secretId()).add("AddReplicaRegions", this.hasAddReplicaRegions() ? this.addReplicaRegions() : null).add("ForceOverwriteReplicaSecret", this.forceOverwriteReplicaSecret()).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "AddReplicaRegions": {
                return Optional.ofNullable(clazz.cast(this.addReplicaRegions()));
            }
            case "ForceOverwriteReplicaSecret": {
                return Optional.ofNullable(clazz.cast(this.forceOverwriteReplicaSecret()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ReplicateSecretToRegionsRequest, T> g) {
        return obj -> g.apply((ReplicateSecretToRegionsRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private List<ReplicaRegionType> addReplicaRegions = DefaultSdkAutoConstructList.getInstance();
        private Boolean forceOverwriteReplicaSecret;

        private BuilderImpl() {
        }

        private BuilderImpl(ReplicateSecretToRegionsRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.addReplicaRegions(model.addReplicaRegions);
            this.forceOverwriteReplicaSecret(model.forceOverwriteReplicaSecret);
        }

        public final String getSecretId() {
            return this.secretId;
        }

        public final void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        @Override
        public final Builder secretId(String secretId) {
            this.secretId = secretId;
            return this;
        }

        public final List<ReplicaRegionType.Builder> getAddReplicaRegions() {
            List<ReplicaRegionType.Builder> result = AddReplicaRegionListTypeCopier.copyToBuilder(this.addReplicaRegions);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setAddReplicaRegions(Collection<ReplicaRegionType.BuilderImpl> addReplicaRegions) {
            this.addReplicaRegions = AddReplicaRegionListTypeCopier.copyFromBuilder(addReplicaRegions);
        }

        @Override
        public final Builder addReplicaRegions(Collection<ReplicaRegionType> addReplicaRegions) {
            this.addReplicaRegions = AddReplicaRegionListTypeCopier.copy(addReplicaRegions);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder addReplicaRegions(ReplicaRegionType ... addReplicaRegions) {
            this.addReplicaRegions(Arrays.asList(addReplicaRegions));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder addReplicaRegions(Consumer<ReplicaRegionType.Builder> ... addReplicaRegions) {
            this.addReplicaRegions(Stream.of(addReplicaRegions).map(c -> (ReplicaRegionType)((ReplicaRegionType.Builder)ReplicaRegionType.builder().applyMutation(c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final Boolean getForceOverwriteReplicaSecret() {
            return this.forceOverwriteReplicaSecret;
        }

        public final void setForceOverwriteReplicaSecret(Boolean forceOverwriteReplicaSecret) {
            this.forceOverwriteReplicaSecret = forceOverwriteReplicaSecret;
        }

        @Override
        public final Builder forceOverwriteReplicaSecret(Boolean forceOverwriteReplicaSecret) {
            this.forceOverwriteReplicaSecret = forceOverwriteReplicaSecret;
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public ReplicateSecretToRegionsRequest build() {
            return new ReplicateSecretToRegionsRequest(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ReplicateSecretToRegionsRequest> {
        public Builder secretId(String var1);

        public Builder addReplicaRegions(Collection<ReplicaRegionType> var1);

        public Builder addReplicaRegions(ReplicaRegionType ... var1);

        public Builder addReplicaRegions(Consumer<ReplicaRegionType.Builder> ... var1);

        public Builder forceOverwriteReplicaSecret(Boolean var1);

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

