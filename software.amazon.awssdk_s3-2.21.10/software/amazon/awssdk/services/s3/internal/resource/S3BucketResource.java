/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.internal.resource.S3Resource;
import software.amazon.awssdk.services.s3.internal.resource.S3ResourceType;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkInternalApi
public final class S3BucketResource
implements S3Resource,
ToCopyableBuilder<Builder, S3BucketResource> {
    private static final S3ResourceType S3_RESOURCE_TYPE = S3ResourceType.BUCKET;
    private final String partition;
    private final String region;
    private final String accountId;
    private final String bucketName;

    private S3BucketResource(Builder b) {
        this.bucketName = (String)Validate.paramNotBlank((CharSequence)b.bucketName, (String)"bucketName");
        this.partition = b.partition;
        this.region = b.region;
        this.accountId = b.accountId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String type() {
        return S3_RESOURCE_TYPE.toString();
    }

    @Override
    public Optional<String> partition() {
        return Optional.ofNullable(this.partition);
    }

    @Override
    public Optional<String> region() {
        return Optional.ofNullable(this.region);
    }

    @Override
    public Optional<String> accountId() {
        return Optional.ofNullable(this.accountId);
    }

    public String bucketName() {
        return this.bucketName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3BucketResource that = (S3BucketResource)o;
        if (this.partition != null ? !this.partition.equals(that.partition) : that.partition != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(that.region) : that.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        return this.bucketName.equals(that.bucketName);
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + this.bucketName.hashCode();
        return result;
    }

    public Builder toBuilder() {
        return S3BucketResource.builder().partition(this.partition).region(this.region).accountId(this.accountId).bucketName(this.bucketName);
    }

    public static final class Builder
    implements CopyableBuilder<Builder, S3BucketResource> {
        private String partition;
        private String region;
        private String accountId;
        private String bucketName;

        private Builder() {
        }

        public void setPartition(String partition) {
            this.partition(partition);
        }

        public Builder partition(String partition) {
            this.partition = partition;
            return this;
        }

        public void setRegion(String region) {
            this.region(region);
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public void setAccountId(String accountId) {
            this.accountId(accountId);
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public void setBucketName(String bucketName) {
            this.bucketName(bucketName);
        }

        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public S3BucketResource build() {
            return new S3BucketResource(this);
        }
    }
}

