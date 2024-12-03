/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 *  javax.persistence.TemporalType
 */
package org.hibernate.procedure;

import javax.persistence.ParameterMode;
import javax.persistence.TemporalType;
import org.hibernate.procedure.ParameterBind;
import org.hibernate.query.procedure.ProcedureParameter;
import org.hibernate.type.Type;

public interface ParameterRegistration<T>
extends ProcedureParameter<T> {
    public String getName();

    public Integer getPosition();

    @Deprecated
    default public Class<T> getType() {
        return this.getParameterType();
    }

    @Override
    public ParameterMode getMode();

    @Override
    public void enablePassingNulls(boolean var1);

    public void setHibernateType(Type var1);

    public ParameterBind<T> getBind();

    public void bindValue(T var1);

    public void bindValue(T var1, TemporalType var2);
}

