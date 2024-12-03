/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.service.impl.StepTypeEnum;
import lombok.Generated;

public enum StepSubType implements StepTypeEnum
{
    USERS_EXPORT("Exporting users and groups", "EXPORTING"),
    USERS_UPLOAD("Uploading users and groups", "UPLOADING"),
    USERS_IMPORT("Importing users and groups", "IMPORTING");

    private final String displayName;
    private final String detailedStatus;

    @Override
    @Generated
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    @Generated
    public String getDetailedStatus() {
        return this.detailedStatus;
    }

    @Generated
    private StepSubType(String displayName, String detailedStatus) {
        this.displayName = displayName;
        this.detailedStatus = detailedStatus;
    }
}

