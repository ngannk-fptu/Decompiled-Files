/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.AndResultSet;
import com.hazelcast.query.impl.OrResultSet;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.RangePredicate;
import java.util.Collection;

public final class PredicateUtils {
    private static final int EXPECTED_AVERAGE_COMPONENT_NAME_LENGTH = 16;
    private static final String THIS_DOT = "this.";
    private static final String KEY_HASH = "__key#";
    private static final String KEY_DOT = "__key.";

    private PredicateUtils() {
    }

    public static int estimatedSizeOf(Collection<QueryableEntry> result) {
        if (result instanceof AndResultSet) {
            return ((AndResultSet)result).estimatedSize();
        }
        if (result instanceof OrResultSet) {
            return ((OrResultSet)result).estimatedSize();
        }
        return result.size();
    }

    public static boolean isNull(Comparable value) {
        return value == null || value == AbstractIndex.NULL;
    }

    public static boolean isRangePredicate(Predicate predicate) {
        return predicate instanceof RangePredicate && !(predicate instanceof NotEqualPredicate);
    }

    public static boolean isEqualPredicate(Predicate predicate) {
        return predicate instanceof EqualPredicate && !(predicate instanceof NotEqualPredicate);
    }

    public static String canonicalizeAttribute(String attribute) {
        if (attribute.startsWith(THIS_DOT)) {
            return attribute.substring(THIS_DOT.length());
        }
        if (attribute.startsWith(KEY_HASH)) {
            return KEY_DOT + attribute.substring(KEY_HASH.length());
        }
        return attribute;
    }

    public static String constructCanonicalCompositeIndexName(String[] components) {
        assert (components.length > 1);
        StringBuilder builder = new StringBuilder(components.length * 16);
        for (String component : components) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(component);
        }
        return builder.toString();
    }
}

