/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.rest.dto.StreamItem
 *  com.atlassian.confluence.plugins.rest.dto.UserDtoFactory
 *  com.atlassian.confluence.plugins.rest.manager.DateEntityFactory
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.BooleanQueryBuilder
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.DefaultSearchWithToken
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchTokenExpiredException
 *  com.atlassian.confluence.search.v2.SearchWithToken
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.CreatorQuery
 *  com.atlassian.confluence.search.v2.sort.CreatedSort
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  org.apache.commons.collections4.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.mobile.rest;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.mobile.rest.RecentContentDto;
import com.atlassian.confluence.plugins.mobile.rest.StreamResourceInterface;
import com.atlassian.confluence.plugins.rest.dto.StreamItem;
import com.atlassian.confluence.plugins.rest.dto.UserDtoFactory;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.DefaultSearchWithToken;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/stream")
public class StreamResource
implements StreamResourceInterface {
    private static final Logger log = LoggerFactory.getLogger(StreamResource.class);
    private static final int ITEMS_PER_PAGE = 10;
    private final SearchManager searchManager;
    private final TransactionTemplate transactionTemplate;
    private final UserDtoFactory userDtoFactory;
    private final DateEntityFactory dateEntityFactory;
    private final LikeManager likeManager;
    private final ContextPathHolder contextPathHolder;
    private final I18nResolver i18nResolver;
    private final NetworkService networkService;
    private final UserAccessor userAccessor;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    public StreamResource(SearchManager searchManager, TransactionTemplate transactionTemplate, UserDtoFactory userDtoFactory, DateEntityFactory dateEntityFactory, LikeManager likeManager, ContextPathHolder contextPathHolder, I18nResolver i18nResolver, NetworkService networkService, UserAccessor userAccessor, SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.searchManager = searchManager;
        this.transactionTemplate = transactionTemplate;
        this.userDtoFactory = userDtoFactory;
        this.dateEntityFactory = dateEntityFactory;
        this.likeManager = likeManager;
        this.contextPathHolder = contextPathHolder;
        this.i18nResolver = i18nResolver;
        this.networkService = networkService;
        this.userAccessor = userAccessor;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }

    @Override
    @GET
    @Path(value="/recentblogs")
    @Consumes(value={"application/json", "application/x-www-form-urlencoded"})
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public RecentContentDto getRecentlyAddedBlogs(@QueryParam(value="nextPageOffset") @DefaultValue(value="0") int nextPageOffset, @QueryParam(value="urlStrategy") @DefaultValue(value="desktop") String urlStrategy, @QueryParam(value="token") @DefaultValue(value="0") long searchToken) {
        ISearch search = this.getRecentlyAddedBlogsSearch(nextPageOffset);
        return this.getSearchResults(search, searchToken);
    }

    @Override
    @GET
    @Path(value="/network")
    @Consumes(value={"application/json", "application/x-www-form-urlencoded"})
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public RecentContentDto getRecentlyAddedFromNetwork(@QueryParam(value="nextPageOffset") @DefaultValue(value="0") int nextPageOffset, @QueryParam(value="urlStrategy") @DefaultValue(value="desktop") String urlStrategy, @QueryParam(value="token") @DefaultValue(value="0") long searchToken) {
        ISearch search = this.getRecentlyAddedFromNetworkSearch(nextPageOffset);
        return this.getSearchResults(search, searchToken);
    }

    private RecentContentDto getSearchResults(ISearch search, long searchToken) {
        RecentContentDto result = new RecentContentDto();
        List streamItems = (List)this.transactionTemplate.execute(() -> {
            SearchResults searchResults;
            try {
                searchResults = this.handleSearch(search, searchToken);
            }
            catch (InvalidSearchException e) {
                log.error("Invalid search", (Throwable)e);
                return Collections.emptyList();
            }
            List searchables = this.searchManager.convertToEntities(searchResults, SearchManager.EntityVersionPolicy.LATEST_VERSION);
            LinkedList contentEntities = searchables.stream().filter(item -> item instanceof ContentEntityObject).map(item -> (ContentEntityObject)item).collect(Collectors.toCollection(LinkedList::new));
            if (searchResults.getNextPageSearch() != null) {
                result.setNextPageOffset(searchResults.getNextPageSearch().getStartOffset());
                result.setToken(searchResults.getNextPageSearch().getSearchToken());
            }
            Map likes = this.likeManager.getLikes((Collection)contentEntities);
            return contentEntities.stream().map(contentEntity -> {
                int numberOfLikes = likes.containsKey(contentEntity.getId()) ? ((List)likes.get(contentEntity.getId())).size() : 0;
                return new StreamItem(contentEntity.getId(), contentEntity.getDisplayTitle(), this.getUrl((ContentEntityObject)contentEntity), this.userDtoFactory.getUserDto(contentEntity.getCreator()), this.dateEntityFactory.buildDateEntity(contentEntity.getCreationDate()).getFriendly(), numberOfLikes, 0);
            }).collect(Collectors.toList());
        });
        result.setStreamItems(streamItems);
        return result;
    }

    private SearchResults handleSearch(ISearch search, long searchToken) throws InvalidSearchException {
        if (searchToken > 0L) {
            try {
                return this.searchManager.search((SearchWithToken)new DefaultSearchWithToken(search, searchToken));
            }
            catch (SearchTokenExpiredException e) {
                log.debug("search token expired.", (Throwable)e);
                throw new WebApplicationException(Response.status((int)500).entity(Collections.singletonMap("message", this.i18nResolver.getText("confluence.mobile.stream.loading.token.expired"))).build());
            }
        }
        return this.searchManager.search(search);
    }

    private ISearch getRecentlyAddedBlogsSearch(int startOffset) {
        HashSet<Object> queries = new HashSet<Object>();
        queries.add(AllQuery.getInstance());
        queries.add(new ContentTypeQuery(EnumSet.of(ContentTypeEnum.BLOG)));
        SearchQuery filteredQuery = (SearchQuery)BooleanQuery.builder().addFilter(this.siteSearchPermissionsQueryFactory.create()).addMust(queries).build();
        return new ContentSearch(filteredQuery, (SearchSort)CreatedSort.DESCENDING, startOffset, 10);
    }

    private ISearch getRecentlyAddedFromNetworkSearch(int startOffset) {
        ArrayList creatorsQueries = new ArrayList();
        Optional.ofNullable(AuthenticatedUserThreadLocal.get()).ifPresent(user -> {
            PageResponse following = this.networkService.getFollowing(user.getKey(), (PageRequest)new SimplePageRequest(0, 100));
            following.getResults().stream().map(item -> this.userAccessor.getUserByName(item.getUsername())).forEach(u -> creatorsQueries.add(new CreatorQuery(u.getKey())));
        });
        HashSet<Object> queries = new HashSet<Object>();
        queries.add(AllQuery.getInstance());
        queries.add(new ContentTypeQuery(EnumSet.of(ContentTypeEnum.PAGE, ContentTypeEnum.BLOG, ContentTypeEnum.COMMENT)));
        BooleanQueryBuilder queryBuilder = BooleanQuery.builder().addFilter(this.siteSearchPermissionsQueryFactory.create()).addMust(queries);
        if (CollectionUtils.isNotEmpty(creatorsQueries)) {
            queryBuilder.addShould(creatorsQueries);
        }
        return new ContentSearch((SearchQuery)queryBuilder.build(), (SearchSort)CreatedSort.DESCENDING, startOffset, 10);
    }

    private String getUrl(ContentEntityObject contentEntity) {
        if (contentEntity instanceof Comment) {
            Comment comment = (Comment)contentEntity;
            ContentEntityObject owningContent = comment.getContainer();
            return this.contextPathHolder.getContextPath() + "/plugins/servlet/mobile#content/view/" + owningContent.getId() + "/" + comment.getId();
        }
        return this.contextPathHolder.getContextPath() + "/plugins/servlet/mobile#content/view/" + contentEntity.getId();
    }
}

