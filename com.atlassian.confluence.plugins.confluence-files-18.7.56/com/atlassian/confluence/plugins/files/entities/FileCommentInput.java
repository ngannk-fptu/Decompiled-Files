/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.entities;

import com.atlassian.confluence.plugins.files.api.CommentAnchor;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FileCommentInput {
    @JsonProperty
    private final long parentId;
    @JsonProperty
    private final CommentAnchor anchor;
    @JsonProperty
    private final String commentBody;
    @JsonProperty
    private final Boolean resolved;

    @JsonCreator
    public FileCommentInput(@JsonProperty(value="parentId") long parentId, @JsonProperty(value="anchor") CommentAnchor anchor, @JsonProperty(value="commentBody") String commentBody, @JsonProperty(value="resolved") Boolean resolved) {
        this.parentId = parentId;
        this.anchor = anchor;
        this.commentBody = commentBody;
        this.resolved = resolved;
    }

    @Nullable
    public Boolean isResolved() {
        return this.resolved;
    }

    @Nullable
    public String getCommentBody() {
        return this.commentBody;
    }

    @Nullable
    public CommentAnchor getAnchor() {
        return this.anchor;
    }

    public long getParentId() {
        return this.parentId;
    }
}

