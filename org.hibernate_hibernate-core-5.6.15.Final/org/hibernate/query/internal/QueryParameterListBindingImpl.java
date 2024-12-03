/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.query.internal;

import java.util.Collection;
import javax.persistence.TemporalType;
import org.hibernate.query.internal.BindingTypeHelper;
import org.hibernate.query.spi.QueryParameterBindingValidator;
import org.hibernate.query.spi.QueryParameterListBinding;
import org.hibernate.type.Type;

public class QueryParameterListBindingImpl<T>
implements QueryParameterListBinding<T> {
    private final boolean isBindingValidationRequired;
    private Collection<T> bindValues;
    private Type bindType;

    public QueryParameterListBindingImpl(Type type, boolean isBindingValidationRequired) {
        this.bindType = type;
        this.isBindingValidationRequired = isBindingValidationRequired;
    }

    @Override
    public void setBindValues(Collection<T> bindValues) {
        if (this.isBindingValidationRequired) {
            this.validate(bindValues);
        }
        this.bindValue(bindValues);
    }

    @Override
    public void setBindValues(Collection<T> values, Type clarifiedType) {
        if (this.isBindingValidationRequired) {
            this.validate(this.bindValues, clarifiedType);
        }
        this.bindValue(values);
        this.bindType = clarifiedType;
    }

    @Override
    public void setBindValues(Collection<T> values, TemporalType clarifiedTemporalType) {
        if (this.isBindingValidationRequired) {
            this.validate(values, clarifiedTemporalType);
        }
        this.bindValue(values);
        Object anElement = values.isEmpty() ? null : values.iterator().next();
        this.bindType = BindingTypeHelper.INSTANCE.determineTypeForTemporalType(clarifiedTemporalType, this.bindType, anElement);
    }

    @Override
    public Collection<T> getBindValues() {
        return this.bindValues;
    }

    @Override
    public Type getBindType() {
        return this.bindType;
    }

    private void bindValue(Collection<T> bindValues) {
        if (bindValues == null) {
            throw new IllegalArgumentException("Collection must be not null!");
        }
        this.bindValues = bindValues;
    }

    private void validate(Collection<T> value) {
        QueryParameterBindingValidator.INSTANCE.validate(this.getBindType(), value);
    }

    private void validate(Collection<T> value, Type clarifiedType) {
        QueryParameterBindingValidator.INSTANCE.validate(clarifiedType, value);
    }

    private void validate(Collection<T> value, TemporalType clarifiedTemporalType) {
        QueryParameterBindingValidator.INSTANCE.validate(this.getBindType(), value, clarifiedTemporalType);
    }
}

