/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.services.s3.internal.settingproviders;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.services.s3.internal.settingproviders.DisableMultiRegionProvider;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class ProfileDisableMultiRegionProvider
implements DisableMultiRegionProvider {
    private static final String AWS_DISABLE_MULTI_REGION = "s3_disable_multiregion_access_points";
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private ProfileDisableMultiRegionProvider(Supplier<ProfileFile> profileFile, String profileName) {
        this.profileFile = profileFile;
        this.profileName = profileName;
    }

    public static ProfileDisableMultiRegionProvider create() {
        return new ProfileDisableMultiRegionProvider(ProfileFile::defaultProfileFile, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow());
    }

    public static ProfileDisableMultiRegionProvider create(ProfileFile profileFile, String profileName) {
        return new ProfileDisableMultiRegionProvider(() -> profileFile, profileName);
    }

    public static ProfileDisableMultiRegionProvider create(Supplier<ProfileFile> profileFile, String profileName) {
        return new ProfileDisableMultiRegionProvider(profileFile, profileName);
    }

    @Override
    public Optional<Boolean> resolve() {
        return this.profileFile.get().profile(this.profileName).map(p -> (String)p.properties().get(AWS_DISABLE_MULTI_REGION)).map(StringUtils::safeStringToBoolean);
    }
}

