/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.partitions.PartitionsLoader;
import com.amazonaws.regions.LegacyRegionXmlMetadataBuilder;
import com.amazonaws.regions.RegionMetadata;

public class RegionMetadataFactory {
    private RegionMetadataFactory() {
    }

    public static RegionMetadata create() {
        RegionMetadata metadata = RegionMetadataFactory.createLegacyXmlRegionMetadata();
        if (metadata == null) {
            metadata = new RegionMetadata(new PartitionsLoader().build());
        }
        return metadata;
    }

    private static RegionMetadata createLegacyXmlRegionMetadata() {
        return new LegacyRegionXmlMetadataBuilder().build();
    }
}

