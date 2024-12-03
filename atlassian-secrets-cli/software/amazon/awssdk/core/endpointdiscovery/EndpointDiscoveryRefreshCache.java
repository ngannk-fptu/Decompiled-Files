/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.endpointdiscovery;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.endpointdiscovery.EndpointDiscoveryCacheLoader;
import software.amazon.awssdk.core.endpointdiscovery.EndpointDiscoveryEndpoint;
import software.amazon.awssdk.core.endpointdiscovery.EndpointDiscoveryFailedException;
import software.amazon.awssdk.core.endpointdiscovery.EndpointDiscoveryRequest;

@SdkProtectedApi
public final class EndpointDiscoveryRefreshCache {
    private final Map<String, EndpointDiscoveryEndpoint> cache = new ConcurrentHashMap<String, EndpointDiscoveryEndpoint>();
    private final EndpointDiscoveryCacheLoader client;

    private EndpointDiscoveryRefreshCache(EndpointDiscoveryCacheLoader client) {
        this.client = client;
    }

    public static EndpointDiscoveryRefreshCache create(EndpointDiscoveryCacheLoader client) {
        return new EndpointDiscoveryRefreshCache(client);
    }

    public URI get(String accessKey, EndpointDiscoveryRequest request) {
        EndpointDiscoveryEndpoint endpoint;
        String key = accessKey;
        if (key == null) {
            key = "";
        }
        if (request.cacheKey().isPresent()) {
            key = key + ":" + request.cacheKey().get();
        }
        if ((endpoint = this.cache.get(key)) == null) {
            if (request.required()) {
                return this.cache.computeIfAbsent(key, k -> this.getAndJoin(request)).endpoint();
            }
            EndpointDiscoveryEndpoint tempEndpoint = EndpointDiscoveryEndpoint.builder().endpoint(request.defaultEndpoint()).expirationTime(Instant.now().plusSeconds(60L)).build();
            EndpointDiscoveryEndpoint previousValue = this.cache.putIfAbsent(key, tempEndpoint);
            if (previousValue != null) {
                return previousValue.endpoint();
            }
            this.refreshCacheAsync(request, key);
            return tempEndpoint.endpoint();
        }
        if (endpoint.expirationTime().isBefore(Instant.now())) {
            this.cache.put(key, endpoint.toBuilder().expirationTime(Instant.now().plusSeconds(60L)).build());
            this.refreshCacheAsync(request, key);
        }
        return endpoint.endpoint();
    }

    private EndpointDiscoveryEndpoint getAndJoin(EndpointDiscoveryRequest request) {
        try {
            return this.discoverEndpoint(request).get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw EndpointDiscoveryFailedException.create(e);
        }
        catch (ExecutionException e) {
            throw EndpointDiscoveryFailedException.create(e.getCause());
        }
    }

    private void refreshCacheAsync(EndpointDiscoveryRequest request, String key) {
        this.discoverEndpoint(request).thenApply(v -> this.cache.put(key, (EndpointDiscoveryEndpoint)v));
    }

    public CompletableFuture<EndpointDiscoveryEndpoint> discoverEndpoint(EndpointDiscoveryRequest request) {
        return this.client.discoverEndpoint(request);
    }

    public void evict(String key) {
        this.cache.remove(key);
    }
}

