/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import javax.annotation.Nullable;
import lombok.Generated;

public class CreateFileRequest {
    private String filename;
    @Nullable
    private String prefix;

    @Generated
    public String getFilename() {
        return this.filename;
    }

    @Nullable
    @Generated
    public String getPrefix() {
        return this.prefix;
    }

    @Generated
    public CreateFileRequest(String filename, @Nullable String prefix) {
        this.filename = filename;
        this.prefix = prefix;
    }
}

