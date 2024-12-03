/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.Parameter
 *  javax.persistence.TemporalType
 */
package org.hibernate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.FlushModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.SynchronizeableQuery;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.QueryParameter;
import org.hibernate.type.Type;

@Deprecated
public interface SQLQuery<T>
extends Query<T>,
SynchronizeableQuery<T> {
    public SQLQuery<T> setResultSetMapping(String var1);

    public boolean isCallable();

    public List<NativeSQLQueryReturn> getQueryReturns();

    public SQLQuery<T> addScalar(String var1);

    public SQLQuery<T> addScalar(String var1, Type var2);

    public RootReturn addRoot(String var1, String var2);

    public RootReturn addRoot(String var1, Class var2);

    public SQLQuery<T> addEntity(String var1);

    public SQLQuery<T> addEntity(String var1, String var2);

    public SQLQuery<T> addEntity(String var1, String var2, LockMode var3);

    public SQLQuery<T> addEntity(Class var1);

    public SQLQuery<T> addEntity(String var1, Class var2);

    public SQLQuery<T> addEntity(String var1, Class var2, LockMode var3);

    public FetchReturn addFetch(String var1, String var2, String var3);

    public SQLQuery<T> addJoin(String var1, String var2);

    public SQLQuery<T> addJoin(String var1, String var2, String var3);

    public SQLQuery<T> addJoin(String var1, String var2, LockMode var3);

    @Override
    public SQLQuery<T> addSynchronizedQuerySpace(String var1);

    @Override
    public SQLQuery<T> addSynchronizedEntityName(String var1) throws MappingException;

    @Override
    public SQLQuery<T> addSynchronizedEntityClass(Class var1) throws MappingException;

    @Override
    public NativeQuery<T> setHibernateFlushMode(FlushMode var1);

    @Override
    public NativeQuery<T> setFlushMode(FlushModeType var1);

    @Override
    public NativeQuery<T> setCacheMode(CacheMode var1);

    @Override
    public NativeQuery<T> setCacheable(boolean var1);

    @Override
    public NativeQuery<T> setCacheRegion(String var1);

    @Override
    public NativeQuery<T> setTimeout(int var1);

    @Override
    public NativeQuery<T> setFetchSize(int var1);

    @Override
    public NativeQuery<T> setReadOnly(boolean var1);

    @Override
    public NativeQuery<T> setLockOptions(LockOptions var1);

    @Override
    public NativeQuery<T> setLockMode(String var1, LockMode var2);

    @Override
    public NativeQuery<T> setComment(String var1);

    @Override
    public NativeQuery<T> addQueryHint(String var1);

    @Override
    public <P> NativeQuery<T> setParameter(QueryParameter<P> var1, P var2);

    @Override
    public <P> NativeQuery<T> setParameter(Parameter<P> var1, P var2);

    @Override
    public NativeQuery<T> setParameter(String var1, Object var2);

    @Override
    public NativeQuery<T> setParameter(int var1, Object var2);

    @Override
    public <P> NativeQuery<T> setParameter(QueryParameter<P> var1, P var2, Type var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Object var2, Type var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Object var2, Type var3);

    @Override
    public <P> NativeQuery<T> setParameter(QueryParameter<P> var1, P var2, TemporalType var3);

    @Override
    public <P> NativeQuery<T> setParameter(String var1, P var2, TemporalType var3);

    @Override
    public <P> NativeQuery<T> setParameter(int var1, P var2, TemporalType var3);

    @Override
    public <P> NativeQuery<T> setParameterList(QueryParameter<P> var1, Collection<P> var2);

    @Override
    public NativeQuery<T> setParameterList(String var1, Collection var2);

    @Override
    public NativeQuery<T> setParameterList(String var1, Collection var2, Type var3);

    @Override
    public NativeQuery<T> setParameterList(String var1, Object[] var2, Type var3);

    @Override
    public NativeQuery<T> setParameterList(String var1, Object[] var2);

    @Override
    public NativeQuery<T> setProperties(Object var1);

    @Override
    public NativeQuery<T> setProperties(Map var1);

    @Override
    public NativeQuery<T> setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Date var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Date var2, TemporalType var3);

    @Override
    public NativeQuery<T> setFlushMode(FlushMode var1);

    public static interface FetchReturn {
        public FetchReturn setLockMode(LockMode var1);

        public FetchReturn addProperty(String var1, String var2);

        public ReturnProperty addProperty(String var1);
    }

    public static interface RootReturn {
        public RootReturn setLockMode(LockMode var1);

        public RootReturn setDiscriminatorAlias(String var1);

        public RootReturn addProperty(String var1, String var2);

        public ReturnProperty addProperty(String var1);
    }

    public static interface ReturnProperty {
        public ReturnProperty addColumnAlias(String var1);
    }
}

