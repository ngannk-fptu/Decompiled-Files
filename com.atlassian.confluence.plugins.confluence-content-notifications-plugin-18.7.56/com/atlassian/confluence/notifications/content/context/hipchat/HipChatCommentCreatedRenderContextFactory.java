/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.notifications.content.context.hipchat;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.context.AbstractCommentCreatedRenderContextFactory;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class HipChatCommentCreatedRenderContextFactory
extends AbstractCommentCreatedRenderContextFactory {
    public HipChatCommentCreatedRenderContextFactory(CachedContentFinder cachedContentFinder, NotificationUserService notificationUserService, LocaleManager localeManager) {
        super(cachedContentFinder, notificationUserService, localeManager);
    }

    @Override
    public Expansion[] getMediumSpecificExpansions() {
        return new Expansion[]{CommonContentExpansions.SPACE, CommonContentExpansions.CONTAINER};
    }

    @Override
    public Maybe<Map<String, Object>> getMediumSpecificContext(Notification<CommentPayload> notification, ServerConfiguration serverConfiguration, User recipient, Content content) {
        return Option.some((Object)ImmutableMap.of());
    }
}

