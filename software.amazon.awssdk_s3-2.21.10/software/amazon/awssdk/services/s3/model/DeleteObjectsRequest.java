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
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DeleteObjectsRequest
extends S3Request
implements ToCopyableBuilder<Builder, DeleteObjectsRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(DeleteObjectsRequest.getter(DeleteObjectsRequest::bucket)).setter(DeleteObjectsRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<Delete> DELETE_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Delete").getter(DeleteObjectsRequest.getter(DeleteObjectsRequest::delete)).setter(DeleteObjectsRequest.setter(Builder::delete)).constructor(Delete::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Delete").unmarshallLocationName("Delete").build(), PayloadTrait.create(), RequiredTrait.create()}).build();
    private static final SdkField<String> MFA_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("MFA").getter(DeleteObjectsRequest.getter(DeleteObjectsRequest::mfa)).setter(DeleteObjectsRequest.setter(Builder::mfa)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-mfa").unmarshallLocationName("x-amz-mfa").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(DeleteObjectsRequest.getter(DeleteObjectsRequest::requestPayerAsString)).setter(DeleteObjectsRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<Boolean> BYPASS_GOVERNANCE_RETENTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BypassGovernanceRetention").getter(DeleteObjectsRequest.getter(DeleteObjectsRequest::bypassGovernanceRetention)).setter(DeleteObjectsRequest.setter(Builder::bypassGovernanceRetention)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-bypass-governance-retention").unmarshallLocationName("x-amz-bypass-governance-retention").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(DeleteObjectsRequest.getter(DeleteObjectsRequest::expectedBucketOwner)).setter(DeleteObjectsRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<String> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumAlgorithm").getter(DeleteObjectsRequest.getter(DeleteObjectsRequest::checksumAlgorithmAsString)).setter(DeleteObjectsRequest.setter(Builder::checksumAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-sdk-checksum-algorithm").unmarshallLocationName("x-amz-sdk-checksum-algorithm").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, DELETE_FIELD, MFA_FIELD, REQUEST_PAYER_FIELD, BYPASS_GOVERNANCE_RETENTION_FIELD, EXPECTED_BUCKET_OWNER_FIELD, CHECKSUM_ALGORITHM_FIELD));
    private final String bucket;
    private final Delete delete;
    private final String mfa;
    private final String requestPayer;
    private final Boolean bypassGovernanceRetention;
    private final String expectedBucketOwner;
    private final String checksumAlgorithm;

    private DeleteObjectsRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.delete = builder.delete;
        this.mfa = builder.mfa;
        this.requestPayer = builder.requestPayer;
        this.bypassGovernanceRetention = builder.bypassGovernanceRetention;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.checksumAlgorithm = builder.checksumAlgorithm;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final Delete delete() {
        return this.delete;
    }

    public final String mfa() {
        return this.mfa;
    }

    public final RequestPayer requestPayer() {
        return RequestPayer.fromValue(this.requestPayer);
    }

    public final String requestPayerAsString() {
        return this.requestPayer;
    }

    public final Boolean bypassGovernanceRetention() {
        return this.bypassGovernanceRetention;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    public final ChecksumAlgorithm checksumAlgorithm() {
        return ChecksumAlgorithm.fromValue(this.checksumAlgorithm);
    }

    public final String checksumAlgorithmAsString() {
        return this.checksumAlgorithm;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.delete());
        hashCode = 31 * hashCode + Objects.hashCode(this.mfa());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.bypassGovernanceRetention());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
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
        if (!(obj instanceof DeleteObjectsRequest)) {
            return false;
        }
        DeleteObjectsRequest other = (DeleteObjectsRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.delete(), other.delete()) && Objects.equals(this.mfa(), other.mfa()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.bypassGovernanceRetention(), other.bypassGovernanceRetention()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.checksumAlgorithmAsString(), other.checksumAlgorithmAsString());
    }

    public final String toString() {
        return ToString.builder((String)"DeleteObjectsRequest").add("Bucket", (Object)this.bucket()).add("Delete", (Object)this.delete()).add("MFA", (Object)this.mfa()).add("RequestPayer", (Object)this.requestPayerAsString()).add("BypassGovernanceRetention", (Object)this.bypassGovernanceRetention()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("ChecksumAlgorithm", (Object)this.checksumAlgorithmAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Delete": {
                return Optional.ofNullable(clazz.cast(this.delete()));
            }
            case "MFA": {
                return Optional.ofNullable(clazz.cast(this.mfa()));
            }
            case "RequestPayer": {
                return Optional.ofNullable(clazz.cast(this.requestPayerAsString()));
            }
            case "BypassGovernanceRetention": {
                return Optional.ofNullable(clazz.cast(this.bypassGovernanceRetention()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
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

    private static <T> Function<Object, T> getter(Function<DeleteObjectsRequest, T> g) {
        return obj -> g.apply((DeleteObjectsRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private Delete delete;
        private String mfa;
        private String requestPayer;
        private Boolean bypassGovernanceRetention;
        private String expectedBucketOwner;
        private String checksumAlgorithm;

        private BuilderImpl() {
        }

        private BuilderImpl(DeleteObjectsRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.delete(model.delete);
            this.mfa(model.mfa);
            this.requestPayer(model.requestPayer);
            this.bypassGovernanceRetention(model.bypassGovernanceRetention);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.checksumAlgorithm(model.checksumAlgorithm);
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

        public final Delete.Builder getDelete() {
            return this.delete != null ? this.delete.toBuilder() : null;
        }

        public final void setDelete(Delete.BuilderImpl delete) {
            this.delete = delete != null ? delete.build() : null;
        }

        @Override
        public final Builder delete(Delete delete) {
            this.delete = delete;
            return this;
        }

        public final String getMfa() {
            return this.mfa;
        }

        public final void setMfa(String mfa) {
            this.mfa = mfa;
        }

        @Override
        public final Builder mfa(String mfa) {
            this.mfa = mfa;
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

        public final Boolean getBypassGovernanceRetention() {
            return this.bypassGovernanceRetention;
        }

        public final void setBypassGovernanceRetention(Boolean bypassGovernanceRetention) {
            this.bypassGovernanceRetention = bypassGovernanceRetention;
        }

        @Override
        public final Builder bypassGovernanceRetention(Boolean bypassGovernanceRetention) {
            this.bypassGovernanceRetention = bypassGovernanceRetention;
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
        public DeleteObjectsRequest build() {
            return new DeleteObjectsRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DeleteObjectsRequest> {
        public Builder bucket(String var1);

        public Builder delete(Delete var1);

        default public Builder delete(Consumer<Delete.Builder> delete) {
            return this.delete((Delete)((Delete.Builder)Delete.builder().applyMutation(delete)).build());
        }

        public Builder mfa(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder bypassGovernanceRetention(Boolean var1);

        public Builder expectedBucketOwner(String var1);

        public Builder checksumAlgorithm(String var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

