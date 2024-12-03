/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.plugins.emailgateway.api.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

public class ReplyToCommentByEmailAnalytics {

    @AsynchronousPreferred
    @EventName(value="confluence.inbound-email.reply-to-email.create.comment")
    public static class CreateComment {
        private final long contentID;
        private final String contentType;
        private final String spaceKey;
        private final long commentID;

        public CreateComment(long contentID, String contentType, String spaceKey, long commentID) {
            this.contentID = contentID;
            this.contentType = contentType;
            this.spaceKey = spaceKey;
            this.commentID = commentID;
        }

        public long getContentID() {
            return this.contentID;
        }

        public long getCommentID() {
            return this.commentID;
        }

        public String getContentType() {
            return this.contentType;
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.inbound-email.reply-to-email.disable.feature")
    public static class DisableFeatureEvent {
    }

    @AsynchronousPreferred
    @EventName(value="confluence.inbound-email.reply-to-email.enable.feature")
    public static class EnableFeatureEvent {
    }
}

