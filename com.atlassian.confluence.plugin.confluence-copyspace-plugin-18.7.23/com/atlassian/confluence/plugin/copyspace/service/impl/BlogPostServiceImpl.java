/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Subject
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.security.ContentPermission
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionFailureDescriptor;
import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionStage;
import com.atlassian.confluence.plugin.copyspace.api.event.FailureReason;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceCopyAttachmentException;
import com.atlassian.confluence.plugin.copyspace.service.AttachmentMetadataService;
import com.atlassian.confluence.plugin.copyspace.service.BlogPostService;
import com.atlassian.confluence.plugin.copyspace.service.CommentService;
import com.atlassian.confluence.plugin.copyspace.service.LabelService;
import com.atlassian.confluence.plugin.copyspace.service.LinksUpdater;
import com.atlassian.confluence.plugin.copyspace.service.ProgressMeterService;
import com.atlassian.confluence.plugin.copyspace.service.SidebarLinkCopier;
import com.atlassian.confluence.plugin.copyspace.service.WatcherService;
import com.atlassian.confluence.plugin.copyspace.util.Constants;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="blogPostServiceImpl")
public class BlogPostServiceImpl
implements BlogPostService {
    static final int BATCH_CONTENT_SIZE = 99;
    static final int NEXT_ITEM_FOR_HAS_MORE_CHECK = 1;
    private static final Logger log = LoggerFactory.getLogger(BlogPostServiceImpl.class);
    private static final Expansion NAME_EXPANSION = new Expansion("name");
    private static final String EXPANSIONS = "restrictions.read.restrictions.user,restrictions.read.restrictions.group,restrictions.update.restrictions.user,restrictions.update.restrictions.group,history.lastUpdated,body.storage";
    private final ContentService contentService;
    private final SpaceService spaceService;
    private final AttachmentManager attachmentManager;
    private final PageManager pageManager;
    private final ContentPermissionManager contentPermissionManager;
    private final CommentManager commentManager;
    private final ProgressMeterService progressMeterService;
    private final AttachmentMetadataService attachmentMetadataService;
    private final CommentService commentService;
    private final LinksUpdater linksUpdater;
    private final LabelService labelService;
    private final TransactionTemplate transactionTemplate;
    private final SpaceManager spaceManager;
    private final SidebarLinkCopier sidebarLinkCopier;
    private final WatcherService watcherService;

    @Autowired
    public BlogPostServiceImpl(@ComponentImport(value="apiContentService") ContentService contentService, @ComponentImport(value="apiSpaceService") SpaceService spaceService, @ComponentImport AttachmentManager attachmentManager, @ComponentImport PageManager pageManager, @ComponentImport ContentPermissionManager contentPermissionManager, @ComponentImport CommentManager commentManager, AttachmentMetadataService attachmentMetadataService, ProgressMeterService progressMeterService, CommentService commentService, LinksUpdater linksUpdater, LabelService labelService, WatcherService watcherService, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport SpaceManager spaceManager, SidebarLinkCopier sidebarLinkCopier) {
        this.contentService = contentService;
        this.spaceService = spaceService;
        this.attachmentManager = attachmentManager;
        this.pageManager = pageManager;
        this.contentPermissionManager = contentPermissionManager;
        this.commentManager = commentManager;
        this.attachmentMetadataService = attachmentMetadataService;
        this.progressMeterService = progressMeterService;
        this.commentService = commentService;
        this.linksUpdater = linksUpdater;
        this.labelService = labelService;
        this.watcherService = watcherService;
        this.transactionTemplate = transactionTemplate;
        this.spaceManager = spaceManager;
        this.sidebarLinkCopier = sidebarLinkCopier;
    }

    @Override
    public void copyBlogPosts(CopySpaceContext context) {
        boolean hasMoreBlogPosts;
        int blogPostsCount = context.getBlogPostsCount();
        if (blogPostsCount == 0) {
            return;
        }
        this.progressMeterService.setStatusMessage("copyspace.progress.message.blogpost.in.progress", context.getProgressMeter());
        int offset = 0;
        do {
            List<Object> originalBlogPosts;
            hasMoreBlogPosts = (originalBlogPosts = this.findBatchedCurrentBlogPostsForSpace(context.getOriginalSpaceKey(), offset)).size() > 99;
            originalBlogPosts = originalBlogPosts.stream().limit(99L).collect(Collectors.toList());
            this.executeBatchInTransaction(originalBlogPosts, context);
            log.debug("Copy blogposts operation. Blogs batch from {} to {} successfully copied.", (Object)(offset + 1), (Object)(offset + originalBlogPosts.size()));
            offset += 99;
        } while (hasMoreBlogPosts);
    }

    @Override
    public void copyWholeBlogWatchers(CopySpaceContext context) {
        if (context.isCopyBlogPosts() && context.isPreserveWatchers()) {
            log.debug("Copying whole blog watchers...");
            Space originalSpace = this.spaceManager.getSpace(context.getOriginalSpaceId());
            Space targetSpace = this.spaceManager.getSpace(context.getTargetSpaceKey());
            this.watcherService.copyWholeBlogWatchers(originalSpace, targetSpace);
        }
    }

    private void executeBatchInTransaction(List<Content> originalBlogPosts, CopySpaceContext context) {
        this.transactionTemplate.execute(() -> {
            Space newSpace = this.spaceManager.getSpace(context.getTargetSpaceKey());
            for (Content originalBlogPost : originalBlogPosts) {
                try {
                    this.copyBlogPost(originalBlogPost, newSpace, context);
                }
                catch (IOException e) {
                    throw new CopySpaceCopyAttachmentException(new ExecutionFailureDescriptor(ExecutionStage.COPY_BLOG_POST, FailureReason.MISSING_ATTACHMENT), (Throwable)e);
                }
            }
            return null;
        });
    }

    private List<Content> findBatchedCurrentBlogPostsForSpace(String spaceKey, int offset) {
        return this.contentService.find(ExpansionsParser.parse((String)EXPANSIONS)).withSpace(new com.atlassian.confluence.api.model.content.Space[]{(com.atlassian.confluence.api.model.content.Space)this.spaceService.find(new Expansion[]{NAME_EXPANSION}).withKeys(new String[]{spaceKey}).fetch().get()}).fetchMany(ContentType.BLOG_POST, (PageRequest)new SimplePageRequest(offset, 100)).getResults();
    }

    public void copyBlogPost(Content originalBlogPostContent, Space newSpace, CopySpaceContext context) throws IOException {
        BlogPost copiedBlogpostEntity = this.createBlogPostCopy(originalBlogPostContent, newSpace, context);
        long originalBlogPostId = originalBlogPostContent.getId().asLong();
        this.copyBlogPostContentPermissions(originalBlogPostContent.getRestrictions(), copiedBlogpostEntity);
        if (context.isCopyAttachments()) {
            log.debug("Copying blog post attachments...");
            this.copyBlogPostAttachments(originalBlogPostId, copiedBlogpostEntity, context);
        }
        if (context.isCopyLabels()) {
            log.debug("Copying blog post labels...");
            this.labelService.copyBlogPostLabels(originalBlogPostId, copiedBlogpostEntity);
        }
        if (context.isCopyComments()) {
            log.debug("Copying blog post comments...");
            this.copyBlogPostComments(originalBlogPostId, copiedBlogpostEntity, context);
        }
        if (context.isPreserveWatchers()) {
            log.debug("Copying blog post watchers...");
            this.watcherService.copyBlogPostWatchers(this.pageManager.getBlogPost(originalBlogPostId), copiedBlogpostEntity);
        }
        this.sidebarLinkCopier.checkAndCopyRewritableSidebarLink(originalBlogPostId, (ContentEntityObject)copiedBlogpostEntity, context.getOriginalSpaceKey(), context.getTargetSpaceKey());
        this.progressMeterService.incrementProgressMeterCount(context.getProgressMeter());
    }

    private BlogPost createBlogPostCopy(Content originalBlogPostContent, Space newSpace, CopySpaceContext context) {
        BlogPost copy = new BlogPost();
        String title = originalBlogPostContent.getTitle();
        Date creationDate = originalBlogPostContent.getHistory().getCreatedDate().toDate();
        ConfluenceUser createdBy = FindUserHelper.getUserByUsername((String)originalBlogPostContent.getHistory().getCreatedBy().optionalUsername().orElse(null));
        String contentBody = ((ContentBody)originalBlogPostContent.getBody().get(ContentRepresentation.STORAGE)).getValue();
        Version lastVersion = (Version)originalBlogPostContent.getHistory().getLastUpdatedRef().get();
        Date lastModificationDate = lastVersion.getWhen().toDate();
        copy.setSpace(newSpace);
        copy.setTitle(title);
        copy.setCreationDate(creationDate);
        copy.setCreator(createdBy);
        copy.setLastModifier(createdBy);
        copy.setLastModificationDate(lastVersion.getNumber() > 1 ? creationDate : lastModificationDate);
        copy.setSynchronyRevisionSource("restored");
        copy.setBodyAsString(this.linksUpdater.rewriteLinks(contentBody, (ContentEntityObject)copy, context));
        this.pageManager.saveContentEntity((ContentEntityObject)copy, Constants.SUPPRESS_EVENT_KEEP_LAST_MODIFIER);
        return copy;
    }

    private void copyBlogPostAttachments(long originalblogPostId, BlogPost copiedEntity, CopySpaceContext context) throws IOException {
        BlogPost originalBlogPostImitation = new BlogPost();
        originalBlogPostImitation.setId(originalblogPostId);
        this.attachmentManager.copyAttachments((ContentEntityObject)originalBlogPostImitation, (ContentEntityObject)copiedEntity, DefaultSaveContext.BULK_OPERATION);
        List originalAttachments = this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)originalBlogPostImitation);
        this.attachmentMetadataService.preserveMetadata(originalAttachments, (ContentEntityObject)copiedEntity);
        this.sidebarLinkCopier.checkAndCopyRewritableAttachmentSidebarLink(originalAttachments, (ContentEntityObject)copiedEntity, context.getOriginalSpaceKey(), context.getTargetSpaceKey());
        if (context.isCopyLabels()) {
            List copiedAttachments = this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)copiedEntity);
            this.labelService.copyAttachmentLabels(originalAttachments, copiedAttachments, context);
        }
    }

    private void copyBlogPostComments(long originalBlogPostId, BlogPost copiedBlogPostEntity, CopySpaceContext context) {
        List blogPostComments = this.commentManager.getPageComments(originalBlogPostId, copiedBlogPostEntity.getCreationDate(), "");
        this.commentService.copyComments((ContentEntityObject)copiedBlogPostEntity, blogPostComments, context);
        this.commentService.copyFileComments((ContentEntityObject)this.pageManager.getBlogPost(originalBlogPostId), (ContentEntityObject)copiedBlogPostEntity);
    }

    private void copyBlogPostContentPermissions(Map<OperationKey, ContentRestriction> contentRestrictions, BlogPost entity) {
        Set<OperationKey> contentRestrictionsKeys = contentRestrictions.keySet();
        for (OperationKey operationKey : contentRestrictionsKeys) {
            String permissionLevel = this.getContentPermissionType(operationKey);
            List subjects = contentRestrictions.get(operationKey).getRestrictions().values().stream().map(PageResponse::getResults).flatMap(Collection::stream).collect(Collectors.toList());
            for (Subject subject : subjects) {
                ContentPermission newPermission = subject instanceof KnownUser ? ContentPermission.createUserPermission((String)permissionLevel, (ConfluenceUser)FindUserHelper.getUserByUsername((String)((KnownUser)subject).getUsername())) : ContentPermission.createGroupPermission((String)permissionLevel, (String)((Group)subject).getName());
                this.contentPermissionManager.addContentPermission(newPermission, (ContentEntityObject)entity);
            }
        }
    }

    private String getContentPermissionType(OperationKey key) {
        if (key == OperationKey.READ) {
            return "View";
        }
        if (key == OperationKey.UPDATE) {
            return "Edit";
        }
        throw new IllegalArgumentException(String.format("Unexpected operation key: %s", key));
    }
}

