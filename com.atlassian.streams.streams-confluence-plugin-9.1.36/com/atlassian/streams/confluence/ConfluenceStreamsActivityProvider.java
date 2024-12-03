/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.search.v2.ChangesSearch
 *  com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.SpacePermissionQueryFactory
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.search.v2.sort.ModifiedSort
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsFeed
 *  com.atlassian.streams.api.common.Iterables
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Options
 *  com.atlassian.streams.api.common.Suppliers
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancellableTask$Result
 *  com.atlassian.streams.spi.CancelledException
 *  com.atlassian.streams.spi.Filters
 *  com.atlassian.streams.spi.StreamsActivityProvider
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.search.v2.ChangesSearch;
import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.StreamsFeed;
import com.atlassian.streams.api.common.Iterables;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.atlassian.streams.api.common.Suppliers;
import com.atlassian.streams.confluence.ConfluenceEntryFactory;
import com.atlassian.streams.confluence.ConfluenceFilterOptionProvider;
import com.atlassian.streams.confluence.ConfluenceFilters;
import com.atlassian.streams.confluence.ConfluenceSearchQueryBuilder;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.confluence.changereport.ActivityItemFactory;
import com.atlassian.streams.spi.CancellableTask;
import com.atlassian.streams.spi.CancelledException;
import com.atlassian.streams.spi.Filters;
import com.atlassian.streams.spi.StreamsActivityProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConfluenceStreamsActivityProvider
implements StreamsActivityProvider {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceStreamsActivityProvider.class);
    public static final String PROVIDER_KEY = "wiki";
    private final SearchManager searchManager;
    private final ConfluenceEntryFactory entryFactory;
    private final I18nResolver i18nResolver;
    private final ActivityItemFactory activityItemFactory;
    private final ConfluenceUserDao confluenceUserDao;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;
    private final ContentPermissionsQueryFactory contentPermissionsQueryFactory;

    public ConfluenceStreamsActivityProvider(@Qualifier(value="searchManager") SearchManager searchManager, ConfluenceEntryFactory entryFactory, I18nResolver i18nResolver, ActivityItemFactory activityItemFactory, ConfluenceUserDao confluenceUserDao, SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory, SpacePermissionQueryFactory spacePermissionQueryFactory, ContentPermissionsQueryFactory contentPermissionsQueryFactory) {
        this.searchManager = (SearchManager)Preconditions.checkNotNull((Object)searchManager, (Object)"searchManager");
        this.entryFactory = (ConfluenceEntryFactory)Preconditions.checkNotNull((Object)entryFactory, (Object)"entryFactory");
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.activityItemFactory = (ActivityItemFactory)Preconditions.checkNotNull((Object)activityItemFactory, (Object)"activityItemFactory");
        this.confluenceUserDao = (ConfluenceUserDao)Preconditions.checkNotNull((Object)confluenceUserDao, (Object)"confluenceUserDao");
        this.siteSearchPermissionsQueryFactory = (SiteSearchPermissionsQueryFactory)Preconditions.checkNotNull((Object)siteSearchPermissionsQueryFactory, (Object)"siteSearchPermissionsQueryFactory");
        this.spacePermissionQueryFactory = (SpacePermissionQueryFactory)Preconditions.checkNotNull((Object)spacePermissionQueryFactory, (Object)"spacePermissionQueryFactory");
        this.contentPermissionsQueryFactory = (ContentPermissionsQueryFactory)Preconditions.checkNotNull((Object)contentPermissionsQueryFactory, (Object)"contentPermissionsQueryFactory");
    }

    public CancellableTask<StreamsFeed> getActivityFeed(final ActivityRequest request) {
        return new CancellableTask<StreamsFeed>(){
            final AtomicBoolean cancelled = new AtomicBoolean(false);

            public StreamsFeed call() {
                Iterable entries = ConfluenceStreamsActivityProvider.this.getStreamsEntries(request, (Supplier)Suppliers.forAtomicBoolean((AtomicBoolean)this.cancelled));
                return new StreamsFeed(ConfluenceStreamsActivityProvider.this.i18nResolver.getText("portlet.activityfeed.name"), Iterables.take((int)request.getMaxResults(), (Iterable)entries), Option.some((Object)ConfluenceStreamsActivityProvider.this.i18nResolver.getText("portlet.activityfeed.description")));
            }

            public CancellableTask.Result cancel() {
                this.cancelled.set(true);
                return CancellableTask.Result.CANCELLED;
            }
        };
    }

    private Iterable<StreamsEntry> getStreamsEntries(ActivityRequest request, Supplier<Boolean> cancelled) {
        int offset = 0;
        Iterable<ConfluenceEntityObject> searchables = this.search(request, offset, cancelled);
        if (com.google.common.collect.Iterables.isEmpty(searchables)) {
            return ImmutableList.of();
        }
        Iterable<ActivityItem> activityItems = this.activityItemFactory.getActivityItems(searchables, request);
        Iterable<StreamsEntry> entries = this.toStreamsEntries(request, activityItems, cancelled);
        while (com.google.common.collect.Iterables.size(entries) < request.getMaxResults()) {
            searchables = this.search(request, offset += request.getMaxResults(), cancelled);
            if (com.google.common.collect.Iterables.isEmpty(searchables)) {
                return entries;
            }
            activityItems = this.activityItemFactory.getActivityItems(activityItems, searchables, request);
            entries = this.toStreamsEntries(request, activityItems, cancelled);
        }
        return entries;
    }

    private Iterable<StreamsEntry> toStreamsEntries(ActivityRequest request, Iterable<ActivityItem> activityItems, Supplier<Boolean> cancelled) {
        return ImmutableList.copyOf((Iterable)Iterables.take((int)request.getMaxResults(), (Iterable)Options.catOptions((Iterable)StreamSupport.stream(activityItems.spliterator(), false).map(this.toStreamsEntry(request.getContextUri(), cancelled)).collect(Collectors.toList()))));
    }

    private Iterable<ConfluenceEntityObject> search(ActivityRequest request, int startOffset, Supplier<Boolean> cancelled) {
        ISearch search = this.buildSearch(request, startOffset);
        try {
            SearchResults results = this.searchManager.search(search);
            if (com.google.common.collect.Iterables.isEmpty((Iterable)results)) {
                return ImmutableList.of();
            }
            if (cancelled.get().booleanValue()) {
                throw new CancelledException();
            }
            List entities = this.searchManager.convertToEntities(results, SearchManager.EntityVersionPolicy.INDEXED_VERSION);
            return entities.stream().filter(ConfluenceEntityObject.class::isInstance).map(e -> (ConfluenceEntityObject)e).collect(Collectors.toList());
        }
        catch (InvalidSearchException e2) {
            log.warn("Invalid search occurred", (Throwable)e2);
            return ImmutableList.of();
        }
    }

    private ISearch buildSearch(ActivityRequest request, int startOffset) {
        ImmutableSet authors = ImmutableSet.copyOf((Iterable)Filters.getAuthors((ActivityRequest)request));
        Iterable<String> searchTerms = ConfluenceFilters.getSearchTerms(request);
        Iterable<String> excludedSearchTerms = ConfluenceFilters.getExcludedSearchTerms(request);
        Iterable activityObjectTypes = Filters.getRequestedActivityObjectTypes((ActivityRequest)request, ConfluenceFilterOptionProvider.activities);
        ConfluenceSearchQueryBuilder queryBuilder = new ConfluenceSearchQueryBuilder(this.confluenceUserDao).inSpace(Filters.getProjectKeys((ActivityRequest)request)).searchFor(searchTerms).excludeTerms(excludedSearchTerms).activityObjects(activityObjectTypes).minDate((Option<Date>)Filters.getMinDate((ActivityRequest)request)).maxDate((Option<Date>)Filters.getMaxDate((ActivityRequest)request));
        ModifiedSort sort = ModifiedSort.DESCENDING;
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        BooleanQuery.Builder filterBuilder = BooleanQuery.builder();
        filterBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        filterBuilder.addFilter(this.spacePermissionQueryFactory.create(authenticatedUser));
        this.contentPermissionsQueryFactory.create(authenticatedUser).ifPresent(arg_0 -> ((BooleanQuery.Builder)filterBuilder).addFilter(arg_0));
        if (com.google.common.collect.Iterables.isEmpty(searchTerms) && com.google.common.collect.Iterables.isEmpty(excludedSearchTerms)) {
            if (!com.google.common.collect.Iterables.isEmpty((Iterable)authors)) {
                filterBuilder.addFilters(authors.stream().map(this::buildLastModifierFilter).collect(Collectors.toSet()));
            }
            queryBuilder.addFilters(filterBuilder.build());
            return new ChangesSearch(queryBuilder.build(), (SearchSort)sort, startOffset, request.getMaxResults());
        }
        queryBuilder.addFilters(filterBuilder.build());
        return new ContentSearch(queryBuilder.createdOrLastModifiedBy((Iterable<String>)authors).build(), (SearchSort)sort, startOffset, request.getMaxResults());
    }

    private Function<ActivityItem, Option<StreamsEntry>> toStreamsEntry(URI baseUri, Supplier<Boolean> cancelled) {
        return activityItem -> {
            try {
                if (((Boolean)cancelled.get()).booleanValue()) {
                    throw new CancelledException();
                }
                return Option.some((Object)this.entryFactory.buildStreamsEntry(baseUri, (ActivityItem)activityItem));
            }
            catch (Exception e) {
                log.warn("Error creating streams entry", (Throwable)e);
                return Option.none();
            }
        };
    }

    private TermQuery buildLastModifierFilter(String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        String userKey = user != null ? user.getKey().getStringValue() : "";
        return new TermQuery("lastModifierName", userKey);
    }
}

