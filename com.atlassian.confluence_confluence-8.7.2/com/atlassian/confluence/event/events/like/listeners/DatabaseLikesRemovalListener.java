/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.event.events.like.listeners;

import com.atlassian.confluence.content.event.PluginContentRemovedEvent;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentVersionRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Qualifier;

public class DatabaseLikesRemovalListener {
    private final LikeManager likeManager;
    private final ContentEntityManager contentEntityManager;
    private final AttachmentManager attachmentManager;
    private final ImmutableSet<Class<?>> relevantEvents;

    public DatabaseLikesRemovalListener(@Qualifier(value="likeManager") LikeManager likeManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @Qualifier(value="attachmentManager") AttachmentManager attachmentManager) {
        this.likeManager = likeManager;
        this.contentEntityManager = contentEntityManager;
        this.attachmentManager = attachmentManager;
        ImmutableSet.Builder builder = ImmutableSet.builder();
        builder.add(BlogPostRemoveEvent.class).add(PageRemoveEvent.class).add(CommentRemoveEvent.class).add(PluginContentRemovedEvent.class);
        this.relevantEvents = builder.build();
    }

    @EventListener
    public void onRemoveEvent(Removed contentRemoveEvent) {
        if (this.relevantEvents.contains(contentRemoveEvent.getClass()) && ((ContentEvent)((Object)contentRemoveEvent)).getContent() != null) {
            this.removeLikesOnContent(((ContentEvent)((Object)contentRemoveEvent)).getContent());
        }
    }

    @EventListener
    public void onAttachmentRemoveEvent(AttachmentRemoveEvent attachmentRemoveEvent) {
        Attachment removedAttachment = attachmentRemoveEvent.getAttachment();
        if (removedAttachment != null) {
            for (Attachment attachment : this.attachmentManager.getAllVersions(removedAttachment)) {
                this.removeLikesOnContent(attachment);
            }
        }
    }

    @EventListener
    public void onAttachmentVersionRemoveEvent(AttachmentVersionRemoveEvent attachmentVersionRemoveEvent) {
        this.removeLikesOnContent(attachmentVersionRemoveEvent.getAttachment());
    }

    @EventListener
    public void onUserRemoveEvent(UserRemoveEvent userRemoveEvent) {
        ConfluenceUser user = this.tryCast(userRemoveEvent.getUser());
        if (user != null) {
            this.likeManager.removeAllLikesFor(user.getKey());
        }
    }

    private ConfluenceUser tryCast(User user) {
        return user instanceof ConfluenceUser ? (ConfluenceUser)user : null;
    }

    private void removeLikesOnContent(ContentEntityObject content) {
        this.likeManager.removeAllLikesOn(content);
        for (Comment comment : content.getComments()) {
            this.likeManager.removeAllLikesOn(comment);
        }
    }
}

