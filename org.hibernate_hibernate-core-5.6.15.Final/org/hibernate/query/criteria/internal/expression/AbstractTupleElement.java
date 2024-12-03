/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.AbstractNode;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.TupleElementImplementor;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;

public abstract class AbstractTupleElement<X>
extends AbstractNode
implements TupleElementImplementor<X>,
Serializable {
    private final Class originalJavaType;
    private Class<X> javaType;
    private String alias;
    private ValueHandlerFactory.ValueHandler<X> valueHandler;

    protected AbstractTupleElement(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType) {
        super(criteriaBuilder);
        this.originalJavaType = javaType;
        this.javaType = javaType;
    }

    public Class<X> getJavaType() {
        return this.javaType;
    }

    protected void resetJavaType(Class targetType) {
        this.javaType = targetType;
        this.valueHandler = ValueHandlerFactory.determineAppropriateHandler(this.javaType);
    }

    protected void forceConversion(ValueHandlerFactory.ValueHandler<X> valueHandler) {
        this.valueHandler = valueHandler;
    }

    @Override
    public ValueHandlerFactory.ValueHandler<X> getValueHandler() {
        return this.valueHandler;
    }

    public String getAlias() {
        return this.alias;
    }

    protected void setAlias(String alias) {
        this.alias = alias;
    }
}

