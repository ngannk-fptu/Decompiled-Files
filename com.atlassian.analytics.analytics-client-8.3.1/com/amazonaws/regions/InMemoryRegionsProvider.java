/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.regions.AbstractRegionMetadataProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.util.ValidationUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SdkInternalApi
public class InMemoryRegionsProvider
extends AbstractRegionMetadataProvider {
    private final List<Region> regions;

    public InMemoryRegionsProvider(List<Region> regions) {
        ValidationUtils.assertNotNull(regions, "regions");
        this.regions = Collections.unmodifiableList(new ArrayList<Region>(regions));
    }

    @Override
    public List<Region> getRegions() {
        return Collections.unmodifiableList(new ArrayList<Region>(this.regions));
    }

    @Override
    public Region getRegion(String regionName) {
        for (Region region : this.regions) {
            if (!region.getName().equals(regionName)) continue;
            return region;
        }
        return null;
    }

    @Override
    public List<Region> getRegionsForService(String serviceName) {
        LinkedList<Region> results = new LinkedList<Region>();
        for (Region region : this.regions) {
            if (!region.isServiceSupported(serviceName)) continue;
            results.add(region);
        }
        return results;
    }

    @Override
    public Region tryGetRegionByExplicitEndpoint(String endpoint) {
        return null;
    }

    @Override
    public Region tryGetRegionByEndpointDnsSuffix(String endpoint) {
        return null;
    }

    public String toString() {
        return this.regions.toString();
    }
}

