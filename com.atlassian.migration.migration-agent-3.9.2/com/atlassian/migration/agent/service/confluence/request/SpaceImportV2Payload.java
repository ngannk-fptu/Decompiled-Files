/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.migration.agent.service.confluence.request;

import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.confluence.request.AbstractSpaceImportPayload;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class SpaceImportV2Payload
extends AbstractSpaceImportPayload {
    public final String filestoreId;
    public final String planId;

    public SpaceImportV2Payload(@JsonProperty(value="filestoreId") String filestoreId, @JsonProperty(value="planId") String planId, @JsonProperty(value="migrationScopeId") String migrationScopeId, @JsonProperty(value="migrationId") String migrationId, @JsonProperty(value="files") List<MigrationCatalogueStorageFile> files) {
        super(migrationScopeId, migrationId, files);
        this.filestoreId = filestoreId;
        this.planId = planId;
    }
}

