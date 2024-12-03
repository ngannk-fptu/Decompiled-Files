/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.endpointdiscovery.providers;

import software.amazon.awssdk.annotations.SdkInternalApi;

@FunctionalInterface
@SdkInternalApi
public interface EndpointDiscoveryProvider {
    public boolean resolveEndpointDiscovery();
}

