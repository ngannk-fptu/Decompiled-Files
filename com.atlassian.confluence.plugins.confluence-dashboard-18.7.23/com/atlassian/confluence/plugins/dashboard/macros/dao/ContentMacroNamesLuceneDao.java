/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.core.exception.InfrastructureException
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableSet
 *  javax.persistence.EntityManager
 */
package com.atlassian.confluence.plugins.dashboard.macros.dao;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNames;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNamesDao;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.core.exception.InfrastructureException;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.persistence.EntityManager;

public class ContentMacroNamesLuceneDao
implements ContentMacroNamesDao {
    private static final String CONTAINING_PAGE_ID = "containingPageId";
    private static final Set<String> FIELD_NAME_SET = ImmutableSet.of((Object)SearchFieldNames.LATEST_VERSION_ID, (Object)SearchFieldNames.HANDLE, (Object)SearchFieldNames.MACRO_NAME, (Object)SearchFieldNames.LAST_MODIFICATION_DATE, (Object)"containingPageId");
    private final SearchManager searchManager;
    private final EntityManagerProvider entityManagerProvider;

    public ContentMacroNamesLuceneDao(SearchManager searchManager, EntityManagerProvider entityManagerProvider) {
        this.searchManager = searchManager;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<ContentMacroNames> getContentMacroNames(Iterable<Content> contents, List<ContentMacroNames> knownMacroNamesAndComments, boolean includeComments) {
        SearchQuery query = this.getContentsQuery(contents, includeComments);
        List<ContentMacroNames> flattenedContentAndComments = this.readMacroNamesFlattened(query);
        List<ContentMacroNames> list = includeComments ? ContentMacroNames.makeCommentHierarchy(flattenedContentAndComments) : flattenedContentAndComments;
        return ContentMacroNames.merge(list, knownMacroNamesAndComments);
    }

    private SearchQuery getContentsQuery(Iterable<Content> contents, boolean includeComments) {
        SearchQuery[] queries = StreamSupport.stream(contents.spliterator(), false).map(content -> this.getContentQuery((Content)content, includeComments)).collect(Collectors.toList()).toArray(new SearchQuery[0]);
        return BooleanQuery.andQuery((SearchQuery[])queries);
    }

    private SearchQuery getContentQuery(Content content, boolean includeComments) {
        long contentId = content.getId().asLong();
        ArrayList<TermQuery> queries = new ArrayList<TermQuery>(4);
        queries.add(new TermQuery("handle", Page.class.getName() + "-" + contentId));
        queries.add(new TermQuery("handle", BlogPost.class.getName() + "-" + contentId));
        queries.add(new TermQuery("handle", CustomContentEntityObject.class.getName() + "-" + contentId));
        if (includeComments) {
            queries.add(new TermQuery(CONTAINING_PAGE_ID, Long.toString(contentId)));
        }
        return BooleanQuery.orQuery((SearchQuery[])queries.toArray(new SearchQuery[0]));
    }

    private List<ContentMacroNames> readMacroNamesFlattened(SearchQuery query) {
        try {
            SearchResults searchResults = this.searchManager.search((ISearch)new ContentSearch(query, null, 0, Integer.MAX_VALUE), FIELD_NAME_SET);
            ArrayList<ContentMacroNames> list = new ArrayList<ContentMacroNames>();
            for (SearchResult searchResult : searchResults) {
                ContentMacroNames macroNames = this.parseMacroNames(searchResult);
                if (macroNames == null) continue;
                list.add(macroNames);
            }
            return list;
        }
        catch (InvalidSearchException ex) {
            String message = String.format("Invalid search query %s; %s", query, ex.getMessage());
            throw new InfrastructureException(message, (Throwable)ex);
        }
    }

    private ContentMacroNames parseMacroNames(SearchResult searchResult) {
        HibernateHandle handle = ContentMacroNamesLuceneDao.parseHandle(searchResult);
        if (handle == null) {
            return null;
        }
        long contentId = handle.getId();
        Long commentParentContentId = handle.getClassName().equals(Comment.class.getName()) ? this.parseLong(searchResult.getField(CONTAINING_PAGE_ID)) : null;
        String contentOrCommandLastModified = searchResult.getField(SearchFieldNames.LAST_MODIFICATION_DATE);
        Set macroNames = searchResult.getFieldValues(SearchFieldNames.MACRO_NAME);
        if (!this.isLuceneRecordUpToDate(contentId, contentOrCommandLastModified)) {
            return null;
        }
        return new ContentMacroNames(contentId, commentParentContentId, macroNames, null);
    }

    private static HibernateHandle parseHandle(SearchResult searchResult) {
        String handleStr = searchResult.getField(SearchFieldNames.HANDLE);
        if (handleStr == null) {
            return null;
        }
        try {
            return new HibernateHandle(handleStr);
        }
        catch (ParseException ex) {
            throw Throwables.propagate((Throwable)ex);
        }
    }

    private Date getDbLastModified(long contentId) {
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        ContentEntityObject entity = (ContentEntityObject)entityManager.find(ContentEntityObject.class, (Object)contentId);
        return entity != null ? entity.getLastModificationDate() : null;
    }

    private boolean isLuceneRecordUpToDate(long contentId, String luceneLastModified) {
        if (luceneLastModified == null) {
            return false;
        }
        Date date = this.getDbLastModified(contentId);
        if (date == null) {
            return false;
        }
        long contentLastModified = date.getTime();
        return Objects.equals(this.luceneDateStringToMillis(luceneLastModified), contentLastModified);
    }

    private long luceneDateStringToMillis(String date) {
        return LuceneUtils.stringToDate((String)date).toInstant().toEpochMilli();
    }

    private Long parseLong(String str) {
        return str != null ? Long.valueOf(Long.parseLong(str)) : null;
    }
}

