/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.query.internal;

import javax.persistence.TemporalType;
import org.hibernate.query.internal.BindingTypeHelper;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.spi.QueryParameterBindingTypeResolver;
import org.hibernate.query.spi.QueryParameterBindingValidator;
import org.hibernate.type.Type;

public class QueryParameterBindingImpl<T>
implements QueryParameterBinding<T> {
    private final QueryParameterBindingTypeResolver typeResolver;
    private final boolean isBindingValidationRequired;
    private boolean isBound;
    private Type bindType;
    private T bindValue;

    public QueryParameterBindingImpl(Type type, QueryParameterBindingTypeResolver typeResolver, boolean isBindingValidationRequired) {
        this.bindType = type;
        this.typeResolver = typeResolver;
        this.isBindingValidationRequired = isBindingValidationRequired;
    }

    @Override
    public boolean isBound() {
        return this.isBound;
    }

    @Override
    public T getBindValue() {
        return this.bindValue;
    }

    @Override
    public Type getBindType() {
        return this.bindType;
    }

    @Override
    public void setBindValue(T value) {
        if (this.isBindingValidationRequired) {
            this.validate(value);
        }
        this.bindValue(value);
    }

    @Override
    public void setBindValue(T value, Type clarifiedType) {
        if (this.isBindingValidationRequired) {
            this.validate(value, clarifiedType);
        }
        if (clarifiedType != null) {
            this.bindType = clarifiedType;
        }
        this.bindValue(value);
    }

    @Override
    public void setBindValue(T value, TemporalType clarifiedTemporalType) {
        if (this.isBindingValidationRequired) {
            this.validate(value, clarifiedTemporalType);
        }
        this.bindValue(value);
        this.bindType = BindingTypeHelper.INSTANCE.determineTypeForTemporalType(clarifiedTemporalType, this.bindType, value);
    }

    private void bindValue(T value) {
        this.isBound = true;
        this.bindValue = value;
        if (this.bindType == null) {
            this.bindType = this.typeResolver.resolveParameterBindType(value);
        }
    }

    private void validate(T value) {
        QueryParameterBindingValidator.INSTANCE.validate(this.getBindType(), value);
    }

    private void validate(T value, Type clarifiedType) {
        QueryParameterBindingValidator.INSTANCE.validate(clarifiedType, value);
    }

    private void validate(T value, TemporalType clarifiedTemporalType) {
        QueryParameterBindingValidator.INSTANCE.validate(this.getBindType(), value, clarifiedTemporalType);
    }
}

