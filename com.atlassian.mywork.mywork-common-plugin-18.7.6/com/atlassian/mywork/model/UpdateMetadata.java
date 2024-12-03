/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.model;

import com.atlassian.mywork.rest.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.node.ObjectNode;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdateMetadata
implements JsonObject {
    @JsonProperty
    private final String globalId;
    @JsonProperty
    private final ObjectNode condition;
    @JsonProperty
    private final ObjectNode metadata;

    @JsonCreator
    public UpdateMetadata(@JsonProperty(value="globalId") String globalId, @JsonProperty(value="condition") ObjectNode condition, @JsonProperty(value="metadata") ObjectNode metadata) {
        this.globalId = globalId;
        this.condition = condition;
        this.metadata = metadata;
    }

    public String getGlobalId() {
        return this.globalId;
    }

    public ObjectNode getCondition() {
        return this.condition;
    }

    public ObjectNode getMetadata() {
        return this.metadata;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals((Object)this, (Object)o, (String[])new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, (String[])new String[0]);
    }
}

