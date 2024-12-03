/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.arns.ArnResource
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.resource;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.arns.ArnResource;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class IntermediateOutpostResource {
    private final String outpostId;
    private final ArnResource outpostSubresource;

    private IntermediateOutpostResource(Builder builder) {
        this.outpostId = (String)Validate.paramNotBlank((CharSequence)builder.outpostId, (String)"outpostId");
        this.outpostSubresource = (ArnResource)Validate.notNull((Object)builder.outpostSubresource, (String)"outpostSubresource", (Object[])new Object[0]);
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)builder.outpostSubresource.resource()), (String)"Invalid format for S3 Outpost ARN", (Object[])new Object[0]);
        Validate.isTrue((boolean)builder.outpostSubresource.resourceType().isPresent(), (String)"Invalid format for S3 Outpost ARN", (Object[])new Object[0]);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String outpostId() {
        return this.outpostId;
    }

    public ArnResource outpostSubresource() {
        return this.outpostSubresource;
    }

    public static final class Builder {
        private String outpostId;
        private ArnResource outpostSubresource;

        private Builder() {
        }

        public Builder outpostSubresource(ArnResource outpostSubResource) {
            this.outpostSubresource = outpostSubResource;
            return this;
        }

        public Builder outpostId(String outpostId) {
            this.outpostId = outpostId;
            return this;
        }

        public IntermediateOutpostResource build() {
            return new IntermediateOutpostResource(this);
        }
    }
}

