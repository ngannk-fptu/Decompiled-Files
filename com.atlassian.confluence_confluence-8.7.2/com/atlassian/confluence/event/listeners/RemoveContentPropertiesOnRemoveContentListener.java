/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.user.PersonalInformationRemoveEvent;
import com.atlassian.event.api.EventListener;

public class RemoveContentPropertiesOnRemoveContentListener {
    private final ContentPropertyManager contentPropertyManager;

    public RemoveContentPropertiesOnRemoveContentListener(ContentPropertyManager contentPropertyManager) {
        this.contentPropertyManager = contentPropertyManager;
    }

    @EventListener
    public void handlePageRemove(PageRemoveEvent event) {
        this.handleEvent(event);
    }

    @EventListener
    public void handleBlogPostRemove(BlogPostRemoveEvent event) {
        this.handleEvent(event);
    }

    @EventListener
    public void handleCommentRemove(CommentRemoveEvent event) {
        this.handleEvent(event);
    }

    @EventListener
    public void handlePersonalInformationRemove(PersonalInformationRemoveEvent event) {
        this.handleEvent(event);
    }

    private void handleEvent(ContentEvent event) {
        ContentEntityObject entity = event.getContent();
        this.contentPropertyManager.removeProperties(entity);
    }
}

