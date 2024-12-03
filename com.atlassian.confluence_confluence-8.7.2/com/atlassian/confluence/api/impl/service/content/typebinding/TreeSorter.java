/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.api.impl.service.content.typebinding;

import com.atlassian.confluence.api.impl.service.content.typebinding.DepthFirstPreOrderComparator;
import com.google.common.base.Function;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TreeSorter {
    public static <T> List<T> depthFirstPreOrdered(Collection<T> unsorted, java.util.function.Function<T, List<T>> ancestorsGetter, Comparator<T> tComparator) {
        DepthFirstPreOrderComparator<T> comparator = new DepthFirstPreOrderComparator<T>(ancestorsGetter, tComparator);
        return Collections.unmodifiableList(unsorted.stream().sorted(comparator).collect(Collectors.toList()));
    }

    @Deprecated
    public static <T> List<T> depthFirstPreOrderSort(Collection<T> unsorted, Function<T, List<T>> ancestorsGetter, Comparator<T> tComparator) {
        return TreeSorter.depthFirstPreOrdered(unsorted, ancestorsGetter, tComparator);
    }
}

