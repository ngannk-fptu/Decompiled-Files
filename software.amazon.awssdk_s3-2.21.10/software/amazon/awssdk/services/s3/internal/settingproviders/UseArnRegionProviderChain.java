/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.s3.internal.settingproviders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.services.s3.internal.settingproviders.ProfileUseArnRegionProvider;
import software.amazon.awssdk.services.s3.internal.settingproviders.SystemsSettingsUseArnRegionProvider;
import software.amazon.awssdk.services.s3.internal.settingproviders.UseArnRegionProvider;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class UseArnRegionProviderChain
implements UseArnRegionProvider {
    private static final Logger log = Logger.loggerFor(UseArnRegionProvider.class);
    private final List<UseArnRegionProvider> providers;

    private UseArnRegionProviderChain(List<UseArnRegionProvider> providers) {
        this.providers = providers;
    }

    public static UseArnRegionProviderChain create() {
        return UseArnRegionProviderChain.create(ProfileFile::defaultProfileFile, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow());
    }

    public static UseArnRegionProviderChain create(ProfileFile profileFile, String profileName) {
        return new UseArnRegionProviderChain(Arrays.asList(SystemsSettingsUseArnRegionProvider.create(), ProfileUseArnRegionProvider.create(profileFile, profileName)));
    }

    public static UseArnRegionProviderChain create(Supplier<ProfileFile> profileFile, String profileName) {
        return new UseArnRegionProviderChain(Arrays.asList(SystemsSettingsUseArnRegionProvider.create(), ProfileUseArnRegionProvider.create(profileFile, profileName)));
    }

    @Override
    public Optional<Boolean> resolveUseArnRegion() {
        for (UseArnRegionProvider provider : this.providers) {
            try {
                Optional<Boolean> useArnRegion = provider.resolveUseArnRegion();
                if (!useArnRegion.isPresent()) continue;
                return useArnRegion;
            }
            catch (Exception ex) {
                log.warn(() -> "Failed to retrieve useArnRegion from " + provider, (Throwable)ex);
            }
        }
        return Optional.empty();
    }
}

