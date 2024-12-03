/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.Parameter
 *  javax.persistence.ParameterMode
 *  javax.persistence.TemporalType
 */
package org.hibernate.procedure.spi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.FlushModeType;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.TemporalType;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.spi.QueryImplementor;

public interface ProcedureCallImplementor<R>
extends ProcedureCall,
QueryImplementor<R> {
    @Override
    default public List<R> getResultList() {
        return this.list();
    }

    @Override
    default public R getSingleResult() {
        return (R)this.uniqueResult();
    }

    @Override
    public ProcedureCallImplementor<R> setHint(String var1, Object var2);

    @Override
    public <T> ProcedureCallImplementor<R> setParameter(Parameter<T> var1, T var2);

    @Override
    public ProcedureCallImplementor<R> setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    @Override
    public ProcedureCallImplementor<R> setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    @Override
    public ProcedureCallImplementor<R> setParameter(String var1, Object var2);

    @Override
    public ProcedureCallImplementor<R> setParameter(String var1, Calendar var2, TemporalType var3);

    @Override
    public ProcedureCallImplementor<R> setParameter(String var1, Date var2, TemporalType var3);

    @Override
    public ProcedureCallImplementor<R> setParameter(int var1, Object var2);

    @Override
    public ProcedureCallImplementor<R> setParameter(int var1, Calendar var2, TemporalType var3);

    @Override
    public ProcedureCallImplementor<R> setParameter(int var1, Date var2, TemporalType var3);

    @Override
    public ProcedureCallImplementor<R> setFlushMode(FlushModeType var1);

    public ProcedureCallImplementor<R> registerStoredProcedureParameter(int var1, Class var2, ParameterMode var3);

    public ProcedureCallImplementor<R> registerStoredProcedureParameter(String var1, Class var2, ParameterMode var3);
}

