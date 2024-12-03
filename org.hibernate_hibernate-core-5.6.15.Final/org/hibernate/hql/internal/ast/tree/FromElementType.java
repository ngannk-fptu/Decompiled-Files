/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.hql.internal.CollectionProperties;
import org.hibernate.hql.internal.CollectionSubqueryFactory;
import org.hibernate.hql.internal.NameGenerator;
import org.hibernate.hql.internal.ast.tree.CollectionPropertyReference;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.internal.AliasConstantsHelper;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.collection.CollectionPropertyMapping;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

class FromElementType {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(FromElementType.class);
    private FromElement fromElement;
    private EntityType entityType;
    private EntityPersister persister;
    private QueryableCollection queryableCollection;
    private CollectionPropertyMapping collectionPropertyMapping;
    private JoinSequence joinSequence;
    private String collectionSuffix;
    private ParameterSpecification indexCollectionSelectorParamSpec;
    private Set<String> treatAsDeclarations;
    private static final List SPECIAL_MANY2MANY_TREATMENT_FUNCTION_NAMES = Arrays.asList("index", "minIndex", "maxIndex");

    public FromElementType(FromElement fromElement, EntityPersister persister, EntityType entityType) {
        this.fromElement = fromElement;
        this.persister = persister;
        this.entityType = entityType;
        if (persister != null) {
            fromElement.setText(((Queryable)persister).getTableName() + " " + this.getTableAlias());
        }
    }

    protected FromElementType(FromElement fromElement) {
        this.fromElement = fromElement;
    }

    private String getTableAlias() {
        return this.fromElement.getTableAlias();
    }

    private String getCollectionTableAlias() {
        return this.fromElement.getCollectionTableAlias();
    }

    public String getCollectionSuffix() {
        return this.collectionSuffix;
    }

    public void setCollectionSuffix(String suffix) {
        this.collectionSuffix = suffix;
    }

    public EntityPersister getEntityPersister() {
        return this.persister;
    }

    public Type getDataType() {
        if (this.persister == null) {
            if (this.queryableCollection == null) {
                return null;
            }
            return this.queryableCollection.getType();
        }
        return this.entityType;
    }

    public Type getSelectType() {
        if (this.entityType == null) {
            return null;
        }
        boolean shallow = this.fromElement.getFromClause().getWalker().isShallowQuery();
        return this.fromElement.getSessionFactoryHelper().getFactory().getTypeResolver().getTypeFactory().manyToOne(this.entityType.getAssociatedEntityName(), shallow);
    }

    public Queryable getQueryable() {
        return this.persister instanceof Queryable ? (Queryable)this.persister : null;
    }

    String renderScalarIdentifierSelect(int i) {
        this.checkInitialized();
        String[] idPropertyName = this.getIdentifierPropertyNames();
        StringBuilder buf = new StringBuilder();
        int counter = 0;
        for (int j = 0; j < idPropertyName.length; ++j) {
            String propertyName = idPropertyName[j];
            String[] toColumns = this.getPropertyMapping(propertyName).toColumns(this.getTableAlias(), propertyName);
            int h = 0;
            while (h < toColumns.length) {
                String column = toColumns[h];
                if (j + h > 0) {
                    buf.append(", ");
                }
                buf.append(column).append(" as ").append(NameGenerator.scalarName(i, counter));
                ++h;
                ++counter;
            }
        }
        LOG.debug("Rendered scalar ID select column(s): " + buf);
        return buf.toString();
    }

