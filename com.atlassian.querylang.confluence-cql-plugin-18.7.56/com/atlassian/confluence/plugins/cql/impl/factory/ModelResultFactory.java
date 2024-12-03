/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchResult
 */
package com.atlassian.confluence.plugins.cql.impl.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchResult;
import java.util.Map;
import java.util.Set;

public interface ModelResultFactory<T> {
    public Map<SearchResult, T> buildFrom(Iterable<SearchResult> var1, Expansions var2);

    public boolean handles(ContentTypeEnum var1);

    public Set<String> getRequiredIndexFields();
}

