/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.event.events.content.Contented
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.notifications.content.ContentEditedPayloadTransformer;
import com.atlassian.confluence.notifications.content.TransformerUtils;

public class BlogPostEditedPayloadTransformer
extends ContentEditedPayloadTransformer<BlogPostUpdateEvent> {
    @Override
    protected long getSourceId(BlogPostUpdateEvent event) {
        return event.getBlogPost().getId();
    }

    @Override
    protected long getOriginalId(BlogPostUpdateEvent event) {
        return event.getOriginalBlogPost().getId();
    }

    @Override
    protected boolean isNotificationSuppressed(BlogPostUpdateEvent blogPostUpdateEvent) {
        return blogPostUpdateEvent.isSuppressNotifications();
    }

    @Override
    protected String getOriginatingUserKey(BlogPostUpdateEvent blogPostUpdateEvent) {
        return (String)TransformerUtils.getOriginatingUserForContented((Contented)blogPostUpdateEvent).getOrNull();
    }

    @Override
    protected ContentType getContentType(BlogPostUpdateEvent blogPostUpdateEvent) {
        return ContentType.BLOG_POST;
    }
}

