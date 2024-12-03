/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.mentions.notifications;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.mentions.notifications.MentionContentPayload;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MentionRenderContextFactory
extends RenderContextProviderTemplate<MentionContentPayload> {
    private static final BandanaContext BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT = new ConfluenceBandanaContext("email-gateway-configuration");
    private static final String ALLOW_TO_CREATE_COMMENT_BY_EMAIL_KEY = "com.atlassian.confluence.plugins.emailgateway.allow.create.comment";
    private final ContentEntityManager contentEntityManager;
    private final ContentUiSupport<ContentEntityObject> contentUiSupport;
    private final UserAccessor userAccessor;
    private final TransactionTemplate transactionTemplate;
    private final PermissionManager permissionManager;
    private final NotificationUserService notificationUserService;
    private final BandanaManager bandanaManager;

    public MentionRenderContextFactory(ContentEntityManager contentEntityManager, ContentUiSupport<ContentEntityObject> contentUiSupport, UserAccessor userAccessor, TransactionTemplate transactionTemplate, PermissionManager permissionManager, NotificationUserService notificationUserService, BandanaManager bandanaManager) {
        this.contentEntityManager = contentEntityManager;
        this.contentUiSupport = contentUiSupport;
        this.userAccessor = userAccessor;
        this.transactionTemplate = transactionTemplate;
        this.permissionManager = permissionManager;
        this.notificationUserService = notificationUserService;
        this.bandanaManager = bandanaManager;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<MentionContentPayload> mentionContentPayloadNotification, ServerConfiguration configuration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        Optional<Pair<Content, String>> maybeContentForId;
        if (roleRecipient.isEmpty() || ((Either)roleRecipient.get()).isLeft()) {
            return MaybeNot.becauseOf((String)"This factory exposes content, thus recipient has to be provided in order to perform a VIEW permission check.", (Object[])new Object[0]);
        }
        RoleRecipient recipient = (RoleRecipient)((Either)roleRecipient.get()).right().get();
        ConfluenceUser recipientUser = this.userAccessor.getUserByKey(recipient.getUserKey());
        HashMap<String, Object> renderContext = new HashMap<String, Object>();
        MentionContentPayload payload = (MentionContentPayload)mentionContentPayloadNotification.getPayload();
        if (payload.getMentionHtml().isDefined()) {
            renderContext.put("contentHtml", payload.getMentionHtml().get());
        }
        if (!(maybeContentForId = this.getContentDetailsForId(payload.getContentId(), recipient.getUserKey())).isPresent()) {
            return MaybeNot.becauseOf((String)("Unable to load content with id " + payload.getContentId() + " may not permissionto view the content"), (Object[])new Object[0]);
        }
        Pair<Content, String> contentForId = maybeContentForId.get();
        Content content = (Content)contentForId.left();
        String contentType = (String)contentForId.right();
        renderContext.put("content", content);
        renderContext.put("contentType", contentType);
        if (ContentType.COMMENT.equals((Object)content.getType())) {
            renderContext.put("messageId", String.valueOf(((Content)content.getContainer()).getId().asLong()));
            boolean replyByEmailEnabled = (Boolean)Optional.ofNullable(this.bandanaManager.getValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, ALLOW_TO_CREATE_COMMENT_BY_EMAIL_KEY)).orElse(false);
            renderContext.put("replyByEmailEnabled", replyByEmailEnabled);
        } else {
            renderContext.put("messageId", String.valueOf(content.getId().asLong()));
        }
        renderContext.put("modifier", this.notificationUserService.findUserForKey((User)recipientUser, payload.getAuthorUserKey()));
        return Option.some(renderContext);
    }

    @Deprecated
    private Optional<Pair<Content, String>> getContentDetailsForId(long id, UserKey recipient) {
        return (Optional)this.transactionTemplate.execute(() -> {
            ContentEntityObject content = Objects.requireNonNull(this.contentEntityManager.getById(id));
            ConfluenceUser user = this.userAccessor.getUserByKey(recipient);
            if (!this.permissionManager.hasPermissionNoExemptions((User)user, Permission.VIEW, (Object)content)) {
                return Optional.empty();
            }
            ContentId contentId = content.getContentId();
            Content.ContentBuilder contentBuilder = Content.builder().id(contentId).title(content.getDisplayTitle()).addLink(LinkType.WEB_UI, content.getUrlPath()).type(ContentType.valueOf((String)content.getType()));
            if (content instanceof Comment) {
                ContentId containerId = ((Comment)content).getContainer().getContentId();
                contentBuilder.container((Container)Content.builder().id(containerId).build());
            }
            return Optional.of(Pair.pair((Object)contentBuilder.build(), (Object)this.contentUiSupport.getContentTypeI18NKey((ConfluenceEntityObject)content)));
        });
    }
}

