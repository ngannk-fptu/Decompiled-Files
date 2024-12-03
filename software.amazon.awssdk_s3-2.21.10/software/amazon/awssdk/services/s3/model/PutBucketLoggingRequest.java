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
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
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
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.BucketLoggingStatus;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class PutBucketLoggingRequest
extends S3Request
implements ToCopyableBuilder<Builder, PutBucketLoggingRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(PutBucketLoggingRequest.getter(PutBucketLoggingRequest::bucket)).setter(PutBucketLoggingRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<BucketLoggingStatus> BUCKET_LOGGING_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("BucketLoggingStatus").getter(PutBucketLoggingRequest.getter(PutBucketLoggingRequest::bucketLoggingStatus)).setter(PutBucketLoggingRequest.setter(Builder::bucketLoggingStatus)).constructor(BucketLoggingStatus::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BucketLoggingStatus").unmarshallLocationName("BucketLoggingStatus").build(), PayloadTrait.create(), RequiredTrait.create()}).build();
    private static final SdkField<String> CONTENT_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentMD5").getter(PutBucketLoggingRequest.getter(PutBucketLoggingRequest::contentMD5)).setter(PutBucketLoggingRequest.setter(Builder::contentMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-MD5").unmarshallLocationName("Content-MD5").build()}).build();
    private static final SdkField<String> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumAlgorithm").getter(PutBucketLoggingRequest.getter(PutBucketLoggingRequest::checksumAlgorithmAsString)).setter(PutBucketLoggingRequest.setter(Builder::checksumAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-sdk-checksum-algorithm").unmarshallLocationName("x-amz-sdk-checksum-algorithm").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(PutBucketLoggingRequest.getter(PutBucketLoggingRequest::expectedBucketOwner)).setter(PutBucketLoggingRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, BUCKET_LOGGING_STATUS_FIELD, CONTENT_MD5_FIELD, CHECKSUM_ALGORITHM_FIELD, EXPECTED_BUCKET_OWNER_FIELD));
    private final String bucket;
    private final BucketLoggingStatus bucketLoggingStatus;
    private final String contentMD5;
    private final String checksumAlgorithm;
    private final String expectedBucketOwner;

    private PutBucketLoggingRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.bucketLoggingStatus = builder.bucketLoggingStatus;
        this.contentMD5 = builder.contentMD5;
        this.checksumAlgorithm = builder.checksumAlgorithm;
        this.expectedBucketOwner = builder.expectedBucketOwner;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final BucketLoggingStatus bucketLoggingStatus() {
        return this.bucketLoggingStatus;
    }

    public final String contentMD5() {
        return this.contentMD5;
    }

    public final ChecksumAlgorithm checksumAlgorithm() {
        return ChecksumAlgorithm.fromValue(this.checksumAlgorithm);
    }

    public final String checksumAlgorithmAsString() {
        return this.checksumAlgorithm;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketLoggingStatus());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumAlgorithmAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
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
        if (!(obj instanceof PutBucketLoggingRequest)) {
            return false;
        }
        PutBucketLoggingRequest other = (PutBucketLoggingRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.bucketLoggingStatus(), other.bucketLoggingStatus()) && Objects.equals(this.contentMD5(), other.contentMD5()) && Objects.equals(this.checksumAlgorithmAsString(), other.checksumAlgorithmAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner());
    }

    public final String toString() {
        return ToString.builder((String)"PutBucketLoggingRequest").add("Bucket", (Object)this.bucket()).add("BucketLoggingStatus", (Object)this.bucketLoggingStatus()).add("ContentMD5", (Object)this.contentMD5()).add("ChecksumAlgorithm", (Object)this.checksumAlgorithmAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "BucketLoggingStatus": {
                return Optional.ofNullable(clazz.cast(this.bucketLoggingStatus()));
            }
            case "ContentMD5": {
                return Optional.ofNullable(clazz.cast(this.contentMD5()));
            }
            case "ChecksumAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.checksumAlgorithmAsString()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PutBucketLoggingRequest, T> g) {
        return obj -> g.apply((PutBucketLoggingRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private BucketLoggingStatus bucketLoggingStatus;
        private String contentMD5;
        private String checksumAlgorithm;
        private String expectedBucketOwner;

        private BuilderImpl() {
        }

        private BuilderImpl(PutBucketLoggingRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.bucketLoggingStatus(model.bucketLoggingStatus);
            this.contentMD5(model.contentMD5);
            this.checksumAlgorithm(model.checksumAlgorithm);
            this.expectedBucketOwner(model.expectedBucketOwner);
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

        public final BucketLoggingStatus.Builder getBucketLoggingStatus() {
            return this.bucketLoggingStatus != null ? this.bucketLoggingStatus.toBuilder() : null;
        }

        public final void setBucketLoggingStatus(BucketLoggingStatus.BuilderImpl bucketLoggingStatus) {
            this.bucketLoggingStatus = bucketLoggingStatus != null ? bucketLoggingStatus.build() : null;
        }

        @Override
        public final Builder bucketLoggingStatus(BucketLoggingStatus bucketLoggingStatus) {
            this.bucketLoggingStatus = bucketLoggingStatus;
            return this;
        }

        public final String getContentMD5() {
            return this.contentMD5;
        }

        public final void setContentMD5(String contentMD5) {
            this.contentMD5 = contentMD5;
        }

        @Override
        public final Builder contentMD5(String contentMD5) {
            this.contentMD5 = contentMD5;
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
        public PutBucketLoggingRequest build() {
            return new PutBucketLoggingRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, PutBucketLoggingRequest> {
        public Builder bucket(String var1);

        public Builder bucketLoggingStatus(BucketLoggingStatus var1);

        default public Builder bucketLoggingStatus(Consumer<BucketLoggingStatus.Builder> bucketLoggingStatus) {
            return this.bucketLoggingStatus((BucketLoggingStatus)((BucketLoggingStatus.Builder)BucketLoggingStatus.builder().applyMutation(bucketLoggingStatus)).build());
        }

        public Builder contentMD5(String var1);

        public Builder checksumAlgorithm(String var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm var1);

        public Builder expectedBucketOwner(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

