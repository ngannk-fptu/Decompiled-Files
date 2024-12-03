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
import java.time.Instant;
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
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.Initiator;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class MultipartUpload
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, MultipartUpload> {
    private static final SdkField<String> UPLOAD_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("UploadId").getter(MultipartUpload.getter(MultipartUpload::uploadId)).setter(MultipartUpload.setter(Builder::uploadId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("UploadId").unmarshallLocationName("UploadId").build()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(MultipartUpload.getter(MultipartUpload::key)).setter(MultipartUpload.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<Instant> INITIATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("Initiated").getter(MultipartUpload.getter(MultipartUpload::initiated)).setter(MultipartUpload.setter(Builder::initiated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Initiated").unmarshallLocationName("Initiated").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(MultipartUpload.getter(MultipartUpload::storageClassAsString)).setter(MultipartUpload.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final SdkField<Owner> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Owner").getter(MultipartUpload.getter(MultipartUpload::owner)).setter(MultipartUpload.setter(Builder::owner)).constructor(Owner::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build()}).build();
    private static final SdkField<Initiator> INITIATOR_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Initiator").getter(MultipartUpload.getter(MultipartUpload::initiator)).setter(MultipartUpload.setter(Builder::initiator)).constructor(Initiator::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Initiator").unmarshallLocationName("Initiator").build()}).build();
    private static final SdkField<String> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumAlgorithm").getter(MultipartUpload.getter(MultipartUpload::checksumAlgorithmAsString)).setter(MultipartUpload.setter(Builder::checksumAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumAlgorithm").unmarshallLocationName("ChecksumAlgorithm").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(UPLOAD_ID_FIELD, KEY_FIELD, INITIATED_FIELD, STORAGE_CLASS_FIELD, OWNER_FIELD, INITIATOR_FIELD, CHECKSUM_ALGORITHM_FIELD));
    private static final long serialVersionUID = 1L;
    private final String uploadId;
    private final String key;
    private final Instant initiated;
    private final String storageClass;
    private final Owner owner;
    private final Initiator initiator;
    private final String checksumAlgorithm;

    private MultipartUpload(BuilderImpl builder) {
        this.uploadId = builder.uploadId;
        this.key = builder.key;
        this.initiated = builder.initiated;
        this.storageClass = builder.storageClass;
        this.owner = builder.owner;
        this.initiator = builder.initiator;
        this.checksumAlgorithm = builder.checksumAlgorithm;
    }

    public final String uploadId() {
        return this.uploadId;
    }

    public final String key() {
        return this.key;
    }

    public final Instant initiated() {
        return this.initiated;
    }

    public final StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
    }

    public final Owner owner() {
        return this.owner;
    }

    public final Initiator initiator() {
        return this.initiator;
    }

    public final ChecksumAlgorithm checksumAlgorithm() {
        return ChecksumAlgorithm.fromValue(this.checksumAlgorithm);
    }

    public final String checksumAlgorithmAsString() {
        return this.checksumAlgorithm;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.uploadId());
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.initiated());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.owner());
        hashCode = 31 * hashCode + Objects.hashCode(this.initiator());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumAlgorithmAsString());
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
        if (!(obj instanceof MultipartUpload)) {
            return false;
        }
        MultipartUpload other = (MultipartUpload)obj;
        return Objects.equals(this.uploadId(), other.uploadId()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.initiated(), other.initiated()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.owner(), other.owner()) && Objects.equals(this.initiator(), other.initiator()) && Objects.equals(this.checksumAlgorithmAsString(), other.checksumAlgorithmAsString());
    }

    public final String toString() {
        return ToString.builder((String)"MultipartUpload").add("UploadId", (Object)this.uploadId()).add("Key", (Object)this.key()).add("Initiated", (Object)this.initiated()).add("StorageClass", (Object)this.storageClassAsString()).add("Owner", (Object)this.owner()).add("Initiator", (Object)this.initiator()).add("ChecksumAlgorithm", (Object)this.checksumAlgorithmAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "UploadId": {
                return Optional.ofNullable(clazz.cast(this.uploadId()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "Initiated": {
                return Optional.ofNullable(clazz.cast(this.initiated()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
            case "Owner": {
                return Optional.ofNullable(clazz.cast(this.owner()));
            }
            case "Initiator": {
                return Optional.ofNullable(clazz.cast(this.initiator()));
            }
            case "ChecksumAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.checksumAlgorithmAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<MultipartUpload, T> g) {
        return obj -> g.apply((MultipartUpload)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String uploadId;
        private String key;
        private Instant initiated;
        private String storageClass;
        private Owner owner;
        private Initiator initiator;
        private String checksumAlgorithm;

        private BuilderImpl() {
        }

        private BuilderImpl(MultipartUpload model) {
            this.uploadId(model.uploadId);
            this.key(model.key);
            this.initiated(model.initiated);
            this.storageClass(model.storageClass);
            this.owner(model.owner);
            this.initiator(model.initiator);
            this.checksumAlgorithm(model.checksumAlgorithm);
        }

        public final String getUploadId() {
            return this.uploadId;
        }

        public final void setUploadId(String uploadId) {
            this.uploadId = uploadId;
        }

        @Override
        public final Builder uploadId(String uploadId) {
            this.uploadId = uploadId;
            return this;
        }

        public final String getKey() {
            return this.key;
        }

        public final void setKey(String key) {
            this.key = key;
        }

        @Override
        public final Builder key(String key) {
            this.key = key;
            return this;
        }

        public final Instant getInitiated() {
            return this.initiated;
        }

        public final void setInitiated(Instant initiated) {
            this.initiated = initiated;
        }

        @Override
        public final Builder initiated(Instant initiated) {
            this.initiated = initiated;
            return this;
        }

        public final String getStorageClass() {
            return this.storageClass;
        }

        public final void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        @Override
        public final Builder storageClass(String storageClass) {
            this.storageClass = storageClass;
            return this;
        }

        @Override
        public final Builder storageClass(StorageClass storageClass) {
            this.storageClass(storageClass == null ? null : storageClass.toString());
            return this;
        }

        public final Owner.Builder getOwner() {
            return this.owner != null ? this.owner.toBuilder() : null;
        }

        public final void setOwner(Owner.BuilderImpl owner) {
            this.owner = owner != null ? owner.build() : null;
        }

        @Override
        public final Builder owner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public final Initiator.Builder getInitiator() {
            return this.initiator != null ? this.initiator.toBuilder() : null;
        }

        public final void setInitiator(Initiator.BuilderImpl initiator) {
            this.initiator = initiator != null ? initiator.build() : null;
        }

        @Override
        public final Builder initiator(Initiator initiator) {
            this.initiator = initiator;
            return this;
        }

        public final String getChecksumAlgorithm() {
            return this.checksumAlgorithm;
        }

        public final void setChecksumAlgorithm(String checksumAlgorithm) {
            this.checksumAlgorithm = checksumAlgorithm;
        }

        @Override
        public final Builder checksumAlgorithm(String checksumAlgorithm) {
            this.checksumAlgorithm = checksumAlgorithm;
            return this;
        }

        @Override
        public final Builder checksumAlgorithm(ChecksumAlgorithm checksumAlgorithm) {
            this.checksumAlgorithm(checksumAlgorithm == null ? null : checksumAlgorithm.toString());
            return this;
        }

        public MultipartUpload build() {
            return new MultipartUpload(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, MultipartUpload> {
        public Builder uploadId(String var1);

        public Builder key(String var1);

        public Builder initiated(Instant var1);

        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);

        public Builder owner(Owner var1);

        default public Builder owner(Consumer<Owner.Builder> owner) {
            return this.owner((Owner)((Owner.Builder)Owner.builder().applyMutation(owner)).build());
        }

        public Builder initiator(Initiator var1);

        default public Builder initiator(Consumer<Initiator.Builder> initiator) {
            return this.initiator((Initiator)((Initiator.Builder)Initiator.builder().applyMutation(initiator)).build());
        }

        public Builder checksumAlgorithm(String var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm var1);
    }
}

