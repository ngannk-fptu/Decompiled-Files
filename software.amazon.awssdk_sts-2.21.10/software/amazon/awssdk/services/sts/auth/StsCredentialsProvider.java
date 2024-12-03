/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.auth.credentials.AwsCredentials
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.AwsSessionCredentials
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 *  software.amazon.awssdk.utils.cache.CachedSupplier
 *  software.amazon.awssdk.utils.cache.CachedSupplier$Builder
 *  software.amazon.awssdk.utils.cache.CachedSupplier$PrefetchStrategy
 *  software.amazon.awssdk.utils.cache.NonBlocking
 *  software.amazon.awssdk.utils.cache.RefreshResult
 */
package software.amazon.awssdk.services.sts.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;
import software.amazon.awssdk.utils.cache.CachedSupplier;
import software.amazon.awssdk.utils.cache.NonBlocking;
import software.amazon.awssdk.utils.cache.RefreshResult;

@ThreadSafe
@SdkPublicApi
public abstract class StsCredentialsProvider
implements AwsCredentialsProvider,
SdkAutoCloseable {
    private static final Logger log = Logger.loggerFor(StsCredentialsProvider.class);
    private static final Duration DEFAULT_STALE_TIME = Duration.ofMinutes(1L);
    private static final Duration DEFAULT_PREFETCH_TIME = Duration.ofMinutes(5L);
    final StsClient stsClient;
    private final CachedSupplier<AwsSessionCredentials> sessionCache;
    private final Duration staleTime;
    private final Duration prefetchTime;
    private final Boolean asyncCredentialUpdateEnabled;

    StsCredentialsProvider(BaseBuilder<?, ?> builder, String asyncThreadName) {
        this.stsClient = (StsClient)Validate.notNull((Object)((BaseBuilder)builder).stsClient, (String)"STS client must not be null.", (Object[])new Object[0]);
        this.staleTime = Optional.ofNullable(((BaseBuilder)builder).staleTime).orElse(DEFAULT_STALE_TIME);
        this.prefetchTime = Optional.ofNullable(((BaseBuilder)builder).prefetchTime).orElse(DEFAULT_PREFETCH_TIME);
        this.asyncCredentialUpdateEnabled = ((BaseBuilder)builder).asyncCredentialUpdateEnabled;
        CachedSupplier.Builder cacheBuilder = CachedSupplier.builder(this::updateSessionCredentials).cachedValueName(this.toString());
        if (((BaseBuilder)builder).asyncCredentialUpdateEnabled.booleanValue()) {
            cacheBuilder.prefetchStrategy((CachedSupplier.PrefetchStrategy)new NonBlocking(asyncThreadName));
        }
        this.sessionCache = cacheBuilder.build();
    }

    private RefreshResult<AwsSessionCredentials> updateSessionCredentials() {
        AwsSessionCredentials credentials = this.getUpdatedCredentials(this.stsClient);
        Instant actualTokenExpiration = (Instant)credentials.expirationTime().orElseThrow(() -> new IllegalStateException("Sourced credentials have no expiration value"));
        return RefreshResult.builder((Object)credentials).staleTime(actualTokenExpiration.minus(this.staleTime)).prefetchTime(actualTokenExpiration.minus(this.prefetchTime)).build();
    }

    public AwsCredentials resolveCredentials() {
        AwsSessionCredentials credentials = (AwsSessionCredentials)this.sessionCache.get();
        credentials.expirationTime().ifPresent(t -> log.debug(() -> "Using STS credentials with expiration time of " + t));
        return credentials;
    }

    public void close() {
        this.sessionCache.close();
    }

    public Duration staleTime() {
        return this.staleTime;
    }

    public Duration prefetchTime() {
        return this.prefetchTime;
    }

    abstract AwsSessionCredentials getUpdatedCredentials(StsClient var1);

    @NotThreadSafe
    @SdkPublicApi
    public static abstract class BaseBuilder<B extends BaseBuilder<B, T>, T extends ToCopyableBuilder<B, T>>
    implements CopyableBuilder<B, T> {
        private final Function<B, T> providerConstructor;
        private Boolean asyncCredentialUpdateEnabled = false;
        private StsClient stsClient;
        private Duration staleTime;
        private Duration prefetchTime;

        BaseBuilder(Function<B, T> providerConstructor) {
            this.providerConstructor = providerConstructor;
        }

        BaseBuilder(Function<B, T> providerConstructor, StsCredentialsProvider provider) {
            this.providerConstructor = providerConstructor;
            this.asyncCredentialUpdateEnabled = provider.asyncCredentialUpdateEnabled;
            this.stsClient = provider.stsClient;
            this.staleTime = provider.staleTime;
            this.prefetchTime = provider.prefetchTime;
        }

        public B stsClient(StsClient stsClient) {
            this.stsClient = stsClient;
            return (B)this;
        }

        public B asyncCredentialUpdateEnabled(Boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
            return (B)this;
        }

        public B staleTime(Duration staleTime) {
            this.staleTime = staleTime;
            return (B)this;
        }

        public B prefetchTime(Duration prefetchTime) {
            this.prefetchTime = prefetchTime;
            return (B)this;
        }

        public T build() {
            return (T)((ToCopyableBuilder)this.providerConstructor.apply(this));
        }
    }
}

