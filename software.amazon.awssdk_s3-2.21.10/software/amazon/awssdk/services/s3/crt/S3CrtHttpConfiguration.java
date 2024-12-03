/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.crt;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.services.s3.crt.S3CrtConnectionHealthConfiguration;
import software.amazon.awssdk.services.s3.crt.S3CrtProxyConfiguration;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class S3CrtHttpConfiguration
implements ToCopyableBuilder<Builder, S3CrtHttpConfiguration> {
    private final Duration connectionTimeout;
    private final S3CrtProxyConfiguration proxyConfiguration;
    private final S3CrtConnectionHealthConfiguration healthConfiguration;
    private final Boolean trustAllCertificatesEnabled;

    private S3CrtHttpConfiguration(DefaultBuilder builder) {
        this.connectionTimeout = builder.connectionTimeout;
        this.proxyConfiguration = builder.proxyConfiguration;
        this.healthConfiguration = builder.healthConfiguration;
        this.trustAllCertificatesEnabled = builder.trustAllCertificatesEnabled;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Duration connectionTimeout() {
        return this.connectionTimeout;
    }

    public S3CrtProxyConfiguration proxyConfiguration() {
        return this.proxyConfiguration;
    }

    public S3CrtConnectionHealthConfiguration healthConfiguration() {
        return this.healthConfiguration;
    }

    public Boolean trustAllCertificatesEnabled() {
        return this.trustAllCertificatesEnabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3CrtHttpConfiguration that = (S3CrtHttpConfiguration)o;
        if (!Objects.equals(this.connectionTimeout, that.connectionTimeout)) {
            return false;
        }
        if (!Objects.equals((Object)this.proxyConfiguration, (Object)that.proxyConfiguration)) {
            return false;
        }
        if (!Objects.equals((Object)this.healthConfiguration, (Object)that.healthConfiguration)) {
            return false;
        }
        return Objects.equals(this.trustAllCertificatesEnabled, that.trustAllCertificatesEnabled);
    }

    public int hashCode() {
        int result = this.connectionTimeout != null ? this.connectionTimeout.hashCode() : 0;
        result = 31 * result + (this.proxyConfiguration != null ? this.proxyConfiguration.hashCode() : 0);
        result = 31 * result + (this.healthConfiguration != null ? this.healthConfiguration.hashCode() : 0);
        result = 31 * result + (this.trustAllCertificatesEnabled != null ? this.trustAllCertificatesEnabled.hashCode() : 0);
        return result;
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    private static final class DefaultBuilder
    implements Builder {
        private S3CrtConnectionHealthConfiguration healthConfiguration;
        private Duration connectionTimeout;
        private Boolean trustAllCertificatesEnabled;
        private S3CrtProxyConfiguration proxyConfiguration;

        private DefaultBuilder() {
        }

        private DefaultBuilder(S3CrtHttpConfiguration httpConfiguration) {
            this.healthConfiguration = httpConfiguration.healthConfiguration;
            this.connectionTimeout = httpConfiguration.connectionTimeout;
            this.proxyConfiguration = httpConfiguration.proxyConfiguration;
            this.trustAllCertificatesEnabled = httpConfiguration.trustAllCertificatesEnabled;
        }

        @Override
        public Builder connectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        @Override
        public Builder trustAllCertificatesEnabled(Boolean trustAllCertificatesEnabled) {
            this.trustAllCertificatesEnabled = trustAllCertificatesEnabled;
            return this;
        }

        @Override
        public Builder proxyConfiguration(S3CrtProxyConfiguration proxyConfiguration) {
            this.proxyConfiguration = proxyConfiguration;
            return this;
        }

        @Override
        public Builder proxyConfiguration(Consumer<S3CrtProxyConfiguration.Builder> configurationBuilder) {
            return this.proxyConfiguration(((S3CrtProxyConfiguration.Builder)S3CrtProxyConfiguration.builder().applyMutation(configurationBuilder)).build());
        }

        @Override
        public Builder connectionHealthConfiguration(S3CrtConnectionHealthConfiguration healthConfiguration) {
            this.healthConfiguration = healthConfiguration;
            return this;
        }

        @Override
        public Builder connectionHealthConfiguration(Consumer<S3CrtConnectionHealthConfiguration.Builder> configurationBuilder) {
            return this.connectionHealthConfiguration(((S3CrtConnectionHealthConfiguration.Builder)S3CrtConnectionHealthConfiguration.builder().applyMutation(configurationBuilder)).build());
        }

        @Override
        public S3CrtHttpConfiguration build() {
            return new S3CrtHttpConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, S3CrtHttpConfiguration> {
        public Builder connectionTimeout(Duration var1);

        public Builder trustAllCertificatesEnabled(Boolean var1);

        public Builder proxyConfiguration(S3CrtProxyConfiguration var1);

        public Builder proxyConfiguration(Consumer<S3CrtProxyConfiguration.Builder> var1);

        public Builder connectionHealthConfiguration(S3CrtConnectionHealthConfiguration var1);

        public Builder connectionHealthConfiguration(Consumer<S3CrtConnectionHealthConfiguration.Builder> var1);

        public S3CrtHttpConfiguration build();
    }
}

