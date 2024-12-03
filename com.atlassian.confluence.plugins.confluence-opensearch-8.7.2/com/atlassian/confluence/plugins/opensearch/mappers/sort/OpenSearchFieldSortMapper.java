/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.confluence.search.v2.sort.FieldSort
 *  com.google.common.collect.ImmutableMap
 *  org.opensearch.client.opensearch._types.FieldSort
 *  org.opensearch.client.opensearch._types.FieldSort$Builder
 *  org.opensearch.client.opensearch._types.FieldSortNumericType
 *  org.opensearch.client.opensearch._types.SortOptions
 *  org.opensearch.client.opensearch._types.mapping.FieldType
 */
package com.atlassian.confluence.plugins.opensearch.mappers.sort;

import com.atlassian.confluence.plugins.opensearch.mappers.sort.OpenSearchSortMapper;
import com.atlassian.confluence.plugins.opensearch.mappers.sort.SortUtils;
import com.atlassian.confluence.search.v2.SearchSort;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.FieldSortNumericType;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.mapping.FieldType;

public class OpenSearchFieldSortMapper
implements OpenSearchSortMapper<com.atlassian.confluence.search.v2.sort.FieldSort> {
    private static final Map<SearchSort.Type, FieldSortNumericType> MAPPERS = ImmutableMap.builder().put((Object)SearchSort.Type.FLOAT, (Object)FieldSortNumericType.Double).put((Object)SearchSort.Type.DOUBLE, (Object)FieldSortNumericType.Double).put((Object)SearchSort.Type.INTEGER, (Object)FieldSortNumericType.Long).put((Object)SearchSort.Type.LONG, (Object)FieldSortNumericType.Long).build();

    @Override
    public String getKey() {
        return "fieldSort";
    }

    @Override
    public List<SortOptions> mapSortToOpenSearch(com.atlassian.confluence.search.v2.sort.FieldSort sort) {
        FieldSort fieldSort = FieldSort.of(builder -> {
            builder.field(sort.getFieldName()).unmappedType(FieldType.Text).order(SortUtils.toOpenSearchSortOrder(sort.getOrder()));
            Optional.ofNullable(MAPPERS.get(sort.getType())).ifPresent(arg_0 -> ((FieldSort.Builder)builder).numericType(arg_0));
            return builder;
        });
        return List.of(SortOptions.of(builder -> builder.field(fieldSort)));
    }
}

