/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.event.events.content.ContentExportedToWordEvent;
import com.atlassian.confluence.event.events.content.ContentHistoricalVersionRemoveEvent;
import com.atlassian.confluence.event.events.content.ContentPurgedFromTrashEvent;
import com.atlassian.confluence.event.events.content.ContentRevertedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostMovedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractContentAuditListener;
import com.atlassian.confluence.internal.content.DraftUtils;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageContentAuditListener
extends AbstractContentAuditListener {
    public static final String PAGE_CREATE_SUMMARY = AuditHelper.buildSummaryTextKey("page.created");
    public static final String PAGE_UPDATE_SUMMARY = AuditHelper.buildSummaryTextKey("page.updated");
    public static final String PAGE_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("page.deleted");
    public static final String PAGE_RESTORE_SUMMARY = AuditHelper.buildSummaryTextKey("page.restored");
    public static final String PAGE_PURGE_SUMMARY = AuditHelper.buildSummaryTextKey("page.purged");
    public static final String PAGE_VERSION_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("page.version.deleted");
    public static final String PAGE_MOVE_SUMMARY = AuditHelper.buildSummaryTextKey("page.moved");
    public static final String BLOG_CREATE_SUMMARY = AuditHelper.buildSummaryTextKey("blog.created");
    public static final String BLOG_UPDATE_SUMMARY = AuditHelper.buildSummaryTextKey("blog.updated");
    public static final String BLOG_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("blog.deleted");
    public static final String BLOG_RESTORE_SUMMARY = AuditHelper.buildSummaryTextKey("blog.restored");
    public static final String BLOG_PURGE_SUMMARY = AuditHelper.buildSummaryTextKey("blog.purged");
    public static final String BLOG_VERSION_DELETE_SUMMARY = AuditHelper.buildSummaryTextKey("blog.version.deleted");
    public static final String BLOG_MOVE_SUMMARY = AuditHelper.buildSummaryTextKey("blog.moved");
    public static final String PAGE_EXPORTED_TO_WORD_EVENT_SUMMARY = AuditHelper.buildSummaryTextKey("page.exported.word");
    public static final String BLOG_EXPORTED_TO_WORD_EVENT_SUMMARY = AuditHelper.buildSummaryTextKey("blogpost.exported.word");
    static final String CONTENT_REVERTED_EVENT_SUMMARY = AuditHelper.buildSummaryTextKey("content.reverted");
    static final String KEY_PAGE_VERSION = "page-version";
    public static final String CONTENT_MOVE_SOURCE_SPACE_ATTRIBUTE = AuditHelper.buildExtraAttribute("content.moved.source.space");
    public static final String CONTENT_MOVE_SOURCE_PARENT_ATTRIBUTE = AuditHelper.buildExtraAttribute("content.moved.source.parent.page");
    public static final String CONTENT_MOVE_TARGET_PARENT_ATTRIBUTE = AuditHelper.buildExtraAttribute("content.moved.target.parent.page");
    public static final String CONTENT_MOVE_TARGET_SPACE_ATTRIBUTE = AuditHelper.buildExtraAttribute("content.moved.target.space");
    private final DateFormatterFactory dateFormatterFactory;

    public PageContentAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, PageManager pageManager, SpaceManager spaceManager, DateFormatterFactory dateFormatterFactory, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, pageManager, spaceManager, auditingContext);
        this.dateFormatterFactory = dateFormatterFactory;
    }

    @EventListener
    public void pageCreateEvent(PageCreateEvent event) {
        this.save(() -> {
            Page page = event.getPage();
            return AuditEvent.builder((AuditType)this.buildAuditType(PAGE_CREATE_SUMMARY, CoverageLevel.FULL)).changedValues(this.buildChangedValues(null, page)).affectedObjects(this.buildAuditResources(page, this.resourceTypes.page())).build();
        });
    }

    @EventListener
    public void blogPostCreateEvent(BlogPostCreateEvent event) {
        this.save(() -> {
            BlogPost blogPost = event.getBlogPost();
            return AuditEvent.builder((AuditType)this.buildAuditType(BLOG_CREATE_SUMMARY, CoverageLevel.FULL)).changedValues(this.buildChangedValues(null, blogPost)).affectedObjects(this.buildAuditResources(blogPost, this.resourceTypes.blog())).build();
        });
    }

    @EventListener
    public void pageUpdateEvent(PageUpdateEvent event) {
        if (EnumSet.of(PageUpdateTrigger.LINK_REFACTORING, PageUpdateTrigger.VIEW_PAGE).contains(event.getUpdateTrigger())) {
            return;
        }
        this.save(() -> {
            Page page = event.getPage();
            AbstractPage previousPage = event.getOriginalPage();
            return AuditEvent.builder((AuditType)this.buildAuditType(PAGE_UPDATE_SUMMARY, CoverageLevel.FULL)).changedValues(this.buildChangedValues(previousPage, page)).affectedObjects(this.buildAuditResources(page, this.resourceTypes.page())).build();
        });
    }

    @EventListener
    public void blogPostUpdateEvent(BlogPostUpdateEvent event) {
        this.save(() -> {
            BlogPost blogPost = event.getBlogPost();
            BlogPost previousBlogPost = event.getOriginalBlogPost();
            return AuditEvent.builder((AuditType)this.buildAuditType(BLOG_UPDATE_SUMMARY, CoverageLevel.FULL)).changedValues(this.buildChangedValues(previousBlogPost, blogPost)).affectedObjects(this.buildAuditResources(blogPost, this.resourceTypes.blog())).build();
        });
    }

    @EventListener
    public void contentHistoricalVersionRemoveEvent(ContentHistoricalVersionRemoveEvent event) {
        ContentEntityObject removedObject = event.getContent();
        if (!DraftUtils.isPageOrBlogPost(removedObject)) {
            return;
        }
        this.saveIfPresent(() -> {
            Optional<VersionHistorySummary> versionHistoryOptional = this.pageManager.getVersionHistorySummaries(removedObject).stream().filter(historySummary -> historySummary.getVersion() == removedObject.getVersion()).findFirst();
            return versionHistoryOptional.map(versionHistory -> AuditEvent.builder((AuditType)this.buildAuditType(DraftUtils.isBlogPost(removedObject) ? BLOG_VERSION_DELETE_SUMMARY : PAGE_VERSION_DELETE_SUMMARY, CoverageLevel.ADVANCED)).extraAttribute(AuditAttribute.fromI18nKeys((String)AuditHelper.buildExtraAttribute("version"), (String)String.valueOf(removedObject.getVersion())).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)AuditHelper.buildExtraAttribute("page.contributors"), (String)versionHistory.getContributorSet().stream().filter(Objects::nonNull).map(User::getFullName).collect(Collectors.joining(", "))).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)AuditHelper.buildExtraAttribute("published"), (String)this.dateFormatterFactory.createGlobal().formatDateTime(versionHistory.getLastModificationDate())).build()).affectedObjects(this.buildAuditResources(removedObject, this.resourceTypes.page())).build());
        });
    }

    @EventListener
    public void onPageTrashedEvent(PageTrashedEvent event) {
        this.save(() -> {
            Page page = event.getPage();
            return AuditEvent.builder((AuditType)this.buildAuditType(PAGE_DELETE_SUMMARY, CoverageLevel.ADVANCED)).affectedObjects(this.buildAuditResources(page, this.resourceTypes.page())).build();
        });
    }

    @EventListener
    public void onBlogPostTrashedEvent(BlogPostTrashedEvent event) {
        this.save(() -> {
            BlogPost blogPost = event.getBlogPost();
            return AuditEvent.builder((AuditType)this.buildAuditType(BLOG_DELETE_SUMMARY, CoverageLevel.ADVANCED)).affectedObjects(this.buildAuditResources(blogPost, this.resourceTypes.blog())).build();
        });
    }

    @EventListener
    public void onPageRestoreEvent(PageRestoreEvent event) {
        this.save(() -> {
            Page page = event.getPage();
            return AuditEvent.builder((AuditType)this.buildAuditType(PAGE_RESTORE_SUMMARY, CoverageLevel.ADVANCED)).affectedObjects(this.buildAuditResources(page, this.resourceTypes.page())).build();
        });
    }

    @EventListener
    public void onBlogPostRestoreEvent(BlogPostRestoreEvent event) {
        this.save(() -> {
            BlogPost blogPost = event.getBlogPost();
            return AuditEvent.builder((AuditType)this.buildAuditType(BLOG_RESTORE_SUMMARY, CoverageLevel.ADVANCED)).affectedObjects(this.buildAuditResources(blogPost, this.resourceTypes.blog())).build();
        });
    }

    @EventListener
    public void onContentPurgedFromTrashEvent(ContentPurgedFromTrashEvent event) {
        this.saveIfPresent(() -> this.buildAbstractPageAuditEventBuilder(event.getContent(), DraftUtils.isBlogPost(event.getContent()) ? BLOG_PURGE_SUMMARY : PAGE_PURGE_SUMMARY, AuditCategories.PAGES).map(AuditEvent.Builder::build));
    }

    @EventListener
    public void onPageOrBlogExportedToWordEvent(ContentExportedToWordEvent event) {
        this.saveIfPresent(() -> this.buildAbstractPageAuditEventBuilder(event.getContent(), DraftUtils.isBlogPost(event.getContent()) ? BLOG_EXPORTED_TO_WORD_EVENT_SUMMARY : PAGE_EXPORTED_TO_WORD_EVENT_SUMMARY, AuditCategories.IMPORT_EXPORT).map(AuditEvent.Builder::build));
    }

    @EventListener
    public void onContentRevertedEvent(ContentRevertedEvent event) {
        this.saveIfPresent(() -> this.buildAbstractPageAuditEventBuilder(event.getEntity(), CONTENT_REVERTED_EVENT_SUMMARY, AuditCategories.PAGES).map(builder -> builder.changedValue(ChangedValue.fromI18nKeys((String)KEY_PAGE_VERSION).to(String.valueOf(event.getVersion())).build())).map(AuditEvent.Builder::build));
    }

    @EventListener
    public void onPageMoveEvent(PageMoveEvent event) {
        this.saveIfPresent(() -> this.buildAbstractPageAuditEventBuilder(event.getPage(), PAGE_MOVE_SUMMARY, AuditCategories.PAGES).map(builder -> {
            Optional.ofNullable(event.getOldSpace().getName()).ifPresent(name -> builder.extraAttribute(AuditAttribute.fromI18nKeys((String)CONTENT_MOVE_SOURCE_SPACE_ATTRIBUTE, (String)name).build()));
            Optional.ofNullable(event.getOldParentPage()).map(ContentEntityObject::getDisplayTitle).ifPresent(title -> builder.extraAttribute(AuditAttribute.fromI18nKeys((String)CONTENT_MOVE_SOURCE_PARENT_ATTRIBUTE, (String)title).build()));
            Optional.ofNullable(this.getSpace(event.getPage())).map(Space::getName).ifPresent(name -> builder.extraAttribute(AuditAttribute.fromI18nKeys((String)CONTENT_MOVE_TARGET_SPACE_ATTRIBUTE, (String)name).build()));
            Optional.ofNullable(event.getNewParentPage()).map(ContentEntityObject::getDisplayTitle).ifPresent(title -> builder.extraAttribute(AuditAttribute.fromI18nKeys((String)CONTENT_MOVE_TARGET_PARENT_ATTRIBUTE, (String)title).build()));
            return builder;
        }).map(AuditEvent.Builder::build));
    }

    @EventListener
    public void onBlogPostMovedEvent(BlogPostMovedEvent event) {
        this.saveIfPresent(() -> this.buildAbstractPageAuditEventBuilder(event.getBlogPost(), BLOG_MOVE_SUMMARY, AuditCategories.PAGES).map(builder -> {
            Optional.ofNullable(event.getOriginalSpace().getName()).ifPresent(name -> builder.extraAttribute(AuditAttribute.fromI18nKeys((String)CONTENT_MOVE_SOURCE_SPACE_ATTRIBUTE, (String)name).build()));
            Optional.ofNullable(this.getSpace(event.getBlogPost())).map(Space::getName).ifPresent(name -> builder.extraAttribute(AuditAttribute.fromI18nKeys((String)CONTENT_MOVE_TARGET_SPACE_ATTRIBUTE, (String)name).build()));
            return builder;
        }).map(AuditEvent.Builder::build));
    }

    private Optional<AuditEvent.Builder> buildAbstractPageAuditEventBuilder(ContentEntityObject content, String summaryKey, String categoryKey) {
        if (DraftUtils.isPageOrBlogPost(content)) {
            return Optional.of(AuditEvent.fromI18nKeys((String)categoryKey, (String)summaryKey, (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.END_USER_ACTIVITY).affectedObjects(this.buildAuditResources(content, this.getPageOrBlogResourceType(content, this.resourceTypes.page()))));
        }
        return Optional.empty();
    }

    private AuditType buildAuditType(String summary, CoverageLevel coverageLevel) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.END_USER_ACTIVITY, (CoverageLevel)coverageLevel, (String)AuditCategories.PAGES, (String)summary).build();
    }

    private List<ChangedValue> buildChangedValues(@Nullable AbstractPage oldChangedObject, @NonNull AbstractPage newChangedObject) {
        return this.getAuditHandlerService().handle(Optional.ofNullable(oldChangedObject), Optional.of(newChangedObject));
    }

    private List<AuditResource> buildAuditResources(ContentEntityObject content, String contentString) {
        ArrayList<AuditResource> affectedObjects = new ArrayList<AuditResource>();
        Optional.ofNullable(this.getSpace(content)).ifPresent(space -> affectedObjects.add(this.buildResource(space.getName(), this.resourceTypes.space(), space.getId())));
        affectedObjects.add(this.buildResource(this.getTitle(content), contentString, content.getIdAsString()));
        return affectedObjects;
    }
}

