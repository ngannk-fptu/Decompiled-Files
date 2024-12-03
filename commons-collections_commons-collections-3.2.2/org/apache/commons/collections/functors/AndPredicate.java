/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.PredicateDecorator;

public final class AndPredicate
implements Predicate,
PredicateDecorator,
Serializable {
    private static final long serialVersionUID = 4189014213763186912L;
    private final Predicate iPredicate1;
    private final Predicate iPredicate2;

    public static Predicate getInstance(Predicate predicate1, Predicate predicate2) {
        if (predicate1 == null || predicate2 == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new AndPredicate(predicate1, predicate2);
    }

    public AndPredicate(Predicate predicate1, Predicate predicate2) {
        this.iPredicate1 = predicate1;
        this.iPredicate2 = predicate2;
    }

    public boolean evaluate(Object object) {
        return this.iPredicate1.evaluate(object) && this.iPredicate2.evaluate(object);
    }

    public Predicate[] getPredicates() {
        return new Predicate[]{this.iPredicate1, this.iPredicate2};
    }
}

