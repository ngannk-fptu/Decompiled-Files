/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntityList;
import com.atlassian.confluence.plugins.rest.entities.documentation.ContentEntityExampleDocument;
import java.util.ArrayList;

public class SearchResultEntityExampleDocument {
    public static final SearchResultEntityList SEARCH_RESULT_ENTITY_LIST = new SearchResultEntityList();

    static {
        ArrayList<SearchResultEntity> results = new ArrayList<SearchResultEntity>(2);
        results.add(ContentEntityExampleDocument.DEMO_PAGE);
        results.add(ContentEntityExampleDocument.DEMO_PAGE);
        SEARCH_RESULT_ENTITY_LIST.setResults(results);
    }
}

