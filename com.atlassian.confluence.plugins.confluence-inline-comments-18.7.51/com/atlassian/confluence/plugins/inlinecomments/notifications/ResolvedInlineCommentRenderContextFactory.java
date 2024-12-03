/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.content.CommentPayload
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.inlinecomments.notifications;

import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.plugins.inlinecomments.notifications.AbstractInlineCommentRenderContextFactory;
import com.atlassian.confluence.plugins.inlinecomments.utils.ResolveCommentConverter;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Qualifier;

public class ResolvedInlineCommentRenderContextFactory
extends AbstractInlineCommentRenderContextFactory {
    public ResolvedInlineCommentRenderContextFactory(CachedContentFinder cachedContentFinder, NotificationUserService notificationUserService, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, LocaleManager localeManager) {
        super(cachedContentFinder, notificationUserService, contentEntityManager, localeManager);
    }

    @Override
    @Nonnull
    protected Map<String, ?> enhanceNotificationContext(User user, String commentContentBody, Container container, Notification<CommentPayload> notification, ContentEntityObject entity) {
        HashMap<String, String> context = new HashMap<String, String>();
        String status = entity.getProperties().getStringProperty("status");
        if (status != null) {
            context.put("resolved", String.valueOf(ResolveCommentConverter.isResolved(status)));
            context.put("resolvedByDangling", String.valueOf(ResolveCommentConverter.isResolvedByDangling(status)));
        } else {
            context.put("resolved", entity.getProperties().getStringProperty("resolved"));
            context.put("resolvedByDangling", entity.getProperties().getStringProperty("resolved-by-dangling"));
        }
        return context;
    }
}

