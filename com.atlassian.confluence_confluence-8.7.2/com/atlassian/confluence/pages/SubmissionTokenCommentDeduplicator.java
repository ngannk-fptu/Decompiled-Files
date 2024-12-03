/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.NewCommentDeduplicator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class SubmissionTokenCommentDeduplicator
implements NewCommentDeduplicator {
    private static final String COMMENT_UUID_PROPERTY_NAME = "comment-uuid";
    private final UUID submissionToken;

    public SubmissionTokenCommentDeduplicator(UUID submissionToken) {
        this.submissionToken = submissionToken;
    }

    private boolean isDuplicateOf(Comment existingComment) {
        String commentUuidProperty;
        if (this.submissionToken != null && StringUtils.isNotBlank((CharSequence)(commentUuidProperty = existingComment.getProperties().getStringProperty(COMMENT_UUID_PROPERTY_NAME)))) {
            return this.submissionToken.toString().equals(commentUuidProperty);
        }
        return false;
    }

    @Override
    public Optional<Comment> getDuplicateComment(Iterable<Comment> existingComments) {
        return StreamSupport.stream(existingComments.spliterator(), false).filter(this::isDuplicateOf).findFirst();
    }

    @Override
    public void newCommentSaved(Comment comment) {
        UUID uuid = this.submissionToken != null ? this.submissionToken : UUID.randomUUID();
        comment.getProperties().setStringProperty(COMMENT_UUID_PROPERTY_NAME, uuid.toString());
    }
}

