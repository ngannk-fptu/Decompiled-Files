/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;

public class SizeOfPluralAttributeExpression
extends ExpressionImpl<Integer>
implements Serializable {
    private final PluralAttributePath path;

    public SizeOfPluralAttributeExpression(CriteriaBuilderImpl criteriaBuilder, PluralAttributePath path) {
        super(criteriaBuilder, Integer.class);
        this.path = path;
    }

    @Deprecated
    public PluralAttributePath getCollectionPath() {
        return this.path;
    }

    public PluralAttributePath getPluralAttributePath() {
        return this.path;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return "size(" + this.getPluralAttributePath().render(renderingContext) + ")";
    }
}

