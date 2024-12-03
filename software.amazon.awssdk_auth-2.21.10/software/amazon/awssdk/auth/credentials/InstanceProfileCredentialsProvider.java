/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.exception.SdkServiceException
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSupplier
 *  software.amazon.awssdk.regions.util.HttpResourcesUtils
 *  software.amazon.awssdk.regions.util.ResourcesEndpointProvider
 *  software.amazon.awssdk.utils.ComparableUtils
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 *  software.amazon.awssdk.utils.cache.CachedSupplier
 *  software.amazon.awssdk.utils.cache.CachedSupplier$PrefetchStrategy
 *  software.amazon.awssdk.utils.cache.CachedSupplier$StaleValueBehavior
 *  software.amazon.awssdk.utils.cache.NonBlocking
 *  software.amazon.awssdk.utils.cache.RefreshResult
 */
package software.amazon.awssdk.auth.credentials;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.HttpCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.Ec2MetadataConfigProvider;
import software.amazon.awssdk.auth.credentials.internal.HttpCredentialsLoader;
import software.amazon.awssdk.auth.credentials.internal.StaticResourcesEndpointProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.regions.util.HttpResourcesUtils;
import software.amazon.awssdk.regions.util.ResourcesEndpointProvider;
import software.amazon.awssdk.utils.ComparableUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;
import software.amazon.awssdk.utils.cache.CachedSupplier;
import software.amazon.awssdk.utils.cache.NonBlocking;
import software.amazon.awssdk.utils.cache.RefreshResult;

