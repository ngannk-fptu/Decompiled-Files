/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SpacePermissionQueryFactory
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.search.Doc;
import com.atlassian.confluence.contributors.search.PageDescendantsSearcher;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value="descendantsSearcher")
@Internal
class DefaultPageDescendantsSearcher
implements PageDescendantsSearcher {
    private final SearchManager searchManager;
    private final SpacePermissionQueryFactory spacePermissionQueryFactory;
    private final ContentPermissionsQueryFactory contentPermissionsQueryFactory;

    @Autowired
    public DefaultPageDescendantsSearcher(@ComponentImport SearchManager searchManager, @ComponentImport SpacePermissionQueryFactory spacePermissionQueryFactory, @ComponentImport ContentPermissionsQueryFactory contentPermissionsQueryFactory) {
        this.searchManager = Objects.requireNonNull(searchManager);
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
        this.contentPermissionsQueryFactory = contentPermissionsQueryFactory;
    }

    @Override
    public Iterable<Doc> getDescendants(Set<Long> pageIds) {
        SearchQuery searchQuery = this.createDescendantsQuery(pageIds);
        ArrayList<Doc> result = new ArrayList<Doc>();
        try {
            this.searchManager.scan(EnumSet.of(SearchIndex.CONTENT), searchQuery, Doc.REQUESTED_FIELDS, x -> result.add(new Doc(fieldName -> x.getOrDefault(fieldName, new String[0]))));
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Iterable<Doc> getDirectChildren(Set<Long> pageIds) {
        return StreamSupport.stream(this.getDescendants(pageIds).spliterator(), false).filter(x -> x.getParentId().map(pageIds::contains).orElse(false)).collect(Collectors.toList());
    }

    private SearchQuery createDescendantsQuery(Set<Long> pageIds) {
        SearchQuery ancestorQuery = (SearchQuery)BooleanQuery.builder().addShould((Collection)pageIds.stream().map(x -> new TermQuery("ancestorIds", String.valueOf(x))).collect(Collectors.toList())).build();
        SearchQuery handleQuery = (SearchQuery)BooleanQuery.builder().addShould((Collection)pageIds.stream().map(x -> new TermQuery(SearchFieldNames.HANDLE, this.createPageHandle((long)x).toString())).collect(Collectors.toList())).build();
        HashSet<SearchQuery> permissionFilterSet = new HashSet<SearchQuery>(2);
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        permissionFilterSet.add(this.spacePermissionQueryFactory.create(currentUser));
        Optional contentPermissionsQuery = this.contentPermissionsQueryFactory.create(currentUser);
        if (contentPermissionsQuery.isPresent()) {
            permissionFilterSet.add((SearchQuery)contentPermissionsQuery.get());
        }
        return (SearchQuery)BooleanQuery.builder().addFilters(permissionFilterSet).addMust((Object)ancestorQuery).addMustNot((Object)handleQuery).build();
    }

    private HibernateHandle createPageHandle(long x) {
        return new HibernateHandle(Page.class.getName(), x);
    }
}

