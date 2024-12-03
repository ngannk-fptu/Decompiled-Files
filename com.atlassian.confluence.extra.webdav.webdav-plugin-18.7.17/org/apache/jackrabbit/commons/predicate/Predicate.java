/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

public interface Predicate {
    public static final Predicate TRUE = new Predicate(){

        @Override
        public boolean evaluate(Object object) {
            return true;
        }
    };
    public static final Predicate FALSE = new Predicate(){

        @Override
        public boolean evaluate(Object object) {
            return false;
        }
    };

    public boolean evaluate(Object var1);
}

