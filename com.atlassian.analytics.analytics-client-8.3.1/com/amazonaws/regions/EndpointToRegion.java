/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionMetadata;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.util.AwsHostNameUtils;
import java.net.URI;

@SdkProtectedApi
public class EndpointToRegion {
    public static String guessRegionNameForEndpoint(String hostname) {
        return EndpointToRegion.guessRegionNameForEndpoint(hostname, null);
    }

    public static String guessRegionNameForEndpoint(String endpoint, String serviceHint) {
        return EndpointToRegion.guessRegionOrRegionNameForEndpoint(endpoint, serviceHint).getRegionName();
    }

    public static String guessRegionNameForEndpointWithDefault(String hostname, String serviceHint, String defaultRegion) {
        String region = EndpointToRegion.guessRegionNameForEndpoint(hostname, serviceHint);
        return region != null ? region : defaultRegion;
    }

    public static Region guessRegionForEndpoint(String hostname) {
        return EndpointToRegion.guessRegionForEndpoint(hostname, null);
    }

    public static Region guessRegionForEndpoint(String endpoint, String serviceHint) {
        return EndpointToRegion.guessRegionOrRegionNameForEndpoint(endpoint, serviceHint).getRegion();
    }

    private static RegionOrRegionName guessRegionOrRegionNameForEndpoint(String endpoint, String serviceHint) {
        if (endpoint == null) {
            return new RegionOrRegionName();
        }
        String host = null;
        try {
            host = URI.create(endpoint).getHost();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (host == null) {
            host = URI.create("http://" + endpoint).getHost();
        }
        if (host == null) {
            return new RegionOrRegionName();
        }
        String regionFromInternalConfig = AwsHostNameUtils.parseRegionFromInternalConfig(host);
        if (regionFromInternalConfig != null) {
            return new RegionOrRegionName(regionFromInternalConfig);
        }
        String regionFromAwsPartitionPattern = AwsHostNameUtils.parseRegionFromAwsPartitionPattern(host);
        if (regionFromAwsPartitionPattern != null) {
            return new RegionOrRegionName(regionFromAwsPartitionPattern);
        }
        String serviceHintRegion = AwsHostNameUtils.parseRegionUsingServiceHint(host, serviceHint);
        if (serviceHintRegion != null) {
            return new RegionOrRegionName(serviceHintRegion);
        }
        RegionMetadata regionMetadata = RegionUtils.getRegionMetadata();
        Region regionByExplicitEndpoint = regionMetadata.tryGetRegionByExplicitEndpoint(host);
        if (regionByExplicitEndpoint != null) {
            return new RegionOrRegionName(regionByExplicitEndpoint);
        }
        Region regionByDnsSuffix = regionMetadata.tryGetRegionByEndpointDnsSuffix(host);
        if (regionByDnsSuffix != null) {
            return new RegionOrRegionName(regionByDnsSuffix);
        }
        String regionFromAfterServiceName = AwsHostNameUtils.parseRegionFromAfterServiceName(host, serviceHint);
        if (regionFromAfterServiceName != null) {
            return new RegionOrRegionName(regionFromAfterServiceName);
        }
        return new RegionOrRegionName();
    }

    private static class RegionOrRegionName {
        private final Region region;
        private final String regionName;

        private RegionOrRegionName(Region region) {
            this.region = region;
            this.regionName = null;
        }

        private RegionOrRegionName(String regionName) {
            this.region = null;
            this.regionName = regionName;
        }

        private RegionOrRegionName() {
            this.region = null;
            this.regionName = null;
        }

        public Region getRegion() {
            if (this.regionName != null) {
                return RegionUtils.getRegion(this.regionName);
            }
            return this.region;
        }

        public String getRegionName() {
            if (this.region != null) {
                return this.region.getName();
            }
            return this.regionName;
        }
    }
}

