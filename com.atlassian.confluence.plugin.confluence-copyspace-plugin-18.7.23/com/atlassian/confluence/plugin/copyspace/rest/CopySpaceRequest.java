/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugin.copyspace.rest;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CopySpaceRequest {
    private final boolean copyAttachments;
    private final boolean copyComments;
    private final boolean copyLabels;
    private final boolean copyBlogPosts;
    private final boolean copyPages;
    private final boolean copyMetadata;
    private final boolean preserveWatchers;
    private final String newDescription;
    private final String newName;
    private final String newKey;
    private final String oldKey;

    @JsonCreator
    public CopySpaceRequest(@JsonProperty(value="copyAttachments") boolean copyAttachments, @JsonProperty(value="copyComments") boolean copyComments, @JsonProperty(value="copyLabels") boolean copyLabels, @JsonProperty(value="copyBlogPosts") boolean copyBlogPosts, @JsonProperty(value="copyPages") boolean copyPages, @JsonProperty(value="keepMetaData") boolean copyMetadata, @JsonProperty(value="preserveWatchers") boolean preserveWatchers, @JsonProperty(value="newDescription") String newDescription, @JsonProperty(value="newName") String newName, @JsonProperty(value="newKey") String newKey, @JsonProperty(value="oldKey") String oldKey) {
        this.copyAttachments = copyAttachments;
        this.copyComments = copyComments;
        this.copyLabels = copyLabels;
        this.copyBlogPosts = copyBlogPosts;
        this.copyPages = copyPages;
        this.copyMetadata = copyMetadata;
        this.preserveWatchers = preserveWatchers;
        this.newDescription = newDescription;
        this.newName = newName;
        this.newKey = newKey;
        this.oldKey = oldKey;
    }

    public boolean isCopyAttachments() {
        return this.copyAttachments;
    }

    public boolean isCopyComments() {
        return this.copyComments;
    }

    public boolean isCopyLabels() {
        return this.copyLabels;
    }

    public boolean isCopyBlogPosts() {
        return this.copyBlogPosts;
    }

    public boolean isCopyPages() {
        return this.copyPages;
    }

    public boolean isCopyMetadata() {
        return this.copyMetadata;
    }

    public boolean isPreserveWatchers() {
        return this.preserveWatchers;
    }

    public String getNewDescription() {
        return this.newDescription;
    }

    public String getNewName() {
        return this.newName;
    }

    public String getNewKey() {
        return this.newKey;
    }

    public String getOldKey() {
        return this.oldKey;
    }
}

