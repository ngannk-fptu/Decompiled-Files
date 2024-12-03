/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.Expandable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SearchExpander {
    public static <T extends Expandable<T>> T expandAll(T expandable) {
        if (expandable == null) {
            return null;
        }
        Expandable expanded = (Expandable)expandable.expand();
        if (expanded == expandable) {
            return expandable;
        }
        return (T)SearchExpander.expandAll(expanded);
    }

    public static <T extends Expandable<T>> List<T> expandAll(Collection<T> expandables) {
        return expandables.stream().map(SearchExpander::expandAll).collect(Collectors.toList());
    }
}

