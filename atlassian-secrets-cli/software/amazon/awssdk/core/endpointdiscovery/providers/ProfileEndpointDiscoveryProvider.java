/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.endpointdiscovery.providers;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.endpointdiscovery.providers.EndpointDiscoveryProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public class ProfileEndpointDiscoveryProvider
implements EndpointDiscoveryProvider {
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private ProfileEndpointDiscoveryProvider(Supplier<ProfileFile> profileFile, String profileName) {
        this.profileFile = profileFile;
        this.profileName = profileName;
    }

    public static ProfileEndpointDiscoveryProvider create() {
        return new ProfileEndpointDiscoveryProvider(ProfileFile::defaultProfileFile, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow());
    }

    public static ProfileEndpointDiscoveryProvider create(Supplier<ProfileFile> profileFile, String profileName) {
        return new ProfileEndpointDiscoveryProvider(profileFile, profileName);
    }

    @Override
    public boolean resolveEndpointDiscovery() {
        return this.profileFile.get().profile(this.profileName).map(p -> p.properties().get("aws_endpoint_discovery_enabled")).map(Boolean::parseBoolean).orElseThrow(() -> SdkClientException.builder().message("No endpoint discovery setting provided in profile: " + this.profileName).build());
    }

    public String toString() {
        return ToString.create("ProfileEndpointDiscoveryProvider");
    }
}

