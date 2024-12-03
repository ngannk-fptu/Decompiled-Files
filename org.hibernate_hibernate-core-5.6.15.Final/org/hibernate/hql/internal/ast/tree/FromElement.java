/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.QueryException;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.hql.internal.CollectionProperties;
import org.hibernate.hql.internal.ast.TypeDiscriminatorMetadata;
import org.hibernate.hql.internal.ast.tree.CollectionPropertyReference;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElementType;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.tree.ParameterContainer;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.param.DynamicFilterParameterSpecification;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.DiscriminatorMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class FromElement
extends HqlSqlWalkerNode
implements DisplayableNode,
ParameterContainer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(FromElement.class);
    private String className;
    private String classAlias;
    private String tableAlias;
    private String collectionTableAlias;
    private FromClause fromClause;
    private boolean includeSubclasses = true;
    private boolean collectionJoin;
    private FromElement origin;
    private String[] columns;
    private String role;
    private boolean fetch;
    private boolean isAllPropertyFetch;
    private boolean filter;
    private int sequence = -1;
    private boolean useFromFragment;
    private boolean initialized;
    private FromElementType elementType;
    private boolean useWhereFragment = true;
    private List<FromElement> destinations;
    private boolean manyToMany;
    private AST withClauseAst;
    private String withClauseFragment;
    private boolean dereferencedBySuperclassProperty;
    private boolean dereferencedBySubclassProperty;
    public static final String DISCRIMINATOR_PROPERTY_NAME = "class";
    private TypeDiscriminatorMetadata typeDiscriminatorMetadata;
    private List<ParameterSpecification> embeddedParameters = new ArrayList<ParameterSpecification>();

    public FromElement() {
    }

    protected FromElement(FromClause fromClause, FromElement origin, String alias) {
        this.fromClause = fromClause;
        this.origin = origin;
        this.classAlias = alias;
        this.tableAlias = origin.getTableAlias();
        super.initialize(fromClause.getWalker());
    }

    public FromElementType getElementType() {
        return this.elementType;
    }

    protected void initializeComponentJoin(FromElementType elementType) {
        this.fromClause.registerFromElement(this);
        elementType.applyTreatAsDeclarations(this.getWalker().getTreatAsDeclarationsByPath(this.classAlias));
        this.elementType = elementType;
        this.initialized = true;
    }

    public String getCollectionSuffix() {
        return this.elementType.getCollectionSuffix();
    }

    public void setCollectionSuffix(String suffix) {
        this.elementType.setCollectionSuffix(suffix);
    }

    public void initializeCollection(FromClause fromClause, String classAlias, String tableAlias) {
        this.doInitialize(fromClause, tableAlias, null, classAlias, null, null);
        this.initialized = true;
    }

    public void initializeEntity(FromClause fromClause, String className, EntityPersister persister, EntityType type, String classAlias, String tableAlias) {
        this.doInitialize(fromClause, tableAlias, className, classAlias, persister, type);
        this.sequence = fromClause.nextFromElementCounter();
        this.initialized = true;
    }

    protected void doInitialize(FromClause fromClause, String tableAlias, String className, String classAlias, EntityPersister persister, EntityType type) {
        if (this.initialized) {
            throw new IllegalStateException("Already initialized!!");
        }
        this.fromClause = fromClause;
        this.tableAlias = tableAlias;
        this.className = className;
        this.classAlias = classAlias;
        this.elementType = new FromElementType(this, persister, type);
        fromClause.registerFromElement(this);
        LOG.debugf("%s : %s (%s) -> %s", new Object[]{fromClause, className, classAlias == null ? "<no alias>" : classAlias, tableAlias});
    }

    public EntityPersister getEntityPersister() {
        return this.elementType.getEntityPersister();
    }

    @Override
    public Type getDataType() {
        return this.elementType.getDataType();
    }

    public Type getSelectType() {
        return this.elementType.getSelectType();
    }

    public Queryable getQueryable() {
        return this.elementType.getQueryable();
    }

    public String getClassName() {
        return this.className;
    }

    public String getClassAlias() {
        return this.classAlias;
    }

    public String getTableName() {
        Queryable queryable = this.getQueryable();
        return queryable != null ? queryable.getTableName() : "{none}";
    }

    public String getTableAlias() {
        return this.tableAlias;
    }

    String renderScalarIdentifierSelect(int i) {
        return this.elementType.renderScalarIdentifierSelect(i);
    }

    void checkInitialized() {
        if (!this.initialized) {
            throw new IllegalStateException("FromElement has not been initialized!");
        }
    }

    String renderIdentifierSelect(int size, int k) {
        return this.elementType.renderIdentifierSelect(size, k);
    }

    String renderPropertySelect(int size, int k) {
        return this.elementType.renderPropertySelect(size, k, this.isAllPropertyFetch);
    }

    public String renderMapKeyPropertySelectFragment(int size, int k) {
        return this.elementType.renderMapKeyPropertySelectFragment(size, k);
    }

    public String renderMapEntryPropertySelectFragment(int size, int k) {
        return this.elementType.renderMapEntryPropertySelectFragment(size, k);
    }

    String renderCollectionSelectFragment(int size, int k) {
        return this.elementType.renderCollectionSelectFragment(size, k);
    }

    String renderValueCollectionSelectFragment(int size, int k) {
        return this.elementType.renderValueCollectionSelectFragment(size, k);
    }

    public FromClause getFromClause() {
        return this.fromClause;
    }

    public boolean isImplied() {
        return false;
    }

    @Override
    public String getDisplayText() {
        StringBuilder buf = new StringBuilder();
        buf.append("FromElement{");
        this.appendDisplayText(buf);
        buf.append("}");
        return buf.toString();
    }

    protected void appendDisplayText(StringBuilder buf) {
        buf.append(this.isImplied() ? (this.isImpliedInFromClause() ? "implied in FROM clause" : "implied") : "explicit");
        buf.append(",").append(this.isCollectionJoin() ? "collection join" : "not a collection join");
        buf.append(",").append(this.fetch ? "fetch join" : "not a fetch join");
        buf.append(",").append(this.isAllPropertyFetch ? "fetch all properties" : "fetch non-lazy properties");
        buf.append(",classAlias=").append(this.getClassAlias());
        buf.append(",role=").append(this.role);
        buf.append(",tableName=").append(this.getTableName());
        buf.append(",tableAlias=").append(this.getTableAlias());
        FromElement origin = this.getRealOrigin();
        buf.append(",origin=").append(origin == null ? "null" : origin.getText());
        buf.append(",columns={");
        if (this.columns != null) {
            for (int i = 0; i < this.columns.length; ++i) {
                buf.append(this.columns[i]);
                if (i >= this.columns.length - 1) continue;
                buf.append(" ");
            }
        }
        buf.append(",className=").append(this.className);
        buf.append("}");
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void setJoinSequence(JoinSequence joinSequence) {
        this.elementType.setJoinSequence(joinSequence);
    }

    public JoinSequence getJoinSequence() {
        return this.elementType.getJoinSequence();
    }

    public void setIncludeSubclasses(boolean includeSubclasses) {
        if (!includeSubclasses && this.isDereferencedBySuperclassOrSubclassProperty() && LOG.isTraceEnabled()) {
            LOG.trace("Attempt to disable subclass-inclusions : ", new Exception("Stack-trace source"));
        }
        this.includeSubclasses = includeSubclasses;
    }

    public boolean isIncludeSubclasses() {
        return this.includeSubclasses;
    }

    public boolean isDereferencedBySuperclassOrSubclassProperty() {
        return this.dereferencedBySubclassProperty || this.dereferencedBySuperclassProperty;
    }

    public String getIdentityColumn() {
        CharSequence[] cols = this.getIdentityColumns();
        if (cols.length == 1) {
            return cols[0];
        }
        return "(" + String.join((CharSequence)", ", cols) + ")";
    }

    public String[] getIdentityColumns() {
        this.checkInitialized();
        String table = this.getTableAlias();
        if (table == null) {
            throw new IllegalStateException("No table alias for node " + this);
        }
        String[] propertyNames = this.getIdentifierPropertyNames();
        ArrayList<String> columns = new ArrayList<String>();
        boolean inSelect = this.getWalker().getStatementType() == 46;
        for (String propertyName : propertyNames) {
            String[] propertyNameColumns;
            for (String propertyNameColumn : propertyNameColumns = this.toColumns(table, propertyName, inSelect)) {
                columns.add(propertyNameColumn);
            }
        }
        return columns.toArray(new String[columns.size()]);
    }

    public void setCollectionJoin(boolean collectionJoin) {
        this.collectionJoin = collectionJoin;
    }

    public boolean isCollectionJoin() {
        return this.collectionJoin;
    }

    public void setRole(String role) {
        this.role = role;
        this.applyTreatAsDeclarations(this.getWalker().getTreatAsDeclarationsByPath(role));
    }

    public void applyTreatAsDeclarations(Set<String> treatAsDeclarationsByPath) {
        this.elementType.applyTreatAsDeclarations(treatAsDeclarationsByPath);
    }

    public String getRole() {
        return this.role;
    }

    public void setQueryableCollection(QueryableCollection queryableCollection) {
        this.elementType.setQueryableCollection(queryableCollection);
    }

    public QueryableCollection getQueryableCollection() {
        return this.elementType.getQueryableCollection();
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public void setOrigin(FromElement origin, boolean manyToMany) {
        this.origin = origin;
        this.manyToMany = manyToMany;
        origin.addDestination(this);
        if (origin.getFromClause() == this.getFromClause()) {
            if (manyToMany) {
                ASTUtil.appendSibling((AST)origin, (AST)this);
            } else if (!(this.getWalker().isInFrom() || this.getWalker().isInSelect() || this.getWalker().isInEntityGraph())) {
                this.getFromClause().addChild((AST)this);
            } else {
                origin.addChild((AST)this);
            }
        } else if (!this.getWalker().isInFrom()) {
            this.getFromClause().addChild((AST)this);
        }
    }

    public boolean isManyToMany() {
        return this.manyToMany;
    }

    private void addDestination(FromElement fromElement) {
        if (this.destinations == null) {
            this.destinations = new LinkedList<FromElement>();
        }
        this.destinations.add(fromElement);
    }

    public List<FromElement> getDestinations() {
        if (this.destinations == null) {
            return Collections.emptyList();
        }
        return this.destinations;
    }

    public FromElement getOrigin() {
        return this.origin;
    }

    public FromElement getRealOrigin() {
        if (this.origin == null) {
            return null;
        }
        if (StringHelper.isEmpty(this.origin.getText())) {
            return this.origin.getRealOrigin();
        }
        return this.origin;
    }

    public FromElement getFetchOrigin() {
        if (this.origin == null) {
            return null;
        }
        if (!this.origin.isFetch()) {
            return this.origin;
        }
        if (StringHelper.isEmpty(this.origin.getText())) {
            return this.origin.getFetchOrigin();
        }
        return this.origin;
    }

    public boolean isNonQualifiedPropertyRef(String identifier) {
        return this.elementType.isNonQualifiedPropertyRef(identifier);
    }

    public TypeDiscriminatorMetadata getTypeDiscriminatorMetadata() {
        if (this.typeDiscriminatorMetadata == null) {
            this.typeDiscriminatorMetadata = this.buildTypeDiscriminatorMetadata();
        }
        return this.typeDiscriminatorMetadata;
    }

    private TypeDiscriminatorMetadata buildTypeDiscriminatorMetadata() {
        String aliasToUse = this.getTableAlias();
        Queryable queryable = this.getQueryable();
        if (queryable == null) {
            QueryableCollection collection = this.getQueryableCollection();
            if (!collection.getElementType().isEntityType()) {
                throw new QueryException("type discrimination cannot be applied to value collection [" + collection.getRole() + "]");
            }
            queryable = (Queryable)collection.getElementPersister();
        }
        this.handlePropertyBeingDereferenced(this.getDataType(), DISCRIMINATOR_PROPERTY_NAME);
        return new TypeDiscriminatorMetadataImpl(queryable.getTypeDiscriminatorMetadata(), aliasToUse);
    }

    public Type getPropertyType(String propertyName, String propertyPath) {
        return this.elementType.getPropertyType(propertyName, propertyPath);
    }

    public String getPropertyTableName(String propertyName) {
        return this.elementType.getPropertyTableName(propertyName);
    }

    public String[] toColumns(String tableAlias, String path, boolean inSelect) {
        return this.elementType.toColumns(tableAlias, path, inSelect);
    }

    public String[] toColumns(String tableAlias, String path, boolean inSelect, boolean forceAlias) {
        return this.elementType.toColumns(tableAlias, path, inSelect, forceAlias);
    }

    public PropertyMapping getPropertyMapping(String propertyName) {
        return this.elementType.getPropertyMapping(propertyName);
    }

    public CollectionPropertyReference getCollectionPropertyReference(String propertyName) {
        return this.elementType.getCollectionPropertyReference(propertyName);
    }

    public String[] getIdentifierPropertyNames() {
        return this.elementType.getIdentifierPropertyNames();
    }

    public void setFetch(boolean fetch) {
        this.fetch = fetch;
        if (fetch && this.getWalker().isShallowQuery()) {
            throw new QueryException("fetch may not be used with scroll() or iterate()");
        }
    }

    public boolean isFetch() {
        return this.fetch;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setFilter(boolean b) {
        this.filter = b;
    }

    public boolean isFilter() {
        return this.filter;
    }

    public boolean useFromFragment() {
        this.checkInitialized();
        return !this.isImplied() || this.useFromFragment;
    }

    public void setUseFromFragment(boolean useFromFragment) {
        this.useFromFragment = useFromFragment;
    }

    public boolean useWhereFragment() {
        return this.useWhereFragment;
    }

    public void setUseWhereFragment(boolean b) {
        this.useWhereFragment = b;
    }

    public void setCollectionTableAlias(String collectionTableAlias) {
        this.collectionTableAlias = collectionTableAlias;
    }

    public String getCollectionTableAlias() {
        return this.collectionTableAlias;
    }

    public boolean isCollectionOfValuesOrComponents() {
        return this.elementType.isCollectionOfValuesOrComponents();
    }

    public boolean isEntity() {
        return this.elementType.isEntity();
    }

    public void setImpliedInFromClause(boolean flag) {
        throw new UnsupportedOperationException("Explicit FROM elements can't be implied in the FROM clause!");
    }

    public boolean isImpliedInFromClause() {
        return false;
    }

    public void setInProjectionList(boolean inProjectionList) {
    }

    public boolean inProjectionList() {
        return !this.isImplied() && this.isFromOrJoinFragment();
    }

    public boolean isFromOrJoinFragment() {
        return this.getType() == 141 || this.getType() == 143 || this.getType() == 144;
    }

    public boolean isAllPropertyFetch() {
        return this.isAllPropertyFetch;
    }

    public void setAllPropertyFetch(boolean fetch) {
        this.isAllPropertyFetch = fetch;
    }

    public AST getWithClauseAst() {
        return this.withClauseAst;
    }

    public String getWithClauseFragment() {
        return this.withClauseFragment;
    }

    public void setWithClauseFragment(AST ast, String withClauseFragment) {
        this.getFromClause().moveFromElementToEnd(this);
        this.withClauseAst = ast;
        this.withClauseFragment = withClauseFragment;
    }

    public void handlePropertyBeingDereferenced(Type propertySource, String propertyName) {
        if (this.getQueryableCollection() != null && CollectionProperties.isCollectionProperty(propertyName)) {
            return;
        }
        if (propertySource.isComponentType()) {
            return;
        }
        Queryable persister = this.getQueryable();
        if (persister != null) {
            try {
                Queryable.Declarer propertyDeclarer = persister.getSubclassPropertyDeclarer(propertyName);
                if (LOG.isTraceEnabled()) {
                    LOG.tracev("Handling property dereference [{0} ({1}) -> {2} ({3})]", new Object[]{persister.getEntityName(), this.getClassAlias(), propertyName, propertyDeclarer});
                }
                if (propertyDeclarer == Queryable.Declarer.SUBCLASS) {
                    this.dereferencedBySubclassProperty = true;
                    this.includeSubclasses = true;
                } else if (propertyDeclarer == Queryable.Declarer.SUPERCLASS) {
                    this.dereferencedBySuperclassProperty = true;
                }
            }
            catch (QueryException queryException) {
                // empty catch block
            }
        }
    }

    public boolean isDereferencedBySuperclassProperty() {
        return this.dereferencedBySuperclassProperty;
    }

    public boolean isDereferencedBySubclassProperty() {
        return this.dereferencedBySubclassProperty;
    }

    @Override
    public void addEmbeddedParameter(ParameterSpecification specification) {
        this.embeddedParameters.add(specification);
    }

    @Override
    public boolean hasEmbeddedParameters() {
        return !this.embeddedParameters.isEmpty();
    }

    @Override
    public ParameterSpecification[] getEmbeddedParameters() {
        List<ParameterSpecification> parameterSpecification = this.getParameterSpecification();
        return parameterSpecification.toArray(new ParameterSpecification[parameterSpecification.size()]);
    }

    private List<ParameterSpecification> getParameterSpecification() {
        List<ParameterSpecification> parameterSpecifications = this.embeddedParameters.stream().filter(o -> o instanceof DynamicFilterParameterSpecification).collect(Collectors.toList());
        parameterSpecifications.addAll(this.embeddedParameters.stream().filter(o -> !(o instanceof DynamicFilterParameterSpecification)).collect(Collectors.toList()));
        return parameterSpecifications;
    }

    public ParameterSpecification getIndexCollectionSelectorParamSpec() {
        return this.elementType.getIndexCollectionSelectorParamSpec();
    }

    public void setIndexCollectionSelectorParamSpec(ParameterSpecification indexCollectionSelectorParamSpec) {
        if (indexCollectionSelectorParamSpec == null) {
            if (this.elementType.getIndexCollectionSelectorParamSpec() != null) {
                this.embeddedParameters.remove(this.elementType.getIndexCollectionSelectorParamSpec());
                this.elementType.setIndexCollectionSelectorParamSpec(null);
            }
        } else {
            this.elementType.setIndexCollectionSelectorParamSpec(indexCollectionSelectorParamSpec);
            this.addEmbeddedParameter(indexCollectionSelectorParamSpec);
        }
    }

    private static class TypeDiscriminatorMetadataImpl
    implements TypeDiscriminatorMetadata {
        private final DiscriminatorMetadata persisterDiscriminatorMetadata;
        private final String alias;

        private TypeDiscriminatorMetadataImpl(DiscriminatorMetadata persisterDiscriminatorMetadata, String alias) {
            this.persisterDiscriminatorMetadata = persisterDiscriminatorMetadata;
            this.alias = alias;
        }

        @Override
        public String getSqlFragment() {
            return this.persisterDiscriminatorMetadata.getSqlFragment(this.alias);
        }

        @Override
        public Type getResolutionType() {
            return this.persisterDiscriminatorMetadata.getResolutionType();
        }
    }
}

