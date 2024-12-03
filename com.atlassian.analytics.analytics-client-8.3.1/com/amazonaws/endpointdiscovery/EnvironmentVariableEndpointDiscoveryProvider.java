/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.endpointdiscovery;

import com.amazonaws.endpointdiscovery.EndpointDiscoveryProvider;

public class EnvironmentVariableEndpointDiscoveryProvider
implements EndpointDiscoveryProvider {
    @Override
    public Boolean endpointDiscoveryEnabled() {
        Boolean endpointDiscoveryEnabled = null;
        String endpointDiscoveryEnabledString = System.getenv("AWS_ENABLE_ENDPOINT_DISCOVERY");
        if (endpointDiscoveryEnabledString != null) {
            try {
                endpointDiscoveryEnabled = Boolean.parseBoolean(endpointDiscoveryEnabledString);
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to parse environment variable AWS_ENABLE_ENDPOINT_DISCOVERY");
            }
        }
        return endpointDiscoveryEnabled;
    }
}

