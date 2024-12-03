/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.auth.scheme.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeParams;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultSecretsManagerAuthSchemeParams
implements SecretsManagerAuthSchemeParams {
    private final String operation;
    private final Region region;

    private DefaultSecretsManagerAuthSchemeParams(Builder builder) {
        this.operation = Validate.paramNotNull(builder.operation, "operation");
        this.region = builder.region;
    }

    public static SecretsManagerAuthSchemeParams.Builder builder() {
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
    public SecretsManagerAuthSchemeParams.Builder toBuilder() {
        return new Builder(this);
    }

    private static final class Builder
    implements SecretsManagerAuthSchemeParams.Builder {
        private String operation;
        private Region region;

        Builder() {
        }

        Builder(DefaultSecretsManagerAuthSchemeParams params) {
            this.operation = params.operation;
            this.region = params.region;
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
        public SecretsManagerAuthSchemeParams build() {
            return new DefaultSecretsManagerAuthSchemeParams(this);
        }
    }
}

