/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.PartitionMetadataProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.partitionmetadata.AwsCnPartitionMetadata;
import software.amazon.awssdk.regions.partitionmetadata.AwsIsoBPartitionMetadata;
import software.amazon.awssdk.regions.partitionmetadata.AwsIsoEPartitionMetadata;
import software.amazon.awssdk.regions.partitionmetadata.AwsIsoFPartitionMetadata;
import software.amazon.awssdk.regions.partitionmetadata.AwsIsoPartitionMetadata;
import software.amazon.awssdk.regions.partitionmetadata.AwsPartitionMetadata;
import software.amazon.awssdk.regions.partitionmetadata.AwsUsGovPartitionMetadata;
import software.amazon.awssdk.utils.ImmutableMap;

@SdkPublicApi
public final class GeneratedPartitionMetadataProvider
implements PartitionMetadataProvider {
    private static final Map<String, PartitionMetadata> PARTITION_METADATA = ImmutableMap.builder().put("aws", new AwsPartitionMetadata()).put("aws-cn", (AwsPartitionMetadata)((Object)new AwsCnPartitionMetadata())).put("aws-us-gov", (AwsPartitionMetadata)((Object)new AwsUsGovPartitionMetadata())).put("aws-iso", (AwsPartitionMetadata)((Object)new AwsIsoPartitionMetadata())).put("aws-iso-b", (AwsPartitionMetadata)((Object)new AwsIsoBPartitionMetadata())).put("aws-iso-e", (AwsPartitionMetadata)((Object)new AwsIsoEPartitionMetadata())).put("aws-iso-f", (AwsPartitionMetadata)((Object)new AwsIsoFPartitionMetadata())).build();

    @Override
    public PartitionMetadata partitionMetadata(String partition) {
        return PARTITION_METADATA.get(partition);
    }

    @Override
    public PartitionMetadata partitionMetadata(Region region) {
        return PARTITION_METADATA.values().stream().filter(p -> region.id().matches(p.regionRegex())).findFirst().orElse(new AwsPartitionMetadata());
    }
}

