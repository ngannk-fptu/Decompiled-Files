/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.FunctorUtils;
import org.apache.commons.collections.functors.PredicateDecorator;
import org.apache.commons.collections.functors.TruePredicate;

public final class AllPredicate
implements Predicate,
PredicateDecorator,
Serializable {
    private static final long serialVersionUID = -3094696765038308799L;
    private final Predicate[] iPredicates;

    public static Predicate getInstance(Predicate[] predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return TruePredicate.INSTANCE;
        }
        if (predicates.length == 1) {
            return predicates[0];
        }
        predicates = FunctorUtils.copy(predicates);
        return new AllPredicate(predicates);
    }

    public static Predicate getInstance(Collection predicates) {
        Predicate[] preds = FunctorUtils.validate(predicates);
        if (preds.length == 0) {
            return TruePredicate.INSTANCE;
        }
        if (preds.length == 1) {
            return preds[0];
        }
        return new AllPredicate(preds);
    }

    public AllPredicate(Predicate[] predicates) {
        this.iPredicates = predicates;
    }

    public boolean evaluate(Object object) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (this.iPredicates[i].evaluate(object)) continue;
            return false;
        }
        return true;
    }

    public Predicate[] getPredicates() {
        return this.iPredicates;
    }
}

