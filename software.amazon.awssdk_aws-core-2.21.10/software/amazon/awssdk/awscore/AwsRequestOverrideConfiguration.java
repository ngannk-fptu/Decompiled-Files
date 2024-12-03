/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.CredentialUtils
 *  software.amazon.awssdk.core.RequestOverrideConfiguration
 *  software.amazon.awssdk.core.RequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.RequestOverrideConfiguration$BuilderImpl
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.awscore;

import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkPublicApi
public final class AwsRequestOverrideConfiguration
extends RequestOverrideConfiguration {
    private final IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;

    private AwsRequestOverrideConfiguration(BuilderImpl builder) {
        super((RequestOverrideConfiguration.Builder)builder);
        this.credentialsProvider = builder.awsCredentialsProvider;
    }

    public static AwsRequestOverrideConfiguration from(RequestOverrideConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        if (configuration instanceof AwsRequestOverrideConfiguration) {
            return (AwsRequestOverrideConfiguration)configuration;
        }
        return new BuilderImpl(configuration).build();
    }

    public Optional<AwsCredentialsProvider> credentialsProvider() {
        return Optional.ofNullable(CredentialUtils.toCredentialsProvider(this.credentialsProvider));
    }

    public Optional<IdentityProvider<? extends AwsCredentialsIdentity>> credentialsIdentityProvider() {
        return Optional.ofNullable(this.credentialsProvider);
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AwsRequestOverrideConfiguration that = (AwsRequestOverrideConfiguration)((Object)o);
        return Objects.equals(this.credentialsProvider, that.credentialsProvider);
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.credentialsProvider);
        return hashCode;
    }

    private static final class BuilderImpl
    extends RequestOverrideConfiguration.BuilderImpl<Builder>
    implements Builder {
        private IdentityProvider<? extends AwsCredentialsIdentity> awsCredentialsProvider;

        private BuilderImpl() {
        }

        private BuilderImpl(RequestOverrideConfiguration requestOverrideConfiguration) {
            super(requestOverrideConfiguration);
        }

        private BuilderImpl(AwsRequestOverrideConfiguration awsRequestOverrideConfig) {
            super((RequestOverrideConfiguration)awsRequestOverrideConfig);
            this.awsCredentialsProvider = awsRequestOverrideConfig.credentialsProvider;
        }

        @Override
        public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            this.awsCredentialsProvider = credentialsProvider;
            return this;
        }

        @Override
        public AwsCredentialsProvider credentialsProvider() {
            return CredentialUtils.toCredentialsProvider(this.awsCredentialsProvider);
        }

        @Override
        public AwsRequestOverrideConfiguration build() {
            return new AwsRequestOverrideConfiguration(this);
        }
    }

    public static interface Builder
    extends RequestOverrideConfiguration.Builder<Builder>,
    SdkBuilder<Builder, AwsRequestOverrideConfiguration> {
        default public Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
            return this.credentialsProvider((IdentityProvider<? extends AwsCredentialsIdentity>)credentialsProvider);
        }

        default public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            throw new UnsupportedOperationException();
        }

        public AwsCredentialsProvider credentialsProvider();

        public AwsRequestOverrideConfiguration build();
    }
}

