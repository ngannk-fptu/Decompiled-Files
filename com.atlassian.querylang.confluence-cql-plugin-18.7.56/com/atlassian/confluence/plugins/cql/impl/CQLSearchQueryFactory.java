/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.query.ActiveUserQuery
 *  com.atlassian.confluence.search.v2.query.ArchivedSpacesQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentStatusQuery
 *  com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery
 *  com.atlassian.confluence.setup.settings.CollaborativeEditingHelper
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.ActiveUserQuery;
import com.atlassian.confluence.search.v2.query.ArchivedSpacesQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentStatusQuery;
import com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CQLSearchQueryFactory {
    private final SpaceManager spaceManager;
    private final CollaborativeEditingHelper collaborativeEditingHelper;
    private final PluginAccessor pluginAccessor;
    private SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    @Autowired
    public CQLSearchQueryFactory(@ComponentImport SpaceManager spaceManager, @ComponentImport CollaborativeEditingHelper collaborativeEditingHelper, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.spaceManager = spaceManager;
        this.collaborativeEditingHelper = collaborativeEditingHelper;
        this.pluginAccessor = pluginAccessor;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }

    public SearchQuery createFilter(boolean includeArchivedSpaces, List<ContentStatus> contentStatuses) {
        Objects.requireNonNull(contentStatuses);
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        SearchQuery cacheableQuery = this.createCacheableSearchQuery(contentStatuses);
        boolQueryBuilder.addMust((Object)cacheableQuery);
        boolQueryBuilder.addMust((Object)this.siteSearchPermissionsQueryFactory.create());
        if (!includeArchivedSpaces) {
            boolQueryBuilder.addMust((Object)new ArchivedSpacesQuery(false, this.spaceManager));
        }
        return boolQueryBuilder.build();
    }

    private SearchQuery createCacheableSearchQuery(List<ContentStatus> contentStatuses) {
        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
        boolQueryBuilder.addMust((Object)new NonViewableCustomContentTypeQuery(this.pluginAccessor));
        boolQueryBuilder.addMust((Object)ActiveUserQuery.getInstance());
        if (!contentStatuses.isEmpty() && !this.collaborativeEditingHelper.getEditMode("").equals("legacy")) {
            boolQueryBuilder.addMust((Object)new ContentStatusQuery(contentStatuses));
        }
        return boolQueryBuilder.build();
    }
}

