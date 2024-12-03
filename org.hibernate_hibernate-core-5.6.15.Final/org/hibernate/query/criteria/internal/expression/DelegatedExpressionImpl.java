/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal.expression;

import java.util.List;
import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;

public abstract class DelegatedExpressionImpl<T>
extends ExpressionImpl<T> {
    private final ExpressionImpl<T> wrapped;

    public DelegatedExpressionImpl(ExpressionImpl<T> wrapped) {
        super(wrapped.criteriaBuilder(), wrapped.getJavaType());
        this.wrapped = wrapped;
    }

    public ExpressionImpl<T> getWrapped() {
        return this.wrapped;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        this.wrapped.registerParameters(registry);
    }

    @Override
    public Selection<T> alias(String alias) {
        this.wrapped.alias(alias);
        return this;
    }

    @Override
    public boolean isCompoundSelection() {
        return this.wrapped.isCompoundSelection();
    }

    @Override
    public List<ValueHandlerFactory.ValueHandler> getValueHandlers() {
        return this.wrapped.getValueHandlers();
    }

    @Override
    public List<Selection<?>> getCompoundSelectionItems() {
        return this.wrapped.getCompoundSelectionItems();
    }

    @Override
    public Class<T> getJavaType() {
        return this.wrapped.getJavaType();
    }

    @Override
    protected void resetJavaType(Class targetType) {
        this.wrapped.resetJavaType(targetType);
    }

    @Override
    protected void forceConversion(ValueHandlerFactory.ValueHandler<T> tValueHandler) {
        this.wrapped.forceConversion(tValueHandler);
    }

    @Override
    public ValueHandlerFactory.ValueHandler<T> getValueHandler() {
        return this.wrapped.getValueHandler();
    }

    @Override
    public String getAlias() {
        return this.wrapped.getAlias();
    }

    @Override
    protected void setAlias(String alias) {
        this.wrapped.setAlias(alias);
    }
}

