/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria;

import org.glassfish.ha.store.criteria.Expression;

public final class Criteria<V> {
    private Class<V> entryClazz;
    private Expression<Boolean> expression;

    Criteria(Class<V> entryClazz) {
        this.entryClazz = entryClazz;
    }

    public Expression<Boolean> getExpression() {
        return this.expression;
    }

    public void setExpression(Expression<Boolean> expression) {
        this.expression = expression;
    }
}

