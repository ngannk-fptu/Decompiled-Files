/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.confluence.impl.search.v2.lucene.filter.SpacePermissionsFilterDao;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryManager;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.FieldExistsQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public class DefaultSpacePermissionQueryFactory
implements SpacePermissionQueryFactory {
    private final SpacePermissionQueryManager spacePermissionQueryManager;
    private final SpacePermissionsFilterDao spacePermissionsFilterDao;

    public DefaultSpacePermissionQueryFactory(SpacePermissionQueryManager spacePermissionQueryManager, SpacePermissionsFilterDao spacePermissionsFilterDao) {
        this.spacePermissionQueryManager = Objects.requireNonNull(spacePermissionQueryManager);
        this.spacePermissionsFilterDao = Objects.requireNonNull(spacePermissionsFilterDao);
    }

    @Override
    public SearchQuery create(@Nullable ConfluenceUser user) {
        Either<AccessDenied, SpacePermissionQueryBuilder> permissionQueryBuilderEither = this.spacePermissionQueryManager.createSpacePermissionQueryBuilder(user, "VIEWSPACE");
        if (permissionQueryBuilderEither.isRight()) {
            SpacePermissionQueryBuilder queryBuilder = (SpacePermissionQueryBuilder)permissionQueryBuilderEither.right().get();
            List<String> permittedSpaces = this.spacePermissionsFilterDao.getPermittedSpaceKeys(queryBuilder);
            return (SearchQuery)BooleanQuery.builder().addShould(new TermSetQuery(SearchFieldNames.SPACE_KEY, (Set<String>)ImmutableSet.copyOf(permittedSpaces))).addShould(FieldExistsQuery.fieldNotExistsQuery(SearchFieldNames.SPACE_KEY)).build();
        }
        return MatchNoDocsQuery.getInstance();
    }
}

