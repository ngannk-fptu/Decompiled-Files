/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.regionmetadata;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.RegionMetadata;

@SdkPublicApi
public final class CnNorth1
implements RegionMetadata {
    private static final String ID = "cn-north-1";
    private static final String DOMAIN = "amazonaws.com.cn";
    private static final String DESCRIPTION = "China (Beijing)";
    private static final String PARTITION_ID = "aws-cn";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String domain() {
        return DOMAIN;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public PartitionMetadata partition() {
        return PartitionMetadata.of(PARTITION_ID);
    }
}

