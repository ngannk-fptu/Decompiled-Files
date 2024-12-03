/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.ListAttribute
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import javax.persistence.metamodel.ListAttribute;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;

public class ListIndexExpression
extends ExpressionImpl<Integer>
implements Serializable {
    private final PathImplementor origin;
    private final ListAttribute listAttribute;

    public ListIndexExpression(CriteriaBuilderImpl criteriaBuilder, PathImplementor origin, ListAttribute listAttribute) {
        super(criteriaBuilder, Integer.class);
        this.origin = origin;
        this.listAttribute = listAttribute;
    }

    public ListAttribute getListAttribute() {
        return this.listAttribute;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return "index(" + this.origin.getPathIdentifier() + ")";
    }
}

