/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.LazyAwsCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class DefaultCredentialsProvider
implements AwsCredentialsProvider,
SdkAutoCloseable,
ToCopyableBuilder<Builder, DefaultCredentialsProvider> {
    private static final DefaultCredentialsProvider DEFAULT_CREDENTIALS_PROVIDER = new DefaultCredentialsProvider(DefaultCredentialsProvider.builder());
    private final LazyAwsCredentialsProvider providerChain;
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;
    private final Boolean reuseLastProviderEnabled;
    private final Boolean asyncCredentialUpdateEnabled;

    private DefaultCredentialsProvider(Builder builder) {
        this.profileFile = builder.profileFile;
        this.profileName = builder.profileName;
        this.reuseLastProviderEnabled = builder.reuseLastProviderEnabled;
        this.asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled;
        this.providerChain = DefaultCredentialsProvider.createChain(builder);
    }

    public static DefaultCredentialsProvider create() {
        return DEFAULT_CREDENTIALS_PROVIDER;
    }

    private static LazyAwsCredentialsProvider createChain(Builder builder) {
        boolean asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled;
        boolean reuseLastProviderEnabled = builder.reuseLastProviderEnabled;
        return LazyAwsCredentialsProvider.create(() -> {
            AwsCredentialsProvider[] credentialsProviders = new AwsCredentialsProvider[]{SystemPropertyCredentialsProvider.create(), EnvironmentVariableCredentialsProvider.create(), WebIdentityTokenFileCredentialsProvider.builder().asyncCredentialUpdateEnabled(asyncCredentialUpdateEnabled).build(), ProfileCredentialsProvider.builder().profileFile(builder.profileFile).profileName(builder.profileName).build(), ((ContainerCredentialsProvider.Builder)ContainerCredentialsProvider.builder().asyncCredentialUpdateEnabled(asyncCredentialUpdateEnabled)).build(), ((InstanceProfileCredentialsProvider.Builder)InstanceProfileCredentialsProvider.builder().asyncCredentialUpdateEnabled(asyncCredentialUpdateEnabled)).profileFile(builder.profileFile).profileName(builder.profileName).build()};
            return AwsCredentialsProviderChain.builder().reuseLastProviderEnabled(reuseLastProviderEnabled).credentialsProviders(credentialsProviders).build();
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return this.providerChain.resolveCredentials();
    }

    @Override
    public void close() {
        this.providerChain.close();
    }

    public String toString() {
        return ToString.builder("DefaultCredentialsProvider").add("providerChain", this.providerChain).build();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder
    implements CopyableBuilder<Builder, DefaultCredentialsProvider> {
        private Supplier<ProfileFile> profileFile;
        private String profileName;
        private Boolean reuseLastProviderEnabled = true;
        private Boolean asyncCredentialUpdateEnabled = false;

        private Builder() {
        }

        private Builder(DefaultCredentialsProvider credentialsProvider) {
            this.profileFile = credentialsProvider.profileFile;
            this.profileName = credentialsProvider.profileName;
            this.reuseLastProviderEnabled = credentialsProvider.reuseLastProviderEnabled;
            this.asyncCredentialUpdateEnabled = credentialsProvider.asyncCredentialUpdateEnabled;
        }

        public Builder profileFile(ProfileFile profileFile) {
            return this.profileFile((Supplier<ProfileFile>)Optional.ofNullable(profileFile).map(ProfileFileSupplier::fixedProfileFile).orElse(null));
        }

        public Builder profileFile(Supplier<ProfileFile> profileFileSupplier) {
            this.profileFile = profileFileSupplier;
            return this;
        }

        public Builder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public Builder reuseLastProviderEnabled(Boolean reuseLastProviderEnabled) {
            this.reuseLastProviderEnabled = reuseLastProviderEnabled;
            return this;
        }

        public Builder asyncCredentialUpdateEnabled(Boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
            return this;
        }

        @Override
        public DefaultCredentialsProvider build() {
            return new DefaultCredentialsProvider(this);
        }
    }
}

