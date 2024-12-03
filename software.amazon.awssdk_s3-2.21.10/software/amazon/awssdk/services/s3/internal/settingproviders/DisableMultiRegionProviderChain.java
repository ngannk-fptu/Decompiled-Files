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
import software.amazon.awssdk.services.s3.internal.settingproviders.DisableMultiRegionProvider;
import software.amazon.awssdk.services.s3.internal.settingproviders.ProfileDisableMultiRegionProvider;
import software.amazon.awssdk.services.s3.internal.settingproviders.SystemsSettingsDisableMultiRegionProvider;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class DisableMultiRegionProviderChain
implements DisableMultiRegionProvider {
    private static final Logger log = Logger.loggerFor(DisableMultiRegionProvider.class);
    private static final String SETTING = "disableMultiRegion";
    private final List<DisableMultiRegionProvider> providers;

    private DisableMultiRegionProviderChain(List<DisableMultiRegionProvider> providers) {
        this.providers = providers;
    }

    public static DisableMultiRegionProviderChain create() {
        return DisableMultiRegionProviderChain.create(ProfileFile::defaultProfileFile, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow());
    }

    public static DisableMultiRegionProviderChain create(ProfileFile profileFile, String profileName) {
        return new DisableMultiRegionProviderChain(Arrays.asList(SystemsSettingsDisableMultiRegionProvider.create(), ProfileDisableMultiRegionProvider.create(profileFile, profileName)));
    }

    public static DisableMultiRegionProviderChain create(Supplier<ProfileFile> profileFile, String profileName) {
        return new DisableMultiRegionProviderChain(Arrays.asList(SystemsSettingsDisableMultiRegionProvider.create(), ProfileDisableMultiRegionProvider.create(profileFile, profileName)));
    }

    @Override
    public Optional<Boolean> resolve() {
        for (DisableMultiRegionProvider provider : this.providers) {
            try {
                Optional<Boolean> value = provider.resolve();
                if (!value.isPresent()) continue;
                return value;
            }
            catch (Exception ex) {
                log.warn(() -> "Failed to retrieve disableMultiRegion from " + provider, (Throwable)ex);
            }
        }
        return Optional.empty();
    }
}

