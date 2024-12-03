/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.query.PagingPredicate;
import java.util.Map;

public final class PagingPredicateAccessor {
    private PagingPredicateAccessor() {
    }

    public static void setAnchor(PagingPredicate predicate, int page, Map.Entry anchor) {
        predicate.setAnchor(page, anchor);
    }

    public static Map.Entry<Integer, Map.Entry> getNearestAnchorEntry(PagingPredicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate.getNearestAnchorEntry();
    }
}

