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
import software.amazon.awssdk.services.s3.model.Checksum;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesParts;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetObjectAttributesResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetObjectAttributesResponse> {
    private static final SdkField<Boolean> DELETE_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("DeleteMarker").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::deleteMarker)).setter(GetObjectAttributesResponse.setter(Builder::deleteMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-delete-marker").unmarshallLocationName("x-amz-delete-marker").build()}).build();
    private static final SdkField<Instant> LAST_MODIFIED_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("LastModified").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::lastModified)).setter(GetObjectAttributesResponse.setter(Builder::lastModified)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Last-Modified").unmarshallLocationName("Last-Modified").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::versionId)).setter(GetObjectAttributesResponse.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-version-id").unmarshallLocationName("x-amz-version-id").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::requestChargedAsString)).setter(GetObjectAttributesResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final SdkField<String> E_TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ETag").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::eTag)).setter(GetObjectAttributesResponse.setter(Builder::eTag)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ETag").unmarshallLocationName("ETag").build()}).build();
    private static final SdkField<Checksum> CHECKSUM_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Checksum").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::checksum)).setter(GetObjectAttributesResponse.setter(Builder::checksum)).constructor(Checksum::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Checksum").unmarshallLocationName("Checksum").build()}).build();
    private static final SdkField<GetObjectAttributesParts> OBJECT_PARTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ObjectParts").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::objectParts)).setter(GetObjectAttributesResponse.setter(Builder::objectParts)).constructor(GetObjectAttributesParts::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ObjectParts").unmarshallLocationName("ObjectParts").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::storageClassAsString)).setter(GetObjectAttributesResponse.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final SdkField<Long> OBJECT_SIZE_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("ObjectSize").getter(GetObjectAttributesResponse.getter(GetObjectAttributesResponse::objectSize)).setter(GetObjectAttributesResponse.setter(Builder::objectSize)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ObjectSize").unmarshallLocationName("ObjectSize").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DELETE_MARKER_FIELD, LAST_MODIFIED_FIELD, VERSION_ID_FIELD, REQUEST_CHARGED_FIELD, E_TAG_FIELD, CHECKSUM_FIELD, OBJECT_PARTS_FIELD, STORAGE_CLASS_FIELD, OBJECT_SIZE_FIELD));
    private final Boolean deleteMarker;
    private final Instant lastModified;
    private final String versionId;
    private final String requestCharged;
    private final String eTag;
    private final Checksum checksum;
    private final GetObjectAttributesParts objectParts;
    private final String storageClass;
    private final Long objectSize;

    private GetObjectAttributesResponse(BuilderImpl builder) {
        super(builder);
        this.deleteMarker = builder.deleteMarker;
        this.lastModified = builder.lastModified;
        this.versionId = builder.versionId;
        this.requestCharged = builder.requestCharged;
        this.eTag = builder.eTag;
        this.checksum = builder.checksum;
        this.objectParts = builder.objectParts;
        this.storageClass = builder.storageClass;
        this.objectSize = builder.objectSize;
    }

    public final Boolean deleteMarker() {
        return this.deleteMarker;
    }

    public final Instant lastModified() {
        return this.lastModified;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
    }

    public final String eTag() {
        return this.eTag;
    }

    public final Checksum checksum() {
        return this.checksum;
    }

    public final GetObjectAttributesParts objectParts() {
        return this.objectParts;
    }

    public final StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
    }

    public final Long objectSize() {
        return this.objectSize;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.deleteMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastModified());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.eTag());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksum());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectParts());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectSize());
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
        if (!(obj instanceof GetObjectAttributesResponse)) {
            return false;
        }
        GetObjectAttributesResponse other = (GetObjectAttributesResponse)((Object)obj);
        return Objects.equals(this.deleteMarker(), other.deleteMarker()) && Objects.equals(this.lastModified(), other.lastModified()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString()) && Objects.equals(this.eTag(), other.eTag()) && Objects.equals(this.checksum(), other.checksum()) && Objects.equals(this.objectParts(), other.objectParts()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.objectSize(), other.objectSize());
    }

    public final String toString() {
        return ToString.builder((String)"GetObjectAttributesResponse").add("DeleteMarker", (Object)this.deleteMarker()).add("LastModified", (Object)this.lastModified()).add("VersionId", (Object)this.versionId()).add("RequestCharged", (Object)this.requestChargedAsString()).add("ETag", (Object)this.eTag()).add("Checksum", (Object)this.checksum()).add("ObjectParts", (Object)this.objectParts()).add("StorageClass", (Object)this.storageClassAsString()).add("ObjectSize", (Object)this.objectSize()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DeleteMarker": {
                return Optional.ofNullable(clazz.cast(this.deleteMarker()));
            }
            case "LastModified": {
                return Optional.ofNullable(clazz.cast(this.lastModified()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
            }
            case "ETag": {
                return Optional.ofNullable(clazz.cast(this.eTag()));
            }
            case "Checksum": {
                return Optional.ofNullable(clazz.cast(this.checksum()));
            }
            case "ObjectParts": {
                return Optional.ofNullable(clazz.cast(this.objectParts()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
            case "ObjectSize": {
                return Optional.ofNullable(clazz.cast(this.objectSize()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetObjectAttributesResponse, T> g) {
        return obj -> g.apply((GetObjectAttributesResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private Boolean deleteMarker;
        private Instant lastModified;
        private String versionId;
        private String requestCharged;
        private String eTag;
        private Checksum checksum;
        private GetObjectAttributesParts objectParts;
        private String storageClass;
        private Long objectSize;

        private BuilderImpl() {
        }

        private BuilderImpl(GetObjectAttributesResponse model) {
            super(model);
            this.deleteMarker(model.deleteMarker);
            this.lastModified(model.lastModified);
            this.versionId(model.versionId);
            this.requestCharged(model.requestCharged);
            this.eTag(model.eTag);
            this.checksum(model.checksum);
            this.objectParts(model.objectParts);
            this.storageClass(model.storageClass);
            this.objectSize(model.objectSize);
        }

        public final Boolean getDeleteMarker() {
            return this.deleteMarker;
        }

        public final void setDeleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }

        @Override
        public final Builder deleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
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

        public final String getVersionId() {
            return this.versionId;
        }

        public final void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        @Override
        public final Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public final String getRequestCharged() {
            return this.requestCharged;
        }

        public final void setRequestCharged(String requestCharged) {
            this.requestCharged = requestCharged;
        }

        @Override
        public final Builder requestCharged(String requestCharged) {
            this.requestCharged = requestCharged;
            return this;
        }

        @Override
        public final Builder requestCharged(RequestCharged requestCharged) {
            this.requestCharged(requestCharged == null ? null : requestCharged.toString());
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

        public final Checksum.Builder getChecksum() {
            return this.checksum != null ? this.checksum.toBuilder() : null;
        }

        public final void setChecksum(Checksum.BuilderImpl checksum) {
            this.checksum = checksum != null ? checksum.build() : null;
        }

        @Override
        public final Builder checksum(Checksum checksum) {
            this.checksum = checksum;
            return this;
        }

        public final GetObjectAttributesParts.Builder getObjectParts() {
            return this.objectParts != null ? this.objectParts.toBuilder() : null;
        }

        public final void setObjectParts(GetObjectAttributesParts.BuilderImpl objectParts) {
            this.objectParts = objectParts != null ? objectParts.build() : null;
        }

        @Override
        public final Builder objectParts(GetObjectAttributesParts objectParts) {
            this.objectParts = objectParts;
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

        public final Long getObjectSize() {
            return this.objectSize;
        }

        public final void setObjectSize(Long objectSize) {
            this.objectSize = objectSize;
        }

        @Override
        public final Builder objectSize(Long objectSize) {
            this.objectSize = objectSize;
            return this;
        }

        @Override
        public GetObjectAttributesResponse build() {
            return new GetObjectAttributesResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetObjectAttributesResponse> {
        public Builder deleteMarker(Boolean var1);

        public Builder lastModified(Instant var1);

        public Builder versionId(String var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);

        public Builder eTag(String var1);

        public Builder checksum(Checksum var1);

        default public Builder checksum(Consumer<Checksum.Builder> checksum) {
            return this.checksum((Checksum)((Checksum.Builder)Checksum.builder().applyMutation(checksum)).build());
        }

        public Builder objectParts(GetObjectAttributesParts var1);

        default public Builder objectParts(Consumer<GetObjectAttributesParts.Builder> objectParts) {
            return this.objectParts((GetObjectAttributesParts)((GetObjectAttributesParts.Builder)GetObjectAttributesParts.builder().applyMutation(objectParts)).build());
        }

        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);

        public Builder objectSize(Long var1);
    }
}

