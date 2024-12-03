/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.locator.ContentLocator
 *  com.atlassian.confluence.api.model.pagination.ContentCursor
 *  com.atlassian.confluence.api.model.pagination.CursorType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.api.util.JodaTimeUtils
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractContentFinder;
import com.atlassian.confluence.api.impl.service.content.finder.ContentLocatorFetcher;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.locator.ContentLocator;
import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.CursorType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Function;
import java.util.function.Predicate;

class PageAndBlogFetcher {
    private final PaginationService paginationService;
    private final PageManagerInternal pageManager;
    private final ContentFactory contentFactory;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;

    public PageAndBlogFetcher(PaginationService paginationService, PageManagerInternal pageManagerInternal, ContentFactory contentFactory, SpaceManager spaceManager, PermissionManager permissionManager) {
        this.paginationService = paginationService;
        this.pageManager = pageManagerInternal;
        this.contentFactory = contentFactory;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
    }

    public PageResponse<Content> fetchPages(AbstractContentFinder finder, LimitedRequest request) throws BadRequestException {
        if (finder.getContentContainerId() != null) {
            return PageResponseImpl.empty((boolean)false, (LimitedRequest)request);
        }
        if (!Strings.isNullOrEmpty((String)finder.getTitle())) {
            if (finder.getSpaces().size() == 1) {
                return this.fetchBySpaceAndTitle(ContentType.PAGE, finder, request);
            }
            return this.fetchByTitle(ContentType.PAGE, finder, request);
        }
        if (finder.getCreatedDate() != null) {
            return this.fetchByDate(ContentType.PAGE, finder, request);
        }
        if (finder.getSpaces().size() == 1) {
            return this.fetchBySpace(ContentType.PAGE, finder, request);
        }
        return this.fetchAllContent(ContentType.PAGE, finder, request);
    }

    public PageResponse<Content> fetchBlogPosts(AbstractContentFinder finder, LimitedRequest request) throws ServiceException {
        if (finder.getContentContainerId() != null) {
            return PageResponseImpl.empty((boolean)false, (LimitedRequest)request);
        }
        if (finder.getCreatedDate() != null && finder.getSpaces().size() == 1 && finder.getTitle() != null) {
            return this.fetchBlogPostBySpaceAndTitleAndDate(finder, request);
        }
        if (finder.getTitle() != null) {
            return this.fetchByTitle(ContentType.BLOG_POST, finder, request);
        }
        if (finder.getCreatedDate() != null) {
            return this.fetchByDate(ContentType.BLOG_POST, finder, request);
        }
        if (finder.getSpaces().size() == 1) {
            return this.fetchBySpace(ContentType.BLOG_POST, finder, request);
        }
        return this.fetchAllContent(ContentType.BLOG_POST, finder, request);
    }

    private PageResponse<Content> fetchBySpaceAndTitle(ContentType contentType, AbstractContentFinder finder, LimitedRequest request) {
        Space space = this.spaceManager.getSpace(finder.getSpaces().get(0).getKey());
        if (space == null) {
            return PageResponseImpl.empty((boolean)false);
        }
        return this.doPaginationRequest(request, (PaginationBatch<? extends AbstractPage>)((PaginationBatch)request1 -> this.pageManager.getFilteredAbstractPages(space, finder.getTitle(), finder.getStatuses(), (LimitedRequest)request1, (Predicate<? super AbstractPage>)finder.asPredicateWithContentType(contentType))), finder.expansions);
    }

