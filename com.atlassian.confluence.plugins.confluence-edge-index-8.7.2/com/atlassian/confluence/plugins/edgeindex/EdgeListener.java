/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.ContentEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentEvent
 *  com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentVersionRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.like.AsyncLikeEvent
 *  com.atlassian.confluence.event.events.like.AsyncLikeRemovedEvent
 *  com.atlassian.confluence.event.events.permission.ContentTreePermissionReindexEvent
 *  com.atlassian.confluence.event.events.types.Removed
 *  com.atlassian.confluence.event.events.types.Trashed
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentVersionRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.like.AsyncLikeEvent;
import com.atlassian.confluence.event.events.like.AsyncLikeRemovedEvent;
import com.atlassian.confluence.event.events.permission.ContentTreePermissionReindexEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.plugins.edgeindex.EdgeFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexManager;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EdgeListener {
    private final EdgeIndexManager edgeIndexManager;
    private final EdgeFactory edgeFactory;
    private final ContentEntityManager contentEntityManager;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;

    @Autowired
    public EdgeListener(EdgeIndexManager edgeIndexManager, EdgeFactory edgeFactory, ContentEntityManager contentEntityManager, TransactionTemplate transactionTemplate, EventPublisher eventPublisher) {
        this.edgeIndexManager = edgeIndexManager;
        this.edgeFactory = edgeFactory;
        this.contentEntityManager = contentEntityManager;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onLikeEvent(AsyncLikeEvent likeEvent) {
        this.transactionTemplate.execute(() -> {
            Edge likeEdge = this.edgeFactory.getLikeEdge(FindUserHelper.getUser((User)likeEvent.getOriginatingUser()), this.contentEntityManager.getById(likeEvent.getContentId()), new Date(likeEvent.getTimestamp()));
            if (likeEvent instanceof AsyncLikeRemovedEvent) {
                this.edgeIndexManager.unIndex(likeEdge);
            } else {
                this.edgeIndexManager.index(likeEdge);
            }
            return null;
        });
    }

    @EventListener
    public void onCommentCreateEvent(CommentCreateEvent commentEvent) {
        if (commentEvent.getComment() == null) {
            return;
        }
        this.edgeIndexManager.index(this.edgeFactory.getCreateEdge(commentEvent.getContent()));
    }

    @EventListener
    public void onPageCreateEvent(PageCreateEvent pageCreateEvent) {
        this.edgeIndexManager.index(this.edgeFactory.getCreateEdge(pageCreateEvent.getContent()));
    }

    @EventListener
    public void onBlogPostCreateEvent(BlogPostCreateEvent blogPostCreateEvent) {
        this.edgeIndexManager.index(this.edgeFactory.getCreateEdge(blogPostCreateEvent.getContent()));
    }

    @EventListener
    public void onContentRemoveEvent(Removed removedEvent) {
        if (!(removedEvent instanceof ContentEvent)) {
            return;
        }
        this.onContentEntityRemoved((ContentEvent)removedEvent);
    }

    @EventListener
    public void onContentTrashed(Trashed trashedEvent) {
        if (!(trashedEvent instanceof ContentEvent)) {
            return;
        }
        this.onContentEntityRemoved((ContentEvent)trashedEvent);
    }

    @EventListener
    public void onContentPermissionReindex(ContentTreePermissionReindexEvent e) {
        this.edgeIndexManager.reIndexPermissions(e.getContent());
    }

    private void onContentEntityRemoved(ContentEvent event) {
        ContentEntityObject content;
        Object object = content = event instanceof AttachmentEvent ? ((AttachmentEvent)event).getAttachment() : event.getContent();
        if (content == null) {
            return;
        }
        if (event instanceof GeneralAttachmentVersionRemoveEvent) {
            this.edgeIndexManager.contentEntityVersionRemoved(content);
        } else {
            this.edgeIndexManager.contentEntityRemoved(content);
        }
    }
}

