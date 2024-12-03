/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.QueryException;
import org.hibernate.ScrollMode;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.FilterImpl;
import org.hibernate.internal.util.EntityPrinter;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public final class QueryParameters {
    private static final Logger LOG = CoreLogging.logger(QueryParameters.class);
    private static final String SYMBOLS = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\".replace("'", "");
    private Type[] positionalParameterTypes;
    private Object[] positionalParameterValues;
    private Map<String, TypedValue> namedParameters;
    private LockOptions lockOptions;
    private RowSelection rowSelection;
    private boolean cacheable;
    private String cacheRegion;
    private String comment;
    private List<String> queryHints;
    private ScrollMode scrollMode;
    private Serializable[] collectionKeys;
    private Object optionalObject;
    private String optionalEntityName;
    private Serializable optionalId;
    private boolean isReadOnlyInitialized;
    private boolean readOnly;
    private boolean callable;
    private boolean autodiscovertypes;
    private boolean isNaturalKeyLookup;
    private boolean passDistinctThrough = true;
    private final ResultTransformer resultTransformer;
    private String processedSQL;
    private Type[] processedPositionalParameterTypes;
    private Object[] processedPositionalParameterValues;
    private HQLQueryPlan queryPlan;

    public QueryParameters() {
        this(ArrayHelper.EMPTY_TYPE_ARRAY, ArrayHelper.EMPTY_OBJECT_ARRAY);
    }

    public QueryParameters(Type type, Object value) {
        this(new Type[]{type}, new Object[]{value});
    }

    public QueryParameters(Type[] positionalParameterTypes, Object[] positionalParameterValues, Object optionalObject, String optionalEntityName, Serializable optionalObjectId) {
        this(positionalParameterTypes, positionalParameterValues);
        this.optionalObject = optionalObject;
        this.optionalId = optionalObjectId;
        this.optionalEntityName = optionalEntityName;
    }

    public QueryParameters(Type[] positionalParameterTypes, Object[] positionalParameterValues) {
        this(positionalParameterTypes, positionalParameterValues, null, null, false, false, false, null, null, null, false, null);
    }

    public QueryParameters(Type[] positionalParameterTypes, Object[] positionalParameterValues, Serializable[] collectionKeys) {
        this(positionalParameterTypes, positionalParameterValues, null, collectionKeys);
    }

    public QueryParameters(Type[] positionalParameterTypes, Object[] positionalParameterValues, Map<String, TypedValue> namedParameters, Serializable[] collectionKeys) {
        this(positionalParameterTypes, positionalParameterValues, namedParameters, null, null, false, false, false, null, null, null, collectionKeys, null);
    }

    public QueryParameters(Type[] positionalParameterTypes, Object[] positionalParameterValues, LockOptions lockOptions, RowSelection rowSelection, boolean isReadOnlyInitialized, boolean readOnly, boolean cacheable, String cacheRegion, String comment, List<String> queryHints, boolean isLookupByNaturalKey, ResultTransformer transformer) {
        this(positionalParameterTypes, positionalParameterValues, null, lockOptions, rowSelection, isReadOnlyInitialized, readOnly, cacheable, cacheRegion, comment, queryHints, null, transformer);
        this.isNaturalKeyLookup = isLookupByNaturalKey;
    }

    public QueryParameters(Type[] positionalParameterTypes, Object[] positionalParameterValues, Map<String, TypedValue> namedParameters, LockOptions lockOptions, RowSelection rowSelection, boolean isReadOnlyInitialized, boolean readOnly, boolean cacheable, String cacheRegion, String comment, List<String> queryHints, Serializable[] collectionKeys, ResultTransformer transformer) {
        this.positionalParameterTypes = positionalParameterTypes;
        this.positionalParameterValues = positionalParameterValues;
        this.namedParameters = namedParameters;
        this.lockOptions = lockOptions;
        this.rowSelection = rowSelection;
        this.cacheable = cacheable;
        this.cacheRegion = cacheRegion;
        this.comment = comment;
        this.queryHints = queryHints;
        this.collectionKeys = collectionKeys;
        this.isReadOnlyInitialized = isReadOnlyInitialized;
        this.readOnly = readOnly;
        this.resultTransformer = transformer;
    }

    public QueryParameters(Type[] positionalParameterTypes, Object[] positionalParameterValues, Map<String, TypedValue> namedParameters, LockOptions lockOptions, RowSelection rowSelection, boolean isReadOnlyInitialized, boolean readOnly, boolean cacheable, String cacheRegion, String comment, List<String> queryHints, Serializable[] collectionKeys, Object optionalObject, String optionalEntityName, Serializable optionalId, ResultTransformer transformer) {
        this(positionalParameterTypes, positionalParameterValues, namedParameters, lockOptions, rowSelection, isReadOnlyInitialized, readOnly, cacheable, cacheRegion, comment, queryHints, collectionKeys, transformer);
        this.optionalEntityName = optionalEntityName;
        this.optionalId = optionalId;
        this.optionalObject = optionalObject;
    }

    public QueryParameters(QueryParameterBindings queryParameterBindings, LockOptions lockOptions, RowSelection selection, boolean isReadOnlyInitialized, boolean readOnly, boolean cacheable, String cacheRegion, String comment, List<String> dbHints, Serializable[] collectionKeys, Object optionalObject, String optionalEntityName, Serializable optionalId, ResultTransformer resultTransformer) {
        this(queryParameterBindings.collectPositionalBindTypes(), queryParameterBindings.collectPositionalBindValues(), queryParameterBindings.collectNamedParameterBindings(), lockOptions, selection, isReadOnlyInitialized, readOnly, cacheable, cacheRegion, comment, dbHints, collectionKeys, optionalObject, optionalEntityName, optionalId, resultTransformer);
    }

    public boolean hasRowSelection() {
        return this.rowSelection != null;
    }

    public Map<String, TypedValue> getNamedParameters() {
        return this.namedParameters;
    }

    public Type[] getPositionalParameterTypes() {
        return this.positionalParameterTypes;
    }

    public Object[] getPositionalParameterValues() {
        return this.positionalParameterValues;
    }

    public RowSelection getRowSelection() {
        return this.rowSelection;
    }

    public ResultTransformer getResultTransformer() {
        return this.resultTransformer;
    }

    public void setNamedParameters(Map<String, TypedValue> map) {
        this.namedParameters = map;
    }

    public void setPositionalParameterTypes(Type[] types) {
        this.positionalParameterTypes = types;
    }

    public void setPositionalParameterValues(Object[] objects) {
        this.positionalParameterValues = objects;
    }

    public void setRowSelection(RowSelection selection) {
        this.rowSelection = selection;
    }

    public LockOptions getLockOptions() {
        return this.lockOptions;
    }

    public void setLockOptions(LockOptions lockOptions) {
        this.lockOptions = lockOptions;
    }

    public void traceParameters(SessionFactoryImplementor factory) throws HibernateException {
        EntityPrinter print = new EntityPrinter(factory);
        if (this.positionalParameterValues.length != 0) {
            LOG.tracev("Parameters: {0}", (Object)print.toString(this.positionalParameterTypes, this.positionalParameterValues));
        }
        if (this.namedParameters != null) {
            LOG.tracev("Named parameters: {0}", (Object)print.toString(this.namedParameters));
        }
    }

    public boolean isCacheable() {
        return this.cacheable;
    }

    public void setCacheable(boolean b) {
        this.cacheable = b;
    }

    public String getCacheRegion() {
        return this.cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public void validateParameters() throws QueryException {
        int values;
        int types = this.positionalParameterTypes == null ? 0 : this.positionalParameterTypes.length;
        int n = values = this.positionalParameterValues == null ? 0 : this.positionalParameterValues.length;
        if (types != values) {
            throw new QueryException("Number of positional parameter types:" + types + " does not match number of positional parameters: " + values);
        }
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getQueryHints() {
        return this.queryHints;
    }

    public void setQueryHints(List<String> queryHints) {
        this.queryHints = queryHints;
    }

    public ScrollMode getScrollMode() {
        return this.scrollMode;
    }

    public void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }

    public Serializable[] getCollectionKeys() {
        return this.collectionKeys;
    }

    public void setCollectionKeys(Serializable[] collectionKeys) {
        this.collectionKeys = collectionKeys;
    }

    public String getOptionalEntityName() {
        return this.optionalEntityName;
    }

    public void setOptionalEntityName(String optionalEntityName) {
        this.optionalEntityName = optionalEntityName;
    }

    public Serializable getOptionalId() {
        return this.optionalId;
    }

    public void setOptionalId(Serializable optionalId) {
        this.optionalId = optionalId;
    }

    public Object getOptionalObject() {
        return this.optionalObject;
    }

    public void setOptionalObject(Object optionalObject) {
        this.optionalObject = optionalObject;
    }

    public boolean isReadOnlyInitialized() {
        return this.isReadOnlyInitialized;
    }

    public boolean isReadOnly() {
        if (!this.isReadOnlyInitialized()) {
            throw new IllegalStateException("cannot call isReadOnly() when isReadOnlyInitialized() returns false");
        }
        return this.readOnly;
    }

    public boolean isReadOnly(SharedSessionContractImplementor session) {
        return this.isReadOnlyInitialized ? this.isReadOnly() : session.getPersistenceContextInternal().isDefaultReadOnly();
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        this.isReadOnlyInitialized = true;
    }

    public void setCallable(boolean callable) {
        this.callable = callable;
    }

    public boolean isCallable() {
        return this.callable;
    }

    public boolean hasAutoDiscoverScalarTypes() {
        return this.autodiscovertypes;
    }

    public boolean isPassDistinctThrough() {
        return this.passDistinctThrough;
    }

    public void setPassDistinctThrough(boolean passDistinctThrough) {
        this.passDistinctThrough = passDistinctThrough;
    }

    public void processFilters(String sql, SharedSessionContractImplementor session) {
        this.processFilters(sql, session.getLoadQueryInfluencers().getEnabledFilters(), session.getFactory());
    }

    public void processFilters(String sql, Map filters, SessionFactoryImplementor factory) {
        if (filters.size() == 0 || !sql.contains(":")) {
            this.processedPositionalParameterValues = this.getPositionalParameterValues();
            this.processedPositionalParameterTypes = this.getPositionalParameterTypes();
            this.processedSQL = sql;
        } else {
            StringTokenizer tokens = new StringTokenizer(sql, SYMBOLS, true);
            StringBuilder result = new StringBuilder();
            ArrayList<Object> parameters = new ArrayList<Object>();
            ArrayList<Type> parameterTypes = new ArrayList<Type>();
            int positionalIndex = 0;
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (token.startsWith(":")) {
                    String filterParameterName = token.substring(1);
                    String[] parts = LoadQueryInfluencers.parseFilterParameterName(filterParameterName);
                    FilterImpl filter = (FilterImpl)filters.get(parts[0]);
                    Object value = filter.getParameter(parts[1]);
                    Type type = filter.getFilterDefinition().getParameterType(parts[1]);
                    if (value != null && Collection.class.isAssignableFrom(value.getClass())) {
                        Iterator itr = ((Collection)value).iterator();
                        while (itr.hasNext()) {
                            Object elementValue = itr.next();
                            result.append('?');
                            parameters.add(elementValue);
                            parameterTypes.add(type);
                            if (!itr.hasNext()) continue;
                            result.append(", ");
                        }
                        continue;
                    }
                    result.append('?');
                    parameters.add(value);
                    parameterTypes.add(type);
                    continue;
                }
                result.append(token);
                if (!"?".equals(token) || positionalIndex >= this.getPositionalParameterValues().length) continue;
                Type type = this.getPositionalParameterTypes()[positionalIndex];
                if (type.isComponentType()) {
                    int paramIndex = 1;
                    int numberOfParametersCoveredBy = this.getNumberOfParametersCoveredBy(((ComponentType)type).getSubtypes());
                    while (paramIndex < numberOfParametersCoveredBy) {
                        String nextToken = tokens.nextToken();
                        if ("?".equals(nextToken)) {
                            ++paramIndex;
                        }
                        result.append(nextToken);
                    }
                }
                parameters.add(this.getPositionalParameterValues()[positionalIndex]);
                parameterTypes.add(type);
                ++positionalIndex;
            }
            this.processedPositionalParameterValues = parameters.toArray();
            this.processedPositionalParameterTypes = parameterTypes.toArray(new Type[parameterTypes.size()]);
            this.processedSQL = result.toString();
        }
    }

    private int getNumberOfParametersCoveredBy(Type[] subtypes) {
        int numberOfParameters = 0;
        for (Type type : subtypes) {
            if (type.isComponentType()) {
                numberOfParameters += this.getNumberOfParametersCoveredBy(((ComponentType)type).getSubtypes());
                continue;
            }
            ++numberOfParameters;
        }
        return numberOfParameters;
    }

    public String getFilteredSQL() {
        return this.processedSQL;
    }

    public Object[] getFilteredPositionalParameterValues() {
        return this.processedPositionalParameterValues;
    }

    public Type[] getFilteredPositionalParameterTypes() {
        return this.processedPositionalParameterTypes;
    }

    public boolean isNaturalKeyLookup() {
        return this.isNaturalKeyLookup;
    }

    public void setNaturalKeyLookup(boolean isNaturalKeyLookup) {
        this.isNaturalKeyLookup = isNaturalKeyLookup;
    }

    public void setAutoDiscoverScalarTypes(boolean autodiscovertypes) {
        this.autodiscovertypes = autodiscovertypes;
    }

    public QueryParameters createCopyUsing(RowSelection selection) {
        QueryParameters copy = new QueryParameters(this.positionalParameterTypes, this.positionalParameterValues, this.namedParameters, this.lockOptions, selection, this.isReadOnlyInitialized, this.readOnly, this.cacheable, this.cacheRegion, this.comment, this.queryHints, this.collectionKeys, this.optionalObject, this.optionalEntityName, this.optionalId, this.resultTransformer);
        copy.processedSQL = this.processedSQL;
        copy.processedPositionalParameterTypes = this.processedPositionalParameterTypes;
        copy.processedPositionalParameterValues = this.processedPositionalParameterValues;
        copy.passDistinctThrough = this.passDistinctThrough;
        return copy;
    }

    public HQLQueryPlan getQueryPlan() {
        return this.queryPlan;
    }

    public void setQueryPlan(HQLQueryPlan queryPlan) {
        this.queryPlan = queryPlan;
    }

    public void bindDynamicParameter(Type paramType, Object paramValue) {
        if (this.processedPositionalParameterTypes != null) {
            int length = this.processedPositionalParameterTypes.length;
            Type[] types = new Type[length + 1];
            Object[] values = new Object[length + 1];
            for (int i = 0; i < length; ++i) {
                types[i] = this.processedPositionalParameterTypes[i];
                values[i] = this.processedPositionalParameterValues[i];
            }
            types[length] = paramType;
            values[length] = paramValue;
            this.processedPositionalParameterTypes = types;
            this.processedPositionalParameterValues = values;
        }
    }
}

