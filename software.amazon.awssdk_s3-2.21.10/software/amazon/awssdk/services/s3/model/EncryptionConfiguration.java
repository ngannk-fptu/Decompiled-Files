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
package software.amazon.awssdk.services.s3.model;

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

public final class EncryptionConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, EncryptionConfiguration> {
    private static final SdkField<String> REPLICA_KMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ReplicaKmsKeyID").getter(EncryptionConfiguration.getter(EncryptionConfiguration::replicaKmsKeyID)).setter(EncryptionConfiguration.setter(Builder::replicaKmsKeyID)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplicaKmsKeyID").unmarshallLocationName("ReplicaKmsKeyID").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(REPLICA_KMS_KEY_ID_FIELD));
    private static final long serialVersionUID = 1L;
    private final String replicaKmsKeyID;

    private EncryptionConfiguration(BuilderImpl builder) {
        this.replicaKmsKeyID = builder.replicaKmsKeyID;
    }

    public final String replicaKmsKeyID() {
        return this.replicaKmsKeyID;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.replicaKmsKeyID());
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
        if (!(obj instanceof EncryptionConfiguration)) {
            return false;
        }
        EncryptionConfiguration other = (EncryptionConfiguration)obj;
        return Objects.equals(this.replicaKmsKeyID(), other.replicaKmsKeyID());
    }

    public final String toString() {
        return ToString.builder((String)"EncryptionConfiguration").add("ReplicaKmsKeyID", (Object)this.replicaKmsKeyID()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ReplicaKmsKeyID": {
                return Optional.ofNullable(clazz.cast(this.replicaKmsKeyID()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<EncryptionConfiguration, T> g) {
        return obj -> g.apply((EncryptionConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String replicaKmsKeyID;

        private BuilderImpl() {
        }

        private BuilderImpl(EncryptionConfiguration model) {
            this.replicaKmsKeyID(model.replicaKmsKeyID);
        }

        public final String getReplicaKmsKeyID() {
            return this.replicaKmsKeyID;
        }

        public final void setReplicaKmsKeyID(String replicaKmsKeyID) {
            this.replicaKmsKeyID = replicaKmsKeyID;
        }

        @Override
        public final Builder replicaKmsKeyID(String replicaKmsKeyID) {
            this.replicaKmsKeyID = replicaKmsKeyID;
            return this;
        }

        public EncryptionConfiguration build() {
            return new EncryptionConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, EncryptionConfiguration> {
        public Builder replicaKmsKeyID(String var1);
    }
}

