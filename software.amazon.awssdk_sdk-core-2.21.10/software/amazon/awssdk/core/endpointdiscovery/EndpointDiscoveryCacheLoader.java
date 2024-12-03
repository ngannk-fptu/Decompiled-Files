/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.endpointdiscovery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.endpointdiscovery.EndpointDiscoveryEndpoint;
import software.amazon.awssdk.core.endpointdiscovery.EndpointDiscoveryRequest;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkProtectedApi
public interface EndpointDiscoveryCacheLoader {
    public CompletableFuture<EndpointDiscoveryEndpoint> discoverEndpoint(EndpointDiscoveryRequest var1);

    default public URI toUri(String address, URI defaultEndpoint) {
        try {
            return new URI(defaultEndpoint.getScheme(), address, defaultEndpoint.getPath(), defaultEndpoint.getFragment());
        }
        catch (URISyntaxException e) {
            throw SdkClientException.builder().message("Unable to construct discovered endpoint").cause(e).build();
        }
    }
}

