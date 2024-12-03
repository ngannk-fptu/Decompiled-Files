/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Order
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

public interface HibernateCriteriaBuilder
extends CriteriaBuilder {
    public <M extends Map<?, ?>> Predicate isMapEmpty(Expression<M> var1);

    public <M extends Map<?, ?>> Predicate isMapNotEmpty(Expression<M> var1);

    public <M extends Map<?, ?>> Expression<Integer> mapSize(Expression<M> var1);

    public <M extends Map<?, ?>> Expression<Integer> mapSize(M var1);

    public Order asc(Expression<?> var1, boolean var2);

    public Order desc(Expression<?> var1, boolean var2);
}

