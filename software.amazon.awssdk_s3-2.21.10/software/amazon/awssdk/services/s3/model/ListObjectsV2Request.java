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

public final class ListObjectsV2Request
extends S3Request
implements ToCopyableBuilder<Builder, ListObjectsV2Request> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(ListObjectsV2Request.getter(ListObjectsV2Request::bucket)).setter(ListObjectsV2Request.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Delimiter").getter(ListObjectsV2Request.getter(ListObjectsV2Request::delimiter)).setter(ListObjectsV2Request.setter(Builder::delimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("delimiter").unmarshallLocationName("delimiter").build()}).build();
    private static final SdkField<String> ENCODING_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncodingType").getter(ListObjectsV2Request.getter(ListObjectsV2Request::encodingTypeAsString)).setter(ListObjectsV2Request.setter(Builder::encodingType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("encoding-type").unmarshallLocationName("encoding-type").build()}).build();
    private static final SdkField<Integer> MAX_KEYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxKeys").getter(ListObjectsV2Request.getter(ListObjectsV2Request::maxKeys)).setter(ListObjectsV2Request.setter(Builder::maxKeys)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("max-keys").unmarshallLocationName("max-keys").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(ListObjectsV2Request.getter(ListObjectsV2Request::prefix)).setter(ListObjectsV2Request.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("prefix").unmarshallLocationName("prefix").build()}).build();
    private static final SdkField<String> CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContinuationToken").getter(ListObjectsV2Request.getter(ListObjectsV2Request::continuationToken)).setter(ListObjectsV2Request.setter(Builder::continuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("continuation-token").unmarshallLocationName("continuation-token").build()}).build();
    private static final SdkField<Boolean> FETCH_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("FetchOwner").getter(ListObjectsV2Request.getter(ListObjectsV2Request::fetchOwner)).setter(ListObjectsV2Request.setter(Builder::fetchOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("fetch-owner").unmarshallLocationName("fetch-owner").build()}).build();
    private static final SdkField<String> START_AFTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StartAfter").getter(ListObjectsV2Request.getter(ListObjectsV2Request::startAfter)).setter(ListObjectsV2Request.setter(Builder::startAfter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("start-after").unmarshallLocationName("start-after").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(ListObjectsV2Request.getter(ListObjectsV2Request::requestPayerAsString)).setter(ListObjectsV2Request.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(ListObjectsV2Request.getter(ListObjectsV2Request::expectedBucketOwner)).setter(ListObjectsV2Request.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<List<String>> OPTIONAL_OBJECT_ATTRIBUTES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("OptionalObjectAttributes").getter(ListObjectsV2Request.getter(ListObjectsV2Request::optionalObjectAttributesAsStrings)).setter(ListObjectsV2Request.setter(Builder::optionalObjectAttributesWithStrings)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-optional-object-attributes").unmarshallLocationName("x-amz-optional-object-attributes").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, DELIMITER_FIELD, ENCODING_TYPE_FIELD, MAX_KEYS_FIELD, PREFIX_FIELD, CONTINUATION_TOKEN_FIELD, FETCH_OWNER_FIELD, START_AFTER_FIELD, REQUEST_PAYER_FIELD, EXPECTED_BUCKET_OWNER_FIELD, OPTIONAL_OBJECT_ATTRIBUTES_FIELD));
    private final String bucket;
    private final String delimiter;
    private final String encodingType;
    private final Integer maxKeys;
    private final String prefix;
    private final String continuationToken;
    private final Boolean fetchOwner;
    private final String startAfter;
    private final String requestPayer;
    private final String expectedBucketOwner;
    private final List<String> optionalObjectAttributes;

    private ListObjectsV2Request(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.delimiter = builder.delimiter;
        this.encodingType = builder.encodingType;
        this.maxKeys = builder.maxKeys;
        this.prefix = builder.prefix;
        this.continuationToken = builder.continuationToken;
        this.fetchOwner = builder.fetchOwner;
        this.startAfter = builder.startAfter;
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

    public final Integer maxKeys() {
        return this.maxKeys;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final String continuationToken() {
        return this.continuationToken;
    }

    public final Boolean fetchOwner() {
        return this.fetchOwner;
    }

    public final String startAfter() {
        return this.startAfter;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.maxKeys());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.continuationToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.fetchOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.startAfter());
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
        if (!(obj instanceof ListObjectsV2Request)) {
            return false;
        }
        ListObjectsV2Request other = (ListObjectsV2Request)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.delimiter(), other.delimiter()) && Objects.equals(this.encodingTypeAsString(), other.encodingTypeAsString()) && Objects.equals(this.maxKeys(), other.maxKeys()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.continuationToken(), other.continuationToken()) && Objects.equals(this.fetchOwner(), other.fetchOwner()) && Objects.equals(this.startAfter(), other.startAfter()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && this.hasOptionalObjectAttributes() == other.hasOptionalObjectAttributes() && Objects.equals(this.optionalObjectAttributesAsStrings(), other.optionalObjectAttributesAsStrings());
    }

    public final String toString() {
        return ToString.builder((String)"ListObjectsV2Request").add("Bucket", (Object)this.bucket()).add("Delimiter", (Object)this.delimiter()).add("EncodingType", (Object)this.encodingTypeAsString()).add("MaxKeys", (Object)this.maxKeys()).add("Prefix", (Object)this.prefix()).add("ContinuationToken", (Object)this.continuationToken()).add("FetchOwner", (Object)this.fetchOwner()).add("StartAfter", (Object)this.startAfter()).add("RequestPayer", (Object)this.requestPayerAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("OptionalObjectAttributes", this.hasOptionalObjectAttributes() ? this.optionalObjectAttributesAsStrings() : null).build();
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
            case "MaxKeys": {
                return Optional.ofNullable(clazz.cast(this.maxKeys()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "ContinuationToken": {
                return Optional.ofNullable(clazz.cast(this.continuationToken()));
            }
            case "FetchOwner": {
                return Optional.ofNullable(clazz.cast(this.fetchOwner()));
            }
            case "StartAfter": {
                return Optional.ofNullable(clazz.cast(this.startAfter()));
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

    private static <T> Function<Object, T> getter(Function<ListObjectsV2Request, T> g) {
        return obj -> g.apply((ListObjectsV2Request)((Object)((Object)obj)));
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
        private Integer maxKeys;
        private String prefix;
        private String continuationToken;
        private Boolean fetchOwner;
        private String startAfter;
        private String requestPayer;
        private String expectedBucketOwner;
        private List<String> optionalObjectAttributes = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ListObjectsV2Request model) {
            super(model);
            this.bucket(model.bucket);
            this.delimiter(model.delimiter);
            this.encodingType(model.encodingType);
            this.maxKeys(model.maxKeys);
            this.prefix(model.prefix);
            this.continuationToken(model.continuationToken);
            this.fetchOwner(model.fetchOwner);
            this.startAfter(model.startAfter);
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

        public final String getContinuationToken() {
            return this.continuationToken;
        }

        public final void setContinuationToken(String continuationToken) {
            this.continuationToken = continuationToken;
        }

        @Override
        public final Builder continuationToken(String continuationToken) {
            this.continuationToken = continuationToken;
            return this;
        }

        public final Boolean getFetchOwner() {
            return this.fetchOwner;
        }

        public final void setFetchOwner(Boolean fetchOwner) {
            this.fetchOwner = fetchOwner;
        }

        @Override
        public final Builder fetchOwner(Boolean fetchOwner) {
            this.fetchOwner = fetchOwner;
            return this;
        }

        public final String getStartAfter() {
            return this.startAfter;
        }

        public final void setStartAfter(String startAfter) {
            this.startAfter = startAfter;
        }

        @Override
        public final Builder startAfter(String startAfter) {
            this.startAfter = startAfter;
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
        public ListObjectsV2Request build() {
            return new ListObjectsV2Request(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListObjectsV2Request> {
        public Builder bucket(String var1);

        public Builder delimiter(String var1);

        public Builder encodingType(String var1);

        public Builder encodingType(EncodingType var1);

        public Builder maxKeys(Integer var1);

        public Builder prefix(String var1);

        public Builder continuationToken(String var1);

        public Builder fetchOwner(Boolean var1);

        public Builder startAfter(String var1);

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

