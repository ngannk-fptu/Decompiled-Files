/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.content.ContentStatus
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.FieldExistsQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContentStatusQuery
implements SearchQuery {
    public static final String KEY = "contentStatus";
    public static final ContentStatusQuery CURRENT = new ContentStatusQuery(ContentStatus.CURRENT);
    private static final FieldExistsQuery contentStatusFieldNotExistsQuery = new FieldExistsQuery(SearchFieldNames.CONTENT_STATUS, true);
    private final Collection<ContentStatus> contentStatuses;

    public static SearchQuery getDefaultContentStatusQuery() {
        return BooleanQuery.orQuery(CURRENT, contentStatusFieldNotExistsQuery);
    }

    public ContentStatusQuery(Collection<ContentStatus> contentStatuses) {
        this.contentStatuses = contentStatuses;
    }

    @VisibleForTesting
    public ContentStatusQuery(ContentStatus ... contentStatuses) {
        this.contentStatuses = Arrays.asList(contentStatuses);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return new ArrayList<ContentStatus>(this.contentStatuses);
    }

    @Override
    public SearchQuery expand() {
        if (this.contentStatuses.isEmpty()) {
            return AllQuery.getInstance();
        }
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        Set contentStatusSubQueries = this.contentStatuses.stream().map(contentStatus -> new TermQuery(SearchFieldNames.CONTENT_STATUS, contentStatus.serialise())).collect(Collectors.toSet());
        queryBuilder.addShould(contentStatusSubQueries);
        if (this.contentStatuses.contains(ContentStatus.CURRENT)) {
            queryBuilder.addShould(contentStatusFieldNotExistsQuery);
        }
        return queryBuilder.build();
    }

    public Collection<ContentStatus> getContentStatuses() {
        return this.contentStatuses;
    }
}

