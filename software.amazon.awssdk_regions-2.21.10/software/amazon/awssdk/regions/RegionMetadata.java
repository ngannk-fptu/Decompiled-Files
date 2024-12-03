/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.regions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.internal.MetadataLoader;

@SdkPublicApi
public interface RegionMetadata {
    public String id();

    @Deprecated
    public String domain();

    public PartitionMetadata partition();

    public String description();

    public static RegionMetadata of(Region region) {
        return MetadataLoader.regionMetadata(region);
    }
}