    String renderIdentifierSelect(int size, int k) {
        this.checkInitialized();
        if (this.fromElement.getFromClause().isSubQuery()) {
            String[] idColumnNames = this.persister != null ? ((Queryable)this.persister).getIdentifierColumnNames() : new String[]{};
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < idColumnNames.length; ++i) {
                buf.append(this.fromElement.getTableAlias()).append('.').append(idColumnNames[i]);
                if (i == idColumnNames.length - 1) continue;
                buf.append(", ");
            }
            return buf.toString();
        }
        if (this.persister == null) {
            throw new QueryException("not an entity");
        }
        String fragment = ((Queryable)this.persister).identifierSelectFragment(this.getTableAlias(), this.getSuffix(size, k));
        return FromElementType.trimLeadingCommaAndSpaces(fragment);
    }

    private String getSuffix(int size, int sequence) {
        return FromElementType.generateSuffix(size, sequence);
    }

    private static String generateSuffix(int size, int k) {
        return size == 1 ? "" : AliasConstantsHelper.get(k);
    }

    private void checkInitialized() {
        this.fromElement.checkInitialized();
    }

    String renderPropertySelect(int size, int k, boolean allProperties) {
        this.checkInitialized();
        if (this.persister == null) {
            return "";
        }
        String fragment = ((Queryable)this.persister).propertySelectFragment(this.getTableAlias(), this.getSuffix(size, k), allProperties);
        return FromElementType.trimLeadingCommaAndSpaces(fragment);
    }

    public String renderMapKeyPropertySelectFragment(int size, int k) {
        if (this.persister == null) {
            throw new IllegalStateException("Unexpected state in call to renderMapKeyPropertySelectFragment");
        }
        String fragment = ((Queryable)this.persister).propertySelectFragment(this.getTableAlias(), this.getSuffix(size, k), false);
        return FromElementType.trimLeadingCommaAndSpaces(fragment);
    }

    public String renderMapEntryPropertySelectFragment(int size, int k) {
        return null;
    }

    String renderCollectionSelectFragment(int size, int k) {
        if (this.queryableCollection == null) {
            return "";
        }
        if (this.collectionSuffix == null) {
            this.collectionSuffix = FromElementType.generateSuffix(size, k);
        }
        String fragment = this.queryableCollection.selectFragment(this.getCollectionTableAlias(), this.collectionSuffix);
        return FromElementType.trimLeadingCommaAndSpaces(fragment);
    }

    public String renderValueCollectionSelectFragment(int size, int k) {
        if (this.queryableCollection == null) {
            return "";
        }
        if (this.collectionSuffix == null) {
            this.collectionSuffix = FromElementType.generateSuffix(size, k);
        }
        String fragment = this.queryableCollection.selectFragment(this.getTableAlias(), this.collectionSuffix);
        return FromElementType.trimLeadingCommaAndSpaces(fragment);
    }

    private static String trimLeadingCommaAndSpaces(String fragment) {
        if (fragment.length() > 0 && fragment.charAt(0) == ',') {
            fragment = fragment.substring(1);
        }
        fragment = fragment.trim();
        return fragment.trim();
    }

    public void setJoinSequence(JoinSequence joinSequence) {
        this.joinSequence = joinSequence;
        joinSequence.applyTreatAsDeclarations(this.treatAsDeclarations);
    }

    public JoinSequence getJoinSequence() {
        if (this.joinSequence != null) {
            return this.joinSequence;
        }
        if (this.persister instanceof Joinable) {
            Joinable joinable = (Joinable)((Object)this.persister);
            JoinSequence joinSequence = this.fromElement.getSessionFactoryHelper().createJoinSequence().setRoot(joinable, this.getTableAlias());
            joinSequence.applyTreatAsDeclarations(this.treatAsDeclarations);
            return joinSequence;
        }
        return null;
    }

    public void applyTreatAsDeclarations(Set<String> treatAsDeclarations) {
        if (treatAsDeclarations != null && !treatAsDeclarations.isEmpty()) {
            if (this.treatAsDeclarations == null) {
                this.treatAsDeclarations = new HashSet<String>();
            }
            for (String treatAsSubclassName : treatAsDeclarations) {
                try {
                    EntityPersister subclassPersister = this.fromElement.getSessionFactoryHelper().requireClassPersister(treatAsSubclassName);
                    this.treatAsDeclarations.add(subclassPersister.getEntityName());
                }
                catch (SemanticException e) {
                    throw new QueryException("Unable to locate persister for subclass named in TREAT-AS : " + treatAsSubclassName);
                }
            }
            if (this.joinSequence != null) {
                this.joinSequence.applyTreatAsDeclarations(this.treatAsDeclarations);
            }
        }
    }

    public void setQueryableCollection(QueryableCollection queryableCollection) {
        if (this.queryableCollection != null) {
            throw new IllegalStateException("QueryableCollection is already defined for " + this + "!");
        }
        this.queryableCollection = queryableCollection;
        if (!queryableCollection.isOneToMany()) {
            this.fromElement.setText(queryableCollection.getTableName() + " " + this.getTableAlias());
        }
    }

    public QueryableCollection getQueryableCollection() {
        return this.queryableCollection;
    }

    public String getPropertyTableName(String propertyName) {
        this.checkInitialized();
        if (this.persister != null) {
            AbstractEntityPersister aep = (AbstractEntityPersister)this.persister;
            try {
                return aep.getSubclassTableName(aep.getSubclassPropertyTableNumber(propertyName));
            }
            catch (QueryException e) {
                return null;
            }
        }
        return null;
    }

    public Type getPropertyType(String propertyName, String propertyPath) {
        this.checkInitialized();
        Type type = null;
        if (this.persister != null && propertyName.equals(propertyPath) && propertyName.equals(this.persister.getIdentifierPropertyName())) {
            type = this.persister.getIdentifierType();
        } else {
            PropertyMapping mapping = this.getPropertyMapping(propertyName);
            type = mapping.toType(propertyPath);
        }
        if (type == null) {
            throw new MappingException("Property " + propertyName + " does not exist in " + (this.queryableCollection == null ? "class" : "collection") + " " + (this.queryableCollection == null ? this.fromElement.getClassName() : this.queryableCollection.getRole()));
        }
        return type;
    }

    String[] toColumns(String tableAlias, String path, boolean inSelect) {
        return this.toColumns(tableAlias, path, inSelect, false);
    }

    String[] toColumns(String tableAlias, String path, boolean inSelect, boolean forceAlias) {
        this.checkInitialized();
        PropertyMapping propertyMapping = this.getPropertyMapping(path);
        if (!inSelect && this.queryableCollection != null && CollectionProperties.isCollectionProperty(path) && this.persister != propertyMapping) {
            return this.getCollectionPropertyReference(path).toColumns(tableAlias);
        }
        if (forceAlias) {
            return propertyMapping.toColumns(tableAlias, path);
        }
        if (this.fromElement.getWalker().getStatementType() == 46) {
            return propertyMapping.toColumns(tableAlias, path);
        }
        if (this.fromElement.getWalker().isSubQuery()) {
            if (this.isCorrelation()) {
                if (this.isMultiTable() && (!this.isUpdateQuery() || this.inWhereClause())) {
                    return propertyMapping.toColumns(tableAlias, path);
                }
                if (this.isInsertQuery()) {
                    return propertyMapping.toColumns(tableAlias, path);
                }
                return propertyMapping.toColumns(this.extractTableName(), path);
            }
            return propertyMapping.toColumns(tableAlias, path);
        }
        if (this.fromElement.getWalker().getCurrentTopLevelClauseType() == 46) {
            return propertyMapping.toColumns(tableAlias, path);
        }
        if (this.isManipulationQuery() && this.isMultiTable() && this.inWhereClause()) {
            return propertyMapping.toColumns(tableAlias, path);
        }
        Object[] columns = propertyMapping.toColumns(path);
        LOG.tracev("Using non-qualified column reference [{0} -> ({1})]", path, ArrayHelper.toString(columns));
        return columns;
    }

    private boolean isCorrelation() {
        FromClause top = this.fromElement.getWalker().getFinalFromClause();
        return this.fromElement.getFromClause() != this.fromElement.getWalker().getCurrentFromClause() && this.fromElement.getFromClause() == top;
    }

    private boolean isMultiTable() {
        return this.fromElement.getQueryable() != null && this.fromElement.getQueryable().isMultiTable();
    }

    private String extractTableName() {
        return this.fromElement.getQueryable().getTableName();
    }

    private boolean isInsertQuery() {
        return this.fromElement.getWalker().getStatementType() == 30;
    }

    private boolean isUpdateQuery() {
        return this.fromElement.getWalker().getStatementType() == 51;
    }

    private boolean isManipulationQuery() {
        return this.fromElement.getWalker().getStatementType() == 51 || this.fromElement.getWalker().getStatementType() == 13;
    }

    private boolean inWhereClause() {
        return this.fromElement.getWalker().getCurrentTopLevelClauseType() == 53;
    }

    PropertyMapping getPropertyMapping(String propertyName) {
        this.checkInitialized();
        if (this.queryableCollection == null) {
            return (PropertyMapping)((Object)this.persister);
        }
        if (this.queryableCollection.isManyToMany() && this.queryableCollection.hasIndex() && SPECIAL_MANY2MANY_TREATMENT_FUNCTION_NAMES.contains(propertyName)) {
            return new SpecialManyToManyCollectionPropertyMapping();
        }
        if (CollectionProperties.isCollectionProperty(propertyName)) {
            if (this.collectionPropertyMapping == null) {
                if (this.persister != null) {
                    try {
                        if (this.persister.getPropertyType(propertyName) != null) {
                            return (PropertyMapping)((Object)this.persister);
                        }
                    }
                    catch (QueryException queryException) {
                        // empty catch block
                    }
                }
                this.collectionPropertyMapping = new CollectionPropertyMapping(this.queryableCollection);
            }
            return this.collectionPropertyMapping;
        }
        if (this.queryableCollection.getElementType().isAnyType()) {
            return this.queryableCollection;
        }
        if (this.queryableCollection.getElementType().isComponentType() && propertyName.equals("id")) {
            return (PropertyMapping)((Object)this.queryableCollection.getOwnerEntityPersister());
        }
        return this.queryableCollection;
    }

    public boolean isCollectionOfValuesOrComponents() {
        return this.persister == null && this.queryableCollection != null && !this.queryableCollection.getElementType().isEntityType();
    }

    public boolean isEntity() {
        return this.persister != null;
    }

    public ParameterSpecification getIndexCollectionSelectorParamSpec() {
        return this.indexCollectionSelectorParamSpec;
    }

    public void setIndexCollectionSelectorParamSpec(ParameterSpecification indexCollectionSelectorParamSpec) {
        this.indexCollectionSelectorParamSpec = indexCollectionSelectorParamSpec;
    }

    public CollectionPropertyReference getCollectionPropertyReference(final String propertyName) {
        PropertyMapping collectionPropertyMapping;
        if (this.queryableCollection == null) {
            throw new QueryException("Not a collection reference");
        }
        if (this.queryableCollection.isManyToMany() && this.queryableCollection.hasIndex() && SPECIAL_MANY2MANY_TREATMENT_FUNCTION_NAMES.contains(propertyName)) {
            collectionPropertyMapping = new SpecialManyToManyCollectionPropertyMapping();
        } else if (CollectionProperties.isCollectionProperty(propertyName)) {
            if (this.collectionPropertyMapping == null) {
                this.collectionPropertyMapping = new CollectionPropertyMapping(this.queryableCollection);
            }
            collectionPropertyMapping = this.collectionPropertyMapping;
        } else {
            collectionPropertyMapping = this.queryableCollection;
        }
        return new CollectionPropertyReference(){

            @Override
            public Type getType() {
                return collectionPropertyMapping.toType(propertyName);
            }

            @Override
            public String[] toColumns(String tableAlias) {
                if (propertyName.equalsIgnoreCase("index")) {
                    return collectionPropertyMapping.toColumns(tableAlias, propertyName);
                }
                Map enabledFilters = FromElementType.this.fromElement.getWalker().getEnabledFilters();
                String subquery = CollectionSubqueryFactory.createCollectionSubquery(FromElementType.this.joinSequence.copyForCollectionProperty().setUseThetaStyle(true), enabledFilters, collectionPropertyMapping.toColumns(tableAlias, propertyName));
                LOG.debugf("toColumns(%s,%s) : subquery = %s", tableAlias, propertyName, subquery);
                return new String[]{"(" + subquery + ")"};
            }
        };
    }

    public boolean isNonQualifiedPropertyRef(String identifier) {
        if (this.queryableCollection == null) {
            assert (this.persister != null);
            try {
                return this.persister.getPropertyType(identifier) != null;
            }
            catch (QueryException qe) {
                return false;
            }
        }
        return false;
    }

    public String[] getIdentifierPropertyNames() {
        if (this.getEntityPersister() != null) {
            String identifierPropertyName = this.getEntityPersister().getIdentifierPropertyName();
            if (identifierPropertyName != null) {
                return new String[]{identifierPropertyName};
            }
            IdentifierProperty identifierProperty = this.getEntityPersister().getEntityMetamodel().getIdentifierProperty();
            if (identifierProperty.hasIdentifierMapper() && !identifierProperty.isEmbedded()) {
                return new String[]{"_identifierMapper"};
            }
            if (EmbeddedComponentType.class.isInstance(identifierProperty.getType())) {
                return ((EmbeddedComponentType)identifierProperty.getType()).getPropertyNames();
            }
        }
        return new String[]{"id"};
    }

    private class SpecialManyToManyCollectionPropertyMapping
    implements PropertyMapping {
        private SpecialManyToManyCollectionPropertyMapping() {
        }

        @Override
        public Type getType() {
            return FromElementType.this.queryableCollection.getCollectionType();
        }

        private void validate(String propertyName) {
            if (!("index".equals(propertyName) || "maxIndex".equals(propertyName) || "minIndex".equals(propertyName))) {
                throw new IllegalArgumentException("Expecting index-related function call");
            }
        }

        @Override
        public Type toType(String propertyName) throws QueryException {
            this.validate(propertyName);
            return FromElementType.this.queryableCollection.getIndexType();
        }

        @Override
        public String[] toColumns(String alias, String propertyName) throws QueryException {
            this.validate(propertyName);
            String joinTableAlias = FromElementType.this.joinSequence.getFirstJoin().getAlias();
            if ("index".equals(propertyName)) {
                return FromElementType.this.queryableCollection.toColumns(joinTableAlias, propertyName);
            }
            String[] cols = FromElementType.this.queryableCollection.getIndexColumnNames(joinTableAlias);
            if ("minIndex".equals(propertyName)) {
                if (cols.length != 1) {
                    throw new QueryException("composite collection index in minIndex()");
                }
                return new String[]{"min(" + cols[0] + ')'};
            }
            if (cols.length != 1) {
                throw new QueryException("composite collection index in maxIndex()");
            }
            return new String[]{"max(" + cols[0] + ')'};
        }

        @Override
        public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
            this.validate(propertyName);
            return FromElementType.this.queryableCollection.toColumns(propertyName);
        }
    }
}

