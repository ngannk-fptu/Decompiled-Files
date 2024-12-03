/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.notifications.NotificationContentCacheKey
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.notifications.content.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.notifications.NotificationContentCacheKey;
import com.atlassian.confluence.notifications.content.BodyContentRenderedEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import java.util.Optional;
import javax.annotation.Nonnull;

@Internal
public class ContentCacheLoader {
    private final ContentService contentService;
    private final EventPublisher eventPublisher;

    public ContentCacheLoader(ContentService contentService, EventPublisher eventPublisher) {
        this.contentService = contentService;
        this.eventPublisher = eventPublisher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public Option<Content> load(@Nonnull NotificationContentCacheKey cacheKey) {
        Option contents = Option.none();
        try {
            Option option = contents = this.contentService.find(cacheKey.getExpansions()).withId(cacheKey.getContentId()).fetchOne();
            return option;
        }
        finally {
            if (contents.isDefined()) {
                this.eventPublisher.publish((Object)new BodyContentRenderedEvent(cacheKey));
            }
        }
    }

    public Optional<Content> loadCacheEntry(@Nonnull NotificationContentCacheKey cacheKey) {
        return Optional.ofNullable((Content)this.load(cacheKey).getOrNull());
    }
}

