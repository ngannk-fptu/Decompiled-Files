/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.migration.agent.service.confluence.request;

import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.confluence.request.AbstractImportPayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public abstract class AbstractSpaceImportPayload
extends AbstractImportPayload {
    AbstractSpaceImportPayload(@JsonProperty(value="migrationScopeId") String migrationScopeId, @JsonProperty(value="migrationId") String migrationId, @JsonProperty(value="files") List<MigrationCatalogueStorageFile> files) {
        super("space", migrationScopeId, migrationId, files);
    }
}

