/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ANTLRException
 *  antlr.RecognitionException
 *  antlr.TokenStreamException
 *  antlr.collections.AST
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.internal.ast;

import antlr.ANTLRException;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.QueryExecutionRequestException;
import org.hibernate.hql.internal.ast.HqlParser;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.ParameterTranslationsImpl;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.exec.DeleteExecutor;
import org.hibernate.hql.internal.ast.exec.IdSubselectUpdateExecutor;
import org.hibernate.hql.internal.ast.exec.InsertExecutor;
import org.hibernate.hql.internal.ast.exec.MultiTableDeleteExecutor;
import org.hibernate.hql.internal.ast.exec.MultiTableUpdateExecutor;
import org.hibernate.hql.internal.ast.exec.SimpleUpdateExecutor;
import org.hibernate.hql.internal.ast.exec.StatementExecutor;
import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.Statement;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.NodeTraverser;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.ParameterTranslations;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.loader.hql.QueryLoader;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class QueryTranslatorImpl
implements FilterTranslator {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)QueryTranslatorImpl.class.getName());
    private SessionFactoryImplementor factory;
    private final String queryIdentifier;
    private String hql;
    private boolean shallowQuery;
    private Map tokenReplacements;
    private Map enabledFilters;
    private boolean compiled;
    private QueryLoader queryLoader;
    private StatementExecutor statementExecutor;
    private Statement sqlAst;
    private String sql;
    private ParameterTranslations paramTranslations;
    private List<ParameterSpecification> collectedParameterSpecifications;
    private EntityGraphQueryHint entityGraphQueryHint;

    public QueryTranslatorImpl(String queryIdentifier, String query, Map enabledFilters, SessionFactoryImplementor factory) {
        this.queryIdentifier = queryIdentifier;
        this.hql = query;
        this.compiled = false;
        this.shallowQuery = false;
        this.enabledFilters = enabledFilters;
        this.factory = factory;
    }

    public QueryTranslatorImpl(String queryIdentifier, String query, Map enabledFilters, SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
        this(queryIdentifier, query, enabledFilters, factory);
        this.entityGraphQueryHint = entityGraphQueryHint;
    }

    @Override
    public void compile(Map replacements, boolean shallow) throws QueryException, MappingException {
        this.doCompile(replacements, shallow, null);
    }

    @Override
    public void compile(String collectionRole, Map replacements, boolean shallow) throws QueryException, MappingException {
        this.doCompile(replacements, shallow, collectionRole);
    }

    private synchronized void doCompile(Map replacements, boolean shallow, String collectionRole) {
        if (this.compiled) {
            LOG.debug("compile() : The query is already compiled, skipping...");
            return;
        }
        this.tokenReplacements = replacements;
        if (this.tokenReplacements == null) {
            this.tokenReplacements = new HashMap();
        }
        this.shallowQuery = shallow;
        try {
            HqlParser parser = this.parse(true);
            HqlSqlWalker w = this.analyze(parser, collectionRole);
            this.sqlAst = (Statement)w.getAST();
            if (this.sqlAst.needsExecutor()) {
                this.statementExecutor = this.buildAppropriateStatementExecutor(w);
            } else {
                this.generate((AST)((QueryNode)this.sqlAst));
                this.queryLoader = this.createQueryLoader(w, this.factory);
            }
            this.compiled = true;
        }
        catch (QueryException qe) {
            if (qe.getQueryString() == null) {
                throw qe.wrapWithQueryString(this.hql);
            }
            throw qe;
        }
        catch (RecognitionException e) {
            LOG.trace("Converted antlr.RecognitionException", e);
            throw QuerySyntaxException.convert(e, this.hql);
        }
        catch (ANTLRException e) {
            LOG.trace("Converted antlr.ANTLRException", e);
            throw new QueryException(e.getMessage(), this.hql);
        }
        catch (IllegalArgumentException e) {
            LOG.trace("Converted IllegalArgumentException", e);
            throw new QueryException(e.getMessage(), this.hql);
        }
        this.enabledFilters = null;
    }

    protected QueryLoader createQueryLoader(HqlSqlWalker w, SessionFactoryImplementor factory) {
        return new QueryLoader(this, factory, w.getSelectClause());
    }

    private void generate(AST sqlAst) throws QueryException, RecognitionException {
        if (this.sql == null) {
            SqlGenerator gen = new SqlGenerator(this.factory);
            gen.statement(sqlAst);
            this.sql = gen.getSQL();
            if (LOG.isDebugEnabled()) {
                LOG.debugf("HQL: %s", this.hql);
                LOG.debugf("SQL: %s", this.sql);
            }
            gen.getParseErrorHandler().throwQueryException();
            if (this.collectedParameterSpecifications == null) {
                this.collectedParameterSpecifications = gen.getCollectedParameters();
            } else {
                this.collectedParameterSpecifications.addAll(gen.getCollectedParameters());
            }
        }
    }

    private HqlSqlWalker analyze(HqlParser parser, String collectionRole) throws QueryException, RecognitionException {
        HqlSqlWalker w = new HqlSqlWalker(this, this.factory, parser, this.tokenReplacements, collectionRole);
        AST hqlAst = parser.getAST();
        w.statement(hqlAst);
        if (LOG.isDebugEnabled()) {
            LOG.debug(TokenPrinters.SQL_TOKEN_PRINTER.showAsString(w.getAST(), "--- SQL AST ---"));
        }
        w.getParseErrorHandler().throwQueryException();
        return w;
    }

    private HqlParser parse(boolean filter) throws TokenStreamException {
        HqlParser parser = HqlParser.getInstance(this.hql);
        parser.setFilter(filter);
        LOG.debugf("parse() - HQL: %s", this.hql);
        try {
            parser.statement();
        }
        catch (RecognitionException e) {
            throw new HibernateException("Unexpected error parsing HQL", e);
        }
        AST hqlAst = parser.getAST();
        parser.getParseErrorHandler().throwQueryException();
        NodeTraverser walker = new NodeTraverser(new JavaConstantConverter(this.factory));
        walker.traverseDepthFirst(hqlAst);
        this.showHqlAst(hqlAst);
        return parser;
    }

    void showHqlAst(AST hqlAst) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(TokenPrinters.HQL_TOKEN_PRINTER.showAsString(hqlAst, "--- HQL AST ---"));
        }
    }

    protected void errorIfDML() throws HibernateException {
        if (this.sqlAst.needsExecutor()) {
            throw new QueryExecutionRequestException("Not supported for DML operations", this.hql);
        }
    }

    private void errorIfSelect() throws HibernateException {
        if (!this.sqlAst.needsExecutor()) {
            throw new QueryExecutionRequestException("Not supported for select queries", this.hql);
        }
    }

    @Override
    public String getQueryIdentifier() {
        return this.queryIdentifier;
    }

    public Statement getSqlAST() {
        return this.sqlAst;
    }

    private HqlSqlWalker getWalker() {
        return this.sqlAst.getWalker();
    }

    @Override
    public Type[] getReturnTypes() {
        this.errorIfDML();
        return this.getWalker().getReturnTypes();
    }

    @Override
    public String[] getReturnAliases() {
        this.errorIfDML();
        return this.getWalker().getReturnAliases();
    }

    @Override
    public String[][] getColumnNames() {
        this.errorIfDML();
        return this.getWalker().getSelectClause().getColumnNames();
    }

    @Override
    public Set<Serializable> getQuerySpaces() {
        return this.getWalker().getQuerySpaces();
    }

    @Override
    public List list(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
        QueryParameters queryParametersToUse;
        boolean needsDistincting;
        this.errorIfDML();
        QueryNode query = (QueryNode)this.sqlAst;
        boolean hasLimit = queryParameters.getRowSelection() != null && queryParameters.getRowSelection().definesLimits();
        boolean bl = needsDistincting = (query.getSelectClause().isDistinct() || this.getEntityGraphQueryHint() != null || hasLimit) && this.containsCollectionFetches();
        if (hasLimit && this.containsCollectionFetches()) {
            boolean fail = session.getFactory().getSessionFactoryOptions().isFailOnPaginationOverCollectionFetchEnabled();
            if (fail) {
                throw new HibernateException("firstResult/maxResults specified with collection fetch. In memory pagination was about to be applied. Failing because 'Fail on pagination over collection fetch' is enabled.");
            }
            LOG.firstOrMaxResultsSpecifiedWithCollectionFetch();
            RowSelection selection = new RowSelection();
            selection.setFetchSize(queryParameters.getRowSelection().getFetchSize());
            selection.setTimeout(queryParameters.getRowSelection().getTimeout());
            queryParametersToUse = queryParameters.createCopyUsing(selection);
        } else {
            queryParametersToUse = queryParameters;
        }
        ArrayList results = this.queryLoader.list(session, queryParametersToUse);
        if (needsDistincting) {
            int includedCount = -1;
            int first = !hasLimit || queryParameters.getRowSelection().getFirstRow() == null ? 0 : queryParameters.getRowSelection().getFirstRow();
            int max = !hasLimit || queryParameters.getRowSelection().getMaxRows() == null ? -1 : queryParameters.getRowSelection().getMaxRows();
            ArrayList tmp = new ArrayList();
            IdentitySet distinction = new IdentitySet();
            for (Object result : results) {
                if (!distinction.add(result) || ++includedCount < first) continue;
                tmp.add(result);
                if (max < 0 || includedCount - first < max - 1) continue;
                break;
            }
            results = tmp;
        }
        return results;
    }

    @Override
    public Iterator iterate(QueryParameters queryParameters, EventSource session) throws HibernateException {
        this.errorIfDML();
        return this.queryLoader.iterate(queryParameters, session);
    }

    @Override
    public ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        this.errorIfDML();
        return this.queryLoader.scroll(queryParameters, session);
    }

    @Override
    public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        this.errorIfSelect();
        return this.statementExecutor.execute(queryParameters, session);
    }

    protected String[] getSqlStatements() {
        this.errorIfSelect();
        return this.statementExecutor.getSqlStatements();
    }

    protected StatementExecutor getStatementExecutor() {
        return this.statementExecutor;
    }

    @Override
    public String getSQLString() {
        return this.sql;
    }

    @Override
    public List<String> collectSqlStrings() {
        ArrayList<String> list = new ArrayList<String>();
        if (this.isManipulationStatement()) {
            String[] sqlStatements = this.statementExecutor.getSqlStatements();
            Collections.addAll(list, sqlStatements);
        } else {
            list.add(this.sql);
        }
        return list;
    }

    public boolean isShallowQuery() {
        return this.shallowQuery;
    }

    @Override
    public String getQueryString() {
        return this.hql;
    }

    @Override
    public Map getEnabledFilters() {
        return this.enabledFilters;
    }

    public int[] getNamedParameterLocs(String name) {
        return this.getWalker().getNamedParameterLocations(name);
    }

    @Override
    public boolean containsCollectionFetches() {
        this.errorIfDML();
        List collectionFetches = ((QueryNode)this.sqlAst).getFromClause().getCollectionFetches();
        return collectionFetches != null && collectionFetches.size() > 0;
    }

    @Override
    public boolean isManipulationStatement() {
        return this.sqlAst.needsExecutor();
    }

    @Override
    public boolean isUpdateStatement() {
        return 51 == this.sqlAst.getStatementType();
    }

    @Override
    public List<String> getPrimaryFromClauseTables() {
        return this.sqlAst.getWalker().getFinalFromClause().getFromElements().stream().map(elem -> ((FromElement)elem).getTableName()).collect(Collectors.toList());
    }

    @Override
    public void validateScrollability() throws HibernateException {
        this.errorIfDML();
        QueryNode query = (QueryNode)this.sqlAst;
        List collectionFetches = query.getFromClause().getCollectionFetches();
        if (collectionFetches.isEmpty()) {
            return;
        }
        if (this.isShallowQuery()) {
            return;
        }
        if (this.getReturnTypes().length > 1) {
            throw new HibernateException("cannot scroll with collection fetches and returned tuples");
        }
        FromElement owner = null;
        for (Object o : query.getSelectClause().getFromElementsForLoad()) {
            FromElement fromElement = (FromElement)o;
            if (fromElement.getOrigin() != null) continue;
            owner = fromElement;
            break;
        }
        if (owner == null) {
            throw new HibernateException("unable to locate collection fetch(es) owner for scrollability checks");
        }
        AST primaryOrdering = query.getOrderByClause().getFirstChild();
        if (primaryOrdering != null) {
            String[] idColNames = owner.getQueryable().getIdentifierColumnNames();
            String expectedPrimaryOrderSeq = String.join((CharSequence)", ", StringHelper.qualify(owner.getTableAlias(), idColNames));
            if (!primaryOrdering.getText().startsWith(expectedPrimaryOrderSeq)) {
                throw new HibernateException("cannot scroll results with collection fetches which are not ordered primarily by the root entity's PK");
            }
        }
    }

    protected StatementExecutor buildAppropriateStatementExecutor(HqlSqlWalker walker) {
        if (walker.getStatementType() == 13) {
            Queryable persister = walker.getFinalFromClause().getFromElement().getQueryable();
            if (persister.isMultiTable()) {
                return new MultiTableDeleteExecutor(walker);
            }
            return new DeleteExecutor(walker);
        }
        if (walker.getStatementType() == 51) {
            Queryable persister = walker.getFinalFromClause().getFromElement().getQueryable();
            if (persister.isMultiTable() && QueryTranslatorImpl.affectsExtraTables(walker, persister)) {
                return new MultiTableUpdateExecutor(walker);
            }
            if (persister.isMultiTable() && walker.getQuerySpaces().size() > 1) {
                return new IdSubselectUpdateExecutor(walker);
            }
            return new SimpleUpdateExecutor(walker);
        }
        if (walker.getStatementType() == 30) {
            return new InsertExecutor(walker);
        }
        throw new QueryException("Unexpected statement type");
    }

    private static boolean affectsExtraTables(HqlSqlWalker walker, Queryable persister) {
        String[] tableNames = persister.getConstraintOrderedTableNameClosure();
        return IntStream.range(0, tableNames.length).filter(table -> walker.getAssignmentSpecifications().stream().anyMatch(assign -> assign.affectsTable(tableNames[table]))).count() > 1L;
    }

    @Override
    public ParameterTranslations getParameterTranslations() {
        if (this.paramTranslations == null) {
            this.paramTranslations = new ParameterTranslationsImpl(this.getWalker().getParameterSpecs());
        }
        return this.paramTranslations;
    }

    public List<ParameterSpecification> getCollectedParameterSpecifications() {
        return this.collectedParameterSpecifications;
    }

    @Override
    public Class getDynamicInstantiationResultType() {
        AggregatedSelectExpression aggregation = this.queryLoader.getAggregatedSelectExpression();
        return aggregation == null ? null : aggregation.getAggregationResultType();
    }

    public EntityGraphQueryHint getEntityGraphQueryHint() {
        return this.entityGraphQueryHint;
    }

    public void setEntityGraphQueryHint(EntityGraphQueryHint entityGraphQueryHint) {
        this.entityGraphQueryHint = entityGraphQueryHint;
    }

    public static class JavaConstantConverter
    implements NodeTraverser.VisitationStrategy {
        private final SessionFactoryImplementor factory;
        private AST dotRoot;

        public JavaConstantConverter(SessionFactoryImplementor factory) {
            this.factory = factory;
        }

        @Override
        public void visit(AST node) {
            if (this.dotRoot != null) {
                if (ASTUtil.isSubtreeChild(this.dotRoot, node)) {
                    return;
                }
                this.dotRoot = null;
            }
            if (node.getType() == 15) {
                this.dotRoot = node;
                this.handleDotStructure(this.dotRoot);
            }
        }

        private void handleDotStructure(AST dotStructureRoot) {
            String expression = ASTUtil.getPathText(dotStructureRoot);
            Object constant = ReflectHelper.getConstantValue(expression, this.factory);
            if (constant != null) {
                dotStructureRoot.setFirstChild(null);
                dotStructureRoot.setType(106);
                dotStructureRoot.setText(expression);
            }
        }
    }
}

