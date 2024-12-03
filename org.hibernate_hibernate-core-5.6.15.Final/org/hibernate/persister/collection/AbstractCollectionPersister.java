/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.persister.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.FetchMode;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.TransientObjectException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.SubselectFetch;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.internal.FilterHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.List;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.CompositeElementPropertyMapping;
import org.hibernate.persister.collection.ElementPropertyMapping;
import org.hibernate.persister.collection.NamedQueryCollectionInitializer;
import org.hibernate.persister.collection.SQLLoadableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.persister.walking.internal.CompositionSingularSubAttributesHelper;
import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CollectionElementDefinition;
import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Alias;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Insert;
import org.hibernate.sql.SelectFragment;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.sql.Template;
import org.hibernate.sql.Update;
import org.hibernate.sql.ordering.antlr.ColumnMapper;
import org.hibernate.sql.ordering.antlr.ColumnReference;
import org.hibernate.sql.ordering.antlr.FormulaReference;
import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
import org.hibernate.sql.ordering.antlr.OrderByTranslation;
import org.hibernate.sql.ordering.antlr.SqlValueReference;
import org.hibernate.type.AnyType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public abstract class AbstractCollectionPersister
implements CollectionMetadata,
SQLLoadableCollection {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)AbstractCollectionPersister.class.getName());
    private final NavigableRole navigableRole;
    private final String sqlDeleteString;
    private final String sqlInsertRowString;
    private final String sqlUpdateRowString;
    private final String sqlDeleteRowString;
    private final String sqlSelectSizeString;
    private final String sqlSelectRowByIndexString;
    private final String sqlDetectRowByIndexString;
    private final String sqlDetectRowByElementString;
    protected final boolean hasWhere;
    protected final String sqlWhereString;
    private final String sqlWhereStringTemplate;
    private final boolean hasOrder;
    private final OrderByTranslation orderByTranslation;
    private final boolean hasManyToManyOrder;
    private final OrderByTranslation manyToManyOrderByTranslation;
    private final int baseIndex;
    private String mappedByProperty;
    protected final boolean indexContainsFormula;
    protected final boolean elementIsPureFormula;
    private final Type keyType;
    private final Type indexType;
    protected final Type elementType;
    private final Type identifierType;
    protected final String[] keyColumnNames;
    protected final String[] indexColumnNames;
    protected final String[] indexFormulaTemplates;
    protected final String[] indexFormulas;
    protected final boolean[] indexColumnIsGettable;
    protected final boolean[] indexColumnIsSettable;
    protected final String[] elementColumnNames;
    protected final String[] elementColumnWriters;
    protected final String[] elementColumnReaders;
    protected final String[] elementColumnReaderTemplates;
    protected final String[] elementFormulaTemplates;
    protected final String[] elementFormulas;
    protected final boolean[] elementColumnIsGettable;
    protected final boolean[] elementColumnIsSettable;
    protected final boolean[] elementColumnIsInPrimaryKey;
    protected final String[] indexColumnAliases;
    protected final String[] elementColumnAliases;
    protected final String[] keyColumnAliases;
    protected final String identifierColumnName;
    private final String identifierColumnAlias;
    protected final String qualifiedTableName;
    private final String queryLoaderName;
    private final boolean isPrimitiveArray;
    private final boolean isArray;
    protected final boolean hasIndex;
    protected final boolean hasIdentifier;
    private final boolean isLazy;
    private final boolean isExtraLazy;
    protected final boolean isInverse;
    private final boolean isMutable;
    private final boolean isVersioned;
    protected final int batchSize;
    private final FetchMode fetchMode;
    private final boolean hasOrphanDelete;
    private final boolean subselectLoadable;
    private final Class elementClass;
    private final String entityName;
    private final Dialect dialect;
    protected final SqlExceptionHelper sqlExceptionHelper;
    private final SessionFactoryImplementor factory;
    private final EntityPersister ownerPersister;
    private final IdentifierGenerator identifierGenerator;
    private final PropertyMapping elementPropertyMapping;
    private final EntityPersister elementPersister;
    private final CollectionDataAccess cacheAccessStrategy;
    private final CollectionType collectionType;
    private CollectionInitializer initializer;
    private final CacheEntryStructure cacheEntryStructure;
    private final FilterHelper filterHelper;
    private final FilterHelper manyToManyFilterHelper;
    private final String manyToManyWhereString;
    private final String manyToManyWhereTemplate;
    private final boolean insertCallable;
    private final boolean updateCallable;
    private final boolean deleteCallable;
    private final boolean deleteAllCallable;
    private ExecuteUpdateResultCheckStyle insertCheckStyle;
    private ExecuteUpdateResultCheckStyle updateCheckStyle;
    private ExecuteUpdateResultCheckStyle deleteCheckStyle;
    private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
    private final Serializable[] spaces;
    private Map collectionPropertyColumnAliases = new HashMap();
    private BasicBatchKey removeBatchKey;
    protected BasicBatchKey recreateBatchKey;
    private BasicBatchKey deleteBatchKey;
    private BasicBatchKey insertBatchKey;
    private String[] indexFragments;

    public AbstractCollectionPersister(Collection collectionBinding, CollectionDataAccess cacheAccessStrategy, PersisterCreationContext creationContext) throws MappingException, CacheException {
        Database database = creationContext.getMetadata().getDatabase();
        this.factory = creationContext.getSessionFactory();
        this.cacheAccessStrategy = cacheAccessStrategy;
        this.cacheEntryStructure = this.factory.getSessionFactoryOptions().isStructuredCacheEntriesEnabled() ? (collectionBinding.isMap() ? StructuredMapCacheEntry.INSTANCE : StructuredCollectionCacheEntry.INSTANCE) : UnstructuredCacheEntry.INSTANCE;
        this.dialect = this.factory.getDialect();
        this.sqlExceptionHelper = this.factory.getSQLExceptionHelper();
        this.collectionType = collectionBinding.getCollectionType();
        this.navigableRole = new NavigableRole(collectionBinding.getRole());
        this.entityName = collectionBinding.getOwnerEntityName();
        this.ownerPersister = this.factory.getEntityPersister(this.entityName);
        this.queryLoaderName = collectionBinding.getLoaderName();
        this.isMutable = collectionBinding.isMutable();
        this.mappedByProperty = collectionBinding.getMappedByProperty();
        Table table = collectionBinding.getCollectionTable();
        this.fetchMode = collectionBinding.getElement().getFetchMode();
        this.elementType = collectionBinding.getElement().getType();
        this.isPrimitiveArray = collectionBinding.isPrimitiveArray();
        this.isArray = collectionBinding.isArray();
        this.subselectLoadable = collectionBinding.isSubselectLoadable();
        this.qualifiedTableName = this.determineTableName(table);
        int spacesSize = 1 + collectionBinding.getSynchronizedTables().size();
        this.spaces = new String[spacesSize];
        this.spaces[0] = this.qualifiedTableName;
        Iterator<Object> iter = collectionBinding.getSynchronizedTables().iterator();
        for (int i = 1; i < spacesSize; ++i) {
            this.spaces[i] = iter.next();
        }
        this.sqlWhereString = StringHelper.isNotEmpty(collectionBinding.getWhere()) ? "( " + collectionBinding.getWhere() + ") " : null;
        this.hasWhere = this.sqlWhereString != null;
        this.sqlWhereStringTemplate = this.hasWhere ? Template.renderWhereStringTemplate(this.sqlWhereString, this.dialect, this.factory.getSqlFunctionRegistry()) : null;
        this.hasOrphanDelete = collectionBinding.hasOrphanDelete();
        int batch = collectionBinding.getBatchSize();
        if (batch == -1) {
            batch = this.factory.getSessionFactoryOptions().getDefaultBatchFetchSize();
        }
        this.batchSize = batch;
        this.isVersioned = collectionBinding.isOptimisticLocked();
        this.keyType = collectionBinding.getKey().getType();
        iter = collectionBinding.getKey().getColumnIterator();
        int keySpan = collectionBinding.getKey().getColumnSpan();
        this.keyColumnNames = new String[keySpan];
        this.keyColumnAliases = new String[keySpan];
        int k = 0;
        while (iter.hasNext()) {
            Column col = (Column)iter.next();
            this.keyColumnNames[k] = col.getQuotedName(this.dialect);
            this.keyColumnAliases[k] = col.getAlias(this.dialect, table);
            ++k;
        }
        if (this.elementType.isEntityType()) {
            String entityName = ((EntityType)this.elementType).getAssociatedEntityName();
            this.elementPersister = this.factory.getEntityPersister(entityName);
        } else {
            this.elementPersister = null;
        }
        int elementSpan = collectionBinding.getElement().getColumnSpan();
        this.elementColumnAliases = new String[elementSpan];
        this.elementColumnNames = new String[elementSpan];
        this.elementColumnWriters = new String[elementSpan];
        this.elementColumnReaders = new String[elementSpan];
        this.elementColumnReaderTemplates = new String[elementSpan];
        this.elementFormulaTemplates = new String[elementSpan];
        this.elementFormulas = new String[elementSpan];
        this.elementColumnIsSettable = new boolean[elementSpan];
        this.elementColumnIsGettable = new boolean[elementSpan];
        this.elementColumnIsInPrimaryKey = new boolean[elementSpan];
        boolean isPureFormula = true;
        boolean hasNotNullableColumns = false;
        boolean oneToMany = collectionBinding.isOneToMany();
        boolean[] columnInsertability = null;
        if (!oneToMany) {
            columnInsertability = collectionBinding.getElement().getColumnInsertability();
        }
        int j = 0;
        iter = collectionBinding.getElement().getColumnIterator();
        while (iter.hasNext()) {
            Selectable selectable = (Selectable)iter.next();
            this.elementColumnAliases[j] = selectable.getAlias(this.dialect, table);
            if (selectable.isFormula()) {
                Formula form = (Formula)selectable;
                this.elementFormulaTemplates[j] = form.getTemplate(this.dialect, this.factory.getSqlFunctionRegistry());
                this.elementFormulas[j] = form.getFormula();
            } else {
                Column col = (Column)selectable;
                this.elementColumnNames[j] = col.getQuotedName(this.dialect);
                this.elementColumnWriters[j] = col.getWriteExpr();
                this.elementColumnReaders[j] = col.getReadExpr(this.dialect);
                this.elementColumnReaderTemplates[j] = col.getTemplate(this.dialect, this.factory.getSqlFunctionRegistry());
                this.elementColumnIsGettable[j] = true;
                this.elementColumnIsSettable[j] = this.elementType.isComponentType() ? columnInsertability[j] : true;
                boolean bl = this.elementColumnIsInPrimaryKey[j] = !col.isNullable();
                if (!col.isNullable()) {
                    hasNotNullableColumns = true;
                }
                isPureFormula = false;
            }
            ++j;
        }
        this.elementIsPureFormula = isPureFormula;
        if (!hasNotNullableColumns) {
            Arrays.fill(this.elementColumnIsInPrimaryKey, true);
        }
        this.hasIndex = collectionBinding.isIndexed();
        if (this.hasIndex) {
            IndexedCollection indexedCollection = (IndexedCollection)collectionBinding;
            this.indexType = indexedCollection.getIndex().getType();
            int indexSpan = indexedCollection.getIndex().getColumnSpan();
            boolean[] indexColumnInsertability = indexedCollection.getIndex().getColumnInsertability();
            boolean[] indexColumnUpdatability = indexedCollection.getIndex().getColumnUpdateability();
            iter = indexedCollection.getIndex().getColumnIterator();
            this.indexColumnNames = new String[indexSpan];
            this.indexFormulaTemplates = new String[indexSpan];
            this.indexFormulas = new String[indexSpan];
            this.indexColumnIsGettable = new boolean[indexSpan];
            this.indexColumnIsSettable = new boolean[indexSpan];
            this.indexColumnAliases = new String[indexSpan];
            int i = 0;
            boolean hasFormula = false;
            while (iter.hasNext()) {
                Selectable s = (Selectable)iter.next();
                this.indexColumnAliases[i] = s.getAlias(this.dialect);
                if (s.isFormula()) {
                    Formula indexForm = (Formula)s;
                    this.indexFormulaTemplates[i] = indexForm.getTemplate(this.dialect, this.factory.getSqlFunctionRegistry());
                    this.indexFormulas[i] = indexForm.getFormula();
                    hasFormula = true;
                } else {
                    Column indexCol = (Column)s;
                    this.indexColumnNames[i] = indexCol.getQuotedName(this.dialect);
                    this.indexColumnIsGettable[i] = true;
                    this.indexColumnIsSettable[i] = indexColumnInsertability[i] || indexColumnUpdatability[i];
                }
                ++i;
            }
            this.indexContainsFormula = hasFormula;
            this.baseIndex = indexedCollection.isList() ? ((List)indexedCollection).getBaseIndex() : 0;
        } else {
            this.indexContainsFormula = false;
            this.indexColumnIsGettable = null;
            this.indexColumnIsSettable = null;
            this.indexFormulaTemplates = null;
            this.indexFormulas = null;
            this.indexType = null;
            this.indexColumnNames = null;
            this.indexColumnAliases = null;
            this.baseIndex = 0;
        }
        this.hasIdentifier = collectionBinding.isIdentified();
        if (this.hasIdentifier) {
            if (collectionBinding.isOneToMany()) {
                throw new MappingException("one-to-many collections with identifiers are not supported");
            }
            IdentifierCollection idColl = (IdentifierCollection)collectionBinding;
            this.identifierType = idColl.getIdentifier().getType();
            iter = idColl.getIdentifier().getColumnIterator();
            Column col = (Column)iter.next();
            this.identifierColumnName = col.getQuotedName(this.dialect);
            this.identifierColumnAlias = col.getAlias(this.dialect);
            this.identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(creationContext.getMetadata().getIdentifierGeneratorFactory(), this.factory.getDialect(), null);
            this.identifierGenerator.initialize(creationContext.getSessionFactory().getSqlStringGenerationContext());
        } else {
            this.identifierType = null;
            this.identifierColumnName = null;
            this.identifierColumnAlias = null;
            this.identifierGenerator = null;
        }
        if (collectionBinding.getCustomSQLInsert() == null) {
            this.sqlInsertRowString = this.generateInsertRowString();
            this.insertCallable = false;
            this.insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
        } else {
            this.sqlInsertRowString = collectionBinding.getCustomSQLInsert();
            this.insertCallable = collectionBinding.isCustomInsertCallable();
            ExecuteUpdateResultCheckStyle executeUpdateResultCheckStyle = this.insertCheckStyle = collectionBinding.getCustomSQLInsertCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(collectionBinding.getCustomSQLInsert(), this.insertCallable) : collectionBinding.getCustomSQLInsertCheckStyle();
        }
        if (collectionBinding.getCustomSQLUpdate() == null) {
            this.sqlUpdateRowString = this.generateUpdateRowString();
            this.updateCallable = false;
            this.updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
        } else {
            this.sqlUpdateRowString = collectionBinding.getCustomSQLUpdate();
            this.updateCallable = collectionBinding.isCustomUpdateCallable();
            ExecuteUpdateResultCheckStyle executeUpdateResultCheckStyle = this.updateCheckStyle = collectionBinding.getCustomSQLUpdateCheckStyle() == null ? ExecuteUpdateResultCheckStyle.determineDefault(collectionBinding.getCustomSQLUpdate(), this.insertCallable) : collectionBinding.getCustomSQLUpdateCheckStyle();
        }
        if (collectionBinding.getCustomSQLDelete() == null) {
            this.sqlDeleteRowString = this.generateDeleteRowString();
            this.deleteCallable = false;
            this.deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
        } else {
            this.sqlDeleteRowString = collectionBinding.getCustomSQLDelete();
            this.deleteCallable = collectionBinding.isCustomDeleteCallable();
            this.deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
        }
        if (collectionBinding.getCustomSQLDeleteAll() == null) {
            this.sqlDeleteString = this.generateDeleteString();
            this.deleteAllCallable = false;
            this.deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
        } else {
            this.sqlDeleteString = collectionBinding.getCustomSQLDeleteAll();
            this.deleteAllCallable = collectionBinding.isCustomDeleteAllCallable();
            this.deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
        }
        this.sqlSelectSizeString = this.generateSelectSizeString(collectionBinding.isIndexed() && !collectionBinding.isMap());
        this.sqlDetectRowByIndexString = this.generateDetectRowByIndexString();
        this.sqlDetectRowByElementString = this.generateDetectRowByElementString();
        this.sqlSelectRowByIndexString = this.generateSelectRowByIndexString();
        this.logStaticSQL();
        this.isLazy = collectionBinding.isLazy();
        this.isExtraLazy = collectionBinding.isExtraLazy();
        this.isInverse = collectionBinding.isInverse();
        this.elementClass = collectionBinding.isArray() ? ((Array)collectionBinding).getElementClass() : null;
        this.elementPropertyMapping = this.elementType.isComponentType() ? new CompositeElementPropertyMapping(this.elementColumnNames, this.elementColumnReaders, this.elementColumnReaderTemplates, this.elementFormulaTemplates, (CompositeType)this.elementType, this.factory) : (!this.elementType.isEntityType() ? new ElementPropertyMapping(this.elementColumnNames, this.elementType) : (this.elementPersister instanceof PropertyMapping ? (PropertyMapping)((Object)this.elementPersister) : new ElementPropertyMapping(this.elementColumnNames, this.elementType)));
        boolean bl = this.hasOrder = collectionBinding.getOrderBy() != null;
        if (this.hasOrder) {
            LOG.debugf("Translating order-by fragment [%s] for collection role : %s", collectionBinding.getOrderBy(), this.getRole());
            this.orderByTranslation = Template.translateOrderBy(collectionBinding.getOrderBy(), new ColumnMapperImpl(), this.factory, this.dialect, this.factory.getSqlFunctionRegistry());
        } else {
            this.orderByTranslation = null;
        }
        this.filterHelper = new FilterHelper(collectionBinding.getFilters(), this.factory);
        this.manyToManyFilterHelper = new FilterHelper(collectionBinding.getManyToManyFilters(), this.factory);
        this.manyToManyWhereString = StringHelper.isNotEmpty(collectionBinding.getManyToManyWhere()) ? "( " + collectionBinding.getManyToManyWhere() + ")" : null;
        this.manyToManyWhereTemplate = this.manyToManyWhereString == null ? null : Template.renderWhereStringTemplate(this.manyToManyWhereString, this.factory.getDialect(), this.factory.getSqlFunctionRegistry());
        boolean bl2 = this.hasManyToManyOrder = collectionBinding.getManyToManyOrdering() != null;
        if (this.hasManyToManyOrder) {
            LOG.debugf("Translating many-to-many order-by fragment [%s] for collection role : %s", collectionBinding.getOrderBy(), this.getRole());
            this.manyToManyOrderByTranslation = Template.translateOrderBy(collectionBinding.getManyToManyOrdering(), new ColumnMapperImpl(), this.factory, this.dialect, this.factory.getSqlFunctionRegistry());
        } else {
            this.manyToManyOrderByTranslation = null;
        }
        this.initCollectionPropertyMap();
    }

    protected String determineTableName(Table table) {
        if (table.getSubselect() != null) {
            return "( " + table.getSubselect() + " )";
        }
        return this.factory.getSqlStringGenerationContext().format(table.getQualifiedTableName());
    }

    private String[] formulaTemplates(String reference, int expectedSize) {
        try {
            int propertyIndex = this.elementPersister.getEntityMetamodel().getPropertyIndex(reference);
            return ((Queryable)this.elementPersister).getSubclassPropertyFormulaTemplateClosure()[propertyIndex];
        }
        catch (Exception e) {
            return new String[expectedSize];
        }
    }

    @Override
    public void postInstantiate() throws MappingException {
        this.initializer = this.queryLoaderName == null ? this.createCollectionInitializer(LoadQueryInfluencers.NONE) : new NamedQueryCollectionInitializer(this.queryLoaderName, this);
    }

    protected void logStaticSQL() {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static SQL for collection: %s", this.getRole());
            if (this.getSQLInsertRowString() != null) {
                LOG.debugf(" Row insert: %s", this.getSQLInsertRowString());
            }
            if (this.getSQLUpdateRowString() != null) {
                LOG.debugf(" Row update: %s", this.getSQLUpdateRowString());
            }
            if (this.getSQLDeleteRowString() != null) {
                LOG.debugf(" Row delete: %s", this.getSQLDeleteRowString());
            }
            if (this.getSQLDeleteString() != null) {
                LOG.debugf(" One-shot delete: %s", this.getSQLDeleteString());
            }
        }
    }

    @Override
    public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
        this.getAppropriateInitializer(key, session).initialize(key, session);
    }

    protected CollectionInitializer getAppropriateInitializer(Serializable key, SharedSessionContractImplementor session) {
        if (this.queryLoaderName != null) {
            return this.initializer;
        }
        CollectionInitializer subselectInitializer = this.getSubselectInitializer(key, session);
        if (subselectInitializer != null) {
            return subselectInitializer;
        }
        if (!session.getLoadQueryInfluencers().hasEnabledFilters()) {
            return this.initializer;
        }
        return this.createCollectionInitializer(session.getLoadQueryInfluencers());
    }

    private CollectionInitializer getSubselectInitializer(Serializable key, SharedSessionContractImplementor session) {
        if (!this.isSubselectLoadable()) {
            return null;
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        SubselectFetch subselect = persistenceContext.getBatchFetchQueue().getSubselect(session.generateEntityKey(key, this.getOwnerEntityPersister()));
        if (subselect == null) {
            return null;
        }
        Iterator iter = subselect.getResult().iterator();
        while (iter.hasNext()) {
            if (persistenceContext.containsEntity((EntityKey)iter.next())) continue;
            iter.remove();
        }
        return this.createSubselectInitializer(subselect, session);
    }

    protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch var1, SharedSessionContractImplementor var2);

    protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers var1) throws MappingException;

    @Override
    public NavigableRole getNavigableRole() {
        return this.navigableRole;
    }

    @Override
    public CollectionDataAccess getCacheAccessStrategy() {
        return this.cacheAccessStrategy;
    }

    @Override
    public boolean hasCache() {
        return this.cacheAccessStrategy != null;
    }

    @Override
    public CollectionType getCollectionType() {
        return this.collectionType;
    }

    protected String getSQLWhereString(String alias) {
        return StringHelper.replace(this.sqlWhereStringTemplate, "$PlaceHolder$", alias);
    }

    @Override
    public String getSQLOrderByString(String alias) {
        return this.hasOrdering() ? this.orderByTranslation.injectAliases(new StandardOrderByAliasResolver(alias)) : "";
    }

    @Override
    public String getManyToManyOrderByString(String alias) {
        return this.hasManyToManyOrdering() ? this.manyToManyOrderByTranslation.injectAliases(new StandardOrderByAliasResolver(alias)) : "";
    }

    @Override
    public FetchMode getFetchMode() {
        return this.fetchMode;
    }

    @Override
    public boolean hasOrdering() {
        return this.hasOrder;
    }

    @Override
    public boolean hasManyToManyOrdering() {
        return this.isManyToMany() && this.hasManyToManyOrder;
    }

    @Override
    public boolean hasWhere() {
        return this.hasWhere;
    }

    protected String getSQLDeleteString() {
        return this.sqlDeleteString;
    }

    protected String getSQLInsertRowString() {
        return this.sqlInsertRowString;
    }

    protected String getSQLUpdateRowString() {
        return this.sqlUpdateRowString;
    }

    protected String getSQLDeleteRowString() {
        return this.sqlDeleteRowString;
    }

    @Override
    public Type getKeyType() {
        return this.keyType;
    }

    @Override
    public Type getIndexType() {
        return this.indexType;
    }

    @Override
    public Type getElementType() {
        return this.elementType;
    }

    @Override
    public Class getElementClass() {
        return this.elementClass;
    }

    @Override
    public Object readElement(ResultSet rs, Object owner, String[] aliases, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        return this.getElementType().nullSafeGet(rs, aliases, session, owner);
    }

    @Override
    public Object readIndex(ResultSet rs, String[] aliases, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object index = this.getIndexType().nullSafeGet(rs, aliases, session, null);
        if (index == null) {
            throw new HibernateException("null index column for collection: " + this.navigableRole.getFullPath());
        }
        index = this.decrementIndexByBase(index);
        return index;
    }

    protected Object decrementIndexByBase(Object index) {
        if (this.baseIndex != 0) {
            index = (Integer)index - this.baseIndex;
        }
        return index;
    }

    @Override
    public Object readIdentifier(ResultSet rs, String alias, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object id = this.getIdentifierType().nullSafeGet(rs, alias, session, null);
        if (id == null) {
            throw new HibernateException("null identifier column for collection: " + this.navigableRole.getFullPath());
        }
        return id;
    }

    @Override
    public Object readKey(ResultSet rs, String[] aliases, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Object hydratedKey = this.getKeyType().hydrate(rs, aliases, session, null);
        return hydratedKey == null ? null : this.getKeyType().resolve(hydratedKey, session, null);
    }

    protected int writeKey(PreparedStatement st, Serializable key, int i, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (key == null) {
            throw new NullPointerException("null key for collection: " + this.navigableRole.getFullPath());
        }
        this.getKeyType().nullSafeSet(st, key, i, session);
        return i + this.keyColumnAliases.length;
    }

    protected int writeElement(PreparedStatement st, Object elt, int i, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.getElementType().nullSafeSet(st, elt, i, this.elementColumnIsSettable, session);
        return i + ArrayHelper.countTrue(this.elementColumnIsSettable);
    }

    protected int writeIndex(PreparedStatement st, Object index, int i, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.getIndexType().nullSafeSet(st, this.incrementIndexByBase(index), i, this.indexColumnIsSettable, session);
        return i + ArrayHelper.countTrue(this.indexColumnIsSettable);
    }

    protected Object incrementIndexByBase(Object index) {
        if (this.baseIndex != 0) {
            index = (Integer)index + this.baseIndex;
        }
        return index;
    }

    protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (this.elementIsPureFormula) {
            throw new AssertionFailure("cannot use a formula-based element in the where condition");
        }
        this.getElementType().nullSafeSet(st, elt, i, this.elementColumnIsInPrimaryKey, session);
        return i + this.elementColumnAliases.length;
    }

    protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (this.indexContainsFormula) {
            throw new AssertionFailure("cannot use a formula-based index in the where condition");
        }
        this.getIndexType().nullSafeSet(st, this.incrementIndexByBase(index), i, session);
        return i + this.indexColumnAliases.length;
    }

    public int writeIdentifier(PreparedStatement st, Object id, int i, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        this.getIdentifierType().nullSafeSet(st, id, i, session);
        return i + 1;
    }

    @Override
    public boolean isPrimitiveArray() {
        return this.isPrimitiveArray;
    }

    @Override
    public boolean isArray() {
        return this.isArray;
    }

    @Override
    public String[] getKeyColumnAliases(String suffix) {
        return new Alias(suffix).toAliasStrings(this.keyColumnAliases);
    }

    @Override
    public String[] getElementColumnAliases(String suffix) {
        return new Alias(suffix).toAliasStrings(this.elementColumnAliases);
    }

    @Override
    public String[] getIndexColumnAliases(String suffix) {
        if (this.hasIndex) {
            return new Alias(suffix).toAliasStrings(this.indexColumnAliases);
        }
        return null;
    }

    @Override
    public String getIdentifierColumnAlias(String suffix) {
        if (this.hasIdentifier) {
            return new Alias(suffix).toAliasString(this.identifierColumnAlias);
        }
        return null;
    }

    @Override
    public String getIdentifierColumnName() {
        if (this.hasIdentifier) {
            return this.identifierColumnName;
        }
        return null;
    }

    @Override
    public String selectFragment(String alias, String columnSuffix) {
        SelectFragment frag = this.generateSelectFragment(alias, columnSuffix);
        this.appendElementColumns(frag, alias);
        this.appendIndexColumns(frag, alias);
        this.appendIdentifierColumns(frag, alias);
        return frag.toFragmentString().substring(2);
    }

    protected String generateSelectSizeString(boolean isIntegerIndexed) {
        String selectValue = isIntegerIndexed ? "max(" + this.getIndexColumnNames()[0] + ") + 1" : "count(" + this.getElementColumnNames()[0] + ")";
        return new SimpleSelect(this.dialect).setTableName(this.getTableName()).addCondition(this.getKeyColumnNames(), "=?").addWhereToken(this.sqlWhereString).addColumn(selectValue).toStatementString();
    }

    protected String generateDetectRowByIndexString() {
        if (!this.hasIndex()) {
            return null;
        }
        return new SimpleSelect(this.dialect).setTableName(this.getTableName()).addCondition(this.getKeyColumnNames(), "=?").addCondition(this.getIndexColumnNames(), "=?").addCondition(this.indexFormulas, "=?").addWhereToken(this.sqlWhereString).addColumn("1").toStatementString();
    }

    protected String generateSelectRowByIndexString() {
        if (!this.hasIndex()) {
            return null;
        }
        return new SimpleSelect(this.dialect).setTableName(this.getTableName()).addCondition(this.getKeyColumnNames(), "=?").addCondition(this.getIndexColumnNames(), "=?").addCondition(this.indexFormulas, "=?").addWhereToken(this.sqlWhereString).addColumns(this.getElementColumnNames(), this.elementColumnAliases).addColumns(this.indexFormulas, this.indexColumnAliases).toStatementString();
    }

    protected String generateDetectRowByElementString() {
        return new SimpleSelect(this.dialect).setTableName(this.getTableName()).addCondition(this.getKeyColumnNames(), "=?").addCondition(this.getElementColumnNames(), "=?").addCondition(this.elementFormulas, "=?").addWhereToken(this.sqlWhereString).addColumn("1").toStatementString();
    }

    protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
        return new SelectFragment().setSuffix(columnSuffix).addColumns(alias, this.keyColumnNames, this.keyColumnAliases);
    }

    protected void appendElementColumns(SelectFragment frag, String elemAlias) {
        for (int i = 0; i < this.elementColumnIsGettable.length; ++i) {
            if (this.elementColumnIsGettable[i]) {
                frag.addColumnTemplate(elemAlias, this.elementColumnReaderTemplates[i], this.elementColumnAliases[i]);
                continue;
            }
            frag.addFormula(elemAlias, this.elementFormulaTemplates[i], this.elementColumnAliases[i]);
        }
    }

    protected void appendIndexColumns(SelectFragment frag, String alias) {
        if (this.hasIndex) {
            for (int i = 0; i < this.indexColumnIsGettable.length; ++i) {
                if (this.indexColumnIsGettable[i]) {
                    frag.addColumn(alias, this.indexColumnNames[i], this.indexColumnAliases[i]);
                    continue;
                }
                frag.addFormula(alias, this.indexFormulaTemplates[i], this.indexColumnAliases[i]);
            }
        }
    }

    protected void appendIdentifierColumns(SelectFragment frag, String alias) {
        if (this.hasIdentifier) {
            frag.addColumn(alias, this.identifierColumnName, this.identifierColumnAlias);
        }
    }

    @Override
    public String[] getIndexColumnNames() {
        return this.indexColumnNames;
    }

    @Override
    public String[] getIndexFormulas() {
        return this.indexFormulas;
    }

    @Override
    public String[] getIndexColumnNames(String alias) {
        return AbstractCollectionPersister.qualify(alias, this.indexColumnNames, this.indexFormulaTemplates);
    }

    @Override
    public String[] getElementColumnNames(String alias) {
        return AbstractCollectionPersister.qualify(alias, this.elementColumnNames, this.elementFormulaTemplates);
    }

    private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
        int span = columnNames.length;
        String[] result = new String[span];
        for (int i = 0; i < span; ++i) {
            result[i] = columnNames[i] == null ? StringHelper.replace(formulaTemplates[i], "$PlaceHolder$", alias) : StringHelper.qualify(alias, columnNames[i]);
        }
        return result;
    }

    @Override
    public String[] getElementColumnNames() {
        return this.elementColumnNames;
    }

    @Override
    public String[] getKeyColumnNames() {
        return this.keyColumnNames;
    }

    @Override
    public boolean hasIndex() {
        return this.hasIndex;
    }

    @Override
    public boolean isLazy() {
        return this.isLazy;
    }

    @Override
    public boolean isInverse() {
        return this.isInverse;
    }

    @Override
    public String getTableName() {
        return this.qualifiedTableName;
    }

    @Override
    public void remove(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        if (!this.isInverse && this.isRowDeleteEnabled()) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Deleting collection: %s", MessageHelper.collectionInfoString((CollectionPersister)this, id, this.getFactory()));
            }
            try {
                PreparedStatement st;
                int offset = 1;
                Expectation expectation = Expectations.appropriateExpectation(this.getDeleteAllCheckStyle());
                boolean callable = this.isDeleteAllCallable();
                boolean useBatch = expectation.canBeBatched();
                String sql = this.getSQLDeleteString();
                if (useBatch) {
                    if (this.removeBatchKey == null) {
                        this.removeBatchKey = new BasicBatchKey(this.getRole() + "#REMOVE", expectation);
                    }
                    st = session.getJdbcCoordinator().getBatch(this.removeBatchKey).getBatchStatement(sql, callable);
                } else {
                    st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
                }
                try {
                    this.writeKey(st, id, offset += expectation.prepare(st), session);
                    if (useBatch) {
                        session.getJdbcCoordinator().getBatch(this.removeBatchKey).addToBatch();
                    } else {
                        expectation.verifyOutcome(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st), st, -1, sql);
                    }
                }
                catch (SQLException sqle) {
                    if (useBatch) {
                        session.getJdbcCoordinator().abortBatch();
                    }
                    throw sqle;
                }
                finally {
                    if (!useBatch) {
                        session.getJdbcCoordinator().getResourceRegistry().release(st);
                        session.getJdbcCoordinator().afterStatementExecution();
                    }
                }
                LOG.debug("Done deleting collection");
            }
            catch (SQLException sqle) {
                throw this.sqlExceptionHelper.convert(sqle, "could not delete collection: " + MessageHelper.collectionInfoString((CollectionPersister)this, id, this.getFactory()), this.getSQLDeleteString());
            }
        }
    }

    @Override
    public void recreate(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        block23: {
            if (this.isInverse) {
                return;
            }
            if (!this.isRowInsertEnabled()) {
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Inserting collection: %s", MessageHelper.collectionInfoString(this, collection, id, session));
            }
            try {
                Iterator entries = collection.entries(this);
                JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
                if (entries.hasNext()) {
                    Expectation expectation = Expectations.appropriateExpectation(this.getInsertCheckStyle());
                    collection.preInsert(this);
                    int i = 0;
                    int count = 0;
                    while (entries.hasNext()) {
                        Object entry = entries.next();
                        if (collection.entryExists(entry, i)) {
                            PreparedStatement st;
                            int offset = 1;
                            boolean callable = this.isInsertCallable();
                            boolean useBatch = expectation.canBeBatched();
                            String sql = this.getSQLInsertRowString();
                            if (useBatch) {
                                if (this.recreateBatchKey == null) {
                                    this.recreateBatchKey = new BasicBatchKey(this.getRole() + "#RECREATE", expectation);
                                }
                                st = jdbcCoordinator.getBatch(this.recreateBatchKey).getBatchStatement(sql, callable);
                            } else {
                                st = jdbcCoordinator.getStatementPreparer().prepareStatement(sql, callable);
                            }
                            try {
                                int loc = this.writeKey(st, id, offset += expectation.prepare(st), session);
                                if (this.hasIdentifier) {
                                    loc = this.writeIdentifier(st, collection.getIdentifier(entry, i), loc, session);
                                }
                                if (this.hasIndex) {
                                    loc = this.writeIndex(st, collection.getIndex(entry, i, this), loc, session);
                                }
                                loc = this.writeElement(st, collection.getElement(entry), loc, session);
                                if (useBatch) {
                                    jdbcCoordinator.getBatch(this.recreateBatchKey).addToBatch();
                                } else {
                                    expectation.verifyOutcome(jdbcCoordinator.getResultSetReturn().executeUpdate(st), st, -1, sql);
                                }
                                collection.afterRowInsert(this, entry, i);
                                ++count;
                            }
                            catch (SQLException sqle) {
                                if (useBatch) {
                                    jdbcCoordinator.abortBatch();
                                }
                                throw sqle;
                            }
                            finally {
                                if (!useBatch) {
                                    jdbcCoordinator.getResourceRegistry().release(st);
                                    jdbcCoordinator.afterStatementExecution();
                                }
                            }
                        }
                        ++i;
                    }
                    LOG.debugf("Done inserting collection: %s rows inserted", count);
                    break block23;
                }
                LOG.debug("Collection was empty");
            }
            catch (SQLException sqle) {
                throw this.sqlExceptionHelper.convert(sqle, "could not insert collection: " + MessageHelper.collectionInfoString(this, collection, id, session), this.getSQLInsertRowString());
            }
        }
    }

    protected boolean isRowDeleteEnabled() {
        return true;
    }

    @Override
    public void deleteRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        block24: {
            if (this.isInverse) {
                return;
            }
            if (!this.isRowDeleteEnabled()) {
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Deleting rows of collection: %s", MessageHelper.collectionInfoString(this, collection, id, session));
            }
            boolean deleteByIndex = !this.isOneToMany() && this.hasIndex && !this.indexContainsFormula;
            Expectation expectation = Expectations.appropriateExpectation(this.getDeleteCheckStyle());
            try {
                Iterator deletes = collection.getDeletes(this, !deleteByIndex);
                if (deletes.hasNext()) {
                    int offset = 1;
                    int count = 0;
                    while (deletes.hasNext()) {
                        PreparedStatement st;
                        boolean callable = this.isDeleteCallable();
                        boolean useBatch = expectation.canBeBatched();
                        String sql = this.getSQLDeleteRowString();
                        if (useBatch) {
                            if (this.deleteBatchKey == null) {
                                this.deleteBatchKey = new BasicBatchKey(this.getRole() + "#DELETE", expectation);
                            }
                            st = session.getJdbcCoordinator().getBatch(this.deleteBatchKey).getBatchStatement(sql, callable);
                        } else {
                            st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
                        }
                        try {
                            expectation.prepare(st);
                            Object entry = deletes.next();
                            int loc = offset;
                            if (this.hasIdentifier) {
                                this.writeIdentifier(st, entry, loc, session);
                            } else {
                                loc = this.writeKey(st, id, loc, session);
                                if (deleteByIndex) {
                                    this.writeIndexToWhere(st, entry, loc, session);
                                } else {
                                    this.writeElementToWhere(st, entry, loc, session);
                                }
                            }
                            if (useBatch) {
                                session.getJdbcCoordinator().getBatch(this.deleteBatchKey).addToBatch();
                            } else {
                                expectation.verifyOutcome(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st), st, -1, sql);
                            }
                        }
                        catch (SQLException sqle) {
                            if (useBatch) {
                                session.getJdbcCoordinator().abortBatch();
                            }
                            throw sqle;
                        }
                        finally {
                            if (!useBatch) {
                                session.getJdbcCoordinator().getResourceRegistry().release(st);
                                session.getJdbcCoordinator().afterStatementExecution();
                            }
                        }
                        LOG.debugf("Done deleting collection rows: %s deleted", ++count);
                    }
                    break block24;
                }
                LOG.debug("No rows to delete");
            }
            catch (SQLException sqle) {
                throw this.sqlExceptionHelper.convert(sqle, "could not delete collection rows: " + MessageHelper.collectionInfoString(this, collection, id, session), this.getSQLDeleteRowString());
            }
        }
    }

    protected boolean isRowInsertEnabled() {
        return true;
    }

    @Override
    public void insertRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        if (this.isInverse) {
            return;
        }
        if (!this.isRowInsertEnabled()) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Inserting rows of collection: %s", MessageHelper.collectionInfoString(this, collection, id, session));
        }
        try {
            collection.preInsert(this);
            Iterator entries = collection.entries(this);
            Expectation expectation = Expectations.appropriateExpectation(this.getInsertCheckStyle());
            boolean callable = this.isInsertCallable();
            boolean useBatch = expectation.canBeBatched();
            String sql = this.getSQLInsertRowString();
            int i = 0;
            int count = 0;
            while (entries.hasNext()) {
                int offset = 1;
                Object entry = entries.next();
                PreparedStatement st = null;
                if (collection.needsInserting(entry, i, this.elementType)) {
                    if (useBatch) {
                        if (this.insertBatchKey == null) {
                            this.insertBatchKey = new BasicBatchKey(this.getRole() + "#INSERT", expectation);
                        }
                        if (st == null) {
                            st = session.getJdbcCoordinator().getBatch(this.insertBatchKey).getBatchStatement(sql, callable);
                        }
                    } else {
                        st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
                    }
                    try {
                        offset += expectation.prepare(st);
                        offset = this.writeKey(st, id, offset, session);
                        if (this.hasIdentifier) {
                            offset = this.writeIdentifier(st, collection.getIdentifier(entry, i), offset, session);
                        }
                        if (this.hasIndex) {
                            offset = this.writeIndex(st, collection.getIndex(entry, i, this), offset, session);
                        }
                        this.writeElement(st, collection.getElement(entry), offset, session);
                        if (useBatch) {
                            session.getJdbcCoordinator().getBatch(this.insertBatchKey).addToBatch();
                        } else {
                            expectation.verifyOutcome(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st), st, -1, sql);
                        }
                        collection.afterRowInsert(this, entry, i);
                        ++count;
                    }
                    catch (SQLException sqle) {
                        if (useBatch) {
                            session.getJdbcCoordinator().abortBatch();
                        }
                        throw sqle;
                    }
                    finally {
                        if (!useBatch) {
                            session.getJdbcCoordinator().getResourceRegistry().release(st);
                            session.getJdbcCoordinator().afterStatementExecution();
                        }
                    }
                }
                ++i;
            }
            LOG.debugf("Done inserting rows: %s inserted", count);
        }
        catch (SQLException sqle) {
            throw this.sqlExceptionHelper.convert(sqle, "could not insert collection rows: " + MessageHelper.collectionInfoString(this, collection, id, session), this.getSQLInsertRowString());
        }
    }

    @Override
    public String getRole() {
        return this.navigableRole.getFullPath();
    }

    public String getOwnerEntityName() {
        return this.entityName;
    }

    @Override
    public EntityPersister getOwnerEntityPersister() {
        return this.ownerPersister;
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator() {
        return this.identifierGenerator;
    }

    @Override
    public Type getIdentifierType() {
        return this.identifierType;
    }

    @Override
    public boolean hasOrphanDelete() {
        return this.hasOrphanDelete;
    }

    @Override
    public Type toType(String propertyName) throws QueryException {
        if ("index".equals(propertyName)) {
            return this.indexType;
        }
        return this.elementPropertyMapping.toType(propertyName);
    }

    @Override
    public abstract boolean isManyToMany();

    @Override
    public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
        StringBuilder buffer = new StringBuilder();
        this.manyToManyFilterHelper.render(buffer, this.elementPersister.getFilterAliasGenerator(alias), enabledFilters);
        if (this.manyToManyWhereString != null) {
            buffer.append(" and ").append(StringHelper.replace(this.manyToManyWhereTemplate, "$PlaceHolder$", alias));
        }
        return buffer.toString();
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        if ("index".equals(propertyName)) {
            return AbstractCollectionPersister.qualify(alias, this.indexColumnNames, this.indexFormulaTemplates);
        }
        return this.elementPropertyMapping.toColumns(alias, propertyName);
    }

    @Override
    public String[] toColumns(String propertyName) throws QueryException {
        if ("index".equals(propertyName)) {
            if (this.indexFragments == null) {
                String[] tmp = new String[this.indexColumnNames.length];
                for (int i = 0; i < this.indexColumnNames.length; ++i) {
                    tmp[i] = this.indexColumnNames[i] == null ? this.indexFormulas[i] : this.indexColumnNames[i];
                    this.indexFragments = tmp;
                }
            }
            return this.indexFragments;
        }
        return this.elementPropertyMapping.toColumns(propertyName);
    }

    @Override
    public Type getType() {
        return this.elementPropertyMapping.getType();
    }

    @Override
    public String getName() {
        return this.getRole();
    }

    @Override
    public EntityPersister getElementPersister() {
        if (this.elementPersister == null) {
            throw new AssertionFailure("not an association");
        }
        return this.elementPersister;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public Serializable[] getCollectionSpaces() {
        return this.spaces;
    }

    protected abstract String generateDeleteString();

    protected abstract String generateDeleteRowString();

    protected abstract String generateUpdateRowString();

    protected abstract String generateInsertRowString();

    @Override
    public void updateRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        if (!this.isInverse && collection.isRowUpdatePossible()) {
            LOG.debugf("Updating rows of collection: %s#%s", this.navigableRole.getFullPath(), id);
            int count = this.doUpdateRows(id, collection, session);
            LOG.debugf("Done updating rows: %s updated", count);
        }
    }

    protected abstract int doUpdateRows(Serializable var1, PersistentCollection var2, SharedSessionContractImplementor var3) throws HibernateException;

    @Override
    public void processQueuedOps(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session) throws HibernateException {
        if (collection.hasQueuedOperations()) {
            this.doProcessQueuedOps(collection, key, session);
        }
    }

    @Deprecated
    protected void doProcessQueuedOps(PersistentCollection collection, Serializable key, int nextIndex, SharedSessionContractImplementor session) throws HibernateException {
        this.doProcessQueuedOps(collection, key, session);
    }

    protected abstract void doProcessQueuedOps(PersistentCollection var1, Serializable var2, SharedSessionContractImplementor var3) throws HibernateException;

    @Override
    public CollectionMetadata getCollectionMetadata() {
        return this;
    }

    @Override
    public SessionFactoryImplementor getFactory() {
        return this.factory;
    }

    protected String filterFragment(String alias) throws MappingException {
        return this.hasWhere() ? " and " + this.getSQLWhereString(alias) : "";
    }

    protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
        return this.hasWhere() ? " and " + this.getSQLWhereString(alias) : "";
    }

    @Override
    public String filterFragment(String alias, Map enabledFilters) throws MappingException {
        StringBuilder sessionFilterFragment = new StringBuilder();
        this.filterHelper.render(sessionFilterFragment, this.getFilterAliasGenerator(alias), enabledFilters);
        return sessionFilterFragment.append(this.filterFragment(alias)).toString();
    }

    @Override
    public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) {
        StringBuilder sessionFilterFragment = new StringBuilder();
        this.filterHelper.render(sessionFilterFragment, this.getFilterAliasGenerator(alias), enabledFilters);
        return sessionFilterFragment.append(this.filterFragment(alias, treatAsDeclarations)).toString();
    }

    @Override
    public String oneToManyFilterFragment(String alias) throws MappingException {
        return "";
    }

    @Override
    public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
        return this.oneToManyFilterFragment(alias);
    }

    protected boolean isInsertCallable() {
        return this.insertCallable;
    }

    protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
        return this.insertCheckStyle;
    }

    protected boolean isUpdateCallable() {
        return this.updateCallable;
    }

    protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
        return this.updateCheckStyle;
    }

    protected boolean isDeleteCallable() {
        return this.deleteCallable;
    }

    protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
        return this.deleteCheckStyle;
    }

    protected boolean isDeleteAllCallable() {
        return this.deleteAllCallable;
    }

    protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
        return this.deleteAllCheckStyle;
    }

    public String toString() {
        return StringHelper.unqualify(this.getClass().getName()) + '(' + this.navigableRole.getFullPath() + ')';
    }

    @Override
    public boolean isVersioned() {
        return this.isVersioned && this.getOwnerEntityPersister().isVersioned();
    }

    protected SQLExceptionConverter getSQLExceptionConverter() {
        return this.getSQLExceptionHelper().getSqlExceptionConverter();
    }

    protected SqlExceptionHelper getSQLExceptionHelper() {
        return this.sqlExceptionHelper;
    }

    @Override
    public CacheEntryStructure getCacheEntryStructure() {
        return this.cacheEntryStructure;
    }

    @Override
    public boolean isAffectedByEnabledFilters(SharedSessionContractImplementor session) {
        Map<String, Filter> enabledFilters = session.getLoadQueryInfluencers().getEnabledFilters();
        return this.filterHelper.isAffectedBy(enabledFilters) || this.isManyToMany() && this.manyToManyFilterHelper.isAffectedBy(enabledFilters);
    }

    public boolean isSubselectLoadable() {
        return this.subselectLoadable;
    }

    @Override
    public boolean isMutable() {
        return this.isMutable;
    }

    @Override
    public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
        String[] rawAliases = (String[])this.collectionPropertyColumnAliases.get(propertyName);
        if (rawAliases == null) {
            return null;
        }
        String[] result = new String[rawAliases.length];
        Alias alias = new Alias(suffix);
        for (int i = 0; i < rawAliases.length; ++i) {
            result[i] = alias.toUnquotedAliasString(rawAliases[i]);
        }
        return result;
    }

    public void initCollectionPropertyMap() {
        this.initCollectionPropertyMap("key", this.keyType, this.keyColumnAliases, this.keyColumnNames);
        this.initCollectionPropertyMap("element", this.elementType, this.elementColumnAliases, this.elementColumnNames);
        if (this.hasIndex) {
            this.initCollectionPropertyMap("index", this.indexType, this.indexColumnAliases, this.indexColumnNames);
        }
        if (this.hasIdentifier) {
            this.initCollectionPropertyMap("id", this.identifierType, new String[]{this.identifierColumnAlias}, new String[]{this.identifierColumnName});
        }
    }

    private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
        this.collectionPropertyColumnAliases.put(aliasName, columnAliases);
        if (type.isComponentType()) {
            CompositeType ct = (CompositeType)type;
            String[] propertyNames = ct.getPropertyNames();
            for (int i = 0; i < propertyNames.length; ++i) {
                String name = propertyNames[i];
                this.collectionPropertyColumnAliases.put(aliasName + "." + name, columnAliases[i]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getSize(Serializable key, SharedSessionContractImplementor session) {
        try {
            JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
            PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement(this.sqlSelectSizeString);
            try {
                this.getKeyType().nullSafeSet(st, key, 1, session);
                ResultSet rs = jdbcCoordinator.getResultSetReturn().extract(st);
                try {
                    int n = rs.next() ? rs.getInt(1) - this.baseIndex : 0;
                    jdbcCoordinator.getResourceRegistry().release(rs, st);
                    return n;
                }
                catch (Throwable throwable) {
                    jdbcCoordinator.getResourceRegistry().release(rs, st);
                    throw throwable;
                }
            }
            finally {
                jdbcCoordinator.getResourceRegistry().release(st);
                jdbcCoordinator.afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw this.getSQLExceptionHelper().convert(sqle, "could not retrieve collection size: " + MessageHelper.collectionInfoString((CollectionPersister)this, key, this.getFactory()), this.sqlSelectSizeString);
        }
    }

    @Override
    public boolean indexExists(Serializable key, Object index, SharedSessionContractImplementor session) {
        return this.exists(key, this.incrementIndexByBase(index), this.getIndexType(), this.sqlDetectRowByIndexString, session);
    }

    @Override
    public boolean elementExists(Serializable key, Object element, SharedSessionContractImplementor session) {
        return this.exists(key, element, this.getElementType(), this.sqlDetectRowByElementString, session);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SharedSessionContractImplementor session) {
        JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
        PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement(sql);
        this.getKeyType().nullSafeSet(st, key, 1, session);
        indexOrElementType.nullSafeSet(st, indexOrElement, this.keyColumnNames.length + 1, session);
        ResultSet rs = jdbcCoordinator.getResultSetReturn().extract(st);
        try {
            boolean bl = rs.next();
            jdbcCoordinator.getResourceRegistry().release(rs, st);
            jdbcCoordinator.getResourceRegistry().release(st);
            jdbcCoordinator.afterStatementExecution();
            return bl;
        }
        catch (Throwable throwable) {
            try {
                try {
                    jdbcCoordinator.getResourceRegistry().release(rs, st);
                    throw throwable;
                }
                catch (TransientObjectException e) {
                    boolean bl = false;
                    jdbcCoordinator.getResourceRegistry().release(st);
                    jdbcCoordinator.afterStatementExecution();
                    return bl;
                }
            }
            catch (Throwable throwable2) {
                try {
                    jdbcCoordinator.getResourceRegistry().release(st);
                    jdbcCoordinator.afterStatementExecution();
                    throw throwable2;
                }
                catch (SQLException sqle) {
                    throw this.getSQLExceptionHelper().convert(sqle, "could not check row existence: " + MessageHelper.collectionInfoString((CollectionPersister)this, key, this.getFactory()), this.sqlSelectSizeString);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    public Object getElementByIndex(Serializable key, Object index, SharedSessionContractImplementor session, Object owner) {
        try {
            JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
            PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement(this.sqlSelectRowByIndexString);
            try {
                ResultSet rs;
                block10: {
                    this.getKeyType().nullSafeSet(st, key, 1, session);
                    this.getIndexType().nullSafeSet(st, this.incrementIndexByBase(index), this.keyColumnNames.length + 1, session);
                    rs = jdbcCoordinator.getResultSetReturn().extract(st);
                    try {
                        if (!rs.next()) break block10;
                        Object object = this.getElementType().nullSafeGet(rs, this.elementColumnAliases, session, owner);
                        jdbcCoordinator.getResourceRegistry().release(rs, st);
                        return object;
                    }
                    catch (Throwable throwable) {
                        jdbcCoordinator.getResourceRegistry().release(rs, st);
                        throw throwable;
                    }
                }
                Object var8_10 = null;
                jdbcCoordinator.getResourceRegistry().release(rs, st);
                return var8_10;
            }
            finally {
                jdbcCoordinator.getResourceRegistry().release(st);
                jdbcCoordinator.afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw this.getSQLExceptionHelper().convert(sqle, "could not read row: " + MessageHelper.collectionInfoString((CollectionPersister)this, key, this.getFactory()), this.sqlSelectSizeString);
        }
    }

    @Override
    public boolean isExtraLazy() {
        return this.isExtraLazy;
    }

    protected Dialect getDialect() {
        return this.dialect;
    }

    public CollectionInitializer getInitializer() {
        return this.initializer;
    }

    @Override
    public int getBatchSize() {
        return this.batchSize;
    }

    @Override
    public String getMappedByProperty() {
        return this.mappedByProperty;
    }

    public abstract FilterAliasGenerator getFilterAliasGenerator(String var1);

    @Override
    public CollectionPersister getCollectionPersister() {
        return this;
    }

    @Override
    public CollectionIndexDefinition getIndexDefinition() {
        if (!this.hasIndex()) {
            return null;
        }
        return new CollectionIndexDefinition(){

            @Override
            public CollectionDefinition getCollectionDefinition() {
                return AbstractCollectionPersister.this;
            }

            @Override
            public Type getType() {
                return AbstractCollectionPersister.this.getIndexType();
            }

            @Override
            public EntityDefinition toEntityDefinition() {
                if (!this.getType().isEntityType()) {
                    throw new IllegalStateException("Cannot treat collection index type as entity");
                }
                return (EntityPersister)((Object)((AssociationType)AbstractCollectionPersister.this.getIndexType()).getAssociatedJoinable(AbstractCollectionPersister.this.getFactory()));
            }

            @Override
            public CompositionDefinition toCompositeDefinition() {
                if (!this.getType().isComponentType()) {
                    throw new IllegalStateException("Cannot treat collection index type as composite");
                }
                return new CompositeCollectionElementDefinition(){

                    @Override
                    public String getName() {
                        return "index";
                    }

                    @Override
                    public CompositeType getType() {
                        return (CompositeType)AbstractCollectionPersister.this.getIndexType();
                    }

                    @Override
                    public boolean isNullable() {
                        return false;
                    }

                    @Override
                    public AttributeSource getSource() {
                        return AbstractCollectionPersister.this.getOwnerEntityPersister();
                    }

                    @Override
                    public Iterable<AttributeDefinition> getAttributes() {
                        return CompositionSingularSubAttributesHelper.getCompositeCollectionIndexSubAttributes(this);
                    }

                    @Override
                    public CollectionDefinition getCollectionDefinition() {
                        return AbstractCollectionPersister.this;
                    }
                };
            }

            @Override
            public AnyMappingDefinition toAnyMappingDefinition() {
                Type type = this.getType();
                if (!type.isAnyType()) {
                    throw new IllegalStateException("Cannot treat collection index type as ManyToAny");
                }
                return new StandardAnyTypeDefinition((AnyType)type, AbstractCollectionPersister.this.isLazy() || AbstractCollectionPersister.this.isExtraLazy());
            }
        };
    }

    @Override
    public CollectionElementDefinition getElementDefinition() {
        return new CollectionElementDefinition(){

            @Override
            public CollectionDefinition getCollectionDefinition() {
                return AbstractCollectionPersister.this;
            }

            @Override
            public Type getType() {
                return AbstractCollectionPersister.this.getElementType();
            }

            @Override
            public AnyMappingDefinition toAnyMappingDefinition() {
                Type type = this.getType();
                if (!type.isAnyType()) {
                    throw new IllegalStateException("Cannot treat collection element type as ManyToAny");
                }
                return new StandardAnyTypeDefinition((AnyType)type, AbstractCollectionPersister.this.isLazy() || AbstractCollectionPersister.this.isExtraLazy());
            }

            @Override
            public EntityDefinition toEntityDefinition() {
                if (!this.getType().isEntityType()) {
                    throw new IllegalStateException("Cannot treat collection element type as entity");
                }
                return AbstractCollectionPersister.this.getElementPersister();
            }

            @Override
            public CompositeCollectionElementDefinition toCompositeElementDefinition() {
                if (!this.getType().isComponentType()) {
                    throw new IllegalStateException("Cannot treat entity collection element type as composite");
                }
                return new CompositeCollectionElementDefinition(){

                    @Override
                    public String getName() {
                        return "";
                    }

                    @Override
                    public CompositeType getType() {
                        return (CompositeType)AbstractCollectionPersister.this.getElementType();
                    }

                    @Override
                    public boolean isNullable() {
                        return false;
                    }

                    @Override
                    public AttributeSource getSource() {
                        return AbstractCollectionPersister.this.getOwnerEntityPersister();
                    }

                    @Override
                    public Iterable<AttributeDefinition> getAttributes() {
                        return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes(this);
                    }

                    @Override
                    public CollectionDefinition getCollectionDefinition() {
                        return AbstractCollectionPersister.this;
                    }
                };
            }
        };
    }

    protected Insert createInsert() {
        return new Insert(this.getFactory().getJdbcServices().getDialect());
    }

    protected Update createUpdate() {
        return new Update(this.getFactory().getJdbcServices().getDialect());
    }

    protected Delete createDelete() {
        return new Delete();
    }

    private class StandardOrderByAliasResolver
    implements OrderByAliasResolver {
        private final String rootAlias;

        private StandardOrderByAliasResolver(String rootAlias) {
            this.rootAlias = rootAlias;
        }

        @Override
        public String resolveTableAlias(String columnReference) {
            if (AbstractCollectionPersister.this.elementPersister == null) {
                return this.rootAlias;
            }
            return ((Loadable)AbstractCollectionPersister.this.elementPersister).getTableAliasForColumn(columnReference, this.rootAlias);
        }
    }

    private class ColumnMapperImpl
    implements ColumnMapper {
        private ColumnMapperImpl() {
        }

        @Override
        public SqlValueReference[] map(String reference) {
            String[] formulaTemplates;
            String[] columnNames;
            if ("$element$".equals(reference)) {
                columnNames = AbstractCollectionPersister.this.elementColumnNames;
                formulaTemplates = AbstractCollectionPersister.this.elementFormulaTemplates;
            } else {
                columnNames = AbstractCollectionPersister.this.elementPropertyMapping.toColumns(reference);
                formulaTemplates = AbstractCollectionPersister.this.formulaTemplates(reference, columnNames.length);
            }
            SqlValueReference[] result = new SqlValueReference[columnNames.length];
            int i = 0;
            for (final String columnName : columnNames) {
                if (columnName == null) {
                    final String formulaTemplate = formulaTemplates[i];
                    result[i] = new FormulaReference(){

                        @Override
                        public String getFormulaFragment() {
                            return formulaTemplate;
                        }
                    };
                } else {
                    result[i] = new ColumnReference(){

                        @Override
                        public String getColumnName() {
                            return columnName;
                        }
                    };
                }
                ++i;
            }
            return result;
        }
    }
}

