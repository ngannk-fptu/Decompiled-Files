/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.impl;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response.AugmentedSearchResult;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SearchResultAugmenterV2;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchResultAugmenterV2Impl
implements SearchResultAugmenterV2 {
    public static final String USER_CAN_VIEW_METADATA_KEY = "userCanView";
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;

    public SearchResultAugmenterV2Impl(PermissionManager permissionManager, ContentEntityManager contentEntityManager) {
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public List<SearchResult> addViewPermissionChecksToResults(List<SearchResult> results, Option<ConfluenceUser> user) {
        return results.stream().map(searchResult -> this.makeResultWithViewPermissionCheck((SearchResult)searchResult, user)).collect(Collectors.toList());
    }

    private SearchResult makeResultWithViewPermissionCheck(SearchResult searchResult, Option<ConfluenceUser> user) {
        ConfluenceUser nullableUser = (ConfluenceUser)user.getOrElse((Object)null);
        boolean userCanView = this.userCanViewContent(nullableUser, searchResult.getHandleId());
        HashMap metadata = Maps.newHashMap();
        metadata.put(USER_CAN_VIEW_METADATA_KEY, Boolean.toString(userCanView));
        return this.withExtraMetaData(searchResult, metadata);
    }

    private boolean userCanViewContent(ConfluenceUser nullableUser, long contentId) {
        return (Boolean)Option.option((Object)this.contentEntityManager.getById(contentId)).map(contentEntityObject -> this.permissionManager.hasPermission((User)nullableUser, Permission.VIEW, contentEntityObject)).getOrElse((Object)false);
    }

    private SearchResult withExtraMetaData(SearchResult result, Map<String, String> extraMetaData) {
        HashMap metadata = Maps.newHashMap((Map)result.getExtraFields());
        metadata.putAll(extraMetaData);
        return new AugmentedSearchResult(result, (Map<String, String>)ImmutableMap.copyOf((Map)metadata));
    }
}

