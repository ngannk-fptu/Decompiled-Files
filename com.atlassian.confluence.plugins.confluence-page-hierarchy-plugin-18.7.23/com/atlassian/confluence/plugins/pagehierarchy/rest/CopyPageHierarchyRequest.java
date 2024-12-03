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
import com.atlassian.confluence.plugins.pagehierarchy.rest.CopyPageHierarchyTitleOptions;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CopyPageHierarchyRequest {
    private final boolean copyAttachments;
    private final boolean copyPermissions;
    private final boolean copyLabels;
    private final ContentId originalPageId;
    private final ContentId destinationPageId;
    private final CopyPageHierarchyTitleOptions titleOptions;

    @JsonCreator
    public CopyPageHierarchyRequest(@JsonProperty(value="copyAttachments") boolean copyAttachments, @JsonProperty(value="copyPermissions") boolean copyPermissions, @JsonProperty(value="copyLabels") boolean copyLabels, @JsonProperty(value="originalPageId") ContentId originalPageId, @JsonProperty(value="destinationPageId") ContentId destinationPageId, @JsonProperty(value="titleOptions") CopyPageHierarchyTitleOptions titleOptions) {
        this.copyAttachments = copyAttachments;
        this.copyPermissions = copyPermissions;
        this.copyLabels = copyLabels;
        this.originalPageId = originalPageId;
        this.destinationPageId = destinationPageId;
        this.titleOptions = titleOptions;
    }

    public CopyPageHierarchyTitleOptions getTitleOptions() {
        return this.titleOptions;
    }

    public boolean isCopyAttachments() {
        return this.copyAttachments;
    }

    public boolean isCopyPermissions() {
        return this.copyPermissions;
    }

    public boolean isCopyLabels() {
        return this.copyLabels;
    }

    public ContentId getOriginalPageId() {
        return this.originalPageId;
    }

    public ContentId getDestinationPageId() {
        return this.destinationPageId;
    }
}

