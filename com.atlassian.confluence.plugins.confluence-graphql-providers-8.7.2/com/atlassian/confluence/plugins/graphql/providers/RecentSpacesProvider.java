/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.user.UserKey
 *  graphql.schema.DataFetchingEnvironment
 *  javax.ws.rs.DefaultValue
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserKey;
import graphql.schema.DataFetchingEnvironment;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.DefaultValue;

@AnonymousAllowed
@GraphQLProvider
@GraphQLName(value="recentSpaceKeys")
public class RecentSpacesProvider {
    private final RecentlyViewedManager recentlyViewedManager;

    public RecentSpacesProvider(@ComponentImport RecentlyViewedManager recentlyViewedManager) {
        this.recentlyViewedManager = recentlyViewedManager;
    }

    @GraphQLName
    public List<String> recentSpaces(DataFetchingEnvironment env, @GraphQLName(value="limit") @DefaultValue(value="25") int limit) throws ServiceException {
        UserKey userKey;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        UserKey userKey2 = userKey = currentUser != null ? currentUser.getKey() : null;
        if (userKey != null) {
            return this.recentlyViewedManager.getRecentlyViewedSpaces(userKey.getStringValue(), limit + 1).stream().map(Space::getKey).map(String::valueOf).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

