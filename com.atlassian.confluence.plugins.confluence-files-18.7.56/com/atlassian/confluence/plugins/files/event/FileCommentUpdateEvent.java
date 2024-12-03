/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.files.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.files.api.FileComment;
import com.atlassian.confluence.plugins.files.entities.FileCommentInput;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.annotation.Nonnull;

public class FileCommentUpdateEvent
extends ConfluenceEvent {
    private final Attachment parentFile;
    private final FileCommentInput fileCommentInput;
    private final FileComment fileComment;
    private final ConfluenceUser originatingUser;

    public FileCommentUpdateEvent(@Nonnull Object src, @Nonnull Attachment parentFile, @Nonnull FileCommentInput fileCommentInput, @Nonnull FileComment fileComment, ConfluenceUser originatingUser) {
        super(src);
        this.parentFile = parentFile;
        this.fileCommentInput = fileCommentInput;
        this.fileComment = fileComment;
        this.originatingUser = originatingUser;
    }

    @Nonnull
    public Attachment getParentFile() {
        return this.parentFile;
    }

    @Nonnull
    public FileCommentInput getFileCommentInput() {
        return this.fileCommentInput;
    }

    @Nonnull
    public FileComment getFileComment() {
        return this.fileComment;
    }

    public ConfluenceUser getOriginatingUser() {
        return this.originatingUser;
    }
}

