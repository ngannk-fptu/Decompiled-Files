/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.regions.internal;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServicePartitionMetadata;

@SdkInternalApi
public class DefaultServicePartitionMetadata
implements ServicePartitionMetadata {
    private final String partition;
    private final Region globalRegionForPartition;

    public DefaultServicePartitionMetadata(String partition, Region globalRegionForPartition) {
        this.partition = partition;
        this.globalRegionForPartition = globalRegionForPartition;
    }

    @Override
    public PartitionMetadata partition() {
        return PartitionMetadata.of(this.partition);
    }

    @Override
    public Optional<Region> globalRegion() {
        return Optional.ofNullable(this.globalRegionForPartition);
    }
}

