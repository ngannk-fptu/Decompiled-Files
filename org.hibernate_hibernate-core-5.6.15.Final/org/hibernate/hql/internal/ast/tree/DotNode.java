/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import org.hibernate.QueryException;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.hql.internal.CollectionProperties;
import org.hibernate.hql.internal.ast.tree.ComponentJoin;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromElementFactory;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.ImpliedFromElement;
import org.hibernate.hql.internal.ast.tree.IndexNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.tree.TableReferenceNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.plan.spi.EntityQuerySpace;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.JoinType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class DotNode
extends FromReferenceNode
implements DisplayableNode,
SelectExpression,
TableReferenceNode {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DotNode.class);
    public static boolean useThetaStyleImplicitJoins;
    public static boolean regressionStyleJoinSuppression;
    public static final IllegalCollectionDereferenceExceptionBuilder DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
    public static IllegalCollectionDereferenceExceptionBuilder ILLEGAL_COLL_DEREF_EXCP_BUILDER;
    private String propertyName;
    private String path;
    private String propertyPath;
    private String[] columns;
    private JoinType joinType = JoinType.INNER_JOIN;
    private boolean fetch;
    private DereferenceType dereferenceType = DereferenceType.UNKNOWN;
    private FromElement impliedJoin;

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    private String[] getColumns() throws QueryException {
        if (this.columns == null) {
            String tableAlias = this.getLhs().getFromElement().getTableAlias();
            this.columns = this.getFromElement().toColumns(tableAlias, this.propertyPath, false);
        }
        return this.columns;
    }

    @Override
    public String getDisplayText() {
        StringBuilder buf = new StringBuilder();
        FromElement fromElement = this.getFromElement();
        buf.append("{propertyName=").append(this.propertyName);
        buf.append(",dereferenceType=").append(this.dereferenceType.name());
        buf.append(",getPropertyPath=").append(this.propertyPath);
        buf.append(",path=").append(this.getPath());
        if (fromElement != null) {
            buf.append(",tableAlias=").append(fromElement.getTableAlias());
            buf.append(",className=").append(fromElement.getClassName());
            buf.append(",classAlias=").append(fromElement.getClassAlias());
        } else {
            buf.append(",no from element");
        }
        buf.append('}');
        return buf.toString();
    }

    @Override
    public void resolveFirstChild() throws SemanticException {
        String propName;
        FromReferenceNode lhs = (FromReferenceNode)this.getFirstChild();
        SqlNode property = (SqlNode)lhs.getNextSibling();
        this.propertyName = propName = property.getText();
        if (this.propertyPath == null) {
            this.propertyPath = propName;
        }
        lhs.resolve(true, true, null, (AST)this);
        this.setFromElement(lhs.getFromElement());
        this.checkSubclassOrSuperclassPropertyReference(lhs, propName);
    }

    @Override
    public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
        if (this.isResolved()) {
            return;
        }
        Type propertyType = this.prepareLhs();
        if (propertyType != null && propertyType.isCollectionType()) {
            this.resolveIndex(null);
        } else {
            this.resolveFirstChild();
            super.resolve(generateJoin, implicitJoin);
        }
    }

    @Override
    public void resolveIndex(AST parent) throws SemanticException {
        if (this.isResolved()) {
            return;
        }
        Type propertyType = this.prepareLhs();
        this.dereferenceCollection((CollectionType)propertyType, true, true, null, parent);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) throws SemanticException {
        if (this.isResolved()) {
            return;
        }
        Type propertyType = this.prepareLhs();
        if (parent == null && "class".equals(this.propertyName)) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfClassEntityTypeSelector(this.getLhs().getPath());
        }
        if (propertyType == null) {
            if (parent == null) {
                this.getWalker().getLiteralProcessor().lookupConstant(this);
            }
            return;
        }
        if (propertyType.isComponentType()) {
            this.checkLhsIsNotCollection();
            this.dereferenceComponent(parent);
            this.initText();
        } else if (propertyType.isEntityType()) {
            this.checkLhsIsNotCollection();
            this.dereferenceEntity((EntityType)propertyType, implicitJoin, classAlias, generateJoin, parent, parentPredicate);
            this.initText();
        } else if (propertyType.isCollectionType()) {
            this.checkLhsIsNotCollection();
            this.dereferenceCollection((CollectionType)propertyType, implicitJoin, false, classAlias, parent);
        } else {
            if (!CollectionProperties.isAnyCollectionProperty(this.propertyName)) {
                this.checkLhsIsNotCollection();
            }
            this.dereferenceType = DereferenceType.PRIMITIVE;
            this.initText();
        }
        this.setResolved();
    }

    private void initText() {
        boolean countDistinct;
        CharSequence[] cols = this.getColumns();
        String text = String.join((CharSequence)", ", cols);
        boolean bl = countDistinct = this.getWalker().isInCountDistinct() && this.getWalker().getSessionFactoryHelper().getFactory().getDialect().requiresParensForTupleDistinctCounts();
        if (cols.length > 1 && (this.getWalker().isComparativeExpressionClause() || countDistinct || this.getWalker().getCurrentClauseType() == 47)) {
            text = "(" + text + ")";
        }
        this.setText(text);
    }

    private Type prepareLhs() throws SemanticException {
        FromReferenceNode lhs = this.getLhs();
        lhs.prepareForDot(this.propertyName);
        return this.getDataType();
    }

    private void dereferenceCollection(CollectionType collectionType, boolean implicitJoin, boolean indexed, String classAlias, AST parent) throws SemanticException {
        EntityPersister entityPersister;
        FromElement lhsFromElement;
        boolean isSizeProperty;
        this.dereferenceType = DereferenceType.COLLECTION;
        String role = collectionType.getRole();
        boolean bl = isSizeProperty = this.getNextSibling() != null && CollectionProperties.isAnyCollectionProperty(this.getNextSibling().getText());
        if (isSizeProperty) {
            indexed = true;
        }
        QueryableCollection queryableCollection = this.getSessionFactoryHelper().requireQueryableCollection(role);
        String propName = this.getPath();
        FromClause currentFromClause = this.getWalker().getCurrentFromClause();
        for (lhsFromElement = this.getLhs().getFromElement(); lhsFromElement != null && ComponentJoin.class.isInstance(lhsFromElement); lhsFromElement = lhsFromElement.getOrigin()) {
        }
        if (lhsFromElement == null) {
            throw new QueryException("Unable to locate appropriate lhs");
        }
        if (this.getWalker().getStatementType() != 46 && this.isFromElementUpdateOrDeleteRoot(lhsFromElement)) {
            Queryable persister;
            boolean useAlias = false;
            if (this.getWalker().getStatementType() != 30 && (persister = lhsFromElement.getQueryable()).isMultiTable()) {
                useAlias = true;
            }
            if (!useAlias) {
                String lhsTableName = lhsFromElement.getQueryable().getTableName();
                this.columns = this.getFromElement().toColumns(lhsTableName, this.propertyPath, false, true);
            }
        }
        FromElementFactory factory = new FromElementFactory(currentFromClause, lhsFromElement, propName, classAlias, this.getColumns(), implicitJoin);
        FromElement elem = factory.createCollection(queryableCollection, role, this.joinType, this.fetch, indexed);
        LOG.debugf("dereferenceCollection() : Created new FROM element for %s : %s", propName, elem);
        this.setImpliedJoin(elem);
        this.setFromElement(elem);
        if (isSizeProperty) {
            elem.setText("");
            elem.setUseWhereFragment(false);
        }
        if (!implicitJoin && (entityPersister = elem.getEntityPersister()) != null) {
            this.getWalker().addQuerySpaces(entityPersister.getQuerySpaces());
        }
        this.getWalker().addQuerySpaces(queryableCollection.getCollectionSpaces());
    }

    private void dereferenceEntity(EntityType toOneType, boolean implicitJoin, String classAlias, boolean generateJoin, AST parent, AST parentPredicate) throws SemanticException {
        boolean joinIsNeeded;
        this.checkForCorrelatedSubquery("dereferenceEntity");
        DotNode parentAsDotNode = null;
        String property = this.propertyName;
        if (DotNode.isDotNode(parent)) {
            parentAsDotNode = (DotNode)parent;
            property = parentAsDotNode.propertyName;
            joinIsNeeded = generateJoin ? (implicitJoin && (toOneType.hasNotFoundAction() || toOneType.isNullable()) ? true : !this.isPropertyEmbeddedInJoinProperties(parentAsDotNode.propertyName)) : false;
        } else if (!this.getWalker().isSelectStatement()) {
            joinIsNeeded = this.getWalker().getCurrentStatementType() == 46 && this.getWalker().isInFrom();
        } else if (regressionStyleJoinSuppression) {
            joinIsNeeded = generateJoin && (!this.getWalker().isInSelect() || !this.getWalker().isShallowQuery());
        } else if (parentPredicate != null) {
            joinIsNeeded = generateJoin;
        } else {
            boolean bl = joinIsNeeded = generateJoin || this.getWalker().isInSelect() || this.getWalker().isInFrom() || implicitJoin && this.getWalker().isInSize();
        }
        if (joinIsNeeded) {
            this.dereferenceEntityJoin(classAlias, toOneType, implicitJoin, parent);
        } else {
            this.dereferenceEntityIdentifier(property, parentAsDotNode);
        }
    }

    private static boolean isDotNode(AST n) {
        return n != null && n.getType() == 15;
    }

    private void dereferenceEntityJoin(String classAlias, EntityType toOneType, boolean isImpliedJoin, AST parent) throws SemanticException {
        boolean useFoundFromElement;
        FromClause currentFromClause;
        FromElement elem;
        this.dereferenceType = DereferenceType.ENTITY;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("dereferenceEntityJoin() : generating join for %s in %s (%s) parent = %s", new Object[]{this.propertyName, this.getFromElement().getClassName(), classAlias == null ? "<no alias>" : classAlias, ASTUtil.getDebugString(parent)});
        }
        String associatedEntityName = toOneType.getAssociatedEntityName();
        String tableAlias = this.getAliasGenerator().createName(associatedEntityName);
        String[] joinColumns = this.getColumns();
        String joinPath = this.getPath();
        if (isImpliedJoin && this.getWalker().isInFrom()) {
            this.joinType = this.getWalker().getImpliedJoinType();
        }
        boolean found = (elem = (currentFromClause = this.getWalker().getCurrentFromClause()).findJoinByPath(joinPath)) != null;
        boolean bl = useFoundFromElement = found && this.canReuse(classAlias, elem);
        if (!useFoundFromElement) {
            JoinSequence joinSequence;
            FromElement lhsFromElement;
            for (lhsFromElement = this.getLhs().getFromElement(); lhsFromElement != null && ComponentJoin.class.isInstance(lhsFromElement); lhsFromElement = lhsFromElement.getOrigin()) {
            }
            if (lhsFromElement == null) {
                throw new QueryException("Unable to locate appropriate lhs");
            }
            String role = lhsFromElement.getClassName() + "." + this.propertyName;
            if (joinColumns.length == 0 && lhsFromElement instanceof EntityQuerySpace) {
                String lhsTableAlias = this.getLhs().getFromElement().getTableAlias();
                AbstractEntityPersister persister = (AbstractEntityPersister)lhsFromElement.getEntityPersister();
                String[][] polyJoinColumns = persister.getPolymorphicJoinColumns(lhsTableAlias, this.propertyPath);
                joinSequence = this.getSessionFactoryHelper().createJoinSequence(isImpliedJoin, (AssociationType)toOneType, tableAlias, this.joinType, polyJoinColumns);
            } else {
                joinSequence = this.getSessionFactoryHelper().createJoinSequence(isImpliedJoin, (AssociationType)toOneType, tableAlias, this.joinType, joinColumns);
            }
            FromElementFactory factory = new FromElementFactory(currentFromClause, lhsFromElement, joinPath, classAlias, joinColumns, isImpliedJoin);
            elem = factory.createEntityJoin(associatedEntityName, tableAlias, joinSequence, this.fetch, this.getWalker().isInFrom(), toOneType, role, joinPath);
            if (isImpliedJoin && toOneType.hasNotFoundAction() && !this.getWalker().isSubQuery()) {
                assert (elem instanceof ImpliedFromElement);
                ImpliedFromElement impliedJoin = (ImpliedFromElement)elem;
                impliedJoin.forceNotFoundFetch();
            }
        } else {
            currentFromClause.addDuplicateAlias(classAlias, elem);
        }
        this.setImpliedJoin(elem);
        this.getWalker().addQuerySpaces(elem.getEntityPersister().getQuerySpaces());
        this.setFromElement(elem);
    }

    private boolean canReuse(String classAlias, FromElement fromElement) {
        if (fromElement.getFromClause() == this.getWalker().getCurrentFromClause() && this.areSame(classAlias, fromElement.getClassAlias())) {
            return true;
        }
        return this.getWalker().getCurrentClauseType() != 23;
    }

    private boolean areSame(String alias1, String alias2) {
        return !StringHelper.isEmpty(alias1) && !StringHelper.isEmpty(alias2) && alias1.equals(alias2);
    }

    private void setImpliedJoin(FromElement elem) {
        DotNode dotLhs;
        this.impliedJoin = elem;
        if (this.getFirstChild().getType() == 15 && (dotLhs = (DotNode)this.getFirstChild()).getImpliedJoin() != null) {
            this.impliedJoin = dotLhs.getImpliedJoin();
        }
    }

    @Override
    public FromElement getImpliedJoin() {
        return this.impliedJoin;
    }

    private boolean isPropertyEmbeddedInJoinProperties(String propertyName) {
        String propertyPath = String.join((CharSequence)".", this.propertyPath, propertyName);
        try {
            Type propertyType = this.getFromElement().getPropertyType(this.propertyPath, propertyPath);
            return propertyType != null;
        }
        catch (QueryException e) {
            return false;
        }
    }

    private void checkForCorrelatedSubquery(String methodName) {
        if (this.isCorrelatedSubselect()) {
            LOG.debugf("%s() : correlated subquery", methodName);
        }
    }

    private boolean isCorrelatedSubselect() {
        return this.getWalker().isSubQuery() && this.getFromElement().getFromClause() != this.getWalker().getCurrentFromClause();
    }

    private void checkLhsIsNotCollection() throws SemanticException {
        if (this.getLhs().getDataType() != null && this.getLhs().getDataType().isCollectionType()) {
            throw ILLEGAL_COLL_DEREF_EXCP_BUILDER.buildIllegalCollectionDereferenceException(this.propertyName, this.getLhs());
        }
    }

    private void dereferenceComponent(AST parent) {
        this.dereferenceType = DereferenceType.COMPONENT;
        this.setPropertyNameAndPath(parent);
    }

    private void dereferenceEntityIdentifier(String propertyName, DotNode dotParent) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("dereferenceShortcut() : property %s in %s does not require a join.", propertyName, this.getFromElement().getClassName());
        }
        this.setPropertyNameAndPath((AST)dotParent);
        this.initText();
        if (dotParent != null) {
            dotParent.dereferenceType = DereferenceType.IDENTIFIER;
            dotParent.setText(this.getText());
            dotParent.columns = this.getColumns();
        }
    }

    private void setPropertyNameAndPath(AST parent) {
        if (DotNode.isDotNode(parent)) {
            DotNode dotNode = (DotNode)parent;
            AST lhs = dotNode.getFirstChild();
            AST rhs = lhs.getNextSibling();
            this.propertyName = rhs.getText();
            dotNode.propertyPath = this.propertyPath = this.propertyPath + "." + this.propertyName;
            LOG.debugf("Unresolved property path is now '%s'", dotNode.propertyPath);
        } else {
            LOG.debugf("Terminal getPropertyPath = [%s]", this.propertyPath);
        }
    }

    @Override
    public Type getDataType() {
        if (super.getDataType() == null) {
            FromElement fromElement = this.getLhs().getFromElement();
            if (fromElement == null) {
                return null;
            }
            Type propertyType = fromElement.getPropertyType(this.propertyPath, this.propertyPath);
            LOG.debugf("getDataType() : %s -> %s", this.propertyPath, propertyType);
            super.setDataType(propertyType);
        }
        return super.getDataType();
    }

    @Override
    public String[] getReferencedTables() {
        FromElement fromElement;
        FromReferenceNode lhs = (FromReferenceNode)this.getFirstChild();
        if (lhs != null && (fromElement = lhs.getFromElement()) != null) {
            String propertyTableName = fromElement.getPropertyTableName(this.propertyPath);
            return new String[]{propertyTableName};
        }
        return null;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public String getPropertyPath() {
        return this.propertyPath;
    }

    public FromReferenceNode getLhs() {
        FromReferenceNode lhs = (FromReferenceNode)this.getFirstChild();
        if (lhs == null) {
            throw new IllegalStateException("DOT node with no left-hand-side!");
        }
        return lhs;
    }

    @Override
    public String getPath() {
        if (this.path == null) {
            FromReferenceNode lhs = this.getLhs();
            if (lhs == null) {
                this.path = this.getText();
            } else {
                SqlNode rhs = (SqlNode)lhs.getNextSibling();
                this.path = lhs.getPath() + "." + rhs.getOriginalText();
            }
        }
        return this.path;
    }

    public void setFetch(boolean fetch) {
        this.fetch = fetch;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        String[] sqlColumns = this.getColumns();
        ColumnHelper.generateScalarColumns(this, sqlColumns, i);
    }

    public void resolveSelectExpression() throws SemanticException {
        if (this.getWalker().isShallowQuery() || this.getWalker().getCurrentFromClause().isSubQuery()) {
            this.resolve(false, true);
        } else {
            this.resolve(true, false);
            Type type = this.getDataType();
            if (type.isEntityType()) {
                FromElement fromElement = this.getFromElement();
                fromElement.setIncludeSubclasses(true);
                if (useThetaStyleImplicitJoins) {
                    fromElement.getJoinSequence().setUseThetaStyle(true);
                    FromElement origin = fromElement.getOrigin();
                    if (origin != null) {
                        ASTUtil.makeSiblingOfParent((AST)origin, (AST)fromElement);
                    }
                }
            }
        }
        for (FromReferenceNode lhs = this.getLhs(); lhs != null; lhs = (FromReferenceNode)lhs.getFirstChild()) {
            this.checkSubclassOrSuperclassPropertyReference(lhs, lhs.getNextSibling().getText());
        }
    }

    public void setResolvedConstant(String text) {
        this.path = text;
        this.dereferenceType = DereferenceType.JAVA_CONSTANT;
        this.setResolved();
    }

    private boolean checkSubclassOrSuperclassPropertyReference(FromReferenceNode lhs, String propertyName) {
        FromElement source;
        if (lhs != null && !(lhs instanceof IndexNode) && (source = lhs.getFromElement()) != null) {
            source.handlePropertyBeingDereferenced(lhs.getDataType(), propertyName);
        }
        return false;
    }

    static {
        ILLEGAL_COLL_DEREF_EXCP_BUILDER = DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER = new IllegalCollectionDereferenceExceptionBuilder(){

            @Override
            public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
                String lhsPath = ASTUtil.getPathText((AST)lhs);
                return new QueryException("illegal attempt to dereference collection [" + lhsPath + "] with element property reference [" + propertyName + "]");
            }
        };
    }

    public static enum DereferenceType {
        UNKNOWN,
        ENTITY,
        COMPONENT,
        COLLECTION,
        PRIMITIVE,
        IDENTIFIER,
        JAVA_CONSTANT;

    }

    public static interface IllegalCollectionDereferenceExceptionBuilder {
        public QueryException buildIllegalCollectionDereferenceException(String var1, FromReferenceNode var2);
    }
}

