/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.endpointdiscovery.providers;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.endpointdiscovery.providers.EndpointDiscoveryProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class SystemPropertiesEndpointDiscoveryProvider
implements EndpointDiscoveryProvider {
    private SystemPropertiesEndpointDiscoveryProvider() {
    }

    public static SystemPropertiesEndpointDiscoveryProvider create() {
        return new SystemPropertiesEndpointDiscoveryProvider();
    }

    @Override
    public boolean resolveEndpointDiscovery() {
        return SdkSystemSetting.AWS_ENDPOINT_DISCOVERY_ENABLED.getBooleanValue().orElseThrow(() -> SdkClientException.builder().message("No endpoint discovery setting set.").build());
    }

    public String toString() {
        return ToString.create("SystemPropertiesEndpointDiscoveryProvider");
    }
}

