/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.pagehierarchy.rest;

import com.atlassian.confluence.api.model.content.id.ContentId;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DeletePageHierarchyRequest {
    private final ContentId targetPageId;
    private final boolean deleteHierarchy;
    private final Set<Long> targetIds;

    @JsonCreator
    public DeletePageHierarchyRequest(@JsonProperty(value="targetPageId") ContentId targetPageId, @JsonProperty(value="deleteHierarchy") boolean deleteHierarchy, @JsonProperty(value="targetIds") Set<Long> targetIds) {
        this.targetPageId = targetPageId;
        this.deleteHierarchy = deleteHierarchy;
        this.targetIds = targetIds;
    }

    public boolean isDeleteHierarchy() {
        return this.deleteHierarchy;
    }

    public ContentId getTargetPageId() {
        return this.targetPageId;
    }

    public Set<Long> getTargetIds() {
        return this.targetIds;
    }
}

