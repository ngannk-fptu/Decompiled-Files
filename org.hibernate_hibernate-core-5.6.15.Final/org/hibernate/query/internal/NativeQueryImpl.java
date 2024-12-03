/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.Parameter
 *  javax.persistence.TemporalType
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.SynchronizeableQuery;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.jpa.internal.util.LockModeTypeHelper;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.Query;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.internal.NativeQueryReturnBuilder;
import org.hibernate.query.internal.NativeQueryReturnBuilderFetchImpl;
import org.hibernate.query.internal.NativeQueryReturnBuilderRootImpl;
import org.hibernate.query.internal.QueryParameterBindingsImpl;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

public class NativeQueryImpl<T>
extends AbstractProducedQuery<T>
implements NativeQueryImplementor<T> {
    private final String sqlString;
    private final QueryParameterBindingsImpl queryParameterBindings;
    private List<NativeSQLQueryReturn> queryReturns;
    private List<NativeQueryReturnBuilder> queryReturnBuilders;
    private boolean autoDiscoverTypes;
    private Collection<String> querySpaces;
    private final boolean callable;
    private final LockOptions lockOptions = new LockOptions();
    private Serializable collectionKey;

    public NativeQueryImpl(NamedSQLQueryDefinition queryDef, SharedSessionContractImplementor session, ParameterMetadata parameterMetadata) {
        super(session, parameterMetadata);
        this.sqlString = queryDef.getQueryString();
        this.callable = queryDef.isCallable();
        ArrayList<String> arrayList = this.querySpaces = queryDef.getQuerySpaces() == null ? null : new ArrayList<String>(queryDef.getQuerySpaces());
        if (queryDef.getResultSetRef() != null) {
            ResultSetMappingDefinition definition = session.getFactory().getNamedQueryRepository().getResultSetMappingDefinition(queryDef.getResultSetRef());
            if (definition == null) {
                throw new MappingException("Unable to find resultset-ref definition: " + queryDef.getResultSetRef());
            }
            this.queryReturns = new ArrayList<NativeSQLQueryReturn>(Arrays.asList(definition.getQueryReturns()));
        } else {
            this.queryReturns = queryDef.getQueryReturns() != null && queryDef.getQueryReturns().length > 0 ? new ArrayList<NativeSQLQueryReturn>(Arrays.asList(queryDef.getQueryReturns())) : new ArrayList<NativeSQLQueryReturn>();
        }
        this.queryParameterBindings = QueryParameterBindingsImpl.from(parameterMetadata, session.getFactory(), session.isQueryParametersValidationEnabled());
    }

    public NativeQueryImpl(String sqlString, boolean callable, SharedSessionContractImplementor session, ParameterMetadata sqlParameterMetadata) {
        super(session, sqlParameterMetadata);
        this.queryReturns = new ArrayList<NativeSQLQueryReturn>();
        this.sqlString = sqlString;
        this.callable = callable;
        this.querySpaces = new ArrayList<String>();
        this.queryParameterBindings = QueryParameterBindingsImpl.from(sqlParameterMetadata, session.getFactory(), session.isQueryParametersValidationEnabled());
    }

    @Override
    protected QueryParameterBindings getQueryParameterBindings() {
        return this.queryParameterBindings;
    }

    @Override
    public NativeQuery setResultSetMapping(String name) {
        ResultSetMappingDefinition mapping = this.getProducer().getFactory().getNamedQueryRepository().getResultSetMappingDefinition(name);
        if (mapping == null) {
            throw new MappingException("Unknown SqlResultSetMapping [" + name + "]");
        }
        NativeSQLQueryReturn[] returns = mapping.getQueryReturns();
        this.queryReturns.addAll(Arrays.asList(returns));
        return this;
    }

    @Override
    public String getQueryString() {
        return this.sqlString;
    }

    @Override
    public boolean isCallable() {
        return this.callable;
    }

    @Override
    public List<NativeSQLQueryReturn> getQueryReturns() {
        this.prepareQueryReturnsIfNecessary();
        return this.queryReturns;
    }

    @Override
    protected List<T> doList() {
        return this.getProducer().list(this.generateQuerySpecification(), this.getQueryParameters());
    }

    private NativeSQLQuerySpecification generateQuerySpecification() {
        return new NativeSQLQuerySpecification(this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer()), this.queryReturns.toArray(new NativeSQLQueryReturn[this.queryReturns.size()]), this.querySpaces);
    }

    @Override
    public QueryParameters getQueryParameters() {
        QueryParameters queryParameters = super.getQueryParameters();
        queryParameters.setCallable(this.callable);
        queryParameters.setAutoDiscoverScalarTypes(this.autoDiscoverTypes);
        if (this.collectionKey != null) {
            queryParameters.setCollectionKeys(new Serializable[]{this.collectionKey});
        }
        return queryParameters;
    }

    private void prepareQueryReturnsIfNecessary() {
        if (this.queryReturnBuilders != null) {
            if (!this.queryReturnBuilders.isEmpty()) {
                if (this.queryReturns != null) {
                    this.queryReturns.clear();
                    this.queryReturns = null;
                }
                this.queryReturns = new ArrayList<NativeSQLQueryReturn>();
                for (NativeQueryReturnBuilder builder : this.queryReturnBuilders) {
                    this.queryReturns.add(builder.buildReturn());
                }
                this.queryReturnBuilders.clear();
            }
            this.queryReturnBuilders = null;
        }
    }

    @Override
    protected ScrollableResultsImplementor doScroll(ScrollMode scrollMode) {
        NativeSQLQuerySpecification nativeSQLQuerySpecification = this.generateQuerySpecification();
        QueryParameters queryParameters = this.getQueryParameters();
        queryParameters.setScrollMode(scrollMode);
        return this.getProducer().scroll(nativeSQLQuerySpecification, queryParameters);
    }

    @Override
    protected void beforeQuery() {
        boolean noReturns;
        this.prepareQueryReturnsIfNecessary();
        boolean bl = noReturns = this.queryReturns == null || this.queryReturns.isEmpty();
        if (noReturns) {
            this.autoDiscoverTypes = true;
        } else {
            for (NativeSQLQueryReturn queryReturn : this.queryReturns) {
                if (queryReturn instanceof NativeSQLQueryScalarReturn) {
                    NativeSQLQueryScalarReturn scalar = (NativeSQLQueryScalarReturn)queryReturn;
                    if (scalar.getType() != null) continue;
                    this.autoDiscoverTypes = true;
                    break;
                }
                if (!NativeSQLQueryConstructorReturn.class.isInstance(queryReturn)) continue;
                this.autoDiscoverTypes = true;
                break;
            }
        }
        super.beforeQuery();
        if (CollectionHelper.isNotEmpty(this.getSynchronizedQuerySpaces())) {
            return;
        }
        if (this.shouldFlush()) {
            this.getProducer().flush();
        }
    }

    @Override
    public Iterator<T> iterate() {
        throw new UnsupportedOperationException("SQL queries do not currently support iteration");
    }

    private boolean shouldFlush() {
        if (this.getProducer().isTransactionInProgress()) {
            FlushMode effectiveFlushMode = this.getHibernateFlushMode();
            if (effectiveFlushMode == null) {
                effectiveFlushMode = this.getProducer().getHibernateFlushMode();
            }
            if (effectiveFlushMode == FlushMode.ALWAYS) {
                return true;
            }
            if (effectiveFlushMode == FlushMode.AUTO && this.getProducer().getFactory().getSessionFactoryOptions().isJpaBootstrap()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected int doExecuteUpdate() {
        return this.getProducer().executeNativeUpdate(this.generateQuerySpecification(), this.getQueryParameters());
    }

    @Override
    public NativeQueryImplementor setCollectionKey(Serializable key) {
        this.collectionKey = key;
        return this;
    }

    @Override
    public NativeQueryImplementor<T> addScalar(String columnAlias) {
        return this.addScalar(columnAlias, null);
    }

    @Override
    public NativeQueryImplementor<T> addScalar(final String columnAlias, final Type type) {
        this.addReturnBuilder(new NativeQueryReturnBuilder(){

            @Override
            public NativeSQLQueryReturn buildReturn() {
                return new NativeSQLQueryScalarReturn(columnAlias, type);
            }
        });
        return this;
    }

    protected void addReturnBuilder(NativeQueryReturnBuilder builder) {
        if (this.queryReturnBuilders == null) {
            this.queryReturnBuilders = new ArrayList<NativeQueryReturnBuilder>();
        }
        this.queryReturnBuilders.add(builder);
    }

    @Override
    public SQLQuery.RootReturn addRoot(String tableAlias, String entityName) {
        NativeQueryReturnBuilderRootImpl builder = new NativeQueryReturnBuilderRootImpl(tableAlias, entityName);
        this.addReturnBuilder(builder);
        return builder;
    }

    @Override
    public SQLQuery.RootReturn addRoot(String tableAlias, Class entityType) {
        return this.addRoot(tableAlias, entityType.getName());
    }

    @Override
    public NativeQueryImplementor<T> addEntity(String entityName) {
        return this.addEntity(StringHelper.unqualify(entityName), entityName);
    }

    @Override
    public NativeQueryImplementor<T> addEntity(String tableAlias, String entityName) {
        this.addRoot(tableAlias, entityName);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> addEntity(String tableAlias, String entityName, LockMode lockMode) {
        this.addRoot(tableAlias, entityName).setLockMode(lockMode);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> addEntity(Class entityType) {
        return this.addEntity(entityType.getName());
    }

    @Override
    public NativeQueryImplementor<T> addEntity(String tableAlias, Class entityClass) {
        return this.addEntity(tableAlias, entityClass.getName());
    }

    @Override
    public NativeQueryImplementor<T> addEntity(String tableAlias, Class entityClass, LockMode lockMode) {
        return this.addEntity(tableAlias, entityClass.getName(), lockMode);
    }

    @Override
    public SQLQuery.FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
        NativeQueryReturnBuilderFetchImpl builder = new NativeQueryReturnBuilderFetchImpl(tableAlias, ownerTableAlias, joinPropertyName);
        this.addReturnBuilder(builder);
        return builder;
    }

    @Override
    public NativeQueryImplementor<T> addJoin(String tableAlias, String path) {
        this.createFetchJoin(tableAlias, path);
        return this;
    }

    private SQLQuery.FetchReturn createFetchJoin(String tableAlias, String path) {
        int loc = path.indexOf(46);
        if (loc < 0) {
            throw new QueryException("not a property path: " + path);
        }
        String ownerTableAlias = path.substring(0, loc);
        String joinedPropertyName = path.substring(loc + 1);
        return this.addFetch(tableAlias, ownerTableAlias, joinedPropertyName);
    }

    @Override
    public NativeQueryImplementor<T> addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
        this.addFetch(tableAlias, ownerTableAlias, joinPropertyName);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> addJoin(String tableAlias, String path, LockMode lockMode) {
        this.createFetchJoin(tableAlias, path).setLockMode(lockMode);
        return this;
    }

    @Override
    public String[] getReturnAliases() {
        throw new UnsupportedOperationException("Native (SQL) queries do not support returning aliases");
    }

    @Override
    public Type[] getReturnTypes() {
        throw new UnsupportedOperationException("Native (SQL) queries do not support returning 'return types'");
    }

    @Override
    public NativeQueryImplementor<T> setEntity(int position, Object val) {
        this.setParameter(position, val, this.getProducer().getFactory().getTypeHelper().entity(this.resolveEntityName(val)));
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setEntity(String name, Object val) {
        this.setParameter(name, val, this.getProducer().getFactory().getTypeHelper().entity(this.resolveEntityName(val)));
        return this;
    }

    @Override
    public Collection<String> getSynchronizedQuerySpaces() {
        return this.querySpaces;
    }

    @Override
    public NativeQueryImplementor<T> addSynchronizedQuerySpace(String querySpace) {
        this.addQuerySpaces(querySpace);
        return this;
    }

    @Override
    public SynchronizeableQuery<T> addSynchronizedQuerySpace(String ... querySpaces) {
        this.addQuerySpaces(querySpaces);
        return this;
    }

    protected void addQuerySpaces(String ... spaces) {
        if (spaces != null) {
            if (this.querySpaces == null) {
                this.querySpaces = new ArrayList<String>();
            }
            this.querySpaces.addAll(Arrays.asList(spaces));
        }
    }

    protected void addQuerySpaces(Serializable ... spaces) {
        if (spaces != null) {
            if (this.querySpaces == null) {
                this.querySpaces = new ArrayList<String>();
            }
            this.querySpaces.addAll(Arrays.asList((String[])spaces));
        }
    }

    @Override
    public NativeQueryImplementor<T> addSynchronizedEntityName(String entityName) throws MappingException {
        this.addQuerySpaces(this.getProducer().getFactory().getMetamodel().entityPersister(entityName).getQuerySpaces());
        return this;
    }

    @Override
    public NativeQueryImplementor<T> addSynchronizedEntityClass(Class entityClass) throws MappingException {
        this.addQuerySpaces(this.getProducer().getFactory().getMetamodel().entityPersister(entityClass.getName()).getQuerySpaces());
        return this;
    }

    @Override
    protected boolean applyQuerySpaces(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String[]) {
            this.addSynchronizedQuerySpace((String[])value);
            return true;
        }
        if (value instanceof Collection) {
            if (this.querySpaces == null) {
                this.querySpaces = new ArrayList<String>();
            }
            this.querySpaces.addAll((Collection)value);
            return true;
        }
        if (value instanceof String) {
            StringTokenizer spaces = new StringTokenizer((String)value, ",");
            while (spaces.hasMoreTokens()) {
                this.addQuerySpaces(spaces.nextToken());
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean isNativeQuery() {
        return true;
    }

    @Override
    public NativeQueryImplementor<T> setHibernateFlushMode(FlushMode flushMode) {
        super.setHibernateFlushMode(flushMode);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setFlushMode(FlushMode flushMode) {
        super.setFlushMode(flushMode);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setFlushMode(FlushModeType flushModeType) {
        super.setFlushMode(flushModeType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setCacheMode(CacheMode cacheMode) {
        super.setCacheMode(cacheMode);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setCacheable(boolean cacheable) {
        super.setCacheable(cacheable);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setCacheRegion(String cacheRegion) {
        super.setCacheRegion(cacheRegion);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setTimeout(int timeout) {
        super.setTimeout(timeout);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setFetchSize(int fetchSize) {
        super.setFetchSize(fetchSize);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setLockOptions(LockOptions lockOptions) {
        super.setLockOptions(lockOptions);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setLockMode(String alias, LockMode lockMode) {
        super.setLockMode(alias, lockMode);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setLockMode(LockModeType lockModeType) {
        throw new IllegalStateException("Illegal attempt to set lock mode on a native SQL query");
    }

    @Override
    public NativeQueryImplementor<T> setComment(String comment) {
        super.setComment(comment);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> addQueryHint(String hint) {
        super.addQueryHint(hint);
        return this;
    }

    @Override
    protected void collectHints(Map<String, Object> hints) {
        super.collectHints(hints);
        this.putIfNotNull(hints, "org.hibernate.lockMode", this.getLockOptions().getLockMode());
    }

    @Override
    protected boolean applyNativeQueryLockMode(Object value) {
        if (LockMode.class.isInstance(value)) {
            this.applyHibernateLockModeHint((LockMode)((Object)value));
        } else if (LockModeType.class.isInstance(value)) {
            this.applyLockModeTypeHint((LockModeType)value);
        } else if (String.class.isInstance(value)) {
            this.applyHibernateLockModeHint(LockModeTypeHelper.interpretLockMode(value));
        } else {
            throw new IllegalArgumentException(String.format("Native lock-mode hint [%s] must specify %s or %s.  Encountered type : %s", "org.hibernate.lockMode", LockMode.class.getName(), LockModeType.class.getName(), value.getClass().getName()));
        }
        return true;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(QueryParameter parameter, Object value) {
        super.setParameter((Parameter)parameter, value);
        return this;
    }

    @Override
    public <P> NativeQueryImplementor<T> setParameter(Parameter<P> parameter, P value) {
        super.setParameter((Parameter)parameter, (Object)value);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, Object value) {
        super.setParameter(name, value);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, Object value) {
        super.setParameter(position, value);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(QueryParameter parameter, Object value, Type type) {
        super.setParameter(parameter, value, type);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, Object value, Type type) {
        super.setParameter(name, value, type);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, Object value, Type type) {
        super.setParameter(position, value, type);
        return this;
    }

    @Override
    public <P> NativeQueryImplementor<T> setParameter(QueryParameter<P> parameter, P value, TemporalType temporalType) {
        super.setParameter((QueryParameter)parameter, (Object)value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, Object value, TemporalType temporalType) {
        super.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, Object value, TemporalType temporalType) {
        super.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType) {
        super.setParameter((Parameter)param, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType) {
        super.setParameter((Parameter)param, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType) {
        super.setParameter((Parameter)param, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType) {
        super.setParameter((Parameter)param, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, Instant value, TemporalType temporalType) {
        super.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, LocalDateTime value, TemporalType temporalType) {
        super.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
        super.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
        super.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, Instant value, TemporalType temporalType) {
        super.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, LocalDateTime value, TemporalType temporalType) {
        super.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
        super.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
        super.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameterList(QueryParameter parameter, Collection values) {
        super.setParameterList(parameter, values);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameterList(String name, Collection values) {
        super.setParameterList(name, values);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameterList(String name, Collection values, Type type) {
        super.setParameterList(name, values, type);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameterList(String name, Object[] values, Type type) {
        super.setParameterList(name, values, type);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameterList(String name, Object[] values) {
        super.setParameterList(name, values);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter param, Calendar value, TemporalType temporalType) {
        super.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter param, Date value, TemporalType temporalType) {
        super.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, Calendar value, TemporalType temporalType) {
        super.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(String name, Date value, TemporalType temporalType) {
        super.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, Calendar value, TemporalType temporalType) {
        super.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setParameter(int position, Date value, TemporalType temporalType) {
        super.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setResultTransformer(ResultTransformer transformer) {
        super.setResultTransformer(transformer);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setProperties(Map map) {
        super.setProperties(map);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setProperties(Object bean) {
        super.setProperties(bean);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setMaxResults(int maxResult) {
        super.setMaxResults(maxResult);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setFirstResult(int startPosition) {
        super.setFirstResult(startPosition);
        return this;
    }

    @Override
    public NativeQueryImplementor<T> setHint(String hintName, Object value) {
        super.setHint(hintName, value);
        return this;
    }

    @Override
    public Query<T> applyGraph(RootGraph graph, GraphSemantic semantic) {
        throw new HibernateException("A native SQL query cannot use EntityGraphs");
    }

    @Override
    protected void applyEntityGraphQueryHint(EntityGraphQueryHint hint) {
        throw new HibernateException("A native SQL query cannot use EntityGraphs");
    }
}

