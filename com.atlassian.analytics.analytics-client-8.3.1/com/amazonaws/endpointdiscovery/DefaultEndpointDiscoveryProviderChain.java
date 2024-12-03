/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.endpointdiscovery;

import com.amazonaws.endpointdiscovery.AwsProfileEndpointDiscoveryProvider;
import com.amazonaws.endpointdiscovery.EndpointDiscoveryProviderChain;
import com.amazonaws.endpointdiscovery.EnvironmentVariableEndpointDiscoveryProvider;
import com.amazonaws.endpointdiscovery.SystemPropertyEndpointDiscoveryProvider;

public class DefaultEndpointDiscoveryProviderChain
extends EndpointDiscoveryProviderChain {
    public DefaultEndpointDiscoveryProviderChain() {
        super(new EnvironmentVariableEndpointDiscoveryProvider(), new SystemPropertyEndpointDiscoveryProvider(), new AwsProfileEndpointDiscoveryProvider());
    }
}

