/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public interface TypedQuery<X>
extends Query {
    @Override
    public List<X> getResultList();

    @Override
    default public Stream<X> getResultStream() {
        return this.getResultList().stream();
    }

    public X getSingleResult();

    @Override
    public TypedQuery<X> setMaxResults(int var1);

    @Override
    public TypedQuery<X> setFirstResult(int var1);

    @Override
    public TypedQuery<X> setHint(String var1, Object var2);

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> var1, T var2);

    @Override
    public TypedQuery<X> setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    @Override
    public TypedQuery<X> setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    @Override
    public TypedQuery<X> setParameter(String var1, Object var2);

    @Override
    public TypedQuery<X> setParameter(String var1, Calendar var2, TemporalType var3);

    @Override
    public TypedQuery<X> setParameter(String var1, Date var2, TemporalType var3);

    @Override
    public TypedQuery<X> setParameter(int var1, Object var2);

    @Override
    public TypedQuery<X> setParameter(int var1, Calendar var2, TemporalType var3);

    @Override
    public TypedQuery<X> setParameter(int var1, Date var2, TemporalType var3);

    @Override
    public TypedQuery<X> setFlushMode(FlushModeType var1);

    @Override
    public TypedQuery<X> setLockMode(LockModeType var1);
}

