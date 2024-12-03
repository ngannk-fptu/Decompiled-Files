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
import software.amazon.awssdk.services.s3.model.EncodingType;
import software.amazon.awssdk.services.s3.model.ObjectListCopier;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListObjectsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListObjectsResponse> {
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListObjectsResponse.getter(ListObjectsResponse::isTruncated)).setter(ListObjectsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<String> MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Marker").getter(ListObjectsResponse.getter(ListObjectsResponse::marker)).setter(ListObjectsResponse.setter(Builder::marker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Marker").unmarshallLocationName("Marker").build()}).build();
    private static final SdkField<String> NEXT_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextMarker").getter(ListObjectsResponse.getter(ListObjectsResponse::nextMarker)).setter(ListObjectsResponse.setter(Builder::nextMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextMarker").unmarshallLocationName("NextMarker").build()}).build();
    private static final SdkField<List<S3Object>> CONTENTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Contents").getter(ListObjectsResponse.getter(ListObjectsResponse::contents)).setter(ListObjectsResponse.setter(Builder::contents)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Contents").unmarshallLocationName("Contents").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(S3Object::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(ListObjectsResponse.getter(ListObjectsResponse::name)).setter(ListObjectsResponse.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").unmarshallLocationName("Name").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(ListObjectsResponse.getter(ListObjectsResponse::prefix)).setter(ListObjectsResponse.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<String> DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Delimiter").getter(ListObjectsResponse.getter(ListObjectsResponse::delimiter)).setter(ListObjectsResponse.setter(Builder::delimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Delimiter").unmarshallLocationName("Delimiter").build()}).build();
    private static final SdkField<Integer> MAX_KEYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxKeys").getter(ListObjectsResponse.getter(ListObjectsResponse::maxKeys)).setter(ListObjectsResponse.setter(Builder::maxKeys)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxKeys").unmarshallLocationName("MaxKeys").build()}).build();
    private static final SdkField<List<CommonPrefix>> COMMON_PREFIXES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("CommonPrefixes").getter(ListObjectsResponse.getter(ListObjectsResponse::commonPrefixes)).setter(ListObjectsResponse.setter(Builder::commonPrefixes)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CommonPrefixes").unmarshallLocationName("CommonPrefixes").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(CommonPrefix::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<String> ENCODING_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncodingType").getter(ListObjectsResponse.getter(ListObjectsResponse::encodingTypeAsString)).setter(ListObjectsResponse.setter(Builder::encodingType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EncodingType").unmarshallLocationName("EncodingType").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(ListObjectsResponse.getter(ListObjectsResponse::requestChargedAsString)).setter(ListObjectsResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(IS_TRUNCATED_FIELD, MARKER_FIELD, NEXT_MARKER_FIELD, CONTENTS_FIELD, NAME_FIELD, PREFIX_FIELD, DELIMITER_FIELD, MAX_KEYS_FIELD, COMMON_PREFIXES_FIELD, ENCODING_TYPE_FIELD, REQUEST_CHARGED_FIELD));
    private final Boolean isTruncated;
    private final String marker;
    private final String nextMarker;
    private final List<S3Object> contents;
    private final String name;
    private final String prefix;
    private final String delimiter;
    private final Integer maxKeys;
    private final List<CommonPrefix> commonPrefixes;
    private final String encodingType;
    private final String requestCharged;

    private ListObjectsResponse(BuilderImpl builder) {
        super(builder);
        this.isTruncated = builder.isTruncated;
        this.marker = builder.marker;
        this.nextMarker = builder.nextMarker;
        this.contents = builder.contents;
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

    public final String marker() {
        return this.marker;
    }

    public final String nextMarker() {
        return this.nextMarker;
    }

    public final boolean hasContents() {
        return this.contents != null && !(this.contents instanceof SdkAutoConstructList);
    }

    public final List<S3Object> contents() {
        return this.contents;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.marker());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasContents() ? this.contents() : null);
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
        if (!(obj instanceof ListObjectsResponse)) {
            return false;
        }
        ListObjectsResponse other = (ListObjectsResponse)((Object)obj);
        return Objects.equals(this.isTruncated(), other.isTruncated()) && Objects.equals(this.marker(), other.marker()) && Objects.equals(this.nextMarker(), other.nextMarker()) && this.hasContents() == other.hasContents() && Objects.equals(this.contents(), other.contents()) && Objects.equals(this.name(), other.name()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.delimiter(), other.delimiter()) && Objects.equals(this.maxKeys(), other.maxKeys()) && this.hasCommonPrefixes() == other.hasCommonPrefixes() && Objects.equals(this.commonPrefixes(), other.commonPrefixes()) && Objects.equals(this.encodingTypeAsString(), other.encodingTypeAsString()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString());
    }

    public final String toString() {
        return ToString.builder((String)"ListObjectsResponse").add("IsTruncated", (Object)this.isTruncated()).add("Marker", (Object)this.marker()).add("NextMarker", (Object)this.nextMarker()).add("Contents", this.hasContents() ? this.contents() : null).add("Name", (Object)this.name()).add("Prefix", (Object)this.prefix()).add("Delimiter", (Object)this.delimiter()).add("MaxKeys", (Object)this.maxKeys()).add("CommonPrefixes", this.hasCommonPrefixes() ? this.commonPrefixes() : null).add("EncodingType", (Object)this.encodingTypeAsString()).add("RequestCharged", (Object)this.requestChargedAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "IsTruncated": {
                return Optional.ofNullable(clazz.cast(this.isTruncated()));
            }
            case "Marker": {
                return Optional.ofNullable(clazz.cast(this.marker()));
            }
            case "NextMarker": {
                return Optional.ofNullable(clazz.cast(this.nextMarker()));
            }
            case "Contents": {
                return Optional.ofNullable(clazz.cast(this.contents()));
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

    private static <T> Function<Object, T> getter(Function<ListObjectsResponse, T> g) {
        return obj -> g.apply((ListObjectsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private Boolean isTruncated;
        private String marker;
        private String nextMarker;
        private List<S3Object> contents = DefaultSdkAutoConstructList.getInstance();
        private String name;
        private String prefix;
        private String delimiter;
        private Integer maxKeys;
        private List<CommonPrefix> commonPrefixes = DefaultSdkAutoConstructList.getInstance();
        private String encodingType;
        private String requestCharged;

        private BuilderImpl() {
        }

        private BuilderImpl(ListObjectsResponse model) {
            super(model);
            this.isTruncated(model.isTruncated);
            this.marker(model.marker);
            this.nextMarker(model.nextMarker);
            this.contents(model.contents);
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

        public final String getMarker() {
            return this.marker;
        }

        public final void setMarker(String marker) {
            this.marker = marker;
        }

        @Override
        public final Builder marker(String marker) {
            this.marker = marker;
            return this;
        }

        public final String getNextMarker() {
            return this.nextMarker;
        }

        public final void setNextMarker(String nextMarker) {
            this.nextMarker = nextMarker;
        }

        @Override
        public final Builder nextMarker(String nextMarker) {
            this.nextMarker = nextMarker;
            return this;
        }

        public final List<S3Object.Builder> getContents() {
            List<S3Object.Builder> result = ObjectListCopier.copyToBuilder(this.contents);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setContents(Collection<S3Object.BuilderImpl> contents) {
            this.contents = ObjectListCopier.copyFromBuilder(contents);
        }

        @Override
        public final Builder contents(Collection<S3Object> contents) {
            this.contents = ObjectListCopier.copy(contents);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder contents(S3Object ... contents) {
            this.contents(Arrays.asList(contents));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder contents(Consumer<S3Object.Builder> ... contents) {
            this.contents(Stream.of(contents).map(c -> (S3Object)((S3Object.Builder)S3Object.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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
        public ListObjectsResponse build() {
            return new ListObjectsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListObjectsResponse> {
        public Builder isTruncated(Boolean var1);

        public Builder marker(String var1);

        public Builder nextMarker(String var1);

        public Builder contents(Collection<S3Object> var1);

        public Builder contents(S3Object ... var1);

        public Builder contents(Consumer<S3Object.Builder> ... var1);

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

