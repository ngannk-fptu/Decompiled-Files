/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
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
import software.amazon.awssdk.services.s3.model.EncodingType;
import software.amazon.awssdk.services.s3.model.OptionalObjectAttributes;
import software.amazon.awssdk.services.s3.model.OptionalObjectAttributesListCopier;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListObjectsRequest
extends S3Request
implements ToCopyableBuilder<Builder, ListObjectsRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(ListObjectsRequest.getter(ListObjectsRequest::bucket)).setter(ListObjectsRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Delimiter").getter(ListObjectsRequest.getter(ListObjectsRequest::delimiter)).setter(ListObjectsRequest.setter(Builder::delimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("delimiter").unmarshallLocationName("delimiter").build()}).build();
    private static final SdkField<String> ENCODING_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncodingType").getter(ListObjectsRequest.getter(ListObjectsRequest::encodingTypeAsString)).setter(ListObjectsRequest.setter(Builder::encodingType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("encoding-type").unmarshallLocationName("encoding-type").build()}).build();
    private static final SdkField<String> MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Marker").getter(ListObjectsRequest.getter(ListObjectsRequest::marker)).setter(ListObjectsRequest.setter(Builder::marker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("marker").unmarshallLocationName("marker").build()}).build();
    private static final SdkField<Integer> MAX_KEYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxKeys").getter(ListObjectsRequest.getter(ListObjectsRequest::maxKeys)).setter(ListObjectsRequest.setter(Builder::maxKeys)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("max-keys").unmarshallLocationName("max-keys").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(ListObjectsRequest.getter(ListObjectsRequest::prefix)).setter(ListObjectsRequest.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("prefix").unmarshallLocationName("prefix").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(ListObjectsRequest.getter(ListObjectsRequest::requestPayerAsString)).setter(ListObjectsRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(ListObjectsRequest.getter(ListObjectsRequest::expectedBucketOwner)).setter(ListObjectsRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<List<String>> OPTIONAL_OBJECT_ATTRIBUTES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("OptionalObjectAttributes").getter(ListObjectsRequest.getter(ListObjectsRequest::optionalObjectAttributesAsStrings)).setter(ListObjectsRequest.setter(Builder::optionalObjectAttributesWithStrings)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-optional-object-attributes").unmarshallLocationName("x-amz-optional-object-attributes").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, DELIMITER_FIELD, ENCODING_TYPE_FIELD, MARKER_FIELD, MAX_KEYS_FIELD, PREFIX_FIELD, REQUEST_PAYER_FIELD, EXPECTED_BUCKET_OWNER_FIELD, OPTIONAL_OBJECT_ATTRIBUTES_FIELD));
    private final String bucket;
    private final String delimiter;
    private final String encodingType;
    private final String marker;
    private final Integer maxKeys;
    private final String prefix;
    private final String requestPayer;
    private final String expectedBucketOwner;
    private final List<String> optionalObjectAttributes;

    private ListObjectsRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.delimiter = builder.delimiter;
        this.encodingType = builder.encodingType;
        this.marker = builder.marker;
        this.maxKeys = builder.maxKeys;
        this.prefix = builder.prefix;
        this.requestPayer = builder.requestPayer;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.optionalObjectAttributes = builder.optionalObjectAttributes;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String delimiter() {
        return this.delimiter;
    }

    public final EncodingType encodingType() {
        return EncodingType.fromValue(this.encodingType);
    }

    public final String encodingTypeAsString() {
        return this.encodingType;
    }

    public final String marker() {
        return this.marker;
    }

    public final Integer maxKeys() {
        return this.maxKeys;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final RequestPayer requestPayer() {
        return RequestPayer.fromValue(this.requestPayer);
    }

    public final String requestPayerAsString() {
        return this.requestPayer;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    public final List<OptionalObjectAttributes> optionalObjectAttributes() {
        return OptionalObjectAttributesListCopier.copyStringToEnum(this.optionalObjectAttributes);
    }

    public final boolean hasOptionalObjectAttributes() {
        return this.optionalObjectAttributes != null && !(this.optionalObjectAttributes instanceof SdkAutoConstructList);
    }

    public final List<String> optionalObjectAttributesAsStrings() {
        return this.optionalObjectAttributes;
    }

    @Override
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.delimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.encodingTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.marker());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxKeys());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasOptionalObjectAttributes() ? this.optionalObjectAttributesAsStrings() : null);
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
        if (!(obj instanceof ListObjectsRequest)) {
            return false;
        }
        ListObjectsRequest other = (ListObjectsRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.delimiter(), other.delimiter()) && Objects.equals(this.encodingTypeAsString(), other.encodingTypeAsString()) && Objects.equals(this.marker(), other.marker()) && Objects.equals(this.maxKeys(), other.maxKeys()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && this.hasOptionalObjectAttributes() == other.hasOptionalObjectAttributes() && Objects.equals(this.optionalObjectAttributesAsStrings(), other.optionalObjectAttributesAsStrings());
    }

    public final String toString() {
        return ToString.builder((String)"ListObjectsRequest").add("Bucket", (Object)this.bucket()).add("Delimiter", (Object)this.delimiter()).add("EncodingType", (Object)this.encodingTypeAsString()).add("Marker", (Object)this.marker()).add("MaxKeys", (Object)this.maxKeys()).add("Prefix", (Object)this.prefix()).add("RequestPayer", (Object)this.requestPayerAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("OptionalObjectAttributes", this.hasOptionalObjectAttributes() ? this.optionalObjectAttributesAsStrings() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Delimiter": {
                return Optional.ofNullable(clazz.cast(this.delimiter()));
            }
            case "EncodingType": {
                return Optional.ofNullable(clazz.cast(this.encodingTypeAsString()));
            }
            case "Marker": {
                return Optional.ofNullable(clazz.cast(this.marker()));
            }
            case "MaxKeys": {
                return Optional.ofNullable(clazz.cast(this.maxKeys()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "RequestPayer": {
                return Optional.ofNullable(clazz.cast(this.requestPayerAsString()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
            case "OptionalObjectAttributes": {
                return Optional.ofNullable(clazz.cast(this.optionalObjectAttributesAsStrings()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListObjectsRequest, T> g) {
        return obj -> g.apply((ListObjectsRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String delimiter;
        private String encodingType;
        private String marker;
        private Integer maxKeys;
        private String prefix;
        private String requestPayer;
        private String expectedBucketOwner;
        private List<String> optionalObjectAttributes = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ListObjectsRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.delimiter(model.delimiter);
            this.encodingType(model.encodingType);
            this.marker(model.marker);
            this.maxKeys(model.maxKeys);
            this.prefix(model.prefix);
            this.requestPayer(model.requestPayer);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.optionalObjectAttributesWithStrings(model.optionalObjectAttributes);
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

        public final String getRequestPayer() {
            return this.requestPayer;
        }

        public final void setRequestPayer(String requestPayer) {
            this.requestPayer = requestPayer;
        }

        @Override
        public final Builder requestPayer(String requestPayer) {
            this.requestPayer = requestPayer;
            return this;
        }

        @Override
        public final Builder requestPayer(RequestPayer requestPayer) {
            this.requestPayer(requestPayer == null ? null : requestPayer.toString());
            return this;
        }

        public final String getExpectedBucketOwner() {
            return this.expectedBucketOwner;
        }

        public final void setExpectedBucketOwner(String expectedBucketOwner) {
            this.expectedBucketOwner = expectedBucketOwner;
        }

        @Override
        public final Builder expectedBucketOwner(String expectedBucketOwner) {
            this.expectedBucketOwner = expectedBucketOwner;
            return this;
        }

        public final Collection<String> getOptionalObjectAttributes() {
            if (this.optionalObjectAttributes instanceof SdkAutoConstructList) {
                return null;
            }
            return this.optionalObjectAttributes;
        }

        public final void setOptionalObjectAttributes(Collection<String> optionalObjectAttributes) {
            this.optionalObjectAttributes = OptionalObjectAttributesListCopier.copy(optionalObjectAttributes);
        }

        @Override
        public final Builder optionalObjectAttributesWithStrings(Collection<String> optionalObjectAttributes) {
            this.optionalObjectAttributes = OptionalObjectAttributesListCopier.copy(optionalObjectAttributes);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder optionalObjectAttributesWithStrings(String ... optionalObjectAttributes) {
            this.optionalObjectAttributesWithStrings(Arrays.asList(optionalObjectAttributes));
            return this;
        }

        @Override
        public final Builder optionalObjectAttributes(Collection<OptionalObjectAttributes> optionalObjectAttributes) {
            this.optionalObjectAttributes = OptionalObjectAttributesListCopier.copyEnumToString(optionalObjectAttributes);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder optionalObjectAttributes(OptionalObjectAttributes ... optionalObjectAttributes) {
            this.optionalObjectAttributes(Arrays.asList(optionalObjectAttributes));
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public ListObjectsRequest build() {
            return new ListObjectsRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListObjectsRequest> {
        public Builder bucket(String var1);

        public Builder delimiter(String var1);

        public Builder encodingType(String var1);

        public Builder encodingType(EncodingType var1);

        public Builder marker(String var1);

        public Builder maxKeys(Integer var1);

        public Builder prefix(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder expectedBucketOwner(String var1);

        public Builder optionalObjectAttributesWithStrings(Collection<String> var1);

        public Builder optionalObjectAttributesWithStrings(String ... var1);

        public Builder optionalObjectAttributes(Collection<OptionalObjectAttributes> var1);

        public Builder optionalObjectAttributes(OptionalObjectAttributes ... var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

