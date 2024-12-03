/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionEndpointKey;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.internal.MetadataLoader;

@SdkPublicApi
public interface PartitionMetadata {
    default public String dnsSuffix() {
        return this.dnsSuffix(PartitionEndpointKey.builder().build());
    }

    default public String dnsSuffix(PartitionEndpointKey key) {
        throw new UnsupportedOperationException();
    }

    default public String hostname() {
        return this.hostname(PartitionEndpointKey.builder().build());
    }

    default public String hostname(PartitionEndpointKey key) {
        throw new UnsupportedOperationException();
    }

    public String id();

    public String name();

    public String regionRegex();

    public static PartitionMetadata of(String partition) {
        return MetadataLoader.partitionMetadata(partition);
    }

    public static PartitionMetadata of(Region region) {
        return MetadataLoader.partitionMetadata(region);
    }
}

