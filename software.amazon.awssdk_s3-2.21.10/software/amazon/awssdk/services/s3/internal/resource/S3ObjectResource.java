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
public final class S3ObjectResource
implements S3Resource {
    private static final S3ResourceType S3_RESOURCE_TYPE = S3ResourceType.OBJECT;
    private final S3Resource parentS3Resource;
    private final String key;

    private S3ObjectResource(Builder b) {
        this.parentS3Resource = this.validateParentS3Resource(b.parentS3Resource);
        this.key = (String)Validate.paramNotBlank((CharSequence)b.key, (String)"key");
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
        return this.parentS3Resource.partition();
    }

    @Override
    public Optional<String> region() {
        return this.parentS3Resource.region();
    }

    @Override
    public Optional<String> accountId() {
        return this.parentS3Resource.accountId();
    }

    public String key() {
        return this.key;
    }

    @Override
    public Optional<S3Resource> parentS3Resource() {
        return Optional.of(this.parentS3Resource);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3ObjectResource that = (S3ObjectResource)o;
        if (this.parentS3Resource != null ? !this.parentS3Resource.equals(that.parentS3Resource) : that.parentS3Resource != null) {
            return false;
        }
        return this.key != null ? this.key.equals(that.key) : that.key == null;
    }

    public int hashCode() {
        int result = this.parentS3Resource != null ? this.parentS3Resource.hashCode() : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }

    private S3Resource validateParentS3Resource(S3Resource parentS3Resource) {
        Validate.paramNotNull((Object)parentS3Resource, (String)"parentS3Resource");
        if (!S3ResourceType.ACCESS_POINT.toString().equals(parentS3Resource.type()) && !S3ResourceType.BUCKET.toString().equals(parentS3Resource.type())) {
            throw new IllegalArgumentException("Invalid 'parentS3Resource' type. An S3 object resource must be associated with either a bucket or access-point parent resource.");
        }
        return parentS3Resource;
    }

    public static final class Builder {
        private S3Resource parentS3Resource;
        private String key;

        private Builder() {
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder parentS3Resource(S3Resource parentS3Resource) {
            this.parentS3Resource = parentS3Resource;
            return this;
        }

        public S3ObjectResource build() {
            return new S3ObjectResource(this);
        }
    }
}

