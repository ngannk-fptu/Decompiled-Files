/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.search.v2.BaseSearchResult;
import com.atlassian.confluence.search.v2.ISearchResultConverter;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import java.util.ArrayList;
import java.util.List;

public class SearchResultConverter
implements ISearchResultConverter {
    private final AnyTypeDao anyTypeDao;
    private final ContentEntityObjectDao contentEntityObjectDao;

    public SearchResultConverter(AnyTypeDao anyTypeDao, ContentEntityObjectDao contentEntityObjectDao) {
        this.anyTypeDao = anyTypeDao;
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    @Override
    public List<Searchable> convertToEntities(Iterable<SearchResult> searchResults, SearchManager.EntityVersionPolicy versionPolicy) {
        ArrayList<Searchable> convertedEntities = new ArrayList<Searchable>();
        for (BaseSearchResult baseSearchResult : searchResults) {
            Searchable searchable = (Searchable)this.anyTypeDao.findByHandle(baseSearchResult.getHandle());
            if (versionPolicy == SearchManager.EntityVersionPolicy.INDEXED_VERSION && searchable instanceof ContentEntityObject) {
                int intIndexVersion;
                String indexedVersion = baseSearchResult.getField(SearchFieldNames.CONTENT_VERSION);
                ContentEntityObject content = (ContentEntityObject)searchable;
                if (indexedVersion != null && (intIndexVersion = Integer.parseInt(indexedVersion)) != content.getVersion()) {
                    searchable = this.contentEntityObjectDao.getVersion(content.getId(), intIndexVersion);
                }
            }
            if (searchable == null) continue;
            convertedEntities.add(searchable);
        }
        return convertedEntities;
    }
}

