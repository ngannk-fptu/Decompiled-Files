/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConfImportStepConfig {
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final boolean includesAttachments;
    @JsonProperty
    private final String fileId;
    @JsonProperty
    private final String confTaskId;

    @JsonCreator
    public ConfImportStepConfig(@JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="includesAttachments") boolean includesAttachments, @JsonProperty(value="fileId") String fileId, @JsonProperty(value="confTaskId") String confTaskId) {
        this.spaceKey = spaceKey;
        this.includesAttachments = includesAttachments;
        this.fileId = fileId;
        this.confTaskId = confTaskId;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public boolean isIncludesAttachments() {
        return this.includesAttachments;
    }

    public String getFileId() {
        return this.fileId;
    }

    public String getConfTaskId() {
        return this.confTaskId;
    }
}