@SdkPublicApi
public final class InstanceProfileCredentialsProvider
implements HttpCredentialsProvider,
ToCopyableBuilder<Builder, InstanceProfileCredentialsProvider> {
    private static final Logger log = Logger.loggerFor(InstanceProfileCredentialsProvider.class);
    private static final String EC2_METADATA_TOKEN_HEADER = "x-aws-ec2-metadata-token";
    private static final String SECURITY_CREDENTIALS_RESOURCE = "/latest/meta-data/iam/security-credentials/";
    private static final String TOKEN_RESOURCE = "/latest/api/token";
    private static final String EC2_METADATA_TOKEN_TTL_HEADER = "x-aws-ec2-metadata-token-ttl-seconds";
    private static final String DEFAULT_TOKEN_TTL = "21600";
    private final Clock clock;
    private final String endpoint;
    private final Ec2MetadataConfigProvider configProvider;
    private final HttpCredentialsLoader httpCredentialsLoader;
    private final CachedSupplier<AwsCredentials> credentialsCache;
    private final Boolean asyncCredentialUpdateEnabled;
    private final String asyncThreadName;
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private InstanceProfileCredentialsProvider(BuilderImpl builder) {
        this.clock = builder.clock;
        this.endpoint = builder.endpoint;
        this.asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled;
        this.asyncThreadName = builder.asyncThreadName;
        this.profileFile = builder.profileFile;
        this.profileName = builder.profileName;
        this.httpCredentialsLoader = HttpCredentialsLoader.create();
        this.configProvider = Ec2MetadataConfigProvider.builder().profileFile(builder.profileFile).profileName(builder.profileName).build();
        if (Boolean.TRUE.equals(builder.asyncCredentialUpdateEnabled)) {
            Validate.paramNotBlank((CharSequence)builder.asyncThreadName, (String)"asyncThreadName");
            this.credentialsCache = CachedSupplier.builder(this::refreshCredentials).cachedValueName(this.toString()).prefetchStrategy((CachedSupplier.PrefetchStrategy)new NonBlocking(builder.asyncThreadName)).staleValueBehavior(CachedSupplier.StaleValueBehavior.ALLOW).clock(this.clock).build();
        } else {
            this.credentialsCache = CachedSupplier.builder(this::refreshCredentials).cachedValueName(this.toString()).staleValueBehavior(CachedSupplier.StaleValueBehavior.ALLOW).clock(this.clock).build();
        }
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static InstanceProfileCredentialsProvider create() {
        return InstanceProfileCredentialsProvider.builder().build();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return (AwsCredentials)this.credentialsCache.get();
    }

    private RefreshResult<AwsCredentials> refreshCredentials() {
        if (this.isLocalCredentialLoadingDisabled()) {
            throw SdkClientException.create((String)"IMDS credentials have been disabled by environment variable or system property.");
        }
        try {
            HttpCredentialsLoader.LoadedCredentials credentials = this.httpCredentialsLoader.loadCredentials(this.createEndpointProvider());
            Instant expiration = credentials.getExpiration().orElse(null);
            log.debug(() -> "Loaded credentials from IMDS with expiration time of " + expiration);
            return RefreshResult.builder((Object)credentials.getAwsCredentials()).staleTime(this.staleTime(expiration)).prefetchTime(this.prefetchTime(expiration)).build();
        }
        catch (RuntimeException e) {
            throw SdkClientException.create((String)"Failed to load credentials from IMDS.", (Throwable)e);
        }
    }

    private boolean isLocalCredentialLoadingDisabled() {
        return SdkSystemSetting.AWS_EC2_METADATA_DISABLED.getBooleanValueOrThrow();
    }

    private Instant staleTime(Instant expiration) {
        if (expiration == null) {
            return null;
        }
        return expiration.minusSeconds(1L);
    }

    private Instant prefetchTime(Instant expiration) {
        Instant now = this.clock.instant();
        if (expiration == null) {
            return now.plus(60L, ChronoUnit.MINUTES);
        }
        Duration timeUntilExpiration = Duration.between(now, expiration);
        if (timeUntilExpiration.isNegative()) {
            return null;
        }
        return now.plus((TemporalAmount)((Object)ComparableUtils.maximum((Comparable[])new Duration[]{timeUntilExpiration.dividedBy(2L), Duration.ofMinutes(5L)})));
    }

    public void close() {
        this.credentialsCache.close();
    }

    public String toString() {
        return ToString.create((String)"InstanceProfileCredentialsProvider");
    }

    private ResourcesEndpointProvider createEndpointProvider() {
        String imdsHostname = this.getImdsEndpoint();
        String token = this.getToken(imdsHostname);
        String[] securityCredentials = this.getSecurityCredentials(imdsHostname, token);
        return new StaticResourcesEndpointProvider(URI.create(imdsHostname + SECURITY_CREDENTIALS_RESOURCE + securityCredentials[0]), this.getTokenHeaders(token));
    }

    private String getImdsEndpoint() {
        if (this.endpoint != null) {
            return this.endpoint;
        }
        return this.configProvider.getEndpoint();
    }

    private String getToken(String imdsHostname) {
        Map<String, String> tokenTtlHeaders = Collections.singletonMap(EC2_METADATA_TOKEN_TTL_HEADER, DEFAULT_TOKEN_TTL);
        StaticResourcesEndpointProvider tokenEndpoint = new StaticResourcesEndpointProvider(this.getTokenEndpoint(imdsHostname), tokenTtlHeaders);
        try {
            return HttpResourcesUtils.instance().readResource((ResourcesEndpointProvider)tokenEndpoint, "PUT");
        }
        catch (SdkServiceException e) {
            if (e.statusCode() == 400) {
                throw SdkClientException.builder().message("Unable to fetch metadata token.").cause((Throwable)e).build();
            }
            log.debug(() -> "Ignoring non-fatal exception while attempting to load metadata token from instance profile.", (Throwable)e);
            return null;
        }
        catch (Exception e) {
            log.debug(() -> "Ignoring non-fatal exception while attempting to load metadata token from instance profile.", (Throwable)e);
            return null;
        }
    }

    private URI getTokenEndpoint(String imdsHostname) {
        String finalHost = imdsHostname;
        if (finalHost.endsWith("/")) {
            finalHost = finalHost.substring(0, finalHost.length() - 1);
        }
        return URI.create(finalHost + TOKEN_RESOURCE);
    }

    private String[] getSecurityCredentials(String imdsHostname, String metadataToken) {
        StaticResourcesEndpointProvider securityCredentialsEndpoint = new StaticResourcesEndpointProvider(URI.create(imdsHostname + SECURITY_CREDENTIALS_RESOURCE), this.getTokenHeaders(metadataToken));
        String securityCredentialsList = (String)FunctionalUtils.invokeSafely(() -> HttpResourcesUtils.instance().readResource(securityCredentialsEndpoint));
        String[] securityCredentials = securityCredentialsList.trim().split("\n");
        if (securityCredentials.length == 0) {
            throw SdkClientException.builder().message("Unable to load credentials path").build();
        }
        return securityCredentials;
    }

    private Map<String, String> getTokenHeaders(String metadataToken) {
        if (metadataToken == null) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(EC2_METADATA_TOKEN_HEADER, metadataToken);
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    @SdkTestInternalApi
    static final class BuilderImpl
    implements Builder {
        private Clock clock = Clock.systemUTC();
        private String endpoint;
        private Boolean asyncCredentialUpdateEnabled;
        private String asyncThreadName;
        private Supplier<ProfileFile> profileFile;
        private String profileName;

        private BuilderImpl() {
            this.asyncThreadName("instance-profile-credentials-provider");
        }

        private BuilderImpl(InstanceProfileCredentialsProvider provider) {
            this.clock = provider.clock;
            this.endpoint = provider.endpoint;
            this.asyncCredentialUpdateEnabled = provider.asyncCredentialUpdateEnabled;
            this.asyncThreadName = provider.asyncThreadName;
            this.profileFile = provider.profileFile;
            this.profileName = provider.profileName;
        }

        Builder clock(Clock clock) {
            this.clock = clock;
            return this;
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
        public Builder profileFile(ProfileFile profileFile) {
            return this.profileFile((Supplier<ProfileFile>)Optional.ofNullable(profileFile).map(ProfileFileSupplier::fixedProfileFile).orElse(null));
        }

        public void setProfileFile(ProfileFile profileFile) {
            this.profileFile(profileFile);
        }

        @Override
        public Builder profileFile(Supplier<ProfileFile> profileFileSupplier) {
            this.profileFile = profileFileSupplier;
            return this;
        }

        public void setProfileFile(Supplier<ProfileFile> profileFileSupplier) {
            this.profileFile(profileFileSupplier);
        }

        @Override
        public Builder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public void setProfileName(String profileName) {
            this.profileName(profileName);
        }

        @Override
        public InstanceProfileCredentialsProvider build() {
            return new InstanceProfileCredentialsProvider(this);
        }
    }

    public static interface Builder
    extends HttpCredentialsProvider.Builder<InstanceProfileCredentialsProvider, Builder>,
    CopyableBuilder<Builder, InstanceProfileCredentialsProvider> {
        public Builder profileFile(ProfileFile var1);

        public Builder profileFile(Supplier<ProfileFile> var1);

        public Builder profileName(String var1);

        @Override
        public InstanceProfileCredentialsProvider build();
    }
}

