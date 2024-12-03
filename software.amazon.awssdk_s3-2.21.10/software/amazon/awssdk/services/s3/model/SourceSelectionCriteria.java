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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ReplicaModifications;
import software.amazon.awssdk.services.s3.model.SseKmsEncryptedObjects;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class SourceSelectionCriteria
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, SourceSelectionCriteria> {
    private static final SdkField<SseKmsEncryptedObjects> SSE_KMS_ENCRYPTED_OBJECTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("SseKmsEncryptedObjects").getter(SourceSelectionCriteria.getter(SourceSelectionCriteria::sseKmsEncryptedObjects)).setter(SourceSelectionCriteria.setter(Builder::sseKmsEncryptedObjects)).constructor(SseKmsEncryptedObjects::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SseKmsEncryptedObjects").unmarshallLocationName("SseKmsEncryptedObjects").build()}).build();
    private static final SdkField<ReplicaModifications> REPLICA_MODIFICATIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ReplicaModifications").getter(SourceSelectionCriteria.getter(SourceSelectionCriteria::replicaModifications)).setter(SourceSelectionCriteria.setter(Builder::replicaModifications)).constructor(ReplicaModifications::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplicaModifications").unmarshallLocationName("ReplicaModifications").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SSE_KMS_ENCRYPTED_OBJECTS_FIELD, REPLICA_MODIFICATIONS_FIELD));
    private static final long serialVersionUID = 1L;
    private final SseKmsEncryptedObjects sseKmsEncryptedObjects;
    private final ReplicaModifications replicaModifications;

    private SourceSelectionCriteria(BuilderImpl builder) {
        this.sseKmsEncryptedObjects = builder.sseKmsEncryptedObjects;
        this.replicaModifications = builder.replicaModifications;
    }

    public final SseKmsEncryptedObjects sseKmsEncryptedObjects() {
        return this.sseKmsEncryptedObjects;
    }

    public final ReplicaModifications replicaModifications() {
        return this.replicaModifications;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.sseKmsEncryptedObjects());
        hashCode = 31 * hashCode + Objects.hashCode(this.replicaModifications());
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
        if (!(obj instanceof SourceSelectionCriteria)) {
            return false;
        }
        SourceSelectionCriteria other = (SourceSelectionCriteria)obj;
        return Objects.equals(this.sseKmsEncryptedObjects(), other.sseKmsEncryptedObjects()) && Objects.equals(this.replicaModifications(), other.replicaModifications());
    }

    public final String toString() {
        return ToString.builder((String)"SourceSelectionCriteria").add("SseKmsEncryptedObjects", (Object)this.sseKmsEncryptedObjects()).add("ReplicaModifications", (Object)this.replicaModifications()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SseKmsEncryptedObjects": {
                return Optional.ofNullable(clazz.cast(this.sseKmsEncryptedObjects()));
            }
            case "ReplicaModifications": {
                return Optional.ofNullable(clazz.cast(this.replicaModifications()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<SourceSelectionCriteria, T> g) {
        return obj -> g.apply((SourceSelectionCriteria)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private SseKmsEncryptedObjects sseKmsEncryptedObjects;
        private ReplicaModifications replicaModifications;

        private BuilderImpl() {
        }

        private BuilderImpl(SourceSelectionCriteria model) {
            this.sseKmsEncryptedObjects(model.sseKmsEncryptedObjects);
            this.replicaModifications(model.replicaModifications);
        }

        public final SseKmsEncryptedObjects.Builder getSseKmsEncryptedObjects() {
            return this.sseKmsEncryptedObjects != null ? this.sseKmsEncryptedObjects.toBuilder() : null;
        }

        public final void setSseKmsEncryptedObjects(SseKmsEncryptedObjects.BuilderImpl sseKmsEncryptedObjects) {
            this.sseKmsEncryptedObjects = sseKmsEncryptedObjects != null ? sseKmsEncryptedObjects.build() : null;
        }

        @Override
        public final Builder sseKmsEncryptedObjects(SseKmsEncryptedObjects sseKmsEncryptedObjects) {
            this.sseKmsEncryptedObjects = sseKmsEncryptedObjects;
            return this;
        }

        public final ReplicaModifications.Builder getReplicaModifications() {
            return this.replicaModifications != null ? this.replicaModifications.toBuilder() : null;
        }

        public final void setReplicaModifications(ReplicaModifications.BuilderImpl replicaModifications) {
            this.replicaModifications = replicaModifications != null ? replicaModifications.build() : null;
        }

        @Override
        public final Builder replicaModifications(ReplicaModifications replicaModifications) {
            this.replicaModifications = replicaModifications;
            return this;
        }

        public SourceSelectionCriteria build() {
            return new SourceSelectionCriteria(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, SourceSelectionCriteria> {
        public Builder sseKmsEncryptedObjects(SseKmsEncryptedObjects var1);

        default public Builder sseKmsEncryptedObjects(Consumer<SseKmsEncryptedObjects.Builder> sseKmsEncryptedObjects) {
            return this.sseKmsEncryptedObjects((SseKmsEncryptedObjects)((SseKmsEncryptedObjects.Builder)SseKmsEncryptedObjects.builder().applyMutation(sseKmsEncryptedObjects)).build());
        }

        public Builder replicaModifications(ReplicaModifications var1);

        default public Builder replicaModifications(Consumer<ReplicaModifications.Builder> replicaModifications) {
            return this.replicaModifications((ReplicaModifications)((ReplicaModifications.Builder)ReplicaModifications.builder().applyMutation(replicaModifications)).build());
        }
    }
}

