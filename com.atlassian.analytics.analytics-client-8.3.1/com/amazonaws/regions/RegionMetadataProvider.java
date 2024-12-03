/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.regions.Region;
import java.util.List;

@SdkInternalApi
public interface RegionMetadataProvider {
    public List<Region> getRegions();

    public Region getRegion(String var1);

    public List<Region> getRegionsForService(String var1);

    public Region getRegionByEndpoint(String var1);

    public Region tryGetRegionByExplicitEndpoint(String var1);

    public Region tryGetRegionByEndpointDnsSuffix(String var1);
}

