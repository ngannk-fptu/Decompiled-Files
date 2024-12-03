/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.TokenIdentity
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.token.credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.auth.token.credentials.internal.TokenUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.TokenIdentity;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class SdkTokenProviderChain
implements SdkTokenProvider,
SdkAutoCloseable {
    private static final Logger log = Logger.loggerFor(SdkTokenProviderChain.class);
    private final List<IdentityProvider<? extends TokenIdentity>> sdkTokenProviders;
    private final boolean reuseLastProviderEnabled;
    private volatile IdentityProvider<? extends TokenIdentity> lastUsedProvider;

    private SdkTokenProviderChain(BuilderImpl builder) {
        Validate.notEmpty((Collection)builder.tokenProviders, (String)"No token providers were specified.", (Object[])new Object[0]);
        this.reuseLastProviderEnabled = builder.reuseLastProviderEnabled;
        this.sdkTokenProviders = Collections.unmodifiableList(builder.tokenProviders);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static SdkTokenProviderChain of(SdkTokenProvider ... sdkTokenProviders) {
        return SdkTokenProviderChain.builder().tokenProviders(sdkTokenProviders).build();
    }

    public static SdkTokenProviderChain of(IdentityProvider<? extends TokenIdentity> ... sdkTokenProviders) {
        return SdkTokenProviderChain.builder().tokenProviders(sdkTokenProviders).build();
    }

    @Override
    public SdkToken resolveToken() {
        if (this.reuseLastProviderEnabled && this.lastUsedProvider != null) {
            return TokenUtils.toSdkToken((TokenIdentity)CompletableFutureUtils.joinLikeSync((CompletableFuture)this.lastUsedProvider.resolveIdentity()));
        }
        ArrayList<String> exceptionMessages = null;
        for (IdentityProvider<? extends TokenIdentity> provider : this.sdkTokenProviders) {
            try {
                TokenIdentity token = (TokenIdentity)CompletableFutureUtils.joinLikeSync((CompletableFuture)provider.resolveIdentity());
                log.debug(() -> "Loading token from " + provider);
                this.lastUsedProvider = provider;
                return TokenUtils.toSdkToken(token);
            }
            catch (RuntimeException e) {
                String message = provider + ": " + e.getMessage();
                log.debug(() -> "Unable to load token from " + message, (Throwable)e);
                if (exceptionMessages == null) {
                    exceptionMessages = new ArrayList<String>();
                }
                exceptionMessages.add(message);
            }
        }
        throw SdkClientException.builder().message("Unable to load token from any of the providers in the chain " + this + " : " + exceptionMessages).build();
    }

    public void close() {
        this.sdkTokenProviders.forEach(c -> IoUtils.closeIfCloseable((Object)c, null));
    }

    public String toString() {
        return ToString.builder((String)"SdkTokenProviderChain").add("tokenProviders", this.sdkTokenProviders).build();
    }

    private static final class BuilderImpl
    implements Builder {
        private Boolean reuseLastProviderEnabled = true;
        private List<IdentityProvider<? extends TokenIdentity>> tokenProviders = new ArrayList<IdentityProvider<? extends TokenIdentity>>();

        private BuilderImpl() {
        }

        @Override
        public Builder reuseLastProviderEnabled(Boolean reuseLastProviderEnabled) {
            this.reuseLastProviderEnabled = reuseLastProviderEnabled;
            return this;
        }

        public void setReuseLastProviderEnabled(Boolean reuseLastProviderEnabled) {
            this.reuseLastProviderEnabled(reuseLastProviderEnabled);
        }

        @Override
        public Builder tokenProviders(Collection<? extends SdkTokenProvider> tokenProviders) {
            this.tokenProviders = new ArrayList<SdkTokenProvider>(tokenProviders);
            return this;
        }

        public void setTokenProviders(Collection<? extends SdkTokenProvider> tokenProviders) {
            this.tokenProviders(tokenProviders);
        }

        @Override
        public Builder tokenIdentityProviders(Collection<? extends IdentityProvider<? extends TokenIdentity>> tokenProviders) {
            this.tokenProviders = new ArrayList<IdentityProvider<? extends TokenIdentity>>(tokenProviders);
            return this;
        }

        public void setTokenIdentityProviders(Collection<? extends IdentityProvider<? extends TokenIdentity>> tokenProviders) {
            this.tokenIdentityProviders(tokenProviders);
        }

        @Override
        public Builder tokenProviders(IdentityProvider<? extends TokenIdentity> ... tokenProvider) {
            return this.tokenIdentityProviders(Arrays.asList(tokenProvider));
        }

        @Override
        public Builder addTokenProvider(IdentityProvider<? extends TokenIdentity> tokenProvider) {
            this.tokenProviders.add(tokenProvider);
            return this;
        }

        @Override
        public SdkTokenProviderChain build() {
            return new SdkTokenProviderChain(this);
        }
    }

    public static interface Builder {
        public Builder reuseLastProviderEnabled(Boolean var1);

        public Builder tokenProviders(Collection<? extends SdkTokenProvider> var1);

        public Builder tokenIdentityProviders(Collection<? extends IdentityProvider<? extends TokenIdentity>> var1);

        default public Builder tokenProviders(SdkTokenProvider ... tokenProviders) {
            return this.tokenProviders((IdentityProvider[])tokenProviders);
        }

        default public Builder tokenProviders(IdentityProvider<? extends TokenIdentity> ... tokenProviders) {
            throw new UnsupportedOperationException();
        }

        default public Builder addTokenProvider(SdkTokenProvider tokenProvider) {
            return this.addTokenProvider((IdentityProvider<? extends TokenIdentity>)tokenProvider);
        }

        default public Builder addTokenProvider(IdentityProvider<? extends TokenIdentity> tokenProvider) {
            throw new UnsupportedOperationException();
        }

        public SdkTokenProviderChain build();
    }
}

