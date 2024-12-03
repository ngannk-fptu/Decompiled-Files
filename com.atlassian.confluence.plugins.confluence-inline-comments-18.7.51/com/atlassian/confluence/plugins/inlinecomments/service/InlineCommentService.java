/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.inlinecomments.entities.HistoryPageInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentCreationBean;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentResult;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentUpdateBean;
import com.atlassian.confluence.plugins.inlinecomments.entities.TopLevelInlineComment;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.Date;

public interface InlineCommentService {
    public Result create(InlineCommentCreationBean var1);

    public Result createAsPageLevelComment(InlineCommentCreationBean var1);

    public InlineCommentResult<TopLevelInlineComment> getComment(long var1);

    public InlineCommentResult<Collection<TopLevelInlineComment>> getCommentThreads(long var1);

    public InlineCommentResult deleteInlineComment(long var1);

    public InlineCommentResult updateComment(InlineCommentUpdateBean var1);

    public InlineCommentResult updateResolveProperty(long var1, boolean var3, Date var4, ConfluenceUser var5, boolean var6, boolean var7);

    public InlineCommentResult updateResolveProperty(Comment var1, boolean var2, Date var3, ConfluenceUser var4, boolean var5, boolean var6);

    public HistoryPageInlineComment getHistoryPageComment(Long var1);

    public InlineCommentResult<Long> getInlineCommentId(Long var1);

    public static class Result {
        private final Status status;
        private final long commentId;
        private final String errorMessage;

        public Result(Status status) {
            this(status, 0L);
        }

        public Result(Status status, long commentId) {
            this(status, commentId, null);
        }

        public Result(Status status, String errorMessage) {
            this(status, 0L, errorMessage);
        }

        private Result(Status status, long commentId, String errorMessage) {
            this.status = status;
            this.commentId = commentId;
            this.errorMessage = errorMessage;
        }

        public Status getStatus() {
            return this.status;
        }

        public long getCommentId() {
            return this.commentId;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public static enum Status {
            SUCCESS,
            NOT_PERMITTED,
            STALE_STORAGE_FORMAT,
            CANNOT_MODIFY_STORAGE_FORMAT,
            OTHER_FAILURE,
            BAD_REQUEST_UTF8_MYSQL_ERROR;

        }
    }
}

