/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.service.impl.StepTypeEnum;
import lombok.Generated;

public enum StepType implements StepTypeEnum
{
    ATTACHMENT_UPLOAD("Checking attachments", "UPLOADING_ATTACHMENTS", "confluenceAttachmentUpload"),
    CONFLUENCE_EXPORT("Exporting database", "EXPORTING", "confluenceSpaceExport"),
    SPACE_USERS_MIGRATION("Migrating space users", "MIGRATING_SPACE_USERS", "confluenceSpaceUserExport"),
    DATA_UPLOAD("Uploading data", "UPLOADING", "confluenceSpaceUpload"),
    CONFLUENCE_IMPORT("Importing to Confluence Cloud", "IMPORTING", "confluenceSpaceImport"),
    USERS_MIGRATION("Migrating users and groups", "MIGRATING_USERS_GROUPS", "userMigration"),
    GLOBAL_ENTITIES_EXPORT("Exporting global templates from database", "EXPORTING", "globalEntitiesExport"),
    GLOBAL_ENTITIES_DATA_UPLOAD("Uploading global templates data", "UPLOADING", "globalEntitiesUpload"),
    GLOBAL_ENTITIES_IMPORT("Importing global templates to Confluence Cloud", "IMPORTING", "globalEntitiesImport");

    private final String displayName;
    private final String detailedStatus;
    private final String operationKey;

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    public String getOperationKey() {
        return this.operationKey;
    }

    @Override
    @Generated
    public String getDetailedStatus() {
        return this.detailedStatus;
    }

    @Generated
    private StepType(String displayName, String detailedStatus, String operationKey) {
        this.displayName = displayName;
        this.detailedStatus = detailedStatus;
        this.operationKey = operationKey;
    }
}

