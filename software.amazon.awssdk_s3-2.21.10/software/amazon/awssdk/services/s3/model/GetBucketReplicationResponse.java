/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

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
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ReplicationConfiguration;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketReplicationResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketReplicationResponse> {
    private static final SdkField<ReplicationConfiguration> REPLICATION_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ReplicationConfiguration").getter(GetBucketReplicationResponse.getter(GetBucketReplicationResponse::replicationConfiguration)).setter(GetBucketReplicationResponse.setter(Builder::replicationConfiguration)).constructor(ReplicationConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplicationConfiguration").unmarshallLocationName("ReplicationConfiguration").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(REPLICATION_CONFIGURATION_FIELD));
    private final ReplicationConfiguration replicationConfiguration;

    private GetBucketReplicationResponse(BuilderImpl builder) {
        super(builder);
        this.replicationConfiguration = builder.replicationConfiguration;
    }

    public final ReplicationConfiguration replicationConfiguration() {
        return this.replicationConfiguration;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.replicationConfiguration());
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
        if (!(obj instanceof GetBucketReplicationResponse)) {
            return false;
        }
        GetBucketReplicationResponse other = (GetBucketReplicationResponse)((Object)obj);
        return Objects.equals(this.replicationConfiguration(), other.replicationConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketReplicationResponse").add("ReplicationConfiguration", (Object)this.replicationConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ReplicationConfiguration": {
                return Optional.ofNullable(clazz.cast(this.replicationConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketReplicationResponse, T> g) {
        return obj -> g.apply((GetBucketReplicationResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private ReplicationConfiguration replicationConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketReplicationResponse model) {
            super(model);
            this.replicationConfiguration(model.replicationConfiguration);
        }

        public final ReplicationConfiguration.Builder getReplicationConfiguration() {
            return this.replicationConfiguration != null ? this.replicationConfiguration.toBuilder() : null;
        }

        public final void setReplicationConfiguration(ReplicationConfiguration.BuilderImpl replicationConfiguration) {
            this.replicationConfiguration = replicationConfiguration != null ? replicationConfiguration.build() : null;
        }

        @Override
        public final Builder replicationConfiguration(ReplicationConfiguration replicationConfiguration) {
            this.replicationConfiguration = replicationConfiguration;
            return this;
        }

        @Override
        public GetBucketReplicationResponse build() {
            return new GetBucketReplicationResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketReplicationResponse> {
        public Builder replicationConfiguration(ReplicationConfiguration var1);

        default public Builder replicationConfiguration(Consumer<ReplicationConfiguration.Builder> replicationConfiguration) {
            return this.replicationConfiguration((ReplicationConfiguration)((ReplicationConfiguration.Builder)ReplicationConfiguration.builder().applyMutation(replicationConfiguration)).build());
        }
    }
}

