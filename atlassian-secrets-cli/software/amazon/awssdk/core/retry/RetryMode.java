/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.OptionalUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkPublicApi
public enum RetryMode {
    LEGACY,
    STANDARD,
    ADAPTIVE;


    public static RetryMode defaultRetryMode() {
        return RetryMode.resolver().resolve();
    }

    public static Resolver resolver() {
        return new Resolver();
    }

    public static class Resolver {
        private static final RetryMode SDK_DEFAULT_RETRY_MODE = LEGACY;
        private Supplier<ProfileFile> profileFile;
        private String profileName;
        private RetryMode defaultRetryMode;

        private Resolver() {
        }

        public Resolver profileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        public Resolver profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public Resolver defaultRetryMode(RetryMode defaultRetryMode) {
            this.defaultRetryMode = defaultRetryMode;
            return this;
        }

        public RetryMode resolve() {
            return OptionalUtils.firstPresent(Resolver.fromSystemSettings(), new Supplier[]{() -> Resolver.fromProfileFile(this.profileFile, this.profileName)}).orElseGet(this::fromDefaultMode);
        }

        private static Optional<RetryMode> fromSystemSettings() {
            return SdkSystemSetting.AWS_RETRY_MODE.getStringValue().flatMap(Resolver::fromString);
        }

        private static Optional<RetryMode> fromProfileFile(Supplier<ProfileFile> profileFile, String profileName) {
            profileFile = profileFile != null ? profileFile : ProfileFile::defaultProfileFile;
            profileName = profileName != null ? profileName : ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
            return profileFile.get().profile(profileName).flatMap(p -> p.property("retry_mode")).flatMap(Resolver::fromString);
        }

        private static Optional<RetryMode> fromString(String string) {
            if (string == null || string.isEmpty()) {
                return Optional.empty();
            }
            switch (StringUtils.lowerCase(string)) {
                case "legacy": {
                    return Optional.of(LEGACY);
                }
                case "standard": {
                    return Optional.of(STANDARD);
                }
                case "adaptive": {
                    return Optional.of(ADAPTIVE);
                }
            }
            throw new IllegalStateException("Unsupported retry policy mode configured: " + string);
        }

        private RetryMode fromDefaultMode() {
            return this.defaultRetryMode != null ? this.defaultRetryMode : SDK_DEFAULT_RETRY_MODE;
        }
    }
}

