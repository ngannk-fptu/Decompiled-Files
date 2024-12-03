/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.contributors.search.Doc;
import com.atlassian.confluence.contributors.search.PageDescendantsSearcher;
import com.atlassian.confluence.contributors.search.PageQueryFactory;
import com.atlassian.confluence.contributors.search.PageSearcher;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value="pageSearcher")
@Internal
class DefaultPageSearcher
implements PageSearcher {
    private final SearchManager searchManager;
    private final PageQueryFactory queryFactory;
    private static final int MAX_LIMIT = 10000;
    private final PageDescendantsSearcher descendantsSearcher;

    @Autowired
    public DefaultPageSearcher(PageDescendantsSearcher descendantsSearcher, PageQueryFactory queryFactory, @ComponentImport SearchManager searchManager) {
        this.descendantsSearcher = Objects.requireNonNull(descendantsSearcher);
        this.queryFactory = Objects.requireNonNull(queryFactory);
        this.searchManager = Objects.requireNonNull(searchManager);
    }

    @Override
    public Iterable<Doc> getDocuments(MacroParameterModel params) {
        ContentSearch search = new ContentSearch(this.queryFactory.createPageQuery(params), null, 0, 10000);
        try {
            SearchResults searchResults = this.searchManager.search((ISearch)search, Doc.REQUESTED_FIELDS);
            ArrayList<Doc> list = new ArrayList<Doc>();
            StreamSupport.stream(searchResults.spliterator(), false).map(x -> new Doc(fieldName -> x.getField(fieldName), fieldName -> x.getFieldValues(fieldName).toArray(new String[0]))).forEach(list::add);
            Set<Long> pageIds = list.stream().map(Doc::getPageId).collect(Collectors.toSet());
            if (!pageIds.isEmpty()) {
                if ("descendants".equals(params.getScope())) {
                    this.descendantsSearcher.getDescendants(pageIds).forEach(list::add);
                } else if ("children".equals(params.getScope())) {
                    this.descendantsSearcher.getDirectChildren(pageIds).forEach(list::add);
                }
            }
            return list;
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
    }
}

