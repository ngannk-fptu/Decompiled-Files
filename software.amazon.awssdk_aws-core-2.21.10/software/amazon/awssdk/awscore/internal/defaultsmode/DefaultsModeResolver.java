/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.OptionalUtils
 */
package software.amazon.awssdk.awscore.internal.defaultsmode;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.OptionalUtils;

@SdkInternalApi
public final class DefaultsModeResolver {
    private static final DefaultsMode SDK_DEFAULT_DEFAULTS_MODE = DefaultsMode.LEGACY;
    private Supplier<ProfileFile> profileFile;
    private String profileName;
    private DefaultsMode mode;

    private DefaultsModeResolver() {
    }

    public static DefaultsModeResolver create() {
        return new DefaultsModeResolver();
    }

    public DefaultsModeResolver profileFile(Supplier<ProfileFile> profileFile) {
        this.profileFile = profileFile;
        return this;
    }

    public DefaultsModeResolver profileName(String profileName) {
        this.profileName = profileName;
        return this;
    }

    public DefaultsModeResolver defaultMode(DefaultsMode mode) {
        this.mode = mode;
        return this;
    }

    public DefaultsMode resolve() {
        return OptionalUtils.firstPresent(DefaultsModeResolver.fromSystemSettings(), (Supplier[])new Supplier[]{() -> DefaultsModeResolver.fromProfileFile(this.profileFile, this.profileName)}).orElseGet(this::fromDefaultMode);
    }

    private static Optional<DefaultsMode> fromSystemSettings() {
        return SdkSystemSetting.AWS_DEFAULTS_MODE.getStringValue().map(value -> DefaultsMode.fromValue(value.toLowerCase(Locale.US)));
    }

    private static Optional<DefaultsMode> fromProfileFile(Supplier<ProfileFile> profileFile, String profileName) {
        profileFile = profileFile != null ? profileFile : ProfileFile::defaultProfileFile;
        profileName = profileName != null ? profileName : ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
        return profileFile.get().profile(profileName).flatMap(p -> p.property("defaults_mode")).map(value -> DefaultsMode.fromValue(value.toLowerCase(Locale.US)));
    }

    private DefaultsMode fromDefaultMode() {
        return this.mode != null ? this.mode : SDK_DEFAULT_DEFAULTS_MODE;
    }
}

