/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.ImmutableMap
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 *  org.apache.lucene.search.SortField$Type
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.FieldSort;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

@Internal
public class FieldSortMapper
implements LuceneSortMapper<FieldSort> {
    private static final Map<SearchSort.Type, SortField.Type> MAPPERS = ImmutableMap.builder().put((Object)SearchSort.Type.STRING, (Object)SortField.Type.STRING).put((Object)SearchSort.Type.FLOAT, (Object)SortField.Type.FLOAT).put((Object)SearchSort.Type.DOUBLE, (Object)SortField.Type.DOUBLE).put((Object)SearchSort.Type.INTEGER, (Object)SortField.Type.INT).put((Object)SearchSort.Type.LONG, (Object)SortField.Type.LONG).build();

    @Override
    public Sort convertToLuceneSort(FieldSort searchSort) {
        if (SearchSort.Order.ASCENDING.equals((Object)searchSort.getOrder())) {
            return new Sort(new SortField(searchSort.getFieldName(), MAPPERS.get((Object)searchSort.getType())));
        }
        return new Sort(new SortField(searchSort.getFieldName(), MAPPERS.get((Object)searchSort.getType()), true));
    }
}

