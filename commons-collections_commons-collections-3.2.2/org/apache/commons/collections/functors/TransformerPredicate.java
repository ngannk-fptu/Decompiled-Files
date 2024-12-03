/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

public final class TransformerPredicate
implements Predicate,
Serializable {
    private static final long serialVersionUID = -2407966402920578741L;
    private final Transformer iTransformer;

    public static Predicate getInstance(Transformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("The transformer to call must not be null");
        }
        return new TransformerPredicate(transformer);
    }

    public TransformerPredicate(Transformer transformer) {
        this.iTransformer = transformer;
    }

    public boolean evaluate(Object object) {
        Object result = this.iTransformer.transform(object);
        if (!(result instanceof Boolean)) {
            throw new FunctorException("Transformer must return an instanceof Boolean, it was a " + (result == null ? "null object" : result.getClass().getName()));
        }
        return (Boolean)result;
    }

    public Transformer getTransformer() {
        return this.iTransformer;
    }
}

