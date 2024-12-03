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

public class ContentContainerQuery
implements SearchQuery {
    public static final String COMMENT_CONTAINER_FIELD = "containingPageId";
    public static final String ATTACHMENT_CONTAINER_FIELD = "ancestorIds";
    private static final String KEY = "contentContainer";
    private List<ContentId> contentIds;

    public ContentContainerQuery(ContentId ... contentIds) {
        this((Iterable<ContentId>)ImmutableList.copyOf((Object[])contentIds));
    }

    public ContentContainerQuery(Iterable<ContentId> contentIds) {
        this.contentIds = ImmutableList.copyOf(contentIds);
    }

    public String getKey() {
        return KEY;
    }

    public List<ContentId> getParameters() {
        return this.contentIds;
    }

    public SearchQuery expand() {
        BooleanQuery.Builder builder = BooleanQuery.builder();
        for (ContentId contentId : this.contentIds) {
            builder.addShould((Object)new TermQuery(COMMENT_CONTAINER_FIELD, contentId.serialise()));
            SearchQuery attachmentQuery = (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery(ATTACHMENT_CONTAINER_FIELD, contentId.serialise())).addMust((Object)new TermQuery(SearchFieldNames.TYPE, ContentTypeEnum.ATTACHMENT.getRepresentation())).build();
            builder.addShould((Object)attachmentQuery);
        }
        return builder.build();
    }
}

