/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.util.SdkUserAgent
 *  software.amazon.awssdk.regions.util.ResourcesEndpointProvider
 *  software.amazon.awssdk.regions.util.ResourcesEndpointRetryPolicy
 *  software.amazon.awssdk.utils.ComparableUtils
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 *  software.amazon.awssdk.utils.cache.CachedSupplier
 *  software.amazon.awssdk.utils.cache.CachedSupplier$PrefetchStrategy
 *  software.amazon.awssdk.utils.cache.NonBlocking
 *  software.amazon.awssdk.utils.cache.RefreshResult
 */
package software.amazon.awssdk.auth.credentials;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.HttpCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.ContainerCredentialsRetryPolicy;
import software.amazon.awssdk.auth.credentials.internal.HttpCredentialsLoader;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.util.SdkUserAgent;
import software.amazon.awssdk.regions.util.ResourcesEndpointProvider;
import software.amazon.awssdk.regions.util.ResourcesEndpointRetryPolicy;
import software.amazon.awssdk.utils.ComparableUtils;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;
import software.amazon.awssdk.utils.cache.CachedSupplier;
import software.amazon.awssdk.utils.cache.NonBlocking;
import software.amazon.awssdk.utils.cache.RefreshResult;

@SdkPublicApi
public final class ContainerCredentialsProvider
implements HttpCredentialsProvider,
ToCopyableBuilder<Builder, ContainerCredentialsProvider> {
    private static final Predicate<InetAddress> IS_LOOPBACK_ADDRESS = InetAddress::isLoopbackAddress;
    private static final Predicate<InetAddress> ALLOWED_HOSTS_RULES = IS_LOOPBACK_ADDRESS;
    private static final String HTTPS = "https";
    private final String endpoint;
    private final HttpCredentialsLoader httpCredentialsLoader;
    private final CachedSupplier<AwsCredentials> credentialsCache;
    private final Boolean asyncCredentialUpdateEnabled;
    private final String asyncThreadName;

    private ContainerCredentialsProvider(BuilderImpl builder) {
        this.endpoint = builder.endpoint;
        this.asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled;
        this.asyncThreadName = builder.asyncThreadName;
        this.httpCredentialsLoader = HttpCredentialsLoader.create();
        if (Boolean.TRUE.equals(builder.asyncCredentialUpdateEnabled)) {
            Validate.paramNotBlank((CharSequence)builder.asyncThreadName, (String)"asyncThreadName");
            this.credentialsCache = CachedSupplier.builder(this::refreshCredentials).cachedValueName(this.toString()).prefetchStrategy((CachedSupplier.PrefetchStrategy)new NonBlocking(builder.asyncThreadName)).build();
        } else {
            this.credentialsCache = CachedSupplier.builder(this::refreshCredentials).cachedValueName(this.toString()).build();
        }
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public String toString() {
        return ToString.create((String)"ContainerCredentialsProvider");
    }

    private RefreshResult<AwsCredentials> refreshCredentials() {
        HttpCredentialsLoader.LoadedCredentials loadedCredentials = this.httpCredentialsLoader.loadCredentials(new ContainerCredentialsEndpointProvider(this.endpoint));
        Instant expiration = loadedCredentials.getExpiration().orElse(null);
        return RefreshResult.builder((Object)loadedCredentials.getAwsCredentials()).staleTime(this.staleTime(expiration)).prefetchTime(this.prefetchTime(expiration)).build();
    }

    private Instant staleTime(Instant expiration) {
        if (expiration == null) {
            return null;
        }
        return expiration.minus(1L, ChronoUnit.MINUTES);
    }

    private Instant prefetchTime(Instant expiration) {
        Instant oneHourFromNow = Instant.now().plus(1L, ChronoUnit.HOURS);
        if (expiration == null) {
            return oneHourFromNow;
        }
        Instant fifteenMinutesBeforeExpiration = expiration.minus(15L, ChronoUnit.MINUTES);
        return (Instant)ComparableUtils.minimum((Comparable[])new Instant[]{oneHourFromNow, fifteenMinutesBeforeExpiration});
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return (AwsCredentials)this.credentialsCache.get();
    }

    public void close() {
        this.credentialsCache.close();
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private static final class BuilderImpl
    implements Builder {
        private String endpoint;
        private Boolean asyncCredentialUpdateEnabled;
        private String asyncThreadName;

        private BuilderImpl() {
            this.asyncThreadName("container-credentials-provider");
        }

        private BuilderImpl(ContainerCredentialsProvider credentialsProvider) {
            this.endpoint = credentialsProvider.endpoint;
            this.asyncCredentialUpdateEnabled = credentialsProvider.asyncCredentialUpdateEnabled;
            this.asyncThreadName = credentialsProvider.asyncThreadName;
        }

        @Override
        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint(endpoint);
        }

        @Override
        public Builder asyncCredentialUpdateEnabled(Boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
            return this;
        }

        public void setAsyncCredentialUpdateEnabled(boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled(asyncCredentialUpdateEnabled);
        }

        @Override
        public Builder asyncThreadName(String asyncThreadName) {
            this.asyncThreadName = asyncThreadName;
            return this;
        }

        public void setAsyncThreadName(String asyncThreadName) {
            this.asyncThreadName(asyncThreadName);
        }

        @Override
        public ContainerCredentialsProvider build() {
            return new ContainerCredentialsProvider(this);
        }
    }

    public static interface Builder
    extends HttpCredentialsProvider.Builder<ContainerCredentialsProvider, Builder>,
    CopyableBuilder<Builder, ContainerCredentialsProvider> {
    }

    static final class ContainerCredentialsEndpointProvider
    implements ResourcesEndpointProvider {
        private final String endpoint;

        ContainerCredentialsEndpointProvider(String endpoint) {
            this.endpoint = endpoint;
        }

        public URI endpoint() throws IOException {
            if (!SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_RELATIVE_URI.getStringValue().isPresent() && !SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_FULL_URI.getStringValue().isPresent()) {
                throw SdkClientException.builder().message(String.format("Cannot fetch credentials from container - neither %s or %s environment variables are set.", SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_FULL_URI.environmentVariable(), SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_RELATIVE_URI.environmentVariable())).build();
            }
            try {
                return SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_RELATIVE_URI.getStringValue().map(this::createUri).orElseGet(this::createGenericContainerUrl);
            }
            catch (SdkClientException e) {
                throw e;
            }
            catch (Exception e) {
                throw SdkClientException.builder().message("Unable to fetch credentials from container.").cause((Throwable)e).build();
            }
        }

        public ResourcesEndpointRetryPolicy retryPolicy() {
            return new ContainerCredentialsRetryPolicy();
        }

        public Map<String, String> headers() {
            HashMap<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("User-Agent", SdkUserAgent.create().userAgent());
            SdkSystemSetting.AWS_CONTAINER_AUTHORIZATION_TOKEN.getStringValue().filter(StringUtils::isNotBlank).ifPresent(t -> requestHeaders.put("Authorization", (String)t));
            return requestHeaders;
        }

        private URI createUri(String relativeUri) {
            String host = this.endpoint != null ? this.endpoint : SdkSystemSetting.AWS_CONTAINER_SERVICE_ENDPOINT.getStringValueOrThrow();
            return URI.create(host + relativeUri);
        }

        private URI createGenericContainerUrl() {
            URI uri = URI.create(SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_FULL_URI.getStringValueOrThrow());
            if (!this.isHttps(uri) && !this.isAllowedHost(uri.getHost())) {
                String envVarName = SdkSystemSetting.AWS_CONTAINER_CREDENTIALS_FULL_URI.environmentVariable();
                throw SdkClientException.builder().message(String.format("The full URI (%s) contained within environment variable %s has an invalid host. Host should resolve to a loopback address or have the full URI be HTTPS.", uri, envVarName)).build();
            }
            return uri;
        }

        private boolean isHttps(URI endpoint) {
            return Objects.equals(ContainerCredentialsProvider.HTTPS, endpoint.getScheme());
        }

        private boolean isAllowedHost(String host) {
            try {
                InetAddress[] addresses = InetAddress.getAllByName(host);
                return addresses.length > 0 && Arrays.stream(addresses).allMatch(this::matchesAllowedHostRules);
            }
            catch (UnknownHostException e) {
                throw SdkClientException.builder().cause((Throwable)e).message(String.format("host (%s) could not be resolved to an IP address.", host)).build();
            }
        }

        private boolean matchesAllowedHostRules(InetAddress inetAddress) {
            return ALLOWED_HOSTS_RULES.test(inetAddress);
        }
    }
}

