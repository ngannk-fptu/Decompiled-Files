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

public class UploadFilePartMCSResponse {
    @JsonProperty(value="uploadUrl")
    private final String uploadUrl;

    @JsonCreator
    public UploadFilePartMCSResponse(@JsonProperty(value="uploadUrl") String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getUploadUrl() {
        return this.uploadUrl;
    }
}

