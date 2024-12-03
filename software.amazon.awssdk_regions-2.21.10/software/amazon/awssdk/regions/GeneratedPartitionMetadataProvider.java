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
    private static final Map<String, PartitionMetadata> PARTITION_METADATA = ImmutableMap.builder().put((Object)"aws", (Object)new AwsPartitionMetadata()).put((Object)"aws-cn", (Object)new AwsCnPartitionMetadata()).put((Object)"aws-us-gov", (Object)new AwsUsGovPartitionMetadata()).put((Object)"aws-iso", (Object)new AwsIsoPartitionMetadata()).put((Object)"aws-iso-b", (Object)new AwsIsoBPartitionMetadata()).put((Object)"aws-iso-e", (Object)new AwsIsoEPartitionMetadata()).put((Object)"aws-iso-f", (Object)new AwsIsoFPartitionMetadata()).build();

    @Override
    public PartitionMetadata partitionMetadata(String partition) {
        return PARTITION_METADATA.get(partition);
    }

    @Override
    public PartitionMetadata partitionMetadata(Region region) {
        return PARTITION_METADATA.values().stream().filter(p -> region.id().matches(p.regionRegex())).findFirst().orElse(new AwsPartitionMetadata());
    }
}