    protected PageResponse<Content> fetchBlogPostBySpaceAndTitleAndDate(AbstractContentFinder finder, LimitedRequest request) {
        Space space = this.spaceManager.getSpace(finder.getSpaces().get(0).getKey());
        if (space == null) {
            return PageResponseImpl.empty((boolean)false);
        }
        ContentLocator locator = ContentLocator.builder().forBlog().bySpaceKeyTitleAndPostingDay(space.getKey(), finder.getTitle(), JodaTimeUtils.convert((LocalDate)finder.getCreatedDate()));
        Content blog = (Content)new ContentLocatorFetcher(locator, this.contentFactory, this.pageManager, finder.expansions).fetchOrNull();
        return blog != null && blog.getId() != null && this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, this.pageManager.getAbstractPage(blog.getId().asLong())) ? PageResponseImpl.fromSingle((Object)blog, (boolean)false).pageRequest(request).build() : PageResponseImpl.empty((boolean)false, (LimitedRequest)request);
    }

    protected PageResponse<Content> fetchByTitle(ContentType contentType, AbstractContentFinder contentFinder, LimitedRequest request) {
        return this.doPaginationRequest(request, (PaginationBatch<? extends AbstractPage>)((PaginationBatch)input -> this.pageManager.getFilteredAbstractPages(contentFinder.getTitle(), contentFinder.getStatuses(), (LimitedRequest)input, (Predicate<? super AbstractPage>)contentFinder.asPredicateWithContentType(contentType))), contentFinder.expansions);
    }

    protected PageResponse<Content> fetchByDate(ContentType contentType, AbstractContentFinder contentFinder, LimitedRequest request) {
        LocalDate day = contentFinder.getCreatedDate();
        ZoneId zone = ZoneId.systemDefault();
        return this.doPaginationRequest(request, (PaginationBatch<? extends AbstractPage>)((PaginationBatch)input -> this.pageManager.getAbstractPages(day, zone, contentFinder.getStatuses(), (LimitedRequest)input, (Predicate<? super AbstractPage>)contentFinder.asPredicateWithContentType(contentType))), contentFinder.expansions);
    }

    protected PageResponse<Content> fetchBySpace(ContentType contentType, AbstractContentFinder finder, LimitedRequest request) {
        Space space = this.spaceManager.getSpace(finder.getSpaces().get(0).getKey());
        if (space == null) {
            return PageResponseImpl.empty((boolean)false);
        }
        if (request.getCursor() == null) {
            return this.fetchBySpaceWithOffset(contentType, finder, request, space);
        }
        return this.fetchBySpaceWithCursor(contentType, finder, request, space);
    }

    private PageResponse<Content> fetchBySpaceWithOffset(ContentType contentType, AbstractContentFinder finder, LimitedRequest request, Space space) {
        return this.doPaginationRequest(request, (PaginationBatch<? extends AbstractPage>)((PaginationBatch)input -> this.pageManager.getFilteredAbstractPages(space, Lists.newArrayList((Object[])new ContentType[]{contentType}), finder.getStatuses(), (LimitedRequest)input, (Predicate<? super AbstractPage>[])new Predicate[0])), finder.expansions);
    }

    private PageResponse<Content> fetchBySpaceWithCursor(ContentType contentType, AbstractContentFinder finder, LimitedRequest request, Space space) {
        return this.doPaginationRequestWithCursor(contentType, request, limitedRequest -> this.pageManager.scanFilteredPages(space, finder.getStatuses(), (LimitedRequest)limitedRequest, (Predicate<? super Page>[])new Predicate[0]), finder.expansions);
    }

    protected PageResponse<Content> fetchAllContent(ContentType contentType, AbstractContentFinder finder, LimitedRequest request) {
        if (request.getCursor() == null) {
            return this.fetchAllContentWithOffset(contentType, finder, request);
        }
        return this.fetchAllContentWithCursor(contentType, finder, request);
    }

    private PageResponse<Content> fetchAllContentWithOffset(ContentType contentType, AbstractContentFinder finder, LimitedRequest request) {
        return this.doPaginationRequest(request, (PaginationBatch<? extends AbstractPage>)((PaginationBatch)input -> this.pageManager.getFilteredAbstractPages(Lists.newArrayList((Object[])new ContentType[]{contentType}), finder.getStatuses(), (LimitedRequest)input, (Predicate<? super AbstractPage>[])new Predicate[0])), finder.expansions);
    }

    private PageResponse<Content> fetchAllContentWithCursor(ContentType contentType, AbstractContentFinder finder, LimitedRequest request) {
        return this.doPaginationRequestWithCursor(contentType, request, limitedRequest -> this.pageManager.scanFilteredPages(finder.getStatuses(), (LimitedRequest)limitedRequest, new Predicate[0]), finder.expansions);
    }

    private void validateContentType(ContentType contentType) {
        if (contentType != ContentType.PAGE) {
            throw new IllegalArgumentException(String.format("Content type %s is not supported in cursor pagination requests", contentType));
        }
    }

    private void validateCursor(LimitedRequest request) {
        if (request.getCursor().getCursorType() != CursorType.CONTENT) {
            throw new IllegalArgumentException(String.format("Cursor type is incorrect. Received: %s, but %s was expected", request.getCursor().getCursorType(), CursorType.CONTENT));
        }
    }

    protected PageResponse<Content> doPaginationRequest(LimitedRequest request, PaginationBatch<? extends AbstractPage> paginationBatch, Expansion[] expansions) {
        return this.paginationService.performPaginationListRequest(request, paginationBatch, items -> this.contentFactory.buildFrom(items, new Expansions(expansions)));
    }

    private PageResponse<Content> doPaginationRequestWithCursor(ContentType contentType, LimitedRequest request, Function<LimitedRequest, PageResponse<Page>> visibleContentScanner, Expansion[] expansions) {
        this.validateCursor(request);
        this.validateContentType(contentType);
        return this.paginationService.performPaginationListRequestWithCursor(request, visibleContentScanner, items -> this.contentFactory.buildFrom(items, new Expansions(expansions)), PageAndBlogFetcher::calculateCursorFromContent);
    }

    private static ContentCursor calculateCursorFromContent(Page content, boolean isReverse) {
        return ContentCursor.createCursor((boolean)isReverse, (long)content.getId());
    }
}

