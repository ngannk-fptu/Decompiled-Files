/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.auth.token.credentials.SdkToken
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.cache.CachedSupplier
 *  software.amazon.awssdk.utils.cache.CachedSupplier$Builder
 *  software.amazon.awssdk.utils.cache.CachedSupplier$PrefetchStrategy
 *  software.amazon.awssdk.utils.cache.NonBlocking
 *  software.amazon.awssdk.utils.cache.RefreshResult
 */
package software.amazon.awssdk.awscore.internal.token;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.awscore.internal.token.TokenRefresher;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.cache.CachedSupplier;
import software.amazon.awssdk.utils.cache.NonBlocking;
import software.amazon.awssdk.utils.cache.RefreshResult;

@ThreadSafe
@SdkInternalApi
public final class CachedTokenRefresher<TokenT extends SdkToken>
implements TokenRefresher<TokenT> {
    private static final Duration DEFAULT_STALE_TIME = Duration.ofMinutes(1L);
    private static final String THREAD_CLASS_NAME = "sdk-token-refresher";
    private final Supplier<TokenT> tokenRetriever;
    private final Duration staleDuration;
    private final Duration prefetchDuration;
    private final Function<SdkException, TokenT> exceptionHandler;
    private final CachedSupplier<TokenT> tokenCacheSupplier;

    private CachedTokenRefresher(Builder builder) {
        Validate.paramNotNull((Object)builder.tokenRetriever, (String)"tokenRetriever");
        this.staleDuration = builder.staleDuration == null ? DEFAULT_STALE_TIME : builder.staleDuration;
        this.prefetchDuration = builder.prefetchDuration == null ? this.staleDuration : builder.prefetchDuration;
        Function defaultExceptionHandler = exp -> {
            throw exp;
        };
        this.exceptionHandler = builder.exceptionHandler == null ? defaultExceptionHandler : builder.exceptionHandler;
        this.tokenRetriever = builder.tokenRetriever;
        CachedSupplier.Builder cachedBuilder = CachedSupplier.builder(this::refreshResult).cachedValueName("SsoOidcTokenProvider()");
        if (builder.asyncRefreshEnabled.booleanValue()) {
            cachedBuilder.prefetchStrategy((CachedSupplier.PrefetchStrategy)new NonBlocking(THREAD_CLASS_NAME));
        }
        this.tokenCacheSupplier = cachedBuilder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TokenT refreshIfStaleAndFetch() {
        return (TokenT)((SdkToken)this.tokenCacheSupplier.get());
    }

    private TokenT refreshAndGetTokenFromSupplier() {
        try {
            SdkToken freshToken = (SdkToken)this.tokenRetriever.get();
            return (TokenT)freshToken;
        }
        catch (SdkException exception) {
            return (TokenT)((SdkToken)this.exceptionHandler.apply(exception));
        }
    }

    private RefreshResult<TokenT> refreshResult() {
        TokenT tokenT = this.refreshAndGetTokenFromSupplier();
        Instant staleTime = tokenT.expirationTime().isPresent() ? ((Instant)tokenT.expirationTime().get()).minus(this.staleDuration) : Instant.now();
        Instant prefetchTime = tokenT.expirationTime().isPresent() ? ((Instant)tokenT.expirationTime().get()).minus(this.prefetchDuration) : null;
        return RefreshResult.builder(tokenT).staleTime(staleTime).prefetchTime(prefetchTime).build();
    }

    public void close() {
        this.tokenCacheSupplier.close();
    }

    public static class Builder<TokenT extends SdkToken> {
        private Function<SdkException, TokenT> exceptionHandler;
        private Duration staleDuration;
        private Duration prefetchDuration;
        private Supplier<TokenT> tokenRetriever;
        private Boolean asyncRefreshEnabled = false;

        public Builder tokenRetriever(Supplier<TokenT> tokenRetriever) {
            this.tokenRetriever = tokenRetriever;
            return this;
        }

        public Builder staleDuration(Duration staleDuration) {
            this.staleDuration = staleDuration;
            return this;
        }

        public Builder prefetchTime(Duration prefetchTime) {
            this.prefetchDuration = prefetchTime;
            return this;
        }

        public Builder asyncRefreshEnabled(Boolean asyncRefreshEnabled) {
            this.asyncRefreshEnabled = asyncRefreshEnabled;
            return this;
        }

        public Builder exceptionHandler(Function<SdkException, TokenT> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public CachedTokenRefresher build() {
            CachedTokenRefresher cachedTokenRefresher = new CachedTokenRefresher(this);
            return cachedTokenRefresher;
        }
    }
}

