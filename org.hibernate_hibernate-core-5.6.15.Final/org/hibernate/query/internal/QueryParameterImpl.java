/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.internal;

import org.hibernate.query.QueryParameter;
import org.hibernate.type.Type;

public abstract class QueryParameterImpl<T>
implements QueryParameter<T> {
    private Type expectedType;

    public QueryParameterImpl(Type expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public Type getHibernateType() {
        return this.expectedType;
    }

    public void setHibernateType(Type expectedType) {
        this.expectedType = expectedType;
    }

    public Class<T> getParameterType() {
        return this.expectedType == null ? null : this.expectedType.getReturnedClass();
    }
}

