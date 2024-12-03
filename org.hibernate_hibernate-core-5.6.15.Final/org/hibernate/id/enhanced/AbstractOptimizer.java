/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.enhanced;

import org.hibernate.HibernateException;
import org.hibernate.id.enhanced.Optimizer;

public abstract class AbstractOptimizer
implements Optimizer {
    protected final Class returnClass;
    protected final int incrementSize;

    AbstractOptimizer(Class returnClass, int incrementSize) {
        if (returnClass == null) {
            throw new HibernateException("return class is required");
        }
        this.returnClass = returnClass;
        this.incrementSize = incrementSize;
    }

    public final Class getReturnClass() {
        return this.returnClass;
    }

    @Override
    public final int getIncrementSize() {
        return this.incrementSize;
    }
}

