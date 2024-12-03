/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResultType
 *  com.atlassian.confluence.search.v2.SpacePermissionQueryFactory
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.LabelQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.contributors.search.PageQueryFactory;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResultType;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Internal
class DefaultPageQueryFactory
implements PageQueryFactory {
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final ContentPermissionsQueryFactory contentPermissionsQueryFactory;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;

    @Autowired
    public DefaultPageQueryFactory(@ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, @ComponentImport ContentPermissionsQueryFactory contentPermissionsQueryFactory, @ComponentImport SpacePermissionQueryFactory spacePermissionQueryFactory) {
        this.spaceManager = Objects.requireNonNull(spaceManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.contentPermissionsQueryFactory = contentPermissionsQueryFactory;
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
    }

    @Override
    public SearchQuery createPageQuery(MacroParameterModel params) {
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        this.contentPermissionsQueryFactory.create(confluenceUser).ifPresent(arg_0 -> ((BooleanQuery.Builder)queryBuilder).addFilter(arg_0));
        if (!this.permissionManager.isSystemAdministrator((User)confluenceUser)) {
            queryBuilder.addFilter(this.spacePermissionQueryFactory.create(confluenceUser));
        }
        return (SearchQuery)queryBuilder.addMust((Object)this.createContentQuery(params.getPageTitle(), params.getContentType(), params.getLabelsString(), params.getPublishedDate())).addMust((Object)this.createSpacesQuery(params.getSpaceKey())).build();
    }

    @VisibleForTesting
    SearchQuery createContentQuery(String contentTitle, String contentType, String labelString, Date publishedDate) {
        BooleanQuery.Builder builder = BooleanQuery.builder();
        builder.addMust((Object)new TermQuery(SearchFieldNames.DOCUMENT_TYPE, SearchResultType.CONTENT.name()));
        if (StringUtils.isBlank((CharSequence)contentType)) {
            builder.addMust((Object)new ContentTypeQuery(Arrays.asList(ContentTypeEnum.BLOG, ContentTypeEnum.PAGE)));
        } else if (StringUtils.equals((CharSequence)"page", (CharSequence)contentType)) {
            builder.addMust((Object)new ContentTypeQuery(ContentTypeEnum.PAGE));
        } else {
            builder.addMust((Object)new ContentTypeQuery(ContentTypeEnum.BLOG));
        }
        if (StringUtils.isNotBlank((CharSequence)contentTitle)) {
            builder.addMust((Object)new TermQuery(SearchFieldNames.CONTENT_NAME_UNTOKENIZED, contentTitle));
        }
        if (StringUtils.isNotBlank((CharSequence)labelString)) {
            builder.addMust((Object)this.createLabelQuery(labelString));
        }
        if (publishedDate != null) {
            builder.addMust((Object)new DateRangeQuery(publishedDate, null, true, true, SearchFieldNames.CREATION_DATE));
        }
        return builder.build();
    }

    @VisibleForTesting
    SearchQuery createLabelQuery(String labelString) {
        BooleanQuery.Builder builder = BooleanQuery.builder();
        for (String labelName : StringUtils.split((String)labelString, (String)" ,")) {
            builder.addShould((Object)new LabelQuery(labelName));
        }
        return builder.build();
    }

    @VisibleForTesting
    SearchQuery createSpacesQuery(String spaceString) {
        Supplier<List> supplier = null;
        if (spaceString.equals("@all")) {
            supplier = () -> ((SpaceManager)this.spaceManager).getAllSpaces();
        } else if (spaceString.startsWith("@")) {
            SpaceType spaceType = SpaceType.getSpaceType((String)spaceString.substring(1));
            supplier = () -> this.spaceManager.getAllSpaces(SpacesQuery.newQuery().withSpaceType(spaceType).build());
        }
        Object spaceKeys = supplier != null ? supplier.get().stream().map(Space::getKey).collect(Collectors.toSet()) : ImmutableSet.copyOf((Object[])StringUtils.split((String)spaceString, (String)" ,"));
        return spaceKeys.size() == 0 ? MatchNoDocsQuery.getInstance() : new InSpaceQuery((Set)spaceKeys);
    }
}

