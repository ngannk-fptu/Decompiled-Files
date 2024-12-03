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
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.CommonPrefixListCopier;
import software.amazon.awssdk.services.s3.model.DeleteMarkerEntry;
import software.amazon.awssdk.services.s3.model.DeleteMarkersCopier;
import software.amazon.awssdk.services.s3.model.EncodingType;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.model.ObjectVersionListCopier;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListObjectVersionsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListObjectVersionsResponse> {
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::isTruncated)).setter(ListObjectVersionsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<String> KEY_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KeyMarker").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::keyMarker)).setter(ListObjectVersionsResponse.setter(Builder::keyMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KeyMarker").unmarshallLocationName("KeyMarker").build()}).build();
    private static final SdkField<String> VERSION_ID_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionIdMarker").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::versionIdMarker)).setter(ListObjectVersionsResponse.setter(Builder::versionIdMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionIdMarker").unmarshallLocationName("VersionIdMarker").build()}).build();
    private static final SdkField<String> NEXT_KEY_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextKeyMarker").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::nextKeyMarker)).setter(ListObjectVersionsResponse.setter(Builder::nextKeyMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextKeyMarker").unmarshallLocationName("NextKeyMarker").build()}).build();
    private static final SdkField<String> NEXT_VERSION_ID_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextVersionIdMarker").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::nextVersionIdMarker)).setter(ListObjectVersionsResponse.setter(Builder::nextVersionIdMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextVersionIdMarker").unmarshallLocationName("NextVersionIdMarker").build()}).build();
    private static final SdkField<List<ObjectVersion>> VERSIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Versions").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::versions)).setter(ListObjectVersionsResponse.setter(Builder::versions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Version").unmarshallLocationName("Version").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ObjectVersion::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<List<DeleteMarkerEntry>> DELETE_MARKERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("DeleteMarkers").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::deleteMarkers)).setter(ListObjectVersionsResponse.setter(Builder::deleteMarkers)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DeleteMarker").unmarshallLocationName("DeleteMarker").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(DeleteMarkerEntry::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::name)).setter(ListObjectVersionsResponse.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").unmarshallLocationName("Name").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::prefix)).setter(ListObjectVersionsResponse.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<String> DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Delimiter").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::delimiter)).setter(ListObjectVersionsResponse.setter(Builder::delimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Delimiter").unmarshallLocationName("Delimiter").build()}).build();
    private static final SdkField<Integer> MAX_KEYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxKeys").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::maxKeys)).setter(ListObjectVersionsResponse.setter(Builder::maxKeys)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxKeys").unmarshallLocationName("MaxKeys").build()}).build();
    private static final SdkField<List<CommonPrefix>> COMMON_PREFIXES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("CommonPrefixes").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::commonPrefixes)).setter(ListObjectVersionsResponse.setter(Builder::commonPrefixes)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CommonPrefixes").unmarshallLocationName("CommonPrefixes").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(CommonPrefix::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<String> ENCODING_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncodingType").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::encodingTypeAsString)).setter(ListObjectVersionsResponse.setter(Builder::encodingType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EncodingType").unmarshallLocationName("EncodingType").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(ListObjectVersionsResponse.getter(ListObjectVersionsResponse::requestChargedAsString)).setter(ListObjectVersionsResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(IS_TRUNCATED_FIELD, KEY_MARKER_FIELD, VERSION_ID_MARKER_FIELD, NEXT_KEY_MARKER_FIELD, NEXT_VERSION_ID_MARKER_FIELD, VERSIONS_FIELD, DELETE_MARKERS_FIELD, NAME_FIELD, PREFIX_FIELD, DELIMITER_FIELD, MAX_KEYS_FIELD, COMMON_PREFIXES_FIELD, ENCODING_TYPE_FIELD, REQUEST_CHARGED_FIELD));
    private final Boolean isTruncated;
    private final String keyMarker;
    private final String versionIdMarker;
    private final String nextKeyMarker;
    private final String nextVersionIdMarker;
    private final List<ObjectVersion> versions;
    private final List<DeleteMarkerEntry> deleteMarkers;
    private final String name;
    private final String prefix;
    private final String delimiter;
    private final Integer maxKeys;
    private final List<CommonPrefix> commonPrefixes;
    private final String encodingType;
    private final String requestCharged;

    private ListObjectVersionsResponse(BuilderImpl builder) {
        super(builder);
        this.isTruncated = builder.isTruncated;
        this.keyMarker = builder.keyMarker;
        this.versionIdMarker = builder.versionIdMarker;
        this.nextKeyMarker = builder.nextKeyMarker;
        this.nextVersionIdMarker = builder.nextVersionIdMarker;
        this.versions = builder.versions;
        this.deleteMarkers = builder.deleteMarkers;
        this.name = builder.name;
        this.prefix = builder.prefix;
        this.delimiter = builder.delimiter;
        this.maxKeys = builder.maxKeys;
        this.commonPrefixes = builder.commonPrefixes;
        this.encodingType = builder.encodingType;
        this.requestCharged = builder.requestCharged;
    }

    public final Boolean isTruncated() {
        return this.isTruncated;
    }

    public final String keyMarker() {
        return this.keyMarker;
    }

    public final String versionIdMarker() {
        return this.versionIdMarker;
    }

    public final String nextKeyMarker() {
        return this.nextKeyMarker;
    }

    public final String nextVersionIdMarker() {
        return this.nextVersionIdMarker;
    }

    public final boolean hasVersions() {
        return this.versions != null && !(this.versions instanceof SdkAutoConstructList);
    }

    public final List<ObjectVersion> versions() {
        return this.versions;
    }

    public final boolean hasDeleteMarkers() {
        return this.deleteMarkers != null && !(this.deleteMarkers instanceof SdkAutoConstructList);
    }

    public final List<DeleteMarkerEntry> deleteMarkers() {
        return this.deleteMarkers;
    }

    public final String name() {
        return this.name;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final String delimiter() {
        return this.delimiter;
    }

    public final Integer maxKeys() {
        return this.maxKeys;
    }

    public final boolean hasCommonPrefixes() {
        return this.commonPrefixes != null && !(this.commonPrefixes instanceof SdkAutoConstructList);
    }

    public final List<CommonPrefix> commonPrefixes() {
        return this.commonPrefixes;
    }

    public final EncodingType encodingType() {
        return EncodingType.fromValue(this.encodingType);
    }

    public final String encodingTypeAsString() {
        return this.encodingType;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.isTruncated());
        hashCode = 31 * hashCode + Objects.hashCode(this.keyMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionIdMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextKeyMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextVersionIdMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasVersions() ? this.versions() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasDeleteMarkers() ? this.deleteMarkers() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.delimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxKeys());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasCommonPrefixes() ? this.commonPrefixes() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.encodingTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
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
        if (!(obj instanceof ListObjectVersionsResponse)) {
            return false;
        }
        ListObjectVersionsResponse other = (ListObjectVersionsResponse)((Object)obj);
        return Objects.equals(this.isTruncated(), other.isTruncated()) && Objects.equals(this.keyMarker(), other.keyMarker()) && Objects.equals(this.versionIdMarker(), other.versionIdMarker()) && Objects.equals(this.nextKeyMarker(), other.nextKeyMarker()) && Objects.equals(this.nextVersionIdMarker(), other.nextVersionIdMarker()) && this.hasVersions() == other.hasVersions() && Objects.equals(this.versions(), other.versions()) && this.hasDeleteMarkers() == other.hasDeleteMarkers() && Objects.equals(this.deleteMarkers(), other.deleteMarkers()) && Objects.equals(this.name(), other.name()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.delimiter(), other.delimiter()) && Objects.equals(this.maxKeys(), other.maxKeys()) && this.hasCommonPrefixes() == other.hasCommonPrefixes() && Objects.equals(this.commonPrefixes(), other.commonPrefixes()) && Objects.equals(this.encodingTypeAsString(), other.encodingTypeAsString()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString());
    }

    public final String toString() {
        return ToString.builder((String)"ListObjectVersionsResponse").add("IsTruncated", (Object)this.isTruncated()).add("KeyMarker", (Object)this.keyMarker()).add("VersionIdMarker", (Object)this.versionIdMarker()).add("NextKeyMarker", (Object)this.nextKeyMarker()).add("NextVersionIdMarker", (Object)this.nextVersionIdMarker()).add("Versions", this.hasVersions() ? this.versions() : null).add("DeleteMarkers", this.hasDeleteMarkers() ? this.deleteMarkers() : null).add("Name", (Object)this.name()).add("Prefix", (Object)this.prefix()).add("Delimiter", (Object)this.delimiter()).add("MaxKeys", (Object)this.maxKeys()).add("CommonPrefixes", this.hasCommonPrefixes() ? this.commonPrefixes() : null).add("EncodingType", (Object)this.encodingTypeAsString()).add("RequestCharged", (Object)this.requestChargedAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "IsTruncated": {
                return Optional.ofNullable(clazz.cast(this.isTruncated()));
            }
            case "KeyMarker": {
                return Optional.ofNullable(clazz.cast(this.keyMarker()));
            }
            case "VersionIdMarker": {
                return Optional.ofNullable(clazz.cast(this.versionIdMarker()));
            }
            case "NextKeyMarker": {
                return Optional.ofNullable(clazz.cast(this.nextKeyMarker()));
            }
            case "NextVersionIdMarker": {
                return Optional.ofNullable(clazz.cast(this.nextVersionIdMarker()));
            }
            case "Versions": {
                return Optional.ofNullable(clazz.cast(this.versions()));
            }
            case "DeleteMarkers": {
                return Optional.ofNullable(clazz.cast(this.deleteMarkers()));
            }
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Delimiter": {
                return Optional.ofNullable(clazz.cast(this.delimiter()));
            }
            case "MaxKeys": {
                return Optional.ofNullable(clazz.cast(this.maxKeys()));
            }
            case "CommonPrefixes": {
                return Optional.ofNullable(clazz.cast(this.commonPrefixes()));
            }
            case "EncodingType": {
                return Optional.ofNullable(clazz.cast(this.encodingTypeAsString()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListObjectVersionsResponse, T> g) {
        return obj -> g.apply((ListObjectVersionsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private Boolean isTruncated;
        private String keyMarker;
        private String versionIdMarker;
        private String nextKeyMarker;
        private String nextVersionIdMarker;
        private List<ObjectVersion> versions = DefaultSdkAutoConstructList.getInstance();
        private List<DeleteMarkerEntry> deleteMarkers = DefaultSdkAutoConstructList.getInstance();
        private String name;
        private String prefix;
        private String delimiter;
        private Integer maxKeys;
        private List<CommonPrefix> commonPrefixes = DefaultSdkAutoConstructList.getInstance();
        private String encodingType;
        private String requestCharged;

        private BuilderImpl() {
        }

        private BuilderImpl(ListObjectVersionsResponse model) {
            super(model);
            this.isTruncated(model.isTruncated);
            this.keyMarker(model.keyMarker);
            this.versionIdMarker(model.versionIdMarker);
            this.nextKeyMarker(model.nextKeyMarker);
            this.nextVersionIdMarker(model.nextVersionIdMarker);
            this.versions(model.versions);
            this.deleteMarkers(model.deleteMarkers);
            this.name(model.name);
            this.prefix(model.prefix);
            this.delimiter(model.delimiter);
            this.maxKeys(model.maxKeys);
            this.commonPrefixes(model.commonPrefixes);
            this.encodingType(model.encodingType);
            this.requestCharged(model.requestCharged);
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

        public final String getKeyMarker() {
            return this.keyMarker;
        }

        public final void setKeyMarker(String keyMarker) {
            this.keyMarker = keyMarker;
        }

        @Override
        public final Builder keyMarker(String keyMarker) {
            this.keyMarker = keyMarker;
            return this;
        }

        public final String getVersionIdMarker() {
            return this.versionIdMarker;
        }

        public final void setVersionIdMarker(String versionIdMarker) {
            this.versionIdMarker = versionIdMarker;
        }

        @Override
        public final Builder versionIdMarker(String versionIdMarker) {
            this.versionIdMarker = versionIdMarker;
            return this;
        }

        public final String getNextKeyMarker() {
            return this.nextKeyMarker;
        }

        public final void setNextKeyMarker(String nextKeyMarker) {
            this.nextKeyMarker = nextKeyMarker;
        }

        @Override
        public final Builder nextKeyMarker(String nextKeyMarker) {
            this.nextKeyMarker = nextKeyMarker;
            return this;
        }

        public final String getNextVersionIdMarker() {
            return this.nextVersionIdMarker;
        }

        public final void setNextVersionIdMarker(String nextVersionIdMarker) {
            this.nextVersionIdMarker = nextVersionIdMarker;
        }

        @Override
        public final Builder nextVersionIdMarker(String nextVersionIdMarker) {
            this.nextVersionIdMarker = nextVersionIdMarker;
            return this;
        }

        public final List<ObjectVersion.Builder> getVersions() {
            List<ObjectVersion.Builder> result = ObjectVersionListCopier.copyToBuilder(this.versions);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setVersions(Collection<ObjectVersion.BuilderImpl> versions) {
            this.versions = ObjectVersionListCopier.copyFromBuilder(versions);
        }

        @Override
        public final Builder versions(Collection<ObjectVersion> versions) {
            this.versions = ObjectVersionListCopier.copy(versions);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder versions(ObjectVersion ... versions) {
            this.versions(Arrays.asList(versions));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder versions(Consumer<ObjectVersion.Builder> ... versions) {
            this.versions(Stream.of(versions).map(c -> (ObjectVersion)((ObjectVersion.Builder)ObjectVersion.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final List<DeleteMarkerEntry.Builder> getDeleteMarkers() {
            List<DeleteMarkerEntry.Builder> result = DeleteMarkersCopier.copyToBuilder(this.deleteMarkers);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setDeleteMarkers(Collection<DeleteMarkerEntry.BuilderImpl> deleteMarkers) {
            this.deleteMarkers = DeleteMarkersCopier.copyFromBuilder(deleteMarkers);
        }

        @Override
        public final Builder deleteMarkers(Collection<DeleteMarkerEntry> deleteMarkers) {
            this.deleteMarkers = DeleteMarkersCopier.copy(deleteMarkers);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder deleteMarkers(DeleteMarkerEntry ... deleteMarkers) {
            this.deleteMarkers(Arrays.asList(deleteMarkers));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder deleteMarkers(Consumer<DeleteMarkerEntry.Builder> ... deleteMarkers) {
            this.deleteMarkers(Stream.of(deleteMarkers).map(c -> (DeleteMarkerEntry)((DeleteMarkerEntry.Builder)DeleteMarkerEntry.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final String getName() {
            return this.name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        @Override
        public final Builder name(String name) {
            this.name = name;
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

        public final String getDelimiter() {
            return this.delimiter;
        }

        public final void setDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public final Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public final Integer getMaxKeys() {
            return this.maxKeys;
        }

        public final void setMaxKeys(Integer maxKeys) {
            this.maxKeys = maxKeys;
        }

        @Override
        public final Builder maxKeys(Integer maxKeys) {
            this.maxKeys = maxKeys;
            return this;
        }

        public final List<CommonPrefix.Builder> getCommonPrefixes() {
            List<CommonPrefix.Builder> result = CommonPrefixListCopier.copyToBuilder(this.commonPrefixes);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setCommonPrefixes(Collection<CommonPrefix.BuilderImpl> commonPrefixes) {
            this.commonPrefixes = CommonPrefixListCopier.copyFromBuilder(commonPrefixes);
        }

        @Override
        public final Builder commonPrefixes(Collection<CommonPrefix> commonPrefixes) {
            this.commonPrefixes = CommonPrefixListCopier.copy(commonPrefixes);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder commonPrefixes(CommonPrefix ... commonPrefixes) {
            this.commonPrefixes(Arrays.asList(commonPrefixes));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder commonPrefixes(Consumer<CommonPrefix.Builder> ... commonPrefixes) {
            this.commonPrefixes(Stream.of(commonPrefixes).map(c -> (CommonPrefix)((CommonPrefix.Builder)CommonPrefix.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final String getEncodingType() {
            return this.encodingType;
        }

        public final void setEncodingType(String encodingType) {
            this.encodingType = encodingType;
        }

        @Override
        public final Builder encodingType(String encodingType) {
            this.encodingType = encodingType;
            return this;
        }

        @Override
        public final Builder encodingType(EncodingType encodingType) {
            this.encodingType(encodingType == null ? null : encodingType.toString());
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

        @Override
        public ListObjectVersionsResponse build() {
            return new ListObjectVersionsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListObjectVersionsResponse> {
        public Builder isTruncated(Boolean var1);

        public Builder keyMarker(String var1);

        public Builder versionIdMarker(String var1);

        public Builder nextKeyMarker(String var1);

        public Builder nextVersionIdMarker(String var1);

        public Builder versions(Collection<ObjectVersion> var1);

        public Builder versions(ObjectVersion ... var1);

        public Builder versions(Consumer<ObjectVersion.Builder> ... var1);

        public Builder deleteMarkers(Collection<DeleteMarkerEntry> var1);

        public Builder deleteMarkers(DeleteMarkerEntry ... var1);

        public Builder deleteMarkers(Consumer<DeleteMarkerEntry.Builder> ... var1);

        public Builder name(String var1);

        public Builder prefix(String var1);

        public Builder delimiter(String var1);

        public Builder maxKeys(Integer var1);

        public Builder commonPrefixes(Collection<CommonPrefix> var1);

        public Builder commonPrefixes(CommonPrefix ... var1);

        public Builder commonPrefixes(Consumer<CommonPrefix.Builder> ... var1);

        public Builder encodingType(String var1);

        public Builder encodingType(EncodingType var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);
    }
}

