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
import software.amazon.awssdk.services.s3.model.AccessControlPolicy;
import software.amazon.awssdk.services.s3.model.BucketCannedACL;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class PutBucketAclRequest
extends S3Request
implements ToCopyableBuilder<Builder, PutBucketAclRequest> {
    private static final SdkField<String> ACL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ACL").getter(PutBucketAclRequest.getter(PutBucketAclRequest::aclAsString)).setter(PutBucketAclRequest.setter(Builder::acl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-acl").unmarshallLocationName("x-amz-acl").build()}).build();
    private static final SdkField<AccessControlPolicy> ACCESS_CONTROL_POLICY_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("AccessControlPolicy").getter(PutBucketAclRequest.getter(PutBucketAclRequest::accessControlPolicy)).setter(PutBucketAclRequest.setter(Builder::accessControlPolicy)).constructor(AccessControlPolicy::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessControlPolicy").unmarshallLocationName("AccessControlPolicy").build(), PayloadTrait.create()}).build();
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(PutBucketAclRequest.getter(PutBucketAclRequest::bucket)).setter(PutBucketAclRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> CONTENT_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentMD5").getter(PutBucketAclRequest.getter(PutBucketAclRequest::contentMD5)).setter(PutBucketAclRequest.setter(Builder::contentMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-MD5").unmarshallLocationName("Content-MD5").build()}).build();
    private static final SdkField<String> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumAlgorithm").getter(PutBucketAclRequest.getter(PutBucketAclRequest::checksumAlgorithmAsString)).setter(PutBucketAclRequest.setter(Builder::checksumAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-sdk-checksum-algorithm").unmarshallLocationName("x-amz-sdk-checksum-algorithm").build()}).build();
    private static final SdkField<String> GRANT_FULL_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantFullControl").getter(PutBucketAclRequest.getter(PutBucketAclRequest::grantFullControl)).setter(PutBucketAclRequest.setter(Builder::grantFullControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-full-control").unmarshallLocationName("x-amz-grant-full-control").build()}).build();
    private static final SdkField<String> GRANT_READ_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantRead").getter(PutBucketAclRequest.getter(PutBucketAclRequest::grantRead)).setter(PutBucketAclRequest.setter(Builder::grantRead)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read").unmarshallLocationName("x-amz-grant-read").build()}).build();
    private static final SdkField<String> GRANT_READ_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantReadACP").getter(PutBucketAclRequest.getter(PutBucketAclRequest::grantReadACP)).setter(PutBucketAclRequest.setter(Builder::grantReadACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read-acp").unmarshallLocationName("x-amz-grant-read-acp").build()}).build();
    private static final SdkField<String> GRANT_WRITE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantWrite").getter(PutBucketAclRequest.getter(PutBucketAclRequest::grantWrite)).setter(PutBucketAclRequest.setter(Builder::grantWrite)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-write").unmarshallLocationName("x-amz-grant-write").build()}).build();
    private static final SdkField<String> GRANT_WRITE_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantWriteACP").getter(PutBucketAclRequest.getter(PutBucketAclRequest::grantWriteACP)).setter(PutBucketAclRequest.setter(Builder::grantWriteACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-write-acp").unmarshallLocationName("x-amz-grant-write-acp").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(PutBucketAclRequest.getter(PutBucketAclRequest::expectedBucketOwner)).setter(PutBucketAclRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACL_FIELD, ACCESS_CONTROL_POLICY_FIELD, BUCKET_FIELD, CONTENT_MD5_FIELD, CHECKSUM_ALGORITHM_FIELD, GRANT_FULL_CONTROL_FIELD, GRANT_READ_FIELD, GRANT_READ_ACP_FIELD, GRANT_WRITE_FIELD, GRANT_WRITE_ACP_FIELD, EXPECTED_BUCKET_OWNER_FIELD));
    private final String acl;
    private final AccessControlPolicy accessControlPolicy;
    private final String bucket;
    private final String contentMD5;
    private final String checksumAlgorithm;
    private final String grantFullControl;
    private final String grantRead;
    private final String grantReadACP;
    private final String grantWrite;
    private final String grantWriteACP;
    private final String expectedBucketOwner;

    private PutBucketAclRequest(BuilderImpl builder) {
        super(builder);
        this.acl = builder.acl;
        this.accessControlPolicy = builder.accessControlPolicy;
        this.bucket = builder.bucket;
        this.contentMD5 = builder.contentMD5;
        this.checksumAlgorithm = builder.checksumAlgorithm;
        this.grantFullControl = builder.grantFullControl;
        this.grantRead = builder.grantRead;
        this.grantReadACP = builder.grantReadACP;
        this.grantWrite = builder.grantWrite;
        this.grantWriteACP = builder.grantWriteACP;
        this.expectedBucketOwner = builder.expectedBucketOwner;
    }

    public final BucketCannedACL acl() {
        return BucketCannedACL.fromValue(this.acl);
    }

    public final String aclAsString() {
        return this.acl;
    }

    public final AccessControlPolicy accessControlPolicy() {
        return this.accessControlPolicy;
    }

    public final String bucket() {
        return this.bucket;
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

    public final String grantFullControl() {
        return this.grantFullControl;
    }

    public final String grantRead() {
        return this.grantRead;
    }

    public final String grantReadACP() {
        return this.grantReadACP;
    }

    public final String grantWrite() {
        return this.grantWrite;
    }

    public final String grantWriteACP() {
        return this.grantWriteACP;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.aclAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.accessControlPolicy());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumAlgorithmAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantFullControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantRead());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantReadACP());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantWrite());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantWriteACP());
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
        if (!(obj instanceof PutBucketAclRequest)) {
            return false;
        }
        PutBucketAclRequest other = (PutBucketAclRequest)((Object)obj);
        return Objects.equals(this.aclAsString(), other.aclAsString()) && Objects.equals(this.accessControlPolicy(), other.accessControlPolicy()) && Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.contentMD5(), other.contentMD5()) && Objects.equals(this.checksumAlgorithmAsString(), other.checksumAlgorithmAsString()) && Objects.equals(this.grantFullControl(), other.grantFullControl()) && Objects.equals(this.grantRead(), other.grantRead()) && Objects.equals(this.grantReadACP(), other.grantReadACP()) && Objects.equals(this.grantWrite(), other.grantWrite()) && Objects.equals(this.grantWriteACP(), other.grantWriteACP()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner());
    }

    public final String toString() {
        return ToString.builder((String)"PutBucketAclRequest").add("ACL", (Object)this.aclAsString()).add("AccessControlPolicy", (Object)this.accessControlPolicy()).add("Bucket", (Object)this.bucket()).add("ContentMD5", (Object)this.contentMD5()).add("ChecksumAlgorithm", (Object)this.checksumAlgorithmAsString()).add("GrantFullControl", (Object)this.grantFullControl()).add("GrantRead", (Object)this.grantRead()).add("GrantReadACP", (Object)this.grantReadACP()).add("GrantWrite", (Object)this.grantWrite()).add("GrantWriteACP", (Object)this.grantWriteACP()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ACL": {
                return Optional.ofNullable(clazz.cast(this.aclAsString()));
            }
            case "AccessControlPolicy": {
                return Optional.ofNullable(clazz.cast(this.accessControlPolicy()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "ContentMD5": {
                return Optional.ofNullable(clazz.cast(this.contentMD5()));
            }
            case "ChecksumAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.checksumAlgorithmAsString()));
            }
            case "GrantFullControl": {
                return Optional.ofNullable(clazz.cast(this.grantFullControl()));
            }
            case "GrantRead": {
                return Optional.ofNullable(clazz.cast(this.grantRead()));
            }
            case "GrantReadACP": {
                return Optional.ofNullable(clazz.cast(this.grantReadACP()));
            }
            case "GrantWrite": {
                return Optional.ofNullable(clazz.cast(this.grantWrite()));
            }
            case "GrantWriteACP": {
                return Optional.ofNullable(clazz.cast(this.grantWriteACP()));
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

    private static <T> Function<Object, T> getter(Function<PutBucketAclRequest, T> g) {
        return obj -> g.apply((PutBucketAclRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String acl;
        private AccessControlPolicy accessControlPolicy;
        private String bucket;
        private String contentMD5;
        private String checksumAlgorithm;
        private String grantFullControl;
        private String grantRead;
        private String grantReadACP;
        private String grantWrite;
        private String grantWriteACP;
        private String expectedBucketOwner;

        private BuilderImpl() {
        }

        private BuilderImpl(PutBucketAclRequest model) {
            super(model);
            this.acl(model.acl);
            this.accessControlPolicy(model.accessControlPolicy);
            this.bucket(model.bucket);
            this.contentMD5(model.contentMD5);
            this.checksumAlgorithm(model.checksumAlgorithm);
            this.grantFullControl(model.grantFullControl);
            this.grantRead(model.grantRead);
            this.grantReadACP(model.grantReadACP);
            this.grantWrite(model.grantWrite);
            this.grantWriteACP(model.grantWriteACP);
            this.expectedBucketOwner(model.expectedBucketOwner);
        }

        public final String getAcl() {
            return this.acl;
        }

        public final void setAcl(String acl) {
            this.acl = acl;
        }

        @Override
        public final Builder acl(String acl) {
            this.acl = acl;
            return this;
        }

        @Override
        public final Builder acl(BucketCannedACL acl) {
            this.acl(acl == null ? null : acl.toString());
            return this;
        }

        public final AccessControlPolicy.Builder getAccessControlPolicy() {
            return this.accessControlPolicy != null ? this.accessControlPolicy.toBuilder() : null;
        }

        public final void setAccessControlPolicy(AccessControlPolicy.BuilderImpl accessControlPolicy) {
            this.accessControlPolicy = accessControlPolicy != null ? accessControlPolicy.build() : null;
        }

        @Override
        public final Builder accessControlPolicy(AccessControlPolicy accessControlPolicy) {
            this.accessControlPolicy = accessControlPolicy;
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

        public final String getGrantFullControl() {
            return this.grantFullControl;
        }

        public final void setGrantFullControl(String grantFullControl) {
            this.grantFullControl = grantFullControl;
        }

        @Override
        public final Builder grantFullControl(String grantFullControl) {
            this.grantFullControl = grantFullControl;
            return this;
        }

        public final String getGrantRead() {
            return this.grantRead;
        }

        public final void setGrantRead(String grantRead) {
            this.grantRead = grantRead;
        }

        @Override
        public final Builder grantRead(String grantRead) {
            this.grantRead = grantRead;
            return this;
        }

        public final String getGrantReadACP() {
            return this.grantReadACP;
        }

        public final void setGrantReadACP(String grantReadACP) {
            this.grantReadACP = grantReadACP;
        }

        @Override
        public final Builder grantReadACP(String grantReadACP) {
            this.grantReadACP = grantReadACP;
            return this;
        }

        public final String getGrantWrite() {
            return this.grantWrite;
        }

        public final void setGrantWrite(String grantWrite) {
            this.grantWrite = grantWrite;
        }

        @Override
        public final Builder grantWrite(String grantWrite) {
            this.grantWrite = grantWrite;
            return this;
        }

        public final String getGrantWriteACP() {
            return this.grantWriteACP;
        }

        public final void setGrantWriteACP(String grantWriteACP) {
            this.grantWriteACP = grantWriteACP;
        }

        @Override
        public final Builder grantWriteACP(String grantWriteACP) {
            this.grantWriteACP = grantWriteACP;
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
        public PutBucketAclRequest build() {
            return new PutBucketAclRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, PutBucketAclRequest> {
        public Builder acl(String var1);

        public Builder acl(BucketCannedACL var1);

        public Builder accessControlPolicy(AccessControlPolicy var1);

        default public Builder accessControlPolicy(Consumer<AccessControlPolicy.Builder> accessControlPolicy) {
            return this.accessControlPolicy((AccessControlPolicy)((AccessControlPolicy.Builder)AccessControlPolicy.builder().applyMutation(accessControlPolicy)).build());
        }

        public Builder bucket(String var1);

        public Builder contentMD5(String var1);

        public Builder checksumAlgorithm(String var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm var1);

        public Builder grantFullControl(String var1);

        public Builder grantRead(String var1);

        public Builder grantReadACP(String var1);

        public Builder grantWrite(String var1);

        public Builder grantWriteACP(String var1);

        public Builder expectedBucketOwner(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

