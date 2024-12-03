/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class AwsCredentialsProviderChain
implements AwsCredentialsProvider,
SdkAutoCloseable,
ToCopyableBuilder<Builder, AwsCredentialsProviderChain> {
    private static final Logger log = Logger.loggerFor(AwsCredentialsProviderChain.class);
    private final List<IdentityProvider<? extends AwsCredentialsIdentity>> credentialsProviders;
    private final boolean reuseLastProviderEnabled;
    private volatile IdentityProvider<? extends AwsCredentialsIdentity> lastUsedProvider;

    private AwsCredentialsProviderChain(BuilderImpl builder) {
        Validate.notEmpty(builder.credentialsProviders, "No credential providers were specified.", new Object[0]);
        this.reuseLastProviderEnabled = builder.reuseLastProviderEnabled;
        this.credentialsProviders = Collections.unmodifiableList(builder.credentialsProviders);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static AwsCredentialsProviderChain of(AwsCredentialsProvider ... awsCredentialsProviders) {
        return AwsCredentialsProviderChain.builder().credentialsProviders(awsCredentialsProviders).build();
    }

    public static AwsCredentialsProviderChain of(IdentityProvider<? extends AwsCredentialsIdentity> ... awsCredentialsProviders) {
        return AwsCredentialsProviderChain.builder().credentialsProviders(awsCredentialsProviders).build();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        if (this.reuseLastProviderEnabled && this.lastUsedProvider != null) {
            return CredentialUtils.toCredentials(CompletableFutureUtils.joinLikeSync(this.lastUsedProvider.resolveIdentity()));
        }
        ArrayList<String> exceptionMessages = null;
        for (IdentityProvider<? extends AwsCredentialsIdentity> provider : this.credentialsProviders) {
            try {
                AwsCredentialsIdentity credentials = CompletableFutureUtils.joinLikeSync(provider.resolveIdentity());
                log.debug(() -> "Loading credentials from " + provider);
                this.lastUsedProvider = provider;
                return CredentialUtils.toCredentials(credentials);
            }
            catch (RuntimeException e) {
                String message = provider + ": " + e.getMessage();
                log.debug(() -> "Unable to load credentials from " + message, e);
                if (exceptionMessages == null) {
                    exceptionMessages = new ArrayList<String>();
                }
                exceptionMessages.add(message);
            }
        }
        throw SdkClientException.builder().message("Unable to load credentials from any of the providers in the chain " + this + " : " + exceptionMessages).build();
    }

    @Override
    public void close() {
        this.credentialsProviders.forEach(c -> IoUtils.closeIfCloseable(c, null));
    }

    public String toString() {
        return ToString.builder("AwsCredentialsProviderChain").add("credentialsProviders", this.credentialsProviders).build();
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private static final class BuilderImpl
    implements Builder {
        private Boolean reuseLastProviderEnabled = true;
        private List<IdentityProvider<? extends AwsCredentialsIdentity>> credentialsProviders = new ArrayList<IdentityProvider<? extends AwsCredentialsIdentity>>();

        private BuilderImpl() {
        }

        private BuilderImpl(AwsCredentialsProviderChain provider) {
            this.reuseLastProviderEnabled = provider.reuseLastProviderEnabled;
            this.credentialsProviders = provider.credentialsProviders;
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
        public Builder credentialsProviders(Collection<? extends AwsCredentialsProvider> credentialsProviders) {
            this.credentialsProviders = new ArrayList<AwsCredentialsProvider>(credentialsProviders);
            return this;
        }

        public void setCredentialsProviders(Collection<? extends AwsCredentialsProvider> credentialsProviders) {
            this.credentialsProviders(credentialsProviders);
        }

        @Override
        public Builder credentialsIdentityProviders(Collection<? extends IdentityProvider<? extends AwsCredentialsIdentity>> credentialsProviders) {
            this.credentialsProviders = new ArrayList<IdentityProvider<? extends AwsCredentialsIdentity>>(credentialsProviders);
            return this;
        }

        public void setCredentialsIdentityProviders(Collection<? extends IdentityProvider<? extends AwsCredentialsIdentity>> credentialsProviders) {
            this.credentialsIdentityProviders(credentialsProviders);
        }

        @Override
        public Builder credentialsProviders(IdentityProvider<? extends AwsCredentialsIdentity> ... credentialsProviders) {
            return this.credentialsIdentityProviders(Arrays.asList(credentialsProviders));
        }

        @Override
        public Builder addCredentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            this.credentialsProviders.add(credentialsProvider);
            return this;
        }

        @Override
        public AwsCredentialsProviderChain build() {
            return new AwsCredentialsProviderChain(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, AwsCredentialsProviderChain> {
        public Builder reuseLastProviderEnabled(Boolean var1);

        public Builder credentialsProviders(Collection<? extends AwsCredentialsProvider> var1);

        public Builder credentialsIdentityProviders(Collection<? extends IdentityProvider<? extends AwsCredentialsIdentity>> var1);

        default public Builder credentialsProviders(AwsCredentialsProvider ... credentialsProviders) {
            return this.credentialsProviders((IdentityProvider[])credentialsProviders);
        }

        default public Builder credentialsProviders(IdentityProvider<? extends AwsCredentialsIdentity> ... credentialsProviders) {
            throw new UnsupportedOperationException();
        }

        default public Builder addCredentialsProvider(AwsCredentialsProvider credentialsProvider) {
            return this.addCredentialsProvider((IdentityProvider<? extends AwsCredentialsIdentity>)credentialsProvider);
        }

        default public Builder addCredentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AwsCredentialsProviderChain build();
    }
}

