/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ImmutableMap
 */
package software.amazon.awssdk.regions;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.RegionMetadata;
import software.amazon.awssdk.regions.RegionMetadataProvider;
import software.amazon.awssdk.regions.regionmetadata.AfSouth1;
import software.amazon.awssdk.regions.regionmetadata.ApEast1;
import software.amazon.awssdk.regions.regionmetadata.ApNortheast1;
import software.amazon.awssdk.regions.regionmetadata.ApNortheast2;
import software.amazon.awssdk.regions.regionmetadata.ApNortheast3;
import software.amazon.awssdk.regions.regionmetadata.ApSouth1;
import software.amazon.awssdk.regions.regionmetadata.ApSouth2;
import software.amazon.awssdk.regions.regionmetadata.ApSoutheast1;
import software.amazon.awssdk.regions.regionmetadata.ApSoutheast2;
import software.amazon.awssdk.regions.regionmetadata.ApSoutheast3;
import software.amazon.awssdk.regions.regionmetadata.ApSoutheast4;
import software.amazon.awssdk.regions.regionmetadata.CaCentral1;
import software.amazon.awssdk.regions.regionmetadata.CnNorth1;
import software.amazon.awssdk.regions.regionmetadata.CnNorthwest1;
import software.amazon.awssdk.regions.regionmetadata.EuCentral1;
import software.amazon.awssdk.regions.regionmetadata.EuCentral2;
import software.amazon.awssdk.regions.regionmetadata.EuNorth1;
import software.amazon.awssdk.regions.regionmetadata.EuSouth1;
import software.amazon.awssdk.regions.regionmetadata.EuSouth2;
import software.amazon.awssdk.regions.regionmetadata.EuWest1;
import software.amazon.awssdk.regions.regionmetadata.EuWest2;
import software.amazon.awssdk.regions.regionmetadata.EuWest3;
import software.amazon.awssdk.regions.regionmetadata.IlCentral1;
import software.amazon.awssdk.regions.regionmetadata.MeCentral1;
import software.amazon.awssdk.regions.regionmetadata.MeSouth1;
import software.amazon.awssdk.regions.regionmetadata.SaEast1;
import software.amazon.awssdk.regions.regionmetadata.UsEast1;
import software.amazon.awssdk.regions.regionmetadata.UsEast2;
import software.amazon.awssdk.regions.regionmetadata.UsGovEast1;
import software.amazon.awssdk.regions.regionmetadata.UsGovWest1;
import software.amazon.awssdk.regions.regionmetadata.UsIsoEast1;
import software.amazon.awssdk.regions.regionmetadata.UsIsoWest1;
import software.amazon.awssdk.regions.regionmetadata.UsIsobEast1;
import software.amazon.awssdk.regions.regionmetadata.UsWest1;
import software.amazon.awssdk.regions.regionmetadata.UsWest2;
import software.amazon.awssdk.utils.ImmutableMap;

@SdkPublicApi
public final class GeneratedRegionMetadataProvider
implements RegionMetadataProvider {
    private static final Map<Region, RegionMetadata> REGION_METADATA = ImmutableMap.builder().put((Object)Region.AF_SOUTH_1, (Object)new AfSouth1()).put((Object)Region.AP_EAST_1, (Object)new ApEast1()).put((Object)Region.AP_NORTHEAST_1, (Object)new ApNortheast1()).put((Object)Region.AP_NORTHEAST_2, (Object)new ApNortheast2()).put((Object)Region.AP_NORTHEAST_3, (Object)new ApNortheast3()).put((Object)Region.AP_SOUTH_1, (Object)new ApSouth1()).put((Object)Region.AP_SOUTH_2, (Object)new ApSouth2()).put((Object)Region.AP_SOUTHEAST_1, (Object)new ApSoutheast1()).put((Object)Region.AP_SOUTHEAST_2, (Object)new ApSoutheast2()).put((Object)Region.AP_SOUTHEAST_3, (Object)new ApSoutheast3()).put((Object)Region.AP_SOUTHEAST_4, (Object)new ApSoutheast4()).put((Object)Region.CA_CENTRAL_1, (Object)new CaCentral1()).put((Object)Region.EU_CENTRAL_1, (Object)new EuCentral1()).put((Object)Region.EU_CENTRAL_2, (Object)new EuCentral2()).put((Object)Region.EU_NORTH_1, (Object)new EuNorth1()).put((Object)Region.EU_SOUTH_1, (Object)new EuSouth1()).put((Object)Region.EU_SOUTH_2, (Object)new EuSouth2()).put((Object)Region.EU_WEST_1, (Object)new EuWest1()).put((Object)Region.EU_WEST_2, (Object)new EuWest2()).put((Object)Region.EU_WEST_3, (Object)new EuWest3()).put((Object)Region.IL_CENTRAL_1, (Object)new IlCentral1()).put((Object)Region.ME_CENTRAL_1, (Object)new MeCentral1()).put((Object)Region.ME_SOUTH_1, (Object)new MeSouth1()).put((Object)Region.SA_EAST_1, (Object)new SaEast1()).put((Object)Region.US_EAST_1, (Object)new UsEast1()).put((Object)Region.US_EAST_2, (Object)new UsEast2()).put((Object)Region.US_WEST_1, (Object)new UsWest1()).put((Object)Region.US_WEST_2, (Object)new UsWest2()).put((Object)Region.CN_NORTH_1, (Object)new CnNorth1()).put((Object)Region.CN_NORTHWEST_1, (Object)new CnNorthwest1()).put((Object)Region.US_GOV_EAST_1, (Object)new UsGovEast1()).put((Object)Region.US_GOV_WEST_1, (Object)new UsGovWest1()).put((Object)Region.US_ISO_EAST_1, (Object)new UsIsoEast1()).put((Object)Region.US_ISO_WEST_1, (Object)new UsIsoWest1()).put((Object)Region.US_ISOB_EAST_1, (Object)new UsIsobEast1()).build();

    @Override
    public RegionMetadata regionMetadata(Region region) {
        return REGION_METADATA.get(region);
    }
}

