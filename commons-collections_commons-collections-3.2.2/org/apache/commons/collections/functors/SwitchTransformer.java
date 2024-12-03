/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.FunctorUtils;

public class SwitchTransformer
implements Transformer,
Serializable {
    private static final long serialVersionUID = -6404460890903469332L;
    private final Predicate[] iPredicates;
    private final Transformer[] iTransformers;
    private final Transformer iDefault;

    public static Transformer getInstance(Predicate[] predicates, Transformer[] transformers, Transformer defaultTransformer) {
        FunctorUtils.validate(predicates);
        FunctorUtils.validate(transformers);
        if (predicates.length != transformers.length) {
            throw new IllegalArgumentException("The predicate and transformer arrays must be the same size");
        }
        if (predicates.length == 0) {
            return defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer;
        }
        predicates = FunctorUtils.copy(predicates);
        transformers = FunctorUtils.copy(transformers);
        return new SwitchTransformer(predicates, transformers, defaultTransformer);
    }

    public static Transformer getInstance(Map predicatesAndTransformers) {
        Transformer[] transformers = null;
        Predicate[] preds = null;
        if (predicatesAndTransformers == null) {
            throw new IllegalArgumentException("The predicate and transformer map must not be null");
        }
        if (predicatesAndTransformers.size() == 0) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        Transformer defaultTransformer = (Transformer)predicatesAndTransformers.remove(null);
        int size = predicatesAndTransformers.size();
        if (size == 0) {
            return defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer;
        }
        transformers = new Transformer[size];
        preds = new Predicate[size];
        int i = 0;
        Iterator it = predicatesAndTransformers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            preds[i] = (Predicate)entry.getKey();
            transformers[i] = (Transformer)entry.getValue();
            ++i;
        }
        return new SwitchTransformer(preds, transformers, defaultTransformer);
    }

    public SwitchTransformer(Predicate[] predicates, Transformer[] transformers, Transformer defaultTransformer) {
        this.iPredicates = predicates;
        this.iTransformers = transformers;
        this.iDefault = defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer;
    }

    public Object transform(Object input) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (!this.iPredicates[i].evaluate(input)) continue;
            return this.iTransformers[i].transform(input);
        }
        return this.iDefault.transform(input);
    }

    public Predicate[] getPredicates() {
        return this.iPredicates;
    }

    public Transformer[] getTransformers() {
        return this.iTransformers;
    }

    public Transformer getDefaultTransformer() {
        return this.iDefault;
    }
}

