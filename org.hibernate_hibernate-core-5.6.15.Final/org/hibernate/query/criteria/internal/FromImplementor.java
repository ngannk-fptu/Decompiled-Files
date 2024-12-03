/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.From
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.From;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.compile.RenderingContext;

public interface FromImplementor<Z, X>
extends PathImplementor<X>,
From<Z, X> {
    @Override
    public void prepareAlias(RenderingContext var1);

    public String renderTableExpression(RenderingContext var1);

    public FromImplementor<Z, X> correlateTo(CriteriaSubqueryImpl var1);

    public void prepareCorrelationDelegate(FromImplementor<Z, X> var1);

    public FromImplementor<Z, X> getCorrelationParent();

    default public boolean canBeReplacedByCorrelatedParentInSubQuery() {
        return this.isCorrelated();
    }
}

