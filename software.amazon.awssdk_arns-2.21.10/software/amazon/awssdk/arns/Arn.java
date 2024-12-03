/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.arns;

import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.arns.ArnResource;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class Arn
implements ToCopyableBuilder<Builder, Arn> {
    private final String partition;
    private final String service;
    private final String region;
    private final String accountId;
    private final String resource;
    private final ArnResource arnResource;

    private Arn(DefaultBuilder builder) {
        this.partition = (String)Validate.paramNotBlank((CharSequence)builder.partition, (String)"partition");
        this.service = (String)Validate.paramNotBlank((CharSequence)builder.service, (String)"service");
        this.region = builder.region;
        this.accountId = builder.accountId;
        this.resource = (String)Validate.paramNotBlank((CharSequence)builder.resource, (String)"resource");
        this.arnResource = ArnResource.fromString(this.resource);
    }

    public String partition() {
        return this.partition;
    }

    public String service() {
        return this.service;
    }

    public Optional<String> region() {
        return StringUtils.isEmpty((CharSequence)this.region) ? Optional.empty() : Optional.of(this.region);
    }

    public Optional<String> accountId() {
        return StringUtils.isEmpty((CharSequence)this.accountId) ? Optional.empty() : Optional.of(this.accountId);
    }

    public ArnResource resource() {
        return this.arnResource;
    }

    public String resourceAsString() {
        return this.resource;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public static Arn fromString(String arn) {
        int arnColonIndex = arn.indexOf(58);
        if (arnColonIndex < 0 || !"arn".equals(arn.substring(0, arnColonIndex))) {
            throw new IllegalArgumentException("Malformed ARN - doesn't start with 'arn:'");
        }
        int partitionColonIndex = arn.indexOf(58, arnColonIndex + 1);
        if (partitionColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no AWS partition specified");
        }
        String partition = arn.substring(arnColonIndex + 1, partitionColonIndex);
        int serviceColonIndex = arn.indexOf(58, partitionColonIndex + 1);
        if (serviceColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no service specified");
        }
        String service = arn.substring(partitionColonIndex + 1, serviceColonIndex);
        int regionColonIndex = arn.indexOf(58, serviceColonIndex + 1);
        if (regionColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no AWS region partition specified");
        }
        String region = arn.substring(serviceColonIndex + 1, regionColonIndex);
        int accountColonIndex = arn.indexOf(58, regionColonIndex + 1);
        if (accountColonIndex < 0) {
            throw new IllegalArgumentException("Malformed ARN - no AWS account specified");
        }
        String accountId = arn.substring(regionColonIndex + 1, accountColonIndex);
        String resource = arn.substring(accountColonIndex + 1);
        if (resource.isEmpty()) {
            throw new IllegalArgumentException("Malformed ARN - no resource specified");
        }
        return Arn.builder().partition(partition).service(service).region(region).accountId(accountId).resource(resource).build();
    }

    public String toString() {
        return "arn:" + this.partition + ":" + this.service + ":" + this.region + ":" + this.accountId + ":" + this.resource;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Arn arn = (Arn)o;
        if (!Objects.equals(this.partition, arn.partition)) {
            return false;
        }
        if (!Objects.equals(this.service, arn.service)) {
            return false;
        }
        if (!Objects.equals(this.region, arn.region)) {
            return false;
        }
        if (!Objects.equals(this.accountId, arn.accountId)) {
            return false;
        }
        if (!Objects.equals(this.resource, arn.resource)) {
            return false;
        }
        return Objects.equals(this.arnResource, arn.arnResource);
    }

    public int hashCode() {
        int result = this.partition.hashCode();
        result = 31 * result + this.service.hashCode();
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + this.resource.hashCode();
        return result;
    }

    public Builder toBuilder() {
        return Arn.builder().accountId(this.accountId).partition(this.partition).region(this.region).resource(this.resource).service(this.service);
    }

    private static final class DefaultBuilder
    implements Builder {
        private String partition;
        private String service;
        private String region;
        private String accountId;
        private String resource;

        private DefaultBuilder() {
        }

        public void setPartition(String partition) {
            this.partition = partition;
        }

        @Override
        public Builder partition(String partition) {
            this.setPartition(partition);
            return this;
        }

        public void setService(String service) {
            this.service = service;
        }

        @Override
        public Builder service(String service) {
            this.setService(service);
            return this;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        @Override
        public Builder region(String region) {
            this.setRegion(region);
            return this;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        @Override
        public Builder accountId(String accountId) {
            this.setAccountId(accountId);
            return this;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        @Override
        public Builder resource(String resource) {
            this.setResource(resource);
            return this;
        }

        @Override
        public Arn build() {
            return new Arn(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, Arn> {
        public Builder partition(String var1);

        public Builder service(String var1);

        public Builder region(String var1);

        public Builder accountId(String var1);

        public Builder resource(String var1);

        public Arn build();
    }
}

