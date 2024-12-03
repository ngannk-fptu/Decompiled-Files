/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.impl.hibernate.HibernateSessionManager5
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.util.DateUtils$DateRange
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.math.LongMath
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.apache.commons.lang3.time.DateUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.HibernateException
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.links.LinksUpdater;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPermissionSummary;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.DefaultDeleteContext;
import com.atlassian.confluence.core.DefaultListBuilder;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.DeleteContext;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.SingleListBuilderCallback;
import com.atlassian.confluence.event.events.analytics.SharedDraftCreatedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostMovedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageChildrenReorderEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveCompletedEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.CopyPageHierarchyFinishEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.CopyPageHierarchyStartEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.DeletePageHierarchyFinishEvent;
import com.atlassian.confluence.event.events.content.pagehierarchy.DeletePageHierarchyStartEvent;
import com.atlassian.confluence.event.events.security.ContentPermissionEvent;
import com.atlassian.confluence.impl.content.DefaultContentEntityManager;
import com.atlassian.confluence.impl.hibernate.HibernateSessionManager5;
import com.atlassian.confluence.impl.pages.attachments.filesystem.UpdateAttachmentsOnFilesystemOnPageMoveHandler;
import com.atlassian.confluence.impl.search.IndexerEventPublisher;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryManager;
import com.atlassian.confluence.internal.content.collab.ContentReconciliationManager;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.internal.pages.persistence.AbstractPageDaoInternal;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.BlogPostStatisticsDTO;
import com.atlassian.confluence.pages.ChildPositionComparator;
import com.atlassian.confluence.pages.DuplicateDataRuntimeException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageStatisticsDTO;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.pages.persistence.dao.BlogPostDao;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.DefaultBulkPageCopy;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.DefaultBulkPageDelete;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.util.DateUtils;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.LongMath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.DateUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

