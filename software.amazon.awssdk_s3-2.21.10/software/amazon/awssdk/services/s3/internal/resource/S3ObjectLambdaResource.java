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
public final class S3ObjectLambdaResource
implements S3Resource,
ToCopyableBuilder<Builder, S3ObjectLambdaResource> {
    private final String partition;
    private final String region;
    private final String accountId;
    private final String accessPointName;

    private S3ObjectLambdaResource(Builder b) {
        this.partition = (String)Validate.paramNotBlank((CharSequence)b.partition, (String)"partition");
        this.region = (String)Validate.paramNotBlank((CharSequence)b.region, (String)"region");
        this.accountId = (String)Validate.paramNotBlank((CharSequence)b.accountId, (String)"accountId");
        this.accessPointName = (String)Validate.paramNotBlank((CharSequence)b.accessPointName, (String)"accessPointName");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String type() {
        return S3ResourceType.OBJECT_LAMBDA.toString();
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

    public String accessPointName() {
        return this.accessPointName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3ObjectLambdaResource that = (S3ObjectLambdaResource)o;
        if (this.partition != null ? !this.partition.equals(that.partition) : that.partition != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(that.region) : that.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        return this.accessPointName != null ? this.accessPointName.equals(that.accessPointName) : that.accessPointName == null;
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + (this.accessPointName != null ? this.accessPointName.hashCode() : 0);
        return result;
    }

    public Builder toBuilder() {
        return S3ObjectLambdaResource.builder().partition(this.partition).region(this.region).accountId(this.accountId).accessPointName(this.accessPointName);
    }

    public static final class Builder
    implements CopyableBuilder<Builder, S3ObjectLambdaResource> {
        private String partition;
        private String region;
        private String accountId;
        private String accessPointName;

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

        public void setAccessPointName(String accessPointName) {
            this.accessPointName(accessPointName);
        }

        public Builder accessPointName(String accessPointName) {
            this.accessPointName = accessPointName;
            return this;
        }

        public S3ObjectLambdaResource build() {
            return new S3ObjectLambdaResource(this);
        }
    }
}

