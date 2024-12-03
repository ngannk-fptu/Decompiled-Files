/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.Expression;

public interface Order {
    public Order reverse();

    public boolean isAscending();

    public Expression<?> getExpression();
}

