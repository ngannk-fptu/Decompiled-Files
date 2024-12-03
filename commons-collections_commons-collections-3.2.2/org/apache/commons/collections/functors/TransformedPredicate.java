/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.PredicateDecorator;

public final class TransformedPredicate
implements Predicate,
PredicateDecorator,
Serializable {
    private static final long serialVersionUID = -5596090919668315834L;
    private final Transformer iTransformer;
    private final Predicate iPredicate;

    public static Predicate getInstance(Transformer transformer, Predicate predicate) {
        if (transformer == null) {
            throw new IllegalArgumentException("The transformer to call must not be null");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("The predicate to call must not be null");
        }
        return new TransformedPredicate(transformer, predicate);
    }

    public TransformedPredicate(Transformer transformer, Predicate predicate) {
        this.iTransformer = transformer;
        this.iPredicate = predicate;
    }

    public boolean evaluate(Object object) {
        Object result = this.iTransformer.transform(object);
        return this.iPredicate.evaluate(result);
    }

    public Predicate[] getPredicates() {
        return new Predicate[]{this.iPredicate};
    }

    public Transformer getTransformer() {
        return this.iTransformer;
    }
}

