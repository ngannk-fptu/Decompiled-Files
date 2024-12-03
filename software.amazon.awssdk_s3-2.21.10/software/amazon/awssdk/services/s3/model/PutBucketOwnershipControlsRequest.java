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
import software.amazon.awssdk.services.s3.model.OwnershipControls;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class PutBucketOwnershipControlsRequest
extends S3Request
implements ToCopyableBuilder<Builder, PutBucketOwnershipControlsRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(PutBucketOwnershipControlsRequest.getter(PutBucketOwnershipControlsRequest::bucket)).setter(PutBucketOwnershipControlsRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> CONTENT_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentMD5").getter(PutBucketOwnershipControlsRequest.getter(PutBucketOwnershipControlsRequest::contentMD5)).setter(PutBucketOwnershipControlsRequest.setter(Builder::contentMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-MD5").unmarshallLocationName("Content-MD5").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(PutBucketOwnershipControlsRequest.getter(PutBucketOwnershipControlsRequest::expectedBucketOwner)).setter(PutBucketOwnershipControlsRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<OwnershipControls> OWNERSHIP_CONTROLS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("OwnershipControls").getter(PutBucketOwnershipControlsRequest.getter(PutBucketOwnershipControlsRequest::ownershipControls)).setter(PutBucketOwnershipControlsRequest.setter(Builder::ownershipControls)).constructor(OwnershipControls::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OwnershipControls").unmarshallLocationName("OwnershipControls").build(), PayloadTrait.create(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, CONTENT_MD5_FIELD, EXPECTED_BUCKET_OWNER_FIELD, OWNERSHIP_CONTROLS_FIELD));
    private final String bucket;
    private final String contentMD5;
    private final String expectedBucketOwner;
    private final OwnershipControls ownershipControls;

    private PutBucketOwnershipControlsRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.contentMD5 = builder.contentMD5;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.ownershipControls = builder.ownershipControls;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String contentMD5() {
        return this.contentMD5;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    public final OwnershipControls ownershipControls() {
        return this.ownershipControls;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.contentMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.ownershipControls());
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
        if (!(obj instanceof PutBucketOwnershipControlsRequest)) {
            return false;
        }
        PutBucketOwnershipControlsRequest other = (PutBucketOwnershipControlsRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.contentMD5(), other.contentMD5()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.ownershipControls(), other.ownershipControls());
    }

    public final String toString() {
        return ToString.builder((String)"PutBucketOwnershipControlsRequest").add("Bucket", (Object)this.bucket()).add("ContentMD5", (Object)this.contentMD5()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("OwnershipControls", (Object)this.ownershipControls()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "ContentMD5": {
                return Optional.ofNullable(clazz.cast(this.contentMD5()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
            case "OwnershipControls": {
                return Optional.ofNullable(clazz.cast(this.ownershipControls()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PutBucketOwnershipControlsRequest, T> g) {
        return obj -> g.apply((PutBucketOwnershipControlsRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String contentMD5;
        private String expectedBucketOwner;
        private OwnershipControls ownershipControls;

        private BuilderImpl() {
        }

        private BuilderImpl(PutBucketOwnershipControlsRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.contentMD5(model.contentMD5);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.ownershipControls(model.ownershipControls);
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

        public final OwnershipControls.Builder getOwnershipControls() {
            return this.ownershipControls != null ? this.ownershipControls.toBuilder() : null;
        }

        public final void setOwnershipControls(OwnershipControls.BuilderImpl ownershipControls) {
            this.ownershipControls = ownershipControls != null ? ownershipControls.build() : null;
        }

        @Override
        public final Builder ownershipControls(OwnershipControls ownershipControls) {
            this.ownershipControls = ownershipControls;
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
        public PutBucketOwnershipControlsRequest build() {
            return new PutBucketOwnershipControlsRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, PutBucketOwnershipControlsRequest> {
        public Builder bucket(String var1);

        public Builder contentMD5(String var1);

        public Builder expectedBucketOwner(String var1);

        public Builder ownershipControls(OwnershipControls var1);

        default public Builder ownershipControls(Consumer<OwnershipControls.Builder> ownershipControls) {
            return this.ownershipControls((OwnershipControls)((OwnershipControls.Builder)OwnershipControls.builder().applyMutation(ownershipControls)).build());
        }

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

