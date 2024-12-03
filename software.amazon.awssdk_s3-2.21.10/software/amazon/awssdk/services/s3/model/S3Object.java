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
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
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
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithmListCopier;
import software.amazon.awssdk.services.s3.model.ObjectStorageClass;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.RestoreStatus;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class S3Object
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, S3Object> {
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(S3Object.getter(S3Object::key)).setter(S3Object.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<Instant> LAST_MODIFIED_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("LastModified").getter(S3Object.getter(S3Object::lastModified)).setter(S3Object.setter(Builder::lastModified)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LastModified").unmarshallLocationName("LastModified").build()}).build();
    private static final SdkField<String> E_TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ETag").getter(S3Object.getter(S3Object::eTag)).setter(S3Object.setter(Builder::eTag)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ETag").unmarshallLocationName("ETag").build()}).build();
    private static final SdkField<List<String>> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("ChecksumAlgorithm").getter(S3Object.getter(S3Object::checksumAlgorithmAsStrings)).setter(S3Object.setter(Builder::checksumAlgorithmWithStrings)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumAlgorithm").unmarshallLocationName("ChecksumAlgorithm").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<Long> SIZE_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("Size").getter(S3Object.getter(S3Object::size)).setter(S3Object.setter(Builder::size)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Size").unmarshallLocationName("Size").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(S3Object.getter(S3Object::storageClassAsString)).setter(S3Object.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final SdkField<Owner> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Owner").getter(S3Object.getter(S3Object::owner)).setter(S3Object.setter(Builder::owner)).constructor(Owner::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build()}).build();
    private static final SdkField<RestoreStatus> RESTORE_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("RestoreStatus").getter(S3Object.getter(S3Object::restoreStatus)).setter(S3Object.setter(Builder::restoreStatus)).constructor(RestoreStatus::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RestoreStatus").unmarshallLocationName("RestoreStatus").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(KEY_FIELD, LAST_MODIFIED_FIELD, E_TAG_FIELD, CHECKSUM_ALGORITHM_FIELD, SIZE_FIELD, STORAGE_CLASS_FIELD, OWNER_FIELD, RESTORE_STATUS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String key;
    private final Instant lastModified;
    private final String eTag;
    private final List<String> checksumAlgorithm;
    private final Long size;
    private final String storageClass;
    private final Owner owner;
    private final RestoreStatus restoreStatus;

    private S3Object(BuilderImpl builder) {
        this.key = builder.key;
        this.lastModified = builder.lastModified;
        this.eTag = builder.eTag;
        this.checksumAlgorithm = builder.checksumAlgorithm;
        this.size = builder.size;
        this.storageClass = builder.storageClass;
        this.owner = builder.owner;
        this.restoreStatus = builder.restoreStatus;
    }

    public final String key() {
        return this.key;
    }

    public final Instant lastModified() {
        return this.lastModified;
    }

    public final String eTag() {
        return this.eTag;
    }

    public final List<ChecksumAlgorithm> checksumAlgorithm() {
        return ChecksumAlgorithmListCopier.copyStringToEnum(this.checksumAlgorithm);
    }

    public final boolean hasChecksumAlgorithm() {
        return this.checksumAlgorithm != null && !(this.checksumAlgorithm instanceof SdkAutoConstructList);
    }

    public final List<String> checksumAlgorithmAsStrings() {
        return this.checksumAlgorithm;
    }

    public final Long size() {
        return this.size;
    }

    public final ObjectStorageClass storageClass() {
        return ObjectStorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
    }

    public final Owner owner() {
        return this.owner;
    }

    public final RestoreStatus restoreStatus() {
        return this.restoreStatus;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastModified());
        hashCode = 31 * hashCode + Objects.hashCode(this.eTag());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasChecksumAlgorithm() ? this.checksumAlgorithmAsStrings() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.size());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.owner());
        hashCode = 31 * hashCode + Objects.hashCode(this.restoreStatus());
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
        if (!(obj instanceof S3Object)) {
            return false;
        }
        S3Object other = (S3Object)obj;
        return Objects.equals(this.key(), other.key()) && Objects.equals(this.lastModified(), other.lastModified()) && Objects.equals(this.eTag(), other.eTag()) && this.hasChecksumAlgorithm() == other.hasChecksumAlgorithm() && Objects.equals(this.checksumAlgorithmAsStrings(), other.checksumAlgorithmAsStrings()) && Objects.equals(this.size(), other.size()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.owner(), other.owner()) && Objects.equals(this.restoreStatus(), other.restoreStatus());
    }

    public final String toString() {
        return ToString.builder((String)"S3Object").add("Key", (Object)this.key()).add("LastModified", (Object)this.lastModified()).add("ETag", (Object)this.eTag()).add("ChecksumAlgorithm", this.hasChecksumAlgorithm() ? this.checksumAlgorithmAsStrings() : null).add("Size", (Object)this.size()).add("StorageClass", (Object)this.storageClassAsString()).add("Owner", (Object)this.owner()).add("RestoreStatus", (Object)this.restoreStatus()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "LastModified": {
                return Optional.ofNullable(clazz.cast(this.lastModified()));
            }
            case "ETag": {
                return Optional.ofNullable(clazz.cast(this.eTag()));
            }
            case "ChecksumAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.checksumAlgorithmAsStrings()));
            }
            case "Size": {
                return Optional.ofNullable(clazz.cast(this.size()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
            case "Owner": {
                return Optional.ofNullable(clazz.cast(this.owner()));
            }
            case "RestoreStatus": {
                return Optional.ofNullable(clazz.cast(this.restoreStatus()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<S3Object, T> g) {
        return obj -> g.apply((S3Object)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String key;
        private Instant lastModified;
        private String eTag;
        private List<String> checksumAlgorithm = DefaultSdkAutoConstructList.getInstance();
        private Long size;
        private String storageClass;
        private Owner owner;
        private RestoreStatus restoreStatus;

        private BuilderImpl() {
        }

        private BuilderImpl(S3Object model) {
            this.key(model.key);
            this.lastModified(model.lastModified);
            this.eTag(model.eTag);
            this.checksumAlgorithmWithStrings(model.checksumAlgorithm);
            this.size(model.size);
            this.storageClass(model.storageClass);
            this.owner(model.owner);
            this.restoreStatus(model.restoreStatus);
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

        public final Instant getLastModified() {
            return this.lastModified;
        }

        public final void setLastModified(Instant lastModified) {
            this.lastModified = lastModified;
        }

        @Override
        public final Builder lastModified(Instant lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public final String getETag() {
            return this.eTag;
        }

        public final void setETag(String eTag) {
            this.eTag = eTag;
        }

        @Override
        public final Builder eTag(String eTag) {
            this.eTag = eTag;
            return this;
        }

        public final Collection<String> getChecksumAlgorithm() {
            if (this.checksumAlgorithm instanceof SdkAutoConstructList) {
                return null;
            }
            return this.checksumAlgorithm;
        }

        public final void setChecksumAlgorithm(Collection<String> checksumAlgorithm) {
            this.checksumAlgorithm = ChecksumAlgorithmListCopier.copy(checksumAlgorithm);
        }

        @Override
        public final Builder checksumAlgorithmWithStrings(Collection<String> checksumAlgorithm) {
            this.checksumAlgorithm = ChecksumAlgorithmListCopier.copy(checksumAlgorithm);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder checksumAlgorithmWithStrings(String ... checksumAlgorithm) {
            this.checksumAlgorithmWithStrings(Arrays.asList(checksumAlgorithm));
            return this;
        }

        @Override
        public final Builder checksumAlgorithm(Collection<ChecksumAlgorithm> checksumAlgorithm) {
            this.checksumAlgorithm = ChecksumAlgorithmListCopier.copyEnumToString(checksumAlgorithm);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder checksumAlgorithm(ChecksumAlgorithm ... checksumAlgorithm) {
            this.checksumAlgorithm(Arrays.asList(checksumAlgorithm));
            return this;
        }

        public final Long getSize() {
            return this.size;
        }

        public final void setSize(Long size) {
            this.size = size;
        }

        @Override
        public final Builder size(Long size) {
            this.size = size;
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
        public final Builder storageClass(ObjectStorageClass storageClass) {
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

        public final RestoreStatus.Builder getRestoreStatus() {
            return this.restoreStatus != null ? this.restoreStatus.toBuilder() : null;
        }

        public final void setRestoreStatus(RestoreStatus.BuilderImpl restoreStatus) {
            this.restoreStatus = restoreStatus != null ? restoreStatus.build() : null;
        }

        @Override
        public final Builder restoreStatus(RestoreStatus restoreStatus) {
            this.restoreStatus = restoreStatus;
            return this;
        }

        public S3Object build() {
            return new S3Object(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, S3Object> {
        public Builder key(String var1);

        public Builder lastModified(Instant var1);

        public Builder eTag(String var1);

        public Builder checksumAlgorithmWithStrings(Collection<String> var1);

        public Builder checksumAlgorithmWithStrings(String ... var1);

        public Builder checksumAlgorithm(Collection<ChecksumAlgorithm> var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm ... var1);

        public Builder size(Long var1);

        public Builder storageClass(String var1);

        public Builder storageClass(ObjectStorageClass var1);

        public Builder owner(Owner var1);

        default public Builder owner(Consumer<Owner.Builder> owner) {
            return this.owner((Owner)((Owner.Builder)Owner.builder().applyMutation(owner)).build());
        }

        public Builder restoreStatus(RestoreStatus var1);

        default public Builder restoreStatus(Consumer<RestoreStatus.Builder> restoreStatus) {
            return this.restoreStatus((RestoreStatus)((RestoreStatus.Builder)RestoreStatus.builder().applyMutation(restoreStatus)).build());
        }
    }
}

