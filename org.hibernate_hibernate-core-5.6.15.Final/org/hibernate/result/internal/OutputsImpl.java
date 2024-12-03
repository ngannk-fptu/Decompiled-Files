/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.result.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.hibernate.JDBCException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.custom.CustomLoader;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.loader.custom.Return;
import org.hibernate.loader.custom.RootReturn;
import org.hibernate.loader.custom.sql.SQLQueryReturnProcessor;
import org.hibernate.param.ParameterBinder;
import org.hibernate.result.NoMoreReturnsException;
import org.hibernate.result.Output;
import org.hibernate.result.Outputs;
import org.hibernate.result.internal.ResultSetOutputImpl;
import org.hibernate.result.internal.UpdateCountOutputImpl;
import org.hibernate.result.spi.ResultContext;
import org.jboss.logging.Logger;

public class OutputsImpl
implements Outputs {
    private static final Logger log = CoreLogging.logger(OutputsImpl.class);
    private final ResultContext context;
    private final PreparedStatement jdbcStatement;
    private final CustomLoaderExtension loader;
    private CurrentReturnState currentReturnState;

    public OutputsImpl(ResultContext context, PreparedStatement jdbcStatement) {
        this.context = context;
        this.jdbcStatement = jdbcStatement;
        this.loader = OutputsImpl.buildSpecializedCustomLoader(context);
        try {
            boolean isResultSet = jdbcStatement.execute();
            this.currentReturnState = this.buildCurrentReturnState(isResultSet);
        }
        catch (SQLException e) {
            throw this.convert(e, "Error calling CallableStatement.getMoreResults");
        }
    }

    private CurrentReturnState buildCurrentReturnState(boolean isResultSet) {
        int updateCount = -1;
        if (!isResultSet) {
            try {
                updateCount = this.jdbcStatement.getUpdateCount();
            }
            catch (SQLException e) {
                throw this.convert(e, "Error calling CallableStatement.getUpdateCount");
            }
        }
        return this.buildCurrentReturnState(isResultSet, updateCount);
    }

    protected CurrentReturnState buildCurrentReturnState(boolean isResultSet, int updateCount) {
        return new CurrentReturnState(isResultSet, updateCount);
    }

    protected JDBCException convert(SQLException e, String message) {
        return this.context.getSession().getJdbcServices().getSqlExceptionHelper().convert(e, message, this.context.getSql());
    }

    @Override
    public Output getCurrent() {
        if (this.currentReturnState == null) {
            return null;
        }
        return this.currentReturnState.getOutput();
    }

    @Override
    public boolean goToNext() {
        if (this.currentReturnState == null) {
            return false;
        }
        if (this.currentReturnState.indicatesMoreOutputs()) {
            try {
                boolean isResultSet = this.jdbcStatement.getMoreResults();
                this.currentReturnState = this.buildCurrentReturnState(isResultSet);
            }
            catch (SQLException e) {
                throw this.convert(e, "Error calling CallableStatement.getMoreResults");
            }
        }
        return this.currentReturnState != null && this.currentReturnState.indicatesMoreOutputs();
    }

    @Override
    public void release() {
        try {
            this.jdbcStatement.close();
        }
        catch (SQLException e) {
            log.debug((Object)"Unable to close PreparedStatement", (Throwable)e);
        }
    }

    private List extractCurrentResults() {
        try {
            return this.extractResults(this.jdbcStatement.getResultSet());
        }
        catch (SQLException e) {
            throw this.convert(e, "Error calling CallableStatement.getResultSet");
        }
    }

    protected List extractResults(ResultSet resultSet) {
        try {
            return this.loader.processResultSet(resultSet);
        }
        catch (SQLException e) {
            throw this.convert(e, "Error extracting results from CallableStatement");
        }
    }

    private static CustomLoaderExtension buildSpecializedCustomLoader(final ResultContext context) {
        SQLQueryReturnProcessor processor = new SQLQueryReturnProcessor(context.getQueryReturns(), context.getSession().getFactory());
        processor.process();
        final List<Return> customReturns = processor.generateCallableReturns();
        CustomQuery customQuery = new CustomQuery(){

            @Override
            public String getSQL() {
                return context.getSql();
            }

            @Override
            public Set<String> getQuerySpaces() {
                return context.getSynchronizedQuerySpaces();
            }

            @Override
            public List<ParameterBinder> getParameterValueBinders() {
                return Collections.emptyList();
            }

            @Override
            public List<Return> getCustomQueryReturns() {
                return customReturns;
            }
        };
        return new CustomLoaderExtension(customQuery, context.getQueryParameters(), context.getSession());
    }

    private static class CustomLoaderExtension
    extends CustomLoader {
        private static final EntityAliases[] NO_ALIASES = new EntityAliases[0];
        private final QueryParameters queryParameters;
        private final SharedSessionContractImplementor session;
        private final EntityAliases[] entityAliases;
        private boolean needsDiscovery = true;

        public CustomLoaderExtension(CustomQuery customQuery, QueryParameters queryParameters, SharedSessionContractImplementor session) {
            super(customQuery, session.getFactory());
            this.queryParameters = queryParameters;
            this.session = session;
            this.entityAliases = this.interpretEntityAliases(customQuery.getCustomQueryReturns());
        }

        private EntityAliases[] interpretEntityAliases(List<Return> customQueryReturns) {
            ArrayList<EntityAliases> entityAliases = new ArrayList<EntityAliases>();
            for (Return queryReturn : customQueryReturns) {
                if (!RootReturn.class.isInstance(queryReturn)) continue;
                entityAliases.add(((RootReturn)queryReturn).getEntityAliases());
            }
            if (entityAliases.isEmpty()) {
                return NO_ALIASES;
            }
            return entityAliases.toArray(new EntityAliases[entityAliases.size()]);
        }

        @Override
        protected EntityAliases[] getEntityAliases() {
            return this.entityAliases;
        }

        public List processResultSet(ResultSet resultSet) throws SQLException {
            if (this.needsDiscovery) {
                super.autoDiscoverTypes(resultSet);
                this.needsDiscovery = false;
            }
            return super.processResultSet(resultSet, this.queryParameters, this.session, true, null, Integer.MAX_VALUE, Collections.emptyList());
        }
    }

    protected class CurrentReturnState {
        private final boolean isResultSet;
        private final int updateCount;
        private Output rtn;

        protected CurrentReturnState(boolean isResultSet, int updateCount) {
            this.isResultSet = isResultSet;
            this.updateCount = updateCount;
        }

        public boolean indicatesMoreOutputs() {
            return this.isResultSet() || this.getUpdateCount() >= 0;
        }

        public boolean isResultSet() {
            return this.isResultSet;
        }

        public int getUpdateCount() {
            return this.updateCount;
        }

        public Output getOutput() {
            if (this.rtn == null) {
                this.rtn = this.buildOutput();
            }
            return this.rtn;
        }

        protected Output buildOutput() {
            if (log.isDebugEnabled()) {
                log.debugf("Building Return [isResultSet=%s, updateCount=%s, extendedReturn=%s", (Object)this.isResultSet(), (Object)this.getUpdateCount(), (Object)this.hasExtendedReturns());
            }
            if (this.isResultSet()) {
                return this.buildResultSetOutput(OutputsImpl.this.extractCurrentResults());
            }
            if (this.getUpdateCount() >= 0) {
                return this.buildUpdateCountOutput(this.updateCount);
            }
            if (this.hasExtendedReturns()) {
                return this.buildExtendedReturn();
            }
            throw new NoMoreReturnsException();
        }

        protected Output buildResultSetOutput(List list) {
            return new ResultSetOutputImpl(list);
        }

        protected Output buildResultSetOutput(Supplier<List> listSupplier) {
            return new ResultSetOutputImpl(listSupplier);
        }

        protected Output buildUpdateCountOutput(int updateCount) {
            return new UpdateCountOutputImpl(updateCount);
        }

        protected boolean hasExtendedReturns() {
            return false;
        }

        protected Output buildExtendedReturn() {
            throw new IllegalStateException("State does not define extended returns");
        }
    }
}

