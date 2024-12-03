/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.event.events.search.PopularQueryExecutionEvent
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SpacePermissionQueryFactory
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentPermissionsQuery
 *  com.atlassian.confluence.search.v2.query.LongRangeQuery
 *  com.atlassian.confluence.search.v2.query.TermSetQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.event.events.search.PopularQueryExecutionEvent;
import com.atlassian.confluence.plugins.edgeindex.EdgeFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeQueries;
import com.atlassian.confluence.plugins.edgeindex.EdgeQueryParameter;
import com.atlassian.confluence.plugins.edgeindex.EdgeSearchIndexAccessor;
import com.atlassian.confluence.plugins.edgeindex.EdgeTypeRepository;
import com.atlassian.confluence.plugins.edgeindex.ScoreConfig;
import com.atlassian.confluence.plugins.edgeindex.TopEdgeTargetCollector;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentPermissionsQuery;
import com.atlassian.confluence.search.v2.query.LongRangeQuery;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={EdgeQueries.class})
@Component
public class DefaultEdgeQueries
implements EdgeQueries {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEdgeQueries.class);
    private final UserAccessor userAccessor;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;
    private final EdgeSearchIndexAccessor edgeSearchIndexAccessor;
    private final EdgeTypeRepository edgeTypeRepository;
    private final NetworkService networkService;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultEdgeQueries(UserAccessor userAccessor, SpacePermissionQueryFactory spacePermissionQueryFactory, EdgeSearchIndexAccessor edgeSearchIndexAccessor, EdgeTypeRepository edgeTypeRepository, NetworkService networkService, EventPublisher eventPublisher) {
        this.userAccessor = userAccessor;
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
        this.edgeSearchIndexAccessor = edgeSearchIndexAccessor;
        this.networkService = networkService;
        this.edgeTypeRepository = edgeTypeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<EdgeTargetInfo> getMostPopular(EdgeQueryParameter edgeQueryParameter) {
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        return this.getMostPopular(edgeQueryParameter, remoteUser);
    }

    List<EdgeTargetInfo> getMostPopular(EdgeQueryParameter edgeQueryParameter, ConfluenceUser remoteUser) {
        ScoreConfig scoreConfig = edgeQueryParameter.getScoreConfig();
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        long max = System.currentTimeMillis() / 1000L;
        long min = max - TimeUnit.SECONDS.convert(edgeQueryParameter.getTime(), edgeQueryParameter.getTimeUnit());
        LongRangeQuery dateRangeQuery = new LongRangeQuery("edge.date", Range.range((Object)min, (Object)max, (boolean)true, (boolean)true));
        searchQueryBuilder.addMust((Object)dateRangeQuery);
        if (remoteUser != null) {
            ContentPermissionsQuery contentPermissionsQuery = ContentPermissionsQuery.builder().user(remoteUser).groupNames(this.userAccessor.getGroupNames((User)remoteUser)).build();
            searchQueryBuilder.addMust((Object)contentPermissionsQuery);
        }
        Collection<EdgeType> edgeTypes = this.getEdgeTypes(edgeQueryParameter.getEdgeTypes());
        Set edgeTypeKeys = edgeTypes.stream().filter(edgeType -> edgeType.getPermissionDelegate().canView(remoteUser)).map(EdgeType::getKey).collect(Collectors.toSet());
        TermSetQuery termSetQuery = new TermSetQuery("edge.type", edgeTypeKeys);
        searchQueryBuilder.addMust((Object)termSetQuery);
        if (!this.userAccessor.isSuperUser((User)remoteUser)) {
            SearchQuery spacePermissionQuery = this.spacePermissionQueryFactory.create(remoteUser);
            searchQueryBuilder.addFilter(spacePermissionQuery);
        }
        TopEdgeTargetCollector topEdgeTargetCollector = new TopEdgeTargetCollector(this.edgeTypeRepository, this.getFolloweeKeys(remoteUser), scoreConfig, edgeQueryParameter.getAcceptFilter(), edgeQueryParameter.getMaxEdgeInfo(), new Date());
        long start = System.currentTimeMillis();
        this.edgeSearchIndexAccessor.scan(searchQueryBuilder.build(), EdgeFactory.REQUIRED_FIELDS, topEdgeTargetCollector);
        long end = System.currentTimeMillis();
        this.eventPublisher.publish((Object)new PopularQueryExecutionEvent(start, end));
        return topEdgeTargetCollector.getTopTargets();
    }

    private Collection<EdgeType> getEdgeTypes(List<String> edgeTypeKeys) {
        Collection<EdgeType> edgeTypes;
        if (edgeTypeKeys == null) {
            edgeTypes = this.edgeTypeRepository.getEdgeIndexTypes();
        } else {
            edgeTypes = new ArrayList<EdgeType>(edgeTypeKeys.size());
            for (String edgeTypeKey : edgeTypeKeys) {
                Option<EdgeType> edgeType = this.edgeTypeRepository.getEdgeIndexTypeByKey(edgeTypeKey);
                if (!edgeType.isDefined()) continue;
                edgeTypes.add((EdgeType)edgeType.get());
            }
        }
        return edgeTypes;
    }

    private Set<UserKey> getFolloweeKeys(ConfluenceUser remoteUser) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        if (remoteUser != null) {
            SimplePageRequest pageRequest = new SimplePageRequest(0, 100);
            PageResponse followees = this.networkService.getFollowing(remoteUser.getKey(), (PageRequest)pageRequest);
            for (com.atlassian.confluence.api.model.people.User followee : followees) {
                if (!(followee instanceof KnownUser)) continue;
                followee.optionalUserKey().ifPresent(arg_0 -> ((ImmutableSet.Builder)builder).add(arg_0));
            }
        }
        return builder.build();
    }
}

