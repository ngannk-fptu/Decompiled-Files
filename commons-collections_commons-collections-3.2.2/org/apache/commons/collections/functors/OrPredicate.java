/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.PredicateDecorator;

public final class OrPredicate
implements Predicate,
PredicateDecorator,
Serializable {
    private static final long serialVersionUID = -8791518325735182855L;
    private final Predicate iPredicate1;
    private final Predicate iPredicate2;

    public static Predicate getInstance(Predicate predicate1, Predicate predicate2) {
        if (predicate1 == null || predicate2 == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new OrPredicate(predicate1, predicate2);
    }

    public OrPredicate(Predicate predicate1, Predicate predicate2) {
        this.iPredicate1 = predicate1;
        this.iPredicate2 = predicate2;
    }

    public boolean evaluate(Object object) {
        return this.iPredicate1.evaluate(object) || this.iPredicate2.evaluate(object);
    }

    public Predicate[] getPredicates() {
        return new Predicate[]{this.iPredicate1, this.iPredicate2};
    }
}

