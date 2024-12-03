/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.SignerLoader
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.SignerLoader;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.services.s3.internal.resource.S3Resource;
import software.amazon.awssdk.services.s3.internal.resource.S3ResourceType;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkInternalApi
public final class S3AccessPointResource
implements S3Resource,
ToCopyableBuilder<Builder, S3AccessPointResource> {
    private static final S3ResourceType S3_RESOURCE_TYPE = S3ResourceType.ACCESS_POINT;
    private static final Set<S3ResourceType> VALID_PARENT_RESOURCE_TYPES = EnumSet.of(S3ResourceType.OUTPOST, S3ResourceType.OBJECT_LAMBDA);
    private final String partition;
    private final String region;
    private final String accountId;
    private final String accessPointName;
    private final S3Resource parentS3Resource;

    private S3AccessPointResource(Builder b) {
        this.accessPointName = (String)Validate.paramNotBlank((CharSequence)b.accessPointName, (String)"accessPointName");
        if (b.parentS3Resource == null) {
            this.parentS3Resource = null;
            this.partition = (String)Validate.paramNotBlank((CharSequence)b.partition, (String)"partition");
            this.region = b.region;
            this.accountId = (String)Validate.paramNotBlank((CharSequence)b.accountId, (String)"accountId");
        } else {
            this.parentS3Resource = this.validateParentS3Resource(b.parentS3Resource);
            Validate.isTrue((b.partition == null ? 1 : 0) != 0, (String)"partition cannot be set on builder if it has parent resource", (Object[])new Object[0]);
            Validate.isTrue((b.region == null ? 1 : 0) != 0, (String)"region cannot be set on builder if it has parent resource", (Object[])new Object[0]);
            Validate.isTrue((b.accountId == null ? 1 : 0) != 0, (String)"accountId cannot be set on builder if it has parent resource", (Object[])new Object[0]);
            this.partition = this.parentS3Resource.partition().orElse(null);
            this.region = this.parentS3Resource.region().orElse(null);
            this.accountId = this.parentS3Resource.accountId().orElse(null);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String type() {
        return S3_RESOURCE_TYPE.toString();
    }

    @Override
    public Optional<S3Resource> parentS3Resource() {
        return Optional.ofNullable(this.parentS3Resource);
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

    @Override
    public Optional<Signer> overrideSigner() {
        return StringUtils.isEmpty((CharSequence)this.region) ? Optional.of(SignerLoader.getS3SigV4aSigner()) : Optional.empty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3AccessPointResource that = (S3AccessPointResource)o;
        if (this.partition != null ? !this.partition.equals(that.partition) : that.partition != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(that.region) : that.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        if (this.parentS3Resource != null ? !this.parentS3Resource.equals(that.parentS3Resource) : that.parentS3Resource != null) {
            return false;
        }
        return this.accessPointName.equals(that.accessPointName);
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + this.accessPointName.hashCode();
        result = 31 * result + (this.parentS3Resource != null ? this.parentS3Resource.hashCode() : 0);
        return result;
    }

    public Builder toBuilder() {
        return S3AccessPointResource.builder().partition(this.partition).region(this.region).accountId(this.accountId).accessPointName(this.accessPointName);
    }

    private S3Resource validateParentS3Resource(S3Resource parentS3Resource) {
        String invalidParentResourceTypeMessage = "Invalid 'parentS3Resource' type. An S3 access point resource must be associated with an outpost or object lambda parent resource.";
        VALID_PARENT_RESOURCE_TYPES.stream().filter(r -> r.toString().equals(parentS3Resource.type())).findAny().orElseThrow(() -> new IllegalArgumentException(invalidParentResourceTypeMessage));
        return parentS3Resource;
    }

    public static final class Builder
    implements CopyableBuilder<Builder, S3AccessPointResource> {
        private String partition;
        private String region;
        private String accountId;
        private String accessPointName;
        private S3Resource parentS3Resource;

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

        public Builder parentS3Resource(S3Resource parentS3Resource) {
            this.parentS3Resource = parentS3Resource;
            return this;
        }

        public S3AccessPointResource build() {
            return new S3AccessPointResource(this);
        }
    }
}

