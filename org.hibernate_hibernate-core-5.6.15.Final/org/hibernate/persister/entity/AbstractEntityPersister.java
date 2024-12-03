/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementHelper;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeDescriptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributesMetadata;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
import org.hibernate.cache.spi.entry.StructuredCacheEntry;
import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.internal.CacheHelper;
import org.hibernate.engine.internal.ImmutableEntityEntryFactory;
import org.hibernate.engine.internal.MutableEntityEntryFactory;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.CachedNaturalIdValueSource;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityEntryFactory;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.ValueInclusion;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PostInsertIdentifierGenerator;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.Binder;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.FilterHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.internal.util.collections.LockModeEnumMap;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.jdbc.TooManyRowsAffectedException;
import org.hibernate.loader.custom.sql.SQLQueryParser;
import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
import org.hibernate.loader.entity.CacheEntityLoaderHelper;
import org.hibernate.loader.entity.CascadeEntityLoader;
import org.hibernate.loader.entity.EntityLoader;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.entity.plan.MultiEntityLoadingSupport;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.BasicEntityPropertyMapping;
import org.hibernate.persister.entity.DiscriminatorMetadata;
import org.hibernate.persister.entity.DiscriminatorType;
import org.hibernate.persister.entity.EntityLoaderLazyCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.persister.entity.MultiLoadOptions;
import org.hibernate.persister.entity.NamedQueryLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.entity.SQLLoadable;
import org.hibernate.persister.entity.UniqueKeyLoadable;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
import org.hibernate.sql.Alias;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Insert;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;
import org.hibernate.sql.Select;
import org.hibernate.sql.SelectFragment;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.sql.Template;
import org.hibernate.sql.Update;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
import org.hibernate.tuple.InMemoryValueGenerationStrategy;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.ValueGeneration;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;
import org.hibernate.type.VersionType;

