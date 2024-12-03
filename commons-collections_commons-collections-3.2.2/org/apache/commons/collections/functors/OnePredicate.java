/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.FalsePredicate;
import org.apache.commons.collections.functors.FunctorUtils;
import org.apache.commons.collections.functors.PredicateDecorator;

public final class OnePredicate
implements Predicate,
PredicateDecorator,
Serializable {
    private static final long serialVersionUID = -8125389089924745785L;
    private final Predicate[] iPredicates;

    public static Predicate getInstance(Predicate[] predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return FalsePredicate.INSTANCE;
        }
        if (predicates.length == 1) {
            return predicates[0];
        }
        predicates = FunctorUtils.copy(predicates);
        return new OnePredicate(predicates);
    }

    public static Predicate getInstance(Collection predicates) {
        Predicate[] preds = FunctorUtils.validate(predicates);
        return new OnePredicate(preds);
    }

    public OnePredicate(Predicate[] predicates) {
        this.iPredicates = predicates;
    }

    public boolean evaluate(Object object) {
        boolean match = false;
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (!this.iPredicates[i].evaluate(object)) continue;
            if (match) {
                return false;
            }
            match = true;
        }
        return match;
    }

    public Predicate[] getPredicates() {
        return this.iPredicates;
    }
}

