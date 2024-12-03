/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConfExportStepConfig {
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final String cloudId;

    @JsonCreator
    public ConfExportStepConfig(@JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="cloudId") String cloudId) {
        this.spaceKey = spaceKey;
        this.cloudId = cloudId;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}

