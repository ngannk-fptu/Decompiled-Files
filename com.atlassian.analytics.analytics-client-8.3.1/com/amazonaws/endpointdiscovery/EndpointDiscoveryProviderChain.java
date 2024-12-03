/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.endpointdiscovery;

import com.amazonaws.endpointdiscovery.EndpointDiscoveryProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EndpointDiscoveryProviderChain
implements EndpointDiscoveryProvider {
    private static final Log LOG = LogFactory.getLog(EndpointDiscoveryProviderChain.class);
    private final List<EndpointDiscoveryProvider> providers;

    public EndpointDiscoveryProviderChain(EndpointDiscoveryProvider ... providers) {
        this.providers = new ArrayList<EndpointDiscoveryProvider>(providers.length);
        Collections.addAll(this.providers, providers);
    }

    @Override
    public Boolean endpointDiscoveryEnabled() {
        Boolean endpointDiscoveryEnabled = null;
        for (EndpointDiscoveryProvider provider : this.providers) {
            try {
                endpointDiscoveryEnabled = provider.endpointDiscoveryEnabled();
                if (endpointDiscoveryEnabled == null) continue;
                return endpointDiscoveryEnabled;
            }
            catch (Exception e) {
                LOG.debug((Object)("Unable to discover endpoint discovery setting " + provider.toString() + ": " + e.getMessage()));
            }
        }
        return endpointDiscoveryEnabled;
    }
}

