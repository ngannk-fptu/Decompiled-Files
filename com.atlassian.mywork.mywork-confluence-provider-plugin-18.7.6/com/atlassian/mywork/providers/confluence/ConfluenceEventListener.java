/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationAddedEvent
 *  com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationEvent
 *  com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationRemovedEvent
 *  com.atlassian.confluence.event.events.like.LikeCreatedEvent
 *  com.atlassian.confluence.event.events.like.LikeEvent
 *  com.atlassian.confluence.event.events.like.LikeRemovedEvent
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Contained
 *  com.atlassian.confluence.plugins.emailtracker.api.EmailReadEvent
 *  com.atlassian.confluence.plugins.inlinecomments.events.InlineCommentResolveEvent
 *  com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent
 *  com.atlassian.confluence.plugins.mentions.api.MentionFinder
 *  com.atlassian.confluence.plugins.sharepage.api.ShareContentEvent
 *  com.atlassian.confluence.plugins.sharepage.api.ShareDraftEvent
 *  com.atlassian.confluence.plugins.sharepage.api.ShareEvent
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.mywork.event.notification.PushNotificationEvent
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationBuilder
 *  com.atlassian.mywork.service.NotificationService
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Timeout
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.ArrayNode
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationAddedEvent;
import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationEvent;
import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationRemovedEvent;
import com.atlassian.confluence.event.events.like.LikeCreatedEvent;
import com.atlassian.confluence.event.events.like.LikeEvent;
import com.atlassian.confluence.event.events.like.LikeRemovedEvent;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.plugins.emailtracker.api.EmailReadEvent;
import com.atlassian.confluence.plugins.inlinecomments.events.InlineCommentResolveEvent;
import com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent;
import com.atlassian.confluence.plugins.mentions.api.MentionFinder;
import com.atlassian.confluence.plugins.sharepage.api.ShareContentEvent;
import com.atlassian.confluence.plugins.sharepage.api.ShareDraftEvent;
import com.atlassian.confluence.plugins.sharepage.api.ShareEvent;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.event.notification.PushNotificationEvent;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.providers.confluence.FieldHelper;
import com.atlassian.mywork.service.NotificationService;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Timeout;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceEventListener {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceEventListener.class);
    private static final String ACTION_CONFLUENCE_USER_MENTION = "mentions.user";
    private static final String ACTION_CONFLUENCE_SHARE = "share";
    private static final String ACTION_CONFLUENCE_COMMENT_RESOLVE = "resolve";
    private static final String ACTION_CONFLUENCE_COMMENT_REOPEN = "reopen";
    private static final String ACTION_CONFLUENCE_COMMENT = "comment";
    private static final String ACTION_CONFLUENCE_LIKE = "like";
    private static final String ACTION_CONFLUENCE_INVITE = "invite";
    private static final String METADATA_KEY_USERNAME = "username";
    private static final String METADATA_KEY_CONTENT_ID = "contentId";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String EMAIL_MENTION_ACTION = "mention";
    private static final String EMAIL_CREATE_ACTION = "create";
    private static final List<String> EMAIL_READ_ACTIONS = Lists.newArrayList((Object[])new String[]{"create", "share", "mention", "like"});
    private final ContentEntityManager contentEntityManager;
    private final NotificationService notificationService;
    private final NotificationManager notificationManager;
    private final Renderer renderer;
    private final UserAccessor userManager;
    private final FieldHelper fieldHelper;
    private final LikeManager likeManager;
    private final TransactionTemplate transactionTemplate;
    private final MentionFinder mentionFinder;
    private final PermissionManager permissionManager;
    private final EventPublisher eventPublisher;

    public ConfluenceEventListener(NotificationService notificationService, ContentEntityManager contentEntityManager, NotificationManager notificationManager, Renderer renderer, UserAccessor userManager, FieldHelper fieldHelper, LikeManager likeManager, TransactionTemplate transactionTemplate, MentionFinder mentionFinder, PermissionManager permissionManager, EventPublisher eventPublisher) {
        this.notificationService = notificationService;
        this.contentEntityManager = contentEntityManager;
        this.notificationManager = notificationManager;
        this.renderer = renderer;
        this.userManager = userManager;
        this.fieldHelper = fieldHelper;
        this.likeManager = likeManager;
        this.transactionTemplate = transactionTemplate;
        this.mentionFinder = mentionFinder;
        this.permissionManager = permissionManager;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onConfluenceMentionEvent(ConfluenceMentionEvent event) throws Exception {
        ContentEntityObject content = event.getContent();
        if (content.getTypeEnum() == ContentTypeEnum.CUSTOM) {
            return;
        }
        User mentioningUser = event.getMentioningUser();
        String userName = event.getMentionedUserProfile().getUsername();
        ConfluenceUser user = this.userManager.getUserByName(userName);
        String authorFullName = mentioningUser != null ? mentioningUser.getFullName() : "Anonymous";
        com.atlassian.mywork.model.Notification notification = (com.atlassian.mywork.model.Notification)this.notificationService.createOrUpdate(userName, this.fieldHelper.buildNotification(content).user(userName).iconUrl(this.fieldHelper.createGravatarUrl(mentioningUser)).title(authorFullName + " mentioned you in " + FieldHelper.getContentTypeDescription(content) + ".").description(event.getMentionHtml()).action(ACTION_CONFLUENCE_USER_MENTION).metadata(this.createFullMetadata(user, mentioningUser, content)).createNotification()).get();
        this.eventPublisher.publish((Object)new PushNotificationEvent(Collections.singletonList(notification)));
        log.debug("Created notification for user {}.", (Object)userName);
    }

    @EventListener
    public void onShareContentEvent(ShareContentEvent event) {
        this.onShareEvent((ShareEvent)event, ShareType.SHARE_CONTENT, ACTION_CONFLUENCE_SHARE);
    }

    @EventListener
    public void onShareDraftEvent(ShareDraftEvent event) {
        this.onShareEvent((ShareEvent)event, ShareType.SHARE_DRAFT, ACTION_CONFLUENCE_INVITE);
    }

    private Set<ConfluenceUser> getUsersFromKeys(Set<String> userKeys) {
        return userKeys.stream().map(userKey -> {
            ConfluenceUser user = this.userManager.getUserByKey(new UserKey(userKey));
            if (user == null) {
                user = this.userManager.getUserByName(userKey);
            }
            return user;
        }).collect(Collectors.toSet());
    }

    @EventListener
    public void onResolveInlineComment(InlineCommentResolveEvent event) {
        Comment comment = event.getComment();
        String actionType = comment.getStatus().isReopened() ? ACTION_CONFLUENCE_COMMENT_REOPEN : ACTION_CONFLUENCE_COMMENT_RESOLVE;
        String actionDescription = " " + (comment.getStatus().isReopened() ? "reopened" : "resolved") + " an inline comment on ";
        this.onCommentEvent(comment, actionType, actionDescription);
    }

    @EventListener
    public void onCommentCreatedEvent(CommentCreateEvent event) {
        Comment comment = event.getComment();
        this.onCommentEvent(comment, ACTION_CONFLUENCE_COMMENT, " commented on ");
    }

    private void onCommentEvent(Comment comment, String actionType, String actionDescription) {
        ContentEntityObject content = comment.getContainer();
        if (!(content instanceof AbstractPage)) {
            return;
        }
        AbstractPage page = (AbstractPage)content;
        List<ConfluenceUser> receivers = this.getRecipients(page, comment);
        ConfluenceUser parent = this.getParentCommentUser(comment);
        boolean isParentWatching = receivers.contains(parent);
        if (parent != null && !isParentWatching) {
            receivers.add(parent);
        }
        if (receivers.isEmpty()) {
            return;
        }
        PageContext renderContext = PageContext.contextWithTimeout((ContentEntityObject)content, (Timeout)Timeout.getMillisTimeout((long)1L, (TimeUnit)TimeUnit.MINUTES));
        renderContext.setOutputType(ConversionContextOutputType.EMAIL.value());
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)renderContext);
        context.setProperty("renderForWorkBox", (Object)true);
        Option<ConfluenceUser> maybeFromUser = this.fieldHelper.getUser();
        User fromUser = (User)maybeFromUser.getOrNull();
        String fullName = fromUser != null ? fromUser.getFullName() : "Anonymous";
        String contentTypeDescription = FieldHelper.getContentTypeDescription(content);
        String title = fullName + actionDescription + contentTypeDescription + ".";
        String url = comment.getUrlPath();
        String commentBody = this.renderer.render((ContentEntityObject)comment, (ConversionContext)context);
        String highlightText = comment.getProperties().getStringProperty("inline-original-selection");
        ArrayList notifications = new ArrayList();
        receivers.stream().filter(user -> this.permissionManager.hasPermissionNoExemptions((User)user, Permission.VIEW, (Object)content)).forEach(receiver -> {
            boolean isParent;
            ObjectNode metadata = this.createMetadata(maybeFromUser, this.getPageId(content), content.getId(), comment.getId(), comment.getVersion());
            metadata.put("likeComment", false);
            if (highlightText != null && !"".equals(highlightText.trim())) {
                metadata.put("highlightText", highlightText);
            }
            metadata.put("watching", !(isParent = receiver.equals(parent)) || isParentWatching);
            metadata.put(ACTION_CONFLUENCE_LIKE, this.hasLike((ConfluenceUser)receiver, (ContentEntityObject)page));
            metadata.put("replyYourComment", isParent);
            if (receiver.equals(page.getCreator())) {
                metadata.put("commentOnYourPage", true);
            }
            String watchingUserName = receiver.getName();
            Future notificationFuture = this.notificationService.createOrUpdate(watchingUserName, this.fieldHelper.buildNotification(content).user(watchingUserName).title(title).url(url).action(actionType).description(commentBody).read(receiver.equals(fromUser)).metadata(metadata).createNotification());
            this.addNotification(notifications, notificationFuture);
        });
        if (ACTION_CONFLUENCE_COMMENT.equals(actionType)) {
            this.eventPublisher.publish((Object)new PushNotificationEvent(notifications));
        }
    }

    private List<ConfluenceUser> getRecipients(AbstractPage page, Comment comment) {
        List watches = this.notificationManager.getNotificationsByContent((ContentEntityObject)page);
        Set mentionUsers = this.mentionFinder.getMentionedUsernames(comment.getBodyContent());
        return watches.stream().map(Notification::getReceiver).filter(Objects::nonNull).filter(target -> !mentionUsers.contains(target.getName()) && !target.equals(comment.getCreator())).collect(Collectors.toList());
    }

    private ConfluenceUser getParentCommentUser(Comment comment) {
        ConfluenceUser creator;
        Comment parent = comment.getParent();
        if (parent != null && (creator = parent.getCreator()) != null && !creator.equals(comment.getCreator())) {
            return creator;
        }
        return null;
    }

    private long getPageId(ContentEntityObject content) {
        if (content instanceof AbstractPage) {
            AbstractPage page = (AbstractPage)content;
            return page.getId();
        }
        if (content instanceof Comment) {
            Comment comment = (Comment)content;
            return this.getPageId(comment.getContainer());
        }
        return 0L;
    }

    @EventListener
    public void onLike(LikeCreatedEvent event) {
        ContentEntityObject ceo = event.getContent();
        if (ceo instanceof AbstractPage || ceo instanceof Comment) {
            this.like((LikeEvent)event);
            this.likeUpdate((LikeEvent)event, true);
        }
    }

    @EventListener
    public void onUnlike(LikeRemovedEvent event) {
        ContentEntityObject ceo = event.getContent();
        if (ceo instanceof AbstractPage || ceo instanceof Comment) {
            this.likeUpdate((LikeEvent)event, false);
        }
    }

    private void like(final LikeEvent event) {
        this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Object>(){

            public Object doInTransaction() {
                ConfluenceEventListener.this.likeInTransaction(event);
                return null;
            }
        });
    }

    private void likeInTransaction(LikeEvent event) {
        ContentEntityObject content = this.contentEntityManager.getById(event.getContent().getId());
        if (content.getTypeEnum() == ContentTypeEnum.CUSTOM) {
            return;
        }
        ConfluenceUser notifiedUser = content.getCreator();
        if (notifiedUser == null) {
            return;
        }
        User likingUser = event.getOriginatingUser();
        if (notifiedUser.equals(likingUser)) {
            return;
        }
        String username = notifiedUser.getName();
        Future notification = this.notificationService.createOrUpdate(username, this.fieldHelper.buildNotification(content, likingUser).user(username).title(likingUser.getFullName() + " liked " + FieldHelper.getContentTypeDescription(content) + ".").action(ACTION_CONFLUENCE_LIKE).metadata(this.createFullMetadata(notifiedUser, likingUser, content)).createNotification());
        ArrayList<com.atlassian.mywork.model.Notification> notifications = new ArrayList<com.atlassian.mywork.model.Notification>();
        this.addNotification(notifications, notification);
        this.eventPublisher.publish((Object)new PushNotificationEvent(notifications));
    }

    private void likeUpdate(LikeEvent event, boolean like) {
        ObjectNode condition = OBJECT_MAPPER.createObjectNode();
        String type = ACTION_CONFLUENCE_LIKE;
        ContentEntityObject content = event.getContent();
        if (content.getTypeEnum() == ContentTypeEnum.CUSTOM) {
            return;
        }
        if (content instanceof Comment) {
            condition.put(METADATA_KEY_CONTENT_ID, content.getId());
            type = "likeComment";
        }
        String globalId = this.fieldHelper.createGlobalId("page", this.getPageId(content));
        this.updateMetadata((ConfluenceUser)event.getOriginatingUser(), globalId, type, like, condition);
    }

    @EventListener
    public void onWatchEvent(ContentNotificationAddedEvent event) {
        this.watchEvent((ContentNotificationEvent)event, true);
    }

    @EventListener
    public void onUnwatchEvent(ContentNotificationRemovedEvent event) {
        this.watchEvent((ContentNotificationEvent)event, false);
    }

    private void watchEvent(ContentNotificationEvent event, boolean watch) {
        ContentEntityObject content = event.getNotification().getContent();
        String globalId = this.fieldHelper.createGlobalId(FieldHelper.getContentType(content), content.getId());
        this.updateMetadata(event.getNotification().getReceiver(), globalId, "watching", watch, OBJECT_MAPPER.createObjectNode());
    }

    private void updateMetadata(ConfluenceUser user, String globalId, String field, boolean status, ObjectNode condition) {
        ObjectNode metadata = OBJECT_MAPPER.createObjectNode();
        metadata.put(field, status);
        this.notificationService.updateMetadata(user.getName(), globalId, condition, metadata);
    }

    @EventListener
    public void onEmailReadEvent(EmailReadEvent event) {
        String action = event.getAction();
        if (!EMAIL_READ_ACTIONS.contains(action)) {
            return;
        }
        ContentEntityObject content = event.getContent();
        if (content == null) {
            log.warn("Can't process read email with action {} but no content", (Object)action);
            return;
        }
        ConfluenceUser actor = event.getActor();
        ObjectNode condition = OBJECT_MAPPER.createObjectNode();
        condition.put(METADATA_KEY_USERNAME, actor.getName());
        if (EMAIL_MENTION_ACTION.equals(action)) {
            action = ACTION_CONFLUENCE_USER_MENTION;
        } else if (EMAIL_CREATE_ACTION.equals(action) && content instanceof Comment) {
            Comment comment = (Comment)content;
            content = comment.getContainer();
            action = ACTION_CONFLUENCE_COMMENT;
            condition.put(METADATA_KEY_CONTENT_ID, comment.getId());
        }
        String globalId = this.fieldHelper.createGlobalId(FieldHelper.getContentType(content), content.getId());
        ConfluenceUser recipient = event.getRecipient();
        this.notificationService.setRead(recipient.getKey(), globalId, action, condition);
    }

    private ObjectNode createFullMetadata(ConfluenceUser notifiedUser, User actor, ContentEntityObject content) {
        return (ObjectNode)this.createMetadata(actor, this.getPageId(content), content.getId(), content.getId(), content.getVersion()).putAll(this.createActionMetadata(notifiedUser, content));
    }

    private ObjectNode createMetadata(Option<ConfluenceUser> actor, long pageId, long itemContentId, long contentId, int contentVersion) {
        return this.createMetadata((User)actor.getOrNull(), pageId, itemContentId, contentId, contentVersion);
    }

    private ObjectNode createMetadata(User actor, long pageId, long itemContentId, long contentId, int contentVersion) {
        String user = actor != null ? actor.getName() : "anonymous";
        String fullName = actor != null ? actor.getFullName() : "Anonymous";
        ObjectNode metadata = JsonNodeFactory.instance.objectNode();
        metadata.put("user", fullName);
        metadata.put(METADATA_KEY_USERNAME, user);
        metadata.put("pageId", pageId);
        metadata.put("itemContentId", itemContentId);
        metadata.put(METADATA_KEY_CONTENT_ID, contentId);
        metadata.put("contentVersion", contentVersion);
        return metadata;
    }

    private ObjectNode createActionMetadata(ConfluenceUser notifiedUser, ContentEntityObject content) {
        ObjectNode metadata = JsonNodeFactory.instance.objectNode();
        AbstractPage page = ConfluenceEventListener.getPage(content);
        if (content instanceof Comment) {
            metadata.put("likeComment", this.hasLike(notifiedUser, content));
        }
        metadata.put("watching", this.notificationManager.isUserWatchingPageOrSpace((User)notifiedUser, null, page));
        metadata.put(ACTION_CONFLUENCE_LIKE, this.hasLike(notifiedUser, (ContentEntityObject)page));
        return metadata;
    }

    private boolean hasLike(ConfluenceUser user, ContentEntityObject page) {
        return this.likeManager.hasLike(page, (User)user);
    }

    private static AbstractPage getPage(ContentEntityObject content) {
        AbstractPage page = null;
        if (content instanceof Contained) {
            page = ConfluenceEventListener.getPage(((Contained)content).getContainer());
        } else if (content instanceof AbstractPage) {
            page = (AbstractPage)content;
        }
        return page;
    }

    private void onShareEvent(ShareEvent event, ShareType shareType, String action) {
        Object title;
        ContentEntityObject content = this.contentEntityManager.getById(event.getEntityId().longValue());
        if (content.getTypeEnum() == ContentTypeEnum.CUSTOM) {
            return;
        }
        User fromUser = (User)this.fieldHelper.getUser().get();
        String contentTypeDescription = FieldHelper.getContentTypeDescription(content);
        switch (shareType) {
            case SHARE_DRAFT: {
                title = contentTypeDescription;
                break;
            }
            default: {
                title = fromUser.getFullName() + " shared " + contentTypeDescription + " with you.";
            }
        }
        String note = StringEscapeUtils.escapeHtml4((String)event.getNote());
        Set userKeys = event.getUsers();
        Set<ConfluenceUser> users = this.getUsersFromKeys(userKeys);
        ArrayList<com.atlassian.mywork.model.Notification> notifications = new ArrayList<com.atlassian.mywork.model.Notification>();
        for (ConfluenceUser user : users) {
            Set others = Sets.filter(users, (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)user)));
            ArrayNode otherUsers = JsonNodeFactory.instance.arrayNode();
            for (ConfluenceUser otherUser : others) {
                ObjectNode otherJson = JsonNodeFactory.instance.objectNode();
                otherJson.put(METADATA_KEY_USERNAME, otherUser.getName());
                otherJson.put("avatarUrl", this.fieldHelper.createGravatarUrl((User)otherUser));
                otherJson.put("fullName", otherUser.getFullName());
                otherUsers.add((JsonNode)otherJson);
            }
            ObjectNode metadata = this.createFullMetadata(user, fromUser, content);
            metadata.put("otherUsers", (JsonNode)otherUsers);
            NotificationBuilder notificationBuilder = this.fieldHelper.buildNotification(content).user(user.getName()).title((String)title).action(action).description(note).metadata(metadata);
            if (shareType.equals((Object)ShareType.SHARE_DRAFT)) {
                String url = content instanceof AbstractPage ? ((AbstractPage)content).getEditUrlPath() : content.getUrlPath();
                notificationBuilder.itemUrl(url);
                notificationBuilder.url(url);
            }
            com.atlassian.mywork.model.Notification notification = notificationBuilder.createNotification();
            this.addNotification(notifications, this.notificationService.createOrUpdate(user.getName(), notification));
            log.debug("Created notification for user {}.", (Object)user);
        }
        if (shareType == ShareType.SHARE_CONTENT) {
            this.eventPublisher.publish((Object)new PushNotificationEvent(notifications));
        }
    }

    private void addNotification(List<com.atlassian.mywork.model.Notification> notifications, Future<com.atlassian.mywork.model.Notification> notificationFuture) {
        try {
            notifications.add(notificationFuture.get());
        }
        catch (Exception e) {
            log.error("Error creating notification", (Throwable)e);
        }
    }

    private static enum ShareType {
        SHARE_CONTENT,
        SHARE_DRAFT;

    }
}

