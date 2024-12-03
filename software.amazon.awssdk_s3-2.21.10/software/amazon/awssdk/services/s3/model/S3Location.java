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
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.Encryption;
import software.amazon.awssdk.services.s3.model.Grant;
import software.amazon.awssdk.services.s3.model.GrantsCopier;
import software.amazon.awssdk.services.s3.model.MetadataEntry;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.model.UserMetadataCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class S3Location
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, S3Location> {
    private static final SdkField<String> BUCKET_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("BucketName").getter(S3Location.getter(S3Location::bucketName)).setter(S3Location.setter(Builder::bucketName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BucketName").unmarshallLocationName("BucketName").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(S3Location.getter(S3Location::prefix)).setter(S3Location.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build(), RequiredTrait.create()}).build();
    private static final SdkField<Encryption> ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Encryption").getter(S3Location.getter(S3Location::encryption)).setter(S3Location.setter(Builder::encryption)).constructor(Encryption::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Encryption").unmarshallLocationName("Encryption").build()}).build();
    private static final SdkField<String> CANNED_ACL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CannedACL").getter(S3Location.getter(S3Location::cannedACLAsString)).setter(S3Location.setter(Builder::cannedACL)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CannedACL").unmarshallLocationName("CannedACL").build()}).build();
    private static final SdkField<List<Grant>> ACCESS_CONTROL_LIST_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("AccessControlList").getter(S3Location.getter(S3Location::accessControlList)).setter(S3Location.setter(Builder::accessControlList)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessControlList").unmarshallLocationName("AccessControlList").build(), ListTrait.builder().memberLocationName("Grant").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Grant::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Grant").unmarshallLocationName("Grant").build()}).build()).build()}).build();
    private static final SdkField<Tagging> TAGGING_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Tagging").getter(S3Location.getter(S3Location::tagging)).setter(S3Location.setter(Builder::tagging)).constructor(Tagging::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tagging").unmarshallLocationName("Tagging").build()}).build();
    private static final SdkField<List<MetadataEntry>> USER_METADATA_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("UserMetadata").getter(S3Location.getter(S3Location::userMetadata)).setter(S3Location.setter(Builder::userMetadata)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("UserMetadata").unmarshallLocationName("UserMetadata").build(), ListTrait.builder().memberLocationName("MetadataEntry").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(MetadataEntry::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MetadataEntry").unmarshallLocationName("MetadataEntry").build()}).build()).build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(S3Location.getter(S3Location::storageClassAsString)).setter(S3Location.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_NAME_FIELD, PREFIX_FIELD, ENCRYPTION_FIELD, CANNED_ACL_FIELD, ACCESS_CONTROL_LIST_FIELD, TAGGING_FIELD, USER_METADATA_FIELD, STORAGE_CLASS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String bucketName;
    private final String prefix;
    private final Encryption encryption;
    private final String cannedACL;
    private final List<Grant> accessControlList;
    private final Tagging tagging;
    private final List<MetadataEntry> userMetadata;
    private final String storageClass;

    private S3Location(BuilderImpl builder) {
        this.bucketName = builder.bucketName;
        this.prefix = builder.prefix;
        this.encryption = builder.encryption;
        this.cannedACL = builder.cannedACL;
        this.accessControlList = builder.accessControlList;
        this.tagging = builder.tagging;
        this.userMetadata = builder.userMetadata;
        this.storageClass = builder.storageClass;
    }

    public final String bucketName() {
        return this.bucketName;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final Encryption encryption() {
        return this.encryption;
    }

    public final ObjectCannedACL cannedACL() {
        return ObjectCannedACL.fromValue(this.cannedACL);
    }

    public final String cannedACLAsString() {
        return this.cannedACL;
    }

    public final boolean hasAccessControlList() {
        return this.accessControlList != null && !(this.accessControlList instanceof SdkAutoConstructList);
    }

    public final List<Grant> accessControlList() {
        return this.accessControlList;
    }

    public final Tagging tagging() {
        return this.tagging;
    }

    public final boolean hasUserMetadata() {
        return this.userMetadata != null && !(this.userMetadata instanceof SdkAutoConstructList);
    }

    public final List<MetadataEntry> userMetadata() {
        return this.userMetadata;
    }

    public final StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketName());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.encryption());
        hashCode = 31 * hashCode + Objects.hashCode(this.cannedACLAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasAccessControlList() ? this.accessControlList() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.tagging());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasUserMetadata() ? this.userMetadata() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
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
        if (!(obj instanceof S3Location)) {
            return false;
        }
        S3Location other = (S3Location)obj;
        return Objects.equals(this.bucketName(), other.bucketName()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.encryption(), other.encryption()) && Objects.equals(this.cannedACLAsString(), other.cannedACLAsString()) && this.hasAccessControlList() == other.hasAccessControlList() && Objects.equals(this.accessControlList(), other.accessControlList()) && Objects.equals(this.tagging(), other.tagging()) && this.hasUserMetadata() == other.hasUserMetadata() && Objects.equals(this.userMetadata(), other.userMetadata()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString());
    }

    public final String toString() {
        return ToString.builder((String)"S3Location").add("BucketName", (Object)this.bucketName()).add("Prefix", (Object)this.prefix()).add("Encryption", (Object)this.encryption()).add("CannedACL", (Object)this.cannedACLAsString()).add("AccessControlList", this.hasAccessControlList() ? this.accessControlList() : null).add("Tagging", (Object)this.tagging()).add("UserMetadata", this.hasUserMetadata() ? this.userMetadata() : null).add("StorageClass", (Object)this.storageClassAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "BucketName": {
                return Optional.ofNullable(clazz.cast(this.bucketName()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Encryption": {
                return Optional.ofNullable(clazz.cast(this.encryption()));
            }
            case "CannedACL": {
                return Optional.ofNullable(clazz.cast(this.cannedACLAsString()));
            }
            case "AccessControlList": {
                return Optional.ofNullable(clazz.cast(this.accessControlList()));
            }
            case "Tagging": {
                return Optional.ofNullable(clazz.cast(this.tagging()));
            }
            case "UserMetadata": {
                return Optional.ofNullable(clazz.cast(this.userMetadata()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<S3Location, T> g) {
        return obj -> g.apply((S3Location)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String bucketName;
        private String prefix;
        private Encryption encryption;
        private String cannedACL;
        private List<Grant> accessControlList = DefaultSdkAutoConstructList.getInstance();
        private Tagging tagging;
        private List<MetadataEntry> userMetadata = DefaultSdkAutoConstructList.getInstance();
        private String storageClass;

        private BuilderImpl() {
        }

        private BuilderImpl(S3Location model) {
            this.bucketName(model.bucketName);
            this.prefix(model.prefix);
            this.encryption(model.encryption);
            this.cannedACL(model.cannedACL);
            this.accessControlList(model.accessControlList);
            this.tagging(model.tagging);
            this.userMetadata(model.userMetadata);
            this.storageClass(model.storageClass);
        }

        public final String getBucketName() {
            return this.bucketName;
        }

        public final void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        @Override
        public final Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public final String getPrefix() {
            return this.prefix;
        }

        public final void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public final Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public final Encryption.Builder getEncryption() {
            return this.encryption != null ? this.encryption.toBuilder() : null;
        }

        public final void setEncryption(Encryption.BuilderImpl encryption) {
            this.encryption = encryption != null ? encryption.build() : null;
        }

        @Override
        public final Builder encryption(Encryption encryption) {
            this.encryption = encryption;
            return this;
        }

        public final String getCannedACL() {
            return this.cannedACL;
        }

        public final void setCannedACL(String cannedACL) {
            this.cannedACL = cannedACL;
        }

        @Override
        public final Builder cannedACL(String cannedACL) {
            this.cannedACL = cannedACL;
            return this;
        }

        @Override
        public final Builder cannedACL(ObjectCannedACL cannedACL) {
            this.cannedACL(cannedACL == null ? null : cannedACL.toString());
            return this;
        }

        public final List<Grant.Builder> getAccessControlList() {
            List<Grant.Builder> result = GrantsCopier.copyToBuilder(this.accessControlList);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setAccessControlList(Collection<Grant.BuilderImpl> accessControlList) {
            this.accessControlList = GrantsCopier.copyFromBuilder(accessControlList);
        }

        @Override
        public final Builder accessControlList(Collection<Grant> accessControlList) {
            this.accessControlList = GrantsCopier.copy(accessControlList);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder accessControlList(Grant ... accessControlList) {
            this.accessControlList(Arrays.asList(accessControlList));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder accessControlList(Consumer<Grant.Builder> ... accessControlList) {
            this.accessControlList(Stream.of(accessControlList).map(c -> (Grant)((Grant.Builder)Grant.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final Tagging.Builder getTagging() {
            return this.tagging != null ? this.tagging.toBuilder() : null;
        }

        public final void setTagging(Tagging.BuilderImpl tagging) {
            this.tagging = tagging != null ? tagging.build() : null;
        }

        @Override
        public final Builder tagging(Tagging tagging) {
            this.tagging = tagging;
            return this;
        }

        public final List<MetadataEntry.Builder> getUserMetadata() {
            List<MetadataEntry.Builder> result = UserMetadataCopier.copyToBuilder(this.userMetadata);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setUserMetadata(Collection<MetadataEntry.BuilderImpl> userMetadata) {
            this.userMetadata = UserMetadataCopier.copyFromBuilder(userMetadata);
        }

        @Override
        public final Builder userMetadata(Collection<MetadataEntry> userMetadata) {
            this.userMetadata = UserMetadataCopier.copy(userMetadata);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder userMetadata(MetadataEntry ... userMetadata) {
            this.userMetadata(Arrays.asList(userMetadata));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder userMetadata(Consumer<MetadataEntry.Builder> ... userMetadata) {
            this.userMetadata(Stream.of(userMetadata).map(c -> (MetadataEntry)((MetadataEntry.Builder)MetadataEntry.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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

        public S3Location build() {
            return new S3Location(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, S3Location> {
        public Builder bucketName(String var1);

        public Builder prefix(String var1);

        public Builder encryption(Encryption var1);

        default public Builder encryption(Consumer<Encryption.Builder> encryption) {
            return this.encryption((Encryption)((Encryption.Builder)Encryption.builder().applyMutation(encryption)).build());
        }

        public Builder cannedACL(String var1);

        public Builder cannedACL(ObjectCannedACL var1);

        public Builder accessControlList(Collection<Grant> var1);

        public Builder accessControlList(Grant ... var1);

        public Builder accessControlList(Consumer<Grant.Builder> ... var1);

        public Builder tagging(Tagging var1);

        default public Builder tagging(Consumer<Tagging.Builder> tagging) {
            return this.tagging((Tagging)((Tagging.Builder)Tagging.builder().applyMutation(tagging)).build());
        }

        public Builder userMetadata(Collection<MetadataEntry> var1);

        public Builder userMetadata(MetadataEntry ... var1);

        public Builder userMetadata(Consumer<MetadataEntry.Builder> ... var1);

        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);
    }
}

