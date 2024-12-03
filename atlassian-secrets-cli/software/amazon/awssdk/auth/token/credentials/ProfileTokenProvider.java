/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.token.credentials;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.auth.token.internal.ProfileTokenProviderLoader;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class ProfileTokenProvider
implements SdkTokenProvider,
SdkAutoCloseable {
    private final SdkTokenProvider tokenProvider;
    private final RuntimeException loadException;
    private final String profileName;

    private ProfileTokenProvider(BuilderImpl builder) {
        SdkTokenProvider sdkTokenProvider = null;
        RuntimeException thrownException = null;
        Supplier selectedProfileFile = null;
        String selectedProfileName = null;
        try {
            selectedProfileName = Optional.ofNullable(builder.profileName).orElseGet(ProfileFileSystemSetting.AWS_PROFILE::getStringValueOrThrow);
            selectedProfileFile = Optional.ofNullable(builder.profileFile).orElse(builder.defaultProfileFileLoader);
            sdkTokenProvider = this.createTokenProvider(selectedProfileFile, selectedProfileName);
        }
        catch (RuntimeException e) {
            thrownException = e;
        }
        if (thrownException != null) {
            this.loadException = thrownException;
            this.tokenProvider = null;
            this.profileName = null;
        } else {
            this.loadException = null;
            this.tokenProvider = sdkTokenProvider;
            this.profileName = selectedProfileName;
        }
    }

    public static ProfileTokenProvider create() {
        return ProfileTokenProvider.builder().build();
    }

    public static ProfileTokenProvider create(String profileName) {
        return ProfileTokenProvider.builder().profileName(profileName).build();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public SdkToken resolveToken() {
        if (this.loadException != null) {
            throw this.loadException;
        }
        return this.tokenProvider.resolveToken();
    }

    public String toString() {
        return ToString.builder("ProfileTokenProvider").add("profileName", this.profileName).build();
    }

    @Override
    public void close() {
        IoUtils.closeIfCloseable(this.tokenProvider, null);
    }

    private SdkTokenProvider createTokenProvider(Supplier<ProfileFile> profileFile, String profileName) {
        return new ProfileTokenProviderLoader(profileFile, profileName).tokenProvider().orElseThrow(() -> {
            String errorMessage = String.format("Profile file contained no information for profile '%s'", profileName);
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

        @Override
        public Builder profileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        public void setProfileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile(profileFile);
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
        public ProfileTokenProvider build() {
            return new ProfileTokenProvider(this);
        }

        @SdkTestInternalApi
        Builder defaultProfileFileLoader(Supplier<ProfileFile> defaultProfileFileLoader) {
            this.defaultProfileFileLoader = defaultProfileFileLoader;
            return this;
        }
    }

    public static interface Builder {
        public Builder profileFile(Supplier<ProfileFile> var1);

        public Builder profileName(String var1);

        public ProfileTokenProvider build();
    }
}

