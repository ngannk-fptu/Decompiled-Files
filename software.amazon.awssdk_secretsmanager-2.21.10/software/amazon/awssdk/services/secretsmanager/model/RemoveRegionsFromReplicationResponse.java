/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
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
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.ReplicationStatusListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.ReplicationStatusType;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RemoveRegionsFromReplicationResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, RemoveRegionsFromReplicationResponse> {
    private static final SdkField<String> ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ARN").getter(RemoveRegionsFromReplicationResponse.getter(RemoveRegionsFromReplicationResponse::arn)).setter(RemoveRegionsFromReplicationResponse.setter(Builder::arn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ARN").build()}).build();
    private static final SdkField<List<ReplicationStatusType>> REPLICATION_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("ReplicationStatus").getter(RemoveRegionsFromReplicationResponse.getter(RemoveRegionsFromReplicationResponse::replicationStatus)).setter(RemoveRegionsFromReplicationResponse.setter(Builder::replicationStatus)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplicationStatus").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ReplicationStatusType::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ARN_FIELD, REPLICATION_STATUS_FIELD));
    private final String arn;
    private final List<ReplicationStatusType> replicationStatus;

    private RemoveRegionsFromReplicationResponse(BuilderImpl builder) {
        super(builder);
        this.arn = builder.arn;
        this.replicationStatus = builder.replicationStatus;
    }

    public final String arn() {
        return this.arn;
    }

    public final boolean hasReplicationStatus() {
        return this.replicationStatus != null && !(this.replicationStatus instanceof SdkAutoConstructList);
    }

    public final List<ReplicationStatusType> replicationStatus() {
        return this.replicationStatus;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasReplicationStatus() ? this.replicationStatus() : null);
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
        if (!(obj instanceof RemoveRegionsFromReplicationResponse)) {
            return false;
        }
        RemoveRegionsFromReplicationResponse other = (RemoveRegionsFromReplicationResponse)((Object)obj);
        return Objects.equals(this.arn(), other.arn()) && this.hasReplicationStatus() == other.hasReplicationStatus() && Objects.equals(this.replicationStatus(), other.replicationStatus());
    }

    public final String toString() {
        return ToString.builder((String)"RemoveRegionsFromReplicationResponse").add("ARN", (Object)this.arn()).add("ReplicationStatus", this.hasReplicationStatus() ? this.replicationStatus() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ARN": {
                return Optional.ofNullable(clazz.cast(this.arn()));
            }
            case "ReplicationStatus": {
                return Optional.ofNullable(clazz.cast(this.replicationStatus()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RemoveRegionsFromReplicationResponse, T> g) {
        return obj -> g.apply((RemoveRegionsFromReplicationResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private String arn;
        private List<ReplicationStatusType> replicationStatus = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(RemoveRegionsFromReplicationResponse model) {
            super(model);
            this.arn(model.arn);
            this.replicationStatus(model.replicationStatus);
        }

        public final String getArn() {
            return this.arn;
        }

        public final void setArn(String arn) {
            this.arn = arn;
        }

        @Override
        public final Builder arn(String arn) {
            this.arn = arn;
            return this;
        }

        public final List<ReplicationStatusType.Builder> getReplicationStatus() {
            List<ReplicationStatusType.Builder> result = ReplicationStatusListTypeCopier.copyToBuilder(this.replicationStatus);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setReplicationStatus(Collection<ReplicationStatusType.BuilderImpl> replicationStatus) {
            this.replicationStatus = ReplicationStatusListTypeCopier.copyFromBuilder(replicationStatus);
        }

        @Override
        public final Builder replicationStatus(Collection<ReplicationStatusType> replicationStatus) {
            this.replicationStatus = ReplicationStatusListTypeCopier.copy(replicationStatus);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder replicationStatus(ReplicationStatusType ... replicationStatus) {
            this.replicationStatus(Arrays.asList(replicationStatus));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder replicationStatus(Consumer<ReplicationStatusType.Builder> ... replicationStatus) {
            this.replicationStatus(Stream.of(replicationStatus).map(c -> (ReplicationStatusType)((ReplicationStatusType.Builder)ReplicationStatusType.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public RemoveRegionsFromReplicationResponse build() {
            return new RemoveRegionsFromReplicationResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, RemoveRegionsFromReplicationResponse> {
        public Builder arn(String var1);

        public Builder replicationStatus(Collection<ReplicationStatusType> var1);

        public Builder replicationStatus(ReplicationStatusType ... var1);

        public Builder replicationStatus(Consumer<ReplicationStatusType.Builder> ... var1);
    }
}

