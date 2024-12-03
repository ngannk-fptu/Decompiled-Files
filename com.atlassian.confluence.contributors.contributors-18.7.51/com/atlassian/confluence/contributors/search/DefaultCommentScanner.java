/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.search.CommentScanner;
import com.atlassian.confluence.contributors.search.Doc;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value="commentScanner")
@Internal
class DefaultCommentScanner
implements CommentScanner {
    private final int MAX_CLAUSE_COUNT = 1024;
    private final SearchManager searchManager;

    @Autowired
    public DefaultCommentScanner(@ComponentImport SearchManager searchManager) {
        this.searchManager = Objects.requireNonNull(searchManager);
    }

    @Override
    public void scan(Set<Long> pageIds, Consumer<Doc> consumer) {
        if (pageIds.isEmpty()) {
            return;
        }
        SearchQuery searchQuery = this.createQuery(pageIds);
        try {
            this.searchManager.scan(EnumSet.of(SearchIndex.CONTENT), searchQuery, Doc.REQUESTED_FIELDS, x -> consumer.accept(new Doc(fieldName -> x.getOrDefault(fieldName, new String[0]))));
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
    }

    private SearchQuery createQuery(Set<Long> pageIds) {
        if (pageIds.isEmpty()) {
            return MatchNoDocsQuery.getInstance();
        }
        ArrayList subLists = Lists.newArrayList((Iterable)Iterables.partition(pageIds, (int)1024));
        SearchQuery searchQuery = (SearchQuery)BooleanQuery.builder().addShould((Collection)subLists.stream().map(subset -> (SearchQuery)BooleanQuery.builder().addShould((Collection)subset.stream().map(x -> new TermQuery("containingPageId", String.valueOf(x))).collect(Collectors.toList())).build()).collect(Collectors.toList())).build();
        return (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery(SearchFieldNames.TYPE, "comment")).addMust((Object)searchQuery).build();
    }
}

