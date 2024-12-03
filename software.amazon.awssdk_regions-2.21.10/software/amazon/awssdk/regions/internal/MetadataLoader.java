/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.regions.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.regions.GeneratedPartitionMetadataProvider;
import software.amazon.awssdk.regions.GeneratedRegionMetadataProvider;
import software.amazon.awssdk.regions.GeneratedServiceMetadataProvider;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.PartitionMetadataProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.RegionMetadata;
import software.amazon.awssdk.regions.RegionMetadataProvider;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServiceMetadataProvider;

@SdkInternalApi
public final class MetadataLoader {
    private static final RegionMetadataProvider REGION_METADATA_PROVIDER = new GeneratedRegionMetadataProvider();
    private static final ServiceMetadataProvider SERVICE_METADATA_PROVIDER = new GeneratedServiceMetadataProvider();
    private static final PartitionMetadataProvider PARTITION_METADATA_PROVIDER = new GeneratedPartitionMetadataProvider();

    private MetadataLoader() {
    }

    public static PartitionMetadata partitionMetadata(Region region) {
        return PARTITION_METADATA_PROVIDER.partitionMetadata(region);
    }

    public static PartitionMetadata partitionMetadata(String partition) {
        return PARTITION_METADATA_PROVIDER.partitionMetadata(partition);
    }

    public static RegionMetadata regionMetadata(Region region) {
        return REGION_METADATA_PROVIDER.regionMetadata(region);
    }

    public static ServiceMetadata serviceMetadata(String service) {
        return SERVICE_METADATA_PROVIDER.serviceMetadata(service);
    }
}

