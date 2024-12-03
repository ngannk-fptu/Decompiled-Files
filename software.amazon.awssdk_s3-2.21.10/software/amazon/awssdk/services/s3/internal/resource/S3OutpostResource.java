/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.internal.resource.S3Resource;
import software.amazon.awssdk.services.s3.internal.resource.S3ResourceType;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class S3OutpostResource
implements S3Resource {
    private final String partition;
    private final String region;
    private final String accountId;
    private final String outpostId;

    private S3OutpostResource(Builder b) {
        this.partition = (String)Validate.paramNotBlank((CharSequence)b.partition, (String)"partition");
        this.region = (String)Validate.paramNotBlank((CharSequence)b.region, (String)"region");
        this.accountId = (String)Validate.paramNotBlank((CharSequence)b.accountId, (String)"accountId");
        this.outpostId = (String)Validate.paramNotBlank((CharSequence)b.outpostId, (String)"outpostId");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String type() {
        return S3ResourceType.OUTPOST.toString();
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

    public String outpostId() {
        return this.outpostId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3OutpostResource that = (S3OutpostResource)o;
        if (this.partition != null ? !this.partition.equals(that.partition) : that.partition != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(that.region) : that.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        return this.outpostId.equals(that.outpostId);
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + this.outpostId.hashCode();
        return result;
    }

    public static final class Builder {
        private String outpostId;
        private String partition;
        private String region;
        private String accountId;

        private Builder() {
        }

        public Builder partition(String partition) {
            this.partition = partition;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder outpostId(String outpostId) {
            this.outpostId = outpostId;
            return this;
        }

        public S3OutpostResource build() {
            return new S3OutpostResource(this);
        }
    }
}

