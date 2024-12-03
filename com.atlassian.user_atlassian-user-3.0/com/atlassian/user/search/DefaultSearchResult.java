/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search;

import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultSearchResult<T>
implements SearchResult<T> {
    private Map<String, Pager<T>> repoKeyToResults = new LinkedHashMap<String, Pager<T>>();

    public DefaultSearchResult() {
    }

    public DefaultSearchResult(Pager<T> result, String repositoryKey) {
        this.addToResults(repositoryKey, result);
    }

    @Override
    public Pager<T> pager() {
        return PagerFactory.getPager(new ArrayList<Pager<T>>(this.repoKeyToResults.values()));
    }

    public void addToResults(String repositoryKey, Pager<T> pager) {
        this.repoKeyToResults.put(repositoryKey, pager);
    }

    @Override
    public Pager<T> pager(String repoKey) {
        return this.repoKeyToResults.get(repoKey);
    }

    @Override
    public Set<String> repositoryKeyset() {
        return this.repoKeyToResults.keySet();
    }
}

