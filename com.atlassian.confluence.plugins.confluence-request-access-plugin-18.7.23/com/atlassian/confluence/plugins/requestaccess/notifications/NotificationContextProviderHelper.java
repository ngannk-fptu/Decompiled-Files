/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.requestaccess.notifications;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.requestaccess.notifications.DefaultAccessNotificationPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import javax.annotation.Nullable;

class NotificationContextProviderHelper {
    private final ContentEntityManager contentEntityManager;

    NotificationContextProviderHelper(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    String getAddPageRestrictionActionUrlPath(Content content, ConfluenceUser requestAccessUser, String accessType) {
        return new UrlBuilder(((Link)content.getLinks().get(LinkType.WEB_UI)).getPath()).add("username", requestAccessUser.getName()).add("userFullName", requestAccessUser.getFullName()).add("accessType", accessType).add("grantAccess", true).toString();
    }

    String getAddDraftRestrictionActionUrlPath(Content content, ConfluenceUser requestAccessUser, String accessType) {
        return new UrlBuilder(((Link)content.getLinks().get(LinkType.EDIT_UI)).getPath()).add("username", requestAccessUser.getName()).add("userFullName", requestAccessUser.getFullName()).add("accessType", accessType).add("grantAccess", true).toString();
    }

    boolean canFindRecipient(Maybe<Either<NotificationAddress, RoleRecipient>> recipientData) {
        return !recipientData.isEmpty() && !((Either)recipientData.get()).isLeft();
    }

    @Nullable
    Content getContent(DefaultAccessNotificationPayload payload) {
        ContentEntityObject content = this.contentEntityManager.getById(payload.getContentId());
        if (content == null) {
            return null;
        }
        Content.ContentBuilder contentBuilder = Content.builder().id(content.getContentId()).title(content.getDisplayTitle()).type(ContentType.valueOf((String)content.getType())).status(content.getContentStatusObject()).addLink(LinkType.WEB_UI, content.getUrlPath());
        if (content instanceof AbstractPage) {
            contentBuilder.addLink(LinkType.EDIT_UI, ((AbstractPage)content).getEditUrlPath());
        }
        return contentBuilder.build();
    }
}

