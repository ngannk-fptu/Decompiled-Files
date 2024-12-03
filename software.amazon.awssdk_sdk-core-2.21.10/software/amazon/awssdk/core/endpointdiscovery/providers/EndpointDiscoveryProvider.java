/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.endpointdiscovery.providers;

import software.amazon.awssdk.annotations.SdkInternalApi;

@FunctionalInterface
@SdkInternalApi
public interface EndpointDiscoveryProvider {
    public boolean resolveEndpointDiscovery();
}

