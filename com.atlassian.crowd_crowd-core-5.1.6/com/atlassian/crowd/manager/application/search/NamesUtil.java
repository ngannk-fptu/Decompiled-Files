/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.NameComparator
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.model.NameComparator;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NamesUtil {
    private NamesUtil() {
    }

    public static <T> List<String> namesOf(Collection<T> entities) {
        if (entities.isEmpty() || entities.iterator().next() instanceof String) {
            return ImmutableList.copyOf(entities);
        }
        return entities.stream().map(NamesUtil.nameGetter(entities)).collect(Collectors.toList());
    }

    public static <T> List<T> filterByName(Collection<T> results, Predicate<String> filter) {
        if (results.isEmpty()) {
            return ImmutableList.of();
        }
        Function nameGetter = NamesUtil.nameGetter(results);
        return results.stream().filter(entity -> filter.test((String)nameGetter.apply(entity))).collect(Collectors.toList());
    }

    public static <T> List<T> filterOutByName(List<T> results, Predicate<String> filter) {
        return NamesUtil.filterByName(results, filter.negate());
    }

    private static <T> Function<T, String> nameGetter(Collection<T> collection) {
        return NameComparator.nameGetter(collection.iterator().next().getClass());
    }
}

