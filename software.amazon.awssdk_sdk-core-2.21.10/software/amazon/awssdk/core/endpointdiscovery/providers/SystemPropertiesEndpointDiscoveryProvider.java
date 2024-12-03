/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
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
        return (Boolean)SdkSystemSetting.AWS_ENDPOINT_DISCOVERY_ENABLED.getBooleanValue().orElseThrow(() -> SdkClientException.builder().message("No endpoint discovery setting set.").build());
    }

    public String toString() {
        return ToString.create((String)"SystemPropertiesEndpointDiscoveryProvider");
    }
}

