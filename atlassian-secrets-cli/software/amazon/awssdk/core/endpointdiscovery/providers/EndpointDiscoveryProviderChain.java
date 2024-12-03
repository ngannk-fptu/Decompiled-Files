/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.endpointdiscovery.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.endpointdiscovery.providers.EndpointDiscoveryProvider;

@SdkProtectedApi
public class EndpointDiscoveryProviderChain
implements EndpointDiscoveryProvider {
    private static final Logger log = LoggerFactory.getLogger(EndpointDiscoveryProviderChain.class);
    private final List<EndpointDiscoveryProvider> providers;

    public EndpointDiscoveryProviderChain(EndpointDiscoveryProvider ... providers) {
        this.providers = new ArrayList<EndpointDiscoveryProvider>(providers.length);
        Collections.addAll(this.providers, providers);
    }

    @Override
    public boolean resolveEndpointDiscovery() {
        for (EndpointDiscoveryProvider provider : this.providers) {
            try {
                return provider.resolveEndpointDiscovery();
            }
            catch (Exception e) {
                log.debug("Unable to load endpoint discovery from {}:{}", (Object)provider.toString(), (Object)e.getMessage());
            }
        }
        return false;
    }
}

