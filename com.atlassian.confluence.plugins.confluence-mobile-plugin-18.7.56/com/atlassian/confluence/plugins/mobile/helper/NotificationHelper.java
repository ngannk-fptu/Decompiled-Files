/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationFilter
 *  com.atlassian.mywork.model.NotificationFilter$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.node.ObjectNode
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.helper;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.mobile.dto.AbstractPageDto;
import com.atlassian.confluence.plugins.mobile.dto.ActionContentDto;
import com.atlassian.confluence.plugins.mobile.dto.AttachmentDto;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.helper.ContentHelper;
import com.atlassian.confluence.plugins.mobile.notification.NotificationCategory;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileAbstractPageConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationHelper {
    private static final Pattern CONTENT_ID_PATTERN = Pattern.compile("(.*&)?contentId=(\\d+)(&.*)?");
    public static final String TASK_ASSIGN_ACTION = "task.assign";
    public static final String COMMENT_ACTION = "comment";
    public static final String MENTION_ACTION = "mentions.user";
    public static final String SHARE_ACTION = "share";
    public static final String LIKE_ACTION = "like";
    public static final String NOTIFICATION_BLOG = "blog";
    public static final List<String> WORKBOX_ACTION_LIST = Collections.unmodifiableList(Arrays.asList("task.assign", "comment", "mentions.user", "share", "like"));
    public static final List<String> IMPORTANT_ACTION_LIST = Collections.unmodifiableList(Arrays.asList("mentions.user", "share", "task.assign", "comment"));
    private final MobileSpaceConverter spaceConverter;
    private final MobileAbstractPageConverter abstractPageConverter;

    @Autowired
    public NotificationHelper(MobileSpaceConverter spaceConverter, MobileAbstractPageConverter abstractPageConverter) {
        this.spaceConverter = spaceConverter;
        this.abstractPageConverter = abstractPageConverter;
    }

    public static Long getContainerId(@Nonnull Notification notification) {
        if (TASK_ASSIGN_ACTION.equals(notification.getAction())) {
            return NotificationHelper.getTaskContentId(notification);
        }
        return notification.getMetadata().get("pageId").asLong();
    }

    public static Long getContentId(@Nonnull Notification notification) {
        if (TASK_ASSIGN_ACTION.equals(notification.getAction())) {
            return NotificationHelper.getTaskContentId(notification);
        }
        return notification.getMetadata().get("contentId").asLong();
    }

    public static ContentType getContentType(@Nonnull Notification notification) {
        if (NOTIFICATION_BLOG.equals(notification.getEntity())) {
            return ContentType.BLOG_POST;
        }
        return ContentType.valueOf((String)notification.getEntity());
    }

    public static NotificationCategory getCategory(@Nonnull Notification notification) {
        String actionType = notification.getAction();
        ContentType contentType = NotificationHelper.getContentType(notification);
        if (LIKE_ACTION.equals(actionType)) {
            return NotificationCategory.getLikeCategory(contentType);
        }
        if (MENTION_ACTION.equals(actionType)) {
            return NotificationCategory.getMentionCategory(contentType);
        }
        if (COMMENT_ACTION.equals(actionType)) {
            ObjectNode metadata = notification.getMetadata();
            if (metadata != null) {
                if (metadata.get("replyYourComment") != null && metadata.get("replyYourComment").asBoolean()) {
                    return NotificationCategory.COMMENT_REPLY;
                }
                if (metadata.get("commentOnYourPage") != null) {
                    return NotificationCategory.COMMENT_CONTENT_CREATOR;
                }
            }
            return NotificationCategory.COMMENT;
        }
        if (TASK_ASSIGN_ACTION.equals(actionType)) {
            return NotificationCategory.TASK_ASSIGN;
        }
        if (SHARE_ACTION.equals(actionType)) {
            ObjectNode metadata = notification.getMetadata();
            boolean isShareGroup = metadata != null && metadata.get("groupName") != null;
            return isShareGroup ? NotificationCategory.SHARE_GROUP : NotificationCategory.SHARE;
        }
        return null;
    }

    public static NotificationFilter build(long from, long to, @Nullable List<Long> notificationIds, @Nullable List<Long> pageIds, @Nullable List<String> actions, @Nullable String appId) {
        if (to > 0L && from > 0L && from > to) {
            throw new BadRequestException("When to and from are specified, 'from' timestamp needs must be before <= 'to'");
        }
        NotificationFilter.Builder builder = NotificationFilter.builder();
        if (!AuthenticatedUserThreadLocal.isAnonymousUser()) {
            builder.userKey(AuthenticatedUserThreadLocal.get().getKey().getStringValue());
        }
        if (from > 0L) {
            builder.fromCreatedDate(new Date(from));
        }
        if (to > 0L) {
            builder.toCreatedDate(new Date(to));
        }
        builder.actions(actions);
        builder.notificationIds(notificationIds);
        builder.pageIds(pageIds);
        builder.appId(appId);
        return builder.build();
    }

    public ActionContentDto buildActionContent(@Nonnull ContentEntityObject ceo, @Nonnull Expansions expansions) {
        ActionContentDto actionContent = new ActionContentDto();
        this.setupActionContent(actionContent, ceo, expansions);
        return actionContent;
    }

    private AbstractPageDto buildPageDtoForNotifications(@Nonnull ContentEntityObject contentEntityObject, @Nonnull Expansions expansions) {
        ContentTypeEnum type = contentEntityObject.getTypeEnum();
        if (ContentTypeEnum.PAGE.equals((Object)type) || ContentTypeEnum.BLOG.equals((Object)type)) {
            return this.abstractPageConverter.to(contentEntityObject, expansions);
        }
        throw new IllegalStateException(String.format("Object with id '%s' and type '%s' did not return type blog or page", contentEntityObject.getId(), type));
    }

    private void setupActionContent(@Nonnull ActionContentDto content, @Nonnull ContentEntityObject ceo, @Nonnull Expansions expansions) {
        Objects.requireNonNull(ceo);
        if (ContentHelper.isPageOrBlog(ceo)) {
            AbstractPage page = (AbstractPage)ceo;
            content.setPage(this.buildPageDtoForNotifications((ContentEntityObject)page, expansions));
            content.setSpace(this.spaceConverter.to(page.getSpace(), expansions));
        }
        if (ceo instanceof Comment) {
            Comment comment = (Comment)ceo;
            CommentDto.Builder builder = CommentDto.builder().id(comment.getId());
            String location = ContentHelper.getCommentLocation(comment);
            if ("inline".equals(location) && comment.getParent() != null) {
                builder.parent(CommentDto.builder().id(comment.getParent().getId()).build());
            }
            builder.body(comment.getExcerpt()).location(location);
            content.setComment(builder.build());
            ContentEntityObject container = comment.getContainer();
            if (container != null && (container.getTypeEnum() == ContentTypeEnum.ATTACHMENT || ContentHelper.isPageOrBlog(container))) {
                this.setupActionContent(content, container, expansions);
            }
        }
        if (ceo instanceof Attachment) {
            Attachment attachment = (Attachment)ceo;
            content.setAttachment(AttachmentDto.builder().id(attachment.getId()).title(attachment.getTitle()).build());
            ContentEntityObject container = attachment.getContainer();
            if (container != null && ContentHelper.isPageOrBlog(container)) {
                this.setupActionContent(content, container, expansions);
            }
        }
    }

    private static Long getTaskContentId(Notification notification) {
        Matcher matcher = CONTENT_ID_PATTERN.matcher(notification.getGlobalId());
        if (matcher.matches()) {
            return Long.valueOf(matcher.group(2));
        }
        throw new ServiceException("Cannot find content id of notification with global id: " + notification.getGlobalId());
    }
}

