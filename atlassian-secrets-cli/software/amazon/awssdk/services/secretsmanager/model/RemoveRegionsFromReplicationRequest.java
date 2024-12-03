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
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.RemoveReplicaRegionListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RemoveRegionsFromReplicationRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, RemoveRegionsFromReplicationRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretId").getter(RemoveRegionsFromReplicationRequest.getter(RemoveRegionsFromReplicationRequest::secretId)).setter(RemoveRegionsFromReplicationRequest.setter(Builder::secretId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()).build();
    private static final SdkField<List<String>> REMOVE_REPLICA_REGIONS_FIELD = SdkField.builder(MarshallingType.LIST).memberName("RemoveReplicaRegions").getter(RemoveRegionsFromReplicationRequest.getter(RemoveRegionsFromReplicationRequest::removeReplicaRegions)).setter(RemoveRegionsFromReplicationRequest.setter(Builder::removeReplicaRegions)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RemoveReplicaRegions").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder(MarshallingType.STRING).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()).build()).build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, REMOVE_REPLICA_REGIONS_FIELD));
    private final String secretId;
    private final List<String> removeReplicaRegions;

    private RemoveRegionsFromReplicationRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.removeReplicaRegions = builder.removeReplicaRegions;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final boolean hasRemoveReplicaRegions() {
        return this.removeReplicaRegions != null && !(this.removeReplicaRegions instanceof SdkAutoConstructList);
    }

    public final List<String> removeReplicaRegions() {
        return this.removeReplicaRegions;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasRemoveReplicaRegions() ? this.removeReplicaRegions() : null);
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
        if (!(obj instanceof RemoveRegionsFromReplicationRequest)) {
            return false;
        }
        RemoveRegionsFromReplicationRequest other = (RemoveRegionsFromReplicationRequest)obj;
        return Objects.equals(this.secretId(), other.secretId()) && this.hasRemoveReplicaRegions() == other.hasRemoveReplicaRegions() && Objects.equals(this.removeReplicaRegions(), other.removeReplicaRegions());
    }

    public final String toString() {
        return ToString.builder("RemoveRegionsFromReplicationRequest").add("SecretId", this.secretId()).add("RemoveReplicaRegions", this.hasRemoveReplicaRegions() ? this.removeReplicaRegions() : null).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "RemoveReplicaRegions": {
                return Optional.ofNullable(clazz.cast(this.removeReplicaRegions()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RemoveRegionsFromReplicationRequest, T> g) {
        return obj -> g.apply((RemoveRegionsFromReplicationRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private List<String> removeReplicaRegions = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(RemoveRegionsFromReplicationRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.removeReplicaRegions(model.removeReplicaRegions);
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

        public final Collection<String> getRemoveReplicaRegions() {
            if (this.removeReplicaRegions instanceof SdkAutoConstructList) {
                return null;
            }
            return this.removeReplicaRegions;
        }

        public final void setRemoveReplicaRegions(Collection<String> removeReplicaRegions) {
            this.removeReplicaRegions = RemoveReplicaRegionListTypeCopier.copy(removeReplicaRegions);
        }

        @Override
        public final Builder removeReplicaRegions(Collection<String> removeReplicaRegions) {
            this.removeReplicaRegions = RemoveReplicaRegionListTypeCopier.copy(removeReplicaRegions);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder removeReplicaRegions(String ... removeReplicaRegions) {
            this.removeReplicaRegions(Arrays.asList(removeReplicaRegions));
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
        public RemoveRegionsFromReplicationRequest build() {
            return new RemoveRegionsFromReplicationRequest(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, RemoveRegionsFromReplicationRequest> {
        public Builder secretId(String var1);

        public Builder removeReplicaRegions(Collection<String> var1);

        public Builder removeReplicaRegions(String ... var1);

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

