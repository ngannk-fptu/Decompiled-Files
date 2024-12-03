/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.regionmetadata;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.RegionMetadata;

@SdkPublicApi
public final class UsIsoEast1
implements RegionMetadata {
    private static final String ID = "us-iso-east-1";
    private static final String DOMAIN = "c2s.ic.gov";
    private static final String DESCRIPTION = "US ISO East";
    private static final String PARTITION_ID = "aws-iso";

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

