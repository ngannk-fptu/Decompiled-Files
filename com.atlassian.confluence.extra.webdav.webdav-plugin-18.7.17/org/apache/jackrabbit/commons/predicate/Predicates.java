/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import org.apache.jackrabbit.commons.predicate.Predicate;

public class Predicates {
    public static Predicate and(final Predicate ... predicates) {
        return new Predicate(){

            @Override
            public boolean evaluate(Object object) {
                for (Predicate predicate : predicates) {
                    if (predicate.evaluate(object)) continue;
                    return false;
                }
                return true;
            }
        };
    }

    public static Predicate or(final Predicate ... predicates) {
        return new Predicate(){

            @Override
            public boolean evaluate(Object object) {
                for (Predicate predicate : predicates) {
                    if (!predicate.evaluate(object)) continue;
                    return true;
                }
                return false;
            }
        };
    }

    public static Predicate not(final Predicate predicate) {
        return new Predicate(){

            @Override
            public boolean evaluate(Object object) {
                return !predicate.evaluate(object);
            }
        };
    }
}

