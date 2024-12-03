/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.FilterConfiguration;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Fetchable;
import org.hibernate.mapping.Filterable;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

public abstract class Collection
implements Fetchable,
Value,
Filterable {
    public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
    public static final String DEFAULT_KEY_COLUMN_NAME = "id";
    private final MetadataImplementor metadata;
    private PersistentClass owner;
    private KeyValue key;
    private Value element;
    private Table collectionTable;
    private String role;
    private boolean lazy;
    private boolean extraLazy;
    private boolean inverse;
    private boolean mutable = true;
    private boolean subselectLoadable;
    private String cacheConcurrencyStrategy;
    private String cacheRegionName;
    private String orderBy;
    private String where;
    private String manyToManyWhere;
    private String manyToManyOrderBy;
    private String referencedPropertyName;
    private String mappedByProperty;
    private boolean sorted;
    private Comparator comparator;
    private String comparatorClassName;
    private boolean orphanDelete;
    private int batchSize = -1;
    private FetchMode fetchMode;
    private boolean embedded = true;
    private boolean optimisticLocked = true;
    private Class collectionPersisterClass;
    private String typeName;
    private Properties typeParameters;
    private final List filters = new ArrayList();
    private final List manyToManyFilters = new ArrayList();
    private final Set<String> synchronizedTables = new HashSet<String>();
    private String customSQLInsert;
    private boolean customInsertCallable;
    private ExecuteUpdateResultCheckStyle insertCheckStyle;
    private String customSQLUpdate;
    private boolean customUpdateCallable;
    private ExecuteUpdateResultCheckStyle updateCheckStyle;
    private String customSQLDelete;
    private boolean customDeleteCallable;
    private ExecuteUpdateResultCheckStyle deleteCheckStyle;
    private String customSQLDeleteAll;
    private boolean customDeleteAllCallable;
    private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
    private String loaderName;

    protected Collection(MetadataBuildingContext buildingContext, PersistentClass owner) {
        this(buildingContext.getMetadataCollector(), owner);
    }

    @Deprecated
    protected Collection(MetadataImplementor metadata, PersistentClass owner) {
        this.metadata = metadata;
        this.owner = owner;
    }

    public MetadataImplementor getMetadata() {
        return this.metadata;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return this.getMetadata().getMetadataBuildingOptions().getServiceRegistry();
    }

    public boolean isSet() {
        return false;
    }

    public KeyValue getKey() {
        return this.key;
    }

    public Value getElement() {
        return this.element;
    }

    public boolean isIndexed() {
        return false;
    }

    public Table getCollectionTable() {
        return this.collectionTable;
    }

    public void setCollectionTable(Table table) {
        this.collectionTable = table;
    }

    public boolean isSorted() {
        return this.sorted;
    }

    public Comparator getComparator() {
        if (this.comparator == null && this.comparatorClassName != null) {
            try {
                ClassLoaderService classLoaderService = this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class);
                this.setComparator((Comparator)classLoaderService.classForName(this.comparatorClassName).newInstance());
            }
            catch (Exception e) {
                throw new MappingException("Could not instantiate comparator class [" + this.comparatorClassName + "] for collection " + this.getRole());
            }
        }
        return this.comparator;
    }

    @Override
    public boolean isLazy() {
        return this.lazy;
    }

    @Override
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public String getRole() {
        return this.role;
    }

    public abstract CollectionType getDefaultCollectionType() throws MappingException;

    public boolean isPrimitiveArray() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    @Override
    public boolean hasFormula() {
        return false;
    }

    public boolean isOneToMany() {
        return this.element instanceof OneToMany;
    }

    public boolean isInverse() {
        return this.inverse;
    }

    public String getOwnerEntityName() {
        return this.owner.getEntityName();
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public void setElement(Value element) {
        this.element = element;
    }

    public void setKey(KeyValue key) {
        this.key = key;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    public PersistentClass getOwner() {
        return this.owner;
    }

    @Deprecated
    public void setOwner(PersistentClass owner) {
        this.owner = owner;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getManyToManyWhere() {
        return this.manyToManyWhere;
    }

    public void setManyToManyWhere(String manyToManyWhere) {
        this.manyToManyWhere = manyToManyWhere;
    }

    public String getManyToManyOrdering() {
        return this.manyToManyOrderBy;
    }

    public void setManyToManyOrdering(String orderFragment) {
        this.manyToManyOrderBy = orderFragment;
    }

    public boolean isIdentified() {
        return false;
    }

    public boolean hasOrphanDelete() {
        return this.orphanDelete;
    }

    public void setOrphanDelete(boolean orphanDelete) {
        this.orphanDelete = orphanDelete;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int i) {
        this.batchSize = i;
    }

    @Override
    public FetchMode getFetchMode() {
        return this.fetchMode;
    }

    @Override
    public void setFetchMode(FetchMode fetchMode) {
        this.fetchMode = fetchMode;
    }

    public void setCollectionPersisterClass(Class persister) {
        this.collectionPersisterClass = persister;
    }

    public Class getCollectionPersisterClass() {
        return this.collectionPersisterClass;
    }

    public void validate(Mapping mapping) throws MappingException {
        assert (this.getKey() != null) : "Collection key not bound : " + this.getRole();
        assert (this.getElement() != null) : "Collection element not bound : " + this.getRole();
        if (!this.getKey().isValid(mapping)) {
            throw new MappingException("collection foreign key mapping has wrong number of columns: " + this.getRole() + " type: " + this.getKey().getType().getName());
        }
        if (!this.getElement().isValid(mapping)) {
            throw new MappingException("collection element mapping has wrong number of columns: " + this.getRole() + " type: " + this.getElement().getType().getName());
        }
        this.checkColumnDuplication();
    }

    private void checkColumnDuplication(Set distinctColumns, Value value) throws MappingException {
        boolean[] insertability = value.getColumnInsertability();
        boolean[] updatability = value.getColumnUpdateability();
        Iterator<Selectable> iterator = value.getColumnIterator();
        int i = 0;
        while (iterator.hasNext()) {
            Column col;
            Selectable s = iterator.next();
            if (!s.isFormula() && (insertability[i] || updatability[i]) && !distinctColumns.add((col = (Column)s).getName())) {
                throw new MappingException("Repeated column in mapping for collection: " + this.getRole() + " column: " + col.getName());
            }
            ++i;
        }
    }

    private void checkColumnDuplication() throws MappingException {
        HashSet cols = new HashSet();
        this.checkColumnDuplication(cols, this.getKey());
        if (this.isIndexed()) {
            this.checkColumnDuplication(cols, ((IndexedCollection)this).getIndex());
        }
        if (this.isIdentified()) {
            this.checkColumnDuplication(cols, ((IdentifierCollection)this).getIdentifier());
        }
        if (!this.isOneToMany()) {
            this.checkColumnDuplication(cols, this.getElement());
        }
    }

    @Override
    public Iterator<Selectable> getColumnIterator() {
        return Collections.emptyList().iterator();
    }

    @Override
    public int getColumnSpan() {
        return 0;
    }

    @Override
    public Type getType() throws MappingException {
        return this.getCollectionType();
    }

    public CollectionType getCollectionType() {
        if (this.typeName == null) {
            return this.getDefaultCollectionType();
        }
        return this.getMetadata().getTypeConfiguration().getTypeResolver().getTypeFactory().customCollection(this.typeName, this.typeParameters, this.role, this.referencedPropertyName);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public boolean isAlternateUniqueKey() {
        return false;
    }

    @Override
    public Table getTable() {
        return this.owner.getTable();
    }

    @Override
    public void createForeignKey() {
    }

    @Override
    public boolean isSimpleValue() {
        return false;
    }

    @Override
    public boolean isValid(Mapping mapping) throws MappingException {
        return true;
    }

    @Override
    public boolean isSame(Value other) {
        return this == other || other instanceof Collection && this.isSame((Collection)other);
    }

    protected static boolean isSame(Value v1, Value v2) {
        return v1 == v2 || v1 != null && v2 != null && v1.isSame(v2);
    }

    public boolean isSame(Collection other) {
        return this == other || Collection.isSame(this.key, other.key) && Collection.isSame(this.element, other.element) && Objects.equals(this.collectionTable, other.collectionTable) && Objects.equals(this.where, other.where) && Objects.equals(this.manyToManyWhere, other.manyToManyWhere) && Objects.equals(this.referencedPropertyName, other.referencedPropertyName) && Objects.equals(this.mappedByProperty, other.mappedByProperty) && Objects.equals(this.typeName, other.typeName) && Objects.equals(this.typeParameters, other.typeParameters);
    }

    private void createForeignKeys() throws MappingException {
        if (this.referencedPropertyName == null) {
            this.getElement().createForeignKey();
            this.key.createForeignKeyOfEntity(this.getOwner().getEntityName());
        }
    }

    abstract void createPrimaryKey();

    public void createAllKeys() throws MappingException {
        this.createForeignKeys();
        if (!this.isInverse()) {
            this.createPrimaryKey();
        }
    }

    public String getCacheConcurrencyStrategy() {
        return this.cacheConcurrencyStrategy;
    }

    public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
        this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
    }

    @Override
    public void setTypeUsingReflection(String className, String propertyName) {
    }

    public String getCacheRegionName() {
        return this.cacheRegionName == null ? this.role : this.cacheRegionName;
    }

    public void setCacheRegionName(String cacheRegionName) {
        this.cacheRegionName = StringHelper.nullIfEmpty(cacheRegionName);
    }

    public void setCustomSQLInsert(String customSQLInsert, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
        this.customSQLInsert = customSQLInsert;
        this.customInsertCallable = callable;
        this.insertCheckStyle = checkStyle;
    }

    public String getCustomSQLInsert() {
        return this.customSQLInsert;
    }

    public boolean isCustomInsertCallable() {
        return this.customInsertCallable;
    }

    public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {
        return this.insertCheckStyle;
    }

    public void setCustomSQLUpdate(String customSQLUpdate, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
        this.customSQLUpdate = customSQLUpdate;
        this.customUpdateCallable = callable;
        this.updateCheckStyle = checkStyle;
    }

    public String getCustomSQLUpdate() {
        return this.customSQLUpdate;
    }

    public boolean isCustomUpdateCallable() {
        return this.customUpdateCallable;
    }

    public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {
        return this.updateCheckStyle;
    }

    public void setCustomSQLDelete(String customSQLDelete, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
        this.customSQLDelete = customSQLDelete;
        this.customDeleteCallable = callable;
        this.deleteCheckStyle = checkStyle;
    }

    public String getCustomSQLDelete() {
        return this.customSQLDelete;
    }

    public boolean isCustomDeleteCallable() {
        return this.customDeleteCallable;
    }

    public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {
        return this.deleteCheckStyle;
    }

    public void setCustomSQLDeleteAll(String customSQLDeleteAll, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
        this.customSQLDeleteAll = customSQLDeleteAll;
        this.customDeleteAllCallable = callable;
        this.deleteAllCheckStyle = checkStyle;
    }

    public String getCustomSQLDeleteAll() {
        return this.customSQLDeleteAll;
    }

    public boolean isCustomDeleteAllCallable() {
        return this.customDeleteAllCallable;
    }

    public ExecuteUpdateResultCheckStyle getCustomSQLDeleteAllCheckStyle() {
        return this.deleteAllCheckStyle;
    }

    @Override
    public void addFilter(String name, String condition, boolean autoAliasInjection, Map<String, String> aliasTableMap, Map<String, String> aliasEntityMap) {
        this.filters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap, null));
    }

    @Override
    public List getFilters() {
        return this.filters;
    }

    public void addManyToManyFilter(String name, String condition, boolean autoAliasInjection, Map<String, String> aliasTableMap, Map<String, String> aliasEntityMap) {
        this.manyToManyFilters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap, null));
    }

    public List getManyToManyFilters() {
        return this.manyToManyFilters;
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.getRole() + ')';
    }

    public Set<String> getSynchronizedTables() {
        return this.synchronizedTables;
    }

    public String getLoaderName() {
        return this.loaderName;
    }

    public void setLoaderName(String name) {
        this.loaderName = name;
    }

    public String getReferencedPropertyName() {
        return this.referencedPropertyName;
    }

    public void setReferencedPropertyName(String propertyRef) {
        this.referencedPropertyName = propertyRef;
    }

    public boolean isOptimisticLocked() {
        return this.optimisticLocked;
    }

    public void setOptimisticLocked(boolean optimisticLocked) {
        this.optimisticLocked = optimisticLocked;
    }

    public boolean isMap() {
        return false;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Properties getTypeParameters() {
        return this.typeParameters;
    }

    public void setTypeParameters(Properties parameterMap) {
        this.typeParameters = parameterMap;
    }

    public void setTypeParameters(Map parameterMap) {
        if (parameterMap instanceof Properties) {
            this.typeParameters = (Properties)parameterMap;
        } else {
            this.typeParameters = new Properties();
            this.typeParameters.putAll((Map<?, ?>)parameterMap);
        }
    }

    @Override
    public boolean[] getColumnInsertability() {
        return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
    }

    @Override
    public boolean[] getColumnUpdateability() {
        return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
    }

    public boolean isSubselectLoadable() {
        return this.subselectLoadable;
    }

    public void setSubselectLoadable(boolean subqueryLoadable) {
        this.subselectLoadable = subqueryLoadable;
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public boolean isExtraLazy() {
        return this.extraLazy;
    }

    public void setExtraLazy(boolean extraLazy) {
        this.extraLazy = extraLazy;
    }

    public boolean hasOrder() {
        return this.orderBy != null || this.manyToManyOrderBy != null;
    }

    public void setComparatorClassName(String comparatorClassName) {
        this.comparatorClassName = comparatorClassName;
    }

    public String getComparatorClassName() {
        return this.comparatorClassName;
    }

    public String getMappedByProperty() {
        return this.mappedByProperty;
    }

    public void setMappedByProperty(String mappedByProperty) {
        this.mappedByProperty = mappedByProperty;
    }
}

