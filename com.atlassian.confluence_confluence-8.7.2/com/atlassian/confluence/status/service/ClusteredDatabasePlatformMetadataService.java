/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.status.service.PlatformAwareClusteredDatabaseMetadataRetriever;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;
import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformMetadata;
import java.sql.Connection;
import java.util.Optional;

public class ClusteredDatabasePlatformMetadataService {
    private final PlatformAwareClusteredDatabaseMetadataRetriever clusteredDbMetadataRetriever;

    public ClusteredDatabasePlatformMetadataService(PlatformAwareClusteredDatabaseMetadataRetriever clusteredDbMetadataRetriever) {
        this.clusteredDbMetadataRetriever = clusteredDbMetadataRetriever;
    }

    public Optional<ClusteredDatabasePlatformMetadata> getClusteredDatabaseMetadataForPlatform(Connection databaseConnection, CloudPlatformType platformType) {
        if (platformType.equals((Object)CloudPlatformType.AWS)) {
            return this.clusteredDbMetadataRetriever.getClusteredDatabaseMetadata(databaseConnection);
        }
        return Optional.empty();
    }
}

