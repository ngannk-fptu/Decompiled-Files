/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.content.typebinding;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

class DepthFirstPreOrderComparator<T>
implements Comparator<T> {
    private Function<T, @NonNull List<T>> ancestorsGetter;
    private Comparator<T> tComparator;
    private Map<T, List<T>> branchMap = Maps.newHashMap();

    DepthFirstPreOrderComparator(Function<T, @NonNull List<T>> ancestorsGetter, Comparator<T> tComparator) {
        this.ancestorsGetter = ancestorsGetter;
        this.tComparator = tComparator;
    }

    @Override
    public int compare(T o1, T o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        List<T> o1Branch = this.getBranch(o1);
        List<T> o2Branch = this.getBranch(o2);
        int o1Size = o1Branch.size();
        int o2Size = o2Branch.size();
        for (int i = 0; i < Math.min(o1Size, o2Size); ++i) {
            T o2Node;
            T o1Node = o1Branch.get(i);
            if (o1Node.equals(o2Node = o2Branch.get(i))) continue;
            return this.tComparator.compare(o1Node, o2Node);
        }
        return o1Size - o2Size;
    }

    private List<T> getBranch(T o1) {
        if (this.branchMap.containsKey(o1)) {
            return this.branchMap.get(o1);
        }
        ImmutableList branch = ImmutableList.builder().addAll((Iterable)this.ancestorsGetter.apply(o1)).add(o1).build();
        this.branchMap.put(o1, (List<T>)branch);
        return branch;
    }
}

