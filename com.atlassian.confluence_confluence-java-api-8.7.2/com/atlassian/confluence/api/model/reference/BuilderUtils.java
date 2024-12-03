/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.CollapsedList;
import com.atlassian.confluence.api.model.reference.CollapsedMap;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.nav.Navigation;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;

public class BuilderUtils {
    public static <T> PageResponse<T> collapsedPageResponse(Navigation.Builder navBuilder) {
        return new CollapsedList(navBuilder);
    }

    public static <T> List<T> collapsedList() {
        return new CollapsedList();
    }

    public static <T> List<T> collapsedList(Navigation.Builder navBuilder) {
        return new CollapsedList(navBuilder);
    }

    public static <K, V> Map<K, V> collapsedMap() {
        return BuilderUtils.collapsedMap(null);
    }

    public static <K, V> Map<K, V> collapsedMap(Navigation.Builder navBuilder) {
        return new CollapsedMap(navBuilder);
    }

    public static <K, V> Map<K, V> modelMap(ModelMapBuilder<? extends K, ? extends V> mapBuilder) {
        Map<? extends K, ? extends V> map = mapBuilder != null ? mapBuilder.build() : null;
        return BuilderUtils.modelMap(map);
    }

    @Deprecated
    public static <K, V> Map<K, V> modelMap(ImmutableMap.Builder<? extends K, ? extends V> mapBuilder) {
        ImmutableMap map = mapBuilder != null ? mapBuilder.build() : null;
        return BuilderUtils.modelMap(map);
    }

    public static <K, V> Map<K, V> modelMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return new CollapsedMap();
        }
        ModelMapBuilder<? extends K, ? extends V> builder = ModelMapBuilder.newInstance();
        return builder.copy(map).build();
    }
}

