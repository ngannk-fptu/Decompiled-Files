/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.links.LinkManager
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.plugins.files.api.FileComment
 *  com.atlassian.confluence.plugins.files.api.services.FileCommentService
 *  com.atlassian.confluence.plugins.files.entities.FileCommentInput
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.AttachmentMetadataService;
import com.atlassian.confluence.plugin.copyspace.service.CommentService;
import com.atlassian.confluence.plugin.copyspace.service.LinksUpdater;
import com.atlassian.confluence.plugin.copyspace.service.SidebarLinkCopier;
import com.atlassian.confluence.plugin.copyspace.util.Constants;
import com.atlassian.confluence.plugin.copyspace.util.MetadataCopier;
import com.atlassian.confluence.plugins.files.api.FileComment;
import com.atlassian.confluence.plugins.files.api.services.FileCommentService;
import com.atlassian.confluence.plugins.files.entities.FileCommentInput;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="commentServiceImpl")
public class CommentServiceImpl
implements CommentService {
    private final SidebarLinkCopier sidebarLinkCopier;
    private final CommentManager commentManager;
    private final FileCommentService fileCommentService;
    private final LinkManager linkManager;
    private final AttachmentMetadataService attachmentMetadataService;
    private final AttachmentManager attachmentManager;
    private final LinksUpdater linksUpdater;
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    public CommentServiceImpl(SidebarLinkCopier sidebarLinkCopier, @ComponentImport CommentManager commentManager, @ComponentImport FileCommentService fileCommentService, @ComponentImport LinkManager linkManager, AttachmentMetadataService attachmentMetadataService, @ComponentImport AttachmentManager attachmentManager, LinksUpdater linksUpdater) {
        this.sidebarLinkCopier = sidebarLinkCopier;
        this.commentManager = commentManager;
        this.fileCommentService = fileCommentService;
        this.linkManager = linkManager;
        this.attachmentMetadataService = attachmentMetadataService;
        this.attachmentManager = attachmentManager;
        this.linksUpdater = linksUpdater;
    }

    @Override
    public void copyComments(ContentEntityObject targetCeo, List<Comment> originalComments, CopySpaceContext context) {
        HashMap<Long, Comment> oldIdToCopiedComment = new HashMap<Long, Comment>();
        List orderedComments = originalComments.stream().sorted(Comparator.comparing(EntityObject::getCreationDate)).collect(Collectors.toList());
        for (Comment oldComment : orderedComments) {
            Comment newParent = null;
            if (oldComment.getParent() != null) {
                Long oldId = oldComment.getParent().getId();
                newParent = (Comment)oldIdToCopiedComment.get(oldId);
            }
            Comment comment = this.saveComment(targetCeo, newParent, oldComment, context);
            oldIdToCopiedComment.put(oldComment.getId(), comment);
            if (!context.isCopyAttachments()) continue;
            this.sidebarLinkCopier.checkAndCopyRewritableAttachmentSidebarLink(oldComment.getAttachments(), (ContentEntityObject)comment, context.getOriginalSpaceKey(), context.getTargetSpaceKey());
            this.attachmentMetadataService.preserveMetadata((ContentEntityObject)oldComment, (ContentEntityObject)comment);
        }
    }

    @Override
    public void copyFileComments(ContentEntityObject originalEntity, ContentEntityObject copiedEntity) {
        try {
            this.copyFileCommentForAttachment(originalEntity, copiedEntity);
        }
        catch (Exception e) {
            log.error("Cannot copy pinned comment from contentId: {} to contentId: {} ", (Object)originalEntity.getContentId(), (Object)copiedEntity.getContentId());
        }
    }

    private void copyFileCommentForAttachment(ContentEntityObject originalEntity, ContentEntityObject copiedEntity) {
        List copiedEntityAttachments = this.attachmentManager.getLatestVersionsOfAttachments(copiedEntity);
        this.attachmentManager.getLatestVersionsOfAttachments(originalEntity).forEach(originalAttachment -> {
            PageResponse comments = this.fileCommentService.getComments(originalAttachment.getId(), (PageRequest)new SimplePageRequest(0, 100));
            log.debug("Got: {} comment trees for attachment: {}", (Object)comments.size(), (Object)originalAttachment.getTitle());
            if (comments.size() > 0) {
                for (FileComment comment : comments.getResults()) {
                    this.copyFileCommentTree(comment, (Attachment)originalAttachment, copiedEntityAttachments);
                }
            }
        });
    }

    private void copyFileCommentTree(FileComment originalComment, Attachment originalAttachment, List<Attachment> copiedEntityAttachments) {
        FileComment commentWithChildren = this.fileCommentService.getCommentById(originalAttachment.getId(), originalComment.getId().asLong());
        copiedEntityAttachments.forEach(copiedAttachment -> {
            if (copiedAttachment.getTitle().equals(originalAttachment.getTitle())) {
                this.copyFileCommentRecursively((Attachment)copiedAttachment, commentWithChildren, 0L);
            }
        });
    }

    private void copyFileCommentRecursively(Attachment copiedAttachment, FileComment comment, long parentId) {
        FileCommentInput commentInput = new FileCommentInput(parentId, comment.getAnchor(), ((ContentBody)comment.getBody().get(ContentRepresentation.VIEW)).getValue(), Boolean.valueOf(comment.getResolved().getValue()));
        FileComment newComment = this.fileCommentService.createComment(copiedAttachment.getId(), commentInput);
        if (!comment.getChildren().isEmpty()) {
            log.debug("Copying {} children for comment: {}", (Object)comment.getChildren().size(), (Object)comment.getId().asLong());
            comment.getChildren().forEach(fileComment -> this.copyFileCommentRecursively(copiedAttachment, (FileComment)fileComment, newComment.getId().asLong()));
        }
    }

    private Comment saveComment(ContentEntityObject destination, Comment parent, Comment oldComment, CopySpaceContext context) {
        Comment comment = new Comment();
        if (oldComment.isInlineComment()) {
            comment.setContentPropertiesFrom((ContentEntityObject)oldComment);
        }
        comment.setBodyAsString(this.linksUpdater.rewriteLinks(oldComment.getBodyContent().getBody(), oldComment.getContainer(), context));
        destination.addComment(comment);
        if (parent != null) {
            parent.addChild(comment);
        }
        this.commentManager.saveContentEntity((ContentEntityObject)comment, Constants.SUPPRESS_EVENT_KEEP_LAST_MODIFIER);
        this.linkManager.updateOutgoingLinks((ContentEntityObject)comment);
        MetadataCopier.copyEntityMetadata((ConfluenceEntityObject)oldComment, (ConfluenceEntityObject)comment);
        return comment;
    }
}

