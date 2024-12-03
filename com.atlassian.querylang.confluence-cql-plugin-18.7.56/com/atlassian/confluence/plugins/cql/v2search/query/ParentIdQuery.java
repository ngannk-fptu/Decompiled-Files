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
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class ParentIdQuery
implements SearchQuery {
    private static final String KEY = "parentId";
    private List<ContentId> parentIds;

    public ParentIdQuery(ContentId ... parentIds) {
        this.parentIds = ImmutableList.copyOf((Object[])parentIds);
    }

    public String getKey() {
        return KEY;
    }

    public List<ContentId> getParameters() {
        return this.parentIds;
    }

    public SearchQuery expand() {
        BooleanQuery.Builder builder = BooleanQuery.builder();
        for (ContentId contentId : this.parentIds) {
            SearchQuery attachmentQuery = (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery("page-or-comment-parentId", contentId.serialise())).addShould((Object)new TermQuery(SearchFieldNames.TYPE, ContentTypeEnum.PAGE.getRepresentation())).addShould((Object)new TermQuery(SearchFieldNames.TYPE, ContentTypeEnum.COMMENT.getRepresentation())).build();
            builder.addShould((Object)attachmentQuery);
        }
        return builder.build();
    }
}

