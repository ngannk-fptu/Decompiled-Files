/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.Numbers;
import java.util.Comparator;

public final class Comparables {
    public static final Comparator<Comparable> COMPARATOR = new Comparator<Comparable>(){

        @Override
        public int compare(Comparable lhs, Comparable rhs) {
            return Comparables.compare(lhs, rhs);
        }
    };

    private Comparables() {
    }

    public static boolean equal(Comparable lhs, Comparable rhs) {
        assert (lhs != null);
        if (rhs == null) {
            return false;
        }
        if (lhs.getClass() == rhs.getClass()) {
            return lhs.equals(rhs);
        }
        if (lhs instanceof Number && rhs instanceof Number) {
            return Numbers.equal((Number)((Object)lhs), (Number)((Object)rhs));
        }
        return lhs.equals(rhs);
    }

    public static int compare(Comparable lhs, Comparable rhs) {
        assert (lhs != null);
        assert (rhs != null);
        if (lhs.getClass() == rhs.getClass()) {
            return lhs.compareTo(rhs);
        }
        if (lhs instanceof Number && rhs instanceof Number) {
            return Numbers.compare(lhs, rhs);
        }
        return lhs.compareTo(rhs);
    }

    public static Comparable canonicalizeForHashLookup(Comparable value) {
        if (value instanceof Number) {
            return Numbers.canonicalizeForHashLookup(value);
        }
        return value;
    }
}

