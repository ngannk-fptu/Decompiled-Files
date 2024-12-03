/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.plugins.search.api.model.SearchResult
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.impl;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SearchResultAugmenter;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.impl.SearchableWithId;
import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchResultAugmenterImpl
implements SearchResultAugmenter {
    public static final String LIKES_METADATA_KEY = "likes";
    public static final String USER_CAN_VIEW_METADATA_KEY = "userCanView";
    public static final List<String> LIKABLE_TYPES = Lists.newArrayList((Object[])new String[]{"page", "blogpost", "comment"});
    private final LikeManager likeManager;
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;

    public SearchResultAugmenterImpl(LikeManager likeManager, PermissionManager permissionManager, ContentEntityManager contentEntityManager) {
        this.likeManager = likeManager;
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public List<SearchResult> addLikeCountsToResults(List<SearchResult> results) {
        ArrayList resultsWithLikes = Lists.newArrayList();
        Map<SearchResult, Searchable> searchablesMap = this.getSearchablesMap(results);
        ArrayList searchables = Lists.newArrayList(searchablesMap.values());
        if (searchables.isEmpty()) {
            return results;
        }
        Map likeMap = this.likeManager.countLikes((Collection)searchables);
        Iterator<SearchResult> iterator = results.iterator();
        while (iterator.hasNext()) {
            SearchResult resultEntity;
            SearchResult finalResult = resultEntity = iterator.next();
            if (searchablesMap.containsKey(resultEntity)) {
                int likes = (Integer)likeMap.get(searchablesMap.get(resultEntity));
                finalResult = this.makeResultWithLike(resultEntity, likes);
            }
            resultsWithLikes.add(finalResult);
        }
        return resultsWithLikes;
    }

    @Override
    public List<SearchResult> addViewPermissionChecksToResults(List<SearchResult> results, Option<ConfluenceUser> user) {
        return results.stream().map(searchResult -> this.makeResultWithViewPermissionCheck((SearchResult)searchResult, user)).collect(Collectors.toList());
    }

    private SearchResult makeResultWithLike(SearchResult searchResult, int likes) {
        HashMap metadata = Maps.newHashMap();
        metadata.put(LIKES_METADATA_KEY, Integer.toString(likes));
        return this.withExtraMetaData(searchResult, metadata);
    }

    private SearchResult makeResultWithViewPermissionCheck(SearchResult searchResult, Option<ConfluenceUser> user) {
        ConfluenceUser nullableUser = (ConfluenceUser)user.getOrElse((Object)null);
        boolean userCanView = this.userCanViewContent(nullableUser, searchResult.getId());
        HashMap metadata = Maps.newHashMap();
        metadata.put(USER_CAN_VIEW_METADATA_KEY, Boolean.toString(userCanView));
        return this.withExtraMetaData(searchResult, metadata);
    }

    private boolean userCanViewContent(ConfluenceUser nullableUser, long contentId) {
        return (Boolean)Option.option((Object)this.contentEntityManager.getById(contentId)).map(contentEntityObject -> this.permissionManager.hasPermission((User)nullableUser, Permission.VIEW, contentEntityObject)).getOrElse((Object)false);
    }

    private SearchResult withExtraMetaData(SearchResult result, Map<String, String> extraMetaData) {
        HashMap metadata = Maps.newHashMap((Map)result.getMetadata());
        metadata.putAll(extraMetaData);
        return new SearchResult(result.getId(), result.getContentType(), result.getTitle(), result.getBodyTextHighlights(), result.getUrl(), result.getSearchResultContainer(), result.getFriendlyDate(), result.getExplanation(), (Map)ImmutableMap.copyOf((Map)metadata));
    }

    private Map<SearchResult, Searchable> getSearchablesMap(List<SearchResult> results) {
        HashMap map = Maps.newHashMap();
        for (SearchResult result : results) {
            if (!LIKABLE_TYPES.contains(result.getContentType())) continue;
            long id = result.getId();
            map.put(result, new SearchableWithId(id));
        }
        return map;
    }
}

