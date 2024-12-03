/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.user.UserAccessor
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.notifications.content.context.hipchat;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.context.AbstractContentCreatedRenderContextFactory;
import com.atlassian.confluence.user.UserAccessor;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class HipChatContentCreatedRenderContextFactory
extends AbstractContentCreatedRenderContextFactory {
    public HipChatContentCreatedRenderContextFactory(CachedContentFinder cachedContentFinder, UserAccessor userAccessor, LocaleManager localeManager, NotificationUserService notificationUserService) {
        super(cachedContentFinder, userAccessor, localeManager, notificationUserService);
    }

    @Override
    protected Expansion[] getMediumSpecificExpansions() {
        return new Expansion[0];
    }

    @Override
    protected Map<String, Object> getMediumSpecificContext(Content content) {
        return ImmutableMap.of();
    }
}

