/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.FilteredQuery
 *  org.apache.lucene.search.MatchAllDocsQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.SpacePermissionQuery;
import com.atlassian.confluence.impl.search.v2.lucene.filter.SpacePermissionsFilterFactory;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

@Deprecated
@Internal
public class SpacePermissionQueryMapper
implements LuceneQueryMapper<SpacePermissionQuery> {
    private final SpacePermissionsFilterFactory spacePermissionsFilterFactory;

    public SpacePermissionQueryMapper(SpacePermissionsFilterFactory spacePermissionsFilterFactory) {
        this.spacePermissionsFilterFactory = spacePermissionsFilterFactory;
    }

    @Override
    public Query convertToLuceneQuery(SpacePermissionQuery spacePermissionQuery) {
        return new FilteredQuery((Query)new MatchAllDocsQuery(), (Filter)this.spacePermissionsFilterFactory.create(spacePermissionQuery.getUser()));
    }
}

