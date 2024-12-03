/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import lombok.Generated;

public class StorageFileRequest {
    private final String uploadMethod;

    @Generated
    public StorageFileRequest(String uploadMethod) {
        this.uploadMethod = uploadMethod;
    }

    @Generated
    public String getUploadMethod() {
        return this.uploadMethod;
    }
}

