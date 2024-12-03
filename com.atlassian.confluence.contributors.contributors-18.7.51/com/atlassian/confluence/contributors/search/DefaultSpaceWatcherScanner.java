/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.search.v2.BooleanQueryBuilder
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.search.SpaceWatcherScanner;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value="spaceWatcherScanner")
@Internal
class DefaultSpaceWatcherScanner
implements SpaceWatcherScanner {
    private final SearchManager searchManager;

    @Autowired
    public DefaultSpaceWatcherScanner(@ComponentImport SearchManager searchManager) {
        this.searchManager = Objects.requireNonNull(searchManager);
    }

    @Override
    public void scan(Set<String> spaceKeys, BiConsumer<String, String> consumer) {
        if (spaceKeys.isEmpty()) {
            return;
        }
        try {
            this.searchManager.scan(EnumSet.of(SearchIndex.CONTENT), this.createSpaceQuery(spaceKeys), (Set)ImmutableSet.of((Object)SearchFieldNames.HANDLE, (Object)"watchers", (Object)"key"), x -> {
                for (String userKey : x.getOrDefault("watchers", new String[0])) {
                    String[] values = x.getOrDefault("key", new String[0]);
                    consumer.accept(values.length == 0 ? "" : values[0], userKey);
                }
            });
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
    }

    private SearchQuery createSpaceQuery(Set<String> spaceKeys) {
        if (spaceKeys.isEmpty()) {
            return MatchNoDocsQuery.getInstance();
        }
        BooleanQueryBuilder keyBuilder = BooleanQuery.builder().addShould((Collection)spaceKeys.stream().map(x -> new TermQuery("key", x)).collect(Collectors.toList()));
        return (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery(SearchFieldNames.TYPE, "space")).addMust((Object)((SearchQuery)keyBuilder.build())).build();
    }
}

