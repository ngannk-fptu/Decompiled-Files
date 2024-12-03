/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.profiles.Profile
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFile$Builder
 *  software.amazon.awssdk.profiles.ProfileFileSupplier
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.auth.credentials;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.ProfileCredentialsUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class ProfileCredentialsProvider
implements AwsCredentialsProvider,
SdkAutoCloseable,
ToCopyableBuilder<Builder, ProfileCredentialsProvider> {
    private volatile AwsCredentialsProvider credentialsProvider;
    private final RuntimeException loadException;
    private final Supplier<ProfileFile> profileFile;
    private volatile ProfileFile currentProfileFile;
    private final String profileName;
    private final Supplier<ProfileFile> defaultProfileFileLoader;
    private final Object credentialsProviderLock = new Object();

    private ProfileCredentialsProvider(BuilderImpl builder) {
        this.defaultProfileFileLoader = builder.defaultProfileFileLoader;
        RuntimeException thrownException = null;
        String selectedProfileName = null;
        Supplier selectedProfileSupplier = null;
        try {
            selectedProfileName = Optional.ofNullable(builder.profileName).orElseGet(() -> ((ProfileFileSystemSetting)ProfileFileSystemSetting.AWS_PROFILE).getStringValueOrThrow());
            selectedProfileSupplier = Optional.ofNullable(builder.profileFile).orElseGet(() -> builder.defaultProfileFileLoader);
        }
        catch (RuntimeException e) {
            thrownException = e;
        }
        this.loadException = thrownException;
        this.profileName = selectedProfileName;
        this.profileFile = selectedProfileSupplier;
    }

    public static ProfileCredentialsProvider create() {
        return ProfileCredentialsProvider.builder().build();
    }

    public static ProfileCredentialsProvider create(String profileName) {
        return ProfileCredentialsProvider.builder().profileName(profileName).build();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AwsCredentials resolveCredentials() {
        if (this.loadException != null) {
            throw this.loadException;
        }
        ProfileFile cachedOrRefreshedProfileFile = this.refreshProfileFile();
        if (this.shouldUpdateCredentialsProvider(cachedOrRefreshedProfileFile)) {
            Object object = this.credentialsProviderLock;
            synchronized (object) {
                if (this.shouldUpdateCredentialsProvider(cachedOrRefreshedProfileFile)) {
                    this.currentProfileFile = cachedOrRefreshedProfileFile;
                    this.handleProfileFileReload(cachedOrRefreshedProfileFile);
                }
            }
        }
        return this.credentialsProvider.resolveCredentials();
    }

    private void handleProfileFileReload(ProfileFile profileFile) {
        this.credentialsProvider = this.createCredentialsProvider(profileFile, this.profileName);
    }

    private ProfileFile refreshProfileFile() {
        return this.profileFile.get();
    }

    private boolean shouldUpdateCredentialsProvider(ProfileFile profileFile) {
        return this.credentialsProvider == null || !Objects.equals(this.currentProfileFile, profileFile);
    }

    public String toString() {
        return ToString.builder((String)"ProfileCredentialsProvider").add("profileName", (Object)this.profileName).add("profileFile", (Object)this.currentProfileFile).build();
    }

    public void close() {
        IoUtils.closeIfCloseable((Object)this.credentialsProvider, null);
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private AwsCredentialsProvider createCredentialsProvider(ProfileFile profileFile, String profileName) {
        return (AwsCredentialsProvider)profileFile.profile(profileName).flatMap(p -> new ProfileCredentialsUtils(profileFile, (Profile)p, arg_0 -> ((ProfileFile)profileFile).profile(arg_0)).credentialsProvider()).orElseThrow(() -> {
            String errorMessage = String.format("Profile file contained no credentials for profile '%s': %s", profileName, profileFile);
            return SdkClientException.builder().message(errorMessage).build();
        });
    }

    static final class BuilderImpl
    implements Builder {
        private Supplier<ProfileFile> profileFile;
        private String profileName;
        private Supplier<ProfileFile> defaultProfileFileLoader = ProfileFile::defaultProfileFile;

        BuilderImpl() {
        }

        BuilderImpl(ProfileCredentialsProvider provider) {
            this.profileName = provider.profileName;
            this.defaultProfileFileLoader = provider.defaultProfileFileLoader;
            this.profileFile = provider.profileFile;
        }

        @Override
        public Builder profileFile(ProfileFile profileFile) {
            return this.profileFile((Supplier<ProfileFile>)Optional.ofNullable(profileFile).map(ProfileFileSupplier::fixedProfileFile).orElse(null));
        }

        public void setProfileFile(ProfileFile profileFile) {
            this.profileFile(profileFile);
        }

        @Override
        public Builder profileFile(Consumer<ProfileFile.Builder> profileFile) {
            return this.profileFile(((ProfileFile.Builder)ProfileFile.builder().applyMutation(profileFile)).build());
        }

        @Override
        public Builder profileFile(Supplier<ProfileFile> profileFileSupplier) {
            this.profileFile = profileFileSupplier;
            return this;
        }

        public void setProfileFile(Supplier<ProfileFile> supplier) {
            this.profileFile(supplier);
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
        public ProfileCredentialsProvider build() {
            return new ProfileCredentialsProvider(this);
        }

        @SdkTestInternalApi
        Builder defaultProfileFileLoader(Supplier<ProfileFile> defaultProfileFileLoader) {
            this.defaultProfileFileLoader = defaultProfileFileLoader;
            return this;
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, ProfileCredentialsProvider> {
        public Builder profileFile(ProfileFile var1);

        public Builder profileFile(Consumer<ProfileFile.Builder> var1);

        public Builder profileFile(Supplier<ProfileFile> var1);

        public Builder profileName(String var1);

        public ProfileCredentialsProvider build();
    }
}

