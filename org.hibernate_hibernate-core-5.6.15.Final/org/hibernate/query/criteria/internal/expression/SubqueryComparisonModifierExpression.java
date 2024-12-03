/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Subquery
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import javax.persistence.criteria.Subquery;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;

public class SubqueryComparisonModifierExpression<Y>
extends ExpressionImpl<Y>
implements Serializable {
    private final Subquery<Y> subquery;
    private final Modifier modifier;

    public SubqueryComparisonModifierExpression(CriteriaBuilderImpl criteriaBuilder, Class<Y> javaType, Subquery<Y> subquery, Modifier modifier) {
        super(criteriaBuilder, javaType);
        this.subquery = subquery;
        this.modifier = modifier;
    }

    public Modifier getModifier() {
        return this.modifier;
    }

    public Subquery<Y> getSubquery() {
        return this.subquery;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return this.getModifier().rendered() + ((Renderable)this.getSubquery()).render(renderingContext);
    }

    public static enum Modifier {
        ALL{

            @Override
            String rendered() {
                return "all ";
            }
        }
        ,
        SOME{

            @Override
            String rendered() {
                return "some ";
            }
        }
        ,
        ANY{

            @Override
            String rendered() {
                return "any ";
            }
        };


        abstract String rendered();
    }
}

