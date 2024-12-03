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
import software.amazon.awssdk.services.s3.internal.settingproviders.UseArnRegionProvider;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class ProfileUseArnRegionProvider
implements UseArnRegionProvider {
    private static final String AWS_USE_ARN_REGION = "s3_use_arn_region";
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private ProfileUseArnRegionProvider(Supplier<ProfileFile> profileFile, String profileName) {
        this.profileFile = profileFile;
        this.profileName = profileName;
    }

    public static ProfileUseArnRegionProvider create() {
        return new ProfileUseArnRegionProvider(ProfileFile::defaultProfileFile, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow());
    }

    public static ProfileUseArnRegionProvider create(ProfileFile profileFile, String profileName) {
        return new ProfileUseArnRegionProvider(() -> profileFile, profileName);
    }

    public static ProfileUseArnRegionProvider create(Supplier<ProfileFile> profileFile, String profileName) {
        return new ProfileUseArnRegionProvider(profileFile, profileName);
    }

    @Override
    public Optional<Boolean> resolveUseArnRegion() {
        return this.profileFile.get().profile(this.profileName).map(p -> (String)p.properties().get(AWS_USE_ARN_REGION)).map(StringUtils::safeStringToBoolean);
    }
}

