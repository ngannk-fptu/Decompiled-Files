/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.procedure;

import javax.persistence.TemporalType;
import org.hibernate.query.spi.QueryParameterBinding;

public interface ParameterBind<T>
extends QueryParameterBinding<T> {
    public T getValue();

    public TemporalType getExplicitTemporalType();
}

