/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.partitions;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.partitions.PartitionRegionImpl;
import com.amazonaws.partitions.model.Endpoint;
import com.amazonaws.partitions.model.Partition;
import com.amazonaws.partitions.model.Region;
import com.amazonaws.partitions.model.Service;
import com.amazonaws.regions.AbstractRegionMetadataProvider;
import com.amazonaws.util.ValidationUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SdkInternalApi
public class PartitionMetadataProvider
extends AbstractRegionMetadataProvider {
    private static final String STANDARD_PARTITION_HOSTNAME = "{service}.{region}.{dnsSuffix}";
    private final Map<String, Partition> partitionMap = new HashMap<String, Partition>();
    private final Map<String, com.amazonaws.regions.Region> credentialScopeRegionByHost = new HashMap<String, com.amazonaws.regions.Region>();
    private final Set<String> standardHostnamePatternDnsSuffixes = new HashSet<String>();
    private final Map<String, com.amazonaws.regions.Region> regionCache = new ConcurrentHashMap<String, com.amazonaws.regions.Region>();

    public PartitionMetadataProvider(List<Partition> partitions) {
        ValidationUtils.assertNotNull(partitions, "partitions");
        for (Partition p : partitions) {
            this.partitionMap.put(p.getPartition(), p);
            if (p.getDefaults() != null && STANDARD_PARTITION_HOSTNAME.equals(p.getDefaults().getHostName())) {
                this.standardHostnamePatternDnsSuffixes.add(p.getDnsSuffix());
            }
            for (Service service : p.getServices().values()) {
                for (Endpoint endpoint : service.getEndpoints().values()) {
                    if (endpoint.getHostName() == null || endpoint.getCredentialScope() == null || endpoint.getCredentialScope().getRegion() == null) continue;
                    com.amazonaws.regions.Region region = this.cacheRegion(new PartitionRegionImpl(endpoint.getCredentialScope().getRegion(), p));
                    this.credentialScopeRegionByHost.put(endpoint.getHostName(), region);
                }
            }
        }
    }

    @Override
    public List<com.amazonaws.regions.Region> getRegions() {
        ArrayList<com.amazonaws.regions.Region> regions = new ArrayList<com.amazonaws.regions.Region>();
        for (Partition p : this.partitionMap.values()) {
            for (Map.Entry<String, Region> entry : p.getRegions().entrySet()) {
                regions.add(new com.amazonaws.regions.Region(new PartitionRegionImpl(entry.getKey(), p)));
            }
        }
        return Collections.unmodifiableList(regions);
    }

    @Override
    public com.amazonaws.regions.Region getRegion(String regionName) {
        if (regionName == null) {
            return null;
        }
        com.amazonaws.regions.Region regionFromCache = this.getRegionFromCache(regionName);
        if (regionFromCache != null) {
            return regionFromCache;
        }
        return this.createNewRegion(regionName);
    }

    private com.amazonaws.regions.Region createNewRegion(String regionName) {
        for (Partition p : this.partitionMap.values()) {
            if (!p.hasRegion(regionName)) continue;
            return this.cacheRegion(new PartitionRegionImpl(regionName, p));
        }
        Partition awsPartition = this.partitionMap.get("aws");
        if (awsPartition != null) {
            return this.cacheRegion(new PartitionRegionImpl(regionName, awsPartition));
        }
        return null;
    }

    private com.amazonaws.regions.Region getRegionFromCache(String regionName) {
        return this.regionCache.get(regionName);
    }

    private com.amazonaws.regions.Region cacheRegion(PartitionRegionImpl regionImpl) {
        com.amazonaws.regions.Region region = new com.amazonaws.regions.Region(regionImpl);
        this.regionCache.put(region.getName(), region);
        return region;
    }

    @Override
    public List<com.amazonaws.regions.Region> getRegionsForService(String serviceName) {
        List<com.amazonaws.regions.Region> allRegions = this.getRegions();
        ArrayList<com.amazonaws.regions.Region> serviceSupportedRegions = new ArrayList<com.amazonaws.regions.Region>();
        for (com.amazonaws.regions.Region r : allRegions) {
            if (!r.isServiceSupported(serviceName)) continue;
            serviceSupportedRegions.add(r);
        }
        return serviceSupportedRegions;
    }

    @Override
    public com.amazonaws.regions.Region tryGetRegionByExplicitEndpoint(String endpoint) {
        String host = PartitionMetadataProvider.getHost(endpoint);
        return this.credentialScopeRegionByHost.get(host);
    }

    @Override
    public com.amazonaws.regions.Region tryGetRegionByEndpointDnsSuffix(String endpoint) {
        String host = PartitionMetadataProvider.getHost(endpoint);
        for (String dnsSuffix : this.standardHostnamePatternDnsSuffixes) {
            dnsSuffix = "." + dnsSuffix;
            if (!host.endsWith(dnsSuffix)) continue;
            String serviceRegion = host.substring(0, host.length() - dnsSuffix.length());
            String region = serviceRegion.substring(serviceRegion.lastIndexOf(46) + 1);
            if (region.isEmpty()) {
                return null;
            }
            return this.getRegion(region);
        }
        return null;
    }
}

