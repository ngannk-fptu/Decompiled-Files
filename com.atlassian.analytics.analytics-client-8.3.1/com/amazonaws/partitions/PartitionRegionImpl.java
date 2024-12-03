/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.partitions;

import com.amazonaws.partitions.model.Endpoint;
import com.amazonaws.partitions.model.Partition;
import com.amazonaws.partitions.model.Service;
import com.amazonaws.regions.RegionImpl;
import com.amazonaws.util.ValidationUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PartitionRegionImpl
implements RegionImpl {
    private static final String SERVICE = "{service}";
    private static final String REGION = "{region}";
    private static final String DNS_SUFFIX = "{dnsSuffix}";
    private final Partition partition;
    private final String region;
    private final Map<String, Endpoint> endpointCache = new ConcurrentHashMap<String, Endpoint>();

    public PartitionRegionImpl(String region, Partition p) {
        this.partition = ValidationUtils.assertNotNull(p, "partition");
        this.region = ValidationUtils.assertNotNull(region, "region");
    }

    @Override
    public String getName() {
        return this.region;
    }

    @Override
    public String getDomain() {
        return this.partition.getDnsSuffix();
    }

    @Override
    public String getPartition() {
        return this.partition.getPartition();
    }

    @Override
    public String getServiceEndpoint(String serviceName) {
        return this.getEndpointString(serviceName, this.getEndpoint(serviceName));
    }

    private Endpoint getEndpoint(String serviceName) {
        Endpoint cachedEndpoint = this.endpointCache.get(serviceName);
        if (cachedEndpoint == null && (cachedEndpoint = this.computeEndpoint(serviceName)) == null) {
            return null;
        }
        this.endpointCache.put(serviceName, cachedEndpoint);
        return cachedEndpoint;
    }

    private Endpoint computeEndpoint(String serviceName) {
        Service service = this.partition.getServices().get(serviceName);
        if (service != null) {
            if (service.getEndpoints().containsKey(this.region)) {
                Endpoint merged = Endpoint.merge(this.partition.getDefaults(), Endpoint.merge(service.getDefaults(), service.getEndpoints().get(this.region)));
                return merged;
            }
            if (service.isPartitionWideEndpointAvailable() && !service.isRegionalized()) {
                Endpoint merged = Endpoint.merge(this.partition.getDefaults(), Endpoint.merge(service.getDefaults(), service.getEndpoints().get(service.getPartitionEndpoint())));
                return merged;
            }
            if (this.partition.getDefaults() != null && this.partition.getDefaults().getHostName() != null) {
                return this.partition.getDefaults();
            }
        }
        return null;
    }

    private String getEndpointString(String serviceName, Endpoint endpoint) {
        return endpoint == null ? null : endpoint.getHostName().replace(SERVICE, serviceName).replace(REGION, this.region).replace(DNS_SUFFIX, this.partition.getDnsSuffix());
    }

    @Override
    public boolean isServiceSupported(String serviceName) {
        return this.isServiceSupportedInRegion(serviceName) || this.isServicePartitionWide(serviceName);
    }

    private boolean isServiceSupportedInRegion(String serviceName) {
        return this.partition.getServices().get(serviceName) != null && this.partition.getServices().get(serviceName).getEndpoints().containsKey(this.region);
    }

    private boolean isServicePartitionWide(String serviceName) {
        return this.partition.getServices().get(serviceName) != null && this.partition.getServices().get(serviceName).getPartitionEndpoint() != null;
    }

    @Override
    public boolean hasHttpsEndpoint(String serviceName) {
        if (!this.isServiceSupported(serviceName)) {
            return false;
        }
        return this.getEndpoint(serviceName).hasHttpsSupport();
    }

    @Override
    public boolean hasHttpEndpoint(String serviceName) {
        if (!this.isServiceSupported(serviceName)) {
            return false;
        }
        return this.getEndpoint(serviceName).hasHttpSupport();
    }

    @Override
    public Collection<String> getAvailableEndpoints() {
        ArrayList<String> endpoints = new ArrayList<String>();
        for (String service : this.partition.getServices().keySet()) {
            if (!this.isServiceSupported(service)) continue;
            endpoints.add(this.getServiceEndpoint(service));
        }
        return Collections.unmodifiableCollection(endpoints);
    }
}

