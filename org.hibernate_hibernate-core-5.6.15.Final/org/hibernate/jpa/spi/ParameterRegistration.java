/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 *  javax.persistence.ParameterMode
 *  javax.persistence.Query
 *  javax.persistence.TemporalType
 */
package org.hibernate.jpa.spi;

import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.hibernate.jpa.spi.ParameterBind;

public interface ParameterRegistration<T>
extends Parameter<T> {
    public boolean isJpaPositionalParameter();

    public Query getQuery();

    public ParameterMode getMode();

    public boolean isBindable();

    public void bindValue(T var1);

    public void bindValue(T var1, TemporalType var2);

    public ParameterBind<T> getBind();
}

