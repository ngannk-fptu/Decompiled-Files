/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;

@SdkPublicApi
public interface PartitionMetadataProvider {
    public PartitionMetadata partitionMetadata(String var1);

    public PartitionMetadata partitionMetadata(Region var1);
}

