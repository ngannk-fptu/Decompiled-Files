/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.query.spi;

import java.util.Collection;
import javax.persistence.TemporalType;
import org.hibernate.Incubating;
import org.hibernate.type.Type;

@Incubating
public interface QueryParameterListBinding<T> {
    public void setBindValues(Collection<T> var1);

    public void setBindValues(Collection<T> var1, Type var2);

    public void setBindValues(Collection<T> var1, TemporalType var2);

    public Collection<T> getBindValues();

    public Type getBindType();
}

