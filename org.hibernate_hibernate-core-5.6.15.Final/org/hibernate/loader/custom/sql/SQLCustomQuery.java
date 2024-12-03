/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.custom.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.loader.custom.sql.SQLQueryParser;
import org.hibernate.loader.custom.sql.SQLQueryReturnProcessor;
import org.hibernate.param.ParameterBinder;
import org.hibernate.persister.collection.SQLLoadableCollection;
import org.hibernate.persister.entity.SQLLoadable;
import org.jboss.logging.Logger;

public class SQLCustomQuery
implements CustomQuery,
Serializable {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SQLCustomQuery.class.getName());
    private final String sql;
    private final Set querySpaces = new HashSet();
    private final List<ParameterBinder> paramValueBinders;
    private final List customQueryReturns = new ArrayList();

    @Override
    public String getSQL() {
        return this.sql;
    }

    public Set getQuerySpaces() {
        return this.querySpaces;
    }

    @Override
    public List<ParameterBinder> getParameterValueBinders() {
        return this.paramValueBinders;
    }

    public List getCustomQueryReturns() {
        return this.customQueryReturns;
    }

    public SQLCustomQuery(String sqlQuery, NativeSQLQueryReturn[] queryReturns, Collection additionalQuerySpaces, SessionFactoryImplementor factory) throws HibernateException {
        LOG.tracev("Starting processing of sql query [{0}]", sqlQuery);
        SQLQueryReturnProcessor processor = new SQLQueryReturnProcessor(queryReturns, factory);
        SQLQueryReturnProcessor.ResultAliasContext aliasContext = processor.process();
        SQLQueryParser parser = new SQLQueryParser(sqlQuery, new ParserContext(aliasContext), factory);
        this.sql = parser.process();
        this.paramValueBinders = parser.getParameterValueBinders();
        this.customQueryReturns.addAll(processor.generateCustomReturns(parser.queryHasAliases()));
        if (additionalQuerySpaces != null) {
            this.querySpaces.addAll(additionalQuerySpaces);
        }
    }

    private static class ParserContext
    implements SQLQueryParser.ParserContext {
        private final SQLQueryReturnProcessor.ResultAliasContext aliasContext;

        public ParserContext(SQLQueryReturnProcessor.ResultAliasContext aliasContext) {
            this.aliasContext = aliasContext;
        }

        @Override
        public boolean isEntityAlias(String alias) {
            return this.getEntityPersisterByAlias(alias) != null;
        }

        @Override
        public SQLLoadable getEntityPersisterByAlias(String alias) {
            return this.aliasContext.getEntityPersister(alias);
        }

        @Override
        public String getEntitySuffixByAlias(String alias) {
            return this.aliasContext.getEntitySuffix(alias);
        }

        @Override
        public boolean isCollectionAlias(String alias) {
            return this.getCollectionPersisterByAlias(alias) != null;
        }

        @Override
        public SQLLoadableCollection getCollectionPersisterByAlias(String alias) {
            return this.aliasContext.getCollectionPersister(alias);
        }

        @Override
        public String getCollectionSuffixByAlias(String alias) {
            return this.aliasContext.getCollectionSuffix(alias);
        }

        @Override
        public Map getPropertyResultsMapByAlias(String alias) {
            return this.aliasContext.getPropertyResultsMap(alias);
        }
    }
}

