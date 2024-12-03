/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateActions;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateHtml;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateLozenge;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateMessage;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateUserFullNameList;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BatchTemplateCommentPattern
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "commentPattern";
    private static final String DEFAULT_HEADER_TEXT = "notifications.batch.comment.headertext.default";
    private final Iterable<UserKey> authors;
    private final BatchTemplateHtml commentBody;
    private final BatchTemplateLozenge status;
    private final boolean contextual;
    private final String inlineContext;
    private final BatchTemplateActions actions;
    private final BatchTemplateMessage message;
    private final boolean split;
    private final Iterable<BatchTemplateCommentPattern> replies;

    public BatchTemplateCommentPattern(Iterable<UserKey> authors, BatchTemplateHtml commentBody, BatchTemplateLozenge status, boolean contextual, String inlineContext, BatchTemplateActions actions, BatchTemplateMessage message, boolean split, Iterable<BatchTemplateCommentPattern> replies) {
        this.authors = authors;
        this.commentBody = commentBody;
        this.status = status;
        this.contextual = contextual;
        this.inlineContext = inlineContext;
        this.actions = actions;
        this.message = message;
        this.split = split;
        this.replies = replies;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    public BatchTemplateHtml getCommentBody() {
        return this.commentBody;
    }

    public Iterable<BatchTemplateCommentPattern> getReplies() {
        return this.replies;
    }

    public BatchTemplateActions getActions() {
        return this.actions;
    }

    public BatchTemplateLozenge getStatus() {
        return this.status;
    }

    public boolean isContextual() {
        return this.contextual;
    }

    public String getInlineContext() {
        return this.inlineContext;
    }

    public BatchTemplateMessage getMessage() {
        return this.message;
    }

    public Iterable<UserKey> getAuthors() {
        return this.authors;
    }

    public boolean isSplit() {
        return this.split;
    }

    public static class Builder {
        private List<UserKey> authors = new ArrayList<UserKey>();
        private BatchTemplateHtml commentBody;
        private BatchTemplateLozenge status;
        private boolean contextual;
        private boolean split;
        private String inlineContext;
        private List<BatchTemplateCommentPattern> replies = new ArrayList<BatchTemplateCommentPattern>();
        private BatchTemplateActions actions;
        private BatchTemplateMessage.Builder message;

        public Builder author(UserKey author) {
            this.authors.add(author);
            return this;
        }

        public Builder singleAuthor(UserKey author) {
            this.authors = Collections.singletonList(author);
            return this;
        }

        public Builder authors(Collection<UserKey> authors) {
            this.authors.addAll(authors);
            return this;
        }

        public Builder commentBody(BatchTemplateHtml commentBody) {
            this.commentBody = commentBody;
            return this;
        }

        public Builder reply(Builder reply) {
            this.replies.add(reply.build());
            return this;
        }

        public Builder replyPattern(BatchTemplateCommentPattern reply) {
            this.replies.add(reply);
            return this;
        }

        public Builder inlineContext(String inlineContext) {
            this.inlineContext = inlineContext;
            return this;
        }

        public Builder status(BatchTemplateLozenge status) {
            this.status = status;
            return this;
        }

        public Builder actions(BatchTemplateActions actions) {
            this.actions = actions;
            return this;
        }

        public Builder contextual(boolean contextual) {
            this.contextual = contextual;
            return this;
        }

        public Builder split(boolean split) {
            this.split = split;
            return this;
        }

        public Builder message(String message) {
            this.message = new BatchTemplateMessage.Builder(message);
            return this;
        }

        public Builder messageStringArg(String name, String value) {
            this.message.arg(name, value);
            return this;
        }

        public Builder messsageElementArg(String name, BatchTemplateElement value) {
            this.message.arg(name, value);
            return this;
        }

        public BatchTemplateMessage.Builder messageBuilder() {
            return this.message;
        }

        public BatchTemplateCommentPattern build() {
            if (this.message == null) {
                this.message = new BatchTemplateMessage.Builder(BatchTemplateCommentPattern.DEFAULT_HEADER_TEXT);
            }
            if (!this.authors.isEmpty()) {
                this.message.arg("users", new BatchTemplateUserFullNameList(this.authors));
            }
            if (this.status != null) {
                this.message.arg("status", this.status);
            }
            return new BatchTemplateCommentPattern(this.authors, this.commentBody, this.status, this.contextual, this.inlineContext, this.actions, this.message.build(), this.split, this.replies);
        }
    }
}

