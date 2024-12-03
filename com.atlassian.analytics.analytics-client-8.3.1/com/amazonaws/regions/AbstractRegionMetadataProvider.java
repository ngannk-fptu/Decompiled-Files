/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionMetadataProvider;
import java.net.URI;

public abstract class AbstractRegionMetadataProvider
implements RegionMetadataProvider {
    @Override
    public Region getRegionByEndpoint(String endpoint) {
        String host = AbstractRegionMetadataProvider.getHost(endpoint);
        for (Region region : this.getRegions()) {
            for (String serviceEndpoint : region.getAvailableEndpoints()) {
                if (!host.equals(AbstractRegionMetadataProvider.getHost(serviceEndpoint))) continue;
                return region;
            }
        }
        throw new IllegalArgumentException("No region found with any service for endpoint " + endpoint);
    }

    protected static String getHost(String endpoint) {
        try {
            String host = URI.create(endpoint).getHost();
            if (host == null) {
                host = URI.create("http://" + endpoint).getHost();
            }
            if (host == null) {
                return "";
            }
            return host;
        }
        catch (IllegalArgumentException e) {
            return "";
        }
    }
}

