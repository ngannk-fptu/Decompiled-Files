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
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.Initiator;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.model.PartsCopier;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListPartsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListPartsResponse> {
    private static final SdkField<Instant> ABORT_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("AbortDate").getter(ListPartsResponse.getter(ListPartsResponse::abortDate)).setter(ListPartsResponse.setter(Builder::abortDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-abort-date").unmarshallLocationName("x-amz-abort-date").build()}).build();
    private static final SdkField<String> ABORT_RULE_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AbortRuleId").getter(ListPartsResponse.getter(ListPartsResponse::abortRuleId)).setter(ListPartsResponse.setter(Builder::abortRuleId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-abort-rule-id").unmarshallLocationName("x-amz-abort-rule-id").build()}).build();
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(ListPartsResponse.getter(ListPartsResponse::bucket)).setter(ListPartsResponse.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Bucket").unmarshallLocationName("Bucket").build()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(ListPartsResponse.getter(ListPartsResponse::key)).setter(ListPartsResponse.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> UPLOAD_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("UploadId").getter(ListPartsResponse.getter(ListPartsResponse::uploadId)).setter(ListPartsResponse.setter(Builder::uploadId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("UploadId").unmarshallLocationName("UploadId").build()}).build();
    private static final SdkField<Integer> PART_NUMBER_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PartNumberMarker").getter(ListPartsResponse.getter(ListPartsResponse::partNumberMarker)).setter(ListPartsResponse.setter(Builder::partNumberMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PartNumberMarker").unmarshallLocationName("PartNumberMarker").build()}).build();
    private static final SdkField<Integer> NEXT_PART_NUMBER_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("NextPartNumberMarker").getter(ListPartsResponse.getter(ListPartsResponse::nextPartNumberMarker)).setter(ListPartsResponse.setter(Builder::nextPartNumberMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextPartNumberMarker").unmarshallLocationName("NextPartNumberMarker").build()}).build();
    private static final SdkField<Integer> MAX_PARTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxParts").getter(ListPartsResponse.getter(ListPartsResponse::maxParts)).setter(ListPartsResponse.setter(Builder::maxParts)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxParts").unmarshallLocationName("MaxParts").build()}).build();
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListPartsResponse.getter(ListPartsResponse::isTruncated)).setter(ListPartsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<List<Part>> PARTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Parts").getter(ListPartsResponse.getter(ListPartsResponse::parts)).setter(ListPartsResponse.setter(Builder::parts)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Part").unmarshallLocationName("Part").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Part::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<Initiator> INITIATOR_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Initiator").getter(ListPartsResponse.getter(ListPartsResponse::initiator)).setter(ListPartsResponse.setter(Builder::initiator)).constructor(Initiator::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Initiator").unmarshallLocationName("Initiator").build()}).build();
    private static final SdkField<Owner> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Owner").getter(ListPartsResponse.getter(ListPartsResponse::owner)).setter(ListPartsResponse.setter(Builder::owner)).constructor(Owner::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(ListPartsResponse.getter(ListPartsResponse::storageClassAsString)).setter(ListPartsResponse.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(ListPartsResponse.getter(ListPartsResponse::requestChargedAsString)).setter(ListPartsResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final SdkField<String> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumAlgorithm").getter(ListPartsResponse.getter(ListPartsResponse::checksumAlgorithmAsString)).setter(ListPartsResponse.setter(Builder::checksumAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumAlgorithm").unmarshallLocationName("ChecksumAlgorithm").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ABORT_DATE_FIELD, ABORT_RULE_ID_FIELD, BUCKET_FIELD, KEY_FIELD, UPLOAD_ID_FIELD, PART_NUMBER_MARKER_FIELD, NEXT_PART_NUMBER_MARKER_FIELD, MAX_PARTS_FIELD, IS_TRUNCATED_FIELD, PARTS_FIELD, INITIATOR_FIELD, OWNER_FIELD, STORAGE_CLASS_FIELD, REQUEST_CHARGED_FIELD, CHECKSUM_ALGORITHM_FIELD));
    private final Instant abortDate;
    private final String abortRuleId;
    private final String bucket;
    private final String key;
    private final String uploadId;
    private final Integer partNumberMarker;
    private final Integer nextPartNumberMarker;
    private final Integer maxParts;
    private final Boolean isTruncated;
    private final List<Part> parts;
    private final Initiator initiator;
    private final Owner owner;
    private final String storageClass;
    private final String requestCharged;
    private final String checksumAlgorithm;

    private ListPartsResponse(BuilderImpl builder) {
        super(builder);
        this.abortDate = builder.abortDate;
        this.abortRuleId = builder.abortRuleId;
        this.bucket = builder.bucket;
        this.key = builder.key;
        this.uploadId = builder.uploadId;
        this.partNumberMarker = builder.partNumberMarker;
        this.nextPartNumberMarker = builder.nextPartNumberMarker;
        this.maxParts = builder.maxParts;
        this.isTruncated = builder.isTruncated;
        this.parts = builder.parts;
        this.initiator = builder.initiator;
        this.owner = builder.owner;
        this.storageClass = builder.storageClass;
        this.requestCharged = builder.requestCharged;
        this.checksumAlgorithm = builder.checksumAlgorithm;
    }

    public final Instant abortDate() {
        return this.abortDate;
    }

    public final String abortRuleId() {
        return this.abortRuleId;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String key() {
        return this.key;
    }

    public final String uploadId() {
        return this.uploadId;
    }

    public final Integer partNumberMarker() {
        return this.partNumberMarker;
    }

    public final Integer nextPartNumberMarker() {
        return this.nextPartNumberMarker;
    }

    public final Integer maxParts() {
        return this.maxParts;
    }

    public final Boolean isTruncated() {
        return this.isTruncated;
    }

    public final boolean hasParts() {
        return this.parts != null && !(this.parts instanceof SdkAutoConstructList);
    }

    public final List<Part> parts() {
        return this.parts;
    }

    public final Initiator initiator() {
        return this.initiator;
    }

    public final Owner owner() {
        return this.owner;
    }

    public final StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.abortDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.abortRuleId());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.uploadId());
        hashCode = 31 * hashCode + Objects.hashCode(this.partNumberMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextPartNumberMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxParts());
        hashCode = 31 * hashCode + Objects.hashCode(this.isTruncated());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasParts() ? this.parts() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.initiator());
        hashCode = 31 * hashCode + Objects.hashCode(this.owner());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumAlgorithmAsString());
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
        if (!(obj instanceof ListPartsResponse)) {
            return false;
        }
        ListPartsResponse other = (ListPartsResponse)((Object)obj);
        return Objects.equals(this.abortDate(), other.abortDate()) && Objects.equals(this.abortRuleId(), other.abortRuleId()) && Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.uploadId(), other.uploadId()) && Objects.equals(this.partNumberMarker(), other.partNumberMarker()) && Objects.equals(this.nextPartNumberMarker(), other.nextPartNumberMarker()) && Objects.equals(this.maxParts(), other.maxParts()) && Objects.equals(this.isTruncated(), other.isTruncated()) && this.hasParts() == other.hasParts() && Objects.equals(this.parts(), other.parts()) && Objects.equals(this.initiator(), other.initiator()) && Objects.equals(this.owner(), other.owner()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString()) && Objects.equals(this.checksumAlgorithmAsString(), other.checksumAlgorithmAsString());
    }

    public final String toString() {
        return ToString.builder((String)"ListPartsResponse").add("AbortDate", (Object)this.abortDate()).add("AbortRuleId", (Object)this.abortRuleId()).add("Bucket", (Object)this.bucket()).add("Key", (Object)this.key()).add("UploadId", (Object)this.uploadId()).add("PartNumberMarker", (Object)this.partNumberMarker()).add("NextPartNumberMarker", (Object)this.nextPartNumberMarker()).add("MaxParts", (Object)this.maxParts()).add("IsTruncated", (Object)this.isTruncated()).add("Parts", this.hasParts() ? this.parts() : null).add("Initiator", (Object)this.initiator()).add("Owner", (Object)this.owner()).add("StorageClass", (Object)this.storageClassAsString()).add("RequestCharged", (Object)this.requestChargedAsString()).add("ChecksumAlgorithm", (Object)this.checksumAlgorithmAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "AbortDate": {
                return Optional.ofNullable(clazz.cast(this.abortDate()));
            }
            case "AbortRuleId": {
                return Optional.ofNullable(clazz.cast(this.abortRuleId()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "UploadId": {
                return Optional.ofNullable(clazz.cast(this.uploadId()));
            }
            case "PartNumberMarker": {
                return Optional.ofNullable(clazz.cast(this.partNumberMarker()));
            }
            case "NextPartNumberMarker": {
                return Optional.ofNullable(clazz.cast(this.nextPartNumberMarker()));
            }
            case "MaxParts": {
                return Optional.ofNullable(clazz.cast(this.maxParts()));
            }
            case "IsTruncated": {
                return Optional.ofNullable(clazz.cast(this.isTruncated()));
            }
            case "Parts": {
                return Optional.ofNullable(clazz.cast(this.parts()));
            }
            case "Initiator": {
                return Optional.ofNullable(clazz.cast(this.initiator()));
            }
            case "Owner": {
                return Optional.ofNullable(clazz.cast(this.owner()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
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

    private static <T> Function<Object, T> getter(Function<ListPartsResponse, T> g) {
        return obj -> g.apply((ListPartsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private Instant abortDate;
        private String abortRuleId;
        private String bucket;
        private String key;
        private String uploadId;
        private Integer partNumberMarker;
        private Integer nextPartNumberMarker;
        private Integer maxParts;
        private Boolean isTruncated;
        private List<Part> parts = DefaultSdkAutoConstructList.getInstance();
        private Initiator initiator;
        private Owner owner;
        private String storageClass;
        private String requestCharged;
        private String checksumAlgorithm;

        private BuilderImpl() {
        }

        private BuilderImpl(ListPartsResponse model) {
            super(model);
            this.abortDate(model.abortDate);
            this.abortRuleId(model.abortRuleId);
            this.bucket(model.bucket);
            this.key(model.key);
            this.uploadId(model.uploadId);
            this.partNumberMarker(model.partNumberMarker);
            this.nextPartNumberMarker(model.nextPartNumberMarker);
            this.maxParts(model.maxParts);
            this.isTruncated(model.isTruncated);
            this.parts(model.parts);
            this.initiator(model.initiator);
            this.owner(model.owner);
            this.storageClass(model.storageClass);
            this.requestCharged(model.requestCharged);
            this.checksumAlgorithm(model.checksumAlgorithm);
        }

        public final Instant getAbortDate() {
            return this.abortDate;
        }

        public final void setAbortDate(Instant abortDate) {
            this.abortDate = abortDate;
        }

        @Override
        public final Builder abortDate(Instant abortDate) {
            this.abortDate = abortDate;
            return this;
        }

        public final String getAbortRuleId() {
            return this.abortRuleId;
        }

        public final void setAbortRuleId(String abortRuleId) {
            this.abortRuleId = abortRuleId;
        }

        @Override
        public final Builder abortRuleId(String abortRuleId) {
            this.abortRuleId = abortRuleId;
            return this;
        }

        public final String getBucket() {
            return this.bucket;
        }

        public final void setBucket(String bucket) {
            this.bucket = bucket;
        }

        @Override
        public final Builder bucket(String bucket) {
            this.bucket = bucket;
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

        public final Integer getPartNumberMarker() {
            return this.partNumberMarker;
        }

        public final void setPartNumberMarker(Integer partNumberMarker) {
            this.partNumberMarker = partNumberMarker;
        }

        @Override
        public final Builder partNumberMarker(Integer partNumberMarker) {
            this.partNumberMarker = partNumberMarker;
            return this;
        }

        public final Integer getNextPartNumberMarker() {
            return this.nextPartNumberMarker;
        }

        public final void setNextPartNumberMarker(Integer nextPartNumberMarker) {
            this.nextPartNumberMarker = nextPartNumberMarker;
        }

        @Override
        public final Builder nextPartNumberMarker(Integer nextPartNumberMarker) {
            this.nextPartNumberMarker = nextPartNumberMarker;
            return this;
        }

        public final Integer getMaxParts() {
            return this.maxParts;
        }

        public final void setMaxParts(Integer maxParts) {
            this.maxParts = maxParts;
        }

        @Override
        public final Builder maxParts(Integer maxParts) {
            this.maxParts = maxParts;
            return this;
        }

        public final Boolean getIsTruncated() {
            return this.isTruncated;
        }

        public final void setIsTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
        }

        @Override
        public final Builder isTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
            return this;
        }

        public final List<Part.Builder> getParts() {
            List<Part.Builder> result = PartsCopier.copyToBuilder(this.parts);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setParts(Collection<Part.BuilderImpl> parts) {
            this.parts = PartsCopier.copyFromBuilder(parts);
        }

        @Override
        public final Builder parts(Collection<Part> parts) {
            this.parts = PartsCopier.copy(parts);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder parts(Part ... parts) {
            this.parts(Arrays.asList(parts));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder parts(Consumer<Part.Builder> ... parts) {
            this.parts(Stream.of(parts).map(c -> (Part)((Part.Builder)Part.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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

        @Override
        public ListPartsResponse build() {
            return new ListPartsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListPartsResponse> {
        public Builder abortDate(Instant var1);

        public Builder abortRuleId(String var1);

        public Builder bucket(String var1);

        public Builder key(String var1);

        public Builder uploadId(String var1);

        public Builder partNumberMarker(Integer var1);

        public Builder nextPartNumberMarker(Integer var1);

        public Builder maxParts(Integer var1);

        public Builder isTruncated(Boolean var1);

        public Builder parts(Collection<Part> var1);

        public Builder parts(Part ... var1);

        public Builder parts(Consumer<Part.Builder> ... var1);

        public Builder initiator(Initiator var1);

        default public Builder initiator(Consumer<Initiator.Builder> initiator) {
            return this.initiator((Initiator)((Initiator.Builder)Initiator.builder().applyMutation(initiator)).build());
        }

        public Builder owner(Owner var1);

        default public Builder owner(Consumer<Owner.Builder> owner) {
            return this.owner((Owner)((Owner.Builder)Owner.builder().applyMutation(owner)).build());
        }

        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);

        public Builder checksumAlgorithm(String var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm var1);
    }
}

