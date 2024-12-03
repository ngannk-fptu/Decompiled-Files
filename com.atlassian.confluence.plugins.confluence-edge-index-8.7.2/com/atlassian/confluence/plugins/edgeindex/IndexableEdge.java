/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.edgeindex;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
final class IndexableEdge {
    private final String edgeId;
    private final String userKey;
    private final Long targetId;
    private final Date date;
    private final String typeKey;

    @JsonCreator
    IndexableEdge(@JsonProperty(value="edgeId") String edgeId, @JsonProperty(value="userKey") String userKey, @JsonProperty(value="targetId") Long targetId, @JsonProperty(value="date") Date date, @JsonProperty(value="typeKey") String typeKey) {
        this.edgeId = edgeId;
        this.userKey = userKey;
        this.targetId = targetId;
        this.date = date;
        this.typeKey = typeKey;
    }

    public String getEdgeId() {
        return this.edgeId;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public Long getTargetId() {
        return this.targetId;
    }

    public Date getDate() {
        return this.date;
    }

    public String getTypeKey() {
        return this.typeKey;
    }
}

