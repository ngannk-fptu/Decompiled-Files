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
package software.amazon.awssdk.services.secretsmanager.model;

import java.io.Serializable;
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

public final class ReplicaRegionType
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ReplicaRegionType> {
    private static final SdkField<String> REGION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Region").getter(ReplicaRegionType.getter(ReplicaRegionType::region)).setter(ReplicaRegionType.setter(Builder::region)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Region").build()}).build();
    private static final SdkField<String> KMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KmsKeyId").getter(ReplicaRegionType.getter(ReplicaRegionType::kmsKeyId)).setter(ReplicaRegionType.setter(Builder::kmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KmsKeyId").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(REGION_FIELD, KMS_KEY_ID_FIELD));
    private static final long serialVersionUID = 1L;
    private final String region;
    private final String kmsKeyId;

    private ReplicaRegionType(BuilderImpl builder) {
        this.region = builder.region;
        this.kmsKeyId = builder.kmsKeyId;
    }

    public final String region() {
        return this.region;
    }

    public final String kmsKeyId() {
        return this.kmsKeyId;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.region());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsKeyId());
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
        if (!(obj instanceof ReplicaRegionType)) {
            return false;
        }
        ReplicaRegionType other = (ReplicaRegionType)obj;
        return Objects.equals(this.region(), other.region()) && Objects.equals(this.kmsKeyId(), other.kmsKeyId());
    }

    public final String toString() {
        return ToString.builder((String)"ReplicaRegionType").add("Region", (Object)this.region()).add("KmsKeyId", (Object)this.kmsKeyId()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Region": {
                return Optional.ofNullable(clazz.cast(this.region()));
            }
            case "KmsKeyId": {
                return Optional.ofNullable(clazz.cast(this.kmsKeyId()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ReplicaRegionType, T> g) {
        return obj -> g.apply((ReplicaRegionType)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String region;
        private String kmsKeyId;

        private BuilderImpl() {
        }

        private BuilderImpl(ReplicaRegionType model) {
            this.region(model.region);
            this.kmsKeyId(model.kmsKeyId);
        }

        public final String getRegion() {
            return this.region;
        }

        public final void setRegion(String region) {
            this.region = region;
        }

        @Override
        public final Builder region(String region) {
            this.region = region;
            return this;
        }

        public final String getKmsKeyId() {
            return this.kmsKeyId;
        }

        public final void setKmsKeyId(String kmsKeyId) {
            this.kmsKeyId = kmsKeyId;
        }

        @Override
        public final Builder kmsKeyId(String kmsKeyId) {
            this.kmsKeyId = kmsKeyId;
            return this;
        }

        public ReplicaRegionType build() {
            return new ReplicaRegionType(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ReplicaRegionType> {
        public Builder region(String var1);

        public Builder kmsKeyId(String var1);
    }
}

