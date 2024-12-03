/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 *  antlr.RecognitionException
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast;

import antlr.ASTFactory;
import antlr.RecognitionException;
import antlr.SemanticException;
import antlr.collections.AST;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.engine.internal.ParameterBinder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.CollectionProperties;
import org.hibernate.hql.internal.antlr.HqlSqlBaseWalker;
import org.hibernate.hql.internal.ast.ErrorReporter;
import org.hibernate.hql.internal.ast.ErrorTracker;
import org.hibernate.hql.internal.ast.HqlParser;
import org.hibernate.hql.internal.ast.InvalidWithClauseException;
import org.hibernate.hql.internal.ast.ParseErrorHandler;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.internal.ast.SqlASTFactory;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.tree.AggregateNode;
import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
import org.hibernate.hql.internal.ast.tree.CastFunctionNode;
import org.hibernate.hql.internal.ast.tree.CollectionFunction;
import org.hibernate.hql.internal.ast.tree.CollectionPathNode;
import org.hibernate.hql.internal.ast.tree.CollectionSizeNode;
import org.hibernate.hql.internal.ast.tree.ConstructorNode;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.EntityJoinFromElement;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromElementFactory;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.IdentNode;
import org.hibernate.hql.internal.ast.tree.ImpliedFromElement;
import org.hibernate.hql.internal.ast.tree.IndexNode;
import org.hibernate.hql.internal.ast.tree.InsertStatement;
import org.hibernate.hql.internal.ast.tree.IntoClause;
import org.hibernate.hql.internal.ast.tree.MethodNode;
import org.hibernate.hql.internal.ast.tree.OperatorNode;
import org.hibernate.hql.internal.ast.tree.ParameterContainer;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.ResolvableNode;
import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
import org.hibernate.hql.internal.ast.tree.ResultVariableRefNode;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.tree.UpdateStatement;
import org.hibernate.hql.internal.ast.util.ASTPrinter;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.AliasGenerator;
import org.hibernate.hql.internal.ast.util.JoinProcessor;
import org.hibernate.hql.internal.ast.util.LiteralProcessor;
import org.hibernate.hql.internal.ast.util.NodeTraverser;
import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
import org.hibernate.hql.internal.ast.util.SyntheticAndFactory;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.param.CollectionFilterKeyParameterSpecification;
import org.hibernate.param.NamedParameterSpecification;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.param.PositionalParameterSpecification;
import org.hibernate.param.VersionTypeSeedParameterSpecification;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.JoinType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.DbTimestampType;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;
import org.hibernate.usertype.UserVersionType;

