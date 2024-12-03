/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class StorageFileDownloadResponse {
    @JsonProperty(value="downloadUrl")
    private final String downloadUrl;

    @JsonCreator
    public StorageFileDownloadResponse(@JsonProperty(value="downloadUrl") String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }
}

