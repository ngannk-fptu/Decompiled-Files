/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.FlushModeType;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public interface StoredProcedureQuery
extends Query {
    @Override
    public StoredProcedureQuery setHint(String var1, Object var2);

    @Override
    public <T> StoredProcedureQuery setParameter(Parameter<T> var1, T var2);

    @Override
    public StoredProcedureQuery setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    @Override
    public StoredProcedureQuery setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    @Override
    public StoredProcedureQuery setParameter(String var1, Object var2);

    @Override
    public StoredProcedureQuery setParameter(String var1, Calendar var2, TemporalType var3);

    @Override
    public StoredProcedureQuery setParameter(String var1, Date var2, TemporalType var3);

    @Override
    public StoredProcedureQuery setParameter(int var1, Object var2);

    @Override
    public StoredProcedureQuery setParameter(int var1, Calendar var2, TemporalType var3);

    @Override
    public StoredProcedureQuery setParameter(int var1, Date var2, TemporalType var3);

    @Override
    public StoredProcedureQuery setFlushMode(FlushModeType var1);

    public StoredProcedureQuery registerStoredProcedureParameter(int var1, Class var2, ParameterMode var3);

    public StoredProcedureQuery registerStoredProcedureParameter(String var1, Class var2, ParameterMode var3);

    public Object getOutputParameterValue(int var1);

    public Object getOutputParameterValue(String var1);

    public boolean execute();

    @Override
    public int executeUpdate();

    @Override
    public List getResultList();

    @Override
    public Object getSingleResult();

    public boolean hasMoreResults();

    public int getUpdateCount();
}

