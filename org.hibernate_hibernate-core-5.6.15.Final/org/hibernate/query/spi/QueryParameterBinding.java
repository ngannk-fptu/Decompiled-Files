/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.query.spi;

import javax.persistence.TemporalType;
import org.hibernate.Incubating;
import org.hibernate.type.Type;

@Incubating
public interface QueryParameterBinding<T> {
    public boolean isBound();

    public void setBindValue(T var1);

    public void setBindValue(T var1, Type var2);

    public void setBindValue(T var1, TemporalType var2);

    public T getBindValue();

    public Type getBindType();
}

