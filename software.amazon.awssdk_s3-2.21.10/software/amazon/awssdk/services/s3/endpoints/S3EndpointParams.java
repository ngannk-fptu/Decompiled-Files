/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.endpoints;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class S3EndpointParams
implements ToCopyableBuilder<Builder, S3EndpointParams> {
    private final String bucket;
    private final Region region;
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

    private S3EndpointParams(BuilderImpl builder) {
        this.bucket = builder.bucket;
        this.region = builder.region;
        this.useFIPS = builder.useFIPS;
        this.useDualStack = builder.useDualStack;
        this.endpoint = builder.endpoint;
        this.forcePathStyle = builder.forcePathStyle;
        this.accelerate = builder.accelerate;
        this.useGlobalEndpoint = builder.useGlobalEndpoint;
        this.useObjectLambdaEndpoint = builder.useObjectLambdaEndpoint;
        this.disableAccessPoints = builder.disableAccessPoints;
        this.disableMultiRegionAccessPoints = builder.disableMultiRegionAccessPoints;
        this.useArnRegion = builder.useArnRegion;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public String bucket() {
        return this.bucket;
    }

    public Region region() {
        return this.region;
    }

    public Boolean useFips() {
        return this.useFIPS;
    }

    public Boolean useDualStack() {
        return this.useDualStack;
    }

    public String endpoint() {
        return this.endpoint;
    }

    public Boolean forcePathStyle() {
        return this.forcePathStyle;
    }

    public Boolean accelerate() {
        return this.accelerate;
    }

    public Boolean useGlobalEndpoint() {
        return this.useGlobalEndpoint;
    }

    public Boolean useObjectLambdaEndpoint() {
        return this.useObjectLambdaEndpoint;
    }

    public Boolean disableAccessPoints() {
        return this.disableAccessPoints;
    }

    public Boolean disableMultiRegionAccessPoints() {
        return this.disableMultiRegionAccessPoints;
    }

    public Boolean useArnRegion() {
        return this.useArnRegion;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private static class BuilderImpl
    implements Builder {
        private String bucket;
        private Region region;
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

        private BuilderImpl() {
        }

        private BuilderImpl(S3EndpointParams builder) {
            this.bucket = builder.bucket;
            this.region = builder.region;
            this.useFIPS = builder.useFIPS;
            this.useDualStack = builder.useDualStack;
            this.endpoint = builder.endpoint;
            this.forcePathStyle = builder.forcePathStyle;
            this.accelerate = builder.accelerate;
            this.useGlobalEndpoint = builder.useGlobalEndpoint;
            this.useObjectLambdaEndpoint = builder.useObjectLambdaEndpoint;
            this.disableAccessPoints = builder.disableAccessPoints;
            this.disableMultiRegionAccessPoints = builder.disableMultiRegionAccessPoints;
            this.useArnRegion = builder.useArnRegion;
        }

        @Override
        public Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        @Override
        public Builder region(Region region) {
            this.region = region;
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
        public S3EndpointParams build() {
            return new S3EndpointParams(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, S3EndpointParams> {
        public Builder bucket(String var1);

        public Builder region(Region var1);

        public Builder useFips(Boolean var1);

        public Builder useDualStack(Boolean var1);

        public Builder endpoint(String var1);

        public Builder forcePathStyle(Boolean var1);

        public Builder accelerate(Boolean var1);

        public Builder useGlobalEndpoint(Boolean var1);

        public Builder useObjectLambdaEndpoint(Boolean var1);

        public Builder disableAccessPoints(Boolean var1);

        public Builder disableMultiRegionAccessPoints(Boolean var1);

        public Builder useArnRegion(Boolean var1);

        public S3EndpointParams build();
    }
}

