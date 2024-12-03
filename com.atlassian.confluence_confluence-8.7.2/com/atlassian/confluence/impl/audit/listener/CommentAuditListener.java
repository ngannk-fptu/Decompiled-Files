/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CommentAuditListener
extends AbstractAuditListener {
    public static final String COMMENT_CREATE_SUMMARY = AuditHelper.buildSummaryTextKey("comment.created");
    public static final String COMMENT_UPDATE_SUMMARY = AuditHelper.buildSummaryTextKey("comment.updated");
    public static final String COMMENT_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("comment.deleted");
    public static final String COMMENT_INLINE_UPDATE_SUMMARY = AuditHelper.buildSummaryTextKey("comment.inline.updated");
    public static final String COMMENT_INLINE_CREATE_SUMMARY = AuditHelper.buildSummaryTextKey("comment.inline.created");
    public static final String COMMENT_INLINE_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("comment.inline.deleted");

    public CommentAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void commentCreateEvent(CommentCreateEvent event) {
        if (event.getComment() == null) {
            return;
        }
        this.save(() -> {
            Comment comment = event.getComment();
            String CREATE_SUMMARY = comment.isInlineComment() ? COMMENT_INLINE_CREATE_SUMMARY : COMMENT_CREATE_SUMMARY;
            return AuditEvent.builder((AuditType)this.buildAuditType(CREATE_SUMMARY, CoverageLevel.FULL)).changedValues(this.buildChangedValues(null, comment)).affectedObjects(this.buildAuditResources(comment)).build();
        });
    }

    @EventListener
    public void commentUpdateEvent(CommentUpdateEvent event) {
        if (event.getComment() == null) {
            return;
        }
        this.save(() -> {
            Comment comment = event.getComment();
            String UPDATE_SUMMARY = comment.isInlineComment() ? COMMENT_INLINE_UPDATE_SUMMARY : COMMENT_UPDATE_SUMMARY;
            return AuditEvent.builder((AuditType)this.buildAuditType(UPDATE_SUMMARY, CoverageLevel.FULL)).changedValues(this.buildChangedValues(event.getOriginalComment(), comment)).affectedObjects(this.buildAuditResources(comment)).build();
        });
    }

    @EventListener
    public void commentRemoveEvent(CommentRemoveEvent event) {
        if (event.getComment() == null) {
            return;
        }
        this.save(() -> {
            Comment comment = event.getComment();
            String DELETE_SUMMARY = comment.isInlineComment() ? COMMENT_INLINE_DELETE_SUMMARY : COMMENT_DELETE_SUMMARY;
            return AuditEvent.builder((AuditType)this.buildAuditType(DELETE_SUMMARY, CoverageLevel.ADVANCED)).changedValues(this.buildChangedValues(comment, null)).affectedObjects(this.buildAuditResources(comment)).build();
        });
    }

    private AuditType buildAuditType(String summary, CoverageLevel level) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.END_USER_ACTIVITY, (CoverageLevel)level, (String)AuditCategories.PAGES, (String)summary).build();
    }

    private List<ChangedValue> buildChangedValues(@Nullable Comment oldComment, @Nullable Comment newComment) {
        return this.getAuditHandlerService().handle(Optional.ofNullable(oldComment), Optional.ofNullable(newComment));
    }

    private List<AuditResource> buildAuditResources(Comment comment) {
        ArrayList<AuditResource> affectedObjects = new ArrayList<AuditResource>();
        Space space = comment.getSpace();
        if (space != null) {
            affectedObjects.add(AuditResource.builder((String)space.getName(), (String)this.resourceTypes.space()).id(String.valueOf(space.getId())).build());
        }
        affectedObjects.add(AuditResource.builder((String)comment.getDisplayTitle(), (String)this.resourceTypes.comment()).id(comment.getIdAsString()).build());
        ContentEntityObject container = comment.getContainer();
        if (container != null) {
            affectedObjects.add(AuditResource.builder((String)container.getTitle(), (String)this.resourceTypes.page()).id(String.valueOf(container.getId())).build());
        }
        return affectedObjects;
    }
}

