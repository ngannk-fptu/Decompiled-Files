/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.procedure.spi;

import java.sql.CallableStatement;
import java.sql.SQLException;
import org.hibernate.procedure.ParameterRegistration;
import org.hibernate.type.Type;

public interface ParameterRegistrationImplementor<T>
extends ParameterRegistration<T> {
    public void prepare(CallableStatement var1, int var2) throws SQLException;

    @Override
    public Type getHibernateType();

    @Override
    public boolean isPassNullsEnabled();

    public int[] getSqlTypes();

    public T extract(CallableStatement var1);
}

