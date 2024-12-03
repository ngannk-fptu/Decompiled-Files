/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.version;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

class PluginStatus {
    @JsonProperty
    final String pluginVersionLastChecked;
    @JsonProperty
    final boolean outdated;
    @JsonProperty
    final long timestamp;
    @JsonProperty
    final LocalDate upgradeBy;

    @JsonCreator
    PluginStatus(@JsonProperty(value="pluginVersionLastChecked") String pluginVersionLastChecked, @JsonProperty(value="outdated") boolean outdated, @JsonProperty(value="upgradeBy") @JsonFormat(pattern="yyyy-MM-dd") LocalDate upgradeBy, @JsonProperty(value="timestamp") long timestamp) {
        this.pluginVersionLastChecked = pluginVersionLastChecked;
        this.outdated = outdated;
        this.timestamp = timestamp;
        this.upgradeBy = upgradeBy;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}

