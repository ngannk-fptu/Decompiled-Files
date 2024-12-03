/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
 *  software.amazon.awssdk.crt.auth.credentials.CredentialsProvider
 *  software.amazon.awssdk.crt.http.HttpMonitoringOptions
 *  software.amazon.awssdk.crt.http.HttpProxyOptions
 *  software.amazon.awssdk.crt.io.ClientBootstrap
 *  software.amazon.awssdk.crt.io.StandardRetryOptions
 *  software.amazon.awssdk.crt.io.TlsCipherPreference
 *  software.amazon.awssdk.crt.io.TlsContext
 *  software.amazon.awssdk.crt.io.TlsContextOptions
 *  software.amazon.awssdk.crtcore.CrtConfigurationUtils
 *  software.amazon.awssdk.crtcore.CrtConnectionHealthConfiguration
 *  software.amazon.awssdk.crtcore.CrtProxyConfiguration
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.net.URI;
import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.crt.auth.credentials.CredentialsProvider;
import software.amazon.awssdk.crt.http.HttpMonitoringOptions;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.StandardRetryOptions;
import software.amazon.awssdk.crt.io.TlsCipherPreference;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crtcore.CrtConfigurationUtils;
import software.amazon.awssdk.crtcore.CrtConnectionHealthConfiguration;
import software.amazon.awssdk.crtcore.CrtProxyConfiguration;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.crt.S3CrtHttpConfiguration;
import software.amazon.awssdk.services.s3.internal.crt.CrtCredentialsProviderAdapter;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkInternalApi
public class S3NativeClientConfiguration
implements SdkAutoCloseable {
    static final long DEFAULT_PART_SIZE_IN_BYTES = 0x800000L;
    private static final Logger log = Logger.loggerFor(S3NativeClientConfiguration.class);
    private static final long DEFAULT_TARGET_THROUGHPUT_IN_GBPS = 10L;
    private final String signingRegion;
    private final StandardRetryOptions standardRetryOptions;
    private final ClientBootstrap clientBootstrap;
    private final CrtCredentialsProviderAdapter credentialProviderAdapter;
    private final CredentialsProvider credentialsProvider;
    private final long partSizeInBytes;
    private final long thresholdInBytes;
    private final double targetThroughputInGbps;
    private final int maxConcurrency;
    private final URI endpointOverride;
    private final boolean checksumValidationEnabled;
    private final Long readBufferSizeInBytes;
    private final TlsContext tlsContext;
    private final HttpProxyOptions proxyOptions;
    private final Duration connectionTimeout;
    private final HttpMonitoringOptions httpMonitoringOptions;
    private final Boolean useEnvironmentVariableProxyOptionsValues;

    public S3NativeClientConfiguration(Builder builder) {
        this.signingRegion = builder.signingRegion == null ? DefaultAwsRegionProviderChain.builder().build().getRegion().id() : builder.signingRegion;
        this.clientBootstrap = new ClientBootstrap(null, null);
        TlsContextOptions clientTlsContextOptions = TlsContextOptions.createDefaultClient().withCipherPreference(TlsCipherPreference.TLS_CIPHER_SYSTEM_DEFAULT);
        if (builder.httpConfiguration != null && builder.httpConfiguration.trustAllCertificatesEnabled() != null) {
            log.warn(() -> "SSL Certificate verification is disabled. This is not a safe setting and should only be used for testing.");
            clientTlsContextOptions.withVerifyPeer(builder.httpConfiguration.trustAllCertificatesEnabled() == false);
        }
        this.tlsContext = new TlsContext(clientTlsContextOptions);
        this.credentialProviderAdapter = builder.credentialsProvider == null ? new CrtCredentialsProviderAdapter((IdentityProvider<? extends AwsCredentialsIdentity>)DefaultCredentialsProvider.create()) : new CrtCredentialsProviderAdapter((IdentityProvider<? extends AwsCredentialsIdentity>)builder.credentialsProvider);
        this.credentialsProvider = this.credentialProviderAdapter.crtCredentials();
        this.partSizeInBytes = builder.partSizeInBytes == null ? 0x800000L : builder.partSizeInBytes;
        this.thresholdInBytes = builder.thresholdInBytes == null ? this.partSizeInBytes : builder.thresholdInBytes;
        this.targetThroughputInGbps = builder.targetThroughputInGbps == null ? 10.0 : builder.targetThroughputInGbps;
        this.maxConcurrency = builder.maxConcurrency == null ? 0 : builder.maxConcurrency;
        this.endpointOverride = builder.endpointOverride;
        this.checksumValidationEnabled = builder.checksumValidationEnabled == null || builder.checksumValidationEnabled != false;
        this.readBufferSizeInBytes = builder.readBufferSizeInBytes == null ? this.partSizeInBytes * 10L : builder.readBufferSizeInBytes;
        if (builder.httpConfiguration != null) {
            this.proxyOptions = CrtConfigurationUtils.resolveProxy((CrtProxyConfiguration)builder.httpConfiguration.proxyConfiguration(), (TlsContext)this.tlsContext).orElse(null);
            this.connectionTimeout = builder.httpConfiguration.connectionTimeout();
            this.httpMonitoringOptions = CrtConfigurationUtils.resolveHttpMonitoringOptions((CrtConnectionHealthConfiguration)builder.httpConfiguration.healthConfiguration()).orElse(null);
        } else {
            this.proxyOptions = null;
            this.connectionTimeout = null;
            this.httpMonitoringOptions = null;
        }
        this.standardRetryOptions = builder.standardRetryOptions;
        this.useEnvironmentVariableProxyOptionsValues = S3NativeClientConfiguration.resolveUseEnvironmentVariableValues(builder);
    }

    private static Boolean resolveUseEnvironmentVariableValues(Builder builder) {
        if (builder != null && builder.httpConfiguration != null && builder.httpConfiguration.proxyConfiguration() != null) {
            return builder.httpConfiguration.proxyConfiguration().isUseEnvironmentVariableValues();
        }
        return true;
    }

    public Boolean isUseEnvironmentVariableValues() {
        return this.useEnvironmentVariableProxyOptionsValues;
    }

    public HttpMonitoringOptions httpMonitoringOptions() {
        return this.httpMonitoringOptions;
    }

    public HttpProxyOptions proxyOptions() {
        return this.proxyOptions;
    }

    public Duration connectionTimeout() {
        return this.connectionTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String signingRegion() {
        return this.signingRegion;
    }

    public ClientBootstrap clientBootstrap() {
        return this.clientBootstrap;
    }

    public CredentialsProvider credentialsProvider() {
        return this.credentialsProvider;
    }

    public TlsContext tlsContext() {
        return this.tlsContext;
    }

    public long partSizeBytes() {
        return this.partSizeInBytes;
    }

    public long thresholdInBytes() {
        return this.thresholdInBytes;
    }

    public double targetThroughputInGbps() {
        return this.targetThroughputInGbps;
    }

    public int maxConcurrency() {
        return this.maxConcurrency;
    }

    public StandardRetryOptions standardRetryOptions() {
        return this.standardRetryOptions;
    }

    public URI endpointOverride() {
        return this.endpointOverride;
    }

    public boolean checksumValidationEnabled() {
        return this.checksumValidationEnabled;
    }

    public Long readBufferSizeInBytes() {
        return this.readBufferSizeInBytes;
    }

    public void close() {
        this.clientBootstrap.close();
        this.tlsContext.close();
        this.credentialProviderAdapter.close();
    }

    public static final class Builder {
        private Long readBufferSizeInBytes;
        private String signingRegion;
        private IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;
        private Long partSizeInBytes;
        private Double targetThroughputInGbps;
        private Integer maxConcurrency;
        private URI endpointOverride;
        private Boolean checksumValidationEnabled;
        private S3CrtHttpConfiguration httpConfiguration;
        private StandardRetryOptions standardRetryOptions;
        private Long thresholdInBytes;

        private Builder() {
        }

        public Builder signingRegion(String signingRegion) {
            this.signingRegion = signingRegion;
            return this;
        }

        public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        public Builder partSizeInBytes(Long partSizeInBytes) {
            this.partSizeInBytes = partSizeInBytes;
            return this;
        }

        public Builder targetThroughputInGbps(Double targetThroughputInGbps) {
            this.targetThroughputInGbps = targetThroughputInGbps;
            return this;
        }

        public Builder maxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
            return this;
        }

        public Builder endpointOverride(URI endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }

        public Builder checksumValidationEnabled(Boolean checksumValidationEnabled) {
            this.checksumValidationEnabled = checksumValidationEnabled;
            return this;
        }

        public S3NativeClientConfiguration build() {
            return new S3NativeClientConfiguration(this);
        }

        public Builder readBufferSizeInBytes(Long readBufferSizeInBytes) {
            this.readBufferSizeInBytes = readBufferSizeInBytes;
            return this;
        }

        public Builder httpConfiguration(S3CrtHttpConfiguration httpConfiguration) {
            this.httpConfiguration = httpConfiguration;
            return this;
        }

        public Builder standardRetryOptions(StandardRetryOptions standardRetryOptions) {
            this.standardRetryOptions = standardRetryOptions;
            return this;
        }

        public Builder thresholdInBytes(Long thresholdInBytes) {
            this.thresholdInBytes = thresholdInBytes;
            return this;
        }
    }
}

