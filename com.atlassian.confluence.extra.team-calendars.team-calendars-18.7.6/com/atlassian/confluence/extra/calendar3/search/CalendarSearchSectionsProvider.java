/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.search.actions.json.ContentNameMatch
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchContext
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchSection
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchSectionsProvider
 *  com.atlassian.confluence.search.contentnames.QueryToken
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.search;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.extra.calendar3.search.CalendarSearcher;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSection;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionsProvider;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarSearchSectionsProvider
implements ContentNameSearchSectionsProvider {
    private static Logger logger = LoggerFactory.getLogger(CalendarSearchSectionsProvider.class);
    private static final int MAX_RESULTS = 3;
    private static final int START_INDEX = 0;
    private static final int WEIGHT = 50;
    private final ContextPathHolder contextPathHolder;
    private final CalendarSearcher calendarSearcher;
    private final DarkFeatureManager darkFeatureManager;

    public CalendarSearchSectionsProvider(ContextPathHolder contextPathHolder, CalendarSearcher calendarSearcher, DarkFeatureManager darkFeatureManager) {
        this.contextPathHolder = contextPathHolder;
        this.calendarSearcher = calendarSearcher;
        this.darkFeatureManager = darkFeatureManager;
    }

    public Collection<ContentNameSearchSection> getSections(List<QueryToken> queryTokens, ContentNameSearchContext context) {
        boolean excludedFromSearch;
        if (this.darkFeatureManager.isFeatureEnabledForAllUsers("v2.content.name.searcher")) {
            return Collections.emptyList();
        }
        HashSet types = Sets.newHashSet((Iterable)context.getTypes());
        boolean bl = excludedFromSearch = types.size() > 0 && !types.contains("calendar-content-type");
        if (excludedFromSearch) {
            logger.debug("Exclude calendar result from search because type does not match");
            return null;
        }
        try {
            List<ContentNameMatch> contentNameMatches = this.searchItems(queryTokens);
            return Collections.singletonList(new ContentNameSearchSection(Integer.valueOf(50), contentNameMatches));
        }
        catch (InvalidSearchException e) {
            logger.error("Could not search for Calendar by using Lucence", (Throwable)e);
            return Collections.emptyList();
        }
    }

    private List<ContentNameMatch> searchItems(List<QueryToken> queryTokens) throws InvalidSearchException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ArrayList<ContentNameMatch> matches = new ArrayList<ContentNameMatch>(this.calendarSearcher.findSubCalendars(currentUser, queryTokens, 0, 3, searchable -> {
            CustomContentEntityObject calendarContent = (CustomContentEntityObject)searchable;
            return this.createContentNameMatch(calendarContent);
        }));
        if (logger.isDebugEnabled()) {
            logger.debug("===========Search ContentNameMatch results from Searcher===========");
            for (ContentNameMatch result : matches) {
                logger.debug("      ContentNameMatch search result : {}", (Object)result.toString());
            }
            logger.debug("===========Search ContentNameMatch results from Searcher===========");
        }
        return matches;
    }

    private ContentNameMatch createContentNameMatch(CustomContentEntityObject calendarContent) {
        String name = calendarContent.getTitle();
        String url = this.contextPathHolder.getContextPath() + calendarContent.getUrlPath();
        return new ContentNameMatch("calendar-item", HtmlUtil.htmlEncode((String)name), url);
    }
}