@ParametersAreNonnullByDefault
public class DefaultPageManager
extends DefaultContentEntityManager
implements PageManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultPageManager.class);
    private static final String LOCK_PREFIX = DefaultPageManager.class.getSimpleName();
    public static final String EXCERPT_KEY = "confluence.excerpt";
    public static final String STALE_DRAFT_REMOVE_BATCH_SIZE = "confluence.stale.draft.remove.batch.size";
    private static final int SPACE_FETCHING_PERCENTAGE = 5;
    private static final int ITEMS_PER_BATCH = Integer.getInteger("confluence.content.removal.batch.size", 50);
    private static final int PAGE_REMOVAL_PERCENTAGE = 55;
    private final PageDaoInternal pageDao;
    private final AttachmentManager attachmentManager;
    private final HibernateSessionManager5 hibernateSessionManager5;
    private final SessionFactory sessionFactory;
    private final EventPublisher eventPublisher;
    private final BlogPostDao blogPostDao;
    private final AbstractPageDaoInternal abstractPageDao;
    private final SpaceDao spaceDao;
    private final ContentPropertyManager contentPropertyManager;
    private final PermissionManager permissionManager;
    private final LoginManager loginManager;
    private final ExceptionTolerantMigrator revertedContentMigrator;
    private final CollaborativeEditingHelper collaborativeEditingHelper;
    private final SpacePermissionQueryManager spacePermissionQueryManager;
    private final ContentPermissionManager contentPermissionManager;
    private final ClusterLockService lockService;
    private final LinksUpdater linksUpdater;
    private final SpacePermissionManager spacePermissionManager;
    private final PlatformTransactionManager transactionManager;
    private final ContentReconciliationManager reconciliationManager;
    private final UpdateAttachmentsOnFilesystemOnPageMoveHandler updateAttachmentsOnFilesystemOnPageMoveHandler;
    private final ContentPropertyService contentPropertyService;
    private final PermissionPredicates permissionPredicates = new PermissionPredicates();

    public DefaultPageManager(PageDaoInternal pageDao, AttachmentManager attachmentManager, HibernateSessionManager5 hibernateSessionManager5, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, BlogPostDao blogPostDao, AbstractPageDaoInternal abstractPageDao, SpaceDao spaceDao, ContentPropertyManager contentPropertyManager, PermissionManager permissionManager, LoginManager loginManager, ExceptionTolerantMigrator revertedContentMigrator, CollaborativeEditingHelper collaborativeEditingHelper, RelationManager relationManager, SpacePermissionQueryManager spacePermissionQueryManager, ContentPermissionManager contentPermissionManager, ClusterLockService clusterLockService, LinksUpdater linksUpdater, SpacePermissionManager spacePermissionManager, PlatformTransactionManager transactionManager, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker, ContentReconciliationManager reconciliationManager, UpdateAttachmentsOnFilesystemOnPageMoveHandler updateAttachmentsOnFilesystemOnPageMoveHandler, SessionFactory sessionFactory, ContentPropertyService contentPropertyService) {
        super(pageDao, sessionFactory, wikiToStorageConverter, eventPublisher, relationManager, collaborativeEditingHelper, auditingContext, retentionFeatureChecker, DefaultPageManager.eventFactory());
        this.pageDao = pageDao;
        this.attachmentManager = attachmentManager;
        this.hibernateSessionManager5 = hibernateSessionManager5;
        this.sessionFactory = sessionFactory;
        this.eventPublisher = eventPublisher;
        this.blogPostDao = (BlogPostDao)Preconditions.checkNotNull((Object)blogPostDao);
        this.abstractPageDao = (AbstractPageDaoInternal)Preconditions.checkNotNull((Object)abstractPageDao);
        this.spaceDao = (SpaceDao)Preconditions.checkNotNull((Object)spaceDao);
        this.contentPropertyManager = (ContentPropertyManager)Preconditions.checkNotNull((Object)contentPropertyManager);
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager);
        this.loginManager = (LoginManager)Preconditions.checkNotNull((Object)loginManager);
        this.revertedContentMigrator = (ExceptionTolerantMigrator)Preconditions.checkNotNull((Object)revertedContentMigrator);
        this.collaborativeEditingHelper = (CollaborativeEditingHelper)Preconditions.checkNotNull((Object)collaborativeEditingHelper);
        this.spacePermissionQueryManager = (SpacePermissionQueryManager)Preconditions.checkNotNull((Object)spacePermissionQueryManager);
        this.contentPermissionManager = (ContentPermissionManager)Preconditions.checkNotNull((Object)contentPermissionManager);
        this.lockService = clusterLockService;
        this.linksUpdater = linksUpdater;
        this.spacePermissionManager = spacePermissionManager;
        this.transactionManager = transactionManager;
        this.reconciliationManager = reconciliationManager;
        this.updateAttachmentsOnFilesystemOnPageMoveHandler = updateAttachmentsOnFilesystemOnPageMoveHandler;
        this.contentPropertyService = contentPropertyService;
    }

    @Deprecated
    public DefaultPageManager(PageDaoInternal pageDao, AttachmentManager attachmentManager, HibernateSessionManager5 hibernateSessionManager5, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, BlogPostDao blogPostDao, AbstractPageDaoInternal abstractPageDao, SpaceDao spaceDao, ContentPropertyManager contentPropertyManager, PermissionManager permissionManager, LoginManager loginManager, ExceptionTolerantMigrator revertedContentMigrator, CollaborativeEditingHelper collaborativeEditingHelper, RelationManager relationManager, SpacePermissionQueryManager spacePermissionQueryManager, ContentPermissionManager contentPermissionManager, ClusterLockService clusterLockService, LinksUpdater linksUpdater, SpacePermissionManager spacePermissionManager, PlatformTransactionManager transactionManager, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker, ContentReconciliationManager reconciliationManager, UpdateAttachmentsOnFilesystemOnPageMoveHandler updateAttachmentsOnFilesystemOnPageMoveHandler, SessionFactory sessionFactory) {
        this(pageDao, attachmentManager, hibernateSessionManager5, wikiToStorageConverter, eventPublisher, blogPostDao, abstractPageDao, spaceDao, contentPropertyManager, permissionManager, loginManager, revertedContentMigrator, collaborativeEditingHelper, relationManager, spacePermissionQueryManager, contentPermissionManager, clusterLockService, linksUpdater, spacePermissionManager, transactionManager, auditingContext, retentionFeatureChecker, reconciliationManager, updateAttachmentsOnFilesystemOnPageMoveHandler, sessionFactory, (ContentPropertyService)ContainerManager.getComponent((String)"contentPropertyService"));
    }

    @Override
    public AbstractPage createDraft(String contentType, String spaceKey) {
        return this.createDraft(contentType, spaceKey, 0L);
    }

    @Override
    public AbstractPage createDraft(String contentType, String spaceKey, long parentPageId) {
        Page parentPage;
        Preconditions.checkArgument(("page".equals(contentType) || "blogpost".equals(contentType) ? 1 : 0) != 0, (Object)"Content type is neither 'page' nor 'blogpost', cannot create draft");
        AbstractPage transientDraft = "page".equals(contentType) ? new Page() : new BlogPost();
        transientDraft.setSpace(this.spaceDao.getSpace(spaceKey));
        if (transientDraft instanceof Page && (parentPage = this.getPage(parentPageId)) != null) {
            ((Page)parentPage.getLatestVersion()).addChild((Page)transientDraft);
        }
        this.reconciliationManager.markDraftSynchronised(transientDraft);
        AbstractPage persistentDraft = super.createDraft(transientDraft, DefaultSaveContext.DRAFT);
        this.eventPublisher.publish((Object)new SharedDraftCreatedEvent());
        return persistentDraft;
    }

    @Override
    public AbstractPage createOrFindDraftFor(@NonNull AbstractPage page) {
        AbstractPage draft = super.findDraftFor(page);
        if (draft == null) {
            draft = "page".equals(page.getType()) ? new Page() : new BlogPost();
            draft.setTitle(page.getTitle());
            draft.setBodyAsString(page.getBodyAsString());
            draft.setOriginalVersion(page);
            draft.setSpace(page.getSpace());
            draft.setCreator(page.getCreator());
            draft.setLastModifier(page.getLastModifier());
            draft.setCreationDate(page.getCreationDate());
            draft.setLastModificationDate(page.getLastModificationDate());
            this.reconciliationManager.markDraftSynchronised(draft);
            draft.setShareId(page.getShareId());
            draft = super.createDraft(draft, DefaultSaveContext.RAW_DRAFT);
            this.eventPublisher.publish((Object)new SharedDraftCreatedEvent());
        } else {
            this.reconciliationManager.reconcileDraft(page, draft);
        }
        return draft;
    }

    @Override
    public void renamePage(AbstractPage page, String newPageTitle) {
        this.saveNewVersion(page, page11 -> page11.setTitle(newPageTitle));
    }

    @Override
    public void renamePageWithoutNotifications(AbstractPage page, String newPageTitle) {
        this.saveNewVersion(page, page11 -> page11.setTitle(newPageTitle), ((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)DefaultSaveContext.builder().suppressNotifications(true)).updateTrigger(PageUpdateTrigger.PAGE_RENAME)).build());
    }

    @Override
    public @NonNull List<BlogPost> getBlogPosts(Space space, boolean currentOnly) {
        return this.blogPostDao.getBlogPosts(space, currentOnly);
    }

    @Override
    public @NonNull List<AbstractPage> getPossibleRedirectsInSpace(Space space, String pageTitle, int maxResultCount) {
        return (List)this.getSpacePermissionQueryBuilderForCurrentUser().fold(accessDenied -> Collections.emptyList(), permissionFilterBuilder -> this.pageDao.findPagesWithCurrentOrHistoricalTitleInPermittedSpace((SpacePermissionQueryBuilder)permissionFilterBuilder, pageTitle, space, maxResultCount));
    }

    @Override
    public @NonNull List<AbstractPage> getPossibleBlogRedirectsInSpace(Space space, String blogTitle, int maxResultCount) {
        return (List)this.getSpacePermissionQueryBuilderForCurrentUser().fold(accessDenied -> Collections.emptyList(), permissionFilterBuilder -> this.pageDao.findBlogsWithCurrentOrHistoricalTitleInPermittedSpace((SpacePermissionQueryBuilder)permissionFilterBuilder, blogTitle, space, maxResultCount));
    }

    @Override
    public @NonNull List<AbstractPage> getPossibleBlogRedirectsNotInSpace(Space space, String blogTitle, int maxResultCount) {
        return (List)this.getSpacePermissionQueryBuilderForCurrentUser().fold(accessDenied -> Collections.emptyList(), permissionFilterBuilder -> this.pageDao.findBlogsWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept((SpacePermissionQueryBuilder)permissionFilterBuilder, blogTitle, space, maxResultCount));
    }

    @Override
    public @NonNull List<AbstractPage> getPossibleRedirectsNotInSpace(Space space, String pageTitle, int maxResultCount) {
        return (List)this.getSpacePermissionQueryBuilderForCurrentUser().fold(accessDenied -> Collections.emptyList(), permissionFilterBuilder -> this.pageDao.findPagesWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept((SpacePermissionQueryBuilder)permissionFilterBuilder, pageTitle, space, maxResultCount));
    }

    private Either<AccessDenied, SpacePermissionQueryBuilder> getSpacePermissionQueryBuilderForCurrentUser() {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return this.spacePermissionQueryManager.createSpacePermissionQueryBuilder(currentUser, "VIEWSPACE");
    }

    @Override
    public @NonNull List getRecentlyAddedBlogPosts(int maxPosts, @Nullable Date timeSince, String spaceKey) {
        if (timeSince == null) {
            return this.getRecentlyAddedBlogPosts(maxPosts, spaceKey);
        }
        return this.blogPostDao.getRecentlyAddedBlogPosts(maxPosts, timeSince, spaceKey);
    }

    @Override
    public @Nullable Page getPage(long id) {
        return this.pageDao.getPageById(id);
    }

    @Override
    public @NonNull List<Page> getPages(Iterable<Long> ids) {
        return this.pageDao.getPagesByIds(ids);
    }

    @Override
    public @Nullable AbstractPage getAbstractPage(long id) {
        return this.abstractPageDao.getAbstractPageById(id);
    }

    @Override
    public @NonNull List<AbstractPage> getAbstractPages(Iterable<Long> ids) {
        return this.abstractPageDao.getAbstractPageByIds(ids);
    }

    @Override
    public @Nullable AbstractPage getById(long id) {
        return this.getAbstractPage(id);
    }

    @Override
    public @Nullable BlogPost getBlogPost(long id) {
        return this.blogPostDao.getById(id);
    }

    @Override
    public @Nullable Page getPage(String spaceKey, String pageTitle) {
        Space space = this.spaceDao.getSpace(spaceKey);
        return this.pageDao.getPage(space, pageTitle);
    }

    @Override
    public @Nullable Page getPageWithComments(String spaceKey, String pageTitle) {
        Space space = this.spaceDao.getSpace(spaceKey);
        return this.pageDao.getPageWithComments(space, pageTitle);
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPagesByTitle(String title, LimitedRequest pageRequest, Predicate<? super AbstractPage> ... filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getAbstractPagesByTitle(title, pageRequest), this.andCanViewPredicate(filter));
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> ... filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getAbstractPages(contentTypes, statuses, pageRequest), this.andCanViewPredicate(filter));
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(Space space, List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> ... filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getAbstractPages(space, contentTypes, statuses, pageRequest), this.andCanViewPredicate(filter));
    }

    @Override
    public PageResponse<AbstractPage> getAbstractPages(LocalDate creationDay, ZoneId zone, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> ... filter) {
        DateUtils.DateRange dateRange = DefaultPageManager.asDateRange(creationDay, zone);
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getAbstractPages(dateRange, statuses, pageRequest), this.andCanViewPredicate(filter));
    }

    private static DateUtils.DateRange asDateRange(LocalDate creationDay, ZoneId zone) {
        ZonedDateTime start = creationDay.atStartOfDay().atZone(zone);
        ZonedDateTime end = start.plusDays(1L);
        return new DateUtils.DateRange(Date.from(start.toInstant()), Date.from(end.toInstant()));
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(Space space, String title, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> ... filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getAbstractPages(space, title, statuses, pageRequest), this.andCanViewPredicate(filter));
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(String title, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> ... filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getAbstractPages(title, statuses, pageRequest), this.andCanViewPredicate(filter));
    }

    @Override
    public List<ContentPermissionSummary> findContentPermissionSummaryByIds(Collection<Long> ids) {
        return this.pageDao.findContentPermissionSummaryByIds(ids);
    }

    @Override
    public int getCommentCountOnPage(long pageId) {
        return this.pageDao.getCommentCountOnPage(pageId);
    }

    @Override
    public int getCommentCountOnBlog(long blogId) {
        return this.blogPostDao.getCommentCountOnBlog(blogId);
    }

    @Override
    public @NonNull List getPageInTrash(String spaceKey, String title) {
        return this.pageDao.getPageInTrash(spaceKey, title);
    }

    @Override
    public @NonNull List<BlogPost> getBlogPostsInTrash(String spaceKey, String title) {
        return this.blogPostDao.getBlogPostsInTrash(spaceKey, title);
    }

    @Override
    public boolean spaceHasBlogPosts(String spaceKey) {
        return this.blogPostDao.getMostRecentBlogPost(spaceKey) != null;
    }

    @Override
    public @NonNull List<Page> getDescendants(Page page) {
        return this.pageDao.getDescendants(page);
    }

    @Override
    public @NonNull List<String> getDescendantTitles(Page page) {
        return this.pageDao.getDescendantTitles(page);
    }

    @Override
    @Deprecated
    public @NonNull List getDescendents(Page page) {
        return this.getDescendants(page);
    }

    @Override
    public void updatePageInAncestorCollections(Page page, @Nullable Page newParent) {
        List<Page> descendants = this.getDescendants(page);
        this.removePageFromAncestorCollections(page, descendants);
        ArrayList<Page> newAncestors = new ArrayList<Page>();
        if (newParent != null) {
            newAncestors.addAll(newParent.getAncestors());
            newAncestors.add(newParent);
        }
        page.getAncestors().clear();
        page.getAncestors().addAll(newAncestors);
        newAncestors.add(page);
        for (Page descendant : descendants) {
            descendant.getAncestors().addAll(0, newAncestors);
        }
    }

    @Override
    public void removePageFromAncestorCollections(Page page) {
        List<Page> descendants = this.getDescendants(page);
        log.debug("Removing page {} from the ancestor collections. Found {} descendants.", (Object)page.getId(), (Object)descendants.size());
        this.removePageFromAncestorCollections(page, descendants);
    }

    private void removePageFromAncestorCollections(Page page, List<Page> descendants) {
        ArrayList<Page> ancestors = new ArrayList<Page>(page.getAncestors());
        ancestors.add(page);
        log.debug("Found {} ancestors of page {}", (Object)ancestors.size(), (Object)page.getId());
        for (Page descendant : descendants) {
            log.debug("Removing ancestors for descendant {}", (Object)descendant.getId());
            descendant.getAncestors().removeAll(ancestors);
        }
        try {
            this.sessionFactory.getCurrentSession().flush();
        }
        catch (HibernateException e) {
            log.error("Could not flush the session: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    @Override
    public void removeAllPages(Space space) {
        this.removeAllPages(space, new ProgressMeter());
    }

    @Override
    public void removeAllPages(Space space, ProgressMeter progress) {
        int percentageAfterFetch = progress.getPercentageComplete() + 5;
        LimitedRequest limitedRequest = LimitedRequestImpl.create((int)ITEMS_PER_BATCH);
        long pageCount = this.pageDao.getPageCount(space.getKey());
        int numberOfBatches = (int)LongMath.divide((long)pageCount, (long)ITEMS_PER_BATCH, (RoundingMode)RoundingMode.UP);
        AtomicInteger totalDeleted = new AtomicInteger(0);
        for (int batch = 1; batch < numberOfBatches + 1; ++batch) {
            this.hibernateSessionManager5.withNewTransaction(() -> {
                PageResponse pageResponse = PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, this.pageDao.getAbstractPages(space, Collections.singletonList(ContentType.PAGE), (List<ContentStatus>)ContentStatus.BUILT_IN, limitedRequest), null);
                List abstractPages = pageResponse.getResults();
                abstractPages.forEach(abstractPage -> {
                    try {
                        Page page = (Page)abstractPage;
                        log.debug("Deleting page with ID: {}", (Object)page.getId());
                        page.remove(this);
                    }
                    catch (Exception e) {
                        log.error("Exception occurred while deleting page with ID {}: {}", (Object)abstractPage.getId(), (Object)e.getMessage());
                    }
                });
                totalDeleted.addAndGet(abstractPages.size());
                return true;
            });
            progress.setStatus(totalDeleted.get() + " pages deleted");
            progress.setPercentage(percentageAfterFetch + batch * 55 / numberOfBatches);
        }
        progress.setPercentage(55);
    }

    @Override
    public void removeAllBlogPosts(Space space) {
        this.removeAllBlogPosts(space, new ProgressMeter());
    }

    @Override
    public void removeAllBlogPosts(Space space, ProgressMeter progress) {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((int)ITEMS_PER_BATCH);
        AtomicBoolean continued = new AtomicBoolean(true);
        AtomicInteger totalDeleted = new AtomicInteger(0);
        do {
            this.hibernateSessionManager5.withNewTransaction(() -> {
                List blogPostBatch = PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, this.pageDao.getAbstractPages(space, Collections.singletonList(ContentType.BLOG_POST), (List<ContentStatus>)ContentStatus.BUILT_IN, limitedRequest), null).getResults();
                continued.set(!blogPostBatch.isEmpty());
                blogPostBatch.forEach(abstractPage -> {
                    BlogPost blogPost = (BlogPost)abstractPage;
                    if (!blogPost.isDraft() || blogPost.isLatestVersion()) {
                        log.debug("Deleting blog post with ID: {}", (Object)blogPost.getId());
                        blogPost.remove(this);
                    }
                });
                totalDeleted.addAndGet(blogPostBatch.size());
                progress.setStatus(totalDeleted + " blog posts deleted");
                return true;
            });
        } while (continued.get());
    }

    @Override
    public void refreshPage(ContentEntityObject page) {
        this.pageDao.refresh(page);
    }

    @Override
    public void deepCopyPage(PageCopyOptions pageCopyOptions, Page originalPage, Page destinationPage) {
        DefaultBulkPageCopy bulkPageCopy = new DefaultBulkPageCopy(this.sessionFactory, this.contentPermissionManager, this.permissionManager, this.contentPropertyManager, this.attachmentManager, this.linksUpdater, this.lockService, this, this.spacePermissionManager, this.eventPublisher, this.contentPropertyService);
        int pageCount = this.countPagesInSubtree(originalPage);
        this.eventPublisher.publish((Object)new CopyPageHierarchyStartEvent(this, originalPage, destinationPage, pageCopyOptions.getUser(), false, pageCount, pageCopyOptions.shouldCopyAttachments(), pageCopyOptions.shouldCopyPermissions(), pageCopyOptions.shouldCopyLabels()));
        bulkPageCopy.deepCopy(pageCopyOptions, originalPage, destinationPage);
        this.eventPublisher.publish((Object)new CopyPageHierarchyFinishEvent(this, originalPage, destinationPage, pageCopyOptions.getUser(), false, pageCount, pageCopyOptions.shouldCopyAttachments(), pageCopyOptions.shouldCopyPermissions(), pageCopyOptions.shouldCopyLabels()));
    }

    @Override
    public final void deepDeletePage(PageDeleteOptions options, Page targetPage) {
        Page loadedTargetPage = Objects.requireNonNull(this.getPage(targetPage.getId()));
        DefaultBulkPageDelete bulkPageDelete = new DefaultBulkPageDelete(this.permissionManager, this.sessionFactory, this.lockService, this);
        this.eventPublisher.publish((Object)new DeletePageHierarchyStartEvent(this, loadedTargetPage, options.getUser(), false, options.getTargetPageIds().size()));
        bulkPageDelete.deepDelete(options, loadedTargetPage);
        this.eventPublisher.publish((Object)new DeletePageHierarchyFinishEvent(this, loadedTargetPage, options.getUser(), false, options.getTargetPageIds().size()));
    }

    @Override
    public int countPagesInSubtree(Page page) {
        return this.pageDao.countPagesInSubtree(page);
    }

    @Override
    public @NonNull Collection<Long> getDescendantIds(Page page) {
        return this.pageDao.getDescendantIds(page);
    }

    @Override
    public @NonNull List<Page> getTopLevelPages(Space space) {
        List<Page> topLevelPages = this.getUnsortedTopLevelPages(space);
        Collections.sort(topLevelPages, ChildPositionComparator.INSTANCE);
        return topLevelPages;
    }

    public @NonNull List<Page> getUnsortedTopLevelPages(Space space) {
        return this.pageDao.getTopLevelPages(space);
    }

    @Override
    public @NonNull ListBuilder<Page> getTopLevelPagesBuilder(Space space) {
        List<Page> allTopLevelPages = this.getTopLevelPages(space);
        ArrayList<Page> topLevelPages = new ArrayList<Page>();
        Iterator<Page> iterator = allTopLevelPages.iterator();
        while (iterator.hasNext()) {
            Page allTopLevelPage;
            Page page = allTopLevelPage = iterator.next();
            if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, page)) continue;
            topLevelPages.add(page);
        }
        return DefaultListBuilder.newInstance(new SingleListBuilderCallback(topLevelPages));
    }

    @Override
    public @NonNull PageResponse<Page> getTopLevelPages(Space space, LimitedRequest pageRequest) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getTopLevelPages(space, pageRequest), this.permissionPredicates.CAN_VIEW);
    }

    @Override
    public @NonNull PageResponse<Page> getChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getChildren(page, pageRequest, depth), this.permissionPredicates.CAN_VIEW);
    }

    @Override
    public @NonNull PageResponse<Page> getDraftChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        return this.pageDao.getDraftChildren(page, pageRequest, depth);
    }

    @Override
    public @NonNull PageResponse<Page> getAllChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        return this.pageDao.getAllChildren(page, pageRequest, depth);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int removeStaleSharedDrafts() {
        AbstractPageDaoInternal abstractPageDaoInternal = this.abstractPageDao;
        int totalStaleDraft = abstractPageDaoInternal.countStaleSharedDrafts();
        int batchSize = Integer.getInteger(STALE_DRAFT_REMOVE_BATCH_SIZE, 100);
        int numberOfBatch = (int)Math.ceil((double)totalStaleDraft / (double)batchSize);
        boolean offset = false;
        int currentBatch = numberOfBatch;
        int totalRemovedStaleDraft = 0;
        while (currentBatch > 0) {
            try {
                --currentBatch;
            }
            catch (DataAccessException exception) {
                try {
                    log.error("Could not remove stale draft with batch " + currentBatch, (Throwable)exception);
                    throw exception;
                }
                catch (Throwable throwable) {
                    --currentBatch;
                    log.info("Total removed stale draft {} over total {}, with current offset {} and batch size {}", new Object[]{totalRemovedStaleDraft, totalStaleDraft, 0, batchSize});
                    throw throwable;
                }
            }
            log.info("Total removed stale draft {} over total {}, with current offset {} and batch size {}", new Object[]{totalRemovedStaleDraft += ((Integer)this.executeInNewTransaction(() -> {
                LimitedRequest limitedRequest = LimitedRequestImpl.create((int)0, (int)batchSize, (int)batchSize);
                List<ContentEntityObject> staleSharedDraftsInner = abstractPageDaoInternal.getStaleSharedDrafts(limitedRequest);
                this.removeContentEntities(staleSharedDraftsInner);
                return staleSharedDraftsInner.size();
            })).intValue(), totalStaleDraft, 0, batchSize});
        }
        return totalRemovedStaleDraft;
    }

    @Override
    public @Nullable BlogPost findPreviousBlogPost(String key, Date time) {
        return this.blogPostDao.getFirstPostBefore(key, time);
    }

    @Override
    public @Nullable BlogPost findNextBlogPost(String key, Date time) {
        return this.blogPostDao.getFirstPostAfter(key, time);
    }

    @Override
    public void trashPage(AbstractPage page) {
        this.trashPage(page, DefaultDeleteContext.DEFAULT);
    }

    @Override
    public final void trashPage(AbstractPage page, @NonNull DeleteContext deleteContext) {
        page.trash();
        Space space = page.getSpace();
        if (page.equals(space.getHomePage())) {
            space.setHomePage(null);
        }
        this.withIndexer(indexer -> indexer.unIndexIncludingDependents(page));
        if (deleteContext.isEventSuppressed()) {
            return;
        }
        switch (page.getType()) {
            case "page": {
                this.eventPublisher.publish((Object)new PageTrashedEvent(this, (Page)page, AuthenticatedUserThreadLocal.get(), deleteContext.isSuppressNotifications()));
                break;
            }
            case "blogpost": {
                this.eventPublisher.publish((Object)new BlogPostTrashedEvent(this, (BlogPost)page, AuthenticatedUserThreadLocal.get(), deleteContext.isSuppressNotifications()));
                break;
            }
        }
    }

    private void withIndexer(Consumer<ConfluenceIndexer> task) {
        new IndexerEventPublisher(this.eventPublisher).publishCallbackEvent(task);
    }

    private void withIndexers(BiConsumer<ConfluenceIndexer, ChangeIndexer> task) {
        new IndexerEventPublisher(this.eventPublisher).publishCallbackEvent(task);
    }

    @Override
    public void restorePage(AbstractPage page) {
        page.restore();
        this.withIndexers((indexer, changeIndexer) -> {
            indexer.indexIncludingDependents(page);
            changeIndexer.reIndexAllVersions(page);
        });
        if (page.getType().equals("page")) {
            this.eventPublisher.publish((Object)new PageRestoreEvent((Object)this, (Page)page));
        } else if (page.getType().equals("blogpost")) {
            this.eventPublisher.publish((Object)new BlogPostRestoreEvent((Object)this, (BlogPost)page));
        }
    }

    @Override
    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    public void movePageToTopLevel(Page oldPage, Space destinationSpace) {
        long pageId = oldPage.getId();
        long spaceId = destinationSpace.getId();
        Page page = Objects.requireNonNull(this.getPage(pageId));
        Integer oldPosition = page.getPosition();
        Space oldSpace = page.getSpace();
        Page oldParent = page.getParent();
        List<Page> descendants = page.getDescendants();
        this.updatePageInAncestorCollections(page, null);
        Space space = this.spaceDao.getById(spaceId);
        this.movePageToSpaceSkipReIndex(page, space);
        this.setPositionIfNeeded(page, this.getTopLevelPages(space));
        if (oldParent != null) {
            oldParent.removeChild(page);
            this.pageDao.saveRaw(oldParent);
        }
        this.reIndexPages(descendants);
        this.publishPageMoveEvents(page, oldSpace, oldParent, oldPosition);
    }

    @Override
    public void moveBlogPostToTopLevel(BlogPost blogPost, Space space) {
        Space oldSpace = blogPost.getSpace();
        this.moveBlogPostToSpace(blogPost, space);
        this.publishBlogPostMoveEvent(blogPost, oldSpace, space);
    }

    private void setPositionIfNeeded(Page sourcePage, List<Page> siblingsToBe) {
        if (this.isOrdered(siblingsToBe)) {
            sourcePage.setPosition(siblingsToBe.size());
        }
    }

    @Override
    public void movePageAfter(Page page, Page refPage) {
        Runnable action = () -> this.executeUnderLocks(page.getSpaceKey(), refPage.getSpaceKey(), () -> this.doMovePageBeforeOrAfter(page, refPage, "below"));
        this.executeWithLogging(action, String.format("move page %s %s %s", page.getId(), "below", refPage.getId()));
    }

    @Override
    public void movePageBefore(Page page, Page refPage) {
        Runnable action = () -> this.executeUnderLocks(page.getSpaceKey(), refPage.getSpaceKey(), () -> this.doMovePageBeforeOrAfter(page, refPage, "above"));
        this.executeWithLogging(action, String.format("move page %s %s %s", page.getId(), "above", refPage.getId()));
    }

    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    private void doMovePageBeforeOrAfter(Page source, Page target, String position) {
        long sourcePageId = source.getId();
        long targetPageId = target.getId();
        Page sourcePage = Objects.requireNonNull(this.getPage(sourcePageId));
        Page targetPage = Objects.requireNonNull(this.getPage(targetPageId));
        Space oldSpace = sourcePage.getSpace();
        Page oldParent = sourcePage.getParent();
        Integer oldPosition = sourcePage.getPosition();
        List<Page> descendants = sourcePage.getDescendants();
        this.updatePageInAncestorCollections(sourcePage, targetPage.getParent());
        this.movePageToSpaceSkipReIndex(sourcePage, targetPage.getSpace());
        TreeSet<Page> updatedPages = new TreeSet<Page>();
        updatedPages.add(targetPage);
        updatedPages.add(sourcePage);
        Page newParent = targetPage.getParent();
        sourcePage.setParentPage(newParent);
        if (oldParent != null && !oldParent.equals(newParent)) {
            oldParent.removeChild(sourcePage);
            updatedPages.add(oldParent);
        }
        if (newParent != null && !newParent.equals(oldParent)) {
            newParent.addChild(sourcePage);
            updatedPages.add(newParent);
        }
        this.setTargetSiblingPositions(sourcePage, targetPage, position);
        this.saveUpdatedPages(updatedPages);
        this.reIndexPages(descendants);
        this.publishPageMoveEvents(sourcePage, oldSpace, oldParent, oldPosition);
    }

    private void setTargetSiblingPositions(Page sourcePage, Page targetPage, String position) {
        List<Page> targetSiblings = this.getSiblings(targetPage);
        targetSiblings.remove(sourcePage);
        this.applyPositions(targetSiblings, true);
        int offset = position.equals("above") ? 0 : 1;
        int newPosition = targetPage.getPosition() + offset;
        targetSiblings.add(newPosition, sourcePage);
        this.applyPositions(targetSiblings, false);
    }

    private void applyPositions(List<Page> siblings, boolean sort) {
        if (sort) {
            siblings.sort(ChildPositionComparator.INSTANCE);
        }
        int position = 0;
        for (Page page : siblings) {
            page.setPosition(position++);
        }
    }

    private @NonNull List<Page> getSiblings(Page page) {
        if (page.getParent() == null) {
            return this.getUnsortedTopLevelPages(page.getSpace());
        }
        return page.getParent().getChildren();
    }

    private boolean isOrdered(List<Page> pages) {
        if (pages.isEmpty()) {
            return false;
        }
        HashSet<Integer> positions = new HashSet<Integer>(pages.size());
        for (Page page : pages) {
            Integer position = page.getPosition();
            if (position == null) {
                return false;
            }
            if (positions.add(position)) continue;
            log.info("Detected duplicate page position. Treating pages as unordered.");
            return false;
        }
        return true;
    }

    @Override
    public void movePageAsChild(Page page, Page newParent) {
        Runnable action = () -> this.executeUnderLocks(page.getSpaceKey(), newParent.getSpaceKey(), () -> this.doMovePageAsChild(page.getId(), newParent.getId()));
        this.executeWithLogging(action, String.format("move page %s as child of %s", page.getId(), newParent.getId()));
    }

    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    private void doMovePageAsChild(long pageId, long newParentId) {
        Page page = Objects.requireNonNull(this.getPage(pageId));
        Page newParent = Objects.requireNonNull(this.getPage(newParentId));
        this.verifyPageHierarchy(page, newParent);
        Space oldSpace = page.getSpace();
        Page oldParent = page.getParent();
        Integer oldPosition = page.getPosition();
        List<Page> descendants = this.getDescendants(page);
        this.updatePageInAncestorCollections(page, newParent);
        this.movePageToSpaceSkipReIndex(page, newParent.getSpace());
        TreeSet<Page> updatedPages = new TreeSet<Page>();
        updatedPages.add(newParent);
        updatedPages.add(page);
        if (oldParent != null) {
            updatedPages.add(oldParent);
            oldParent.removeChild(page);
        }
        List<Page> targetSiblings = newParent.getChildren();
        boolean targetOrdered = this.isOrdered(targetSiblings);
        page.setPosition(null);
        newParent.addChild(page);
        if (targetOrdered) {
            page.setPosition(this.getMaxPosition(targetSiblings, 0) + 1);
        }
        this.saveUpdatedPages(updatedPages);
        this.reIndexPages(descendants);
        this.publishPageMoveEvents(page, oldSpace, oldParent, oldPosition);
    }

    private Integer getMaxPosition(List<Page> pages, int defaultValue) {
        return pages.stream().map(Page::getPosition).filter(Objects::nonNull).max(Integer::compare).orElse(defaultValue);
    }

    private void verifyPageHierarchy(Page page, Page newParent) {
        List<Page> ancestors = newParent.getAncestors();
        List ancestorIds = ancestors.stream().map(EntityObject::getId).collect(Collectors.toList());
        Preconditions.checkArgument((!ancestorIds.contains(page.getId()) ? 1 : 0) != 0, (String)"Illegal move of parent [%s] under it's child [%s].", (long)page.getId(), (long)newParent.getId());
    }

    @Override
    public void moveChildrenToNewParent(Page oldPage, Page newParent) {
        Runnable action = () -> this.executeUnderLocks(oldPage.getSpaceKey(), newParent.getSpaceKey(), () -> this.doMoveChildrenToNewParent(oldPage, newParent));
        this.executeWithLogging(action, String.format("move child %s to parent %s", oldPage.getId(), newParent.getId()));
    }

    private void doMoveChildrenToNewParent(Page oldPage, Page newParent) {
        ArrayList<Page> children = new ArrayList<Page>(oldPage.getChildren());
        if (children.contains(newParent)) {
            return;
        }
        TreeSet<Page> updatedPages = new TreeSet<Page>();
        updatedPages.add(newParent);
        updatedPages.add(oldPage);
        Space oldSpace = oldPage.getSpace();
        ArrayList<Page> descendentPages = new ArrayList<Page>();
        HashMap<String, Integer> childrenPositionMap = new HashMap<String, Integer>();
        for (Page page : children) {
            childrenPositionMap.put(page.getIdAsString(), page.getPosition());
            descendentPages.addAll(this.getDescendants(page));
            this.updatePageInAncestorCollections(page, newParent);
            this.movePageToSpaceSkipReIndex(page, newParent.getSpace());
            oldPage.removeChild(page);
            newParent.addChild(page);
            updatedPages.add(page);
            page.setPosition(null);
        }
        List<Page> targetSiblings = newParent.getChildren();
        boolean targetOrdered = this.isOrdered(targetSiblings);
        if (targetOrdered) {
            this.applyPositions(targetSiblings, true);
            updatedPages.addAll(targetSiblings);
        }
        this.saveUpdatedPages(updatedPages);
        this.reIndexPages(descendentPages);
        for (Page page : children) {
            this.publishPageMoveEvents(page, oldSpace, oldPage, (Integer)childrenPositionMap.get(page.getIdAsString()));
        }
    }

    @Override
    public void setChildPageOrder(Page parentPage, List<Long> childIds) {
        List<Page> oldSortedChildren = parentPage.getSortedChildren();
        ArrayList<Page> newSortedChildren = new ArrayList<Page>(childIds.size());
        for (int newPosition = 0; newPosition < childIds.size(); ++newPosition) {
            long id = childIds.get(newPosition);
            Page childPage = (Page)this.abstractPageDao.getAbstractPageById(id);
            childPage.setPosition(newPosition);
            this.pageDao.saveRaw(childPage);
            newSortedChildren.add(childPage);
        }
        this.eventPublisher.publish((Object)new PageChildrenReorderEvent(this, parentPage, oldSortedChildren, newSortedChildren, AuthenticatedUserThreadLocal.get()));
    }

    @Override
    public void revertChildPageOrder(Page parentPage) {
        List<Page> oldSortedChildren = parentPage.getSortedChildren();
        for (Page childPage : oldSortedChildren) {
            childPage.setPosition(null);
            this.pageDao.saveRaw(childPage);
        }
        List<Page> newSortedChildren = parentPage.getSortedChildren();
        this.eventPublisher.publish((Object)new PageChildrenReorderEvent(this, parentPage, oldSortedChildren, newSortedChildren, AuthenticatedUserThreadLocal.get()));
    }

    @Override
    public @NonNull List<ContentEntityObject> getOrderedXhtmlContentFromContentId(long startContentId, long endContentId, int maxRows) {
        return this.abstractPageDao.getOrderedXhtmlContentFromContentId(startContentId, endContentId, maxRows);
    }

    @Override
    public int getCountOfLatestXhtmlContent(long endContentId) {
        return this.abstractPageDao.getCountOfLatestXhtmlContent(endContentId);
    }

    @Override
    public long getHighestCeoId() {
        return this.abstractPageDao.getHighestCeoId();
    }

    @Override
    public @NonNull List<ContentEntityObject> getPreviousVersionsOfPageWithTaskId(long pageId, long taskId, int maxRows) {
        return this.abstractPageDao.getPreviousVersionsOfPageWithTaskId(pageId, taskId, maxRows);
    }

    private void moveCustomContentToTargetSpace(ContentEntityObject ceo, Space targetSpace) {
        for (CustomContentEntityObject cceo : ceo.getCustomContent()) {
            if (cceo.getSpace() != null) {
                cceo.setSpace(targetSpace);
            }
            this.moveCustomContentToTargetSpace(cceo, targetSpace);
        }
    }

    private void movePageToSpaceSkipReIndex(Page sourcePage, Space targetSpace) {
        if (sourcePage.getSpace().equals(targetSpace)) {
            return;
        }
        sourcePage.setSpace(targetSpace);
        Page draft = (Page)this.findDraftFor(sourcePage.getId());
        if (draft != null) {
            draft.setSpace(targetSpace);
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        List children = this.getAllChildren(sourcePage, LimitedRequestImpl.create((int)0x7FFFFFFE), Depth.ROOT).getResults();
        for (Page child : children) {
            if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, child)) {
                List<Page> siblingsToBe = this.getTopLevelPages(child.getSpace());
                this.updatePageInAncestorCollections(child, null);
                sourcePage.removeChild(child);
                this.setPositionIfNeeded(child, siblingsToBe);
                continue;
            }
            this.movePageToSpaceSkipReIndex(child, targetSpace);
        }
        this.moveCustomContentToTargetSpace(sourcePage, targetSpace);
        this.pageDao.saveRaw(sourcePage);
    }

    private void reIndexPages(List<Page> descendants) {
        this.withIndexer(indexer -> {
            for (Page descendant : descendants) {
                indexer.reIndex(descendant);
            }
        });
    }

    private void moveBlogPostToSpace(BlogPost sourceBlogPost, Space targetSpace) {
        if (sourceBlogPost.getSpace().equals(targetSpace)) {
            return;
        }
        sourceBlogPost.setSpace(targetSpace);
        BlogPost draft = (BlogPost)this.findDraftFor(sourceBlogPost.getId());
        if (draft != null) {
            draft.setSpace(targetSpace);
        }
        this.pageDao.saveRaw(sourceBlogPost);
    }

    private void saveUpdatedPages(Collection<Page> updatedPages) {
        for (Page updatedPage : updatedPages) {
            this.pageDao.saveRaw(updatedPage);
        }
    }

    @Override
    public @Nullable AbstractPage getPageByVersion(AbstractPage mostRecentPage, int version) {
        return (AbstractPage)this.getOtherVersion(mostRecentPage, version);
    }

    @Override
    public @Nullable BlogPost getBlogPost(String spaceKey, String postTitle, Calendar day) {
        return this.getBlogPost(spaceKey, postTitle, day, false);
    }

    @Override
    public @Nullable BlogPost getBlogPost(String spaceKey, String postTitle, Calendar day, boolean eagerLoadComments) {
        Space space = this.spaceDao.getSpace(spaceKey);
        if (space == null || postTitle == null || day == null) {
            return null;
        }
        return this.blogPostDao.getBlogPost(space, postTitle, day, eagerLoadComments);
    }

    @Override
    public @Nullable BlogPost getNewestBlogPost(@Nullable String spaceKey) {
        List newest = this.getRecentlyAddedBlogPosts(1, spaceKey);
        return newest.size() < 1 ? null : (BlogPost)newest.get(0);
    }

    @Override
    public @NonNull List getRecentlyAddedBlogPosts(int maxCount, @Nullable String spaceKey) {
        return this.blogPostDao.getRecentlyAddedBlogPosts(maxCount, spaceKey);
    }

    @Override
    public @NonNull List getRecentlyAddedPages(int maxCount, @Nullable String spaceKey) {
        return this.pageDao.getRecentlyAddedPages(maxCount, spaceKey);
    }

    @Override
    public @NonNull List getRecentlyUpdatedPages(int maxCount, @Nullable String spaceKey) {
        return this.pageDao.getRecentlyUpdatedPages(maxCount, spaceKey);
    }

    @Override
    public @NonNull List getOrphanedPages(@Nullable String spaceKey) {
        return this.pageDao.getOrphanedPages(spaceKey);
    }

    @Override
    public @NonNull List<OutgoingLink> getUndefinedPages(@Nullable String spaceKey) {
        return this.getUndefinedLinks(spaceKey);
    }

    @Override
    public final List<OutgoingLink> getUndefinedLinks(@Nullable String spaceKey) {
        List<OutgoingLink> result = this.pageDao.getUndefinedLinks(spaceKey);
        return this.filterOutEscapedLinks(this.filterOutProfileLinks(result));
    }

    private List<OutgoingLink> filterOutEscapedLinks(List<OutgoingLink> result) {
        return result.stream().filter(outgoingLink -> !outgoingLink.getDestinationPageTitle().endsWith("\\")).collect(Collectors.toList());
    }

    private List<OutgoingLink> filterOutProfileLinks(List<OutgoingLink> result) {
        return result.stream().filter(outgoingLink -> !outgoingLink.getDestinationPageTitle().startsWith("~")).collect(Collectors.toList());
    }

    @Override
    public @NonNull List getPermissionPages(Space space) {
        return this.pageDao.getPermissionPages(space);
    }

    @Override
    public Collection<Page> getPermissionPages(Space space, LimitedRequest limitedRequest) {
        return this.pageDao.getPermissionPages(space, limitedRequest);
    }

    @Override
    public long getPermissionPagesCount(Space space) {
        return this.pageDao.getPermissionPagesCount(space);
    }

    @Override
    public void saveContentEntity(ContentEntityObject obj, @Nullable SaveContext saveContext) {
        if (!(obj instanceof AbstractPage)) {
            super.saveContentEntity(obj, saveContext);
            return;
        }
        AbstractPage abstractPage = (AbstractPage)obj;
        if (!abstractPage.isDraft()) {
            Page page;
            this.throwIfDuplicateAbstractPageTitle(abstractPage);
            if (abstractPage instanceof Page && (page = (Page)abstractPage).isNew() && page.getPosition() == null) {
                this.setPagePosition(page);
            }
        }
        Optional<Date> lastUpdatedDate = Optional.ofNullable(obj.getLastModificationDate());
        this.reconciliationManager.handleContentUpdateBeforeSave(abstractPage, saveContext);
        super.saveContentEntity(abstractPage, saveContext);
        this.reconciliationManager.handleContentUpdateAfterSave(abstractPage, saveContext, lastUpdatedDate);
    }

    private void setPagePosition(Page page) {
        Integer maxSiblingPosition = this.pageDao.getMaxSiblingPosition(page);
        if (maxSiblingPosition == null) {
            return;
        }
        page.setPosition(maxSiblingPosition + 1);
    }

    @Override
    public void saveContentEntity(ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
        AbstractPage page;
        Optional<Date> lastUpdatedDate = Optional.ofNullable(obj.getLastModificationDate());
        if (obj instanceof AbstractPage) {
            page = (AbstractPage)obj;
            AbstractPage origPage = (AbstractPage)origObj;
            if (!(origPage == null || page.getSpaceKey().equals(origPage.getSpaceKey()) && page.getTitle().equals(origPage.getTitle()))) {
                this.throwIfDuplicateAbstractPageTitle(page);
            }
            this.reconciliationManager.handleContentUpdateBeforeSave(page, saveContext);
        }
        super.saveContentEntity(obj, origObj, saveContext);
        if (obj instanceof AbstractPage) {
            page = (AbstractPage)obj;
            this.reconciliationManager.handleContentUpdateAfterSave(page, saveContext, lastUpdatedDate);
        }
        if (this.contentPropertyManager.getTextProperty(obj, EXCERPT_KEY) != null) {
            this.contentPropertyManager.setTextProperty(obj, EXCERPT_KEY, "");
        }
    }

    @Override
    public <T extends ContentEntityObject> void saveNewVersion(T current, Modification<T> modification, @Nullable SaveContext saveContext) {
        if (current instanceof AbstractPage && this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(((AbstractPage)current).getSpaceKey()) && current.isDraft()) {
            throw new RuntimeException((Throwable)new AtlassianCoreException("This method shouldn't be called on draft pages when shared-drafts is enabled."));
        }
        super.saveNewVersion(current, modification, saveContext);
    }

    @Override
    public void reconcileIfNeeded(AbstractPage content, @Nullable SaveContext saveContext) {
        this.reconciliationManager.reconcileIfNeeded(content, saveContext);
    }

    private void throwIfDuplicateAbstractPageTitle(AbstractPage page) {
        AbstractPage duplicateDetection;
        if (page instanceof BlogPost) {
            Calendar publishDateCalendar = Calendar.getInstance();
            if (page.getCreationDate() != null) {
                publishDateCalendar.setTime(page.getCreationDate());
            }
            duplicateDetection = this.getBlogPost(page.getSpaceKey(), page.getTitle(), publishDateCalendar);
        } else {
            duplicateDetection = this.getPage(page.getSpaceKey(), page.getTitle());
        }
        if (duplicateDetection != null && duplicateDetection.getId() != page.getId() && page.isCurrent()) {
            log.error("Cannot save page with ID [{}] and title [{}] in space [{}], since page with ID [{}] already exists in that space with the same title", new Object[]{page.getId(), page.getTitle(), page.getSpaceKey(), duplicateDetection.getId()});
            throw new DuplicateDataRuntimeException("title", "A page already exists with the title " + page.getTitle() + " in the space with key " + page.getSpaceKey());
        }
    }

    private static DefaultContentEntityManager.EventFactory eventFactory() {
        return new DefaultContentEntityManager.EventFactory(){

            @Override
            public Optional<?> newCreateEvent(Object source, ContentEntityObject obj, @Nullable SaveContext saveContext) {
                if (obj instanceof Page) {
                    return Optional.of(new PageCreateEvent(source, (Page)obj, Collections.emptyMap(), saveContext));
                }
                if (obj instanceof BlogPost) {
                    return Optional.of(new BlogPostCreateEvent(source, (BlogPost)obj, Collections.emptyMap(), saveContext));
                }
                return Optional.empty();
            }

            @Override
            public Optional<?> newUpdateEvent(Object source, ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
                if (obj instanceof Page) {
                    return Optional.of(new PageUpdateEvent(source, (Page)obj, (Page)origObj, saveContext));
                }
                if (obj instanceof BlogPost) {
                    return Optional.of(new BlogPostUpdateEvent(source, (BlogPost)obj, (BlogPost)origObj, saveContext));
                }
                return Optional.empty();
            }

            @Override
            public Optional<?> newRemoveEvent(Object source, ContentEntityObject obj) {
                if (obj instanceof Page) {
                    return Optional.of(new PageRemoveEvent(source, (Page)obj));
                }
                if (obj instanceof BlogPost) {
                    return Optional.of(new BlogPostRemoveEvent(source, (BlogPost)obj));
                }
                return Optional.empty();
            }
        };
    }

    private void publishPageMoveEvents(Page movedPage, Space oldSpace, @Nullable Page oldParent, @Nullable Integer oldPosition) {
        boolean differentSpace;
        ArrayList movedPageList = Lists.newArrayList((Object[])new Page[]{movedPage});
        if (movedPage.hasChildren()) {
            movedPageList.addAll(this.getDescendants(movedPage));
        }
        PageMoveEvent pageMoveEvent = new PageMoveEvent((Object)this, movedPage, movedPageList, oldSpace, oldParent, oldPosition, (User)AuthenticatedUserThreadLocal.get(), false);
        this.updateAttachmentsOnFilesystemOnPageMoveHandler.handlePageMovedEvent(pageMoveEvent);
        this.eventPublisher.publish((Object)pageMoveEvent);
        this.eventPublisher.publish((Object)new PageMoveCompletedEvent(movedPage, movedPageList, oldSpace));
        boolean bl = differentSpace = !Objects.equals(movedPage.getSpaceKey(), oldSpace.getKey());
        if (differentSpace || this.permissionsAreDifferentOnParentPages(oldParent, movedPage.getParent())) {
            this.eventPublisher.publish((Object)new ContentPermissionEvent(this, movedPage, null));
        }
    }

    private boolean permissionsAreDifferentOnParentPages(@Nullable Page oldParent, @Nullable Page newParent) {
        TreeSet oldPermissions = Sets.newTreeSet();
        if (oldParent != null) {
            List<ContentPermissionSet> oldPermissionSets = this.contentPermissionManager.getContentPermissionSets(oldParent, "View");
            oldPermissionSets.forEach(ps -> oldPermissions.addAll(ps.getAllExcept(Collections.emptySet())));
        }
        TreeSet newPermissions = Sets.newTreeSet();
        if (newParent != null) {
            List<ContentPermissionSet> newPermissionSets = this.contentPermissionManager.getContentPermissionSets(newParent, "View");
            newPermissionSets.forEach(ps -> newPermissions.addAll(ps.getAllExcept(Collections.emptySet())));
        }
        return !oldPermissions.equals(newPermissions);
    }

    private void publishBlogPostMoveEvent(BlogPost movedBlogPost, Space oldSpace, Space newSpace) {
        BlogPostMovedEvent blogPostMovedEvent = new BlogPostMovedEvent(this, AuthenticatedUserThreadLocal.get(), movedBlogPost, oldSpace, newSpace);
        this.updateAttachmentsOnFilesystemOnPageMoveHandler.handleBlogPostMovedEvent(blogPostMovedEvent);
        this.eventPublisher.publish((Object)blogPostMovedEvent);
        this.eventPublisher.publish((Object)new ContentPermissionEvent(this, movedBlogPost, null));
    }

    @Override
    public int getAuthoredPagesCountByUser(String username) {
        return this.pageDao.getAuthoredPagesCountByUser(username);
    }

    @Override
    public boolean isPageRecentlyUpdatedForUser(Page page, @Nullable User user) {
        if (user == null) {
            return false;
        }
        LoginInfo loginInfo = this.loginManager.getLoginInfo(user);
        if (loginInfo == null) {
            return false;
        }
        Date lastLogin = loginInfo.getPreviousSuccessfulLoginDate();
        if (lastLogin == null) {
            return false;
        }
        if (page.getLastModificationDate() != null) {
            return lastLogin.before(page.getLastModificationDate());
        }
        return page.getCreationDate() != null && lastLogin.before(page.getCreationDate());
    }

    @Override
    public void revertContentEntityBackToVersion(ContentEntityObject entity, int version, @Nullable String revertComment, boolean revertTitle) {
        this.revertContentEntityBackToVersion(entity, version, revertComment, revertTitle, this::getBodyContentForRevert);
    }

    private @NonNull BodyContent getBodyContentForRevert(ContentEntityObject historicalVersion) {
        BodyContent unmigratedBodyContent = super.convertFromWikiToStorageFormatIfRequired(historicalVersion);
        ExceptionTolerantMigrator.MigrationResult migrationResult = this.revertedContentMigrator.migrate(unmigratedBodyContent.getBody(), new DefaultConversionContext(historicalVersion.toPageContext()));
        if (!migrationResult.getExceptions().isEmpty()) {
            DefaultPageManager.rethrowMigrationExceptions(migrationResult.getExceptions(), historicalVersion);
        }
        unmigratedBodyContent.setBody(migrationResult.getContent());
        return unmigratedBodyContent;
    }

    private static void rethrowMigrationExceptions(List<RuntimeException> migrationExceptions, ContentEntityObject historicalVersion) {
        RuntimeException firstException = migrationExceptions.iterator().next();
        if (migrationExceptions.size() > 1) {
            throw new RuntimeException(migrationExceptions.size() + " failures occured during migration of reverted " + historicalVersion + "; rethrowing the first one", firstException);
        }
        throw new RuntimeException("Failure occured during migration of reverted " + historicalVersion, firstException);
    }

    @Override
    public @NonNull List getPagesCreatedOrUpdatedSinceDate(Date previousLoginDate) {
        return this.pageDao.getPagesCreatedOrUpdatedSinceDate(previousLoginDate);
    }

    @Override
    public @NonNull List getBlogPosts(String spaceKey, Calendar postingDate, int period) {
        Space space = this.spaceDao.getSpace(spaceKey);
        if (space == null || postingDate == null) {
            return Collections.emptyList();
        }
        return this.blogPostDao.getBlogPosts(space, postingDate, period);
    }

    @Override
    public @NonNull List getBlogPosts(String spaceKey, Calendar postingDate, int period, int startIndex, int maxResultCount) {
        Space space = this.spaceDao.getSpace(spaceKey);
        if (space == null || postingDate == null) {
            return Collections.emptyList();
        }
        return this.blogPostDao.getBlogPosts(space, postingDate, period, startIndex, maxResultCount);
    }

    @Override
    public long getBlogPostCount(String spaceKey, @Nullable Calendar postingDate, int period) {
        return this.blogPostDao.getBlogPostCount(spaceKey, postingDate, period);
    }

    @Override
    public long getPageCount(@NonNull String spaceKey) {
        return this.pageDao.getPageCount(spaceKey);
    }

    @Override
    public int countCurrentPages() {
        return this.pageDao.countCurrentPages();
    }

    @Override
    public int countDraftPages() {
        return this.pageDao.countDraftPages();
    }

    @Override
    public int countPagesWithUnpublishedChanges() {
        return this.pageDao.countPagesWithUnpublishedChanges();
    }

    @Override
    public Optional<PageStatisticsDTO> getPageStatistics() {
        return this.pageDao.getPageStatistics();
    }

    @Override
    public int countCurrentBlogs() {
        return this.blogPostDao.countCurrentBlogs();
    }

    @Override
    public int countDraftBlogs() {
        return this.blogPostDao.countDraftBlogs();
    }

    @Override
    public int countBlogsWithUnpublishedChanges() {
        return this.blogPostDao.countBlogsWithUnpublishedChanges();
    }

    @Override
    public Optional<BlogPostStatisticsDTO> getBlogStatistics() {
        return this.blogPostDao.getBlogPostStatistics();
    }

    @Override
    public @NonNull Set<Date> getYearsWithBlogPosts(String spaceKey) {
        Space space = this.spaceDao.getSpace(spaceKey);
        return space == null ? Collections.emptySet() : this.blogPostDao.getBlogPostDates(space).stream().map(date -> DateUtils.truncate((Date)date, (int)1)).collect(Collectors.toSet());
    }

    @Override
    public @NonNull List<Date> getBlogPostDates(String spaceKey) {
        Space space = this.spaceDao.getSpace(spaceKey);
        return space == null ? Collections.emptyList() : this.blogPostDao.getBlogPostDates(space);
    }

    @Override
    public @NonNull List<Date> getBlogPostDates(String spaceKey, Calendar date, int period) {
        return this.blogPostDao.getBlogPostDates(spaceKey, date, period);
    }

    @Override
    public @NonNull Set<Date> getMonthsWithBlogPosts(String spaceKey, Calendar year) {
        HashSet<Date> months = new HashSet<Date>();
        for (Date date : this.blogPostDao.getBlogPostDates(spaceKey, year, 1)) {
            months.add(DateUtils.truncate((Date)date, (int)2));
        }
        return months;
    }

    @Override
    public @NonNull List<Page> getPages(@Nullable Space space, boolean currentOnly) {
        return this.pageDao.getPages(space, currentOnly);
    }

    @Override
    public @NonNull List<Page> getPagesWithPermissions(Space space) {
        return this.pageDao.getPagesWithPermissions(space);
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<Page> getFilteredPages(Space space, LimitedRequest pageRequest, Predicate<? super Page> ... filters) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getPages(space, pageRequest), this.andCanViewPredicate(filters));
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<Page> getFilteredPages(LimitedRequest pageRequest, Predicate<? super Page> ... filters) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getPages(pageRequest), this.andCanViewPredicate(filters));
    }

    @Override
    public @NonNull PageResponse<Page> scanFilteredPages(List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super Page> ... filter) {
        Objects.requireNonNull(pageRequest.getCursor());
        return DefaultPageManager.filteredResponseWithCursor(pageRequest, this.andCanViewPredicate(filter), this.pageDao.scanFilteredPages(statuses, pageRequest));
    }

    @Override
    public @NonNull PageResponse<Page> scanFilteredPages(Space space, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super Page> ... filter) {
        Objects.requireNonNull(pageRequest.getCursor());
        return DefaultPageManager.filteredResponseWithCursor(pageRequest, this.andCanViewPredicate(filter), this.pageDao.scanFilteredPages(space, statuses, pageRequest));
    }

    @Override
    @Deprecated
    public @NonNull PageResponse<BlogPost> getFilteredBlogPosts(Space space, LimitedRequest pageRequest, Predicate<? super BlogPost> ... filters) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getBlogPosts(space, pageRequest), this.andCanViewPredicate(filters));
    }

    private <T> Predicate<? super T> andCanViewPredicate(Predicate<? super T> ... filters) {
        ArrayList filterList = new ArrayList(List.of(filters));
        filterList.add(this.permissionPredicates.CAN_VIEW);
        return arg -> filterList.stream().allMatch(p -> p.test(arg));
    }

    @Override
    public @NonNull Collection<Long> getPageIds(Space space) {
        return this.pageDao.getPageIds(space);
    }

    @Override
    public @NonNull List getPagesStartingWith(Space space, String s) {
        return this.pageDao.getPagesStartingWith(space, s);
    }

    @Override
    public @Nullable BlogPost findPreviousBlogPost(BlogPost post) {
        return this.blogPostDao.getFirstPostBefore(post);
    }

    @Override
    public @Nullable BlogPost findNextBlogPost(BlogPost post) {
        return this.blogPostDao.getFirstPostAfter(post);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void executeUnderLocks(String k1, String k2, Runnable operation) {
        ArrayList lockArray = Lists.newArrayList((Iterable)ImmutableSortedSet.of((Comparable)((Object)(LOCK_PREFIX + "." + k1)), (Comparable)((Object)(LOCK_PREFIX + "." + k2))));
        Pair lockA = Pair.pair((Object)((String)lockArray.get(0)), (Object)this.lockService.getLockForName((String)lockArray.get(0)));
        Option maybeLockB = lockArray.size() != 1 ? Option.some((Object)Pair.pair((Object)((String)lockArray.get(1)), (Object)this.lockService.getLockForName((String)lockArray.get(1)))) : Option.none();
        try {
            if (!((ClusterLock)lockA.right()).tryLock(5L, TimeUnit.MINUTES)) {
                log.error("Failed to acquire lock #1: [{}]", lockA.left());
                return;
            }
            try {
                if (maybeLockB.isDefined() && !((ClusterLock)((Pair)maybeLockB.get()).right()).tryLock(5L, TimeUnit.MINUTES)) {
                    log.error("Failed to acquire lock #2: [{}]", (Object)maybeLockB.map(Pair::left));
                    return;
                }
                try {
                    operation.run();
                }
                finally {
                    if (maybeLockB.isDefined()) {
                        ((ClusterLock)((Pair)maybeLockB.get()).right()).unlock();
                    }
                }
            }
            finally {
                ((ClusterLock)lockA.right()).unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("Thread is interrupted either before or during page move. Locks keys: {} and {}", new Object[]{lockA.left(), maybeLockB.map(Pair::left), e});
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void executeWithLogging(Runnable action, String actionDescription) {
        String pageMoveUuid = UUID.randomUUID().toString();
        log.info("{} Started [{}]", (Object)pageMoveUuid, (Object)actionDescription);
        long started = System.currentTimeMillis();
        try {
            action.run();
        }
        catch (Throwable throwable) {
            log.info("{} Finished [{}] in [{}] ms", new Object[]{pageMoveUuid, actionDescription, System.currentTimeMillis() - started});
            throw throwable;
        }
        log.info("{} Finished [{}] in [{}] ms", new Object[]{pageMoveUuid, actionDescription, System.currentTimeMillis() - started});
    }

    private Object executeInNewTransaction(Callable callable) {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition);
        return transactionTemplate.execute(status -> {
            try {
                return callable.call();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private class PermissionPredicates {
        public final Predicate<Object> CAN_VIEW = this.hasPermission(Permission.VIEW);

        private PermissionPredicates() {
        }

        public com.google.common.base.Predicate<Object> hasPermission(final Permission permission) {
            return new com.google.common.base.Predicate<Object>(){

                public boolean apply(@Nullable Object page) {
                    return DefaultPageManager.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), permission, page);
                }

                public String toString() {
                    return "CAN " + Permission.VIEW;
                }
            };
        }
    }
}

