/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.PredicateDecorator;

public final class NullIsExceptionPredicate
implements Predicate,
PredicateDecorator,
Serializable {
    private static final long serialVersionUID = 3243449850504576071L;
    private final Predicate iPredicate;

    public static Predicate getInstance(Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new NullIsExceptionPredicate(predicate);
    }

    public NullIsExceptionPredicate(Predicate predicate) {
        this.iPredicate = predicate;
    }

    public boolean evaluate(Object object) {
        if (object == null) {
            throw new FunctorException("Input Object must not be null");
        }
        return this.iPredicate.evaluate(object);
    }

    public Predicate[] getPredicates() {
        return new Predicate[]{this.iPredicate};
    }
}

