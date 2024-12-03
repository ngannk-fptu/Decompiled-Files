/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CreateSinglepartFileResponse {
    @JsonProperty(value="fileId")
    private final String fileId;
    @JsonProperty(value="uploadUrl")
    private final String uploadUrl;

    @JsonCreator
    public CreateSinglepartFileResponse(@JsonProperty(value="fileId") String fileId, @JsonProperty(value="uploadUrl") String uploadUrl) {
        this.fileId = fileId;
        this.uploadUrl = uploadUrl;
    }

    @Generated
    public String getFileId() {
        return this.fileId;
    }

    @Generated
    public String getUploadUrl() {
        return this.uploadUrl;
    }
}

