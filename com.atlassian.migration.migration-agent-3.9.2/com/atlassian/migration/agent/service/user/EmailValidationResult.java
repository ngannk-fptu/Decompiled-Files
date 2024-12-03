/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import lombok.Generated;

public class EmailValidationResult {
    private final int invalidEmailsCount;
    private final String fileId;

    @Generated
    public int getInvalidEmailsCount() {
        return this.invalidEmailsCount;
    }

    @Generated
    public String getFileId() {
        return this.fileId;
    }

    @Generated
    public EmailValidationResult(int invalidEmailsCount, String fileId) {
        this.invalidEmailsCount = invalidEmailsCount;
        this.fileId = fileId;
    }
}

