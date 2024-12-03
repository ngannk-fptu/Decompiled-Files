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
import software.amazon.awssdk.services.s3.model.BucketCannedACL;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.ObjectOwnership;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CreateBucketRequest
extends S3Request
implements ToCopyableBuilder<Builder, CreateBucketRequest> {
    private static final SdkField<String> ACL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ACL").getter(CreateBucketRequest.getter(CreateBucketRequest::aclAsString)).setter(CreateBucketRequest.setter(Builder::acl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-acl").unmarshallLocationName("x-amz-acl").build()}).build();
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(CreateBucketRequest.getter(CreateBucketRequest::bucket)).setter(CreateBucketRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<CreateBucketConfiguration> CREATE_BUCKET_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("CreateBucketConfiguration").getter(CreateBucketRequest.getter(CreateBucketRequest::createBucketConfiguration)).setter(CreateBucketRequest.setter(Builder::createBucketConfiguration)).constructor(CreateBucketConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CreateBucketConfiguration").unmarshallLocationName("CreateBucketConfiguration").build(), PayloadTrait.create()}).build();
    private static final SdkField<String> GRANT_FULL_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantFullControl").getter(CreateBucketRequest.getter(CreateBucketRequest::grantFullControl)).setter(CreateBucketRequest.setter(Builder::grantFullControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-full-control").unmarshallLocationName("x-amz-grant-full-control").build()}).build();
    private static final SdkField<String> GRANT_READ_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantRead").getter(CreateBucketRequest.getter(CreateBucketRequest::grantRead)).setter(CreateBucketRequest.setter(Builder::grantRead)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read").unmarshallLocationName("x-amz-grant-read").build()}).build();
    private static final SdkField<String> GRANT_READ_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantReadACP").getter(CreateBucketRequest.getter(CreateBucketRequest::grantReadACP)).setter(CreateBucketRequest.setter(Builder::grantReadACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read-acp").unmarshallLocationName("x-amz-grant-read-acp").build()}).build();
    private static final SdkField<String> GRANT_WRITE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantWrite").getter(CreateBucketRequest.getter(CreateBucketRequest::grantWrite)).setter(CreateBucketRequest.setter(Builder::grantWrite)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-write").unmarshallLocationName("x-amz-grant-write").build()}).build();
    private static final SdkField<String> GRANT_WRITE_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantWriteACP").getter(CreateBucketRequest.getter(CreateBucketRequest::grantWriteACP)).setter(CreateBucketRequest.setter(Builder::grantWriteACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-write-acp").unmarshallLocationName("x-amz-grant-write-acp").build()}).build();
    private static final SdkField<Boolean> OBJECT_LOCK_ENABLED_FOR_BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ObjectLockEnabledForBucket").getter(CreateBucketRequest.getter(CreateBucketRequest::objectLockEnabledForBucket)).setter(CreateBucketRequest.setter(Builder::objectLockEnabledForBucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-bucket-object-lock-enabled").unmarshallLocationName("x-amz-bucket-object-lock-enabled").build()}).build();
    private static final SdkField<String> OBJECT_OWNERSHIP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectOwnership").getter(CreateBucketRequest.getter(CreateBucketRequest::objectOwnershipAsString)).setter(CreateBucketRequest.setter(Builder::objectOwnership)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-object-ownership").unmarshallLocationName("x-amz-object-ownership").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACL_FIELD, BUCKET_FIELD, CREATE_BUCKET_CONFIGURATION_FIELD, GRANT_FULL_CONTROL_FIELD, GRANT_READ_FIELD, GRANT_READ_ACP_FIELD, GRANT_WRITE_FIELD, GRANT_WRITE_ACP_FIELD, OBJECT_LOCK_ENABLED_FOR_BUCKET_FIELD, OBJECT_OWNERSHIP_FIELD));
    private final String acl;
    private final String bucket;
    private final CreateBucketConfiguration createBucketConfiguration;
    private final String grantFullControl;
    private final String grantRead;
    private final String grantReadACP;
    private final String grantWrite;
    private final String grantWriteACP;
    private final Boolean objectLockEnabledForBucket;
    private final String objectOwnership;

    private CreateBucketRequest(BuilderImpl builder) {
        super(builder);
        this.acl = builder.acl;
        this.bucket = builder.bucket;
        this.createBucketConfiguration = builder.createBucketConfiguration;
        this.grantFullControl = builder.grantFullControl;
        this.grantRead = builder.grantRead;
        this.grantReadACP = builder.grantReadACP;
        this.grantWrite = builder.grantWrite;
        this.grantWriteACP = builder.grantWriteACP;
        this.objectLockEnabledForBucket = builder.objectLockEnabledForBucket;
        this.objectOwnership = builder.objectOwnership;
    }

    public final BucketCannedACL acl() {
        return BucketCannedACL.fromValue(this.acl);
    }

    public final String aclAsString() {
        return this.acl;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final CreateBucketConfiguration createBucketConfiguration() {
        return this.createBucketConfiguration;
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

    public final Boolean objectLockEnabledForBucket() {
        return this.objectLockEnabledForBucket;
    }

    public final ObjectOwnership objectOwnership() {
        return ObjectOwnership.fromValue(this.objectOwnership);
    }

    public final String objectOwnershipAsString() {
        return this.objectOwnership;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.createBucketConfiguration());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantFullControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantRead());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantReadACP());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantWrite());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantWriteACP());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockEnabledForBucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectOwnershipAsString());
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
        if (!(obj instanceof CreateBucketRequest)) {
            return false;
        }
        CreateBucketRequest other = (CreateBucketRequest)((Object)obj);
        return Objects.equals(this.aclAsString(), other.aclAsString()) && Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.createBucketConfiguration(), other.createBucketConfiguration()) && Objects.equals(this.grantFullControl(), other.grantFullControl()) && Objects.equals(this.grantRead(), other.grantRead()) && Objects.equals(this.grantReadACP(), other.grantReadACP()) && Objects.equals(this.grantWrite(), other.grantWrite()) && Objects.equals(this.grantWriteACP(), other.grantWriteACP()) && Objects.equals(this.objectLockEnabledForBucket(), other.objectLockEnabledForBucket()) && Objects.equals(this.objectOwnershipAsString(), other.objectOwnershipAsString());
    }

    public final String toString() {
        return ToString.builder((String)"CreateBucketRequest").add("ACL", (Object)this.aclAsString()).add("Bucket", (Object)this.bucket()).add("CreateBucketConfiguration", (Object)this.createBucketConfiguration()).add("GrantFullControl", (Object)this.grantFullControl()).add("GrantRead", (Object)this.grantRead()).add("GrantReadACP", (Object)this.grantReadACP()).add("GrantWrite", (Object)this.grantWrite()).add("GrantWriteACP", (Object)this.grantWriteACP()).add("ObjectLockEnabledForBucket", (Object)this.objectLockEnabledForBucket()).add("ObjectOwnership", (Object)this.objectOwnershipAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ACL": {
                return Optional.ofNullable(clazz.cast(this.aclAsString()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "CreateBucketConfiguration": {
                return Optional.ofNullable(clazz.cast(this.createBucketConfiguration()));
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
            case "ObjectLockEnabledForBucket": {
                return Optional.ofNullable(clazz.cast(this.objectLockEnabledForBucket()));
            }
            case "ObjectOwnership": {
                return Optional.ofNullable(clazz.cast(this.objectOwnershipAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CreateBucketRequest, T> g) {
        return obj -> g.apply((CreateBucketRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String acl;
        private String bucket;
        private CreateBucketConfiguration createBucketConfiguration;
        private String grantFullControl;
        private String grantRead;
        private String grantReadACP;
        private String grantWrite;
        private String grantWriteACP;
        private Boolean objectLockEnabledForBucket;
        private String objectOwnership;

        private BuilderImpl() {
        }

        private BuilderImpl(CreateBucketRequest model) {
            super(model);
            this.acl(model.acl);
            this.bucket(model.bucket);
            this.createBucketConfiguration(model.createBucketConfiguration);
            this.grantFullControl(model.grantFullControl);
            this.grantRead(model.grantRead);
            this.grantReadACP(model.grantReadACP);
            this.grantWrite(model.grantWrite);
            this.grantWriteACP(model.grantWriteACP);
            this.objectLockEnabledForBucket(model.objectLockEnabledForBucket);
            this.objectOwnership(model.objectOwnership);
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

        public final CreateBucketConfiguration.Builder getCreateBucketConfiguration() {
            return this.createBucketConfiguration != null ? this.createBucketConfiguration.toBuilder() : null;
        }

        public final void setCreateBucketConfiguration(CreateBucketConfiguration.BuilderImpl createBucketConfiguration) {
            this.createBucketConfiguration = createBucketConfiguration != null ? createBucketConfiguration.build() : null;
        }

        @Override
        public final Builder createBucketConfiguration(CreateBucketConfiguration createBucketConfiguration) {
            this.createBucketConfiguration = createBucketConfiguration;
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

        public final Boolean getObjectLockEnabledForBucket() {
            return this.objectLockEnabledForBucket;
        }

        public final void setObjectLockEnabledForBucket(Boolean objectLockEnabledForBucket) {
            this.objectLockEnabledForBucket = objectLockEnabledForBucket;
        }

        @Override
        public final Builder objectLockEnabledForBucket(Boolean objectLockEnabledForBucket) {
            this.objectLockEnabledForBucket = objectLockEnabledForBucket;
            return this;
        }

        public final String getObjectOwnership() {
            return this.objectOwnership;
        }

        public final void setObjectOwnership(String objectOwnership) {
            this.objectOwnership = objectOwnership;
        }

        @Override
        public final Builder objectOwnership(String objectOwnership) {
            this.objectOwnership = objectOwnership;
            return this;
        }

        @Override
        public final Builder objectOwnership(ObjectOwnership objectOwnership) {
            this.objectOwnership(objectOwnership == null ? null : objectOwnership.toString());
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
        public CreateBucketRequest build() {
            return new CreateBucketRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, CreateBucketRequest> {
        public Builder acl(String var1);

        public Builder acl(BucketCannedACL var1);

        public Builder bucket(String var1);

        public Builder createBucketConfiguration(CreateBucketConfiguration var1);

        default public Builder createBucketConfiguration(Consumer<CreateBucketConfiguration.Builder> createBucketConfiguration) {
            return this.createBucketConfiguration((CreateBucketConfiguration)((CreateBucketConfiguration.Builder)CreateBucketConfiguration.builder().applyMutation(createBucketConfiguration)).build());
        }

        public Builder grantFullControl(String var1);

        public Builder grantRead(String var1);

        public Builder grantReadACP(String var1);

        public Builder grantWrite(String var1);

        public Builder grantWriteACP(String var1);

        public Builder objectLockEnabledForBucket(Boolean var1);

        public Builder objectOwnership(String var1);

        public Builder objectOwnership(ObjectOwnership var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

