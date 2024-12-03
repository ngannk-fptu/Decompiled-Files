/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.plugins.mobile.notification;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;

public final class NotificationCategory
extends BaseApiEnum {
    public static final NotificationCategory COMMENT = new NotificationCategory("comment", 0);
    public static final NotificationCategory COMMENT_REPLY = new NotificationCategory("comment-reply", 0);
    public static final NotificationCategory COMMENT_CONTENT_CREATOR = new NotificationCategory("comment-contentcreator", 0);
    public static final NotificationCategory MENTION_PAGE = new NotificationCategory("mention-page", 2);
    public static final NotificationCategory MENTION_BLOGPOST = new NotificationCategory("mention-blogpost", 2);
    public static final NotificationCategory MENTION_COMMENT = new NotificationCategory("mention-comment", 2);
    public static final NotificationCategory SHARE = new NotificationCategory("share", 2);
    public static final NotificationCategory SHARE_GROUP = new NotificationCategory("share-group", 1);
    public static final NotificationCategory TASK_ASSIGN = new NotificationCategory("task-assign", 2);
    public static final NotificationCategory LIKE_PAGE = new NotificationCategory("like-page", 0);
    public static final NotificationCategory LIKE_BLOGPOST = new NotificationCategory("like-blogpost", 0);
    public static final NotificationCategory LIKE_COMMENT = new NotificationCategory("like-comment", 0);
    public static final List<NotificationCategory> BUILT_IN = Collections.unmodifiableList(Arrays.asList(COMMENT, COMMENT_REPLY, COMMENT_CONTENT_CREATOR, MENTION_PAGE, MENTION_BLOGPOST, MENTION_COMMENT, SHARE, SHARE_GROUP, TASK_ASSIGN, LIKE_PAGE, LIKE_BLOGPOST, LIKE_COMMENT));
    private int priority;

    public NotificationCategory(String value, int priority) {
        super(value);
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    @JsonCreator
    public static NotificationCategory valueOf(@Nullable String value) {
        return BUILT_IN.stream().filter(category -> category.serialise().equals(value)).findFirst().orElse(null);
    }

    public static NotificationCategory getMentionCategory(ContentType contentType) {
        if (ContentType.PAGE.equals((Object)contentType)) {
            return MENTION_PAGE;
        }
        if (ContentType.BLOG_POST.equals((Object)contentType)) {
            return MENTION_BLOGPOST;
        }
        if (ContentType.COMMENT.equals((Object)contentType)) {
            return MENTION_COMMENT;
        }
        throw new ServiceException(contentType + " is not supported");
    }

    public static NotificationCategory getLikeCategory(ContentType contentType) {
        if (ContentType.PAGE.equals((Object)contentType)) {
            return LIKE_PAGE;
        }
        if (ContentType.BLOG_POST.equals((Object)contentType)) {
            return LIKE_BLOGPOST;
        }
        if (ContentType.COMMENT.equals((Object)contentType)) {
            return LIKE_COMMENT;
        }
        throw new ServiceException(contentType + " is not supported");
    }
}

