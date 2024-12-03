/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.migration.agent.service.confluence.request;

import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public abstract class AbstractImportPayload {
    public final boolean useFileStoreIds;
    public final String applicationId;
    public final String type;
    public final String migrationScopeId;
    public final String migrationId;
    public final List<MigrationCatalogueStorageFile> files;

    AbstractImportPayload(@JsonProperty(value="type") String type, @JsonProperty(value="migrationScopeId") String migrationScopeId, @JsonProperty(value="migrationId") String migrationId, @JsonProperty(value="files") List<MigrationCatalogueStorageFile> files) {
        this.useFileStoreIds = true;
        this.applicationId = "ccma";
        this.type = type;
        this.migrationScopeId = migrationScopeId;
        this.migrationId = migrationId;
        this.files = files;
    }
}

