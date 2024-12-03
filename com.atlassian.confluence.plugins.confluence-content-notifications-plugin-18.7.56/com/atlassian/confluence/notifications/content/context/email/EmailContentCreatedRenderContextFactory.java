/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.user.UserAccessor
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.notifications.content.context.email;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.context.AbstractContentCreatedRenderContextFactory;
import com.atlassian.confluence.user.UserAccessor;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class EmailContentCreatedRenderContextFactory
extends AbstractContentCreatedRenderContextFactory {
    private final CachedContentFinder cachedContentFinder;

    public EmailContentCreatedRenderContextFactory(CachedContentFinder cachedContentFinder, UserAccessor userAccessor, LocaleManager localeManager, NotificationUserService notificationUserService) {
        super(cachedContentFinder, userAccessor, localeManager, notificationUserService);
        this.cachedContentFinder = cachedContentFinder;
    }

    @Override
    protected Expansion[] getMediumSpecificExpansions() {
        return new Expansion[]{CommonContentExpansions.SPACE, this.cachedContentFinder.exportBody()};
    }

    @Override
    protected Map<String, Object> getMediumSpecificContext(Content content) {
        return ImmutableMap.of((Object)"contentHtml", (Object)((ContentBody)content.getBody().get(this.cachedContentFinder.exportRepresentation())).getValue());
    }
}

