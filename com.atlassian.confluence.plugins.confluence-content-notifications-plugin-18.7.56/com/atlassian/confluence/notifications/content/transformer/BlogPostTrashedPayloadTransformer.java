/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.types.UserDriven
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.notifications.content.SimpleContentIdPayload;
import com.atlassian.confluence.notifications.content.TransformerUtils;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class BlogPostTrashedPayloadTransformer
extends PayloadTransformerTemplate<BlogPostTrashedEvent, ContentIdPayload> {
    protected Maybe<ContentIdPayload> checkedCreate(BlogPostTrashedEvent blogPostEvent) {
        if (blogPostEvent.isSuppressNotifications()) {
            return Option.none();
        }
        String lastModifierKey = (String)TransformerUtils.getOriginatingUserForUserDriven((UserDriven)blogPostEvent).getOrNull();
        SimpleContentIdPayload value = new SimpleContentIdPayload(ContentType.BLOG_POST, blogPostEvent.getBlogPost().getId(), lastModifierKey);
        return Option.some((Object)value);
    }
}

