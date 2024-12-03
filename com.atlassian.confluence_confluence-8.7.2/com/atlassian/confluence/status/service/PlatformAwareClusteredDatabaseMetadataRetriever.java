/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformMetadata;
import java.sql.Connection;
import java.util.Optional;

public interface PlatformAwareClusteredDatabaseMetadataRetriever {
    public Optional<ClusteredDatabasePlatformMetadata> getClusteredDatabaseMetadata(Connection var1);
}

