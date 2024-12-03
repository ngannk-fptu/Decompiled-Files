/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.TermQuery
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.plugins.cql.v2search.query.ContentIdQuery;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Arrays;
import java.util.List;

public class AncestorIdQuery
implements SearchQuery {
    public static final String KEY = "ancestor";
    public static final String ANCESTOR_IDS_FIELD = "ancestorIds";
    private final List<ContentId> contentIds;

    public AncestorIdQuery(ContentId ... contentIds) {
        this(Arrays.asList(contentIds));
    }

    public AncestorIdQuery(List<ContentId> contentIds) {
        this.contentIds = contentIds;
    }

    public String getKey() {
        return KEY;
    }

    public List<ContentId> getParameters() {
        return this.contentIds;
    }

    public SearchQuery expand() {
        BooleanQuery.Builder setOfAncestorsBuilder = BooleanQuery.builder();
        for (ContentId id : this.contentIds) {
            SearchQuery childrenOfOnly = (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery(ANCESTOR_IDS_FIELD, id.serialise())).addMustNot((Object)new ContentIdQuery(id)).build();
            setOfAncestorsBuilder.addShould((Object)childrenOfOnly);
        }
        SearchQuery setOfAncestors = setOfAncestorsBuilder.build();
        return (SearchQuery)BooleanQuery.builder().addMust((Object)setOfAncestors).addMust((Object)new TermQuery(SearchFieldNames.TYPE, ContentTypeEnum.PAGE.getRepresentation())).build();
    }
}

