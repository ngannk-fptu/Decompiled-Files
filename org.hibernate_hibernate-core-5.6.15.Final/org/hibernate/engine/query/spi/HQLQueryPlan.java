/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.query.spi.NamedParameterDescriptor;
import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
import org.hibernate.engine.query.spi.ReturnMetadata;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.QuerySplitter;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.NamedParameterInformation;
import org.hibernate.hql.spi.ParameterTranslations;
import org.hibernate.hql.spi.PositionalParameterInformation;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.query.internal.ParameterMetadataImpl;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.type.Type;

public class HQLQueryPlan
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(HQLQueryPlan.class);
    private final String sourceQuery;
    private final QueryTranslator[] translators;
    private final ParameterMetadataImpl parameterMetadata;
    private final ReturnMetadata returnMetadata;
    private final Set querySpaces;
    private final Set<String> enabledFilterNames;
    private final boolean shallow;

    public HQLQueryPlan(String hql, boolean shallow, Map<String, Filter> enabledFilters, SessionFactoryImplementor factory) {
        this(hql, null, shallow, enabledFilters, factory, null);
    }

    public HQLQueryPlan(String hql, boolean shallow, Map<String, Filter> enabledFilters, SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
        this(hql, null, shallow, enabledFilters, factory, entityGraphQueryHint);
    }

    protected HQLQueryPlan(String hql, String collectionRole, boolean shallow, Map<String, Filter> enabledFilters, SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
        this.sourceQuery = hql;
        this.shallow = shallow;
        this.enabledFilterNames = enabledFilters.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<String>(enabledFilters.keySet()));
        String[] concreteQueryStrings = QuerySplitter.concreteQueries(hql, factory);
        int length = concreteQueryStrings.length;
        this.translators = new QueryTranslator[length];
        HashSet<Serializable> combinedQuerySpaces = new HashSet<Serializable>();
        Map querySubstitutions = factory.getSessionFactoryOptions().getQuerySubstitutions();
        QueryTranslatorFactory queryTranslatorFactory = factory.getFastSessionServices().queryTranslatorFactory;
        for (int i = 0; i < length; ++i) {
            if (collectionRole == null) {
                this.translators[i] = queryTranslatorFactory.createQueryTranslator(hql, concreteQueryStrings[i], enabledFilters, factory, entityGraphQueryHint);
                this.translators[i].compile(querySubstitutions, shallow);
            } else {
                this.translators[i] = queryTranslatorFactory.createFilterTranslator(hql, concreteQueryStrings[i], enabledFilters, factory);
                ((FilterTranslator)this.translators[i]).compile(collectionRole, querySubstitutions, shallow);
            }
            combinedQuerySpaces.addAll(this.translators[i].getQuerySpaces());
        }
        this.querySpaces = combinedQuerySpaces;
        if (length == 0) {
            this.parameterMetadata = new ParameterMetadataImpl(null, null);
            this.returnMetadata = null;
        } else {
            this.parameterMetadata = this.buildParameterMetadata(this.translators[0].getParameterTranslations(), hql);
            if (this.translators[0].isManipulationStatement()) {
                this.returnMetadata = null;
            } else {
                Type[] types = length > 1 ? new Type[this.translators[0].getReturnTypes().length] : this.translators[0].getReturnTypes();
                this.returnMetadata = new ReturnMetadata(this.translators[0].getReturnAliases(), types);
            }
        }
    }

    public String getSourceQuery() {
        return this.sourceQuery;
    }

    public Set getQuerySpaces() {
        return this.querySpaces;
    }

    public ParameterMetadataImpl getParameterMetadata() {
        return this.parameterMetadata;
    }

    public ReturnMetadata getReturnMetadata() {
        return this.returnMetadata;
    }

    public Set getEnabledFilterNames() {
        return this.enabledFilterNames;
    }

    public String[] getSqlStrings() {
        ArrayList<String> sqlStrings = new ArrayList<String>();
        for (QueryTranslator translator : this.translators) {
            sqlStrings.addAll(translator.collectSqlStrings());
        }
        return ArrayHelper.toStringArray(sqlStrings);
    }

    public Set getUtilizedFilterNames() {
        return null;
    }

    public boolean isShallow() {
        return this.shallow;
    }

    public List performList(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        QueryParameters queryParametersToUse;
        boolean needsLimit;
        RowSelection rowSelection;
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Find: {0}", this.getSourceQuery());
            queryParameters.traceParameters(session.getFactory());
        }
        boolean hasLimit = (rowSelection = queryParameters.getRowSelection()) != null && rowSelection.definesLimits();
        boolean bl = needsLimit = hasLimit && this.translators.length > 1;
        if (needsLimit) {
            LOG.needsLimit();
            RowSelection selection = new RowSelection();
            selection.setFetchSize(queryParameters.getRowSelection().getFetchSize());
            selection.setTimeout(queryParameters.getRowSelection().getTimeout());
            queryParametersToUse = queryParameters.createCopyUsing(selection);
        } else {
            queryParametersToUse = queryParameters;
        }
        if (this.translators.length == 1) {
            return this.translators[0].list(session, queryParametersToUse);
        }
        int guessedResultSize = this.guessResultSize(rowSelection);
        ArrayList combinedResults = new ArrayList(guessedResultSize);
        IdentitySet distinction = needsLimit ? new IdentitySet(guessedResultSize) : null;
        int includedCount = -1;
        block0: for (QueryTranslator translator : this.translators) {
            List tmp = translator.list(session, queryParametersToUse);
            if (needsLimit) {
                int first = queryParameters.getRowSelection().getFirstRow() == null ? 0 : queryParameters.getRowSelection().getFirstRow();
                int max = queryParameters.getRowSelection().getMaxRows() == null ? -1 : queryParameters.getRowSelection().getMaxRows();
                for (Object result : tmp) {
                    if (!distinction.add(result) || ++includedCount < first) continue;
                    combinedResults.add(result);
                    if (max < 0 || includedCount <= max) continue;
                    break block0;
                }
                continue;
            }
            combinedResults.addAll(tmp);
        }
        return combinedResults;
    }

    protected int guessResultSize(RowSelection rowSelection) {
        if (rowSelection != null) {
            int maxReasonableAllocation;
            int n = maxReasonableAllocation = rowSelection.getFetchSize() != null ? rowSelection.getFetchSize() : 100;
            if (rowSelection.getMaxRows() != null && rowSelection.getMaxRows() > 0) {
                return Math.min(maxReasonableAllocation, rowSelection.getMaxRows());
            }
            if (rowSelection.getFetchSize() != null && rowSelection.getFetchSize() > 0) {
                return rowSelection.getFetchSize();
            }
        }
        return 7;
    }

    public Iterator performIterate(QueryParameters queryParameters, EventSource session) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Iterate: {0}", this.getSourceQuery());
            queryParameters.traceParameters(session.getFactory());
        }
        if (this.translators.length == 0) {
            return Collections.emptyIterator();
        }
        boolean many = this.translators.length > 1;
        Iterator[] results = null;
        if (many) {
            results = new Iterator[this.translators.length];
        }
        Iterator result = null;
        for (int i = 0; i < this.translators.length; ++i) {
            result = this.translators[i].iterate(queryParameters, session);
            if (!many) continue;
            results[i] = result;
        }
        return many ? new JoinedIterator(results) : result;
    }

    public ScrollableResultsImplementor performScroll(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Iterate: {0}", this.getSourceQuery());
            queryParameters.traceParameters(session.getFactory());
        }
        if (this.translators.length != 1) {
            throw new QueryException("implicit polymorphism not supported for scroll() queries");
        }
        if (queryParameters.getRowSelection().definesLimits() && this.translators[0].containsCollectionFetches()) {
            throw new QueryException("firstResult/maxResults not supported in conjunction with scroll() of a query containing collection fetches");
        }
        return this.translators[0].scroll(queryParameters, session);
    }

    public int performExecuteUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Execute update: {0}", this.getSourceQuery());
            queryParameters.traceParameters(session.getFactory());
        }
        if (this.translators.length != 1) {
            LOG.splitQueries(this.getSourceQuery(), this.translators.length);
        }
        int result = 0;
        for (QueryTranslator translator : this.translators) {
            result += translator.executeUpdate(queryParameters, session);
        }
        return result;
    }

    private ParameterMetadataImpl buildParameterMetadata(ParameterTranslations parameterTranslations, String hql) {
        Map<String, NamedParameterDescriptor> namedParamDescriptorMap;
        Map<Integer, OrdinalParameterDescriptor> ordinalParamDescriptors;
        if (parameterTranslations.getPositionalParameterInformationMap().isEmpty()) {
            ordinalParamDescriptors = Collections.emptyMap();
        } else {
            HashMap<Integer, OrdinalParameterDescriptor> temp = new HashMap<Integer, OrdinalParameterDescriptor>();
            for (Map.Entry<Integer, PositionalParameterInformation> entry : parameterTranslations.getPositionalParameterInformationMap().entrySet()) {
                int position = entry.getKey();
                temp.put(position, new OrdinalParameterDescriptor(position, position - 1, entry.getValue().getExpectedType(), entry.getValue().getSourceLocations()));
            }
            ordinalParamDescriptors = Collections.unmodifiableMap(temp);
        }
        if (parameterTranslations.getNamedParameterInformationMap().isEmpty()) {
            namedParamDescriptorMap = Collections.emptyMap();
        } else {
            HashMap<String, NamedParameterDescriptor> tmp = new HashMap<String, NamedParameterDescriptor>();
            for (Map.Entry<String, NamedParameterInformation> namedEntry : parameterTranslations.getNamedParameterInformationMap().entrySet()) {
                String name = namedEntry.getKey();
                tmp.put(name, new NamedParameterDescriptor(name, parameterTranslations.getNamedParameterInformation(name).getExpectedType(), namedEntry.getValue().getSourceLocations()));
            }
            namedParamDescriptorMap = Collections.unmodifiableMap(tmp);
        }
        return new ParameterMetadataImpl(ordinalParamDescriptors, namedParamDescriptorMap);
    }

    public QueryTranslator[] getTranslators() {
        QueryTranslator[] copy = new QueryTranslator[this.translators.length];
        System.arraycopy(this.translators, 0, copy, 0, copy.length);
        return copy;
    }

    public Class getDynamicInstantiationResultType() {
        return this.translators[0].getDynamicInstantiationResultType();
    }

    public boolean isSelect() {
        return !this.translators[0].isManipulationStatement();
    }

    public boolean isUpdate() {
        return this.translators[0].isUpdateStatement();
    }
}

