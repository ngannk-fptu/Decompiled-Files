/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
 *  software.amazon.awssdk.awscore.endpoint.DualstackEnabledProvider
 *  software.amazon.awssdk.awscore.endpoint.FipsEnabledProvider
 *  software.amazon.awssdk.awscore.presigner.SdkPresigner
 *  software.amazon.awssdk.awscore.presigner.SdkPresigner$Builder
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.services.s3.internal.signing;

import java.net.URI;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.endpoint.DualstackEnabledProvider;
import software.amazon.awssdk.awscore.endpoint.FipsEnabledProvider;
import software.amazon.awssdk.awscore.presigner.SdkPresigner;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public abstract class DefaultSdkPresigner
implements SdkPresigner {
    private final Supplier<ProfileFile> profileFile = ProfileFile::defaultProfileFile;
    private final String profileName = ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
    private final Region region;
    private final URI endpointOverride;
    private final IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;
    private final Boolean dualstackEnabled;
    private final boolean fipsEnabled;

    protected DefaultSdkPresigner(Builder<?> b) {
        this.region = ((Builder)b).region != null ? ((Builder)b).region : DefaultAwsRegionProviderChain.builder().profileFile(this.profileFile).profileName(this.profileName).build().getRegion();
        this.credentialsProvider = ((Builder)b).credentialsProvider != null ? ((Builder)b).credentialsProvider : DefaultCredentialsProvider.builder().profileFile(this.profileFile).profileName(this.profileName).build();
        this.endpointOverride = ((Builder)b).endpointOverride;
        this.dualstackEnabled = ((Builder)b).dualstackEnabled != null ? ((Builder)b).dualstackEnabled : (Boolean)DualstackEnabledProvider.builder().profileFile(this.profileFile).profileName(this.profileName).build().isDualstackEnabled().orElse(null);
        this.fipsEnabled = ((Builder)b).fipsEnabled != null ? ((Builder)b).fipsEnabled.booleanValue() : FipsEnabledProvider.builder().profileFile(this.profileFile).profileName(this.profileName).build().isFipsEnabled().orElse(false).booleanValue();
    }

    protected Supplier<ProfileFile> profileFileSupplier() {
        return this.profileFile;
    }

    protected String profileName() {
        return this.profileName;
    }

    protected Region region() {
        return this.region;
    }

    protected IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider() {
        return this.credentialsProvider;
    }

    protected Boolean dualstackEnabled() {
        return this.dualstackEnabled;
    }

    protected boolean fipsEnabled() {
        return this.fipsEnabled;
    }

    protected URI endpointOverride() {
        return this.endpointOverride;
    }

    public void close() {
        IoUtils.closeIfCloseable(this.credentialsProvider, null);
    }

    @SdkInternalApi
    public static abstract class Builder<B extends Builder<B>>
    implements SdkPresigner.Builder {
        private Region region;
        private IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;
        private Boolean dualstackEnabled;
        private Boolean fipsEnabled;
        private URI endpointOverride;

        protected Builder() {
        }

        public B region(Region region) {
            this.region = region;
            return this.thisBuilder();
        }

        public B credentialsProvider(AwsCredentialsProvider credentialsProvider) {
            return (B)this.credentialsProvider((IdentityProvider)credentialsProvider);
        }

        public B credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this.thisBuilder();
        }

        public B dualstackEnabled(Boolean dualstackEnabled) {
            this.dualstackEnabled = dualstackEnabled;
            return this.thisBuilder();
        }

        public B fipsEnabled(Boolean fipsEnabled) {
            this.fipsEnabled = fipsEnabled;
            return this.thisBuilder();
        }

        public B endpointOverride(URI endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this.thisBuilder();
        }

        private B thisBuilder() {
            return (B)this;
        }
    }
}

