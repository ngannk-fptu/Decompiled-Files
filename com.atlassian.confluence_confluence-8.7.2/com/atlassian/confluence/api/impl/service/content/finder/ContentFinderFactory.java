/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.AttachmentContentId
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.locator.ContentLocator
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService$SingleContentFetcher
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.impl.service.content.ContentServiceImpl;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractContentFinder;
import com.atlassian.confluence.api.impl.service.content.finder.ContentLocatorFetcher;
import com.atlassian.confluence.api.impl.service.content.finder.FinderPredicates;
import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.impl.service.content.finder.PageAndBlogFetcher;
import com.atlassian.confluence.api.impl.service.content.typebinding.AttachmentContentTypeApiSupport;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.AttachmentContentId;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.locator.ContentLocator;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentFinderFactory {
    private static final Logger log = LoggerFactory.getLogger(ContentFinderFactory.class);
    private final ContentFactory contentFactory;
    private final SpaceManager spaceManager;
    private final PageManagerInternal pageManager;
    private final PaginationService paginationService;
    private final ContentEntityManagerInternal contentEntityManager;
    private final AttachmentContentTypeApiSupport attachmentContentTypeBinding;
    private final PermissionManager permissionManager;
    private final FinderProxyFactory finderProxyFactory;

    public ContentFinderFactory(ContentFactory contentFactory, PageManagerInternal pageManager, SpaceManager spaceManager, PaginationService paginationService, AttachmentContentTypeApiSupport attachmentContentTypeBinding, ContentEntityManagerInternal contentEntityManager, PermissionManager permissionManager, FinderProxyFactory finderProxyFactory) {
        this.contentFactory = contentFactory;
        this.pageManager = pageManager;
        this.paginationService = paginationService;
        this.attachmentContentTypeBinding = attachmentContentTypeBinding;
        this.contentEntityManager = contentEntityManager;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.finderProxyFactory = finderProxyFactory;
    }

    public ContentService.ContentFinder createContentFinder(ContentServiceImpl contentService, Expansion ... expansions) {
        ContentFinderImpl contentFinder = new ContentFinderImpl(contentService, expansions);
        return this.finderProxyFactory.createProxy(contentFinder, ContentService.ContentFinder.class);
    }

    private class ContentFinderImpl
    extends AbstractContentFinder {
        private final ContentServiceImpl contentService;

        ContentFinderImpl(ContentServiceImpl contentService, Expansion ... expansions) {
            super(expansions);
            this.contentService = contentService;
        }

        public ContentService.SingleContentFetcher withLocator(ContentLocator locator) {
            ContentLocatorFetcher locatorFetcher = new ContentLocatorFetcher(locator, ContentFinderFactory.this.contentFactory, ContentFinderFactory.this.pageManager, new Expansion[0]);
            return ContentFinderFactory.this.finderProxyFactory.createProxy(locatorFetcher, ContentService.SingleContentFetcher.class);
        }

        public Optional<Content> fetch() {
            if (this.getContentId() != null) {
                return this.internalFetchById(this.getContentId(), this.getVersion(), this.getStatuses(), this.expansions);
            }
            for (ContentType type : this.getContentTypes()) {
                try {
                    Iterator iterator = this.fetchMany(type, SimplePageRequest.ONE).iterator();
                    if (!iterator.hasNext()) continue;
                    Content content = (Content)iterator.next();
                    return Optional.of(content);
                }
                catch (ServiceException ex) {
                    log.debug("Converting exception in fetchMany to option.none()", (Throwable)ex);
                }
            }
            return Optional.empty();
        }

        public PageResponse<Content> fetchMany(ContentType type, PageRequest request) throws ServiceException {
            LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.content((Expansions)new Expansions(this.expansions)));
            if (!this.getContentIds().isEmpty()) {
                return ContentFinderFactory.this.paginationService.performPaginationListRequest(limitedRequest, input -> {
                    Predicate[] predicateArray = new Predicate[1];
                    predicateArray[0] = this.asPredicateWithContentType(type).and(FinderPredicates.permissionPredicate(Permission.VIEW, ContentFinderFactory.this.permissionManager))::test;
                    return ContentFinderFactory.this.contentEntityManager.getByIdsAndFilters(this.getContentIds(), (LimitedRequest)input, predicateArray);
                }, items -> ContentFinderFactory.this.contentFactory.buildFrom(items, new Expansions(this.expansions)));
            }
            if (type.equals((Object)ContentType.PAGE)) {
                return this.createPageAndBlogFetcher().fetchPages(this, limitedRequest);
            }
            if (type.equals((Object)ContentType.BLOG_POST)) {
                return this.createPageAndBlogFetcher().fetchBlogPosts(this, limitedRequest);
            }
            if (type.equals((Object)ContentType.COMMENT)) {
                return this.internalFetchComments(request);
            }
            if (type.equals((Object)ContentType.ATTACHMENT)) {
                return this.internalFetchAttachments(limitedRequest);
            }
            throw new IllegalArgumentException("Could not fetch content for unrecognized content type " + type);
        }

        private PageAndBlogFetcher createPageAndBlogFetcher() {
            return new PageAndBlogFetcher(ContentFinderFactory.this.paginationService, ContentFinderFactory.this.pageManager, ContentFinderFactory.this.contentFactory, ContentFinderFactory.this.spaceManager, ContentFinderFactory.this.permissionManager);
        }

        private PageResponse<Content> internalFetchComments(PageRequest request) throws ServiceException {
            throw new NotImplementedServiceException("Cannot fetch comments with finder");
        }

        private PageResponse<Content> internalFetchAttachments(LimitedRequest limitedRequest) throws ServiceException {
            return ContentFinderFactory.this.attachmentContentTypeBinding.getAttachments(this.getContentContainerId(), limitedRequest, (com.google.common.base.Predicate<? super Attachment>)((com.google.common.base.Predicate)this.asPredicate()::test), new Expansions(this.expansions));
        }

        public Map<ContentType, PageResponse<Content>> fetchMappedByContentType(PageRequest request) throws ServiceException {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (ContentType type : this.getContentTypes()) {
                builder.put((Object)type, this.fetchMany(type, request));
            }
            return builder.build();
        }

        private Optional<Content> internalFetchById(ContentId id, int version, List<ContentStatus> statuses, Expansion ... expansions) {
            ArrayList<ContentStatus> fetchStatuses = new ArrayList<ContentStatus>(statuses);
            if (version >= 1) {
                for (ContentStatus status : fetchStatuses) {
                    if (ContentStatus.HISTORICAL.equals((Object)status) || ContentStatus.CURRENT.equals((Object)status)) continue;
                    throw new BadRequestException("Only historical content can be fetched by version ('status=" + status.getValue() + "' cannot be used with 'version' parameter)");
                }
                if (!fetchStatuses.contains(ContentStatus.HISTORICAL)) {
                    fetchStatuses.add(ContentStatus.HISTORICAL);
                }
                if (!fetchStatuses.contains(ContentStatus.CURRENT)) {
                    fetchStatuses.add(ContentStatus.CURRENT);
                }
            } else if (fetchStatuses.contains(ContentStatus.HISTORICAL)) {
                throw new BadRequestException("Cannot fetch historical content without specifying a version");
            }
            if (id instanceof AttachmentContentId) {
                return ContentFinderFactory.this.attachmentContentTypeBinding.getById(id, statuses, new Expansions(expansions));
            }
            Object entity = Collections.singletonList(ContentStatus.DRAFT).equals(fetchStatuses) ? ContentFinderFactory.this.contentEntityManager.findDraftFor(id.asLong()) : ContentFinderFactory.this.contentEntityManager.getById(id, version);
            return Optional.ofNullable(this.contentService.buildContent((ContentEntityObject)entity, (List<ContentStatus>)fetchStatuses, expansions));
        }
    }
}

