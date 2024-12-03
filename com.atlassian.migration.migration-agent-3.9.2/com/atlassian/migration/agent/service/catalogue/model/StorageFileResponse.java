/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class StorageFileResponse {
    private final String fileId;
    private final String uploadId;

    public StorageFileResponse(@JsonProperty(value="fileId") String fileId, @JsonProperty(value="uploadId") String uploadId) {
        this.fileId = fileId;
        this.uploadId = uploadId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public String getUploadId() {
        return this.uploadId;
    }
}

