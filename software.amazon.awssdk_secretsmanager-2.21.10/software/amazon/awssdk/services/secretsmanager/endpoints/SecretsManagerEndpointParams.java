/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.secretsmanager.endpoints;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class SecretsManagerEndpointParams
implements ToCopyableBuilder<Builder, SecretsManagerEndpointParams> {
    private final Region region;
    private final Boolean useDualStack;
    private final Boolean useFIPS;
    private final String endpoint;

    private SecretsManagerEndpointParams(BuilderImpl builder) {
        this.region = builder.region;
        this.useDualStack = builder.useDualStack;
        this.useFIPS = builder.useFIPS;
        this.endpoint = builder.endpoint;
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

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private static class BuilderImpl
    implements Builder {
        private Region region;
        private Boolean useDualStack = false;
        private Boolean useFIPS = false;
        private String endpoint;

        private BuilderImpl() {
        }

        private BuilderImpl(SecretsManagerEndpointParams builder) {
            this.region = builder.region;
            this.useDualStack = builder.useDualStack;
            this.useFIPS = builder.useFIPS;
            this.endpoint = builder.endpoint;
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
        public SecretsManagerEndpointParams build() {
            return new SecretsManagerEndpointParams(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, SecretsManagerEndpointParams> {
        public Builder region(Region var1);

        public Builder useDualStack(Boolean var1);

        public Builder useFips(Boolean var1);

        public Builder endpoint(String var1);

        public SecretsManagerEndpointParams build();
    }
}

