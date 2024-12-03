/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.arn.ArnResource;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.ValidationUtils;

@SdkInternalApi
public final class IntermediateOutpostResource {
    private final String outpostId;
    private final ArnResource outpostSubresource;

    private IntermediateOutpostResource(Builder builder) {
        this.outpostId = ValidationUtils.assertStringNotEmpty(builder.outpostId, "outpostId");
        this.outpostSubresource = ValidationUtils.assertNotNull(builder.outpostSubresource, "outpostSubresource");
        if (StringUtils.isNullOrEmpty(builder.outpostSubresource.getResourceType()) || StringUtils.isNullOrEmpty(builder.outpostSubresource.getResource())) {
            throw new IllegalArgumentException("Invalid format for S3 Outpost ARN '" + this.outpostSubresource + "'");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getOutpostId() {
        return this.outpostId;
    }

    public ArnResource getOutpostSubresource() {
        return this.outpostSubresource;
    }

    public static final class Builder {
        private String outpostId;
        private ArnResource outpostSubresource;

        private Builder() {
        }

        public Builder withOutpostSubresource(ArnResource outpostSubResource) {
            this.outpostSubresource = outpostSubResource;
            return this;
        }

        public Builder withOutpostId(String outpostId) {
            this.outpostId = outpostId;
            return this;
        }

        public IntermediateOutpostResource build() {
            return new IntermediateOutpostResource(this);
        }
    }
}

