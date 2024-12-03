/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.Parameter
 *  javax.persistence.TemporalType
 *  javax.persistence.criteria.ParameterExpression
 */
package org.hibernate.query.criteria.internal.compile;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.criteria.ParameterExpression;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.criteria.internal.compile.ExplicitParameterInfo;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.QueryProducerImplementor;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

public class CriteriaQueryTypeQueryAdapter<X>
implements QueryImplementor<X> {
    private final SharedSessionContractImplementor entityManager;
    private final QueryImplementor<X> jpqlQuery;
    private final Map<ParameterExpression<?>, ExplicitParameterInfo<?>> explicitParameterInfoMap;

    public CriteriaQueryTypeQueryAdapter(SharedSessionContractImplementor entityManager, QueryImplementor<X> jpqlQuery, Map<ParameterExpression<?>, ExplicitParameterInfo<?>> explicitParameterInfoMap) {
        this.entityManager = entityManager;
        this.jpqlQuery = jpqlQuery;
        this.explicitParameterInfoMap = explicitParameterInfoMap;
    }

    @Override
    public List<X> getResultList() {
        return this.jpqlQuery.getResultList();
    }

    @Override
    public X uniqueResult() {
        return (X)this.jpqlQuery.uniqueResult();
    }

    @Override
    public Optional<X> uniqueResultOptional() {
        return this.jpqlQuery.uniqueResultOptional();
    }

    @Override
    public Stream<X> stream() {
        return this.jpqlQuery.stream();
    }

    @Override
    public List<X> list() {
        return this.jpqlQuery.list();
    }

    @Override
    public QueryImplementor<X> setCacheMode(CacheMode cacheMode) {
        this.jpqlQuery.setCacheMode(cacheMode);
        return this;
    }

    @Override
    public boolean isCacheable() {
        return this.jpqlQuery.isCacheable();
    }

    @Override
    public X getSingleResult() {
        return (X)this.jpqlQuery.getSingleResult();
    }

    @Override
    public ParameterMetadata getParameterMetadata() {
        return this.jpqlQuery.getParameterMetadata();
    }

    @Override
    public String[] getNamedParameters() {
        return this.jpqlQuery.getNamedParameters();
    }

    public int getMaxResults() {
        return this.jpqlQuery.getMaxResults();
    }

    @Override
    public QueryImplementor<X> setMaxResults(int maxResult) {
        this.jpqlQuery.setMaxResults(maxResult);
        return this;
    }

    public int getFirstResult() {
        return this.jpqlQuery.getFirstResult();
    }

    @Override
    public QueryImplementor<X> setFirstResult(int i) {
        this.jpqlQuery.setFirstResult(i);
        return this;
    }

    public Map<String, Object> getHints() {
        return this.jpqlQuery.getHints();
    }

    @Override
    public QueryImplementor<X> setHint(String name, Object value) {
        this.jpqlQuery.setHint(name, value);
        return this;
    }

    @Override
    public QueryImplementor<X> applyGraph(RootGraph graph, GraphSemantic semantic) {
        this.jpqlQuery.applyGraph(graph, semantic);
        return this;
    }

    protected boolean isNativeQuery() {
        return false;
    }

    @Override
    public String getQueryString() {
        return this.jpqlQuery.getQueryString();
    }

    @Override
    public FlushMode getHibernateFlushMode() {
        return this.jpqlQuery.getHibernateFlushMode();
    }

    @Override
    public FlushModeType getFlushMode() {
        return this.jpqlQuery.getFlushMode();
    }

    @Override
    public CacheMode getCacheMode() {
        return this.jpqlQuery.getCacheMode();
    }

    @Override
    public Type[] getReturnTypes() {
        return this.jpqlQuery.getReturnTypes();
    }

    @Override
    public LockOptions getLockOptions() {
        return this.jpqlQuery.getLockOptions();
    }

    @Override
    public RowSelection getQueryOptions() {
        return this.jpqlQuery.getQueryOptions();
    }

    @Override
    public QueryImplementor<X> setFlushMode(FlushModeType flushModeType) {
        this.jpqlQuery.setFlushMode(flushModeType);
        return this;
    }

    @Override
    public QueryImplementor setFlushMode(FlushMode flushMode) {
        this.jpqlQuery.setFlushMode(flushMode);
        return this;
    }

    @Override
    public QueryImplementor<X> setHibernateFlushMode(FlushMode flushMode) {
        this.jpqlQuery.setHibernateFlushMode(flushMode);
        return this;
    }

    @Override
    public QueryImplementor setCacheable(boolean cacheable) {
        this.jpqlQuery.setCacheable(cacheable);
        return this;
    }

    @Override
    public String getCacheRegion() {
        return this.jpqlQuery.getCacheRegion();
    }

    @Override
    public QueryImplementor setCacheRegion(String cacheRegion) {
        this.jpqlQuery.setCacheRegion(cacheRegion);
        return this;
    }

    @Override
    public Integer getTimeout() {
        return this.jpqlQuery.getTimeout();
    }

    @Override
    public QueryImplementor setTimeout(int timeout) {
        this.jpqlQuery.setTimeout(timeout);
        return this;
    }

    @Override
    public Integer getFetchSize() {
        return this.jpqlQuery.getFetchSize();
    }

    @Override
    public QueryImplementor setLockOptions(LockOptions lockOptions) {
        this.jpqlQuery.setLockOptions(lockOptions);
        return this;
    }

    @Override
    public QueryImplementor setLockMode(String alias, LockMode lockMode) {
        this.jpqlQuery.setLockMode(alias, lockMode);
        return this;
    }

    @Override
    public String getComment() {
        return this.jpqlQuery.getComment();
    }

    @Override
    public QueryImplementor setComment(String comment) {
        this.jpqlQuery.setComment(comment);
        return this;
    }

    @Override
    public QueryImplementor addQueryHint(String hint) {
        this.jpqlQuery.addQueryHint(hint);
        return this;
    }

    @Override
    public Iterator<X> iterate() {
        return this.jpqlQuery.iterate();
    }

    @Override
    public ScrollableResults scroll() {
        return this.jpqlQuery.scroll();
    }

    @Override
    public ScrollableResults scroll(ScrollMode scrollMode) {
        return this.jpqlQuery.scroll(scrollMode);
    }

    @Override
    public QueryImplementor setFetchSize(int fetchSize) {
        this.jpqlQuery.setFetchSize(fetchSize);
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return this.jpqlQuery.isReadOnly();
    }

    public LockModeType getLockMode() {
        return this.jpqlQuery.getLockMode();
    }

    @Override
    public QueryImplementor<X> setLockMode(LockModeType lockModeType) {
        this.jpqlQuery.setLockMode(lockModeType);
        return this;
    }

    @Override
    public QueryImplementor setReadOnly(boolean readOnly) {
        this.jpqlQuery.setReadOnly(readOnly);
        return this;
    }

    @Override
    public Type determineProperBooleanType(int position, Object value, Type defaultType) {
        return this.jpqlQuery.determineProperBooleanType(position, value, defaultType);
    }

    @Override
    public Type determineProperBooleanType(String name, Object value, Type defaultType) {
        return this.jpqlQuery.determineProperBooleanType(name, value, defaultType);
    }

    @Override
    public String[] getReturnAliases() {
        return this.jpqlQuery.getReturnAliases();
    }

    public Set<Parameter<?>> getParameters() {
        this.entityManager.checkOpen(false);
        return new HashSet(this.explicitParameterInfoMap.values());
    }

    public boolean isBound(Parameter<?> param) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        Parameter jpqlParameter = parameterInfo.isNamed() ? this.jpqlQuery.getParameter(parameterInfo.getName()) : this.jpqlQuery.getParameter(parameterInfo.getPosition());
        return this.jpqlQuery.isBound(jpqlParameter);
    }

    public <T> T getParameterValue(Parameter<T> param) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            return (T)this.jpqlQuery.getParameterValue(parameterInfo.getName());
        }
        return (T)this.jpqlQuery.getParameterValue(parameterInfo.getPosition());
    }

    private <T> ExplicitParameterInfo<?> resolveParameterInfo(Parameter<T> param) {
        if (ExplicitParameterInfo.class.isInstance(param)) {
            return (ExplicitParameterInfo)param;
        }
        if (ParameterExpression.class.isInstance(param)) {
            return this.explicitParameterInfoMap.get(param);
        }
        for (ExplicitParameterInfo<?> parameterInfo : this.explicitParameterInfoMap.values()) {
            if (param.getName() != null && param.getName().equals(parameterInfo.getName())) {
                return parameterInfo;
            }
            if (param.getPosition() == null || !param.getPosition().equals(parameterInfo.getPosition())) continue;
            return parameterInfo;
        }
        throw new IllegalArgumentException("Unable to locate parameter [" + param + "] in query");
    }

    @Override
    public <T> QueryImplementor<X> setParameter(Parameter<T> param, T t) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), t);
        } else {
            this.jpqlQuery.setParameter(parameterInfo.getPosition(), t);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(Parameter<Calendar> param, Calendar calendar, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), calendar, temporalType);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), calendar, temporalType);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(Parameter<Date> param, Date date, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), date, temporalType);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), date, temporalType);
        }
        return this;
    }

    public <T> T unwrap(Class<T> cls) {
        return (T)this.jpqlQuery.unwrap(cls);
    }

    public Object getParameterValue(String name) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        return this.jpqlQuery.getParameterValue(name);
    }

    private ExplicitParameterInfo<?> locateParameterByName(String name) {
        for (ExplicitParameterInfo<?> parameterInfo : this.explicitParameterInfoMap.values()) {
            if (!parameterInfo.isNamed() || !parameterInfo.getName().equals(name)) continue;
            return parameterInfo;
        }
        throw new IllegalArgumentException("Unable to locate parameter registered with that name [" + name + "]");
    }

    private ExplicitParameterInfo<?> locateParameterByPosition(int position) {
        for (ExplicitParameterInfo<?> parameterInfo : this.explicitParameterInfoMap.values()) {
            if (parameterInfo.getPosition() != position) continue;
            return parameterInfo;
        }
        throw new IllegalArgumentException("Unable to locate parameter registered at position [" + position + "]");
    }

    public Parameter<?> getParameter(String name) {
        this.entityManager.checkOpen(false);
        return this.locateParameterByName(name);
    }

    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameter = this.locateParameterByName(name);
        if (type.isAssignableFrom(parameter.getParameterType())) {
            return parameter;
        }
        throw new IllegalArgumentException("Named parameter [" + name + "] type is not assignanle to request type [" + type.getName() + "]");
    }

    @Override
    public QueryImplementor<X> setParameter(String name, Object value) {
        this.entityManager.checkOpen(true);
        ExplicitParameterInfo<?> parameterInfo = this.locateParameterByName(name);
        parameterInfo.validateBindValue(value);
        this.jpqlQuery.setParameter(name, value);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(String name, Calendar calendar, TemporalType temporalType) {
        this.entityManager.checkOpen(true);
        ExplicitParameterInfo<?> parameterInfo = this.locateParameterByName(name);
        parameterInfo.validateCalendarBind();
        this.jpqlQuery.setParameter(name, calendar, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(String name, Date date, TemporalType temporalType) {
        this.entityManager.checkOpen(true);
        ExplicitParameterInfo<?> parameterInfo = this.locateParameterByName(name);
        parameterInfo.validateDateBind();
        this.jpqlQuery.setParameter(name, date, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setEntity(String name, Object val) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.locateParameterByName(name);
        parameterInfo.validateBindValue(val);
        this.jpqlQuery.setEntity(name, val);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(String name, Object val, Type type) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.locateParameterByName(name);
        parameterInfo.validateBindValue(val);
        this.jpqlQuery.setParameter(name, val, type);
        return this;
    }

    @Override
    public <T> QueryImplementor<X> setParameter(QueryParameter<T> parameter, T val) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(parameter);
        parameterInfo.validateBindValue(val);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), val);
        } else {
            this.jpqlQuery.setParameter(parameterInfo.getPosition(), val);
        }
        return this;
    }

    @Override
    public <P> QueryImplementor<X> setParameter(QueryParameter<P> parameter, P val, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(parameter);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), (Object)val, temporalType);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), (Object)val, temporalType);
        }
        return this;
    }

    @Override
    public <P> QueryImplementor<X> setParameter(String name, P val, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, (Object)val, temporalType);
        return this;
    }

    @Override
    public <P> QueryImplementor<X> setParameterList(QueryParameter<P> parameter, Collection<P> values) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(parameter);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), values);
        } else {
            this.jpqlQuery.setParameter(parameterInfo.getPosition(), values);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameterList(String name, Collection values) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, (Object)values);
        return this;
    }

    @Override
    public Query<X> setParameterList(int position, Collection values) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, (Object)values);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameterList(String name, Collection values, Type type) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, (Object)values, type);
        return this;
    }

    @Override
    public Query<X> setParameterList(int position, Collection values, Type type) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, (Object)values, type);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameterList(String name, Object[] values, Type type) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, (Object)values, type);
        return this;
    }

    @Override
    public Query<X> setParameterList(int position, Object[] values, Type type) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, (Object)values, type);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameterList(String name, Object[] values) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, (Object)values);
        return this;
    }

    @Override
    public Query<X> setParameterList(int position, Object[] values) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, (Object)values);
        return this;
    }

    @Override
    public <P> QueryImplementor<X> setParameter(QueryParameter<P> parameter, P value, Type type) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(parameter);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), value, type);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), value, type);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), value, temporalType);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), value, temporalType);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), value, temporalType);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), value, temporalType);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), value, temporalType);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), value, temporalType);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        ExplicitParameterInfo<?> parameterInfo = this.resolveParameterInfo(param);
        if (parameterInfo.isNamed()) {
            this.jpqlQuery.setParameter(parameterInfo.getName(), value, temporalType);
        } else {
            this.jpqlQuery.setParameter((int)parameterInfo.getPosition(), value, temporalType);
        }
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(String name, Instant value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(String name, LocalDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByName(name);
        this.jpqlQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setResultTransformer(ResultTransformer transformer) {
        this.jpqlQuery.setResultTransformer(transformer);
        return this;
    }

    @Override
    public QueryImplementor<X> setProperties(Object bean) {
        this.jpqlQuery.setProperties(bean);
        return this;
    }

    @Override
    public QueryImplementor setProperties(Map map) {
        this.jpqlQuery.setProperties(map);
        return this;
    }

    @Override
    public QueryProducerImplementor getProducer() {
        return this.jpqlQuery.getProducer();
    }

    @Override
    public void setOptionalId(Serializable id) {
        this.jpqlQuery.setOptionalId(id);
    }

    @Override
    public void setOptionalEntityName(String entityName) {
        this.jpqlQuery.setOptionalEntityName(entityName);
    }

    @Override
    public void setOptionalObject(Object optionalObject) {
        this.jpqlQuery.setOptionalObject(optionalObject);
    }

    @Override
    public QueryImplementor<X> setParameter(int position, LocalDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(int position, Instant value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<X> setParameter(int position, Object val, Type type) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, val, type);
        return this;
    }

    @Override
    public QueryImplementor<X> setEntity(int position, Object val) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, val);
        return this;
    }

    @Override
    public <P> QueryImplementor<X> setParameter(int position, P val, TemporalType temporalType) {
        this.entityManager.checkOpen(false);
        this.locateParameterByPosition(position);
        this.jpqlQuery.setParameter(position, (Object)val, temporalType);
        return this;
    }

    public int executeUpdate() {
        throw new IllegalStateException("Typed criteria queries do not support executeUpdate");
    }

    @Override
    public QueryImplementor<X> setParameter(int i, Object o) {
        throw new IllegalArgumentException("Criteria queries do not support positioned parameters");
    }

    @Override
    public QueryImplementor<X> setParameter(int i, Calendar calendar, TemporalType temporalType) {
        throw new IllegalArgumentException("Criteria queries do not support positioned parameters");
    }

    @Override
    public QueryImplementor<X> setParameter(int i, Date date, TemporalType temporalType) {
        throw new IllegalArgumentException("Criteria queries do not support positioned parameters");
    }

    public Object getParameterValue(int position) {
        throw new IllegalArgumentException("Criteria queries do not support positioned parameters");
    }

    public Parameter<?> getParameter(int position) {
        throw new IllegalArgumentException("Criteria queries do not support positioned parameters");
    }

    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        throw new IllegalArgumentException("Criteria queries do not support positioned parameters");
    }
}

