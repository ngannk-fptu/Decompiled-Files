/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.endpointdiscovery.providers;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.endpointdiscovery.providers.EndpointDiscoveryProviderChain;
import software.amazon.awssdk.core.endpointdiscovery.providers.ProfileEndpointDiscoveryProvider;
import software.amazon.awssdk.core.endpointdiscovery.providers.SystemPropertiesEndpointDiscoveryProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;

@SdkProtectedApi
public class DefaultEndpointDiscoveryProviderChain
extends EndpointDiscoveryProviderChain {
    public DefaultEndpointDiscoveryProviderChain() {
        this(ProfileFile::defaultProfileFile, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow());
    }

    public DefaultEndpointDiscoveryProviderChain(SdkClientConfiguration clientConfiguration) {
        this(clientConfiguration.option(SdkClientOption.PROFILE_FILE_SUPPLIER), clientConfiguration.option(SdkClientOption.PROFILE_NAME));
    }

    private DefaultEndpointDiscoveryProviderChain(Supplier<ProfileFile> profileFile, String profileName) {
        super(SystemPropertiesEndpointDiscoveryProvider.create(), ProfileEndpointDiscoveryProvider.create(profileFile, profileName));
    }
}

