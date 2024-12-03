/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.PredicateDecorator;

public final class NullIsFalsePredicate
implements Predicate,
PredicateDecorator,
Serializable {
    private static final long serialVersionUID = -2997501534564735525L;
    private final Predicate iPredicate;

    public static Predicate getInstance(Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new NullIsFalsePredicate(predicate);
    }

    public NullIsFalsePredicate(Predicate predicate) {
        this.iPredicate = predicate;
    }

    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }
        return this.iPredicate.evaluate(object);
    }

    public Predicate[] getPredicates() {
        return new Predicate[]{this.iPredicate};
    }
}

