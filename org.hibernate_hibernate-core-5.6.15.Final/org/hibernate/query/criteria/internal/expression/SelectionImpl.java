/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.SelectionImplementor;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.expression.AbstractTupleElement;

public abstract class SelectionImpl<X>
extends AbstractTupleElement<X>
implements SelectionImplementor<X>,
ParameterContainer,
Serializable {
    public SelectionImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType) {
        super(criteriaBuilder, javaType);
    }

    public Selection<X> alias(String alias) {
        this.setAlias(alias);
        return this;
    }

    public boolean isCompoundSelection() {
        return false;
    }

    @Override
    public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
        return this.getValueHandler() == null ? null : Collections.singletonList(this.getValueHandler());
    }

    public List<Selection<?>> getCompoundSelectionItems() {
        throw new IllegalStateException("Not a compound selection");
    }
}

