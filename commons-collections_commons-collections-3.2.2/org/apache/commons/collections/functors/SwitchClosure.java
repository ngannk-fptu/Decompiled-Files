/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.FunctorUtils;
import org.apache.commons.collections.functors.NOPClosure;

public class SwitchClosure
implements Closure,
Serializable {
    private static final long serialVersionUID = 3518477308466486130L;
    private final Predicate[] iPredicates;
    private final Closure[] iClosures;
    private final Closure iDefault;

    public static Closure getInstance(Predicate[] predicates, Closure[] closures, Closure defaultClosure) {
        FunctorUtils.validate(predicates);
        FunctorUtils.validate(closures);
        if (predicates.length != closures.length) {
            throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
        }
        if (predicates.length == 0) {
            return defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure;
        }
        predicates = FunctorUtils.copy(predicates);
        closures = FunctorUtils.copy(closures);
        return new SwitchClosure(predicates, closures, defaultClosure);
    }

    public static Closure getInstance(Map predicatesAndClosures) {
        Closure[] closures = null;
        Predicate[] preds = null;
        if (predicatesAndClosures == null) {
            throw new IllegalArgumentException("The predicate and closure map must not be null");
        }
        if (predicatesAndClosures.size() == 0) {
            return NOPClosure.INSTANCE;
        }
        Closure defaultClosure = (Closure)predicatesAndClosures.remove(null);
        int size = predicatesAndClosures.size();
        if (size == 0) {
            return defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure;
        }
        closures = new Closure[size];
        preds = new Predicate[size];
        int i = 0;
        Iterator it = predicatesAndClosures.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            preds[i] = (Predicate)entry.getKey();
            closures[i] = (Closure)entry.getValue();
            ++i;
        }
        return new SwitchClosure(preds, closures, defaultClosure);
    }

    public SwitchClosure(Predicate[] predicates, Closure[] closures, Closure defaultClosure) {
        this.iPredicates = predicates;
        this.iClosures = closures;
        this.iDefault = defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure;
    }

    public void execute(Object input) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (!this.iPredicates[i].evaluate(input)) continue;
            this.iClosures[i].execute(input);
            return;
        }
        this.iDefault.execute(input);
    }

    public Predicate[] getPredicates() {
        return this.iPredicates;
    }

    public Closure[] getClosures() {
        return this.iClosures;
    }

    public Closure getDefaultClosure() {
        return this.iDefault;
    }
}

