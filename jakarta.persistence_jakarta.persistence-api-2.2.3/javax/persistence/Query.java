/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;

public interface Query {
    public List getResultList();

    default public Stream getResultStream() {
        return this.getResultList().stream();
    }

    public Object getSingleResult();

    public int executeUpdate();

    public Query setMaxResults(int var1);

    public int getMaxResults();

    public Query setFirstResult(int var1);

    public int getFirstResult();

    public Query setHint(String var1, Object var2);

    public Map<String, Object> getHints();

    public <T> Query setParameter(Parameter<T> var1, T var2);

    public Query setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    public Query setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    public Query setParameter(String var1, Object var2);

    public Query setParameter(String var1, Calendar var2, TemporalType var3);

    public Query setParameter(String var1, Date var2, TemporalType var3);

    public Query setParameter(int var1, Object var2);

    public Query setParameter(int var1, Calendar var2, TemporalType var3);

    public Query setParameter(int var1, Date var2, TemporalType var3);

    public Set<Parameter<?>> getParameters();

    public Parameter<?> getParameter(String var1);

    public <T> Parameter<T> getParameter(String var1, Class<T> var2);

    public Parameter<?> getParameter(int var1);

    public <T> Parameter<T> getParameter(int var1, Class<T> var2);

    public boolean isBound(Parameter<?> var1);

    public <T> T getParameterValue(Parameter<T> var1);

    public Object getParameterValue(String var1);

    public Object getParameterValue(int var1);

    public Query setFlushMode(FlushModeType var1);

    public FlushModeType getFlushMode();

    public Query setLockMode(LockModeType var1);

    public LockModeType getLockMode();

    public <T> T unwrap(Class<T> var1);
}

