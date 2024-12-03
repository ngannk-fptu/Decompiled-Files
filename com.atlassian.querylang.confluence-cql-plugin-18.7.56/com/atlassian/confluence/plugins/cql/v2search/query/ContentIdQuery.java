/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

public class ContentIdQuery
implements SearchQuery {
    public static final String KEY = "idField";
    static final List<Class<? extends Searchable>> SUPPORTED_CLASSES = ImmutableList.builder().add(Page.class).add(BlogPost.class).add(Comment.class).add(CustomContentEntityObject.class).add(Attachment.class).build();
    private final List<ContentId> contentIds;
    private final List<Class<? extends Searchable>> supportedClass;
    private static final String HANDLE_FIELD_VALUE_PATTERN = "%s-%d";

    static String buildHandleFieldValue(ContentId contentId, Class<? extends Searchable> searchableClass) {
        return String.format(HANDLE_FIELD_VALUE_PATTERN, searchableClass.getName(), contentId.asLong());
    }

    public ContentIdQuery(ContentId ... contentIds) {
        this(Arrays.asList(contentIds));
    }

    public ContentIdQuery(Iterable<ContentId> contentIds) {
        this(contentIds, SUPPORTED_CLASSES);
    }

    public ContentIdQuery(Iterable<ContentId> contentIds, List<Class<? extends Searchable>> supportedClass) {
        this.contentIds = ImmutableList.copyOf(contentIds);
        this.supportedClass = supportedClass == null || supportedClass.isEmpty() ? SUPPORTED_CLASSES : supportedClass;
    }

    public String getKey() {
        return KEY;
    }

    public List<ContentId> getParameters() {
        return this.contentIds;
    }

    public List<ContentId> getContentIds() {
        return this.contentIds;
    }

    public List<Class<? extends Searchable>> getSupportedClass() {
        return this.supportedClass;
    }

    public SearchQuery expand() {
        BooleanQuery.Builder boolQueryBuilder = BooleanQuery.builder();
        for (ContentId contentId : this.contentIds) {
            for (Class<? extends Searchable> searchableClass : this.supportedClass) {
                String fieldValue = ContentIdQuery.buildHandleFieldValue(contentId, searchableClass);
                boolQueryBuilder.addShould((Object)new TermQuery(SearchFieldNames.HANDLE, fieldValue));
            }
        }
        return boolQueryBuilder.build();
    }
}

