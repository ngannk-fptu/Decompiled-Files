/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;

@SdkPublicApi
public interface ServicePartitionMetadata {
    public PartitionMetadata partition();

    public Optional<Region> globalRegion();
}

