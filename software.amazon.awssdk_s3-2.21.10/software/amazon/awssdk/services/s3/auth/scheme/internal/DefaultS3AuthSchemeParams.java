/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.auth.scheme.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeParams;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3AuthSchemeParams
implements S3AuthSchemeParams {
    private final String operation;
    private final Region region;
    private final String bucket;
    private final Boolean useFIPS;
    private final Boolean useDualStack;
    private final String endpoint;
    private final Boolean forcePathStyle;
    private final Boolean accelerate;
    private final Boolean useGlobalEndpoint;
    private final Boolean useObjectLambdaEndpoint;
    private final Boolean disableAccessPoints;
    private final Boolean disableMultiRegionAccessPoints;
    private final Boolean useArnRegion;

    private DefaultS3AuthSchemeParams(Builder builder) {
        this.operation = (String)Validate.paramNotNull((Object)builder.operation, (String)"operation");
        this.region = builder.region;
        this.bucket = builder.bucket;
        this.useFIPS = (Boolean)Validate.paramNotNull((Object)builder.useFIPS, (String)"useFIPS");
        this.useDualStack = (Boolean)Validate.paramNotNull((Object)builder.useDualStack, (String)"useDualStack");
        this.endpoint = builder.endpoint;
        this.forcePathStyle = (Boolean)Validate.paramNotNull((Object)builder.forcePathStyle, (String)"forcePathStyle");
        this.accelerate = (Boolean)Validate.paramNotNull((Object)builder.accelerate, (String)"accelerate");
        this.useGlobalEndpoint = (Boolean)Validate.paramNotNull((Object)builder.useGlobalEndpoint, (String)"useGlobalEndpoint");
        this.useObjectLambdaEndpoint = builder.useObjectLambdaEndpoint;
        this.disableAccessPoints = builder.disableAccessPoints;
        this.disableMultiRegionAccessPoints = (Boolean)Validate.paramNotNull((Object)builder.disableMultiRegionAccessPoints, (String)"disableMultiRegionAccessPoints");
        this.useArnRegion = builder.useArnRegion;
    }

    public static S3AuthSchemeParams.Builder builder() {
        return new Builder();
    }

    @Override
    public String operation() {
        return this.operation;
    }

    @Override
    public Region region() {
        return this.region;
    }

    @Override
    public String bucket() {
        return this.bucket;
    }

    @Override
    public Boolean useFips() {
        return this.useFIPS;
    }

    @Override
    public Boolean useDualStack() {
        return this.useDualStack;
    }

    @Override
    public String endpoint() {
        return this.endpoint;
    }

    @Override
    public Boolean forcePathStyle() {
        return this.forcePathStyle;
    }

    @Override
    public Boolean accelerate() {
        return this.accelerate;
    }

    @Override
    public Boolean useGlobalEndpoint() {
        return this.useGlobalEndpoint;
    }

    @Override
    public Boolean useObjectLambdaEndpoint() {
        return this.useObjectLambdaEndpoint;
    }

    @Override
    public Boolean disableAccessPoints() {
        return this.disableAccessPoints;
    }

    @Override
    public Boolean disableMultiRegionAccessPoints() {
        return this.disableMultiRegionAccessPoints;
    }

    @Override
    public Boolean useArnRegion() {
        return this.useArnRegion;
    }

    @Override
    public S3AuthSchemeParams.Builder toBuilder() {
        return new Builder(this);
    }

    private static final class Builder
    implements S3AuthSchemeParams.Builder {
        private String operation;
        private Region region;
        private String bucket;
        private Boolean useFIPS = false;
        private Boolean useDualStack = false;
        private String endpoint;
        private Boolean forcePathStyle = false;
        private Boolean accelerate = false;
        private Boolean useGlobalEndpoint = false;
        private Boolean useObjectLambdaEndpoint;
        private Boolean disableAccessPoints;
        private Boolean disableMultiRegionAccessPoints = false;
        private Boolean useArnRegion;

        Builder() {
        }

        Builder(DefaultS3AuthSchemeParams params) {
            this.operation = params.operation;
            this.region = params.region;
            this.bucket = params.bucket;
            this.useFIPS = params.useFIPS;
            this.useDualStack = params.useDualStack;
            this.endpoint = params.endpoint;
            this.forcePathStyle = params.forcePathStyle;
            this.accelerate = params.accelerate;
            this.useGlobalEndpoint = params.useGlobalEndpoint;
            this.useObjectLambdaEndpoint = params.useObjectLambdaEndpoint;
            this.disableAccessPoints = params.disableAccessPoints;
            this.disableMultiRegionAccessPoints = params.disableMultiRegionAccessPoints;
            this.useArnRegion = params.useArnRegion;
        }

        @Override
        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }

        @Override
        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        @Override
        public Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        @Override
        public Builder useFips(Boolean useFIPS) {
            this.useFIPS = useFIPS;
            if (this.useFIPS == null) {
                this.useFIPS = false;
            }
            return this;
        }

        @Override
        public Builder useDualStack(Boolean useDualStack) {
            this.useDualStack = useDualStack;
            if (this.useDualStack == null) {
                this.useDualStack = false;
            }
            return this;
        }

        @Override
        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        @Override
        public Builder forcePathStyle(Boolean forcePathStyle) {
            this.forcePathStyle = forcePathStyle;
            if (this.forcePathStyle == null) {
                this.forcePathStyle = false;
            }
            return this;
        }

        @Override
        public Builder accelerate(Boolean accelerate) {
            this.accelerate = accelerate;
            if (this.accelerate == null) {
                this.accelerate = false;
            }
            return this;
        }

        @Override
        public Builder useGlobalEndpoint(Boolean useGlobalEndpoint) {
            this.useGlobalEndpoint = useGlobalEndpoint;
            if (this.useGlobalEndpoint == null) {
                this.useGlobalEndpoint = false;
            }
            return this;
        }

        @Override
        public Builder useObjectLambdaEndpoint(Boolean useObjectLambdaEndpoint) {
            this.useObjectLambdaEndpoint = useObjectLambdaEndpoint;
            return this;
        }

        @Override
        public Builder disableAccessPoints(Boolean disableAccessPoints) {
            this.disableAccessPoints = disableAccessPoints;
            return this;
        }

        @Override
        public Builder disableMultiRegionAccessPoints(Boolean disableMultiRegionAccessPoints) {
            this.disableMultiRegionAccessPoints = disableMultiRegionAccessPoints;
            if (this.disableMultiRegionAccessPoints == null) {
                this.disableMultiRegionAccessPoints = false;
            }
            return this;
        }

        @Override
        public Builder useArnRegion(Boolean useArnRegion) {
            this.useArnRegion = useArnRegion;
            return this;
        }

        @Override
        public S3AuthSchemeParams build() {
            return new DefaultS3AuthSchemeParams(this);
        }
    }
}

