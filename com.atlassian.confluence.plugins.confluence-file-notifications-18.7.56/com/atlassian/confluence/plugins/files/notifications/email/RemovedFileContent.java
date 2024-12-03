/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.api.model.content.id.ContentId;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class RemovedFileContent {
    private final ContentId fileContentId;
    private final String fileTitle;
    private final String fileCommentBody;
    private final int fileUnresolvedComments;
    private final int fileVersion;

    @JsonCreator
    public RemovedFileContent(@JsonProperty(value="fileContentId") ContentId fileContentId, @JsonProperty(value="fileTitle") String fileTitle, @JsonProperty(value="fileCommentBody") String fileCommentBody, @JsonProperty(value="fileUnresolvedComments") int fileUnresolvedComments, @JsonProperty(value="fileVersion") int fileVersion) {
        this.fileTitle = fileTitle;
        this.fileCommentBody = fileCommentBody;
        this.fileUnresolvedComments = fileUnresolvedComments;
        this.fileVersion = fileVersion;
        this.fileContentId = fileContentId;
    }

    public String getFileTitle() {
        return this.fileTitle;
    }

    public String getFileCommentBody() {
        return this.fileCommentBody;
    }

    public int getFileUnresolvedComments() {
        return this.fileUnresolvedComments;
    }

    public int getFileVersion() {
        return this.fileVersion;
    }

    public ContentId getFileContentId() {
        return this.fileContentId;
    }
}

