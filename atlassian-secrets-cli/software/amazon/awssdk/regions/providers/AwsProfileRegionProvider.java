/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.providers;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@SdkProtectedApi
public final class AwsProfileRegionProvider
implements AwsRegionProvider {
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    public AwsProfileRegionProvider() {
        this(null, null);
    }

    public AwsProfileRegionProvider(Supplier<ProfileFile> profileFile, String profileName) {
        this.profileFile = profileFile != null ? profileFile : ProfileFile::defaultProfileFile;
        this.profileName = profileName != null ? profileName : ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
    }

    @Override
    public Region getRegion() {
        return this.profileFile.get().profile(this.profileName).map(p -> p.properties().get("region")).map(Region::of).orElseThrow(() -> SdkClientException.builder().message("No region provided in profile: " + this.profileName).build());
    }
}

