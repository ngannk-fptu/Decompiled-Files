/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.CacheRetrieveMode
 *  javax.persistence.CacheStoreMode
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.NoResultException
 *  javax.persistence.Parameter
 *  javax.persistence.TemporalType
 *  javax.persistence.TransactionRequiredException
 *  org.jboss.logging.Logger
 */
package org.hibernate.query.internal;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TransactionRequiredException;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.NonUniqueResultException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.QueryParameterException;
import org.hibernate.ScrollMode;
import org.hibernate.TypeMismatchException;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.ExceptionConverter;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.internal.RootGraphImpl;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.hql.internal.QueryExecutionRequestException;
import org.hibernate.internal.EmptyScrollableResults;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.HEMLogging;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jpa.QueryHints;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.jpa.internal.util.CacheModeHelper;
import org.hibernate.jpa.internal.util.ConfigurationHelper;
import org.hibernate.jpa.internal.util.FlushModeTypeHelper;
import org.hibernate.jpa.internal.util.LockModeTypeHelper;
import org.hibernate.property.access.spi.BuiltInPropertyAccessStrategies;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.Query;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.internal.ScrollableResultsIterator;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.spi.QueryParameterListBinding;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.query.spi.StreamDecorator;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public abstract class AbstractProducedQuery<R>
implements QueryImplementor<R> {
    private static final EntityManagerMessageLogger MSG_LOGGER = HEMLogging.messageLogger(AbstractProducedQuery.class);
    private static final Logger LOGGER = Logger.getLogger(AbstractProducedQuery.class);
    private final SharedSessionContractImplementor producer;
    private final ParameterMetadata parameterMetadata;
    private FlushMode flushMode;
    private CacheStoreMode cacheStoreMode;
    private CacheRetrieveMode cacheRetrieveMode;
    private boolean cacheable;
    private String cacheRegion;
    private Boolean readOnly;
    private LockOptions lockOptions = new LockOptions();
    private String comment;
    private final List<String> dbHints = new ArrayList<String>();
    private ResultTransformer resultTransformer;
    private RowSelection queryOptions = new RowSelection();
    private EntityGraphQueryHint entityGraphQueryHint;
    private Object optionalObject;
    private Serializable optionalId;
    private String optionalEntityName;
    private Boolean passDistinctThrough;
    private FlushMode sessionFlushMode;
    private CacheMode sessionCacheMode;

    public AbstractProducedQuery(SharedSessionContractImplementor producer, ParameterMetadata parameterMetadata) {
        this.producer = producer;
        this.parameterMetadata = parameterMetadata;
    }

    @Override
    public SharedSessionContractImplementor getProducer() {
        return this.producer;
    }

    @Override
    public FlushMode getHibernateFlushMode() {
        return this.flushMode;
    }

    @Override
    public QueryImplementor setHibernateFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    @Override
    public QueryImplementor setFlushMode(FlushMode flushMode) {
        return this.setHibernateFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        this.getProducer().checkOpen();
        return this.flushMode == null ? this.getProducer().getFlushMode() : FlushModeTypeHelper.getFlushModeType(this.flushMode);
    }

    @Override
    public QueryImplementor setFlushMode(FlushModeType flushModeType) {
        this.getProducer().checkOpen();
        this.setHibernateFlushMode(FlushModeTypeHelper.getFlushMode(flushModeType));
        return this;
    }

    @Override
    public CacheMode getCacheMode() {
        return CacheModeHelper.interpretCacheMode(this.cacheStoreMode, this.cacheRetrieveMode);
    }

    @Override
    public QueryImplementor setCacheMode(CacheMode cacheMode) {
        this.cacheStoreMode = CacheModeHelper.interpretCacheStoreMode(cacheMode);
        this.cacheRetrieveMode = CacheModeHelper.interpretCacheRetrieveMode(cacheMode);
        return this;
    }

    @Override
    public boolean isCacheable() {
        return this.cacheable;
    }

    @Override
    public QueryImplementor setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }

    @Override
    public String getCacheRegion() {
        return this.cacheRegion;
    }

    @Override
    public QueryImplementor setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
        return this;
    }

    @Override
    public Integer getTimeout() {
        return this.queryOptions.getTimeout();
    }

    @Override
    public QueryImplementor setTimeout(int timeout) {
        this.queryOptions.setTimeout(timeout);
        return this;
    }

    @Override
    public Integer getFetchSize() {
        return this.queryOptions.getFetchSize();
    }

    @Override
    public QueryImplementor setFetchSize(int fetchSize) {
        this.queryOptions.setFetchSize(fetchSize);
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly == null ? this.producer.getPersistenceContextInternal().isDefaultReadOnly() : this.readOnly.booleanValue();
    }

    @Override
    public QueryImplementor setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    @Override
    public LockOptions getLockOptions() {
        return this.lockOptions;
    }

    @Override
    public QueryImplementor setLockOptions(LockOptions lockOptions) {
        this.lockOptions.setLockMode(lockOptions.getLockMode());
        this.lockOptions.setScope(lockOptions.getScope());
        this.lockOptions.setTimeOut(lockOptions.getTimeOut());
        this.lockOptions.setFollowOnLocking(lockOptions.getFollowOnLocking());
        return this;
    }

    @Override
    public QueryImplementor setLockMode(String alias, LockMode lockMode) {
        this.lockOptions.setAliasSpecificLockMode(alias, lockMode);
        return this;
    }

    @Override
    public QueryImplementor setLockMode(LockModeType lockModeType) {
        this.getProducer().checkOpen();
        if (!LockModeType.NONE.equals((Object)lockModeType) && !this.isSelect()) {
            throw new IllegalStateException("Illegal attempt to set lock mode on a non-SELECT query");
        }
        this.lockOptions.setLockMode(LockModeTypeHelper.getLockMode(lockModeType));
        return this;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public QueryImplementor setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public QueryImplementor addQueryHint(String hint) {
        this.dbHints.add(hint);
        return this;
    }

    @Override
    public ParameterMetadata getParameterMetadata() {
        return this.parameterMetadata;
    }

    @Override
    public String[] getNamedParameters() {
        return ArrayHelper.toStringArray(this.getParameterMetadata().getNamedParameterNames());
    }

    @Override
    public QueryImplementor<R> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType) {
        this.locateBinding(param).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType) {
        this.locateBinding(param).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType) {
        this.locateBinding(param).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType) {
        this.locateBinding(param).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(String name, Instant value, TemporalType temporalType) {
        this.locateBinding(name).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(String name, LocalDateTime value, TemporalType temporalType) {
        this.locateBinding(name).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
        this.locateBinding(name).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
        this.locateBinding(name).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(int position, Instant value, TemporalType temporalType) {
        this.locateBinding(position).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(int position, LocalDateTime value, TemporalType temporalType) {
        this.locateBinding(position).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
        this.locateBinding(position).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor<R> setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
        QueryParameterBinding<OffsetDateTime> binding = this.getQueryParameterBindings().getBinding(this.getParameterMetadata().getQueryParameter(position));
        binding.setBindValue(value, temporalType);
        return this;
    }

    public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value) {
        this.getQueryParameterBindings().getBinding(parameter).setBindValue(value);
        return this;
    }

    private <P> QueryParameterBinding<P> locateBinding(Parameter<P> parameter) {
        if (parameter instanceof QueryParameter) {
            return this.getQueryParameterBindings().getBinding((QueryParameter)parameter);
        }
        if (parameter.getName() != null) {
            return this.getQueryParameterBindings().getBinding(parameter.getName());
        }
        if (parameter.getPosition() != null) {
            return this.getQueryParameterBindings().getBinding(parameter.getPosition());
        }
        throw this.getExceptionConverter().convert(new IllegalArgumentException("Could not resolve binding for given parameter reference [" + parameter + "]"));
    }

    private <P> QueryParameterBinding<P> locateBinding(String name) {
        return this.getQueryParameterBindings().getBinding(name);
    }

    private <P> QueryParameterBinding<P> locateBinding(int position) {
        return this.getQueryParameterBindings().getBinding(position);
    }

    public <P> QueryImplementor setParameter(Parameter<P> parameter, P value) {
        this.getProducer().checkOpen();
        if (value instanceof TypedParameterValue) {
            this.setParameter(parameter, ((TypedParameterValue)value).getValue(), ((TypedParameterValue)value).getType());
        } else if (value instanceof Collection && !this.isRegisteredAsBasicType(value.getClass())) {
            this.locateListBinding(parameter).setBindValues((Collection)value);
        } else {
            this.locateBinding(parameter).setBindValue(value);
        }
        return this;
    }

    private <P> void setParameter(Parameter<P> parameter, Object value, Type type) {
        if (parameter instanceof QueryParameter) {
            this.setParameter((QueryParameter)parameter, value, type);
        } else if (value == null) {
            this.locateBinding(parameter).setBindValue(null, type);
        } else if (value instanceof Collection && !this.isRegisteredAsBasicType(value.getClass())) {
            this.locateListBinding(parameter).setBindValues((Collection)value, type);
        } else {
            this.locateBinding(parameter).setBindValue(value, type);
        }
    }

    private QueryParameterListBinding locateListBinding(Parameter parameter) {
        if (parameter instanceof QueryParameter) {
            return this.getQueryParameterBindings().getQueryParameterListBinding((QueryParameter)parameter);
        }
        return this.getQueryParameterBindings().getQueryParameterListBinding(parameter.getName());
    }

    @Override
    public QueryImplementor setParameter(String name, Object value) {
        this.getProducer().checkOpen();
        if (value instanceof TypedParameterValue) {
            TypedParameterValue typedValueWrapper = (TypedParameterValue)value;
            this.setParameter(name, typedValueWrapper.getValue(), typedValueWrapper.getType());
        } else if (value instanceof Collection && !this.isRegisteredAsBasicType(value.getClass())) {
            this.setParameterList(name, (Collection)value);
        } else {
            this.getQueryParameterBindings().getBinding(name).setBindValue(value);
        }
        return this;
    }

    @Override
    public QueryImplementor setParameter(int position, Object value) {
        this.getProducer().checkOpen();
        if (value instanceof TypedParameterValue) {
            TypedParameterValue typedParameterValue = (TypedParameterValue)value;
            this.setParameter(position, typedParameterValue.getValue(), typedParameterValue.getType());
        } else if (value instanceof Collection && !this.isRegisteredAsBasicType(value.getClass())) {
            this.setParameterList(this.getParameterMetadata().getQueryParameter(position), (Collection)value);
        } else {
            this.getQueryParameterBindings().getBinding(position).setBindValue(value);
        }
        return this;
    }

    public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value, Type type) {
        this.getQueryParameterBindings().getBinding(parameter).setBindValue(value, type);
        return this;
    }

    @Override
    public QueryImplementor setParameter(String name, Object value, Type type) {
        this.getQueryParameterBindings().getBinding(name).setBindValue(value, type);
        return this;
    }

    @Override
    public QueryImplementor setParameter(int position, Object value, Type type) {
        this.getQueryParameterBindings().getBinding(position).setBindValue(value, type);
        return this;
    }

    public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value, TemporalType temporalType) {
        this.getQueryParameterBindings().getBinding(parameter).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor setParameter(String name, Object value, TemporalType temporalType) {
        this.getQueryParameterBindings().getBinding(name).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor setParameter(int position, Object value, TemporalType temporalType) {
        this.getQueryParameterBindings().getBinding(position).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public <P> QueryImplementor<R> setParameterList(QueryParameter<P> parameter, Collection<P> values) {
        this.getQueryParameterBindings().getQueryParameterListBinding(parameter).setBindValues(values);
        return this;
    }

    @Override
    public QueryImplementor setParameterList(String name, Collection values) {
        this.getQueryParameterBindings().getQueryParameterListBinding(name).setBindValues(values);
        return this;
    }

    public QueryImplementor setParameterList(int position, Collection values) {
        this.getQueryParameterBindings().getQueryParameterListBinding(position).setBindValues(values);
        return this;
    }

    @Override
    public QueryImplementor setParameterList(String name, Collection values, Type type) {
        this.getQueryParameterBindings().getQueryParameterListBinding(name).setBindValues(values, type);
        return this;
    }

    public QueryImplementor setParameterList(int position, Collection values, Type type) {
        this.getQueryParameterBindings().getQueryParameterListBinding(position).setBindValues(values, type);
        return this;
    }

    @Override
    public QueryImplementor setParameterList(String name, Object[] values, Type type) {
        this.getQueryParameterBindings().getQueryParameterListBinding(name).setBindValues(Arrays.asList(values), type);
        return this;
    }

    public QueryImplementor setParameterList(int position, Object[] values, Type type) {
        this.getQueryParameterBindings().getQueryParameterListBinding(position).setBindValues(Arrays.asList(values), type);
        return this;
    }

    @Override
    public QueryImplementor setParameterList(String name, Object[] values) {
        this.getQueryParameterBindings().getQueryParameterListBinding(name).setBindValues(Arrays.asList(values));
        return this;
    }

    public QueryImplementor setParameterList(int position, Object[] values) {
        this.getQueryParameterBindings().getQueryParameterListBinding(position).setBindValues(Arrays.asList(values));
        return this;
    }

    public QueryImplementor setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        this.getProducer().checkOpen();
        this.getQueryParameterBindings().getBinding((QueryParameter)param).setBindValue(value, temporalType);
        return this;
    }

    public QueryImplementor setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        this.getProducer().checkOpen();
        this.getQueryParameterBindings().getBinding((QueryParameter)param).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
        this.getProducer().checkOpen();
        this.getQueryParameterBindings().getBinding(name).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
        this.getProducer().checkOpen();
        this.getQueryParameterBindings().getBinding(name).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
        this.getProducer().checkOpen();
        this.getQueryParameterBindings().getBinding(position).setBindValue(value, temporalType);
        return this;
    }

    @Override
    public QueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
        this.getProducer().checkOpen();
        this.getQueryParameterBindings().getBinding(position).setBindValue(value, temporalType);
        return this;
    }

    public Set<Parameter<?>> getParameters() {
        this.getProducer().checkOpen(false);
        return this.getParameterMetadata().collectAllParametersJpa();
    }

    public QueryParameter<?> getParameter(String name) {
        this.getProducer().checkOpen(false);
        try {
            return this.getParameterMetadata().getQueryParameter(name);
        }
        catch (HibernateException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public <T> QueryParameter<T> getParameter(String name, Class<T> type) {
        this.getProducer().checkOpen(false);
        try {
            QueryParameter parameter = this.getParameterMetadata().getQueryParameter(name);
            if (!parameter.getParameterType().isAssignableFrom(type)) {
                throw new IllegalArgumentException("The type [" + parameter.getParameterType().getName() + "] associated with the parameter corresponding to name [" + name + "] is not assignable to requested Java type [" + type.getName() + "]");
            }
            return parameter;
        }
        catch (HibernateException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public QueryParameter<?> getParameter(int position) {
        this.getProducer().checkOpen(false);
        try {
            if (this.getParameterMetadata().getPositionalParameterCount() == 0) {
                try {
                    return this.getParameterMetadata().getQueryParameter(Integer.toString(position));
                }
                catch (HibernateException e) {
                    throw new QueryParameterException("could not locate parameter at position [" + position + "]");
                }
            }
            return this.getParameterMetadata().getQueryParameter(position);
        }
        catch (HibernateException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public <T> QueryParameter<T> getParameter(int position, Class<T> type) {
        this.getProducer().checkOpen(false);
        try {
            QueryParameter parameter = this.getParameterMetadata().getQueryParameter(position);
            if (!parameter.getParameterType().isAssignableFrom(type)) {
                throw new IllegalArgumentException("The type [" + parameter.getParameterType().getName() + "] associated with the parameter corresponding to position [" + position + "] is not assignable to requested Java type [" + type.getName() + "]");
            }
            return parameter;
        }
        catch (HibernateException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public boolean isBound(Parameter<?> parameter) {
        this.getProducer().checkOpen();
        return this.getQueryParameterBindings().isBound((QueryParameter)parameter);
    }

    public <T> T getParameterValue(Parameter<T> parameter) {
        LOGGER.tracef("#getParameterValue(%s)", parameter);
        this.getProducer().checkOpen(false);
        return (T)this.getParameterValue((QueryParameter)parameter, queryParameter -> new IllegalStateException("Parameter value not yet bound : " + queryParameter.toString()), (queryParameter, e) -> {
            String message = "Parameter reference [" + queryParameter + "] did not come from this query";
            if (e == null) {
                return new IllegalArgumentException(message);
            }
            return new IllegalArgumentException(message, (Throwable)((Object)e));
        }, (queryParameter, isBound) -> LOGGER.debugf("Checking whether parameter reference [%s] is bound : %s", queryParameter, isBound));
    }

    public Object getParameterValue(String name) {
        this.getProducer().checkOpen(false);
        QueryParameter queryParameter = this.getParameterMetadata().getQueryParameter(name);
        return this.getParameterValue(queryParameter, parameter -> new IllegalStateException("Parameter value not yet bound : " + parameter.getName()), (parameter, e) -> {
            String message = "Could not resolve parameter by name - " + parameter.getName();
            if (e == null) {
                return new IllegalArgumentException(message);
            }
            return new IllegalArgumentException(message, (Throwable)((Object)e));
        }, (parameter, isBound) -> LOGGER.debugf("Checking whether positional named [%s] is bound : %s", (Object)parameter.getName(), isBound));
    }

    public Object getParameterValue(int position) {
        this.getProducer().checkOpen(false);
        QueryParameter queryParameter = this.getParameterMetadata().getQueryParameter(position);
        return this.getParameterValue(queryParameter, parameter -> new IllegalStateException("Parameter value not yet bound : " + parameter.getPosition()), (parameter, e) -> {
            String message = "Could not resolve parameter by position - " + parameter.getPosition();
            if (e == null) {
                return new IllegalArgumentException(message);
            }
            return new IllegalArgumentException(message, (Throwable)((Object)e));
        }, (parameter, isBound) -> LOGGER.debugf("Checking whether positional parameter [%s] is bound : %s", (Object)parameter.getPosition(), isBound));
    }

    private Object getParameterValue(QueryParameter queryParameter, Function<QueryParameter, IllegalStateException> notBoundParamenterException, BiFunction<QueryParameter, QueryParameterException, IllegalArgumentException> couldNotResolveParameterException, BiConsumer<QueryParameter, Boolean> boundCheckingLogger) {
        try {
            QueryParameterBindings parameterBindings = this.getQueryParameterBindings();
            if (queryParameter == null) {
                throw couldNotResolveParameterException.apply(queryParameter, null);
            }
            if (parameterBindings.isMultiValuedBinding(queryParameter)) {
                QueryParameterListBinding queryParameterListBinding = parameterBindings.getQueryParameterListBinding(queryParameter);
                Collection bindValues = queryParameterListBinding.getBindValues();
                if (bindValues == null) {
                    throw notBoundParamenterException.apply(queryParameter);
                }
                return bindValues;
            }
            QueryParameterBinding binding = parameterBindings.getBinding(queryParameter);
            boolean bound = binding.isBound();
            boundCheckingLogger.accept(queryParameter, bound);
            if (!bound) {
                throw notBoundParamenterException.apply(queryParameter);
            }
            return binding.getBindValue();
        }
        catch (QueryParameterException e) {
            throw couldNotResolveParameterException.apply(queryParameter, e);
        }
    }

    @Override
    public QueryImplementor setProperties(Object bean) {
        String[] params;
        Class<?> clazz = bean.getClass();
        for (String namedParam : params = this.getNamedParameters()) {
            try {
                PropertyAccess propertyAccess = BuiltInPropertyAccessStrategies.BASIC.getStrategy().buildPropertyAccess(clazz, namedParam);
                Getter getter = propertyAccess.getGetter();
                Class retType = getter.getReturnType();
                Object object = getter.get(bean);
                if (Collection.class.isAssignableFrom(retType)) {
                    this.setParameterList(namedParam, (Collection)object);
                    continue;
                }
                if (retType.isArray()) {
                    this.setParameterList(namedParam, (Object[])object);
                    continue;
                }
                Type type = this.determineType(namedParam, retType);
                this.setParameter(namedParam, object, type);
            }
            catch (PropertyNotFoundException propertyNotFoundException) {
                // empty catch block
            }
        }
        return this;
    }

    protected Type determineType(String namedParam, Class retType) {
        Type type = this.getQueryParameterBindings().getBinding(namedParam).getBindType();
        if (type == null) {
            type = this.getParameterMetadata().getQueryParameter(namedParam).getHibernateType();
        }
        if (type == null) {
            type = this.getProducer().getFactory().resolveParameterBindType(retType);
        }
        return type;
    }

    @Override
    public QueryImplementor setProperties(Map map) {
        String[] namedParameterNames;
        for (String paramName : namedParameterNames = this.getNamedParameters()) {
            Object object = map.get(paramName);
            if (object == null) {
                if (!map.containsKey(paramName)) continue;
                this.setParameter(paramName, null, this.determineType(paramName, null));
                continue;
            }
            Class<?> retType = object.getClass();
            if (Collection.class.isAssignableFrom(retType)) {
                this.setParameterList(paramName, (Collection)object);
                continue;
            }
            if (retType.isArray()) {
                this.setParameterList(paramName, (Object[])object);
                continue;
            }
            this.setParameter(paramName, object, this.determineType(paramName, retType));
        }
        return this;
    }

    @Override
    public QueryImplementor setResultTransformer(ResultTransformer transformer) {
        this.resultTransformer = transformer;
        return this;
    }

    @Override
    public RowSelection getQueryOptions() {
        return this.queryOptions;
    }

    public int getMaxResults() {
        this.getProducer().checkOpen();
        return this.queryOptions.getMaxRows() == null ? Integer.MAX_VALUE : this.queryOptions.getMaxRows();
    }

    @Override
    public QueryImplementor setMaxResults(int maxResult) {
        this.getProducer().checkOpen();
        if (maxResult < 0) {
            throw new IllegalArgumentException("max-results cannot be negative");
        }
        this.queryOptions.setMaxRows(maxResult);
        return this;
    }

    public int getFirstResult() {
        this.getProducer().checkOpen();
        return this.queryOptions.getFirstRow() == null ? 0 : this.queryOptions.getFirstRow();
    }

    @Override
    public QueryImplementor setFirstResult(int startPosition) {
        this.getProducer().checkOpen();
        if (startPosition < 0) {
            throw new IllegalArgumentException("first-result value cannot be negative : " + startPosition);
        }
        this.queryOptions.setFirstRow(startPosition);
        return this;
    }

    public Set<String> getSupportedHints() {
        return QueryHints.getDefinedHints();
    }

    public Map<String, Object> getHints() {
        this.getProducer().checkOpen(false);
        HashMap<String, Object> hints = new HashMap<String, Object>();
        this.collectBaselineHints(hints);
        this.collectHints(hints);
        return hints;
    }

    protected void collectBaselineHints(Map<String, Object> hints) {
    }

    protected void collectHints(Map<String, Object> hints) {
        LockOptions lockOptions;
        int lockOptionsTimeOut;
        RowSelection queryOptions = this.getQueryOptions();
        Integer queryTimeout = queryOptions.getTimeout();
        if (queryTimeout != null) {
            hints.put("org.hibernate.timeout", queryTimeout);
            hints.put("javax.persistence.query.timeout", queryTimeout * 1000);
            hints.put("jakarta.persistence.query.timeout", queryTimeout * 1000);
        }
        if ((lockOptionsTimeOut = (lockOptions = this.getLockOptions()).getTimeOut()) != -1) {
            hints.put("javax.persistence.lock.timeout", lockOptionsTimeOut);
            hints.put("jakarta.persistence.lock.timeout", lockOptionsTimeOut);
        }
        if (lockOptions.getScope()) {
            hints.put("javax.persistence.lock.scope", lockOptions.getScope());
            hints.put("jakarta.persistence.lock.scope", lockOptions.getScope());
        }
        if (lockOptions.hasAliasSpecificLockModes() && this.canApplyAliasSpecificLockModeHints()) {
            for (Map.Entry<String, LockMode> entry : lockOptions.getAliasSpecificLocks()) {
                hints.put("org.hibernate.lockMode." + entry.getKey(), entry.getValue().name());
            }
        }
        this.putIfNotNull(hints, "org.hibernate.comment", this.getComment());
        this.putIfNotNull(hints, "org.hibernate.fetchSize", queryOptions.getFetchSize());
        this.putIfNotNull(hints, "org.hibernate.flushMode", this.getHibernateFlushMode());
        if (this.cacheStoreMode != null || this.cacheRetrieveMode != null) {
            this.putIfNotNull(hints, "org.hibernate.cacheMode", CacheModeHelper.interpretCacheMode(this.cacheStoreMode, this.cacheRetrieveMode));
            this.putIfNotNull(hints, "javax.persistence.cache.retrieveMode", (Enum)this.cacheRetrieveMode);
            this.putIfNotNull(hints, "jakarta.persistence.cache.retrieveMode", (Enum)this.cacheRetrieveMode);
            this.putIfNotNull(hints, "javax.persistence.cache.storeMode", (Enum)this.cacheStoreMode);
            this.putIfNotNull(hints, "jakarta.persistence.cache.storeMode", (Enum)this.cacheStoreMode);
        }
        if (this.isCacheable()) {
            hints.put("org.hibernate.cacheable", true);
            this.putIfNotNull(hints, "org.hibernate.cacheRegion", this.getCacheRegion());
        }
        if (this.isReadOnly()) {
            hints.put("org.hibernate.readOnly", true);
        }
        if (this.entityGraphQueryHint != null) {
            hints.put(this.entityGraphQueryHint.getHintName(), this.entityGraphQueryHint.getOriginEntityGraph());
        }
    }

    protected void putIfNotNull(Map<String, Object> hints, String hintName, Enum hintValue) {
        if (hintValue != null) {
            hints.put(hintName, hintValue);
        }
    }

    protected void putIfNotNull(Map<String, Object> hints, String hintName, Object hintValue) {
        if (hintValue != null) {
            hints.put(hintName, hintValue);
        }
    }

    @Override
    public QueryImplementor setHint(String hintName, Object value) {
        this.getProducer().checkOpen(true);
        boolean applied = false;
        try {
            if ("org.hibernate.timeout".equals(hintName)) {
                applied = this.applyTimeoutHint(ConfigurationHelper.getInteger(value));
            } else if ("javax.persistence.query.timeout".equals(hintName) || "jakarta.persistence.query.timeout".equals(hintName)) {
                int timeout = (int)Math.round(ConfigurationHelper.getInteger(value).doubleValue() / 1000.0);
                applied = this.applyTimeoutHint(timeout);
            } else if ("javax.persistence.lock.timeout".equals(hintName) || "jakarta.persistence.lock.timeout".equals(hintName)) {
                applied = this.applyLockTimeoutHint(ConfigurationHelper.getInteger(value));
            } else if ("org.hibernate.comment".equals(hintName)) {
                applied = this.applyCommentHint((String)value);
            } else if ("org.hibernate.fetchSize".equals(hintName)) {
                applied = this.applyFetchSizeHint(ConfigurationHelper.getInteger(value));
            } else if ("org.hibernate.cacheable".equals(hintName)) {
                applied = this.applyCacheableHint(ConfigurationHelper.getBoolean(value));
            } else if ("org.hibernate.cacheRegion".equals(hintName)) {
                applied = this.applyCacheRegionHint((String)value);
            } else if ("org.hibernate.readOnly".equals(hintName)) {
                applied = this.applyReadOnlyHint(ConfigurationHelper.getBoolean(value));
            } else if ("org.hibernate.flushMode".equals(hintName)) {
                applied = this.applyFlushModeHint(ConfigurationHelper.getFlushMode(value));
            } else if ("org.hibernate.cacheMode".equals(hintName)) {
                applied = this.applyCacheModeHint(ConfigurationHelper.getCacheMode(value));
            } else if ("javax.persistence.cache.retrieveMode".equals(hintName) || "jakarta.persistence.cache.retrieveMode".equals(hintName)) {
                CacheRetrieveMode retrieveMode = value != null ? CacheRetrieveMode.valueOf((String)value.toString()) : null;
                applied = this.applyJpaCacheRetrieveMode(retrieveMode);
            } else if ("javax.persistence.cache.storeMode".equals(hintName) || "jakarta.persistence.cache.storeMode".equals(hintName)) {
                CacheStoreMode storeMode = value != null ? CacheStoreMode.valueOf((String)value.toString()) : null;
                applied = this.applyJpaCacheStoreMode(storeMode);
            } else if ("org.hibernate.query.native.spaces".equals(hintName)) {
                applied = this.applyQuerySpaces(value);
            } else if ("org.hibernate.lockMode".equals(hintName)) {
                applied = this.applyNativeQueryLockMode(value);
            } else if (hintName.startsWith("org.hibernate.lockMode")) {
                if (this.canApplyAliasSpecificLockModeHints()) {
                    String alias = hintName.substring("org.hibernate.lockMode".length() + 1);
                    try {
                        LockMode lockMode = LockModeTypeHelper.interpretLockMode(value);
                        this.applyAliasSpecificLockModeHint(alias, lockMode);
                    }
                    catch (Exception e) {
                        MSG_LOGGER.unableToDetermineLockModeValue(hintName, value);
                        applied = false;
                    }
                } else {
                    applied = false;
                }
            } else if (QueryHints.HINT_FETCHGRAPH.equals(hintName) || QueryHints.HINT_LOADGRAPH.equals(hintName) || QueryHints.JAKARTA_HINT_FETCHGRAPH.equals(hintName) || QueryHints.JAKARTA_HINT_LOADGRAPH.equals(hintName)) {
                if (value instanceof RootGraph) {
                    this.applyGraph((RootGraph)value, GraphSemantic.fromJpaHintName(hintName));
                    this.applyEntityGraphQueryHint(new EntityGraphQueryHint(hintName, (RootGraphImpl)value));
                } else {
                    MSG_LOGGER.warnf("The %s hint was set, but the value was not an EntityGraph!", hintName);
                }
                applied = true;
            } else if ("hibernate.query.followOnLocking".equals(hintName)) {
                applied = this.applyFollowOnLockingHint(ConfigurationHelper.getBoolean(value));
            } else if ("hibernate.query.passDistinctThrough".equals(hintName)) {
                applied = this.applyPassDistinctThrough(ConfigurationHelper.getBoolean(value));
            } else {
                MSG_LOGGER.ignoringUnrecognizedQueryHint(hintName);
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("Value for hint");
        }
        if (!applied) {
            this.handleUnrecognizedHint(hintName, value);
        }
        return this;
    }

    protected boolean applyQuerySpaces(Object value) {
        throw new IllegalStateException("Illegal attempt to apply native-query spaces to a non-native query");
    }

    protected void handleUnrecognizedHint(String hintName, Object value) {
        MSG_LOGGER.debugf("Skipping unsupported query hint [%s]", hintName);
    }

    protected boolean applyJpaCacheRetrieveMode(CacheRetrieveMode mode) {
        this.cacheRetrieveMode = mode;
        return true;
    }

    protected boolean applyJpaCacheStoreMode(CacheStoreMode storeMode) {
        this.cacheStoreMode = storeMode;
        return true;
    }

    protected boolean applyNativeQueryLockMode(Object value) {
        if (!this.isNativeQuery()) {
            throw new IllegalStateException("Illegal attempt to set lock mode on non-native query via hint; use Query#setLockMode instead");
        }
        return false;
    }

    protected boolean applyTimeoutHint(int timeout) {
        this.setTimeout(timeout);
        return true;
    }

    protected boolean applyLockTimeoutHint(int timeout) {
        this.getLockOptions().setTimeOut(timeout);
        return true;
    }

    protected boolean applyCommentHint(String comment) {
        this.setComment(comment);
        return true;
    }

    protected boolean applyFetchSizeHint(int fetchSize) {
        this.setFetchSize(fetchSize);
        return true;
    }

    protected boolean applyCacheableHint(boolean isCacheable) {
        this.setCacheable(isCacheable);
        return true;
    }

    protected boolean applyCacheRegionHint(String regionName) {
        this.setCacheRegion(regionName);
        return true;
    }

    protected boolean applyReadOnlyHint(boolean isReadOnly) {
        this.setReadOnly(isReadOnly);
        return true;
    }

    protected boolean applyCacheModeHint(CacheMode cacheMode) {
        this.setCacheMode(cacheMode);
        return true;
    }

    protected boolean applyFlushModeHint(FlushMode flushMode) {
        this.setFlushMode(flushMode);
        return true;
    }

    protected boolean canApplyAliasSpecificLockModeHints() {
        return true;
    }

    protected boolean applyLockModeTypeHint(LockModeType lockModeType) {
        this.getLockOptions().setLockMode(LockModeTypeHelper.getLockMode(lockModeType));
        return true;
    }

    protected boolean applyHibernateLockModeHint(LockMode lockMode) {
        this.getLockOptions().setLockMode(lockMode);
        return true;
    }

    protected void applyAliasSpecificLockModeHint(String alias, LockMode lockMode) {
        this.getLockOptions().setAliasSpecificLockMode(alias, lockMode);
    }

    @Override
    public Query<R> applyGraph(RootGraph graph, GraphSemantic semantic) {
        if (semantic == null) {
            this.entityGraphQueryHint = null;
        } else {
            if (graph == null) {
                throw new IllegalStateException("Semantic was non-null, but graph was null");
            }
            this.applyEntityGraphQueryHint(new EntityGraphQueryHint((RootGraphImplementor)graph, semantic));
        }
        return this;
    }

    @Deprecated
    protected void applyEntityGraphQueryHint(EntityGraphQueryHint hint) {
        this.entityGraphQueryHint = hint;
    }

    protected boolean applyFollowOnLockingHint(Boolean followOnLocking) {
        this.getLockOptions().setFollowOnLocking(followOnLocking);
        return true;
    }

    protected boolean applyPassDistinctThrough(boolean passDistinctThrough) {
        this.passDistinctThrough = passDistinctThrough;
        return true;
    }

    protected abstract boolean isNativeQuery();

    public LockModeType getLockMode() {
        this.getProducer().checkOpen(false);
        if (!this.isSelect()) {
            throw new IllegalStateException("Illegal attempt to get lock mode on a non-SELECT query");
        }
        return LockModeTypeHelper.getLockModeType(this.lockOptions.getLockMode());
    }

    public <T> T unwrap(Class<T> cls) {
        if (cls.isInstance(this.getProducer())) {
            return (T)this.getProducer();
        }
        if (cls.isInstance(this.getParameterMetadata())) {
            return (T)this.getParameterMetadata();
        }
        if (cls.isInstance(this.getQueryParameterBindings())) {
            return (T)this.getQueryParameterBindings();
        }
        if (cls.isInstance(this)) {
            return (T)this;
        }
        throw new HibernateException("Could not unwrap this [" + this.toString() + "] as requested Java type [" + cls.getName() + "]");
    }

    protected QueryParameters makeQueryParametersForExecution(String hql) {
        HQLQueryPlan entityGraphHintedQueryPlan;
        if (this.entityGraphQueryHint == null) {
            entityGraphHintedQueryPlan = null;
        } else {
            SharedSessionContractImplementor producer = this.getProducer();
            entityGraphHintedQueryPlan = new HQLQueryPlan(hql, false, producer.getLoadQueryInfluencers().getEnabledFilters(), producer.getFactory(), this.entityGraphQueryHint);
        }
        QueryParameters queryParameters = new QueryParameters(this.getQueryParameterBindings(), this.getLockOptions(), this.queryOptions, true, this.isReadOnly(), this.cacheable, this.cacheRegion, this.comment, this.dbHints, null, this.optionalObject, this.optionalEntityName, this.optionalId, this.resultTransformer);
        this.appendQueryPlanToQueryParameters(hql, queryParameters, entityGraphHintedQueryPlan);
        if (this.passDistinctThrough != null) {
            queryParameters.setPassDistinctThrough(this.passDistinctThrough);
        }
        return queryParameters;
    }

    protected void appendQueryPlanToQueryParameters(String hql, QueryParameters queryParameters, HQLQueryPlan queryPlan) {
        if (queryPlan != null) {
            queryParameters.setQueryPlan(queryPlan);
        }
    }

    public QueryParameters getQueryParameters() {
        String expandedQuery = this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer());
        return this.makeQueryParametersForExecution(expandedQuery);
    }

    protected Type[] getPositionalParameterTypes() {
        return this.getQueryParameterBindings().collectPositionalBindTypes();
    }

    protected Object[] getPositionalParameterValues() {
        return this.getQueryParameterBindings().collectPositionalBindValues();
    }

    protected Map<String, TypedValue> getNamedParameterMap() {
        return this.getQueryParameterBindings().collectNamedParameterBindings();
    }

    protected void beforeQuery() {
        CacheMode effectiveCacheMode;
        if (this.optionalId == null) {
            this.getQueryParameterBindings().verifyParametersBound(this.isCallable());
        }
        assert (this.sessionFlushMode == null);
        assert (this.sessionCacheMode == null);
        if (this.flushMode != null) {
            this.sessionFlushMode = this.getProducer().getHibernateFlushMode();
            this.getProducer().setHibernateFlushMode(this.flushMode);
        }
        if ((effectiveCacheMode = CacheModeHelper.effectiveCacheMode(this.cacheStoreMode, this.cacheRetrieveMode)) != null) {
            this.sessionCacheMode = this.getProducer().getCacheMode();
            this.getProducer().setCacheMode(effectiveCacheMode);
        }
        if (this.entityGraphQueryHint != null && this.entityGraphQueryHint.getSemantic() == GraphSemantic.FETCH) {
            this.getProducer().setEnforcingFetchGraph(true);
        }
    }

    protected void afterQuery() {
        if (this.sessionFlushMode != null) {
            this.getProducer().setHibernateFlushMode(this.sessionFlushMode);
            this.sessionFlushMode = null;
        }
        if (this.sessionCacheMode != null) {
            this.getProducer().setCacheMode(this.sessionCacheMode);
            this.sessionCacheMode = null;
        }
        this.getProducer().setEnforcingFetchGraph(false);
    }

    @Override
    public Iterator<R> iterate() {
        this.beforeQuery();
        try {
            Iterator<R> iterator = this.doIterate();
            return iterator;
        }
        finally {
            this.afterQuery();
        }
    }

    protected Iterator<R> doIterate() {
        if (this.getMaxResults() == 0) {
            return Collections.emptyIterator();
        }
        return this.getProducer().iterate(this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer()), this.getQueryParameters());
    }

    @Override
    public ScrollableResultsImplementor scroll() {
        return this.scroll(this.getProducer().getJdbcServices().getJdbcEnvironment().getDialect().defaultScrollMode());
    }

    @Override
    public ScrollableResultsImplementor scroll(ScrollMode scrollMode) {
        this.beforeQuery();
        try {
            ScrollableResultsImplementor scrollableResultsImplementor = this.doScroll(scrollMode);
            return scrollableResultsImplementor;
        }
        finally {
            this.afterQuery();
        }
    }

    protected ScrollableResultsImplementor doScroll(ScrollMode scrollMode) {
        if (this.getMaxResults() == 0) {
            return EmptyScrollableResults.INSTANCE;
        }
        String query = this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer());
        QueryParameters queryParameters = this.makeQueryParametersForExecution(query);
        queryParameters.setScrollMode(scrollMode);
        return this.getProducer().scroll(query, queryParameters);
    }

    @Override
    public Stream<R> stream() {
        if (this.getMaxResults() == 0) {
            Spliterator spliterator = Spliterators.emptySpliterator();
            return StreamSupport.stream(spliterator, false);
        }
        ScrollableResultsImplementor scrollableResults = this.scroll(ScrollMode.FORWARD_ONLY);
        ScrollableResultsIterator iterator = new ScrollableResultsIterator(scrollableResults);
        Spliterator spliterator = Spliterators.spliteratorUnknownSize(iterator, 256);
        return new StreamDecorator(StreamSupport.stream(spliterator, false), iterator::close);
    }

    @Override
    public Optional<R> uniqueResultOptional() {
        return Optional.ofNullable(this.uniqueResult());
    }

    @Override
    public List<R> list() {
        this.beforeQuery();
        try {
            List<R> list = this.doList();
            return list;
        }
        catch (QueryExecutionRequestException he) {
            throw new IllegalStateException((Throwable)((Object)he));
        }
        catch (TypeMismatchException e) {
            throw new IllegalArgumentException((Throwable)((Object)e));
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he, this.getLockOptions());
        }
        finally {
            this.afterQuery();
        }
    }

    protected boolean isCallable() {
        return false;
    }

    protected List<R> doList() {
        if (this.getMaxResults() == 0) {
            return Collections.EMPTY_LIST;
        }
        if (this.lockOptions.getLockMode() != null && this.lockOptions.getLockMode() != LockMode.NONE && !this.getProducer().isTransactionInProgress()) {
            throw new TransactionRequiredException("no transaction is in progress");
        }
        String expandedQuery = this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer());
        return this.getProducer().list(expandedQuery, this.makeQueryParametersForExecution(expandedQuery));
    }

    protected abstract QueryParameterBindings getQueryParameterBindings();

    @Override
    public R uniqueResult() {
        return AbstractProducedQuery.uniqueElement(this.list());
    }

    @Override
    public R getSingleResult() {
        try {
            List<R> list = this.list();
            if (list.size() == 0) {
                throw new NoResultException("No entity found for query");
            }
            return AbstractProducedQuery.uniqueElement(list);
        }
        catch (HibernateException e) {
            throw this.getExceptionConverter().convert(e, this.getLockOptions());
        }
    }

    public static <R> R uniqueElement(List<R> list) throws NonUniqueResultException {
        int size = list.size();
        if (size == 0) {
            return null;
        }
        R first = list.get(0);
        for (int i = 1; i < size; ++i) {
            if (list.get(i) == first) continue;
            throw new NonUniqueResultException(list.size());
        }
        return first;
    }

    public int executeUpdate() throws HibernateException {
        this.getProducer().checkTransactionNeededForUpdateOperation("Executing an update/delete query");
        this.beforeQuery();
        try {
            int n = this.doExecuteUpdate();
            return n;
        }
        catch (QueryExecutionRequestException e) {
            throw new IllegalStateException((Throwable)((Object)e));
        }
        catch (TypeMismatchException e) {
            throw new IllegalArgumentException((Throwable)((Object)e));
        }
        catch (HibernateException e) {
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            this.afterQuery();
        }
    }

    protected int doExecuteUpdate() {
        String expandedQuery = this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer());
        return this.getProducer().executeUpdate(expandedQuery, this.makeQueryParametersForExecution(expandedQuery));
    }

    protected String resolveEntityName(Object val) {
        if (val == null) {
            throw new IllegalArgumentException("entity for parameter binding cannot be null");
        }
        return this.getProducer().bestGuessEntityName(val);
    }

    @Override
    public void setOptionalEntityName(String optionalEntityName) {
        this.optionalEntityName = optionalEntityName;
    }

    @Override
    public void setOptionalId(Serializable optionalId) {
        this.optionalId = optionalId;
    }

    @Override
    public void setOptionalObject(Object optionalObject) {
        this.optionalObject = optionalObject;
    }

    @Override
    public Type determineProperBooleanType(String name, Object value, Type defaultType) {
        QueryParameterBinding binding = this.getQueryParameterBindings().getBinding(name);
        return binding.getBindType() != null ? binding.getBindType() : defaultType;
    }

    @Override
    public Type determineProperBooleanType(int position, Object value, Type defaultType) {
        QueryParameterBinding binding = this.getQueryParameterBindings().getBinding(position);
        return binding.getBindType() != null ? binding.getBindType() : defaultType;
    }

    protected boolean isSelect() {
        return this.getProducer().getFactory().getQueryPlanCache().getHQLQueryPlan(this.getQueryString(), false, Collections.emptyMap()).isSelect();
    }

    protected ExceptionConverter getExceptionConverter() {
        return this.producer.getExceptionConverter();
    }

    private boolean isRegisteredAsBasicType(Class cl) {
        return this.producer.getFactory().getTypeResolver().basic(cl.getName()) != null;
    }
}

