/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  javax.persistence.criteria.CompoundSelection
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.TupleElementImplementor;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.SelectionImpl;

public class CompoundSelectionImpl<X>
extends SelectionImpl<X>
implements CompoundSelection<X>,
Renderable,
Serializable {
    private final boolean isConstructor;
    private List<Selection<?>> selectionItems;

    public CompoundSelectionImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, List<Selection<?>> selectionItems) {
        super(criteriaBuilder, javaType);
        this.isConstructor = !javaType.isArray() && !Tuple.class.isAssignableFrom(javaType);
        this.selectionItems = selectionItems;
    }

    @Override
    public boolean isCompoundSelection() {
        return true;
    }

    @Override
    public List<Selection<?>> getCompoundSelectionItems() {
        return this.selectionItems;
    }

    @Override
    public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
        if (this.isConstructor) {
            return null;
        }
        boolean foundHandlers = false;
        ArrayList<ValueHandlerFactory.ValueHandler> valueHandlers = new ArrayList<ValueHandlerFactory.ValueHandler>();
        for (Selection<?> selection : this.getCompoundSelectionItems()) {
            ValueHandlerFactory.ValueHandler valueHandler = ((TupleElementImplementor)selection).getValueHandler();
            valueHandlers.add(valueHandler);
            foundHandlers = foundHandlers || valueHandler != null;
        }
        return foundHandlers ? null : valueHandlers;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        for (Selection<?> selectionItem : this.getCompoundSelectionItems()) {
            ParameterContainer.Helper.possibleParameter(selectionItem, registry);
        }
    }

    @Override
    public String render(RenderingContext renderingContext) {
        StringBuilder buff = new StringBuilder();
        if (this.isConstructor) {
            buff.append("new ").append(this.getJavaType().getName()).append('(');
        }
        String sep = "";
        for (Selection<?> selection : this.selectionItems) {
            buff.append(sep).append(((Renderable)selection).render(renderingContext));
            sep = ", ";
        }
        if (this.isConstructor) {
            buff.append(')');
        }
        return buff.toString();
    }
}

