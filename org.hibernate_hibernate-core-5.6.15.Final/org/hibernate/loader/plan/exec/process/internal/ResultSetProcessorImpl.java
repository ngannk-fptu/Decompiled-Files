/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.exec.process.internal;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
import org.hibernate.loader.plan.exec.process.spi.RowReader;
import org.hibernate.loader.plan.exec.process.spi.ScrollableResultSetProcessor;
import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;

public class ResultSetProcessorImpl
implements ResultSetProcessor {
    private static final Logger LOG = Logger.getLogger(ResultSetProcessorImpl.class);
    private final LoadPlan loadPlan;
    private final AliasResolutionContext aliasResolutionContext;
    private final RowReader rowReader;
    private final boolean hadSubselectFetches;
    private final boolean shouldUseOptionalEntityInstance;

    public ResultSetProcessorImpl(LoadPlan loadPlan, AliasResolutionContext aliasResolutionContext, RowReader rowReader, boolean shouldUseOptionalEntityInstance, boolean hadSubselectFetches) {
        this.loadPlan = loadPlan;
        this.aliasResolutionContext = aliasResolutionContext;
        this.rowReader = rowReader;
        this.shouldUseOptionalEntityInstance = shouldUseOptionalEntityInstance;
        this.hadSubselectFetches = hadSubselectFetches;
    }

    @Override
    public ScrollableResultSetProcessor toOnDemandForm() {
        throw new NotYetImplementedException();
    }

    @Override
    public List extractResults(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, NamedParameterContext namedParameterContext, boolean returnProxies, boolean readOnly, ResultTransformer forcedResultTransformer, List<AfterLoadAction> afterLoadActionList) throws SQLException {
        this.handlePotentiallyEmptyCollectionRootReturns(queryParameters.getCollectionKeys(), resultSet, session);
        ResultSetProcessingContextImpl context = this.createResultSetProcessingContext(resultSet, session, queryParameters, namedParameterContext, returnProxies, readOnly);
        List<Object> loadResults = this.extractRows(resultSet, queryParameters, context);
        this.rowReader.finishUp(context, afterLoadActionList);
        context.wrapUp();
        session.getPersistenceContextInternal().initializeNonLazyCollections();
        return loadResults;
    }

    protected ResultSetProcessingContextImpl createResultSetProcessingContext(ResultSet resultSet, SharedSessionContractImplementor session, QueryParameters queryParameters, NamedParameterContext namedParameterContext, boolean returnProxies, boolean readOnly) {
        return new ResultSetProcessingContextImpl(resultSet, session, this.loadPlan, this.aliasResolutionContext, readOnly, this.shouldUseOptionalEntityInstance, returnProxies, queryParameters, namedParameterContext, this.hadSubselectFetches);
    }

    protected List<Object> extractRows(ResultSet resultSet, QueryParameters queryParameters, ResultSetProcessingContextImpl context) throws SQLException {
        int count;
        ArrayList<Object> loadResults;
        int maxRows;
        boolean traceEnabled = LOG.isTraceEnabled();
        RowSelection selection = queryParameters.getRowSelection();
        if (LimitHelper.hasMaxRows(selection)) {
            maxRows = selection.getMaxRows();
            if (traceEnabled) {
                LOG.tracef("Limiting ResultSet processing to just %s rows", maxRows);
            }
            int sizeHint = maxRows < 50 ? maxRows : 50;
            loadResults = new ArrayList(sizeHint);
        } else {
            loadResults = new ArrayList<Object>();
            maxRows = Integer.MAX_VALUE;
        }
        if (traceEnabled) {
            LOG.trace((Object)"Processing result set");
        }
        for (count = 0; count < maxRows && resultSet.next(); ++count) {
            if (traceEnabled) {
                LOG.tracef("Starting ResultSet row #%s", count);
            }
            Object logicalRow = this.rowReader.readRow(resultSet, context);
            loadResults.add(logicalRow);
            context.finishUpRow();
        }
        if (traceEnabled) {
            LOG.tracev("Done processing result set ({0} rows)", (Object)count);
        }
        return loadResults;
    }

    protected void handlePotentiallyEmptyCollectionRootReturns(Serializable[] collectionKeys, ResultSet resultSet, SharedSessionContractImplementor session) {
        if (collectionKeys == null) {
            return;
        }
        CollectionPersister persister = ((CollectionReturn)this.loadPlan.getReturns().get(0)).getCollectionPersister();
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        boolean debugEnabled = LOG.isDebugEnabled();
        for (Serializable key : collectionKeys) {
            if (debugEnabled) {
                LOG.debugf("Preparing collection initializer : %s", (Object)MessageHelper.collectionInfoString(persister, key, session.getFactory()));
            }
            persistenceContext.getLoadContexts().getCollectionLoadContext(resultSet).getLoadingCollection(persister, key);
        }
    }
}

