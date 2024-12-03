/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.notifications.batch.service.BatchContext
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.notifications.content.batching;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.notifications.batch.service.BatchContext;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import javax.annotation.Nullable;

@ExperimentalSpi
public class CommentContext
implements BatchContext {
    private final UserKey originator;
    private final long commentId;
    @Nullable
    private final Long parentCommentId;
    private final long pageId;
    @Nullable
    private final String notificationKey;

    public CommentContext(UserKey originator, long commentId, Option<Long> parentCommentId, long pageId) {
        this(originator, commentId, parentCommentId, pageId, (Maybe<String>)Option.none());
    }

    @Deprecated
    public CommentContext(UserKey originator, long commentId, Option<Long> parentCommentId, long pageId, Maybe<String> notificationKey) {
        this.originator = originator;
        this.commentId = commentId;
        this.parentCommentId = (Long)parentCommentId.getOrNull();
        this.pageId = pageId;
        this.notificationKey = (String)notificationKey.getOrNull();
    }

    public CommentContext(UserKey originator, long commentId, Optional<Long> parentCommentId, long pageId, Optional<String> notificationKey) {
        this.originator = originator;
        this.commentId = commentId;
        this.parentCommentId = parentCommentId.orElse(null);
        this.pageId = pageId;
        this.notificationKey = notificationKey.orElse(null);
    }

    public long getCommentId() {
        return this.commentId;
    }

    @Deprecated
    public Option<Long> getParentCommentId() {
        return Option.option((Object)this.parentCommentId);
    }

    public Optional<Long> optionalParentCommentId() {
        return Optional.ofNullable(this.parentCommentId);
    }

    public long getPageId() {
        return this.pageId;
    }

    public UserKey getOriginator() {
        return this.originator;
    }

    public Maybe<String> getNotificationKey() {
        return Option.option((Object)this.notificationKey);
    }

    public final Optional<String> optionalNotificationKey() {
        return Optional.ofNullable(this.notificationKey);
    }
}