public abstract class AbstractEntityPersister
implements OuterJoinLoadable,
Queryable,
ClassMetadata,
UniqueKeyLoadable,
SQLLoadable,
LazyPropertyInitializer,
PostInsertIdentityPersister,
Lockable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractEntityPersister.class);
    public static final String ENTITY_CLASS = "class";
    public static final String VERSION_COLUMN_ALIAS = "version_";
    private final NavigableRole navigableRole;
    private final SessionFactoryImplementor factory;
    private final boolean canReadFromCache;
    private final boolean canWriteToCache;
    private final boolean invalidateCache;
    private final EntityDataAccess cacheAccessStrategy;
    private final NaturalIdDataAccess naturalIdRegionAccessStrategy;
    private final boolean isLazyPropertiesCacheable;
    private final CacheEntryHelper cacheEntryHelper;
    private final EntityMetamodel entityMetamodel;
    private final EntityTuplizer entityTuplizer;
    private final EntityEntryFactory entityEntryFactory;
    private final String[] rootTableKeyColumnNames;
    private final String[] rootTableKeyColumnReaders;
    private final String[] rootTableKeyColumnReaderTemplates;
    private final String[] identifierAliases;
    private final int identifierColumnSpan;
    private final String versionColumnName;
    private final boolean hasFormulaProperties;
    protected final int batchSize;
    private final boolean hasSubselectLoadableCollections;
    protected final String rowIdName;
    private final String sqlWhereString;
    private final String sqlWhereStringTemplate;
    private final int[] propertyColumnSpans;
    private final String[] propertySubclassNames;
    private final String[][] propertyColumnAliases;
    private final String[][] propertyColumnNames;
    private final String[][] propertyColumnFormulaTemplates;
    private final String[][] propertyColumnReaderTemplates;
    private final String[][] propertyColumnWriters;
    private final boolean[][] propertyColumnUpdateable;
    private final boolean[][] propertyColumnInsertable;
    private final boolean[] propertyUniqueness;
    private final boolean[] propertySelectable;
    private final List<Integer> lobProperties;
    private final String[] lazyPropertyNames;
    private final int[] lazyPropertyNumbers;
    private final Type[] lazyPropertyTypes;
    private final String[][] lazyPropertyColumnAliases;
    private final String[] subclassPropertyNameClosure;
    private final String[] subclassPropertySubclassNameClosure;
    private final Type[] subclassPropertyTypeClosure;
    private final String[][] subclassPropertyFormulaTemplateClosure;
    private final String[][] subclassPropertyColumnNameClosure;
    private final String[][] subclassPropertyColumnReaderClosure;
    private final String[][] subclassPropertyColumnReaderTemplateClosure;
    private final FetchMode[] subclassPropertyFetchModeClosure;
    private final boolean[] subclassPropertyNullabilityClosure;
    private final boolean[] propertyDefinedOnSubclass;
    private final int[][] subclassPropertyColumnNumberClosure;
    private final int[][] subclassPropertyFormulaNumberClosure;
    private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
    private final String[] subclassColumnClosure;
    private final boolean[] subclassColumnLazyClosure;
    private final String[] subclassColumnAliasClosure;
    private final boolean[] subclassColumnSelectableClosure;
    private final String[] subclassColumnReaderTemplateClosure;
    private final String[] subclassFormulaClosure;
    private final String[] subclassFormulaTemplateClosure;
    private final String[] subclassFormulaAliasClosure;
    private final boolean[] subclassFormulaLazyClosure;
    private final FilterHelper filterHelper;
    private volatile Set<String> affectingFetchProfileNames;
    private final LockModeEnumMap<LockingStrategy> lockers = new LockModeEnumMap();
    private final EntityLoaderLazyCollection loaders = new EntityLoaderLazyCollection();
    private volatile Map<String, UniqueEntityLoader> uniqueKeyLoaders;
    private volatile Map<LockMode, EntityLoader> naturalIdLoaders;
    private String sqlVersionSelectString;
    private String sqlSnapshotSelectString;
    private Map<String, String> sqlLazySelectStringsByFetchGroup;
    private String sqlIdentityInsertString;
    private String sqlUpdateByRowIdString;
    private String sqlLazyUpdateByRowIdString;
    private String[] sqlDeleteStrings;
    private String[] sqlInsertStrings;
    private String[] sqlUpdateStrings;
    private String[] sqlLazyUpdateStrings;
    private String sqlInsertGeneratedValuesSelectString;
    private String sqlUpdateGeneratedValuesSelectString;
    protected boolean[] insertCallable;
    protected boolean[] updateCallable;
    protected boolean[] deleteCallable;
    protected String[] customSQLInsert;
    protected String[] customSQLUpdate;
    protected String[] customSQLDelete;
    protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
    protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
    protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
    private InsertGeneratedIdentifierDelegate identityDelegate;
    private boolean[] tableHasColumns;
    private final String loaderName;
    private UniqueEntityLoader queryLoader;
    private final Map subclassPropertyAliases = new HashMap();
    private final Map subclassPropertyColumnNames = new HashMap();
    protected final BasicEntityPropertyMapping propertyMapping;
    private final boolean useReferenceCacheEntries;
    private static final String DISCRIMINATOR_ALIAS = "clazz_";
    private DiscriminatorMetadata discriminatorMetadata;
    private BasicBatchKey insertBatchKey;
    private BasicBatchKey updateBatchKey;
    private BasicBatchKey deleteBatchKey;
    private Boolean naturalIdIsNonNullable;
    private String cachedPkByNonNullableNaturalIdQuery;
    private EntityIdentifierDefinition entityIdentifierDefinition;
    private Iterable<AttributeDefinition> attributeDefinitions;

    protected void addDiscriminatorToInsert(Insert insert) {
    }

    protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
    }

    protected abstract int[] getSubclassColumnTableNumberClosure();

    protected abstract int[] getSubclassFormulaTableNumberClosure();

    @Override
    public abstract String getSubclassTableName(int var1);

    protected abstract String[] getSubclassTableKeyColumns(int var1);

    protected abstract boolean isClassOrSuperclassTable(int var1);

    protected boolean isClassOrSuperclassJoin(int j) {
        return this.isClassOrSuperclassTable(j);
    }

    public abstract int getSubclassTableSpan();

    public abstract int getTableSpan();

    public abstract boolean isTableCascadeDeleteEnabled(int var1);

    public abstract String getTableName(int var1);

    public abstract String[] getKeyColumns(int var1);

    public abstract boolean isPropertyOfTable(int var1, int var2);

    protected abstract int[] getPropertyTableNumbersInSelect();

    protected abstract int[] getPropertyTableNumbers();

    protected abstract int getSubclassPropertyTableNumber(int var1);

    protected abstract String filterFragment(String var1) throws MappingException;

    protected abstract String filterFragment(String var1, Set<String> var2);

    @Override
    public String getDiscriminatorColumnName() {
        return DISCRIMINATOR_ALIAS;
    }

    public String getDiscriminatorColumnReaders() {
        return DISCRIMINATOR_ALIAS;
    }

    public String getDiscriminatorColumnReaderTemplate() {
        if (this.getEntityMetamodel().getSubclassEntityNames().size() == 1) {
            return this.getDiscriminatorSQLValue();
        }
        return "$PlaceHolder$.clazz_";
    }

    public String getDiscriminatorAlias() {
        return DISCRIMINATOR_ALIAS;
    }

    public String getDiscriminatorFormulaTemplate() {
        return null;
    }

    public boolean isInverseTable(int j) {
        return false;
    }

    public boolean isNullableTable(int j) {
        return false;
    }

    protected boolean isNullableSubclassTable(int j) {
        return false;
    }

    protected boolean isInverseSubclassTable(int j) {
        return false;
    }

    @Override
    public boolean isSubclassEntityName(String entityName) {
        return this.entityMetamodel.getSubclassEntityNames().contains(entityName);
    }

    private boolean[] getTableHasColumns() {
        return this.tableHasColumns;
    }

    @Override
    public String[] getRootTableKeyColumnNames() {
        return this.rootTableKeyColumnNames;
    }

    public String[] getSQLUpdateByRowIdStrings() {
        if (this.sqlUpdateByRowIdString == null) {
            throw new AssertionFailure("no update by row id");
        }
        String[] result = new String[this.getTableSpan() + 1];
        result[0] = this.sqlUpdateByRowIdString;
        System.arraycopy(this.sqlUpdateStrings, 0, result, 1, this.getTableSpan());
        return result;
    }

    public String[] getSQLLazyUpdateByRowIdStrings() {
        if (this.sqlLazyUpdateByRowIdString == null) {
            throw new AssertionFailure("no update by row id");
        }
        String[] result = new String[this.getTableSpan()];
        result[0] = this.sqlLazyUpdateByRowIdString;
        System.arraycopy(this.sqlLazyUpdateStrings, 1, result, 1, this.getTableSpan() - 1);
        return result;
    }

    public String getSQLSnapshotSelectString() {
        return this.sqlSnapshotSelectString;
    }

    public String getSQLLazySelectString(String fetchGroup) {
        return this.sqlLazySelectStringsByFetchGroup.get(fetchGroup);
    }

    public String[] getSQLDeleteStrings() {
        return this.sqlDeleteStrings;
    }

    public String[] getSQLInsertStrings() {
        return this.sqlInsertStrings;
    }

    public String[] getSQLUpdateStrings() {
        return this.sqlUpdateStrings;
    }

    public String[] getSQLLazyUpdateStrings() {
        return this.sqlLazyUpdateStrings;
    }

    public ExecuteUpdateResultCheckStyle[] getInsertResultCheckStyles() {
        return this.insertResultCheckStyles;
    }

    public ExecuteUpdateResultCheckStyle[] getUpdateResultCheckStyles() {
        return this.updateResultCheckStyles;
    }

    public ExecuteUpdateResultCheckStyle[] getDeleteResultCheckStyles() {
        return this.deleteResultCheckStyles;
    }

    public String getSQLIdentityInsertString() {
        return this.sqlIdentityInsertString;
    }

    public String getVersionSelectString() {
        return this.sqlVersionSelectString;
    }

    public boolean isInsertCallable(int j) {
        return this.insertCallable[j];
    }

    public boolean isUpdateCallable(int j) {
        return this.updateCallable[j];
    }

    public boolean isDeleteCallable(int j) {
        return this.deleteCallable[j];
    }

    protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
        return false;
    }

    protected boolean isSubclassTableSequentialSelect(int j) {
        return false;
    }

    public boolean hasSequentialSelect() {
        return false;
    }

    public boolean[] getTableUpdateNeeded(int[] dirtyProperties, boolean hasDirtyCollection) {
        if (dirtyProperties == null) {
            return this.getTableHasColumns();
        }
        boolean[] updateability = this.getPropertyUpdateability();
        int[] propertyTableNumbers = this.getPropertyTableNumbers();
        boolean[] tableUpdateNeeded = new boolean[this.getTableSpan()];
        for (int property : dirtyProperties) {
            int table = propertyTableNumbers[property];
            boolean bl = tableUpdateNeeded[table] = tableUpdateNeeded[table] || this.getPropertyColumnSpan(property) > 0 && updateability[property];
            if (this.getPropertyColumnSpan(property) <= 0 || updateability[property]) continue;
            LOG.ignoreImmutablePropertyModification(this.getPropertyNames()[property], this.getEntityName());
        }
        if (this.isVersioned()) {
            tableUpdateNeeded[0] = tableUpdateNeeded[0] || Versioning.isVersionIncrementRequired(dirtyProperties, hasDirtyCollection, this.getPropertyVersionability());
        }
        return tableUpdateNeeded;
    }

    @Override
    public boolean hasRowId() {
        return this.rowIdName != null;
    }

    public boolean[][] getPropertyColumnUpdateable() {
        return this.propertyColumnUpdateable;
    }

    public boolean[][] getPropertyColumnInsertable() {
        return this.propertyColumnInsertable;
    }

    public boolean[] getPropertySelectable() {
        return this.propertySelectable;
    }

    public String[] getTableNames() {
        String[] tableNames = new String[this.getTableSpan()];
        for (int i = 0; i < tableNames.length; ++i) {
            tableNames[i] = this.getTableName(i);
        }
        return tableNames;
    }

    public AbstractEntityPersister(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy, NaturalIdDataAccess naturalIdRegionAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
        Object formulaTemplates;
        this.factory = creationContext.getSessionFactory();
        this.navigableRole = new NavigableRole(persistentClass.getEntityName());
        SessionFactoryOptions sessionFactoryOptions = creationContext.getSessionFactory().getSessionFactoryOptions();
        if (sessionFactoryOptions.isSecondLevelCacheEnabled()) {
            this.canWriteToCache = this.determineCanWriteToCache(persistentClass, cacheAccessStrategy);
            this.canReadFromCache = this.determineCanReadFromCache(persistentClass, cacheAccessStrategy);
            this.cacheAccessStrategy = cacheAccessStrategy;
            this.isLazyPropertiesCacheable = persistentClass.getRootClass().isLazyPropertiesCacheable();
            this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
        } else {
            this.canWriteToCache = false;
            this.canReadFromCache = false;
            this.cacheAccessStrategy = null;
            this.isLazyPropertiesCacheable = true;
            this.naturalIdRegionAccessStrategy = null;
        }
        this.entityMetamodel = new EntityMetamodel(persistentClass, this, creationContext);
        this.entityTuplizer = this.entityMetamodel.getTuplizer();
        this.entityEntryFactory = this.entityMetamodel.isMutable() ? MutableEntityEntryFactory.INSTANCE : ImmutableEntityEntryFactory.INSTANCE;
        JdbcServices jdbcServices = this.factory.getServiceRegistry().getService(JdbcServices.class);
        Dialect dialect = jdbcServices.getJdbcEnvironment().getDialect();
        int batch = persistentClass.getBatchSize();
        if (batch == -1) {
            batch = this.factory.getSessionFactoryOptions().getDefaultBatchFetchSize();
        }
        this.batchSize = batch;
        this.hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
        this.propertyMapping = new BasicEntityPropertyMapping(this);
        this.identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
        this.rootTableKeyColumnNames = new String[this.identifierColumnSpan];
        this.rootTableKeyColumnReaders = new String[this.identifierColumnSpan];
        this.rootTableKeyColumnReaderTemplates = new String[this.identifierColumnSpan];
        this.identifierAliases = new String[this.identifierColumnSpan];
        this.rowIdName = persistentClass.getRootTable().getRowId();
        this.loaderName = persistentClass.getLoaderName();
        Iterator iter = persistentClass.getIdentifier().getColumnIterator();
        int i = 0;
        while (iter.hasNext()) {
            Column col = (Column)iter.next();
            this.rootTableKeyColumnNames[i] = col.getQuotedName(dialect);
            this.rootTableKeyColumnReaders[i] = col.getReadExpr(dialect);
            this.rootTableKeyColumnReaderTemplates[i] = col.getTemplate(dialect, this.factory.getSqlFunctionRegistry());
            this.identifierAliases[i] = col.getAlias(dialect, persistentClass.getRootTable());
            ++i;
        }
        this.versionColumnName = persistentClass.isVersioned() ? ((Column)persistentClass.getVersion().getColumnIterator().next()).getQuotedName(dialect) : null;
        this.sqlWhereString = StringHelper.isNotEmpty(persistentClass.getWhere()) ? "( " + persistentClass.getWhere() + ") " : null;
        this.sqlWhereStringTemplate = this.sqlWhereString == null ? null : Template.renderWhereStringTemplate(this.sqlWhereString, dialect, this.factory.getSqlFunctionRegistry());
        boolean lazyAvailable = this.isInstrumented();
        int hydrateSpan = this.entityMetamodel.getPropertySpan();
        this.propertyColumnSpans = new int[hydrateSpan];
        this.propertySubclassNames = new String[hydrateSpan];
        this.propertyColumnAliases = new String[hydrateSpan][];
        this.propertyColumnNames = new String[hydrateSpan][];
        this.propertyColumnFormulaTemplates = new String[hydrateSpan][];
        this.propertyColumnReaderTemplates = new String[hydrateSpan][];
        this.propertyColumnWriters = new String[hydrateSpan][];
        this.propertyUniqueness = new boolean[hydrateSpan];
        this.propertySelectable = new boolean[hydrateSpan];
        this.propertyColumnUpdateable = new boolean[hydrateSpan][];
        this.propertyColumnInsertable = new boolean[hydrateSpan][];
        HashSet<Property> thisClassProperties = new HashSet<Property>();
        ArrayList<String> lazyNames = new ArrayList<String>();
        ArrayList<Integer> lazyNumbers = new ArrayList<Integer>();
        ArrayList<Type> lazyTypes = new ArrayList<Type>();
        ArrayList<String[]> lazyColAliases = new ArrayList<String[]>();
        ArrayList<Integer> lobPropertiesLocalCollector = new ArrayList<Integer>();
        iter = persistentClass.getPropertyClosureIterator();
        i = 0;
        boolean foundFormula = false;
        while (iter.hasNext()) {
            boolean lazy;
            int span;
            Property prop = (Property)iter.next();
            thisClassProperties.add(prop);
            this.propertyColumnSpans[i] = span = prop.getColumnSpan();
            this.propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
            String[] colNames = new String[span];
            String[] colAliases = new String[span];
            String[] colReaderTemplates = new String[span];
            String[] colWriters = new String[span];
            formulaTemplates = new String[span];
            Iterator colIter = prop.getColumnIterator();
            int k = 0;
            while (colIter.hasNext()) {
                Selectable thing = (Selectable)colIter.next();
                colAliases[k] = thing.getAlias(dialect, prop.getValue().getTable());
                if (thing.isFormula()) {
                    foundFormula = true;
                    ((Formula)thing).setFormula(this.substituteBrackets(((Formula)thing).getFormula()));
                    formulaTemplates[k] = thing.getTemplate(dialect, this.factory.getSqlFunctionRegistry());
                } else {
                    Column col = (Column)thing;
                    colNames[k] = col.getQuotedName(dialect);
                    colReaderTemplates[k] = col.getTemplate(dialect, this.factory.getSqlFunctionRegistry());
                    colWriters[k] = col.getWriteExpr();
                }
                ++k;
            }
            this.propertyColumnNames[i] = colNames;
            this.propertyColumnFormulaTemplates[i] = formulaTemplates;
            this.propertyColumnReaderTemplates[i] = colReaderTemplates;
            this.propertyColumnWriters[i] = colWriters;
            this.propertyColumnAliases[i] = colAliases;
            boolean bl = lazy = !EnhancementHelper.includeInBaseFetchGroup(prop, this.entityMetamodel.isInstrumented(), entityName -> {
                MetadataImplementor metadata = creationContext.getMetadata();
                PersistentClass entityBinding = metadata.getEntityBinding(entityName);
                assert (entityBinding != null);
                return entityBinding.hasSubclasses();
            }, sessionFactoryOptions.isCollectionsInDefaultFetchGroupEnabled());
            if (lazy) {
                lazyNames.add(prop.getName());
                lazyNumbers.add(i);
                lazyTypes.add(prop.getValue().getType());
                lazyColAliases.add(colAliases);
            }
            this.propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
            this.propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
            this.propertySelectable[i] = prop.isSelectable();
            this.propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
            if (prop.isLob() && dialect.forceLobAsLastValue()) {
                lobPropertiesLocalCollector.add(i);
            }
            ++i;
        }
        this.lobProperties = CollectionHelper.toSmallList(lobPropertiesLocalCollector);
        this.hasFormulaProperties = foundFormula;
        this.lazyPropertyColumnAliases = ArrayHelper.to2DStringArray(lazyColAliases);
        this.lazyPropertyNames = ArrayHelper.toStringArray(lazyNames);
        this.lazyPropertyNumbers = ArrayHelper.toIntArray(lazyNumbers);
        this.lazyPropertyTypes = ArrayHelper.toTypeArray(lazyTypes);
        ArrayList<String> columns = new ArrayList<String>();
        ArrayList<Boolean> columnsLazy = new ArrayList<Boolean>();
        ArrayList<String> columnReaderTemplates = new ArrayList<String>();
        ArrayList<String> aliases = new ArrayList<String>();
        ArrayList<String> formulas = new ArrayList<String>();
        ArrayList<String> formulaAliases = new ArrayList<String>();
        formulaTemplates = new ArrayList();
        ArrayList<Boolean> formulasLazy = new ArrayList<Boolean>();
        ArrayList<Type> types = new ArrayList<Type>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> classes = new ArrayList<String>();
        ArrayList<String[]> templates = new ArrayList<String[]>();
        ArrayList<String[]> propColumns = new ArrayList<String[]>();
        ArrayList<String[]> propColumnReaders = new ArrayList<String[]>();
        ArrayList<String[]> propColumnReaderTemplates = new ArrayList<String[]>();
        ArrayList<FetchMode> joinedFetchesList = new ArrayList<FetchMode>();
        ArrayList<CascadeStyle> cascades = new ArrayList<CascadeStyle>();
        ArrayList<Boolean> definedBySubclass = new ArrayList<Boolean>();
        ArrayList<int[]> propColumnNumbers = new ArrayList<int[]>();
        ArrayList<int[]> propFormulaNumbers = new ArrayList<int[]>();
        ArrayList<Boolean> columnSelectables = new ArrayList<Boolean>();
        ArrayList<Boolean> propNullables = new ArrayList<Boolean>();
        iter = persistentClass.getSubclassPropertyClosureIterator();
        while (iter.hasNext()) {
            boolean lazy;
            Property prop = (Property)iter.next();
            names.add(prop.getName());
            classes.add(prop.getPersistentClass().getEntityName());
            boolean isDefinedBySubclass = !thisClassProperties.contains(prop);
            definedBySubclass.add(isDefinedBySubclass);
            propNullables.add(prop.isOptional() || isDefinedBySubclass);
            types.add(prop.getType());
            Iterator colIter = prop.getColumnIterator();
            String[] cols = new String[prop.getColumnSpan()];
            String[] readers = new String[prop.getColumnSpan()];
            String[] readerTemplates = new String[prop.getColumnSpan()];
            String[] forms = new String[prop.getColumnSpan()];
            int[] colnos = new int[prop.getColumnSpan()];
            int[] formnos = new int[prop.getColumnSpan()];
            int l = 0;
            boolean bl = lazy = !EnhancementHelper.includeInBaseFetchGroup(prop, this.entityMetamodel.isInstrumented(), entityName -> {
                MetadataImplementor metadata = creationContext.getMetadata();
                PersistentClass entityBinding = metadata.getEntityBinding(entityName);
                assert (entityBinding != null);
                return entityBinding.hasSubclasses();
            }, sessionFactoryOptions.isCollectionsInDefaultFetchGroupEnabled());
            while (colIter.hasNext()) {
                Selectable thing = (Selectable)colIter.next();
                if (thing.isFormula()) {
                    String template = thing.getTemplate(dialect, this.factory.getSqlFunctionRegistry());
                    formnos[l] = ((ArrayList)formulaTemplates).size();
                    colnos[l] = -1;
                    ((ArrayList)formulaTemplates).add(template);
                    forms[l] = template;
                    formulas.add(thing.getText(dialect));
                    formulaAliases.add(thing.getAlias(dialect));
                    formulasLazy.add(lazy);
                } else {
                    String readerTemplate;
                    Column col = (Column)thing;
                    String colName = col.getQuotedName(dialect);
                    colnos[l] = columns.size();
                    formnos[l] = -1;
                    columns.add(colName);
                    cols[l] = colName;
                    aliases.add(thing.getAlias(dialect, prop.getValue().getTable()));
                    columnsLazy.add(lazy);
                    columnSelectables.add(prop.isSelectable());
                    readers[l] = col.getReadExpr(dialect);
                    readerTemplates[l] = readerTemplate = col.getTemplate(dialect, this.factory.getSqlFunctionRegistry());
                    columnReaderTemplates.add(readerTemplate);
                }
                ++l;
            }
            propColumns.add(cols);
            propColumnReaders.add(readers);
            propColumnReaderTemplates.add(readerTemplates);
            templates.add(forms);
            propColumnNumbers.add(colnos);
            propFormulaNumbers.add(formnos);
            joinedFetchesList.add(prop.getValue().getFetchMode());
            cascades.add(prop.getCascadeStyle());
        }
        this.subclassColumnClosure = ArrayHelper.toStringArray(columns);
        this.subclassColumnAliasClosure = ArrayHelper.toStringArray(aliases);
        this.subclassColumnLazyClosure = ArrayHelper.toBooleanArray(columnsLazy);
        this.subclassColumnSelectableClosure = ArrayHelper.toBooleanArray(columnSelectables);
        this.subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray(columnReaderTemplates);
        this.subclassFormulaClosure = ArrayHelper.toStringArray(formulas);
        this.subclassFormulaTemplateClosure = ArrayHelper.toStringArray((Collection)formulaTemplates);
        this.subclassFormulaAliasClosure = ArrayHelper.toStringArray(formulaAliases);
        this.subclassFormulaLazyClosure = ArrayHelper.toBooleanArray(formulasLazy);
        this.subclassPropertyNameClosure = ArrayHelper.toStringArray(names);
        this.subclassPropertySubclassNameClosure = ArrayHelper.toStringArray(classes);
        this.subclassPropertyTypeClosure = ArrayHelper.toTypeArray(types);
        this.subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray(propNullables);
        this.subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray(templates);
        this.subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray(propColumns);
        this.subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray(propColumnReaders);
        this.subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray(propColumnReaderTemplates);
        this.subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray(propColumnNumbers);
        this.subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray(propFormulaNumbers);
        this.subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
        iter = cascades.iterator();
        int j = 0;
        while (iter.hasNext()) {
            this.subclassPropertyCascadeStyleClosure[j++] = (CascadeStyle)iter.next();
        }
        this.subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
        iter = joinedFetchesList.iterator();
        j = 0;
        while (iter.hasNext()) {
            this.subclassPropertyFetchModeClosure[j++] = (FetchMode)((Object)iter.next());
        }
        this.propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
        iter = definedBySubclass.iterator();
        j = 0;
        while (iter.hasNext()) {
            this.propertyDefinedOnSubclass[j++] = (Boolean)iter.next();
        }
        this.filterHelper = new FilterHelper(persistentClass.getFilters(), this.factory);
        boolean refCacheEntries = true;
        if (!this.factory.getSessionFactoryOptions().isDirectReferenceCacheEntriesEnabled()) {
            refCacheEntries = false;
        }
        if (this.entityMetamodel.isMutable()) {
            refCacheEntries = false;
        }
        for (Type type : this.getSubclassPropertyTypeClosure()) {
            if (!type.isAssociationType()) continue;
            refCacheEntries = false;
        }
        this.useReferenceCacheEntries = refCacheEntries;
        this.cacheEntryHelper = this.buildCacheEntryHelper();
        this.invalidateCache = sessionFactoryOptions.isSecondLevelCacheEnabled() ? this.canWriteToCache && this.determineWhetherToInvalidateCache(persistentClass, creationContext) : false;
    }

    private boolean determineWhetherToInvalidateCache(PersistentClass persistentClass, PersisterCreationContext creationContext) {
        if (this.hasFormulaProperties()) {
            return true;
        }
        if (this.isVersioned()) {
            return false;
        }
        if (this.entityMetamodel.isDynamicUpdate()) {
            return false;
        }
        boolean complianceEnabled = creationContext.getSessionFactory().getSessionFactoryOptions().getJpaCompliance().isJpaCacheComplianceEnabled();
        if (complianceEnabled) {
            return false;
        }
        return persistentClass.getJoinClosureSpan() >= 1;
    }

    private boolean determineCanWriteToCache(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy) {
        if (cacheAccessStrategy == null) {
            return false;
        }
        return persistentClass.isCached();
    }

    private boolean determineCanReadFromCache(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy) {
        if (cacheAccessStrategy == null) {
            return false;
        }
        if (persistentClass.isCached()) {
            return true;
        }
        Iterator subclassIterator = persistentClass.getSubclassIterator();
        while (subclassIterator.hasNext()) {
            Subclass subclass = (Subclass)subclassIterator.next();
            if (!subclass.isCached()) continue;
            return true;
        }
        return false;
    }

    protected CacheEntryHelper buildCacheEntryHelper() {
        if (this.cacheAccessStrategy == null) {
            return NoopCacheEntryHelper.INSTANCE;
        }
        if (this.canUseReferenceCacheEntries()) {
            this.entityMetamodel.setLazy(false);
            return new ReferenceCacheEntryHelper(this);
        }
        return this.factory.getSessionFactoryOptions().isStructuredCacheEntriesEnabled() ? new StructuredCacheEntryHelper(this) : new StandardCacheEntryHelper(this);
    }

    @Override
    public boolean canUseReferenceCacheEntries() {
        return this.useReferenceCacheEntries;
    }

    protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
        return string == null ? null : Template.renderWhereStringTemplate(string, factory.getDialect(), factory.getSqlFunctionRegistry());
    }

    protected Map<String, String> generateLazySelectStringsByFetchGroup() {
        BytecodeEnhancementMetadata enhancementMetadata = this.entityMetamodel.getBytecodeEnhancementMetadata();
        if (!enhancementMetadata.isEnhancedForLazyLoading() || !enhancementMetadata.getLazyAttributesMetadata().hasLazyAttributes()) {
            return Collections.emptyMap();
        }
        HashMap<String, String> result = new HashMap<String, String>();
        LazyAttributesMetadata lazyAttributesMetadata = enhancementMetadata.getLazyAttributesMetadata();
        for (String groupName : lazyAttributesMetadata.getFetchGroupNames()) {
            HashSet<Integer> tableNumbers = new HashSet<Integer>();
            ArrayList<Integer> columnNumbers = new ArrayList<Integer>();
            ArrayList<Integer> formulaNumbers = new ArrayList<Integer>();
            for (LazyAttributeDescriptor lazyAttributeDescriptor : lazyAttributesMetadata.getFetchGroupAttributeDescriptors(groupName)) {
                int[] formNumbers;
                int[] colNumbers;
                int propertyNumber = this.getSubclassPropertyIndex(lazyAttributeDescriptor.getName());
                int tableNumber = this.getSubclassPropertyTableNumber(propertyNumber);
                tableNumbers.add(tableNumber);
                for (int colNumber : colNumbers = this.subclassPropertyColumnNumberClosure[propertyNumber]) {
                    if (colNumber == -1) continue;
                    columnNumbers.add(colNumber);
                }
                for (int formNumber : formNumbers = this.subclassPropertyFormulaNumberClosure[propertyNumber]) {
                    if (formNumber == -1) continue;
                    formulaNumbers.add(formNumber);
                }
            }
            if (columnNumbers.size() == 0 && formulaNumbers.size() == 0) continue;
            result.put(groupName, this.renderSelect(ArrayHelper.toIntArray(tableNumbers), ArrayHelper.toIntArray(columnNumbers), ArrayHelper.toIntArray(formulaNumbers)));
        }
        return result;
    }

    @Override
    public Object initializeLazyProperty(String fieldName, Object entity, SharedSessionContractImplementor session) {
        CacheEntry cacheEntry;
        Object initializedValue;
        EntityDataAccess cacheAccess;
        Object cacheKey;
        Serializable ce;
        Type type;
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        EntityEntry entry = persistenceContext.getEntry(entity);
        PersistentAttributeInterceptor interceptor = ((PersistentAttributeInterceptable)entity).$$_hibernate_getInterceptor();
        assert (interceptor != null) : "Expecting bytecode interceptor to be non-null";
        if (this.hasCollections() && (type = this.getPropertyType(fieldName)).isCollectionType()) {
            EntityEntry ownerEntry;
            Serializable key;
            CollectionType collectionType = (CollectionType)type;
            CollectionPersister persister = this.factory.getMetamodel().collectionPersister(collectionType.getRole());
            PersistentCollection collection = persistenceContext.getCollection(new CollectionKey(persister, key = this.getCollectionKey(persister, entity, entry, session)));
            if (collection == null) {
                collection = collectionType.instantiate(session, persister, key);
                collection.setOwner(entity);
                persistenceContext.addUninitializedCollection(persister, collection, key);
            }
            interceptor.attributeInitialized(fieldName);
            if (collectionType.isArrayType()) {
                persistenceContext.addCollectionHolder(collection);
            }
            if ((ownerEntry = persistenceContext.getEntry(entity)) == null) {
                throw new LazyInitializationException("Could not locate EntityEntry for the collection owner in the PersistenceContext");
            }
            ownerEntry.overwriteLoadedStateCollectionValue(fieldName, collection);
            return collection;
        }
        Serializable id = session.getContextEntityIdentifier(entity);
        if (entry == null) {
            throw new HibernateException("entity is not associated with the session: " + id);
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), fieldName);
        }
        if (session.getCacheMode().isGetEnabled() && this.canReadFromCache() && this.isLazyPropertiesCacheable() && (ce = CacheHelper.fromSharedCache(session, cacheKey = (cacheAccess = this.getCacheAccessStrategy()).generateCacheKey(id, this, session.getFactory(), session.getTenantIdentifier()), cacheAccess)) != null && (initializedValue = this.initializeLazyPropertiesFromCache(fieldName, entity, session, entry, cacheEntry = (CacheEntry)this.getCacheEntryStructure().destructure(ce, this.factory))) != LazyPropertyInitializer.UNFETCHED_PROPERTY) {
            return initializedValue;
        }
        return this.initializeLazyPropertiesFromDatastore(fieldName, entity, session, id, entry);
    }

    protected Serializable getCollectionKey(CollectionPersister persister, Object owner, EntityEntry ownerEntry, SharedSessionContractImplementor session) {
        CollectionType collectionType = persister.getCollectionType();
        if (ownerEntry != null) {
            return collectionType.getKeyOfOwner(owner, session);
        }
        if (collectionType.getLHSPropertyName() == null) {
            return persister.getOwnerEntityPersister().getIdentifier(owner, session);
        }
        return (Serializable)persister.getOwnerEntityPersister().getPropertyValue(owner, collectionType.getLHSPropertyName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object initializeLazyPropertiesFromDatastore(String fieldName, Object entity, SharedSessionContractImplementor session, Serializable id, EntityEntry entry) {
        if (!this.hasLazyProperties()) {
            throw new AssertionFailure("no lazy properties");
        }
        PersistentAttributeInterceptor interceptor = ((PersistentAttributeInterceptable)entity).$$_hibernate_getInterceptor();
        assert (interceptor != null) : "Expecting bytecode interceptor to be non-null";
        LOG.tracef("Initializing lazy properties from datastore (triggered for `%s`)", fieldName);
        String fetchGroup = this.getEntityMetamodel().getBytecodeEnhancementMetadata().getLazyAttributesMetadata().getFetchGroupName(fieldName);
        List<LazyAttributeDescriptor> fetchGroupAttributeDescriptors = this.getEntityMetamodel().getBytecodeEnhancementMetadata().getLazyAttributesMetadata().getFetchGroupAttributeDescriptors(fetchGroup);
        Set<String> initializedLazyAttributeNames = interceptor.getInitializedLazyAttributeNames();
        String lazySelect = this.getSQLLazySelectString(fetchGroup);
        try {
            Object result = null;
            PreparedStatement ps = null;
            try {
                ResultSet rs = null;
                try {
                    if (lazySelect != null) {
                        ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(lazySelect);
                        this.getIdentifierType().nullSafeSet(ps, id, 1, session);
                        rs = session.getJdbcCoordinator().getResultSetReturn().extract(ps);
                        rs.next();
                    }
                    for (LazyAttributeDescriptor fetchGroupAttributeDescriptor : fetchGroupAttributeDescriptors) {
                        boolean previousInitialized = initializedLazyAttributeNames.contains(fetchGroupAttributeDescriptor.getName());
                        if (previousInitialized) continue;
                        Object selectedValue = fetchGroupAttributeDescriptor.getType().nullSafeGet(rs, this.lazyPropertyColumnAliases[fetchGroupAttributeDescriptor.getLazyIndex()], session, entity);
                        boolean set = this.initializeLazyProperty(fieldName, entity, session, entry, fetchGroupAttributeDescriptor.getLazyIndex(), selectedValue);
                        if (!set) continue;
                        result = selectedValue;
                        interceptor.attributeInitialized(fetchGroupAttributeDescriptor.getName());
                    }
                }
                finally {
                    if (rs != null) {
                        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, ps);
                    }
                }
                if (ps != null) {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
            }
            catch (Throwable throwable) {
                if (ps != null) {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
                throw throwable;
            }
            LOG.trace("Done initializing lazy properties");
            return result;
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not initialize lazy properties: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), lazySelect);
        }
    }

    protected Object initializeLazyPropertiesFromCache(String fieldName, Object entity, SharedSessionContractImplementor session, EntityEntry entry, CacheEntry cacheEntry) {
        LOG.trace("Initializing lazy properties from second-level cache");
        Object result = null;
        Serializable[] disassembledValues = cacheEntry.getDisassembledState();
        for (int j = 0; j < this.lazyPropertyNames.length; ++j) {
            Serializable cachedValue = disassembledValues[this.lazyPropertyNumbers[j]];
            Type lazyPropertyType = this.lazyPropertyTypes[j];
            String propertyName = this.lazyPropertyNames[j];
            if (cachedValue == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
                if (!fieldName.equals(propertyName)) continue;
                result = LazyPropertyInitializer.UNFETCHED_PROPERTY;
                continue;
            }
            Object propValue = lazyPropertyType.assemble(cachedValue, session, entity);
            if (!this.initializeLazyProperty(fieldName, entity, session, entry, j, propValue)) continue;
            result = propValue;
        }
        LOG.trace("Done initializing lazy properties");
        return result;
    }

    protected boolean initializeLazyProperty(String fieldName, Object entity, SharedSessionContractImplementor session, EntityEntry entry, int j, Object propValue) {
        this.setPropertyValue(entity, this.lazyPropertyNumbers[j], propValue);
        if (entry.getLoadedState() != null) {
            entry.getLoadedState()[this.lazyPropertyNumbers[j]] = this.lazyPropertyTypes[j].deepCopy(propValue, this.factory);
        }
        if (entry.getDeletedState() != null) {
            entry.getDeletedState()[this.lazyPropertyNumbers[j]] = this.lazyPropertyTypes[j].deepCopy(propValue, this.factory);
        }
        return fieldName.equals(this.lazyPropertyNames[j]);
    }

    public boolean isBatchable() {
        return this.optimisticLockStyle().isNone() || !this.isVersioned() && this.optimisticLockStyle().isVersion() || this.getFactory().getSessionFactoryOptions().isJdbcBatchVersionedData();
    }

    @Override
    public NavigableRole getNavigableRole() {
        return this.navigableRole;
    }

    @Override
    public Serializable[] getQuerySpaces() {
        return this.getPropertySpaces();
    }

    @Override
    public boolean isBatchLoadable() {
        return this.batchSize > 1;
    }

    @Override
    public String[] getIdentifierColumnNames() {
        return this.rootTableKeyColumnNames;
    }

    public String[] getIdentifierColumnReaders() {
        return this.rootTableKeyColumnReaders;
    }

    public String[] getIdentifierColumnReaderTemplates() {
        return this.rootTableKeyColumnReaderTemplates;
    }

    public int getIdentifierColumnSpan() {
        return this.identifierColumnSpan;
    }

    public String[] getIdentifierAliases() {
        return this.identifierAliases;
    }

    @Override
    public String getVersionColumnName() {
        return this.versionColumnName;
    }

    public String getVersionedTableName() {
        return this.getTableName(0);
    }

    protected boolean[] getSubclassColumnLazyiness() {
        return this.subclassColumnLazyClosure;
    }

    protected boolean[] getSubclassFormulaLazyiness() {
        return this.subclassFormulaLazyClosure;
    }

    @Override
    public boolean isCacheInvalidationRequired() {
        return this.invalidateCache;
    }

    @Override
    public boolean isLazyPropertiesCacheable() {
        return this.isLazyPropertiesCacheable;
    }

    @Override
    public String selectFragment(String alias, String suffix) {
        return this.identifierSelectFragment(alias, suffix) + this.propertySelectFragment(alias, suffix, false);
    }

    @Override
    public String[] getIdentifierAliases(String suffix) {
        return new Alias(suffix).toAliasStrings(this.getIdentifierAliases());
    }

    @Override
    public String[] getPropertyAliases(String suffix, int i) {
        return new Alias(suffix).toUnquotedAliasStrings(this.propertyColumnAliases[i]);
    }

    @Override
    public String getDiscriminatorAlias(String suffix) {
        return this.entityMetamodel.hasSubclasses() ? new Alias(suffix).toAliasString(this.getDiscriminatorAlias()) : null;
    }

    @Override
    public String identifierSelectFragment(String name, String suffix) {
        return new SelectFragment().setSuffix(suffix).addColumns(name, this.getIdentifierColumnNames(), this.getIdentifierAliases()).toFragmentString().substring(2);
    }

    @Override
    public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
        return this.propertySelectFragmentFragment(tableAlias, suffix, allProperties).toFragmentString();
    }

    @Override
    public SelectFragment propertySelectFragmentFragment(String tableAlias, String suffix, boolean allProperties) {
        SelectFragment select = new SelectFragment().setSuffix(suffix).setUsedAliases(this.getIdentifierAliases());
        int[] columnTableNumbers = this.getSubclassColumnTableNumberClosure();
        String[] columnAliases = this.getSubclassColumnAliasClosure();
        String[] columnReaderTemplates = this.getSubclassColumnReaderTemplateClosure();
        for (int i = 0; i < this.getSubclassColumnClosure().length; ++i) {
            boolean selectable;
            boolean bl = selectable = (allProperties || !this.subclassColumnLazyClosure[i]) && !this.isSubclassTableSequentialSelect(columnTableNumbers[i]) && this.subclassColumnSelectableClosure[i];
            if (!selectable) continue;
            String subalias = AbstractEntityPersister.generateTableAlias(tableAlias, columnTableNumbers[i]);
            select.addColumnTemplate(subalias, columnReaderTemplates[i], columnAliases[i]);
        }
        int[] formulaTableNumbers = this.getSubclassFormulaTableNumberClosure();
        String[] formulaTemplates = this.getSubclassFormulaTemplateClosure();
        String[] formulaAliases = this.getSubclassFormulaAliasClosure();
        for (int i = 0; i < this.getSubclassFormulaTemplateClosure().length; ++i) {
            boolean selectable;
            boolean bl = selectable = (allProperties || !this.subclassFormulaLazyClosure[i]) && !this.isSubclassTableSequentialSelect(formulaTableNumbers[i]);
            if (!selectable) continue;
            String subalias = AbstractEntityPersister.generateTableAlias(tableAlias, formulaTableNumbers[i]);
            select.addFormula(subalias, formulaTemplates[i], formulaAliases[i]);
        }
        if (this.entityMetamodel.hasSubclasses()) {
            this.addDiscriminatorToSelect(select, tableAlias, suffix);
        }
        if (this.hasRowId()) {
            select.addColumn(tableAlias, this.rowIdName, "rowid_");
        }
        return select;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    public Object[] getDatabaseSnapshot(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Getting current persistent state for: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
        }
        try {
            PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.getSQLSnapshotSelectString());
            try {
                ResultSet rs;
                block12: {
                    this.getIdentifierType().nullSafeSet(ps, id, 1, session);
                    rs = session.getJdbcCoordinator().getResultSetReturn().extract(ps);
                    if (rs.next()) break block12;
                    Object[] objectArray = null;
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, ps);
                    return objectArray;
                }
                try {
                    Type[] types = this.getPropertyTypes();
                    Object[] values = new Object[types.length];
                    boolean[] includeProperty = this.getPropertyUpdateability();
                    for (int i = 0; i < types.length; ++i) {
                        if (!includeProperty[i]) continue;
                        values[i] = types[i].hydrate(rs, this.getPropertyAliases("", i), session, null);
                    }
                    Object[] objectArray = values;
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, ps);
                    return objectArray;
                }
                catch (Throwable throwable) {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, ps);
                    throw throwable;
                }
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not retrieve snapshot: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), this.getSQLSnapshotSelectString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SharedSessionContractImplementor session) throws HibernateException {
        int propertyIndex;
        if (LOG.isTraceEnabled()) {
            LOG.tracef("resolving unique key [%s] to identifier for entity [%s]", key, this.getEntityName());
        }
        if ((propertyIndex = this.getSubclassPropertyIndex(uniquePropertyName)) < 0) {
            throw new HibernateException("Could not determine Type for property [" + uniquePropertyName + "] on entity [" + this.getEntityName() + "]");
        }
        Type propertyType = this.getSubclassPropertyType(propertyIndex);
        try {
            PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.generateIdByUniqueKeySelectString(uniquePropertyName));
            try {
                ResultSet rs;
                block12: {
                    propertyType.nullSafeSet(ps, key, 1, session);
                    rs = session.getJdbcCoordinator().getResultSetReturn().extract(ps);
                    try {
                        if (rs.next()) break block12;
                        Serializable serializable = null;
                        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, ps);
                        return serializable;
                    }
                    catch (Throwable throwable) {
                        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, ps);
                        throw throwable;
                    }
                }
                Serializable serializable = (Serializable)this.getIdentifierType().nullSafeGet(rs, this.getIdentifierAliases(), session, null);
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, ps);
                return serializable;
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, String.format("could not resolve unique property [%s] to identifier for entity [%s]", uniquePropertyName, this.getEntityName()), this.getSQLSnapshotSelectString());
        }
    }

    public String generateIdByUniqueKeySelectString(String uniquePropertyName) {
        Select select = new Select(this.getFactory().getDialect());
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("resolve id by unique property [" + this.getEntityName() + "." + uniquePropertyName + "]");
        }
        String rooAlias = this.getRootAlias();
        select.setFromClause(this.fromTableFragment(rooAlias) + this.fromJoinFragment(rooAlias, true, false));
        SelectFragment selectFragment = new SelectFragment();
        selectFragment.addColumns(rooAlias, this.getIdentifierColumnNames(), this.getIdentifierAliases());
        select.setSelectClause(selectFragment);
        StringBuilder whereClauseBuffer = new StringBuilder();
        int uniquePropertyIndex = this.getSubclassPropertyIndex(uniquePropertyName);
        String uniquePropertyTableAlias = AbstractEntityPersister.generateTableAlias(rooAlias, this.getSubclassPropertyTableNumber(uniquePropertyIndex));
        String sep = "";
        for (String columnTemplate : this.getSubclassPropertyColumnReaderTemplateClosure()[uniquePropertyIndex]) {
            if (columnTemplate == null) continue;
            String columnReference = StringHelper.replace(columnTemplate, "$PlaceHolder$", uniquePropertyTableAlias);
            whereClauseBuffer.append(sep).append(columnReference).append("=?");
            sep = " and ";
        }
        for (String formulaTemplate : this.getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex]) {
            if (formulaTemplate == null) continue;
            String formulaReference = StringHelper.replace(formulaTemplate, "$PlaceHolder$", uniquePropertyTableAlias);
            whereClauseBuffer.append(sep).append(formulaReference).append("=?");
            sep = " and ";
        }
        whereClauseBuffer.append(this.whereJoinFragment(rooAlias, true, false));
        select.setWhereClause(whereClauseBuffer.toString());
        return select.setOuterJoins("", "").toStatementString();
    }

    public String generateSelectVersionString() {
        SimpleSelect select = new SimpleSelect(this.getFactory().getDialect()).setTableName(this.getVersionedTableName());
        if (this.isVersioned()) {
            select.addColumn(this.getVersionColumnName(), VERSION_COLUMN_ALIAS);
        } else {
            select.addColumns(this.rootTableKeyColumnNames);
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("get version " + this.getEntityName());
        }
        return select.addCondition(this.rootTableKeyColumnNames, "=?").toStatementString();
    }

    public boolean[] getPropertyUniqueness() {
        return this.propertyUniqueness;
    }

    public String generateInsertGeneratedValuesSelectString() {
        return this.generateGeneratedValuesSelectString(GenerationTiming.INSERT);
    }

    public String generateUpdateGeneratedValuesSelectString() {
        return this.generateGeneratedValuesSelectString(GenerationTiming.ALWAYS);
    }

    private String generateGeneratedValuesSelectString(GenerationTiming generationTimingToMatch) {
        Select select = new Select(this.getFactory().getDialect());
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("get generated state " + this.getEntityName());
        }
        CharSequence[] aliasedIdColumns = StringHelper.qualify(this.getRootAlias(), this.getIdentifierColumnNames());
        String selectClause = this.concretePropertySelectFragment(this.getRootAlias(), propertyNumber -> {
            InDatabaseValueGenerationStrategy generationStrategy = this.entityMetamodel.getInDatabaseValueGenerationStrategies()[propertyNumber];
            GenerationTiming timing = generationStrategy.getGenerationTiming();
            return generationStrategy != null && (generationTimingToMatch == GenerationTiming.INSERT && timing.includesInsert() || generationTimingToMatch == GenerationTiming.ALWAYS && timing.includesUpdate());
        });
        selectClause = selectClause.substring(2);
        String fromClause = this.fromTableFragment(this.getRootAlias()) + this.fromJoinFragment(this.getRootAlias(), true, false);
        String whereClause = String.join((CharSequence)"=? and ", aliasedIdColumns) + "=?" + this.whereJoinFragment(this.getRootAlias(), true, false);
        return select.setSelectClause(selectClause).setFromClause(fromClause).setOuterJoins("", "").setWhereClause(whereClause).toStatementString();
    }

    protected String concretePropertySelectFragment(String alias, boolean[] includeProperty) {
        return this.concretePropertySelectFragment(alias, propertyNumber -> includeProperty[propertyNumber]);
    }

    protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
        int propertyCount = this.getPropertyNames().length;
        int[] propertyTableNumbers = this.getPropertyTableNumbersInSelect();
        SelectFragment frag = new SelectFragment();
        for (int i = 0; i < propertyCount; ++i) {
            if (!inclusionChecker.includeProperty(i)) continue;
            frag.addColumnTemplates(AbstractEntityPersister.generateTableAlias(alias, propertyTableNumbers[i]), this.propertyColumnReaderTemplates[i], this.propertyColumnAliases[i]);
            frag.addFormulas(AbstractEntityPersister.generateTableAlias(alias, propertyTableNumbers[i]), this.propertyColumnFormulaTemplates[i], this.propertyColumnAliases[i]);
        }
        return frag.toFragmentString();
    }

    public String generateSnapshotSelectString() {
        Select select = new Select(this.getFactory().getDialect());
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("get current state " + this.getEntityName());
        }
        CharSequence[] aliasedIdColumns = StringHelper.qualify(this.getRootAlias(), this.getIdentifierColumnNames());
        String selectClause = String.join((CharSequence)", ", aliasedIdColumns) + this.concretePropertySelectFragment(this.getRootAlias(), this.getPropertyUpdateability());
        String fromClause = this.fromTableFragment(this.getRootAlias()) + this.fromJoinFragment(this.getRootAlias(), true, false);
        String whereClause = String.join((CharSequence)"=? and ", aliasedIdColumns) + "=?" + this.whereJoinFragment(this.getRootAlias(), true, false);
        return select.setSelectClause(selectClause).setFromClause(fromClause).setOuterJoins("", "").setWhereClause(whereClause).toStatementString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object forceVersionIncrement(Serializable id, Object currentVersion, SharedSessionContractImplementor session) {
        if (!this.isVersioned()) {
            throw new AssertionFailure("cannot force version increment on non-versioned entity");
        }
        if (this.isVersionPropertyGenerated()) {
            throw new HibernateException("LockMode.FORCE is currently not supported for generated version properties");
        }
        Object nextVersion = this.getVersionType().next(currentVersion, session);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Forcing version increment [" + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()) + "; " + this.getVersionType().toLoggableString(currentVersion, this.getFactory()) + " -> " + this.getVersionType().toLoggableString(nextVersion, this.getFactory()) + "]");
        }
        String versionIncrementString = this.generateVersionIncrementUpdateString();
        try {
            PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(versionIncrementString, false);
            try {
                this.getVersionType().nullSafeSet(st, nextVersion, 1, session);
                this.getIdentifierType().nullSafeSet(st, id, 2, session);
                this.getVersionType().nullSafeSet(st, currentVersion, 2 + this.getIdentifierColumnSpan(), session);
                int rows = session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st);
                if (rows != 1) {
                    throw new StaleObjectStateException(this.getEntityName(), id);
                }
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException sqle) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not retrieve version: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), this.getVersionSelectString());
        }
        return nextVersion;
    }

    private String generateVersionIncrementUpdateString() {
        Update update = this.createUpdate().setTableName(this.getTableName(0));
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment("forced version increment");
        }
        update.addColumn(this.getVersionColumnName());
        update.addPrimaryKeyColumns(this.rootTableKeyColumnNames);
        update.setVersionColumnName(this.getVersionColumnName());
        return update.toStatementString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    public Object getCurrentVersion(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Getting version: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
        }
        try {
            PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.getVersionSelectString());
            try {
                ResultSet rs;
                block14: {
                    block13: {
                        this.getIdentifierType().nullSafeSet(st, id, 1, session);
                        rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
                        try {
                            if (rs.next()) break block13;
                            Object var5_6 = null;
                            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                            return var5_6;
                        }
                        catch (Throwable throwable) {
                            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                            throw throwable;
                        }
                    }
                    if (this.isVersioned()) break block14;
                    AbstractEntityPersister abstractEntityPersister = this;
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                    return abstractEntityPersister;
                }
                Object object = this.getVersionType().nullSafeGet(rs, VERSION_COLUMN_ALIAS, session, null);
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, st);
                return object;
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not retrieve version: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), this.getVersionSelectString());
        }
    }

    protected LockingStrategy generateLocker(LockMode lockMode) {
        return this.factory.getDialect().getLockingStrategy(this, lockMode);
    }

    private LockingStrategy getLocker(LockMode lockMode) {
        return this.lockers.computeIfAbsent(lockMode, this::generateLocker);
    }

    @Override
    public void lock(Serializable id, Object version, Object object, LockMode lockMode, SharedSessionContractImplementor session) throws HibernateException {
        this.getLocker(lockMode).lock(id, version, object, -1, session);
    }

    @Override
    public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SharedSessionContractImplementor session) throws HibernateException {
        this.getLocker(lockOptions.getLockMode()).lock(id, version, object, lockOptions.getTimeOut(), session);
    }

    @Override
    public String getRootTableName() {
        return this.getSubclassTableName(0);
    }

    @Override
    public String getRootTableAlias(String drivingAlias) {
        return drivingAlias;
    }

    @Override
    public String[] getRootTableIdentifierColumnNames() {
        return this.getRootTableKeyColumnNames();
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        return this.propertyMapping.toColumns(alias, propertyName);
    }

    @Override
    public String[] toColumns(String propertyName) throws QueryException {
        return this.propertyMapping.getColumnNames(propertyName);
    }

    @Override
    public Type toType(String propertyName) throws QueryException {
        return this.propertyMapping.toType(propertyName);
    }

    @Override
    public String[] getPropertyColumnNames(String propertyName) {
        return this.propertyMapping.getColumnNames(propertyName);
    }

    @Override
    public int getSubclassPropertyTableNumber(String propertyPath) {
        int index;
        String rootPropertyName = StringHelper.root(propertyPath);
        Type type = this.propertyMapping.toType(rootPropertyName);
        if (type.isAssociationType()) {
            AssociationType assocType = (AssociationType)type;
            if (assocType.useLHSPrimaryKey()) {
                return 0;
            }
            if (type.isCollectionType()) {
                rootPropertyName = assocType.getLHSPropertyName();
            }
        }
        return (index = ArrayHelper.indexOf(this.getSubclassPropertyNameClosure(), rootPropertyName)) == -1 ? 0 : this.getSubclassPropertyTableNumber(index);
    }

    @Override
    public Queryable.Declarer getSubclassPropertyDeclarer(String propertyPath) {
        int tableIndex = this.getSubclassPropertyTableNumber(propertyPath);
        if (tableIndex == 0) {
            return Queryable.Declarer.CLASS;
        }
        if (this.isClassOrSuperclassTable(tableIndex)) {
            return Queryable.Declarer.SUPERCLASS;
        }
        return Queryable.Declarer.SUBCLASS;
    }

    @Override
    public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
        if (this.discriminatorMetadata == null) {
            this.discriminatorMetadata = this.buildTypeDiscriminatorMetadata();
        }
        return this.discriminatorMetadata;
    }

    private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
        return new DiscriminatorMetadata(){

            @Override
            public String getSqlFragment(String sqlQualificationAlias) {
                return AbstractEntityPersister.this.toColumns(sqlQualificationAlias, AbstractEntityPersister.ENTITY_CLASS)[0];
            }

            @Override
            public Type getResolutionType() {
                return new DiscriminatorType(AbstractEntityPersister.this.getDiscriminatorType(), AbstractEntityPersister.this);
            }
        };
    }

    public static String generateTableAlias(String rootAlias, int tableNumber) {
        if (tableNumber == 0) {
            return rootAlias;
        }
        StringBuilder buf = new StringBuilder().append(rootAlias);
        if (!rootAlias.endsWith("_")) {
            buf.append('_');
        }
        return buf.append(tableNumber).append('_').toString();
    }

    @Override
    public String[] toColumns(String name, int i) {
        String alias = AbstractEntityPersister.generateTableAlias(name, this.getSubclassPropertyTableNumber(i));
        String[] cols = this.getSubclassPropertyColumnNames(i);
        String[] templates = this.getSubclassPropertyFormulaTemplateClosure()[i];
        String[] result = new String[cols.length];
        for (int j = 0; j < cols.length; ++j) {
            result[j] = cols[j] == null ? StringHelper.replace(templates[j], "$PlaceHolder$", alias) : StringHelper.qualify(alias, cols[j]);
        }
        return result;
    }

    private int getSubclassPropertyIndex(String propertyName) {
        return ArrayHelper.indexOf(this.subclassPropertyNameClosure, propertyName);
    }

    protected String[] getPropertySubclassNames() {
        return this.propertySubclassNames;
    }

    @Override
    public String[] getPropertyColumnNames(int i) {
        return this.propertyColumnNames[i];
    }

    public String[] getPropertyColumnWriters(int i) {
        return this.propertyColumnWriters[i];
    }

    public int getPropertyColumnSpan(int i) {
        return this.propertyColumnSpans[i];
    }

    public boolean hasFormulaProperties() {
        return this.hasFormulaProperties;
    }

    @Override
    public FetchMode getFetchMode(int i) {
        return this.subclassPropertyFetchModeClosure[i];
    }

    @Override
    public CascadeStyle getCascadeStyle(int i) {
        return this.subclassPropertyCascadeStyleClosure[i];
    }

    @Override
    public Type getSubclassPropertyType(int i) {
        return this.subclassPropertyTypeClosure[i];
    }

    @Override
    public String getSubclassPropertyName(int i) {
        return this.subclassPropertyNameClosure[i];
    }

    @Override
    public int countSubclassProperties() {
        return this.subclassPropertyTypeClosure.length;
    }

    @Override
    public String[] getSubclassPropertyColumnNames(int i) {
        return this.subclassPropertyColumnNameClosure[i];
    }

    @Override
    public boolean isDefinedOnSubclass(int i) {
        return this.propertyDefinedOnSubclass[i];
    }

    @Override
    public String[][] getSubclassPropertyFormulaTemplateClosure() {
        return this.subclassPropertyFormulaTemplateClosure;
    }

    protected Type[] getSubclassPropertyTypeClosure() {
        return this.subclassPropertyTypeClosure;
    }

    protected String[][] getSubclassPropertyColumnNameClosure() {
        return this.subclassPropertyColumnNameClosure;
    }

    public String[][] getSubclassPropertyColumnReaderClosure() {
        return this.subclassPropertyColumnReaderClosure;
    }

    public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
        return this.subclassPropertyColumnReaderTemplateClosure;
    }

    protected String[] getSubclassPropertyNameClosure() {
        return this.subclassPropertyNameClosure;
    }

    @Override
    public int[] resolveAttributeIndexes(String[] attributeNames) {
        if (attributeNames == null || attributeNames.length == 0) {
            return new int[0];
        }
        int[] fields = new int[attributeNames.length];
        int counter = 0;
        Arrays.sort(attributeNames);
        Integer index0 = this.entityMetamodel.getPropertyIndexOrNull(attributeNames[0]);
        if (index0 != null) {
            fields[counter++] = index0;
        }
        int i = 0;
        for (int j = 1; j < attributeNames.length; ++j) {
            Integer index;
            if (!attributeNames[i].equals(attributeNames[j]) && (index = this.entityMetamodel.getPropertyIndexOrNull(attributeNames[j])) != null) {
                fields[counter++] = index;
            }
            ++i;
        }
        return Arrays.copyOf(fields, counter);
    }

    @Override
    public int[] resolveDirtyAttributeIndexes(Object[] currentState, Object[] previousState, String[] attributeNames, SessionImplementor session) {
        BitSet mutablePropertiesIndexes = this.entityMetamodel.getMutablePropertiesIndexes();
        int estimatedSize = attributeNames == null ? 0 : attributeNames.length + mutablePropertiesIndexes.cardinality();
        ArrayList<Integer> fields = new ArrayList<Integer>(estimatedSize);
        if (estimatedSize == 0) {
            return ArrayHelper.EMPTY_INT_ARRAY;
        }
        if (!mutablePropertiesIndexes.isEmpty()) {
            Type[] propertyTypes = this.entityMetamodel.getPropertyTypes();
            boolean[] propertyCheckability = this.entityMetamodel.getPropertyCheckability();
            int i = mutablePropertiesIndexes.nextSetBit(0);
            while (i >= 0) {
                boolean dirty;
                boolean bl = dirty = currentState[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY && (previousState == null || previousState[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || propertyCheckability[i] && propertyTypes[i].isDirty(previousState[i], currentState[i], this.propertyColumnUpdateable[i], session));
                if (dirty) {
                    fields.add(i);
                }
                i = mutablePropertiesIndexes.nextSetBit(i + 1);
            }
        }
        if (attributeNames != null) {
            boolean[] propertyUpdateability = this.entityMetamodel.getPropertyUpdateability();
            for (String attributeName : attributeNames) {
                Integer index = this.entityMetamodel.getPropertyIndexOrNull(attributeName);
                if (index == null || !propertyUpdateability[index] || fields.contains(index)) continue;
                fields.add(index);
            }
        }
        return ArrayHelper.toIntArray(fields);
    }

    protected String[] getSubclassPropertySubclassNameClosure() {
        return this.subclassPropertySubclassNameClosure;
    }

    protected String[] getSubclassColumnClosure() {
        return this.subclassColumnClosure;
    }

    protected String[] getSubclassColumnAliasClosure() {
        return this.subclassColumnAliasClosure;
    }

    public String[] getSubclassColumnReaderTemplateClosure() {
        return this.subclassColumnReaderTemplateClosure;
    }

    protected String[] getSubclassFormulaClosure() {
        return this.subclassFormulaClosure;
    }

    protected String[] getSubclassFormulaTemplateClosure() {
        return this.subclassFormulaTemplateClosure;
    }

    protected String[] getSubclassFormulaAliasClosure() {
        return this.subclassFormulaAliasClosure;
    }

    @Override
    public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
        String[] rawAliases = (String[])this.subclassPropertyAliases.get(propertyName);
        if (rawAliases == null) {
            return null;
        }
        String[] result = new String[rawAliases.length];
        for (int i = 0; i < rawAliases.length; ++i) {
            result[i] = new Alias(suffix).toUnquotedAliasString(rawAliases[i]);
        }
        return result;
    }

    @Override
    public String[] getSubclassPropertyColumnNames(String propertyName) {
        return (String[])this.subclassPropertyColumnNames.get(propertyName);
    }

    protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
        this.internalInitSubclassPropertyAliasesMap(null, model.getSubclassPropertyClosureIterator());
        if (!this.entityMetamodel.hasNonIdentifierPropertyNamedId()) {
            this.subclassPropertyAliases.put("id", this.getIdentifierAliases());
            this.subclassPropertyColumnNames.put("id", this.getIdentifierColumnNames());
        }
        if (this.hasIdentifierProperty()) {
            this.subclassPropertyAliases.put(this.getIdentifierPropertyName(), this.getIdentifierAliases());
            this.subclassPropertyColumnNames.put(this.getIdentifierPropertyName(), this.getIdentifierColumnNames());
        }
        if (this.getIdentifierType().isComponentType()) {
            CompositeType componentId = (CompositeType)this.getIdentifierType();
            String[] idPropertyNames = componentId.getPropertyNames();
            String[] idAliases = this.getIdentifierAliases();
            String[] idColumnNames = this.getIdentifierColumnNames();
            for (int i = 0; i < idPropertyNames.length; ++i) {
                if (this.entityMetamodel.hasNonIdentifierPropertyNamedId()) {
                    this.subclassPropertyAliases.put("id." + idPropertyNames[i], new String[]{idAliases[i]});
                    this.subclassPropertyColumnNames.put("id." + this.getIdentifierPropertyName() + "." + idPropertyNames[i], new String[]{idColumnNames[i]});
                }
                if (this.hasIdentifierProperty()) {
                    this.subclassPropertyAliases.put(this.getIdentifierPropertyName() + "." + idPropertyNames[i], new String[]{idAliases[i]});
                    this.subclassPropertyColumnNames.put(this.getIdentifierPropertyName() + "." + idPropertyNames[i], new String[]{idColumnNames[i]});
                    continue;
                }
                this.subclassPropertyAliases.put(idPropertyNames[i], new String[]{idAliases[i]});
                this.subclassPropertyColumnNames.put(idPropertyNames[i], new String[]{idColumnNames[i]});
            }
        }
        if (this.entityMetamodel.isPolymorphic()) {
            this.subclassPropertyAliases.put(ENTITY_CLASS, new String[]{this.getDiscriminatorAlias()});
            this.subclassPropertyColumnNames.put(ENTITY_CLASS, new String[]{this.getDiscriminatorColumnName()});
        }
    }

    private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
        while (propertyIterator.hasNext()) {
            String propname;
            Property prop = (Property)propertyIterator.next();
            String string = propname = path == null ? prop.getName() : path + "." + prop.getName();
            if (prop.isComposite()) {
                Component component = (Component)prop.getValue();
                Iterator compProps = component.getPropertyIterator();
                this.internalInitSubclassPropertyAliasesMap(propname, compProps);
                continue;
            }
            String[] aliases = new String[prop.getColumnSpan()];
            String[] cols = new String[prop.getColumnSpan()];
            Iterator colIter = prop.getColumnIterator();
            int l = 0;
            while (colIter.hasNext()) {
                Selectable thing = (Selectable)colIter.next();
                aliases[l] = thing.getAlias(this.getFactory().getDialect(), prop.getValue().getTable());
                cols[l] = thing.getText(this.getFactory().getDialect());
                ++l;
            }
            this.subclassPropertyAliases.put(propname, aliases);
            this.subclassPropertyColumnNames.put(propname, cols);
        }
    }

    protected int[] getLazyPropertyNumbers() {
        return this.lazyPropertyNumbers;
    }

    protected String[] getLazyPropertyNames() {
        return this.lazyPropertyNames;
    }

    protected Type[] getLazyPropertyTypes() {
        return this.lazyPropertyTypes;
    }

    protected String[][] getLazyPropertyColumnAliases() {
        return this.lazyPropertyColumnAliases;
    }

    @Override
    public Object loadByUniqueKey(String propertyName, Object uniqueKey, SharedSessionContractImplementor session) throws HibernateException {
        return this.getAppropriateUniqueKeyLoader(propertyName, session).load(uniqueKey, session, LockOptions.NONE);
    }

    @Override
    public Object loadByNaturalId(Object[] naturalIdValues, LockOptions lockOptions, SharedSessionContractImplementor session) throws HibernateException {
        return this.getAppropriateNaturalIdLoader(AbstractEntityPersister.determineValueNullness(naturalIdValues), lockOptions, session).load(naturalIdValues, session, LockOptions.NONE);
    }

    private EntityLoader getAppropriateNaturalIdLoader(boolean[] valueNullness, LockOptions lockOptions, SharedSessionContractImplementor session) {
        LoadQueryInfluencers loadQueryInfluencers = session.getLoadQueryInfluencers();
        return this.useStaticNaturalIdLoader(valueNullness, lockOptions, loadQueryInfluencers) ? this.naturalIdLoaders.get((Object)lockOptions.getLockMode()) : this.createNaturalIdLoader(valueNullness, lockOptions, loadQueryInfluencers);
    }

    private boolean useStaticNaturalIdLoader(boolean[] valueNullness, LockOptions lockOptions, LoadQueryInfluencers loadQueryInfluencers) {
        return lockOptions.getTimeOut() == -1 && ArrayHelper.isAllFalse(valueNullness) && !loadQueryInfluencers.hasEnabledFilters() && !loadQueryInfluencers.hasEnabledFetchProfiles();
    }

    protected UniqueEntityLoader getAppropriateUniqueKeyLoader(String propertyName, SharedSessionContractImplementor session) {
        LoadQueryInfluencers loadQueryInfluencers = session.getLoadQueryInfluencers();
        return this.useStaticUniqueKeyLoader(propertyName, loadQueryInfluencers) ? this.uniqueKeyLoaders.get(propertyName) : this.createUniqueKeyLoader(this.propertyMapping.toType(propertyName), this.propertyMapping.toColumns(propertyName), loadQueryInfluencers);
    }

    private boolean useStaticUniqueKeyLoader(String propertyName, LoadQueryInfluencers loadQueryInfluencers) {
        return !loadQueryInfluencers.hasEnabledFilters() && !loadQueryInfluencers.hasEnabledFetchProfiles() && propertyName.indexOf(46) < 0;
    }

    @Override
    public int getPropertyIndex(String propertyName) {
        return this.entityMetamodel.getPropertyIndex(propertyName);
    }

    protected void createUniqueKeyLoaders() throws MappingException {
        Type[] propertyTypes = this.getPropertyTypes();
        String[] propertyNames = this.getPropertyNames();
        for (int i = 0; i < this.propertyUniqueness.length; ++i) {
            if (!this.propertyUniqueness[i]) continue;
            if (this.uniqueKeyLoaders == null) {
                this.uniqueKeyLoaders = new HashMap<String, UniqueEntityLoader>();
            }
            this.uniqueKeyLoaders.put(propertyNames[i], this.createUniqueKeyLoader(propertyTypes[i], this.getPropertyColumnNames(i), LoadQueryInfluencers.NONE));
        }
        if (this.uniqueKeyLoaders == null) {
            this.uniqueKeyLoaders = Collections.emptyMap();
        }
    }

    protected UniqueEntityLoader createUniqueKeyLoader(Type uniqueKeyType, String[] columns, LoadQueryInfluencers loadQueryInfluencers) {
        if (uniqueKeyType.isEntityType()) {
            String className = ((EntityType)uniqueKeyType).getAssociatedEntityName();
            uniqueKeyType = this.getFactory().getMetamodel().entityPersister(className).getIdentifierType();
        }
        return new EntityLoader((OuterJoinLoadable)this, columns, uniqueKeyType, 1, LockMode.NONE, this.getFactory(), loadQueryInfluencers);
    }

    protected void createNaturalIdLoaders() throws MappingException {
        if (this.hasNaturalIdentifier()) {
            this.naturalIdLoaders = new HashMap<LockMode, EntityLoader>();
            boolean[] valueNullness = new boolean[this.getNaturalIdentifierProperties().length];
            for (LockMode lockMode : LockMode.values()) {
                this.naturalIdLoaders.put(lockMode, this.createNaturalIdLoader(valueNullness, new LockOptions(lockMode), LoadQueryInfluencers.NONE));
            }
        } else {
            this.naturalIdLoaders = Collections.emptyMap();
        }
    }

    private EntityLoader createNaturalIdLoader(boolean[] valueNullness, LockOptions lockOptions, LoadQueryInfluencers loadQueryInfluencers) {
        return new EntityLoader(this, valueNullness, 1, lockOptions, this.getFactory(), loadQueryInfluencers);
    }

    protected String getSQLWhereString(String alias) {
        return StringHelper.replace(this.sqlWhereStringTemplate, "$PlaceHolder$", alias);
    }

    protected boolean hasWhere() {
        return this.sqlWhereString != null;
    }

    private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
        for (int i = 0; i < this.getSubclassPropertyNameClosure().length; ++i) {
            this.propertyMapping.initPropertyPaths(this.getSubclassPropertyNameClosure()[i], this.getSubclassPropertyTypeClosure()[i], this.getSubclassPropertyColumnNameClosure()[i], this.getSubclassPropertyColumnReaderClosure()[i], this.getSubclassPropertyColumnReaderTemplateClosure()[i], this.getSubclassPropertyFormulaTemplateClosure()[i], mapping);
        }
    }

    private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
        String idProp = this.getIdentifierPropertyName();
        if (idProp != null) {
            this.propertyMapping.initPropertyPaths(idProp, this.getIdentifierType(), this.getIdentifierColumnNames(), this.getIdentifierColumnReaders(), this.getIdentifierColumnReaderTemplates(), null, mapping);
        }
        if (this.entityMetamodel.getIdentifierProperty().isEmbedded()) {
            this.propertyMapping.initPropertyPaths(null, this.getIdentifierType(), this.getIdentifierColumnNames(), this.getIdentifierColumnReaders(), this.getIdentifierColumnReaderTemplates(), null, mapping);
        }
        if (!this.entityMetamodel.hasNonIdentifierPropertyNamedId()) {
            this.propertyMapping.initPropertyPaths("id", this.getIdentifierType(), this.getIdentifierColumnNames(), this.getIdentifierColumnReaders(), this.getIdentifierColumnReaderTemplates(), null, mapping);
        }
    }

    private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
        this.propertyMapping.initPropertyPaths(ENTITY_CLASS, this.getDiscriminatorType(), new String[]{this.getDiscriminatorColumnName()}, new String[]{this.getDiscriminatorColumnReaders()}, new String[]{this.getDiscriminatorColumnReaderTemplate()}, new String[]{this.getDiscriminatorFormulaTemplate()}, this.getFactory());
    }

    protected void initPropertyPaths(Mapping mapping) throws MappingException {
        this.initOrdinaryPropertyPaths(mapping);
        this.initOrdinaryPropertyPaths(mapping);
        this.initIdentifierPropertyPaths(mapping);
        if (this.entityMetamodel.isPolymorphic()) {
            this.initDiscriminatorPropertyPath(mapping);
        }
    }

    protected UniqueEntityLoader createEntityLoader(LockMode lockMode, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        return BatchingEntityLoaderBuilder.getBuilder(this.getFactory()).buildLoader((OuterJoinLoadable)this, this.batchSize, lockMode, this.getFactory(), loadQueryInfluencers);
    }

    protected UniqueEntityLoader createEntityLoader(LockOptions lockOptions, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        return BatchingEntityLoaderBuilder.getBuilder(this.getFactory()).buildLoader((OuterJoinLoadable)this, this.batchSize, lockOptions, this.getFactory(), loadQueryInfluencers);
    }

    protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
        return this.createEntityLoader(lockMode, LoadQueryInfluencers.NONE);
    }

    protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement, String statementSQL) throws HibernateException {
        try {
            expectation.verifyOutcome(rows, statement, -1, statementSQL);
        }
        catch (StaleStateException e) {
            if (!this.isNullableTable(tableNumber)) {
                StatisticsImplementor statistics = this.getFactory().getStatistics();
                if (statistics.isStatisticsEnabled()) {
                    statistics.optimisticFailure(this.getEntityName());
                }
                throw new StaleObjectStateException(this.getEntityName(), id);
            }
        }
        catch (TooManyRowsAffectedException e) {
            throw new HibernateException("Duplicate identifier in table for: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
        }
        finally {
            return false;
        }
        return true;
    }

    public String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
        return this.generateUpdateString(includeProperty, j, null, useRowId);
    }

    public String generateUpdateString(boolean[] includeProperty, int j, Object[] oldFields, boolean useRowId) {
        Update update = this.createUpdate().setTableName(this.getTableName(j));
        boolean hasColumns = false;
        for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
            if (!includeProperty[i] || !this.isPropertyOfTable(i, j) || this.lobProperties.contains(i)) continue;
            update.addColumns(this.getPropertyColumnNames(i), this.propertyColumnUpdateable[i], this.propertyColumnWriters[i]);
            hasColumns = hasColumns || this.getPropertyColumnSpan(i) > 0;
        }
        for (int i : this.lobProperties) {
            if (!includeProperty[i] || !this.isPropertyOfTable(i, j)) continue;
            update.addColumns(this.getPropertyColumnNames(i), this.propertyColumnUpdateable[i], this.propertyColumnWriters[i]);
            hasColumns = true;
        }
        if (useRowId) {
            update.addPrimaryKeyColumns(new String[]{this.rowIdName});
        } else {
            update.addPrimaryKeyColumns(this.getKeyColumns(j));
        }
        if (j == 0 && this.isVersioned() && this.entityMetamodel.getOptimisticLockStyle().isVersion()) {
            if (this.checkVersion(includeProperty)) {
                update.setVersionColumnName(this.getVersionColumnName());
                hasColumns = true;
            }
        } else if (this.isAllOrDirtyOptLocking() && oldFields != null) {
            boolean[] includeInWhere = this.entityMetamodel.getOptimisticLockStyle().isAll() ? this.getPropertyUpdateability() : includeProperty;
            boolean[] versionability = this.getPropertyVersionability();
            Type[] types = this.getPropertyTypes();
            for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
                boolean include;
                boolean bl = include = includeInWhere[i] && this.isPropertyOfTable(i, j) && versionability[i];
                if (!include) continue;
                String[] propertyColumnNames = this.getPropertyColumnNames(i);
                String[] propertyColumnWriters = this.getPropertyColumnWriters(i);
                boolean[] propertyNullness = types[i].toColumnNullness(oldFields[i], this.getFactory());
                for (int k = 0; k < propertyNullness.length; ++k) {
                    if (propertyNullness[k]) {
                        update.addWhereColumn(propertyColumnNames[k], "=" + propertyColumnWriters[k]);
                        continue;
                    }
                    update.addWhereColumn(propertyColumnNames[k], " is null");
                }
            }
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment("update " + this.getEntityName());
        }
        return hasColumns ? update.toStatementString() : null;
    }

    public final boolean checkVersion(boolean[] includeProperty) {
        return includeProperty[this.getVersionProperty()] || this.entityMetamodel.isVersionGenerated();
    }

    public String generateInsertString(boolean[] includeProperty, int j) {
        return this.generateInsertString(false, includeProperty, j);
    }

    public String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
        return this.generateInsertString(identityInsert, includeProperty, 0);
    }

    public String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {
        Insert insert = this.createInsert().setTableName(this.getTableName(j));
        for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
            if (!this.isPropertyOfTable(i, j) || this.lobProperties.contains(i)) continue;
            InDatabaseValueGenerationStrategy generationStrategy = this.entityMetamodel.getInDatabaseValueGenerationStrategies()[i];
            if (generationStrategy != null && generationStrategy.getGenerationTiming().includesInsert()) {
                String[] values;
                if (!generationStrategy.referenceColumnsInSql()) continue;
                if (generationStrategy.getReferencedColumnValues() == null) {
                    values = this.propertyColumnWriters[i];
                } else {
                    int numberOfColumns = this.propertyColumnWriters[i].length;
                    values = new String[numberOfColumns];
                    for (int x = 0; x < numberOfColumns; ++x) {
                        values[x] = generationStrategy.getReferencedColumnValues()[x] != null ? generationStrategy.getReferencedColumnValues()[x] : this.propertyColumnWriters[i][x];
                    }
                }
                insert.addColumns(this.getPropertyColumnNames(i), this.propertyColumnInsertable[i], values);
                continue;
            }
            if (!includeProperty[i]) continue;
            insert.addColumns(this.getPropertyColumnNames(i), this.propertyColumnInsertable[i], this.propertyColumnWriters[i]);
        }
        if (j == 0) {
            this.addDiscriminatorToInsert(insert);
        }
        if (j == 0 && identityInsert) {
            insert.addIdentityColumn(this.getKeyColumns(0)[0]);
        } else {
            insert.addColumns(this.getKeyColumns(j));
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            insert.setComment("insert " + this.getEntityName());
        }
        for (int i : this.lobProperties) {
            if (!includeProperty[i] || !this.isPropertyOfTable(i, j)) continue;
            insert.addColumns(this.getPropertyColumnNames(i), this.propertyColumnInsertable[i], this.propertyColumnWriters[i]);
        }
        String result = insert.toStatementString();
        if (j == 0 && identityInsert && this.useInsertSelectIdentity()) {
            result = this.getFactory().getDialect().getIdentityColumnSupport().appendIdentitySelectToInsert(this.getKeyColumns(0)[0], result);
        }
        return result;
    }

    public String generateIdentityInsertString(SqlStringGenerationContext context, boolean[] includeProperty) {
        IdentifierGeneratingInsert insert = this.identityDelegate.prepareIdentifierGeneratingInsert(context);
        insert.setTableName(this.getTableName(0));
        for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
            String[] values;
            if (!this.isPropertyOfTable(i, 0) || this.lobProperties.contains(i)) continue;
            InDatabaseValueGenerationStrategy generationStrategy = this.entityMetamodel.getInDatabaseValueGenerationStrategies()[i];
            if (includeProperty[i]) {
                insert.addColumns(this.getPropertyColumnNames(i), this.propertyColumnInsertable[i], this.propertyColumnWriters[i]);
                continue;
            }
            if (generationStrategy == null || !generationStrategy.getGenerationTiming().includesInsert() || !generationStrategy.referenceColumnsInSql()) continue;
            if (generationStrategy.getReferencedColumnValues() == null) {
                values = this.propertyColumnWriters[i];
            } else {
                values = new String[this.propertyColumnWriters[i].length];
                for (int j = 0; j < values.length; ++j) {
                    values[j] = generationStrategy.getReferencedColumnValues()[j] != null ? generationStrategy.getReferencedColumnValues()[j] : this.propertyColumnWriters[i][j];
                }
            }
            insert.addColumns(this.getPropertyColumnNames(i), this.propertyColumnInsertable[i], values);
        }
        for (int i : this.lobProperties) {
            if (!includeProperty[i] || !this.isPropertyOfTable(i, 0)) continue;
            insert.addColumns(this.getPropertyColumnNames(i), this.propertyColumnInsertable[i], this.propertyColumnWriters[i]);
        }
        this.addDiscriminatorToInsert(insert);
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            insert.setComment("insert " + this.getEntityName());
        }
        return insert.toStatementString();
    }

    public String generateDeleteString(int j) {
        Delete delete = this.createDelete().setTableName(this.getTableName(j)).addPrimaryKeyColumns(this.getKeyColumns(j));
        if (j == 0) {
            delete.setVersionColumnName(this.getVersionColumnName());
        }
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            delete.setComment("delete " + this.getEntityName());
        }
        return delete.toStatementString();
    }

    public int dehydrate(Serializable id, Object[] fields, boolean[] includeProperty, boolean[][] includeColumns, int j, PreparedStatement st, SharedSessionContractImplementor session, boolean isUpdate) throws HibernateException, SQLException {
        return this.dehydrate(id, fields, null, includeProperty, includeColumns, j, st, session, 1, isUpdate);
    }

    public int dehydrate(Serializable id, Object[] fields, Object rowId, boolean[] includeProperty, boolean[][] includeColumns, int j, PreparedStatement ps, SharedSessionContractImplementor session, int index, boolean isUpdate) throws SQLException, HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Dehydrating entity: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
        }
        for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
            if (!includeProperty[i] || !this.isPropertyOfTable(i, j) || this.lobProperties.contains(i)) continue;
            this.getPropertyTypes()[i].nullSafeSet(ps, fields[i], index, includeColumns[i], session);
            index += ArrayHelper.countTrue(includeColumns[i]);
        }
        if (!isUpdate) {
            index += this.dehydrateId(id, rowId, ps, session, index);
        }
        for (int i : this.lobProperties) {
            if (!includeProperty[i] || !this.isPropertyOfTable(i, j)) continue;
            this.getPropertyTypes()[i].nullSafeSet(ps, fields[i], index, includeColumns[i], session);
            index += ArrayHelper.countTrue(includeColumns[i]);
        }
        if (isUpdate) {
            index += this.dehydrateId(id, rowId, ps, session, index);
        }
        return index;
    }

    private int dehydrateId(Serializable id, Object rowId, PreparedStatement ps, SharedSessionContractImplementor session, int index) throws SQLException {
        if (rowId != null) {
            if (LOG.isTraceEnabled()) {
                LOG.tracev(String.format("binding parameter [%s] as ROWID - [%s]", index, rowId), new Object[0]);
            }
            ps.setObject(index, rowId);
            return 1;
        }
        if (id != null) {
            this.getIdentifierType().nullSafeSet(ps, id, index, session);
            return this.getIdentifierColumnSpan();
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object[] hydrate(ResultSet rs, Serializable id, Object object, Loadable rootLoadable, String[][] suffixedPropertyColumns, boolean forceEager, boolean[] propertiesForceEager, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Hydrating entity: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
        }
        AbstractEntityPersister rootPersister = (AbstractEntityPersister)rootLoadable;
        boolean hasDeferred = rootPersister.hasSequentialSelect();
        PreparedStatement sequentialSelect = null;
        ResultSet sequentialResultSet = null;
        boolean sequentialSelectEmpty = false;
        try {
            String sql;
            if (hasDeferred && (sql = rootPersister.getSequentialSelect(this.getEntityName())) != null) {
                sequentialSelect = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql);
                rootPersister.getIdentifierType().nullSafeSet(sequentialSelect, id, 1, session);
                sequentialResultSet = session.getJdbcCoordinator().getResultSetReturn().extract(sequentialSelect);
                if (!sequentialResultSet.next()) {
                    sequentialSelectEmpty = true;
                }
            }
            String[] propNames = this.getPropertyNames();
            Type[] types = this.getPropertyTypes();
            Object[] values = new Object[types.length];
            boolean[] laziness = this.getPropertyLaziness();
            String[] propSubclassNames = this.getSubclassPropertySubclassNameClosure();
            for (int i = 0; i < types.length; ++i) {
                if (!this.propertySelectable[i]) {
                    values[i] = PropertyAccessStrategyBackRefImpl.UNKNOWN;
                    continue;
                }
                if (forceEager || !laziness[i] || propertiesForceEager != null && propertiesForceEager[i]) {
                    boolean propertyIsDeferred;
                    boolean bl = propertyIsDeferred = hasDeferred && rootPersister.isSubclassPropertyDeferred(propNames[i], propSubclassNames[i]);
                    if (propertyIsDeferred && sequentialSelectEmpty) {
                        values[i] = null;
                        continue;
                    }
                    ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
                    String[] cols = propertyIsDeferred ? this.propertyColumnAliases[i] : suffixedPropertyColumns[i];
                    values[i] = types[i].hydrate(propertyResultSet, cols, session, object);
                    continue;
                }
                values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
            }
            if (sequentialResultSet != null) {
                session.getJdbcCoordinator().getResourceRegistry().release(sequentialResultSet, sequentialSelect);
            }
            Object[] objectArray = values;
            if (sequentialSelect != null) {
                session.getJdbcCoordinator().getResourceRegistry().release(sequentialSelect);
                session.getJdbcCoordinator().afterStatementExecution();
            }
            return objectArray;
        }
        catch (Throwable throwable) {
            if (sequentialSelect != null) {
                session.getJdbcCoordinator().getResourceRegistry().release(sequentialSelect);
                session.getJdbcCoordinator().afterStatementExecution();
            }
            throw throwable;
        }
    }

    public boolean useInsertSelectIdentity() {
        return !this.useGetGeneratedKeys() && this.getFactory().getDialect().getIdentityColumnSupport().supportsInsertSelectIdentity();
    }

    public boolean useGetGeneratedKeys() {
        return this.getFactory().getSessionFactoryOptions().isGetGeneratedKeysEnabled();
    }

    protected String getSequentialSelect(String entityName) {
        throw new UnsupportedOperationException("no sequential selects");
    }

    public Serializable insert(final Object[] fields, final boolean[] notNull, String sql, final Object object, final SharedSessionContractImplementor session) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Inserting entity: {0} (native id)", this.getEntityName());
            if (this.isVersioned()) {
                LOG.tracev("Version: {0}", Versioning.getVersion(fields, this));
            }
        }
        Binder binder = new Binder(){

            @Override
            public void bindValues(PreparedStatement ps) throws SQLException {
                AbstractEntityPersister.this.dehydrate(null, fields, notNull, AbstractEntityPersister.this.propertyColumnInsertable, 0, ps, session, false);
            }

            @Override
            public Object getEntity() {
                return object;
            }
        };
        return this.identityDelegate.performInsert(sql, session, binder);
    }

    @Override
    public String getIdentitySelectString() {
        return this.getFactory().getDialect().getIdentityColumnSupport().getIdentitySelectString(this.getTableName(0), this.getKeyColumns(0)[0], this.getIdentifierType().sqlTypes(this.getFactory())[0]);
    }

    @Override
    public String getSelectByUniqueKeyString(String propertyName) {
        return new SimpleSelect(this.getFactory().getDialect()).setTableName(this.getTableName(0)).addColumns(this.getKeyColumns(0)).addCondition(this.getPropertyColumnNames(propertyName), "=?").toStatementString();
    }

    public void insert(Serializable id, Object[] fields, boolean[] notNull, int j, String sql, Object object, SharedSessionContractImplementor session) throws HibernateException {
        boolean useBatch;
        if (this.isInverseTable(j)) {
            return;
        }
        if (this.isNullableTable(j) && this.isAllNull(fields, j)) {
            return;
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Inserting entity: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
            if (j == 0 && this.isVersioned()) {
                LOG.tracev("Version: {0}", Versioning.getVersion(fields, this));
            }
        }
        Expectation expectation = Expectations.appropriateExpectation(this.insertResultCheckStyles[j]);
        int jdbcBatchSizeToUse = session.getConfiguredJdbcBatchSize();
        boolean bl = useBatch = expectation.canBeBatched() && jdbcBatchSizeToUse > 1 && this.getIdentifierGenerator().supportsJdbcBatchInserts();
        if (useBatch && this.insertBatchKey == null) {
            this.insertBatchKey = new BasicBatchKey(this.getEntityName() + "#INSERT", expectation);
        }
        boolean callable = this.isInsertCallable(j);
        try {
            PreparedStatement insert = useBatch ? session.getJdbcCoordinator().getBatch(this.insertBatchKey).getBatchStatement(sql, callable) : session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
            try {
                int index = 1;
                this.dehydrate(id, fields, null, notNull, this.propertyColumnInsertable, j, insert, session, index += expectation.prepare(insert), false);
                if (useBatch) {
                    session.getJdbcCoordinator().getBatch(this.insertBatchKey).addToBatch();
                } else {
                    expectation.verifyOutcome(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(insert), insert, -1, sql);
                }
            }
            catch (RuntimeException | SQLException e) {
                if (useBatch) {
                    session.getJdbcCoordinator().abortBatch();
                }
                throw e;
            }
            finally {
                if (!useBatch) {
                    session.getJdbcCoordinator().getResourceRegistry().release(insert);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
            }
        }
        catch (SQLException e) {
            throw this.getFactory().getSQLExceptionHelper().convert(e, "could not insert: " + MessageHelper.infoString(this), sql);
        }
    }

    public void updateOrInsert(Serializable id, Object[] fields, Object[] oldFields, Object rowId, boolean[] includeProperty, int j, Object oldVersion, Object object, String sql, SharedSessionContractImplementor session) throws HibernateException {
        if (!this.isInverseTable(j)) {
            boolean isRowToUpdate;
            if (this.isNullableTable(j) && oldFields != null && this.isAllNull(oldFields, j)) {
                isRowToUpdate = false;
            } else if (this.isNullableTable(j) && this.isAllNull(fields, j)) {
                isRowToUpdate = true;
                this.delete(id, oldVersion, j, object, this.getSQLDeleteStrings()[j], session, null);
            } else {
                isRowToUpdate = this.update(id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session);
            }
            if (!isRowToUpdate && !this.isAllNull(fields, j)) {
                this.insert(id, fields, this.getPropertyInsertability(), j, this.getSQLInsertStrings()[j], object, session);
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean update(Serializable id, Object[] fields, Object[] oldFields, Object rowId, boolean[] includeProperty, int j, Object oldVersion, Object object, String sql, SharedSessionContractImplementor session) throws HibernateException {
        boolean useVersion;
        boolean useBatch;
        Expectation expectation = Expectations.appropriateExpectation(this.updateResultCheckStyles[j]);
        int jdbcBatchSizeToUse = session.getConfiguredJdbcBatchSize();
        boolean bl = useBatch = expectation.canBeBatched() && this.isBatchable() && jdbcBatchSizeToUse > 1 && (oldFields != null || !this.isNullableTable(j));
        if (useBatch && this.updateBatchKey == null) {
            this.updateBatchKey = new BasicBatchKey(this.getEntityName() + "#UPDATE", expectation);
        }
        boolean callable = this.isUpdateCallable(j);
        boolean bl2 = useVersion = j == 0 && this.isVersioned();
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Updating entity: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
            if (useVersion) {
                LOG.tracev("Existing version: {0} -> New version:{1}", oldVersion, fields[this.getVersionProperty()]);
            }
        }
        try {
            int index = 1;
            PreparedStatement update = useBatch ? session.getJdbcCoordinator().getBatch(this.updateBatchKey).getBatchStatement(sql, callable) : session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
            try {
                index += expectation.prepare(update);
                index = this.dehydrate(id, fields, rowId, includeProperty, this.propertyColumnUpdateable, j, update, session, index, true);
                if (useVersion && this.entityMetamodel.getOptimisticLockStyle().isVersion()) {
                    if (this.checkVersion(includeProperty)) {
                        this.getVersionType().nullSafeSet(update, oldVersion, index, session);
                    }
                } else if (this.isAllOrDirtyOptLocking() && oldFields != null) {
                    boolean[] versionability = this.getPropertyVersionability();
                    boolean[] includeOldField = this.entityMetamodel.getOptimisticLockStyle().isAll() ? this.getPropertyUpdateability() : includeProperty;
                    Type[] types = this.getPropertyTypes();
                    for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
                        boolean include;
                        boolean bl3 = include = includeOldField[i] && this.isPropertyOfTable(i, j) && versionability[i];
                        if (!include) continue;
                        boolean[] settable = types[i].toColumnNullness(oldFields[i], this.getFactory());
                        types[i].nullSafeSet(update, oldFields[i], index, settable, session);
                        index += ArrayHelper.countTrue(settable);
                    }
                }
                if (useBatch) {
                    session.getJdbcCoordinator().getBatch(this.updateBatchKey).addToBatch();
                    boolean versionability = true;
                    return versionability;
                }
                boolean versionability = this.check(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(update), id, j, expectation, update, sql);
                return versionability;
            }
            catch (RuntimeException | SQLException e) {
                if (!useBatch) throw e;
                session.getJdbcCoordinator().abortBatch();
                throw e;
            }
            finally {
                if (!useBatch) {
                    session.getJdbcCoordinator().getResourceRegistry().release(update);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
            }
        }
        catch (SQLException e) {
            throw this.getFactory().getSQLExceptionHelper().convert(e, "could not update: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), sql);
        }
    }

    public void delete(Serializable id, Object version, int j, Object object, String sql, SharedSessionContractImplementor session, Object[] loadedState) throws HibernateException {
        boolean useBatch;
        if (this.isInverseTable(j)) {
            return;
        }
        boolean useVersion = j == 0 && this.isVersioned();
        boolean callable = this.isDeleteCallable(j);
        Expectation expectation = Expectations.appropriateExpectation(this.deleteResultCheckStyles[j]);
        boolean bl = useBatch = j == 0 && this.isBatchable() && expectation.canBeBatched();
        if (useBatch && this.deleteBatchKey == null) {
            this.deleteBatchKey = new BasicBatchKey(this.getEntityName() + "#DELETE", expectation);
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Deleting entity: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
            if (useVersion) {
                LOG.tracev("Version: {0}", version);
            }
        }
        if (this.isTableCascadeDeleteEnabled(j)) {
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Delete handled by foreign key constraint: {0}", this.getTableName(j));
            }
            return;
        }
        try {
            int index = 1;
            PreparedStatement delete = useBatch ? session.getJdbcCoordinator().getBatch(this.deleteBatchKey).getBatchStatement(sql, callable) : session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, callable);
            try {
                this.getIdentifierType().nullSafeSet(delete, id, index += expectation.prepare(delete), session);
                index += this.getIdentifierColumnSpan();
                if (useVersion) {
                    this.getVersionType().nullSafeSet(delete, version, index, session);
                } else if (this.isAllOrDirtyOptLocking() && loadedState != null) {
                    boolean[] versionability = this.getPropertyVersionability();
                    Type[] types = this.getPropertyTypes();
                    for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
                        if (!this.isPropertyOfTable(i, j) || !versionability[i]) continue;
                        boolean[] settable = types[i].toColumnNullness(loadedState[i], this.getFactory());
                        types[i].nullSafeSet(delete, loadedState[i], index, settable, session);
                        index += ArrayHelper.countTrue(settable);
                    }
                }
                if (useBatch) {
                    session.getJdbcCoordinator().getBatch(this.deleteBatchKey).addToBatch();
                } else {
                    this.check(session.getJdbcCoordinator().getResultSetReturn().executeUpdate(delete), id, j, expectation, delete, sql);
                }
            }
            catch (RuntimeException | SQLException e) {
                if (useBatch) {
                    session.getJdbcCoordinator().abortBatch();
                }
                throw e;
            }
            finally {
                if (!useBatch) {
                    session.getJdbcCoordinator().getResourceRegistry().release(delete);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
            }
        }
        catch (SQLException sqle) {
            throw this.getFactory().getSQLExceptionHelper().convert(sqle, "could not delete: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), sql);
        }
    }

    protected String[] getUpdateStrings(boolean byRowId, boolean lazy) {
        if (byRowId) {
            return lazy ? this.getSQLLazyUpdateByRowIdStrings() : this.getSQLUpdateByRowIdStrings();
        }
        return lazy ? this.getSQLLazyUpdateStrings() : this.getSQLUpdateStrings();
    }

    @Override
    public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SharedSessionContractImplementor session) throws HibernateException {
        int j;
        String[] updateStrings;
        boolean[] propsToUpdate;
        InMemoryValueGenerationStrategy[] valueGenerationStrategies;
        int valueGenerationStrategiesSize;
        if (this.getEntityMetamodel().hasPreUpdateGeneratedValues() && (valueGenerationStrategiesSize = (valueGenerationStrategies = this.getEntityMetamodel().getInMemoryValueGenerationStrategies()).length) != 0) {
            int[] fieldsPreUpdateNeeded = new int[valueGenerationStrategiesSize];
            int count = 0;
            for (int i = 0; i < valueGenerationStrategiesSize; ++i) {
                if (valueGenerationStrategies[i] == null || !valueGenerationStrategies[i].getGenerationTiming().includesUpdate()) continue;
                fields[i] = valueGenerationStrategies[i].getValueGenerator().generateValue((Session)((Object)session), object);
                this.setPropertyValue(object, i, fields[i]);
                fieldsPreUpdateNeeded[count++] = i;
            }
            if (dirtyFields != null) {
                dirtyFields = ArrayHelper.join(dirtyFields, ArrayHelper.trim(fieldsPreUpdateNeeded, count));
            }
        }
        boolean[] tableUpdateNeeded = this.getTableUpdateNeeded(dirtyFields, hasDirtyCollection);
        int span = this.getTableSpan();
        EntityEntry entry = session.getPersistenceContextInternal().getEntry(object);
        if (entry == null && !this.isMutable()) {
            throw new IllegalStateException("Updating immutable entity that is not in session yet!");
        }
        if (dirtyFields != null && this.entityMetamodel.isDynamicUpdate()) {
            propsToUpdate = this.getPropertiesToUpdate(dirtyFields, hasDirtyCollection);
            updateStrings = new String[span];
            for (j = 0; j < span; ++j) {
                updateStrings[j] = tableUpdateNeeded[j] ? this.generateUpdateString(propsToUpdate, j, oldFields, j == 0 && rowId != null) : null;
            }
        } else if (dirtyFields != null && this.hasUninitializedLazyProperties(object) && this.hasLazyDirtyFields(dirtyFields)) {
            propsToUpdate = this.getPropertiesToUpdate(dirtyFields, hasDirtyCollection);
            boolean[] propertyLaziness = this.getPropertyLaziness();
            for (int i = 0; i < propertyLaziness.length; ++i) {
                if (propertyLaziness[i]) continue;
                propsToUpdate[i] = true;
            }
            updateStrings = new String[span];
            for (int j2 = 0; j2 < span; ++j2) {
                updateStrings[j2] = tableUpdateNeeded[j2] ? this.generateUpdateString(propsToUpdate, j2, oldFields, j2 == 0 && rowId != null) : null;
            }
        } else if (!this.isModifiableEntity(entry)) {
            propsToUpdate = this.getPropertiesToUpdate(dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields, hasDirtyCollection);
            updateStrings = new String[span];
            for (int j3 = 0; j3 < span; ++j3) {
                updateStrings[j3] = tableUpdateNeeded[j3] ? this.generateUpdateString(propsToUpdate, j3, oldFields, j3 == 0 && rowId != null) : null;
            }
        } else {
            updateStrings = this.getUpdateStrings(rowId != null, this.hasUninitializedLazyProperties(object));
            propsToUpdate = this.getPropertyUpdateability(object);
        }
        for (j = 0; j < span; ++j) {
            if (!tableUpdateNeeded[j]) continue;
            this.updateOrInsert(id, fields, oldFields, j == 0 ? rowId : null, propsToUpdate, j, oldVersion, object, updateStrings[j], session);
        }
    }

    private boolean hasLazyDirtyFields(int[] dirtyFields) {
        boolean[] propertyLaziness = this.getPropertyLaziness();
        for (int i = 0; i < dirtyFields.length; ++i) {
            if (!propertyLaziness[dirtyFields[i]]) continue;
            return true;
        }
        return false;
    }

    @Override
    public Serializable insert(Object[] fields, Object object, SharedSessionContractImplementor session) throws HibernateException {
        Serializable id;
        this.preInsertInMemoryValueGeneration(fields, object, session);
        int span = this.getTableSpan();
        if (this.entityMetamodel.isDynamicInsert()) {
            boolean[] notNull = this.getPropertiesToInsert(fields);
            id = this.insert(fields, notNull, this.generateInsertString(true, notNull), object, session);
            for (int j = 1; j < span; ++j) {
                this.insert(id, fields, notNull, j, this.generateInsertString(notNull, j), object, session);
            }
        } else {
            id = this.insert(fields, this.getPropertyInsertability(), this.getSQLIdentityInsertString(), object, session);
            for (int j = 1; j < span; ++j) {
                this.insert(id, fields, this.getPropertyInsertability(), j, this.getSQLInsertStrings()[j], object, session);
            }
        }
        return id;
    }

    @Override
    public void insert(Serializable id, Object[] fields, Object object, SharedSessionContractImplementor session) {
        this.preInsertInMemoryValueGeneration(fields, object, session);
        int span = this.getTableSpan();
        if (this.entityMetamodel.isDynamicInsert()) {
            boolean[] notNull = this.getPropertiesToInsert(fields);
            for (int j = 0; j < span; ++j) {
                this.insert(id, fields, notNull, j, this.generateInsertString(notNull, j), object, session);
            }
        } else {
            for (int j = 0; j < span; ++j) {
                this.insert(id, fields, this.getPropertyInsertability(), j, this.getSQLInsertStrings()[j], object, session);
            }
        }
    }

    protected void preInsertInMemoryValueGeneration(Object[] fields, Object object, SharedSessionContractImplementor session) {
        if (this.getEntityMetamodel().hasPreInsertGeneratedValues()) {
            InMemoryValueGenerationStrategy[] strategies = this.getEntityMetamodel().getInMemoryValueGenerationStrategies();
            for (int i = 0; i < strategies.length; ++i) {
                if (strategies[i] == null || !strategies[i].getGenerationTiming().includesInsert()) continue;
                fields[i] = strategies[i].getValueGenerator().generateValue((Session)((Object)session), object);
                this.setPropertyValue(object, i, fields[i]);
            }
        }
    }

    @Override
    public void delete(Serializable id, Object version, Object object, SharedSessionContractImplementor session) throws HibernateException {
        int span = this.getTableSpan();
        boolean isImpliedOptimisticLocking = !this.entityMetamodel.isVersioned() && this.isAllOrDirtyOptLocking();
        Object[] loadedState = null;
        if (isImpliedOptimisticLocking) {
            EntityKey key = session.generateEntityKey(id, this);
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            Object entity = persistenceContext.getEntity(key);
            if (entity != null) {
                EntityEntry entry = persistenceContext.getEntry(entity);
                loadedState = entry.getLoadedState();
            }
        }
        String[] deleteStrings = isImpliedOptimisticLocking && loadedState != null ? this.generateSQLDeleteStrings(loadedState) : this.getSQLDeleteStrings();
        for (int j = span - 1; j >= 0; --j) {
            this.delete(id, version, j, object, deleteStrings[j], session, loadedState);
        }
    }

    protected boolean isAllOrDirtyOptLocking() {
        return this.entityMetamodel.getOptimisticLockStyle().isAllOrDirty();
    }

    protected String[] generateSQLDeleteStrings(Object[] loadedState) {
        int span = this.getTableSpan();
        String[] deleteStrings = new String[span];
        for (int j = span - 1; j >= 0; --j) {
            Delete delete = this.createDelete().setTableName(this.getTableName(j)).addPrimaryKeyColumns(this.getKeyColumns(j));
            if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
                delete.setComment("delete " + this.getEntityName() + " [" + j + "]");
            }
            boolean[] versionability = this.getPropertyVersionability();
            Type[] types = this.getPropertyTypes();
            for (int i = 0; i < this.entityMetamodel.getPropertySpan(); ++i) {
                if (!this.isPropertyOfTable(i, j) || !versionability[i]) continue;
                String[] propertyColumnNames = this.getPropertyColumnNames(i);
                boolean[] propertyNullness = types[i].toColumnNullness(loadedState[i], this.getFactory());
                for (int k = 0; k < propertyNullness.length; ++k) {
                    if (propertyNullness[k]) {
                        delete.addWhereFragment(propertyColumnNames[k] + " = ?");
                        continue;
                    }
                    delete.addWhereFragment(propertyColumnNames[k] + " is null");
                }
            }
            deleteStrings[j] = delete.toStatementString();
        }
        return deleteStrings;
    }

    protected void logStaticSQL() {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Static SQL for entity: %s", this.getEntityName());
            for (Map.Entry<String, String> entry : this.sqlLazySelectStringsByFetchGroup.entrySet()) {
                LOG.debugf(" Lazy select (%s) : %s", entry.getKey(), entry.getValue());
            }
            if (this.sqlVersionSelectString != null) {
                LOG.debugf(" Version select: %s", this.sqlVersionSelectString);
            }
            if (this.sqlSnapshotSelectString != null) {
                LOG.debugf(" Snapshot select: %s", this.sqlSnapshotSelectString);
            }
            for (int j = 0; j < this.getTableSpan(); ++j) {
                LOG.debugf(" Insert %s: %s", j, this.getSQLInsertStrings()[j]);
                LOG.debugf(" Update %s: %s", j, this.getSQLUpdateStrings()[j]);
                LOG.debugf(" Delete %s: %s", j, this.getSQLDeleteStrings()[j]);
            }
            if (this.sqlIdentityInsertString != null) {
                LOG.debugf(" Identity insert: %s", this.sqlIdentityInsertString);
            }
            if (this.sqlUpdateByRowIdString != null) {
                LOG.debugf(" Update by row id (all fields): %s", this.sqlUpdateByRowIdString);
            }
            if (this.sqlLazyUpdateByRowIdString != null) {
                LOG.debugf(" Update by row id (non-lazy fields): %s", this.sqlLazyUpdateByRowIdString);
            }
            if (this.sqlInsertGeneratedValuesSelectString != null) {
                LOG.debugf(" Insert-generated property select: %s", this.sqlInsertGeneratedValuesSelectString);
            }
            if (this.sqlUpdateGeneratedValuesSelectString != null) {
                LOG.debugf(" Update-generated property select: %s", this.sqlUpdateGeneratedValuesSelectString);
            }
        }
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
    public String generateFilterConditionAlias(String rootAlias) {
        return rootAlias;
    }

    @Override
    public String oneToManyFilterFragment(String alias) throws MappingException {
        return "";
    }

    @Override
    public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
        return this.oneToManyFilterFragment(alias);
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        return this.getSubclassTableSpan() == 1 ? "" : this.createJoin(alias, innerJoin, includeSubclasses, Collections.emptySet(), null).toFromFragmentString();
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return this.getSubclassTableSpan() == 1 ? "" : this.createJoin(alias, innerJoin, includeSubclasses, treatAsDeclarations, null).toFromFragmentString();
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations, Set<String> referencedTables) {
        return this.getSubclassTableSpan() == 1 ? "" : this.createJoin(alias, innerJoin, includeSubclasses, treatAsDeclarations, referencedTables).toFromFragmentString();
    }

    @Override
    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        return this.getSubclassTableSpan() == 1 ? "" : this.createJoin(alias, innerJoin, includeSubclasses, Collections.emptySet(), null).toWhereFragmentString();
    }

    @Override
    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return this.getSubclassTableSpan() == 1 ? "" : this.createJoin(alias, innerJoin, includeSubclasses, treatAsDeclarations, null).toWhereFragmentString();
    }

    protected boolean isSubclassTableLazy(int j) {
        return false;
    }

    protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return this.createJoin(name, innerJoin, includeSubclasses, treatAsDeclarations, null);
    }

    protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations, Set<String> referencedTables) {
        String[] idCols = StringHelper.qualify(name, this.getIdentifierColumnNames());
        JoinFragment join = this.getFactory().getDialect().createOuterJoinFragment();
        int tableSpan = this.getSubclassTableSpan();
        for (int j = 1; j < tableSpan; ++j) {
            JoinType joinType = this.determineSubclassTableJoinType(j, innerJoin, includeSubclasses, treatAsDeclarations, referencedTables);
            if (joinType == null || joinType == JoinType.NONE) continue;
            join.addJoin(this.getSubclassTableName(j), AbstractEntityPersister.generateTableAlias(name, j), idCols, this.getSubclassTableKeyColumns(j), joinType);
        }
        return join;
    }

    protected JoinType determineSubclassTableJoinType(int subclassTableNumber, boolean canInnerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
        return this.determineSubclassTableJoinType(subclassTableNumber, canInnerJoin, includeSubclasses, treatAsDeclarations, null);
    }

    protected JoinType determineSubclassTableJoinType(int subclassTableNumber, boolean canInnerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations, Set<String> referencedTables) {
        if (this.isClassOrSuperclassJoin(subclassTableNumber)) {
            String superclassTableName = this.getSubclassTableName(subclassTableNumber);
            if (referencedTables != null && this.canOmitSuperclassTableJoin() && !referencedTables.contains(superclassTableName)) {
                return JoinType.NONE;
            }
            boolean shouldInnerJoin = canInnerJoin && !this.isInverseTable(subclassTableNumber) && !this.isNullableTable(subclassTableNumber);
            return shouldInnerJoin ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN;
        }
        if (this.isSubclassTableIndicatedByTreatAsDeclarations(subclassTableNumber, treatAsDeclarations)) {
            return JoinType.INNER_JOIN;
        }
        if (includeSubclasses && !this.isSubclassTableSequentialSelect(subclassTableNumber) && !this.isSubclassTableLazy(subclassTableNumber)) {
            return JoinType.LEFT_OUTER_JOIN;
        }
        return JoinType.NONE;
    }

    protected boolean isSubclassTableIndicatedByTreatAsDeclarations(int subclassTableNumber, Set<String> treatAsDeclarations) {
        return false;
    }

    protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
        String[] keyCols = StringHelper.qualify(drivingAlias, this.getSubclassTableKeyColumns(tableNumbers[0]));
        JoinFragment jf = this.getFactory().getDialect().createOuterJoinFragment();
        for (int i = 1; i < tableNumbers.length; ++i) {
            int j = tableNumbers[i];
            jf.addJoin(this.getSubclassTableName(j), AbstractEntityPersister.generateTableAlias(this.getRootAlias(), j), keyCols, this.getSubclassTableKeyColumns(j), this.isInverseSubclassTable(j) || this.isNullableSubclassTable(j) ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN);
        }
        return jf;
    }

    protected SelectFragment createSelect(int[] subclassColumnNumbers, int[] subclassFormulaNumbers) {
        SelectFragment selectFragment = new SelectFragment();
        int[] columnTableNumbers = this.getSubclassColumnTableNumberClosure();
        String[] columnAliases = this.getSubclassColumnAliasClosure();
        String[] columnReaderTemplates = this.getSubclassColumnReaderTemplateClosure();
        for (int i = 0; i < subclassColumnNumbers.length; ++i) {
            int columnNumber = subclassColumnNumbers[i];
            if (!this.subclassColumnSelectableClosure[columnNumber]) continue;
            String subalias = AbstractEntityPersister.generateTableAlias(this.getRootAlias(), columnTableNumbers[columnNumber]);
            selectFragment.addColumnTemplate(subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber]);
        }
        int[] formulaTableNumbers = this.getSubclassFormulaTableNumberClosure();
        String[] formulaTemplates = this.getSubclassFormulaTemplateClosure();
        String[] formulaAliases = this.getSubclassFormulaAliasClosure();
        for (int i = 0; i < subclassFormulaNumbers.length; ++i) {
            int formulaNumber = subclassFormulaNumbers[i];
            String subalias = AbstractEntityPersister.generateTableAlias(this.getRootAlias(), formulaTableNumbers[formulaNumber]);
            selectFragment.addFormula(subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber]);
        }
        return selectFragment;
    }

    protected String createFrom(int tableNumber, String alias) {
        return this.getSubclassTableName(tableNumber) + ' ' + alias;
    }

    protected String createWhereByKey(int tableNumber, String alias) {
        return String.join((CharSequence)"=? and ", StringHelper.qualify(alias, this.getSubclassTableKeyColumns(tableNumber))) + "=?";
    }

    protected String renderSelect(int[] tableNumbers, int[] columnNumbers, int[] formulaNumbers) {
        Arrays.sort(tableNumbers);
        int drivingTable = tableNumbers[0];
        String drivingAlias = AbstractEntityPersister.generateTableAlias(this.getRootAlias(), drivingTable);
        String where = this.createWhereByKey(drivingTable, drivingAlias);
        String from = this.createFrom(drivingTable, drivingAlias);
        JoinFragment jf = this.createJoin(tableNumbers, drivingAlias);
        SelectFragment selectFragment = this.createSelect(columnNumbers, formulaNumbers);
        Select select = new Select(this.getFactory().getDialect());
        select.setSelectClause(selectFragment.toFragmentString().substring(2));
        select.setFromClause(from);
        select.setWhereClause(where);
        select.setOuterJoins(jf.toFromFragmentString(), jf.toWhereFragmentString());
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("sequential select " + this.getEntityName());
        }
        return select.toStatementString();
    }

    private String getRootAlias() {
        return StringHelper.generateAlias(this.getEntityName());
    }

    protected void postConstruct(Mapping mapping) throws MappingException {
        this.initPropertyPaths(mapping);
        this.prepareEntityIdentifierDefinition();
    }

    private void doLateInit() {
        int j;
        int joinSpan = this.getTableSpan();
        this.sqlDeleteStrings = new String[joinSpan];
        this.sqlInsertStrings = new String[joinSpan];
        this.sqlUpdateStrings = new String[joinSpan];
        this.sqlLazyUpdateStrings = new String[joinSpan];
        this.sqlUpdateByRowIdString = this.rowIdName == null ? null : this.generateUpdateString(this.getPropertyUpdateability(), 0, true);
        this.sqlLazyUpdateByRowIdString = this.rowIdName == null ? null : this.generateUpdateString(this.getNonLazyPropertyUpdateability(), 0, true);
        for (j = 0; j < joinSpan; ++j) {
            this.sqlInsertStrings[j] = this.customSQLInsert[j] == null ? this.generateInsertString(this.getPropertyInsertability(), j) : this.substituteBrackets(this.customSQLInsert[j]);
            this.sqlUpdateStrings[j] = this.customSQLUpdate[j] == null ? this.generateUpdateString(this.getPropertyUpdateability(), j, false) : this.substituteBrackets(this.customSQLUpdate[j]);
            this.sqlLazyUpdateStrings[j] = this.customSQLUpdate[j] == null ? this.generateUpdateString(this.getNonLazyPropertyUpdateability(), j, false) : this.substituteBrackets(this.customSQLUpdate[j]);
            this.sqlDeleteStrings[j] = this.customSQLDelete[j] == null ? this.generateDeleteString(j) : this.substituteBrackets(this.customSQLDelete[j]);
        }
        this.tableHasColumns = new boolean[joinSpan];
        for (j = 0; j < joinSpan; ++j) {
            this.tableHasColumns[j] = this.sqlUpdateStrings[j] != null;
        }
        this.sqlSnapshotSelectString = this.generateSnapshotSelectString();
        this.sqlLazySelectStringsByFetchGroup = this.generateLazySelectStringsByFetchGroup();
        this.sqlVersionSelectString = this.generateSelectVersionString();
        if (this.hasInsertGeneratedProperties()) {
            this.sqlInsertGeneratedValuesSelectString = this.generateInsertGeneratedValuesSelectString();
        }
        if (this.hasUpdateGeneratedProperties()) {
            this.sqlUpdateGeneratedValuesSelectString = this.generateUpdateGeneratedValuesSelectString();
        }
        if (this.isIdentifierAssignedByInsert()) {
            this.identityDelegate = ((PostInsertIdentifierGenerator)this.getIdentifierGenerator()).getInsertGeneratedIdentifierDelegate(this, this.getFactory().getDialect(), this.useGetGeneratedKeys());
            this.sqlIdentityInsertString = this.customSQLInsert[0] == null ? this.generateIdentityInsertString(this.factory.getSqlStringGenerationContext(), this.getPropertyInsertability()) : this.substituteBrackets(this.customSQLInsert[0]);
        } else {
            this.sqlIdentityInsertString = null;
        }
        this.logStaticSQL();
    }

    private String substituteBrackets(String sql) {
        return new SubstituteBracketSQLQueryParser(sql, this.getFactory()).process();
    }

    @Override
    public final void postInstantiate() throws MappingException {
        this.doLateInit();
        this.createLoaders();
        this.createUniqueKeyLoaders();
        this.createNaturalIdLoaders();
        this.createQueryLoader();
        this.doPostInstantiate();
    }

    protected void doPostInstantiate() {
    }

    protected void createLoaders() {
        if (!this.factory.getSessionFactoryOptions().isDelayBatchFetchLoaderCreationsEnabled()) {
            for (LockMode lockMode : LockMode.values()) {
                this.getLoaderByLockMode(lockMode);
            }
            this.loaders.getOrCreateByInternalFetchProfileMerge(this::buildMergeCascadeEntityLoader);
            this.loaders.getOrCreateByInternalFetchProfileRefresh(this::buildRefreshCascadeEntityLoader);
        } else {
            this.getLoaderByLockMode(LockMode.NONE);
        }
    }

    protected UniqueEntityLoader buildMergeCascadeEntityLoader(LockMode ignored) {
        return new CascadeEntityLoader(this, CascadingActions.MERGE, this.getFactory());
    }

    protected UniqueEntityLoader buildRefreshCascadeEntityLoader(LockMode ignored) {
        return new CascadeEntityLoader(this, CascadingActions.REFRESH, this.getFactory());
    }

    protected final UniqueEntityLoader getLoaderByLockMode(LockMode lockMode) {
        return this.loaders.getOrBuildByLockMode(lockMode, this::generateDelayedEntityLoader);
    }

    private UniqueEntityLoader generateDelayedEntityLoader(LockMode lockMode) {
        switch (lockMode) {
            case NONE: 
            case READ: 
            case OPTIMISTIC: 
            case OPTIMISTIC_FORCE_INCREMENT: {
                return this.createEntityLoader(lockMode);
            }
            case UPGRADE: 
            case UPGRADE_NOWAIT: 
            case UPGRADE_SKIPLOCKED: 
            case FORCE: 
            case PESSIMISTIC_READ: 
            case PESSIMISTIC_WRITE: 
            case PESSIMISTIC_FORCE_INCREMENT: {
                boolean disableForUpdate = this.getSubclassTableSpan() > 1 && this.hasSubclasses() && !this.getFactory().getDialect().supportsOuterJoinForUpdate();
                return disableForUpdate ? this.getLoaderByLockMode(LockMode.READ) : this.createEntityLoader(lockMode);
            }
        }
        throw new IllegalStateException(String.format(Locale.ROOT, "Lock mode %1$s not supported by entity loaders.", new Object[]{lockMode}));
    }

    protected void createQueryLoader() {
        if (this.loaderName != null) {
            this.queryLoader = new NamedQueryLoader(this.loaderName, this);
        }
    }

    @Override
    public Object load(Serializable id, Object optionalObject, LockMode lockMode, SharedSessionContractImplementor session) {
        return this.load(id, optionalObject, new LockOptions().setLockMode(lockMode), session);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SharedSessionContractImplementor session) throws HibernateException {
        return this.doLoad(id, optionalObject, lockOptions, session, null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SharedSessionContractImplementor session, Boolean readOnly) throws HibernateException {
        return this.doLoad(id, optionalObject, lockOptions, session, readOnly);
    }

    private Object doLoad(Serializable id, Object optionalObject, LockOptions lockOptions, SharedSessionContractImplementor session, Boolean readOnly) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Fetching entity: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
        }
        UniqueEntityLoader loader = this.getAppropriateLoader(lockOptions, session);
        return loader.load(id, optionalObject, session, lockOptions, readOnly);
    }

    @Override
    public Object initializeEnhancedEntityUsedAsProxy(Object entity, String nameOfAttributeBeingAccessed, SharedSessionContractImplementor session) {
        BytecodeEnhancementMetadata enhancementMetadata = this.getEntityMetamodel().getBytecodeEnhancementMetadata();
        BytecodeLazyAttributeInterceptor currentInterceptor = enhancementMetadata.extractLazyInterceptor(entity);
        if (currentInterceptor instanceof EnhancementAsProxyLazinessInterceptor) {
            EnhancementAsProxyLazinessInterceptor proxyInterceptor = (EnhancementAsProxyLazinessInterceptor)currentInterceptor;
            EntityKey entityKey = proxyInterceptor.getEntityKey();
            Serializable identifier = entityKey.getIdentifier();
            LoadEvent loadEvent = new LoadEvent(identifier, entity, (EventSource)session, false);
            Object loaded = null;
            if (this.canReadFromCache) {
                loaded = CacheEntityLoaderHelper.INSTANCE.loadFromSecondLevelCache(loadEvent, this, entityKey);
            }
            if (loaded == null) {
                loaded = this.getLoaderByLockMode(LockMode.READ).load(identifier, entity, session, LockOptions.READ);
            }
            if (loaded == null) {
                PersistenceContext persistenceContext = session.getPersistenceContext();
                persistenceContext.removeEntry(entity);
                persistenceContext.removeEntity(entityKey);
                session.getFactory().getEntityNotFoundDelegate().handleEntityNotFound(entityKey.getEntityName(), identifier);
            }
            LazyAttributeLoadingInterceptor interceptor = enhancementMetadata.injectInterceptor(entity, identifier, session);
            if (nameOfAttributeBeingAccessed == null) {
                return null;
            }
            Object value = interceptor.isAttributeLoaded(nameOfAttributeBeingAccessed) ? this.getEntityTuplizer().getPropertyValue(entity, nameOfAttributeBeingAccessed) : this.initializeLazyProperty(nameOfAttributeBeingAccessed, entity, session);
            return interceptor.readObject(entity, nameOfAttributeBeingAccessed, value);
        }
        throw new IllegalStateException();
    }

    @Override
    public List multiLoad(Serializable[] ids, SharedSessionContractImplementor session, MultiLoadOptions loadOptions) {
        return MultiEntityLoadingSupport.multiLoad(this, ids, session, loadOptions);
    }

    @Override
    public void registerAffectingFetchProfile(String fetchProfileName) {
        if (this.affectingFetchProfileNames == null) {
            this.affectingFetchProfileNames = new HashSet<String>();
        }
        this.affectingFetchProfileNames.add(fetchProfileName);
    }

    private boolean isAffectedByEntityGraph(SharedSessionContractImplementor session) {
        return session.getLoadQueryInfluencers().getEffectiveEntityGraph().getGraph() != null;
    }

    private boolean isAffectedByEnabledFetchProfiles(SharedSessionContractImplementor session) {
        Set<String> fetchProfileNames = this.affectingFetchProfileNames;
        if (fetchProfileNames != null) {
            for (String s : session.getLoadQueryInfluencers().getEnabledFetchProfileNames()) {
                if (!fetchProfileNames.contains(s)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isAffectedByEnabledFilters(SharedSessionContractImplementor session) {
        return session.getLoadQueryInfluencers().hasEnabledFilters() && this.filterHelper.isAffectedBy(session.getLoadQueryInfluencers().getEnabledFilters());
    }

    protected UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SharedSessionContractImplementor session) {
        if (this.queryLoader != null) {
            return this.queryLoader;
        }
        LoadQueryInfluencers loadQueryInfluencers = session.getLoadQueryInfluencers();
        if (this.isAffectedByEnabledFilters(session)) {
            return this.createEntityLoader(lockOptions, loadQueryInfluencers);
        }
        if (loadQueryInfluencers.getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan(lockOptions.getLockMode())) {
            return this.getLoaderByString(loadQueryInfluencers.getInternalFetchProfile());
        }
        if (this.isAffectedByEnabledFetchProfiles(session) || this.isAffectedByEntityGraph(session)) {
            return this.createEntityLoader(lockOptions, loadQueryInfluencers);
        }
        if (lockOptions.getTimeOut() != -1) {
            return this.createEntityLoader(lockOptions, loadQueryInfluencers);
        }
        return this.getLoaderByLockMode(lockOptions.getLockMode());
    }

    private UniqueEntityLoader getLoaderByString(String internalFetchProfile) {
        if ("merge".equals(internalFetchProfile)) {
            return this.loaders.getOrCreateByInternalFetchProfileMerge(this::buildMergeCascadeEntityLoader);
        }
        if ("refresh".equals(internalFetchProfile)) {
            return this.loaders.getOrCreateByInternalFetchProfileRefresh(this::buildRefreshCascadeEntityLoader);
        }
        return null;
    }

    public final boolean isAllNull(Object[] array, int tableNumber) {
        for (int i = 0; i < array.length; ++i) {
            if (!this.isPropertyOfTable(i, tableNumber) || array[i] == null) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isSubclassPropertyNullable(int i) {
        return this.subclassPropertyNullabilityClosure[i];
    }

    public final boolean[] getPropertiesToUpdate(int[] dirtyProperties, boolean hasDirtyCollection) {
        boolean[] propsToUpdate = new boolean[this.entityMetamodel.getPropertySpan()];
        boolean[] updateability = this.getPropertyUpdateability();
        for (int j = 0; j < dirtyProperties.length; ++j) {
            int property = dirtyProperties[j];
            if (!updateability[property]) continue;
            propsToUpdate[property] = true;
        }
        if (this.isVersioned() && updateability[this.getVersionProperty()]) {
            propsToUpdate[this.getVersionProperty()] = Versioning.isVersionIncrementRequired(dirtyProperties, hasDirtyCollection, this.getPropertyVersionability());
        }
        return propsToUpdate;
    }

    public boolean[] getPropertiesToInsert(Object[] fields) {
        boolean[] notNull = new boolean[fields.length];
        boolean[] insertable = this.getPropertyInsertability();
        for (int i = 0; i < fields.length; ++i) {
            notNull[i] = insertable[i] && fields[i] != null;
        }
        return notNull;
    }

    @Override
    public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SharedSessionContractImplementor session) throws HibernateException {
        int[] props = TypeHelper.findDirty(this.entityMetamodel.getProperties(), currentState, previousState, this.propertyColumnUpdateable, session);
        if (props == null) {
            return null;
        }
        this.logDirtyProperties(props);
        return props;
    }

    @Override
    public int[] findModified(Object[] old, Object[] current, Object entity, SharedSessionContractImplementor session) throws HibernateException {
        int[] props = TypeHelper.findModified(this.entityMetamodel.getProperties(), current, old, this.propertyColumnUpdateable, this.getPropertyUpdateability(), session);
        if (props == null) {
            return null;
        }
        this.logDirtyProperties(props);
        return props;
    }

    public boolean[] getPropertyUpdateability(Object entity) {
        return this.hasUninitializedLazyProperties(entity) ? this.getNonLazyPropertyUpdateability() : this.getPropertyUpdateability();
    }

    private void logDirtyProperties(int[] props) {
        if (LOG.isTraceEnabled()) {
            for (int i = 0; i < props.length; ++i) {
                String propertyName = this.entityMetamodel.getProperties()[props[i]].getName();
                LOG.trace(StringHelper.qualify(this.getEntityName(), propertyName) + " is dirty");
            }
        }
    }

    @Override
    public SessionFactoryImplementor getFactory() {
        return this.factory;
    }

    @Override
    public EntityMetamodel getEntityMetamodel() {
        return this.entityMetamodel;
    }

    @Override
    public boolean canReadFromCache() {
        return this.canReadFromCache;
    }

    @Override
    public boolean canWriteToCache() {
        return this.canWriteToCache;
    }

    @Override
    public boolean hasCache() {
        return this.canWriteToCache;
    }

    @Override
    public EntityDataAccess getCacheAccessStrategy() {
        return this.cacheAccessStrategy;
    }

    @Override
    public CacheEntryStructure getCacheEntryStructure() {
        return this.cacheEntryHelper.getCacheEntryStructure();
    }

    @Override
    public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
        return this.cacheEntryHelper.buildCacheEntry(entity, state, version, session);
    }

    @Override
    public boolean hasNaturalIdCache() {
        return this.naturalIdRegionAccessStrategy != null;
    }

    @Override
    public NaturalIdDataAccess getNaturalIdCacheAccessStrategy() {
        return this.naturalIdRegionAccessStrategy;
    }

    public Comparator getVersionComparator() {
        return this.isVersioned() ? this.getVersionType().getComparator() : null;
    }

    @Override
    public final String getEntityName() {
        return this.entityMetamodel.getName();
    }

    @Override
    public EntityType getEntityType() {
        return this.entityMetamodel.getEntityType();
    }

    public boolean isPolymorphic() {
        return this.entityMetamodel.isPolymorphic();
    }

    @Override
    public boolean isInherited() {
        return this.entityMetamodel.isInherited();
    }

    @Override
    public boolean hasCascades() {
        return this.entityMetamodel.hasCascades();
    }

    @Override
    public boolean hasIdentifierProperty() {
        return !this.entityMetamodel.getIdentifierProperty().isVirtual();
    }

    @Override
    public VersionType getVersionType() {
        return (VersionType)this.locateVersionType();
    }

    private Type locateVersionType() {
        return this.entityMetamodel.getVersionProperty() == null ? null : this.entityMetamodel.getVersionProperty().getType();
    }

    @Override
    public int getVersionProperty() {
        return this.entityMetamodel.getVersionPropertyIndex();
    }

    @Override
    public boolean isVersioned() {
        return this.entityMetamodel.isVersioned();
    }

    @Override
    public boolean isIdentifierAssignedByInsert() {
        return this.entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
    }

    @Override
    public boolean hasLazyProperties() {
        return this.entityMetamodel.hasLazyProperties();
    }

    @Override
    public void afterReassociate(Object entity, SharedSessionContractImplementor session) {
        if (this.getEntityMetamodel().getBytecodeEnhancementMetadata().isEnhancedForLazyLoading()) {
            BytecodeLazyAttributeInterceptor interceptor = this.getEntityMetamodel().getBytecodeEnhancementMetadata().extractLazyInterceptor(entity);
            if (interceptor == null) {
                this.getEntityMetamodel().getBytecodeEnhancementMetadata().injectInterceptor(entity, this.getIdentifier(entity, session), session);
            } else {
                interceptor.setSession(session);
            }
        }
        this.handleNaturalIdReattachment(entity, session);
    }

    private void handleNaturalIdReattachment(Object entity, SharedSessionContractImplementor session) {
        if (!this.hasNaturalIdentifier()) {
            return;
        }
        if (this.getEntityMetamodel().hasImmutableNaturalId()) {
            return;
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        PersistenceContext.NaturalIdHelper naturalIdHelper = persistenceContext.getNaturalIdHelper();
        Serializable id = this.getIdentifier(entity, session);
        Object[] entitySnapshot = persistenceContext.getDatabaseSnapshot(id, this);
        Object[] naturalIdSnapshot = entitySnapshot == StatefulPersistenceContext.NO_ROW ? null : naturalIdHelper.extractNaturalIdValues(entitySnapshot, (EntityPersister)this);
        naturalIdHelper.removeSharedNaturalIdCrossReference(this, id, naturalIdSnapshot);
        naturalIdHelper.manageLocalNaturalIdCrossReference(this, id, naturalIdHelper.extractNaturalIdValues(entity, (EntityPersister)this), naturalIdSnapshot, CachedNaturalIdValueSource.UPDATE);
    }

    @Override
    public Boolean isTransient(Object entity, SharedSessionContractImplementor session) throws HibernateException {
        EntityDataAccess cache;
        Object ck;
        Serializable ce;
        Boolean result;
        Serializable id = this.canExtractIdOutOfEntity() ? this.getIdentifier(entity, session) : null;
        if (id == null) {
            return Boolean.TRUE;
        }
        Object version = this.getVersion(entity);
        if (this.isVersioned() && (result = this.entityMetamodel.getVersionProperty().getUnsavedValue().isUnsaved(version)) != null) {
            return result;
        }
        result = this.entityMetamodel.getIdentifierProperty().getUnsavedValue().isUnsaved(id);
        if (result != null) {
            return result;
        }
        if (session.getCacheMode().isGetEnabled() && this.canReadFromCache() && (ce = CacheHelper.fromSharedCache(session, ck = (cache = this.getCacheAccessStrategy()).generateCacheKey(id, this, session.getFactory(), session.getTenantIdentifier()), this.getCacheAccessStrategy())) != null) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public boolean hasCollections() {
        return this.entityMetamodel.hasCollections();
    }

    @Override
    public boolean hasMutableProperties() {
        return this.entityMetamodel.hasMutableProperties();
    }

    @Override
    public boolean isMutable() {
        return this.entityMetamodel.isMutable();
    }

    public final boolean isModifiableEntity(EntityEntry entry) {
        return entry == null ? this.isMutable() : entry.isModifiableEntity();
    }

    @Override
    public boolean isAbstract() {
        return this.entityMetamodel.isAbstract();
    }

    @Override
    public boolean hasSubclasses() {
        return this.entityMetamodel.hasSubclasses();
    }

    @Override
    public boolean hasProxy() {
        return this.entityMetamodel.isLazy() && !this.entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
        return this.entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
    }

    @Deprecated
    public InsertGeneratedIdentifierDelegate getIdentityDelegate() {
        return this.identityDelegate;
    }

    @Override
    public String getRootEntityName() {
        return this.entityMetamodel.getRootName();
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return this;
    }

    @Override
    public String getMappedSuperclass() {
        return this.entityMetamodel.getSuperclass();
    }

    @Override
    public boolean isExplicitPolymorphism() {
        return this.entityMetamodel.isExplicitPolymorphism();
    }

    protected boolean useDynamicUpdate() {
        return this.entityMetamodel.isDynamicUpdate();
    }

    protected boolean useDynamicInsert() {
        return this.entityMetamodel.isDynamicInsert();
    }

    public boolean hasEmbeddedCompositeIdentifier() {
        return this.entityMetamodel.getIdentifierProperty().isEmbedded();
    }

    @Override
    public boolean canExtractIdOutOfEntity() {
        return this.hasIdentifierProperty() || this.hasEmbeddedCompositeIdentifier() || this.hasIdentifierMapper();
    }

    private boolean hasIdentifierMapper() {
        return this.entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
    }

    @Override
    public String[] getKeyColumnNames() {
        return this.getIdentifierColumnNames();
    }

    @Override
    public String getName() {
        return this.getEntityName();
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean consumesEntityAlias() {
        return true;
    }

    @Override
    public boolean consumesCollectionAlias() {
        return false;
    }

    @Override
    public Type getPropertyType(String propertyName) throws MappingException {
        return this.propertyMapping.toType(propertyName);
    }

    @Override
    public Type getType() {
        return this.entityMetamodel.getEntityType();
    }

    @Override
    public boolean isSelectBeforeUpdateRequired() {
        return this.entityMetamodel.isSelectBeforeUpdate();
    }

    protected final OptimisticLockStyle optimisticLockStyle() {
        return this.entityMetamodel.getOptimisticLockStyle();
    }

    @Override
    public Object createProxy(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        return this.entityMetamodel.getTuplizer().createProxy(id, session);
    }

    public String toString() {
        return StringHelper.unqualify(this.getClass().getName()) + '(' + this.entityMetamodel.getName() + ')';
    }

    @Override
    public final String selectFragment(Joinable rhs, String rhsAlias, String lhsAlias, String entitySuffix, String collectionSuffix, boolean includeCollectionColumns) {
        return this.selectFragment(lhsAlias, entitySuffix);
    }

    @Override
    public boolean isInstrumented() {
        return this.entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
    }

    @Override
    public boolean hasInsertGeneratedProperties() {
        return this.entityMetamodel.hasInsertGeneratedValues();
    }

    @Override
    public boolean hasUpdateGeneratedProperties() {
        return this.entityMetamodel.hasUpdateGeneratedValues();
    }

    @Override
    public boolean isVersionPropertyGenerated() {
        return this.isVersioned() && this.getEntityMetamodel().isVersionGenerated();
    }

    @Override
    public boolean isVersionPropertyInsertable() {
        return this.isVersioned() && this.getPropertyInsertability()[this.getVersionProperty()];
    }

    @Override
    public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
        this.getEntityTuplizer().afterInitialize(entity, session);
    }

    @Override
    public String[] getPropertyNames() {
        return this.entityMetamodel.getPropertyNames();
    }

    @Override
    public Type[] getPropertyTypes() {
        return this.entityMetamodel.getPropertyTypes();
    }

    @Override
    public boolean[] getPropertyLaziness() {
        return this.entityMetamodel.getPropertyLaziness();
    }

    @Override
    public boolean[] getPropertyUpdateability() {
        return this.entityMetamodel.getPropertyUpdateability();
    }

    @Override
    public boolean[] getPropertyCheckability() {
        return this.entityMetamodel.getPropertyCheckability();
    }

    public boolean[] getNonLazyPropertyUpdateability() {
        return this.entityMetamodel.getNonlazyPropertyUpdateability();
    }

    @Override
    public boolean[] getPropertyInsertability() {
        return this.entityMetamodel.getPropertyInsertability();
    }

    @Override
    @Deprecated
    public ValueInclusion[] getPropertyInsertGenerationInclusions() {
        return null;
    }

    @Override
    @Deprecated
    public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
        return null;
    }

    @Override
    public boolean[] getPropertyNullability() {
        return this.entityMetamodel.getPropertyNullability();
    }

    @Override
    public boolean[] getPropertyVersionability() {
        return this.entityMetamodel.getPropertyVersionability();
    }

    @Override
    public CascadeStyle[] getPropertyCascadeStyles() {
        return this.entityMetamodel.getCascadeStyles();
    }

    @Override
    public final Class getMappedClass() {
        return this.getEntityTuplizer().getMappedClass();
    }

    @Override
    public boolean implementsLifecycle() {
        return this.getEntityTuplizer().isLifecycleImplementor();
    }

    @Override
    public Class getConcreteProxyClass() {
        return this.getEntityTuplizer().getConcreteProxyClass();
    }

    @Override
    public void setPropertyValues(Object object, Object[] values) {
        this.getEntityTuplizer().setPropertyValues(object, values);
    }

    @Override
    public void setPropertyValue(Object object, int i, Object value) {
        this.getEntityTuplizer().setPropertyValue(object, i, value);
    }

    @Override
    public Object[] getPropertyValues(Object object) {
        return this.getEntityTuplizer().getPropertyValues(object);
    }

    @Override
    public Object getPropertyValue(Object object, int i) {
        return this.getEntityTuplizer().getPropertyValue(object, i);
    }

    @Override
    public Object getPropertyValue(Object object, String propertyName) {
        return this.getEntityTuplizer().getPropertyValue(object, propertyName);
    }

    @Override
    public Serializable getIdentifier(Object object) {
        return this.getEntityTuplizer().getIdentifier(object, null);
    }

    @Override
    public Serializable getIdentifier(Object entity, SharedSessionContractImplementor session) {
        return this.getEntityTuplizer().getIdentifier(entity, session);
    }

    @Override
    public void setIdentifier(Object entity, Serializable id, SharedSessionContractImplementor session) {
        this.getEntityTuplizer().setIdentifier(entity, id, session);
    }

    @Override
    public Object getVersion(Object object) {
        return this.getEntityTuplizer().getVersion(object);
    }

    @Override
    public Object instantiate(Serializable id, SharedSessionContractImplementor session) {
        return this.getEntityTuplizer().instantiate(id, session);
    }

    @Override
    public boolean isInstance(Object object) {
        return this.getEntityTuplizer().isInstance(object);
    }

    @Override
    public boolean hasUninitializedLazyProperties(Object object) {
        return this.entityMetamodel.getBytecodeEnhancementMetadata().hasUnFetchedAttributes(object);
    }

    @Override
    public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SharedSessionContractImplementor session) {
        this.getEntityTuplizer().resetIdentifier(entity, currentId, currentVersion, session);
    }

    @Override
    public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
        if (!this.hasSubclasses()) {
            return this;
        }
        String concreteEntityName = this.getEntityTuplizer().determineConcreteSubclassEntityName(instance, factory);
        if (concreteEntityName == null || this.getEntityName().equals(concreteEntityName)) {
            return this;
        }
        return factory.getEntityPersister(concreteEntityName);
    }

    @Override
    public boolean isMultiTable() {
        return false;
    }

    public int getPropertySpan() {
        return this.entityMetamodel.getPropertySpan();
    }

    @Override
    public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SharedSessionContractImplementor session) throws HibernateException {
        return this.getEntityTuplizer().getPropertyValuesToInsert(object, mergeMap, session);
    }

    @Override
    public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session) {
        if (!this.hasInsertGeneratedProperties()) {
            throw new AssertionFailure("no insert-generated properties");
        }
        this.processGeneratedProperties(id, entity, state, session, this.sqlInsertGeneratedValuesSelectString, GenerationTiming.INSERT);
    }

    @Override
    public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session) {
        if (!this.hasUpdateGeneratedProperties()) {
            throw new AssertionFailure("no update-generated properties");
        }
        this.processGeneratedProperties(id, entity, state, session, this.sqlUpdateGeneratedValuesSelectString, GenerationTiming.ALWAYS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session, String selectionSQL, GenerationTiming matchTiming) {
        session.getJdbcCoordinator().executeBatch();
        try {
            PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(selectionSQL);
            try {
                this.getIdentifierType().nullSafeSet(ps, id, 1, session);
                ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(ps);
                try {
                    if (!rs.next()) {
                        throw new HibernateException("Unable to locate row for retrieval of generated properties: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
                    }
                    int propertyIndex = -1;
                    for (NonIdentifierAttribute attribute : this.entityMetamodel.getProperties()) {
                        ++propertyIndex;
                        if (!AbstractEntityPersister.isValueGenerationRequired(attribute, matchTiming)) continue;
                        Object hydratedState = attribute.getType().hydrate(rs, this.getPropertyAliases("", propertyIndex), session, entity);
                        state[propertyIndex] = attribute.getType().resolve(hydratedState, session, entity);
                        this.setPropertyValue(entity, propertyIndex, state[propertyIndex]);
                    }
                }
                finally {
                    if (rs != null) {
                        session.getJdbcCoordinator().getResourceRegistry().release(rs, ps);
                    }
                }
            }
            finally {
                session.getJdbcCoordinator().getResourceRegistry().release(ps);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException e) {
            throw this.getFactory().getSQLExceptionHelper().convert(e, "unable to select generated column values", selectionSQL);
        }
    }

    public static boolean isValueGenerationRequired(NonIdentifierAttribute attribute, GenerationTiming matchTiming) {
        if (attribute.getType() instanceof ComponentType) {
            ComponentType type = (ComponentType)attribute.getType();
            for (ValueGeneration valueGenerationStrategy : type.getPropertyValueGenerationStrategies()) {
                if (!AbstractEntityPersister.isReadRequired(valueGenerationStrategy, matchTiming)) continue;
                return true;
            }
            return false;
        }
        return AbstractEntityPersister.isReadRequired(attribute.getValueGenerationStrategy(), matchTiming);
    }

    private static boolean isReadRequired(ValueGeneration valueGeneration, GenerationTiming matchTiming) {
        return valueGeneration != null && valueGeneration.getValueGenerator() == null && valueGeneration.timingMatches(matchTiming);
    }

    @Override
    public String getIdentifierPropertyName() {
        return this.entityMetamodel.getIdentifierProperty().getName();
    }

    @Override
    public Type getIdentifierType() {
        return this.entityMetamodel.getIdentifierProperty().getType();
    }

    @Override
    public boolean hasSubselectLoadableCollections() {
        return this.hasSubselectLoadableCollections;
    }

    @Override
    public int[] getNaturalIdentifierProperties() {
        return this.entityMetamodel.getNaturalIdentifierProperties();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    public Object[] getNaturalIdentifierSnapshot(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        if (!this.hasNaturalIdentifier()) {
            throw new MappingException("persistent class did not define a natural-id : " + MessageHelper.infoString(this));
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Getting current natural-id snapshot state for: {0}", MessageHelper.infoString((EntityPersister)this, id, this.getFactory()));
        }
        int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
        int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
        boolean[] naturalIdMarkers = new boolean[this.getPropertySpan()];
        Type[] extractionTypes = new Type[naturalIdPropertyCount];
        for (int i = 0; i < naturalIdPropertyCount; ++i) {
            extractionTypes[i] = this.getPropertyTypes()[naturalIdPropertyIndexes[i]];
            naturalIdMarkers[naturalIdPropertyIndexes[i]] = true;
        }
        Select select = new Select(this.getFactory().getDialect());
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("get current natural-id state " + this.getEntityName());
        }
        select.setSelectClause(this.concretePropertySelectFragmentSansLeadingComma(this.getRootAlias(), naturalIdMarkers));
        select.setFromClause(this.fromTableFragment(this.getRootAlias()) + this.fromJoinFragment(this.getRootAlias(), true, false));
        CharSequence[] aliasedIdColumns = StringHelper.qualify(this.getRootAlias(), this.getIdentifierColumnNames());
        String whereClause = String.join((CharSequence)"=? and ", aliasedIdColumns) + "=?" + this.whereJoinFragment(this.getRootAlias(), true, false);
        String sql = select.setOuterJoins("", "").setWhereClause(whereClause).toStatementString();
        Object[] snapshot = new Object[naturalIdPropertyCount];
        try {
            JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
            PreparedStatement ps = jdbcCoordinator.getStatementPreparer().prepareStatement(sql);
            try {
                ResultSet rs;
                block15: {
                    this.getIdentifierType().nullSafeSet(ps, id, 1, session);
                    rs = jdbcCoordinator.getResultSetReturn().extract(ps);
                    if (rs.next()) break block15;
                    Object[] objectArray = null;
                    jdbcCoordinator.getResourceRegistry().release(rs, ps);
                    return objectArray;
                }
                try {
                    EntityKey key = session.generateEntityKey(id, this);
                    Object owner = session.getPersistenceContextInternal().getEntity(key);
                    for (int i = 0; i < naturalIdPropertyCount; ++i) {
                        snapshot[i] = extractionTypes[i].hydrate(rs, this.getPropertyAliases("", naturalIdPropertyIndexes[i]), session, null);
                        if (!extractionTypes[i].isEntityType()) continue;
                        snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
                    }
                    Object[] objectArray = snapshot;
                    jdbcCoordinator.getResourceRegistry().release(rs, ps);
                    return objectArray;
                }
                catch (Throwable throwable) {
                    jdbcCoordinator.getResourceRegistry().release(rs, ps);
                    throw throwable;
                }
            }
            finally {
                jdbcCoordinator.getResourceRegistry().release(ps);
                jdbcCoordinator.afterStatementExecution();
            }
        }
        catch (SQLException e) {
            throw this.getFactory().getSQLExceptionHelper().convert(e, "could not retrieve snapshot: " + MessageHelper.infoString((EntityPersister)this, id, this.getFactory()), sql);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    @Override
    @Deprecated
    public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions, SharedSessionContractImplementor session) {
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Resolving natural-id [%s] to id : %s ", Arrays.asList(naturalIdValues), MessageHelper.infoString(this));
        }
        boolean[] valueNullness = AbstractEntityPersister.determineValueNullness(naturalIdValues);
        String sqlEntityIdByNaturalIdString = this.determinePkByNaturalIdQuery(valueNullness);
        try {
            PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sqlEntityIdByNaturalIdString);
            try {
                ResultSet rs;
                block12: {
                    int positions = 1;
                    int loop = 0;
                    for (int idPosition : this.getNaturalIdentifierProperties()) {
                        Object naturalIdValue;
                        if ((naturalIdValue = naturalIdValues[loop++]) == null) continue;
                        Type type = this.getPropertyTypes()[idPosition];
                        type.nullSafeSet(ps, naturalIdValue, positions, session);
                        positions += type.getColumnSpan(session.getFactory());
                    }
                    rs = session.getJdbcCoordinator().getResultSetReturn().extract(ps);
                    try {
                        if (rs.next()) break block12;
                        Serializable serializable = null;
                        session.getJdbcCoordinator().getResourceRegistry().release(rs, ps);
                        return serializable;
                    }
                    catch (Throwable throwable) {
                        session.getJdbcCoordinator().getResourceRegistry().release(rs, ps);
                        throw throwable;
                    }
                }
                Object hydratedId = this.getIdentifierType().hydrate(rs, this.getIdentifierAliases(), session, null);
                Serializable serializable = (Serializable)this.getIdentifierType().resolve(hydratedId, session, null);
                session.getJdbcCoordinator().getResourceRegistry().release(rs, ps);
                return serializable;
            }
            finally {
                session.getJdbcCoordinator().getResourceRegistry().release(ps);
                session.getJdbcCoordinator().afterStatementExecution();
            }
        }
        catch (SQLException e) {
            throw this.getFactory().getSQLExceptionHelper().convert(e, String.format("could not resolve natural-id [%s] to id : %s", Arrays.asList(naturalIdValues), MessageHelper.infoString(this)), sqlEntityIdByNaturalIdString);
        }
    }

    public static boolean[] determineValueNullness(Object[] naturalIdValues) {
        boolean[] nullness = new boolean[naturalIdValues.length];
        for (int i = 0; i < naturalIdValues.length; ++i) {
            nullness[i] = naturalIdValues[i] == null;
        }
        return nullness;
    }

    @Deprecated
    protected String determinePkByNaturalIdQuery(boolean[] valueNullness) {
        if (!this.hasNaturalIdentifier()) {
            throw new HibernateException("Attempt to build natural-id -> PK resolution query for entity that does not define natural id");
        }
        if (this.isNaturalIdNonNullable()) {
            if (valueNullness != null && !ArrayHelper.isAllFalse(valueNullness)) {
                throw new HibernateException("Null value(s) passed to lookup by non-nullable natural-id");
            }
            if (this.cachedPkByNonNullableNaturalIdQuery == null) {
                this.cachedPkByNonNullableNaturalIdQuery = this.generateEntityIdByNaturalIdSql(null);
            }
            return this.cachedPkByNonNullableNaturalIdQuery;
        }
        return this.generateEntityIdByNaturalIdSql(valueNullness);
    }

    public boolean isNaturalIdNonNullable() {
        if (this.naturalIdIsNonNullable == null) {
            this.naturalIdIsNonNullable = this.determineNaturalIdNullability();
        }
        return this.naturalIdIsNonNullable;
    }

    private boolean determineNaturalIdNullability() {
        boolean[] nullability = this.getPropertyNullability();
        for (int position : this.getNaturalIdentifierProperties()) {
            if (!nullability[position]) continue;
            return false;
        }
        return true;
    }

    @Deprecated
    private String generateEntityIdByNaturalIdSql(boolean[] valueNullness) {
        EntityPersister rootPersister = this.getFactory().getEntityPersister(this.getRootEntityName());
        if (rootPersister != this && rootPersister instanceof AbstractEntityPersister) {
            return ((AbstractEntityPersister)rootPersister).generateEntityIdByNaturalIdSql(valueNullness);
        }
        Select select = new Select(this.getFactory().getDialect());
        if (this.getFactory().getSessionFactoryOptions().isCommentsEnabled()) {
            select.setComment("get current natural-id->entity-id state " + this.getEntityName());
        }
        String rootAlias = this.getRootAlias();
        select.setSelectClause(this.identifierSelectFragment(rootAlias, ""));
        select.setFromClause(this.fromTableFragment(rootAlias) + this.fromJoinFragment(rootAlias, true, false));
        StringBuilder whereClause = new StringBuilder();
        int[] propertyTableNumbers = this.getPropertyTableNumbers();
        int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
        int valuesIndex = -1;
        for (int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; ++propIdx) {
            ++valuesIndex;
            if (propIdx > 0) {
                whereClause.append(" and ");
            }
            int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
            String tableAlias = AbstractEntityPersister.generateTableAlias(rootAlias, propertyTableNumbers[naturalIdIdx]);
            String[] propertyColumnNames = this.getPropertyColumnNames(naturalIdIdx);
            CharSequence[] aliasedPropertyColumns = StringHelper.qualify(tableAlias, propertyColumnNames);
            if (valueNullness != null && valueNullness[valuesIndex]) {
                whereClause.append(String.join((CharSequence)" is null and ", aliasedPropertyColumns)).append(" is null");
                continue;
            }
            whereClause.append(String.join((CharSequence)"=? and ", aliasedPropertyColumns)).append("=?");
        }
        whereClause.append(this.whereJoinFragment(this.getRootAlias(), true, false));
        return select.setOuterJoins("", "").setWhereClause(whereClause.toString()).toStatementString();
    }

    protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
        String concretePropertySelectFragment = this.concretePropertySelectFragment(alias, include);
        int firstComma = concretePropertySelectFragment.indexOf(", ");
        if (firstComma == 0) {
            concretePropertySelectFragment = concretePropertySelectFragment.substring(2);
        }
        return concretePropertySelectFragment;
    }

    @Override
    public boolean hasNaturalIdentifier() {
        return this.entityMetamodel.hasNaturalIdentifier();
    }

    @Override
    public void setPropertyValue(Object object, String propertyName, Object value) {
        this.getEntityTuplizer().setPropertyValue(object, propertyName, value);
    }

    public static int getTableId(String tableName, String[] tables) {
        for (int j = 0; j < tables.length; ++j) {
            if (!tableName.equalsIgnoreCase(tables[j])) continue;
            return j;
        }
        throw new AssertionFailure("Table " + tableName + " not found");
    }

    @Override
    public EntityMode getEntityMode() {
        return this.entityMetamodel.getEntityMode();
    }

    @Override
    public EntityTuplizer getEntityTuplizer() {
        return this.entityTuplizer;
    }

    @Override
    public BytecodeEnhancementMetadata getInstrumentationMetadata() {
        return this.getBytecodeEnhancementMetadata();
    }

    @Override
    public BytecodeEnhancementMetadata getBytecodeEnhancementMetadata() {
        return this.entityMetamodel.getBytecodeEnhancementMetadata();
    }

    @Override
    public String getTableAliasForColumn(String columnName, String rootAlias) {
        return AbstractEntityPersister.generateTableAlias(rootAlias, this.determineTableNumberForColumn(columnName));
    }

    public int determineTableNumberForColumn(String columnName) {
        return 0;
    }

    protected String determineTableName(Table table) {
        if (table.getSubselect() != null) {
            return "( " + table.getSubselect() + " )";
        }
        return this.factory.getSqlStringGenerationContext().format(table.getQualifiedTableName());
    }

    @Override
    public EntityEntryFactory getEntityEntryFactory() {
        return this.entityEntryFactory;
    }

    @Override
    public void generateEntityDefinition() {
        this.prepareEntityIdentifierDefinition();
        this.collectAttributeDefinitions();
    }

    @Override
    public EntityPersister getEntityPersister() {
        return this;
    }

    @Override
    public EntityIdentifierDefinition getEntityKeyDefinition() {
        return this.entityIdentifierDefinition;
    }

    @Override
    public Iterable<AttributeDefinition> getAttributes() {
        return this.attributeDefinitions;
    }

    public String[][] getPolymorphicJoinColumns(String lhsTableAlias, String propertyPath) {
        Set subclassEntityNames = this.getEntityMetamodel().getSubclassEntityNames();
        ArrayList<Object[]> polymorphicJoinColumns = new ArrayList<Object[]>(subclassEntityNames.size());
        block0: for (String subclassEntityName : subclassEntityNames) {
            AbstractEntityPersister subclassPersister = (AbstractEntityPersister)this.getFactory().getMetamodel().entityPersister(subclassEntityName);
            Object[] joinColumns = subclassPersister.toColumns(lhsTableAlias, propertyPath);
            if (joinColumns.length == 0) continue;
            for (Object[] existingColumns : polymorphicJoinColumns) {
                if (!Arrays.deepEquals(existingColumns, joinColumns)) continue;
                continue block0;
            }
            polymorphicJoinColumns.add(joinColumns);
        }
        return ArrayHelper.to2DStringArray(polymorphicJoinColumns);
    }

    public boolean canOmitSuperclassTableJoin() {
        return false;
    }

    private void prepareEntityIdentifierDefinition() {
        if (this.entityIdentifierDefinition != null) {
            return;
        }
        Type idType = this.getIdentifierType();
        if (!idType.isComponentType()) {
            this.entityIdentifierDefinition = EntityIdentifierDefinitionHelper.buildSimpleEncapsulatedIdentifierDefinition(this);
            return;
        }
        CompositeType cidType = (CompositeType)idType;
        if (!cidType.isEmbedded()) {
            this.entityIdentifierDefinition = EntityIdentifierDefinitionHelper.buildEncapsulatedCompositeIdentifierDefinition(this);
            return;
        }
        this.entityIdentifierDefinition = EntityIdentifierDefinitionHelper.buildNonEncapsulatedCompositeIdentifierDefinition(this);
    }

    private void collectAttributeDefinitions(Map<String, AttributeDefinition> attributeDefinitionsByName, EntityMetamodel metamodel) {
        for (int i = 0; i < metamodel.getPropertySpan(); ++i) {
            NonIdentifierAttribute attributeDefinition = metamodel.getProperties()[i];
            AttributeDefinition oldAttributeDefinition = attributeDefinitionsByName.get(attributeDefinition.getName());
            if (oldAttributeDefinition != null) {
                if (!LOG.isTraceEnabled()) continue;
                LOG.tracef("Ignoring subclass attribute definition [%s.%s] because it is defined in a superclass ", this.entityMetamodel.getName(), attributeDefinition.getName());
                continue;
            }
            attributeDefinitionsByName.put(attributeDefinition.getName(), attributeDefinition);
        }
        Set subClassEntityNames = metamodel.getSubclassEntityNames();
        if (subClassEntityNames == null) {
            return;
        }
        for (String subClassEntityName : subClassEntityNames) {
            if (metamodel.getName().equals(subClassEntityName)) continue;
            try {
                EntityPersister subClassEntityPersister = this.factory.getEntityPersister(subClassEntityName);
                this.collectAttributeDefinitions(attributeDefinitionsByName, subClassEntityPersister.getEntityMetamodel());
            }
            catch (MappingException e) {
                throw new IllegalStateException(String.format("Could not locate subclass EntityPersister [%s] while processing EntityPersister [%s]", subClassEntityName, metamodel.getName()), (Throwable)((Object)e));
            }
        }
    }

    private void collectAttributeDefinitions() {
        LinkedHashMap<String, AttributeDefinition> attributeDefinitionsByName = new LinkedHashMap<String, AttributeDefinition>();
        this.collectAttributeDefinitions(attributeDefinitionsByName, this.getEntityMetamodel());
        this.attributeDefinitions = CollectionHelper.toSmallList(new ArrayList(attributeDefinitionsByName.values()));
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

    private static class SubstituteBracketSQLQueryParser
    extends SQLQueryParser {
        SubstituteBracketSQLQueryParser(String queryString, SessionFactoryImplementor factory) {
            super(queryString, null, factory);
        }

        @Override
        public String process() {
            return this.substituteBrackets(this.getOriginalQueryString());
        }
    }

    private static class NoopCacheEntryHelper
    implements CacheEntryHelper {
        public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();

        private NoopCacheEntryHelper() {
        }

        @Override
        public CacheEntryStructure getCacheEntryStructure() {
            return UnstructuredCacheEntry.INSTANCE;
        }

        @Override
        public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
            throw new HibernateException("Illegal attempt to build cache entry for non-cached entity");
        }
    }

    private static class StructuredCacheEntryHelper
    implements CacheEntryHelper {
        private final EntityPersister persister;
        private final StructuredCacheEntry structure;

        private StructuredCacheEntryHelper(EntityPersister persister) {
            this.persister = persister;
            this.structure = new StructuredCacheEntry(persister);
        }

        @Override
        public CacheEntryStructure getCacheEntryStructure() {
            return this.structure;
        }

        @Override
        public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
            return new StandardCacheEntryImpl(state, this.persister, version, session, entity);
        }
    }

    private static class ReferenceCacheEntryHelper
    implements CacheEntryHelper {
        private final EntityPersister persister;

        private ReferenceCacheEntryHelper(EntityPersister persister) {
            this.persister = persister;
        }

        @Override
        public CacheEntryStructure getCacheEntryStructure() {
            return UnstructuredCacheEntry.INSTANCE;
        }

        @Override
        public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
            return new ReferenceCacheEntryImpl(entity, this.persister);
        }
    }

    private static class StandardCacheEntryHelper
    implements CacheEntryHelper {
        private final EntityPersister persister;

        private StandardCacheEntryHelper(EntityPersister persister) {
            this.persister = persister;
        }

        @Override
        public CacheEntryStructure getCacheEntryStructure() {
            return UnstructuredCacheEntry.INSTANCE;
        }

        @Override
        public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
            return new StandardCacheEntryImpl(state, this.persister, version, session, entity);
        }
    }

    public static interface CacheEntryHelper {
        public CacheEntryStructure getCacheEntryStructure();

        public CacheEntry buildCacheEntry(Object var1, Object[] var2, Object var3, SharedSessionContractImplementor var4);
    }

    protected static interface InclusionChecker {
        public boolean includeProperty(int var1);
    }
}

