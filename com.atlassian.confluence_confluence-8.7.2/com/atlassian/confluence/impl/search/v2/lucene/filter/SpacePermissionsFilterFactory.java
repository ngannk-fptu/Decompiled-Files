/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.user.User
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.queries.BooleanFilter
 *  org.apache.lucene.search.CachingWrapperFilter
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.atlassian.confluence.impl.search.v2.lucene.EmptyDocIdSet;
import com.atlassian.confluence.impl.search.v2.lucene.filter.SpaceFilter;
import com.atlassian.confluence.impl.search.v2.lucene.filter.SpacePermissionsFilter;
import com.atlassian.confluence.impl.search.v2.lucene.filter.SpacePermissionsFilterDao;
import com.atlassian.confluence.impl.search.v2.lucene.filter.TermFilter;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryManager;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.fugue.Either;
import com.atlassian.user.User;
import java.util.List;
import java.util.Objects;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;

public class SpacePermissionsFilterFactory {
    private static Filter spaceLessFilter = new CachingWrapperFilter((Filter)new TermFilter(new Term(SearchFieldNames.IN_SPACE, "false")));
    private static final DocIdSet EMPTY_DOCIDSET = new EmptyDocIdSet();
    private final SpacePermissionsFilterDao spacePermissionsFilterDao;
    private final SpacePermissionQueryManager spacePermissionQueryManager;

    public SpacePermissionsFilterFactory(SpacePermissionsFilterDao spacePermissionsFilterDao, SpacePermissionQueryManager spacePermissionQueryManager) {
        this.spacePermissionsFilterDao = Objects.requireNonNull(spacePermissionsFilterDao);
        this.spacePermissionQueryManager = Objects.requireNonNull(spacePermissionQueryManager);
    }

    public SpacePermissionsFilter create(User currentUser) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(currentUser);
        Either<AccessDenied, SpacePermissionQueryBuilder> permissionQueryBuilderEither = this.spacePermissionQueryManager.createSpacePermissionQueryBuilder(confluenceUser, "VIEWSPACE");
        if (permissionQueryBuilderEither.isRight()) {
            return new SpacePermissionsFilter(spaceLessFilter, this.createPermittedSpacesFilter((SpacePermissionQueryBuilder)permissionQueryBuilderEither.right().get()));
        }
        return new SpacePermissionsFilter((Filter)new BooleanFilter(), (Filter)new BooleanFilter()){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) {
                return EMPTY_DOCIDSET;
            }
        };
    }

    public static Filter getSpaceLessFilter() {
        return spaceLessFilter;
    }

    private Filter createPermittedSpacesFilter(SpacePermissionQueryBuilder spacePermissionQueryBuilder) {
        List<String> spaceKeys;
        List<String> permittedSpaceKeys = this.spacePermissionsFilterDao.getPermittedSpaceKeys(spacePermissionQueryBuilder);
        List<String> unpermittedSpaceKeys = this.spacePermissionsFilterDao.getUnPermittedSpaceKeys(spacePermissionQueryBuilder);
        boolean negatingFlag = false;
        if (unpermittedSpaceKeys.size() < permittedSpaceKeys.size()) {
            negatingFlag = true;
            spaceKeys = unpermittedSpaceKeys;
        } else {
            spaceKeys = permittedSpaceKeys;
        }
        return SpaceFilter.createFilter(spaceKeys, negatingFlag);
    }
}

