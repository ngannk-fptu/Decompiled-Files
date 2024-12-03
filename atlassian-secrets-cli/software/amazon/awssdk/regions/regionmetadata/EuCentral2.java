/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.regionmetadata;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.RegionMetadata;

@SdkPublicApi
public final class EuCentral2
implements RegionMetadata {
    private static final String ID = "eu-central-2";
    private static final String DOMAIN = "amazonaws.com";
    private static final String DESCRIPTION = "Europe (Zurich)";
    private static final String PARTITION_ID = "aws";

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

