/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.query.TextFieldQuery
 *  com.atlassian.confluence.search.v2.query.WildcardTextFieldQuery
 *  com.atlassian.confluence.search.v2.sort.ModifiedSort
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.EmbeddedSubCalendarsParser;
import com.atlassian.confluence.extra.calendar3.EmbeddedSubCalendarsTracker;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.confluence.search.v2.query.WildcardTextFieldQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="embeddedSubCalendarsTracker")
public class DefaultEmbeddedSubCalendarsTracker
implements EmbeddedSubCalendarsTracker {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEmbeddedSubCalendarsTracker.class);
    public static final int MAX_RESULT = 2000;
    private final SearchManager searchManager;
    private final EmbeddedSubCalendarsParser embeddedSubCalendarsParser;
    private static final Collection<String> TENANT_CONTEXT_AND_WORK_CONTEXT_EXCEPTION_CLASS_NAMES = ImmutableSet.of((Object)"com.atlassian.tenant.api.TenantContextException", (Object)"com.atlassian.workcontext.exception.WorkContextSeveredException", (Object)"com.atlassian.workcontext.exception.WorkContextReferenceSetException", (Object)"com.atlassian.workcontext.exception.WorkContextException");

    @Autowired
    public DefaultEmbeddedSubCalendarsTracker(@ComponentImport SearchManager searchManager, EmbeddedSubCalendarsParser embeddedSubCalendarsParser) {
        this.searchManager = searchManager;
        this.embeddedSubCalendarsParser = embeddedSubCalendarsParser;
    }

    @Override
    public int getEmbedCount(String subCalendarId) {
        int embedCount = 0;
        for (ContentEntityObject contentEntity : this.getContentEmbeddingSubCalendar(subCalendarId)) {
            embedCount += Collections2.filter(this.embeddedSubCalendarsParser.getEmbeddedSubCalendarIds(contentEntity), (Predicate)Predicates.equalTo((Object)subCalendarId)).size();
        }
        return embedCount;
    }

    private Collection<String> getEmbedSubCalendarsAPI() {
        ArrayList returnList = Lists.newArrayList();
        for (ContentEntityObject contentEntity : this.getContentEmbeddingSubCalendar()) {
            Collection<String> subCalendarsPerPage = this.embeddedSubCalendarsParser.getEmbeddedSubCalendarIds(contentEntity);
            if (subCalendarsPerPage == null || subCalendarsPerPage.isEmpty()) continue;
            returnList.addAll(subCalendarsPerPage);
        }
        return returnList;
    }

    @Override
    public Collection<String> getEmbedSubCalendars() {
        return this.getEmbedSubCalendarsAPI();
    }

    public Collection<ContentEntityObject> getContentEmbeddingSubCalendar() {
        try {
            return Collections2.transform((Collection)Collections2.filter(this.searchByQuery(new ContentSearch((SearchQuery)new WildcardTextFieldQuery("embeddedSubCalendarId", "*", BooleanOperator.AND), (SearchSort)ModifiedSort.DESCENDING)), (Predicate)Predicates.instanceOf(ContentEntityObject.class)), searchable -> {
                if (searchable instanceof AbstractPage) {
                    AbstractPage aPage = (AbstractPage)searchable;
                    return null == aPage.getSpace() ? aPage.getLatestVersion() : aPage;
                }
                return (ContentEntityObject)searchable;
            });
        }
        catch (InvalidSearchException searchQueryError) {
            LOG.error(String.format("Invalid search query with *", new Object[0]), (Throwable)searchQueryError);
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<ContentEntityObject> getContentEmbeddingSubCalendar(String subCalendarId) {
        try {
            return Collections2.transform((Collection)Collections2.filter(this.searchByQuery(new ContentSearch((SearchQuery)new TextFieldQuery("embeddedSubCalendarId", subCalendarId, BooleanOperator.AND), (SearchSort)ModifiedSort.DESCENDING)), (Predicate)Predicates.and((Predicate)Predicates.instanceOf(ContentEntityObject.class), searchable -> {
                ContentEntityObject pageContent = (ContentEntityObject)searchable;
                return this.embeddedSubCalendarsParser.getEmbeddedSubCalendarIds(pageContent).contains(subCalendarId);
            })), searchable -> {
                if (searchable instanceof AbstractPage) {
                    AbstractPage aPage = (AbstractPage)searchable;
                    return null == aPage.getSpace() ? aPage.getLatestVersion() : aPage;
                }
                return (ContentEntityObject)searchable;
            });
        }
        catch (InvalidSearchException searchQueryError) {
            LOG.error(String.format("Invalid search query %s", subCalendarId), (Throwable)searchQueryError);
            return Collections.emptyList();
        }
    }

    private List<Searchable> searchByQuery(ContentSearch iSearch) throws InvalidSearchException {
        try {
            return this.searchManager.searchEntities((ISearch)iSearch, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        }
        catch (RuntimeException e) {
            String exClassName = e.getClass().getCanonicalName();
            if (TENANT_CONTEXT_AND_WORK_CONTEXT_EXCEPTION_CLASS_NAMES.contains(exClassName)) {
                LOG.warn("Search failed when no WorkContext is set.", (Throwable)e);
                return Collections.emptyList();
            }
            throw e;
        }
    }
}

