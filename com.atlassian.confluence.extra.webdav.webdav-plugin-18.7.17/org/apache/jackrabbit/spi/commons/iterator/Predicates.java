/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.iterator;

import org.apache.jackrabbit.spi.commons.iterator.Predicate;

public final class Predicates {
    public static final Predicate TRUE = new Predicate(){

        public boolean evaluate(Object arg) {
            return true;
        }
    };
    public static final Predicate FALSE = new Predicate(){

        public boolean evaluate(Object arg) {
            return false;
        }
    };

    private Predicates() {
    }

    public static <T> Predicate<T> TRUE() {
        return TRUE;
    }

    public static <T> Predicate<T> FALSE() {
        return FALSE;
    }
}