public class HqlSqlWalker
extends HqlSqlBaseWalker
implements ErrorReporter,
ParameterBinder.NamedParameterSource {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(HqlSqlWalker.class);
    private final QueryTranslatorImpl queryTranslatorImpl;
    private final HqlParser hqlParser;
    private final SessionFactoryHelper sessionFactoryHelper;
    private final Map tokenReplacements;
    private final AliasGenerator aliasGenerator = new AliasGenerator();
    private final LiteralProcessor literalProcessor;
    private final ParseErrorHandler parseErrorHandler;
    private final String collectionFilterRole;
    private FromClause currentFromClause;
    private SelectClause selectClause;
    private Map<String, SelectExpression> selectExpressionsByResultVariable = new HashMap<String, SelectExpression>();
    private Set<Serializable> querySpaces = new HashSet<Serializable>();
    private int parameterCount;
    private Map namedParameters;
    private Map positionalParameters;
    private ArrayList<ParameterSpecification> parameterSpecs = new ArrayList();
    private int numberOfParametersInSetClause;
    private ArrayList<AssignmentSpecification> assignmentSpecifications = new ArrayList();
    private JoinType impliedJoinType = JoinType.INNER_JOIN;
    private boolean inEntityGraph;
    private int traceDepth;
    private boolean hasAnyForcibleNotFoundImplicitJoins;

    public HqlSqlWalker(QueryTranslatorImpl qti, SessionFactoryImplementor sfi, HqlParser parser, Map tokenReplacements, String collectionRole) {
        this.setASTFactory(new SqlASTFactory(this));
        this.parseErrorHandler = new ErrorTracker(qti.getQueryString());
        this.queryTranslatorImpl = qti;
        this.sessionFactoryHelper = new SessionFactoryHelper(sfi);
        this.literalProcessor = new LiteralProcessor(this);
        this.tokenReplacements = tokenReplacements;
        this.collectionFilterRole = collectionRole;
        this.hqlParser = parser;
    }

    public void traceIn(String ruleName, AST tree) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = StringHelper.repeat('-', this.traceDepth++ * 2) + "-> ";
        String traceText = ruleName + " (" + this.buildTraceNodeName(tree) + ")";
        LOG.trace(prefix + traceText);
    }

    private String buildTraceNodeName(AST tree) {
        return tree == null ? "???" : tree.getText() + " [" + TokenPrinters.SQL_TOKEN_PRINTER.getTokenTypeName(tree.getType()) + "]";
    }

    public void traceOut(String ruleName, AST tree) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = "<-" + StringHelper.repeat('-', --this.traceDepth * 2) + " ";
        LOG.trace(prefix + ruleName);
    }

    @Override
    protected void prepareFromClauseInputTree(AST fromClauseInput) {
        if (!this.isSubQuery() && this.isFilter()) {
            QueryableCollection persister = this.sessionFactoryHelper.getCollectionPersister(this.collectionFilterRole);
            Type collectionElementType = persister.getElementType();
            if (!collectionElementType.isEntityType()) {
                throw new QueryException("collection of values in filter: this");
            }
            String collectionElementEntityName = persister.getElementPersister().getEntityName();
            ASTFactory inputAstFactory = this.hqlParser.getASTFactory();
            AST fromElement = inputAstFactory.create(81, collectionElementEntityName);
            ASTUtil.createSibling(inputAstFactory, 75, "this", fromElement);
            fromClauseInput.addChild(fromElement);
            LOG.debug("prepareFromClauseInputTree() : Filter - Added 'this' as a from element...");
            this.queryTranslatorImpl.showHqlAst(this.hqlParser.getAST());
            Type collectionFilterKeyType = this.sessionFactoryHelper.requireQueryableCollection(this.collectionFilterRole).getKeyType();
            ParameterNode collectionFilterKeyParameter = (ParameterNode)this.astFactory.create(132, "?");
            CollectionFilterKeyParameterSpecification collectionFilterKeyParameterSpec = new CollectionFilterKeyParameterSpecification(this.collectionFilterRole, collectionFilterKeyType);
            ++this.parameterCount;
            collectionFilterKeyParameter.setHqlParameterSpecification(collectionFilterKeyParameterSpec);
            this.parameterSpecs.add(collectionFilterKeyParameterSpec);
        }
    }

    public boolean isFilter() {
        return this.collectionFilterRole != null;
    }

    public String getCollectionFilterRole() {
        return this.collectionFilterRole;
    }

    public boolean isInEntityGraph() {
        return this.inEntityGraph;
    }

    public SessionFactoryHelper getSessionFactoryHelper() {
        return this.sessionFactoryHelper;
    }

    public Map getTokenReplacements() {
        return this.tokenReplacements;
    }

    public AliasGenerator getAliasGenerator() {
        return this.aliasGenerator;
    }

    public FromClause getCurrentFromClause() {
        return this.currentFromClause;
    }

    public ParseErrorHandler getParseErrorHandler() {
        return this.parseErrorHandler;
    }

    @Override
    public void reportError(RecognitionException e) {
        this.parseErrorHandler.reportError(e);
    }

    @Override
    public void reportError(String s) {
        this.parseErrorHandler.reportError(s);
    }

    @Override
    public void reportWarning(String s) {
        this.parseErrorHandler.reportWarning(s);
    }

    public Set<Serializable> getQuerySpaces() {
        return this.querySpaces;
    }

    @Override
    protected AST createFromElement(String path, AST alias, AST propertyFetch) throws SemanticException {
        FromElement fromElement = this.currentFromClause.addFromElement(path, alias);
        fromElement.setAllPropertyFetch(propertyFetch != null);
        return fromElement;
    }

    @Override
    protected AST createFromFilterElement(AST filterEntity, AST alias) throws SemanticException {
        FromElement fromElement = this.currentFromClause.addFromElement(filterEntity.getText(), alias);
        FromClause fromClause = fromElement.getFromClause();
        QueryableCollection persister = this.sessionFactoryHelper.getCollectionPersister(this.collectionFilterRole);
        String[] keyColumnNames = persister.getKeyColumnNames();
        String fkTableAlias = persister.isOneToMany() ? fromElement.getTableAlias() : fromClause.getAliasGenerator().createName(this.collectionFilterRole);
        JoinSequence join = this.sessionFactoryHelper.createJoinSequence();
        join.setRoot(persister, fkTableAlias);
        if (!persister.isOneToMany()) {
            join.addJoin((AssociationType)persister.getElementType(), fromElement.getTableAlias(), JoinType.INNER_JOIN, persister.getElementColumnNames(fkTableAlias));
        }
        join.addCondition(fkTableAlias, keyColumnNames, " = ?");
        fromElement.setJoinSequence(join);
        fromElement.setFilter(true);
        LOG.debug("createFromFilterElement() : processed filter FROM element.");
        return fromElement;
    }

    @Override
    protected void createFromJoinElement(AST path, AST alias, int joinType, AST fetchNode, AST propertyFetch, AST with) throws SemanticException {
        boolean fetch;
        boolean bl = fetch = fetchNode != null;
        if (fetch && this.isSubQuery()) {
            throw new QueryException("fetch not allowed in subquery from-elements");
        }
        EntityPersister entityJoinReferencedPersister = this.resolveEntityJoinReferencedPersister(path);
        if (entityJoinReferencedPersister != null) {
            EntityJoinFromElement join = this.createEntityJoin(entityJoinReferencedPersister, alias, joinType, propertyFetch, with);
            ((FromReferenceNode)path).setFromElement(join);
        } else {
            FromElement fromElement;
            if (path.getType() != 15) {
                throw new SemanticException("Path expected for join!");
            }
            DotNode dot = (DotNode)path;
            JoinType hibernateJoinType = JoinProcessor.toHibernateJoinType(joinType);
            dot.setJoinType(hibernateJoinType);
            dot.setFetch(fetch);
            dot.resolve(true, false, alias == null ? null : alias.getText());
            if (dot.getDataType() != null && dot.getDataType().isComponentType()) {
                if (dot.getDataType().isAnyType()) {
                    throw new SemanticException("An AnyType attribute cannot be join fetched");
                }
                FromElementFactory factory = new FromElementFactory(this.getCurrentFromClause(), dot.getLhs().getFromElement(), dot.getPropertyPath(), alias == null ? null : alias.getText(), null, false);
                fromElement = factory.createComponentJoin((CompositeType)dot.getDataType());
            } else {
                fromElement = dot.getImpliedJoin();
                fromElement.setAllPropertyFetch(propertyFetch != null);
                if (with != null) {
                    if (fetch) {
                        throw new SemanticException("with-clause not allowed on fetched associations; use filters");
                    }
                    this.handleWithFragment(fromElement, with);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("createFromJoinElement() : " + this.getASTPrinter().showAsString((AST)fromElement, "-- join tree --"));
            }
        }
    }

    private EntityPersister resolveEntityJoinReferencedPersister(AST path) {
        if (path.getType() == 111) {
            IdentNode pathIdentNode = (IdentNode)path;
            String name = path.getText();
            if (name == null) {
                name = pathIdentNode.getOriginalText();
            }
            return this.sessionFactoryHelper.findEntityPersisterByName(name);
        }
        if (path.getType() == 15) {
            String pathText = ASTUtil.getPathText(path);
            return this.sessionFactoryHelper.findEntityPersisterByName(pathText);
        }
        return null;
    }

    @Override
    protected void finishFromClause(AST fromClause) throws SemanticException {
        ((FromClause)fromClause).finishInit();
    }

    private EntityJoinFromElement createEntityJoin(EntityPersister entityPersister, AST aliasNode, int joinType, AST propertyFetch, AST with) throws SemanticException {
        String alias = aliasNode == null ? null : aliasNode.getText();
        LOG.debugf("Creating entity-join FromElement [%s -> %s]", alias, entityPersister.getEntityName());
        EntityJoinFromElement join = new EntityJoinFromElement(this, this.getCurrentFromClause(), entityPersister, JoinProcessor.toHibernateJoinType(joinType), propertyFetch != null, alias);
        if (with != null) {
            this.handleWithFragment(join, with);
        }
        return join;
    }

    private void handleWithFragment(FromElement fromElement, AST hqlWithNode) throws SemanticException {
        try {
            this.withClause(hqlWithNode);
            AST hqlSqlWithNode = this.returnAST;
            if (LOG.isDebugEnabled()) {
                LOG.debug("handleWithFragment() : " + this.getASTPrinter().showAsString(hqlSqlWithNode, "-- with clause --"));
            }
            WithClauseVisitor visitor = new WithClauseVisitor(fromElement, this.queryTranslatorImpl);
            NodeTraverser traverser = new NodeTraverser(visitor);
            traverser.traverseDepthFirst(hqlSqlWithNode);
            SqlGenerator sql = new SqlGenerator(this.getSessionFactoryHelper().getFactory());
            sql.whereExpr(hqlSqlWithNode.getFirstChild());
            fromElement.setWithClauseFragment(hqlSqlWithNode.getFirstChild(), "(" + sql.getSQL() + ")");
        }
        catch (SemanticException e) {
            throw e;
        }
        catch (InvalidWithClauseException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SemanticException(e.getMessage());
        }
    }

    public void registerForcibleNotFoundImplicitJoin(ImpliedFromElement impliedJoin) {
        this.hasAnyForcibleNotFoundImplicitJoins = true;
    }

    public boolean hasAnyForcibleNotFoundImplicitJoins() {
        return this.hasAnyForcibleNotFoundImplicitJoins;
    }

    @Override
    protected void pushFromClause(AST fromNode, AST inputFromNode) {
        FromClause newFromClause = (FromClause)fromNode;
        newFromClause.setParentFromClause(this.currentFromClause);
        this.currentFromClause = newFromClause;
    }

    private void popFromClause() {
        this.currentFromClause = this.currentFromClause.getParentFromClause();
    }

    @Override
    protected void lookupAlias(AST aliasRef) throws SemanticException {
        FromElement alias = this.currentFromClause.getFromElement(aliasRef.getText());
        FromReferenceNode aliasRefNode = (FromReferenceNode)aliasRef;
        aliasRefNode.setFromElement(alias);
    }

    @Override
    protected void setImpliedJoinType(int joinType) {
        this.impliedJoinType = JoinProcessor.toHibernateJoinType(joinType);
    }

    public JoinType getImpliedJoinType() {
        return this.impliedJoinType;
    }

    @Override
    protected AST createCollectionSizeFunction(AST collectionPath, boolean inSelect) throws SemanticException {
        assert (collectionPath instanceof CollectionPathNode);
        return new CollectionSizeNode((CollectionPathNode)collectionPath);
    }

    @Override
    protected AST lookupProperty(AST dot, boolean root, boolean inSelect) throws SemanticException {
        DotNode dotNode = (DotNode)dot;
        FromReferenceNode lhs = dotNode.getLhs();
        AST rhs = lhs.getNextSibling();
        if (lhs.getDataType() != null && lhs.getDataType().isCollectionType()) {
            if (CollectionProperties.isCollectionProperty(rhs.getText())) {
                DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfCollectionPropertiesInHql(rhs.getText(), lhs.getPath());
            }
            if ("indices".equalsIgnoreCase(rhs.getText()) || "elements".equalsIgnoreCase(rhs.getText())) {
                CollectionFunction f;
                if (LOG.isDebugEnabled()) {
                    LOG.debugf("lookupProperty() %s => %s(%s)", dotNode.getPath(), rhs.getText(), lhs.getPath());
                }
                if (rhs instanceof CollectionFunction) {
                    f = (CollectionFunction)rhs;
                } else {
                    f = new CollectionFunction();
                    f.initialize(86, rhs.getText());
                    f.initialize(this);
                }
                f.setFirstChild((AST)lhs);
                lhs.setNextSibling(null);
                dotNode.setFirstChild((AST)f);
                this.resolve((AST)lhs);
                f.resolve(inSelect);
                return f;
            }
        }
        dotNode.resolveFirstChild();
        return dotNode;
    }

    @Override
    protected boolean isNonQualifiedPropertyRef(AST ident) {
        String identText = ident.getText();
        if (this.currentFromClause.isFromElementAlias(identText)) {
            return false;
        }
        List fromElements = this.currentFromClause.getExplicitFromElements();
        if (fromElements.size() == 1) {
            FromElement fromElement = (FromElement)fromElements.get(0);
            try {
                LOG.tracev("Attempting to resolve property [{0}] as a non-qualified ref", identText);
                return fromElement.isNonQualifiedPropertyRef(identText);
            }
            catch (QueryException queryException) {
                // empty catch block
            }
        }
        return false;
    }

    @Override
    protected AST lookupNonQualifiedProperty(AST property) throws SemanticException {
        FromElement fromElement = (FromElement)this.currentFromClause.getExplicitFromElements().get(0);
        AST syntheticDotNode = this.generateSyntheticDotNodeForNonQualifiedPropertyRef(property, fromElement);
        return this.lookupProperty(syntheticDotNode, false, this.getCurrentClauseType() == 46);
    }

    private AST generateSyntheticDotNodeForNonQualifiedPropertyRef(AST property, FromElement fromElement) {
        AST dot = this.getASTFactory().create(15, "{non-qualified-property-ref}");
        ((DotNode)dot).setPropertyPath(((FromReferenceNode)property).getPath());
        IdentNode syntheticAlias = (IdentNode)this.getASTFactory().create(111, "{synthetic-alias}");
        syntheticAlias.setFromElement(fromElement);
        syntheticAlias.setResolved();
        dot.setFirstChild((AST)syntheticAlias);
        dot.addChild(property);
        return dot;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void processQuery(AST select, AST query) throws SemanticException {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("processQuery() : %s", query.toStringTree());
        }
        try {
            boolean explicitSelect;
            QueryNode qn = (QueryNode)query;
            boolean bl = explicitSelect = select != null && select.getNumberOfChildren() > 0;
            if (this.queryTranslatorImpl.getEntityGraphQueryHint() != null) {
                boolean oldInEntityGraph = this.inEntityGraph;
                try {
                    this.inEntityGraph = true;
                    qn.getFromClause().getFromElements().addAll(this.queryTranslatorImpl.getEntityGraphQueryHint().toFromElements(qn.getFromClause(), this));
                }
                finally {
                    this.inEntityGraph = oldInEntityGraph;
                }
            }
            if (!explicitSelect) {
                this.createSelectClauseFromFromClause(qn);
            } else {
                this.useSelectClause(select);
            }
            JoinProcessor joinProcessor = new JoinProcessor(this);
            joinProcessor.processJoins(qn);
            for (FromElement fromElement : qn.getFromClause().getProjectionList()) {
                String orderByFragment;
                if (!fromElement.isFetch() || fromElement.getQueryableCollection() == null) continue;
                if (fromElement.getQueryableCollection().hasOrdering()) {
                    orderByFragment = fromElement.getQueryableCollection().getSQLOrderByString(fromElement.getCollectionTableAlias());
                    qn.getOrderByClause().addOrderFragment(orderByFragment);
                }
                if (!fromElement.getQueryableCollection().hasManyToManyOrdering()) continue;
                orderByFragment = fromElement.getQueryableCollection().getManyToManyOrderByString(fromElement.getTableAlias());
                qn.getOrderByClause().addOrderFragment(orderByFragment);
            }
        }
        finally {
            this.popFromClause();
        }
    }

    protected void postProcessDML(RestrictableStatement statement) throws SemanticException {
        statement.getFromClause().resolve();
        FromElement fromElement = (FromElement)statement.getFromClause().getFromElements().get(0);
        Queryable persister = fromElement.getQueryable();
        fromElement.setText(persister.getTableName());
        if (persister.getDiscriminatorType() != null || !this.queryTranslatorImpl.getEnabledFilters().isEmpty()) {
            new SyntheticAndFactory(this).addDiscriminatorWhereFragment(statement, persister, this.queryTranslatorImpl.getEnabledFilters(), fromElement.getTableAlias());
        }
    }

    @Override
    protected void postProcessUpdate(AST update) throws SemanticException {
        UpdateStatement updateStatement = (UpdateStatement)update;
        this.postProcessDML(updateStatement);
    }

    @Override
    protected void postProcessDelete(AST delete) throws SemanticException {
        this.postProcessDML((DeleteStatement)delete);
    }

    @Override
    protected void postProcessInsert(AST insert) throws SemanticException, QueryException {
        boolean includeVersionProperty;
        InsertStatement insertStatement = (InsertStatement)insert;
        insertStatement.validate();
        SelectClause selectClause = insertStatement.getSelectClause();
        Queryable persister = insertStatement.getIntoClause().getQueryable();
        if (!insertStatement.getIntoClause().isExplicitIdInsertion()) {
            IdentifierGenerator generator = persister.getIdentifierGenerator();
            if (!BulkInsertionCapableIdentifierGenerator.class.isInstance(generator)) {
                throw new QueryException("Invalid identifier generator encountered for implicit id handling as part of bulk insertions");
            }
            BulkInsertionCapableIdentifierGenerator capableGenerator = (BulkInsertionCapableIdentifierGenerator)BulkInsertionCapableIdentifierGenerator.class.cast(generator);
            if (!capableGenerator.supportsBulkInsertionIdentifierGeneration()) {
                throw new QueryException("Identifier generator reported it does not support implicit id handling as part of bulk insertions");
            }
            String fragment = capableGenerator.determineBulkInsertionIdentifierGenerationSelectFragment(this.sessionFactoryHelper.getFactory().getSqlStringGenerationContext());
            if (fragment != null) {
                AST fragmentNode = this.getASTFactory().create(150, fragment);
                AST originalFirstSelectExprNode = selectClause.getFirstChild();
                selectClause.setFirstChild(fragmentNode);
                fragmentNode.setNextSibling(originalFirstSelectExprNode);
                insertStatement.getIntoClause().prependIdColumnSpec();
            }
        }
        if (this.sessionFactoryHelper.getFactory().getDialect().supportsParametersInInsertSelect()) {
            int i = 0;
            for (AST child = selectClause.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (!(child instanceof ParameterNode)) continue;
                ((ParameterNode)child).setExpectedType(insertStatement.getIntoClause().getInsertionTypes()[selectClause.getParameterPositions().get(i)]);
                ++i;
            }
        }
        boolean bl = includeVersionProperty = persister.isVersioned() && !insertStatement.getIntoClause().isExplicitVersionInsertion() && persister.isVersionPropertyInsertable();
        if (includeVersionProperty) {
            VersionType versionType = persister.getVersionType();
            Object versionValueNode = null;
            if (this.sessionFactoryHelper.getFactory().getDialect().supportsParametersInInsertSelect()) {
                int[] sqlTypes = versionType.sqlTypes(this.sessionFactoryHelper.getFactory());
                if (sqlTypes == null || sqlTypes.length == 0) {
                    throw new IllegalStateException(versionType.getClass() + ".sqlTypes() returns null or empty array");
                }
                if (sqlTypes.length > 1) {
                    throw new IllegalStateException(versionType.getClass() + ".sqlTypes() returns > 1 element; only single-valued versions are allowed.");
                }
                versionValueNode = this.getASTFactory().create(132, "?");
                VersionTypeSeedParameterSpecification paramSpec = new VersionTypeSeedParameterSpecification(versionType);
                ((ParameterNode)versionValueNode).setHqlParameterSpecification(paramSpec);
                this.parameterSpecs.add(0, paramSpec);
                if (this.sessionFactoryHelper.getFactory().getDialect().requiresCastingOfParametersInSelectClause()) {
                    MethodNode versionMethodNode = (MethodNode)this.getASTFactory().create(86, "(");
                    AST methodIdentNode = this.getASTFactory().create(111, "cast");
                    versionMethodNode.addChild(methodIdentNode);
                    versionMethodNode.initializeMethodNode(methodIdentNode, true);
                    AST castExprListNode = this.getASTFactory().create(80, "exprList");
                    methodIdentNode.setNextSibling(castExprListNode);
                    castExprListNode.addChild(versionValueNode);
                    versionValueNode.setNextSibling(this.getASTFactory().create(111, this.sessionFactoryHelper.getFactory().getDialect().getTypeName(sqlTypes[0])));
                    this.processFunction((AST)versionMethodNode, true);
                    versionValueNode = versionMethodNode;
                }
            } else if (this.isIntegral(versionType)) {
                try {
                    Object seedValue = versionType.seed(null);
                    versionValueNode = this.getASTFactory().create(150, seedValue.toString());
                }
                catch (Throwable t) {
                    throw new QueryException("could not determine seed value for version on bulk insert [" + versionType + "]");
                }
            } else if (this.isDatabaseGeneratedTimestamp(versionType)) {
                String functionName = this.sessionFactoryHelper.getFactory().getDialect().getCurrentTimestampSQLFunctionName();
                versionValueNode = this.getASTFactory().create(150, functionName);
            } else {
                throw new QueryException("cannot handle version type [" + versionType + "] on bulk inserts with dialects not supporting parameterSpecs in insert-select statements");
            }
            AST currentFirstSelectExprNode = selectClause.getFirstChild();
            selectClause.setFirstChild((AST)versionValueNode);
            versionValueNode.setNextSibling(currentFirstSelectExprNode);
            insertStatement.getIntoClause().prependVersionColumnSpec();
        }
        if (insertStatement.getIntoClause().isDiscriminated()) {
            String sqlValue = insertStatement.getIntoClause().getQueryable().getDiscriminatorSQLValue();
            AST discrimValue = this.getASTFactory().create(150, sqlValue);
            insertStatement.getSelectClause().addChild(discrimValue);
        }
    }

    private boolean isDatabaseGeneratedTimestamp(Type type) {
        return DbTimestampType.class.isAssignableFrom(type.getClass());
    }

    private boolean isIntegral(Type type) {
        return Long.class.isAssignableFrom(type.getReturnedClass()) || Integer.class.isAssignableFrom(type.getReturnedClass()) || Long.TYPE.isAssignableFrom(type.getReturnedClass()) || Integer.TYPE.isAssignableFrom(type.getReturnedClass());
    }

    private void useSelectClause(AST select) throws SemanticException {
        this.selectClause = (SelectClause)select;
        this.selectClause.initializeExplicitSelectClause(this.currentFromClause);
    }

    private void createSelectClauseFromFromClause(QueryNode qn) throws SemanticException {
        AST select = this.astFactory.create(145, "{derived select clause}");
        FromClause sibling = qn.getFromClause();
        qn.setFirstChild(select);
        select.setNextSibling((AST)sibling);
        this.selectClause = (SelectClause)select;
        this.selectClause.initializeDerivedSelectClause(this.currentFromClause);
        LOG.debug("Derived SELECT clause created.");
    }

    @Override
    protected void resolve(AST node) throws SemanticException {
        this.resolve(node, null);
    }

    @Override
    protected void resolve(AST node, AST predicateNode) throws SemanticException {
        if (node != null) {
            ResolvableNode r = (ResolvableNode)node;
            if (this.isInFunctionCall()) {
                r.resolveInFunctionCall(false, true);
            } else {
                r.resolve(false, true, null, null, predicateNode);
            }
        }
    }

    @Override
    protected void resolveSelectExpression(AST node) throws SemanticException {
        int type = node.getType();
        switch (type) {
            case 15: {
                DotNode dot = (DotNode)node;
                dot.resolveSelectExpression();
                break;
            }
            case 148: {
                FromReferenceNode aliasRefNode = (FromReferenceNode)node;
                aliasRefNode.resolve(false, false);
                FromElement fromElement = aliasRefNode.getFromElement();
                if (fromElement == null) break;
                fromElement.setIncludeSubclasses(true);
                break;
            }
        }
    }

    @Override
    protected void beforeSelectClause() throws SemanticException {
        FromClause from = this.getCurrentFromClause();
        List fromElements = from.getFromElements();
        for (FromElement fromElement : fromElements) {
            fromElement.setIncludeSubclasses(false);
        }
    }

    @Override
    protected AST generatePositionalParameter(AST delimiterNode, AST numberNode) throws SemanticException {
        if (this.getSessionFactoryHelper().isStrictJPAQLComplianceEnabled() && this.namedParameters != null) {
            throw new SemanticException("Cannot mix positional and named parameters: " + this.queryTranslatorImpl.getQueryString());
        }
        if (numberNode == null) {
            throw new QueryException(String.format(Locale.ROOT, "Legacy-style query parameters (`?`) are no longer supported; use JPA-style ordinal parameters (e.g., `?1`) instead : %s", this.queryTranslatorImpl.getQueryString()));
        }
        String positionString = numberNode.getText();
        int label = Integer.parseInt(positionString);
        this.trackPositionalParameterPositions(label);
        ParameterNode parameter = (ParameterNode)this.astFactory.create(132, positionString);
        parameter.setText("?");
        int queryParamtersPosition = this.isFilter() ? label : label - 1;
        PositionalParameterSpecification paramSpec = new PositionalParameterSpecification(delimiterNode.getLine(), delimiterNode.getColumn(), label, queryParamtersPosition);
        parameter.setHqlParameterSpecification(paramSpec);
        this.parameterSpecs.add(paramSpec);
        return parameter;
    }

    private void trackPositionalParameterPositions(int label) {
        if (this.positionalParameters == null) {
            this.positionalParameters = new HashMap();
        }
        Integer loc = this.parameterCount++;
        Object existingValue = this.positionalParameters.get(label);
        if (existingValue == null) {
            this.positionalParameters.put(label, loc);
        } else if (existingValue instanceof Integer) {
            ArrayList<Object> list = new ArrayList<Object>();
            this.positionalParameters.put(label, list);
            list.add(existingValue);
            list.add(loc);
        } else {
            ((List)existingValue).add(loc);
        }
    }

    @Override
    protected AST generateNamedParameter(AST delimiterNode, AST nameNode) throws SemanticException {
        if (this.getSessionFactoryHelper().isStrictJPAQLComplianceEnabled() && this.positionalParameters != null) {
            throw new SemanticException("Cannot mix positional and named parameters: " + this.queryTranslatorImpl.getQueryString());
        }
        String name = nameNode.getText();
        this.trackNamedParameterPositions(name);
        ParameterNode parameter = (ParameterNode)this.astFactory.create(156, name);
        parameter.setText("?");
        NamedParameterSpecification paramSpec = new NamedParameterSpecification(delimiterNode.getLine(), delimiterNode.getColumn(), name);
        parameter.setHqlParameterSpecification(paramSpec);
        this.parameterSpecs.add(paramSpec);
        return parameter;
    }

    private void trackNamedParameterPositions(String name) {
        if (this.namedParameters == null) {
            this.namedParameters = new HashMap();
        }
        Integer loc = this.parameterCount++;
        Object existingValue = this.namedParameters.get(name);
        if (existingValue == null) {
            this.namedParameters.put(name, loc);
        } else if (existingValue instanceof Integer) {
            ArrayList<Integer> list = new ArrayList<Integer>(4);
            list.add((Integer)existingValue);
            list.add(loc);
            this.namedParameters.put(name, list);
        } else {
            ((List)existingValue).add(loc);
        }
    }

    @Override
    protected void processConstant(AST constant) throws SemanticException {
        this.literalProcessor.processConstant(constant, true);
    }

    @Override
    protected void processBoolean(AST constant) throws SemanticException {
        this.literalProcessor.processBoolean(constant);
    }

    @Override
    protected void processNumericLiteral(AST literal) {
        this.literalProcessor.processNumeric(literal);
    }

    @Override
    protected void processIndex(AST indexOp) throws SemanticException {
        IndexNode indexNode = (IndexNode)indexOp;
        indexNode.resolve(true, true);
    }

    @Override
    protected AST createCollectionPath(AST qualifier, AST reference) throws SemanticException {
        return CollectionPathNode.from(qualifier, reference, this);
    }

    @Override
    protected void processFunction(AST functionCall, boolean inSelect) throws SemanticException {
        MethodNode methodNode = (MethodNode)functionCall;
        methodNode.resolve(inSelect);
    }

    @Override
    protected void processCastFunction(AST castFunctionCall, boolean inSelect) throws SemanticException {
        CastFunctionNode castFunctionNode = (CastFunctionNode)castFunctionCall;
        castFunctionNode.resolve(inSelect);
    }

    @Override
    protected void processAggregation(AST node, boolean inSelect) throws SemanticException {
        AggregateNode aggregateNode = (AggregateNode)node;
        aggregateNode.resolve();
    }

    @Override
    protected void processConstructor(AST constructor) throws SemanticException {
        ConstructorNode constructorNode = (ConstructorNode)constructor;
        constructorNode.prepare();
    }

    @Override
    protected void setAlias(AST selectExpr, AST ident) {
        ((SelectExpression)selectExpr).setAlias(ident.getText());
        if (!this.isSubQuery()) {
            this.selectExpressionsByResultVariable.put(ident.getText(), (SelectExpression)selectExpr);
        }
    }

    @Override
    protected boolean isOrderExpressionResultVariableRef(AST orderExpressionNode) throws SemanticException {
        return !this.isSubQuery() && orderExpressionNode.getType() == 111 && this.selectExpressionsByResultVariable.containsKey(orderExpressionNode.getText());
    }

    @Override
    protected boolean isGroupExpressionResultVariableRef(AST groupExpressionNode) throws SemanticException {
        return this.getDialect().supportsSelectAliasInGroupByClause() && !this.isSubQuery() && groupExpressionNode.getType() == 111 && this.selectExpressionsByResultVariable.containsKey(groupExpressionNode.getText());
    }

    @Override
    protected void handleResultVariableRef(AST resultVariableRef) throws SemanticException {
        if (this.isSubQuery()) {
            throw new SemanticException("References to result variables in subqueries are not supported.");
        }
        ((ResultVariableRefNode)resultVariableRef).setSelectExpression(this.selectExpressionsByResultVariable.get(resultVariableRef.getText()));
    }

    @Override
    public int[] getNamedParameterLocations(String name) throws QueryException {
        Object o = this.namedParameters.get(name);
        if (o == null) {
            throw new QueryException("Named parameter does not appear in Query: " + name, this.queryTranslatorImpl.getQueryString());
        }
        if (o instanceof Integer) {
            return new int[]{(Integer)o};
        }
        return ArrayHelper.toIntArray((ArrayList)o);
    }

    public void addQuerySpaces(Serializable[] spaces) {
        this.querySpaces.addAll(Arrays.asList(spaces));
    }

    public Type[] getReturnTypes() {
        return this.selectClause.getQueryReturnTypes();
    }

    public String[] getReturnAliases() {
        return this.selectClause.getQueryReturnAliases();
    }

    public SelectClause getSelectClause() {
        return this.selectClause;
    }

    public FromClause getFinalFromClause() {
        FromClause top = this.currentFromClause;
        while (top.getParentFromClause() != null) {
            top = top.getParentFromClause();
        }
        return top;
    }

    public boolean isShallowQuery() {
        return this.getStatementType() == 30 || this.queryTranslatorImpl.isShallowQuery();
    }

    public Map getEnabledFilters() {
        return this.queryTranslatorImpl.getEnabledFilters();
    }

    public LiteralProcessor getLiteralProcessor() {
        return this.literalProcessor;
    }

    public ASTPrinter getASTPrinter() {
        return TokenPrinters.SQL_TOKEN_PRINTER;
    }

    public ArrayList<ParameterSpecification> getParameterSpecs() {
        return this.parameterSpecs;
    }

    public int getNumberOfParametersInSetClause() {
        return this.numberOfParametersInSetClause;
    }

    @Override
    protected void evaluateAssignment(AST eq) throws SemanticException {
        this.prepareLogicOperator(eq);
        Queryable persister = this.getCurrentFromClause().getFromElement().getQueryable();
        this.evaluateAssignment(eq, persister, -1);
    }

    private void evaluateAssignment(AST eq, Queryable persister, int targetIndex) {
        if (persister.isMultiTable()) {
            AssignmentSpecification specification = new AssignmentSpecification(eq, persister);
            if (targetIndex >= 0) {
                this.assignmentSpecifications.add(targetIndex, specification);
            } else {
                this.assignmentSpecifications.add(specification);
            }
            this.numberOfParametersInSetClause += specification.getParameters().length;
        }
    }

    public ArrayList<AssignmentSpecification> getAssignmentSpecifications() {
        return this.assignmentSpecifications;
    }

    @Override
    protected AST createIntoClause(String path, AST propertySpec) throws SemanticException {
        Queryable persister = (Queryable)this.getSessionFactoryHelper().requireClassPersister(path);
        IntoClause intoClause = (IntoClause)this.getASTFactory().create(31, persister.getEntityName());
        intoClause.setFirstChild(propertySpec);
        intoClause.initialize(persister);
        this.addQuerySpaces(persister.getQuerySpaces());
        return intoClause;
    }

    @Override
    protected void prepareVersioned(AST updateNode, AST versioned) throws SemanticException {
        UpdateStatement updateStatement = (UpdateStatement)updateNode;
        FromClause fromClause = updateStatement.getFromClause();
        if (versioned != null) {
            Queryable persister = fromClause.getFromElement().getQueryable();
            if (!persister.isVersioned()) {
                throw new SemanticException("increment option specified for update of non-versioned entity");
            }
            VersionType versionType = persister.getVersionType();
            if (versionType instanceof UserVersionType) {
                throw new SemanticException("user-defined version types not supported for increment option");
            }
            AST eq = this.getASTFactory().create(108, "=");
            AST versionPropertyNode = this.generateVersionPropertyNode(persister);
            eq.setFirstChild(versionPropertyNode);
            AST versionIncrementNode = null;
            if (this.isTimestampBasedVersion(versionType)) {
                versionIncrementNode = this.getASTFactory().create(132, "?");
                VersionTypeSeedParameterSpecification paramSpec = new VersionTypeSeedParameterSpecification(versionType);
                ((ParameterNode)versionIncrementNode).setHqlParameterSpecification(paramSpec);
                this.parameterSpecs.add(0, paramSpec);
            } else {
                versionIncrementNode = this.getASTFactory().create(122, "+");
                versionIncrementNode.setFirstChild(this.generateVersionPropertyNode(persister));
                versionIncrementNode.addChild(this.getASTFactory().create(111, "1"));
            }
            eq.addChild(versionIncrementNode);
            this.evaluateAssignment(eq, persister, 0);
            AST setClause = updateStatement.getSetClause();
            AST currentFirstSetElement = setClause.getFirstChild();
            setClause.setFirstChild(eq);
            eq.setNextSibling(currentFirstSetElement);
        }
    }

    private boolean isTimestampBasedVersion(VersionType versionType) {
        Class javaType = versionType.getReturnedClass();
        return Date.class.isAssignableFrom(javaType) || Calendar.class.isAssignableFrom(javaType);
    }

    private AST generateVersionPropertyNode(Queryable persister) throws SemanticException {
        String versionPropertyName = persister.getPropertyNames()[persister.getVersionProperty()];
        AST versionPropertyRef = this.getASTFactory().create(111, versionPropertyName);
        AST versionPropertyNode = this.lookupNonQualifiedProperty(versionPropertyRef);
        this.resolve(versionPropertyNode);
        return versionPropertyNode;
    }

    @Override
    protected void prepareLogicOperator(AST operator) throws SemanticException {
        ((OperatorNode)operator).initialize();
    }

    @Override
    protected void prepareArithmeticOperator(AST operator) throws SemanticException {
        ((OperatorNode)operator).initialize();
    }

    @Override
    protected void validateMapPropertyExpression(AST node) throws SemanticException {
        try {
            FromReferenceNode fromReferenceNode = (FromReferenceNode)node;
            QueryableCollection collectionPersister = fromReferenceNode.getFromElement().getQueryableCollection();
            if (!Map.class.isAssignableFrom(collectionPersister.getCollectionType().getReturnedClass())) {
                throw new SemanticException("node did not reference a map");
            }
        }
        catch (SemanticException se) {
            throw se;
        }
        catch (Throwable t) {
            throw new SemanticException("node did not reference a map");
        }
    }

    public Set<String> getTreatAsDeclarationsByPath(String path) {
        return this.hqlParser.getTreatMap().get(path);
    }

    public Dialect getDialect() {
        return this.sessionFactoryHelper.getFactory().getFastSessionServices().dialect;
    }

    public static void panic() {
        throw new QueryException("TreeWalker: panic");
    }

    private static class WithClauseVisitor
    implements NodeTraverser.VisitationStrategy {
        private final FromElement joinFragment;
        private final QueryTranslatorImpl queryTranslatorImpl;
        private FromElement referencedFromElement;
        private String joinAlias;

        public WithClauseVisitor(FromElement fromElement, QueryTranslatorImpl queryTranslatorImpl) {
            this.joinFragment = fromElement;
            this.queryTranslatorImpl = queryTranslatorImpl;
        }

        @Override
        public void visit(AST node) {
            if (node instanceof DotNode) {
                DotNode dotNode = (DotNode)node;
                FromElement fromElement = dotNode.getFromElement();
                if (this.referencedFromElement == null) {
                    this.referencedFromElement = fromElement;
                    if (fromElement != null) {
                        this.joinAlias = this.extractAppliedAlias(dotNode);
                    }
                }
            } else if (node instanceof ParameterNode) {
                this.applyParameterSpecification(((ParameterNode)node).getHqlParameterSpecification());
            } else if (node instanceof ParameterContainer) {
                this.applyParameterSpecifications((ParameterContainer)node);
            }
        }

        private void applyParameterSpecifications(ParameterContainer parameterContainer) {
            if (parameterContainer.hasEmbeddedParameters()) {
                ParameterSpecification[] specs;
                for (ParameterSpecification spec : specs = parameterContainer.getEmbeddedParameters()) {
                    this.applyParameterSpecification(spec);
                }
            }
        }

        private void applyParameterSpecification(ParameterSpecification paramSpec) {
            this.joinFragment.addEmbeddedParameter(paramSpec);
        }

        private String extractAppliedAlias(DotNode dotNode) {
            return dotNode.getText().substring(0, dotNode.getText().indexOf(46));
        }

        public FromElement getReferencedFromElement() {
            return this.referencedFromElement;
        }

        public String getJoinAlias() {
            return this.joinAlias;
        }
    }
}

