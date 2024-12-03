/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CompoundSelection
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.path.MapAttributeJoin;
import org.hibernate.sql.ast.Clause;

public class MapEntryExpression<K, V>
extends ExpressionImpl<Map.Entry<K, V>>
implements CompoundSelection<Map.Entry<K, V>>,
Serializable {
    private final MapAttributeJoin<?, K, V> original;

    public MapEntryExpression(CriteriaBuilderImpl criteriaBuilder, Class<Map.Entry<K, V>> javaType, MapAttributeJoin<?, K, V> original) {
        super(criteriaBuilder, javaType);
        this.original = original;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        if (renderingContext.getClauseStack().getCurrent() == Clause.SELECT) {
            return "entry(" + this.original.render(renderingContext) + ")";
        }
        throw new IllegalStateException("illegal reference to map entry outside of select clause.");
    }

    @Override
    public boolean isCompoundSelection() {
        return true;
    }

    @Override
    public List<Selection<?>> getCompoundSelectionItems() {
        return Arrays.asList(this.original.key(), this.original.value());
    }
}

