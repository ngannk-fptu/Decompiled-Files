/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.sts.endpoints;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class StsEndpointParams
implements ToCopyableBuilder<Builder, StsEndpointParams> {
    private final Region region;
    private final Boolean useDualStack;
    private final Boolean useFIPS;
    private final String endpoint;
    private final Boolean useGlobalEndpoint;

    private StsEndpointParams(BuilderImpl builder) {
        this.region = builder.region;
        this.useDualStack = builder.useDualStack;
        this.useFIPS = builder.useFIPS;
        this.endpoint = builder.endpoint;
        this.useGlobalEndpoint = builder.useGlobalEndpoint;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public Region region() {
        return this.region;
    }

    public Boolean useDualStack() {
        return this.useDualStack;
    }

    public Boolean useFips() {
        return this.useFIPS;
    }

    public String endpoint() {
        return this.endpoint;
    }

    public Boolean useGlobalEndpoint() {
        return this.useGlobalEndpoint;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private static class BuilderImpl
    implements Builder {
        private Region region;
        private Boolean useDualStack = false;
        private Boolean useFIPS = false;
        private String endpoint;
        private Boolean useGlobalEndpoint = false;

        private BuilderImpl() {
        }

        private BuilderImpl(StsEndpointParams builder) {
            this.region = builder.region;
            this.useDualStack = builder.useDualStack;
            this.useFIPS = builder.useFIPS;
            this.endpoint = builder.endpoint;
            this.useGlobalEndpoint = builder.useGlobalEndpoint;
        }

        @Override
        public Builder region(Region region) {
            this.region = region;
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
        public Builder useFips(Boolean useFIPS) {
            this.useFIPS = useFIPS;
            if (this.useFIPS == null) {
                this.useFIPS = false;
            }
            return this;
        }

        @Override
        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
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
        public StsEndpointParams build() {
            return new StsEndpointParams(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, StsEndpointParams> {
        public Builder region(Region var1);

        public Builder useDualStack(Boolean var1);

        public Builder useFips(Boolean var1);

        public Builder endpoint(String var1);

        public Builder useGlobalEndpoint(Boolean var1);

        public StsEndpointParams build();
    }
}

