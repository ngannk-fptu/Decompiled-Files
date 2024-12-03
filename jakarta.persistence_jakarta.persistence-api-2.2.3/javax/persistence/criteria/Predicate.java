/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.List;
import javax.persistence.criteria.Expression;

public interface Predicate
extends Expression<Boolean> {
    public BooleanOperator getOperator();

    public boolean isNegated();

    public List<Expression<Boolean>> getExpressions();

    public Predicate not();

    public static enum BooleanOperator {
        AND,
        OR;

    }
}

