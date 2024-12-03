/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class GlobalEntitiesExportStepConfig {
    @JsonProperty
    private final String fileId;
    @JsonProperty
    private final String cloudId;

    @JsonCreator
    public GlobalEntitiesExportStepConfig(@JsonProperty(value="fileId") String fileId, @JsonProperty(value="cloudId") String cloudId) {
        this.fileId = fileId;
        this.cloudId = cloudId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}

