/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 *  org.apache.lucene.search.SortField$Type
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchSort;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

@Deprecated
public class DefaultSortMapper
implements LuceneSortMapper {
    private final String sortFieldName;
    private Sort sortAsc;
    private Sort sortDec;

    public DefaultSortMapper(String columnName, String type) {
        this.sortFieldName = columnName;
        SortField.Type numericaltype = "BYTE".equalsIgnoreCase(type) ? SortField.Type.BYTE : ("DOC".equalsIgnoreCase(type) ? SortField.Type.DOC : ("DOUBLE".equalsIgnoreCase(type) ? SortField.Type.DOUBLE : ("FLOAT".equalsIgnoreCase(type) ? SortField.Type.FLOAT : ("LONG".equalsIgnoreCase(type) ? SortField.Type.LONG : ("INT".equalsIgnoreCase(type) ? SortField.Type.INT : ("SCORE".equalsIgnoreCase(type) ? SortField.Type.SCORE : ("SHORT".equalsIgnoreCase(type) ? SortField.Type.SHORT : ("STRING".equalsIgnoreCase(type) ? SortField.Type.STRING : ("STRING_VAL".equalsIgnoreCase(type) ? SortField.Type.STRING_VAL : SortField.Type.STRING)))))))));
        this.sortAsc = new Sort(new SortField(columnName, numericaltype));
        this.sortDec = new Sort(new SortField(columnName, numericaltype, true));
    }

    public Sort convertToLuceneSort(SearchSort searchSort) {
        if (SearchSort.Order.ASCENDING.equals((Object)searchSort.getOrder())) {
            return this.sortAsc;
        }
        return this.sortDec;
    }

    public String getSortFieldName() {
        return this.sortFieldName;
    }
}

