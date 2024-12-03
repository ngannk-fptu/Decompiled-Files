/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.hibernate.EntityMode;
import org.hibernate.MappingException;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.FilterConfiguration;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.internal.util.collections.SingletonIterator;
import org.hibernate.jpa.event.spi.CallbackDefinition;
import org.hibernate.mapping.AttributeContainer;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Filterable;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.MetaAttributable;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.sql.Alias;

public abstract class PersistentClass
implements AttributeContainer,
Serializable,
Filterable,
MetaAttributable {
    private static final Alias PK_ALIAS = new Alias(15, "PK");
    public static final String NULL_DISCRIMINATOR_MAPPING = "null";
    public static final String NOT_NULL_DISCRIMINATOR_MAPPING = "not null";
    private final MetadataBuildingContext metadataBuildingContext;
    private String entityName;
    private String className;
    private transient Class mappedClass;
    private String proxyInterfaceName;
    private transient Class proxyInterface;
    private String jpaEntityName;
    private String discriminatorValue;
    private boolean lazy;
    private List<Property> properties = new ArrayList<Property>();
    private List<Property> declaredProperties = new ArrayList<Property>();
    private final List<Subclass> subclasses = new ArrayList<Subclass>();
    private final List<Property> subclassProperties = new ArrayList<Property>();
    private final List<Table> subclassTables = new ArrayList<Table>();
    private boolean dynamicInsert;
    private boolean dynamicUpdate;
    private int batchSize = -1;
    private boolean selectBeforeUpdate;
    private Map metaAttributes;
    private List<Join> joins = new ArrayList<Join>();
    private final List<Join> subclassJoins = new ArrayList<Join>();
    private final List<FilterConfiguration> filters = new ArrayList<FilterConfiguration>();
    protected final Set<String> synchronizedTables = new HashSet<String>();
    private String loaderName;
    private Boolean isAbstract;
    private boolean hasSubselectLoadableCollections;
    private Component identifierMapper;
    private List<CallbackDefinition> callbackDefinitions;
    private String customSQLInsert;
    private boolean customInsertCallable;
    private ExecuteUpdateResultCheckStyle insertCheckStyle;
    private String customSQLUpdate;
    private boolean customUpdateCallable;
    private ExecuteUpdateResultCheckStyle updateCheckStyle;
    private String customSQLDelete;
    private boolean customDeleteCallable;
    private ExecuteUpdateResultCheckStyle deleteCheckStyle;
    private Map tuplizerImpls;
    private MappedSuperclass superMappedSuperclass;
    private Component declaredIdentifierMapper;
    private OptimisticLockStyle optimisticLockStyle;
    private boolean isCached;

    public PersistentClass(MetadataBuildingContext metadataBuildingContext) {
        this.metadataBuildingContext = metadataBuildingContext;
    }

    public ServiceRegistry getServiceRegistry() {
        return this.metadataBuildingContext.getBuildingOptions().getServiceRegistry();
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className == null ? null : className.intern();
        this.mappedClass = null;
    }

    public String getProxyInterfaceName() {
        return this.proxyInterfaceName;
    }

    public void setProxyInterfaceName(String proxyInterfaceName) {
        this.proxyInterfaceName = proxyInterfaceName;
        this.proxyInterface = null;
    }

    public Class getMappedClass() throws MappingException {
        if (this.className == null) {
            return null;
        }
        try {
            if (this.mappedClass == null) {
                this.mappedClass = this.metadataBuildingContext.getBootstrapContext().getClassLoaderAccess().classForName(this.className);
            }
            return this.mappedClass;
        }
        catch (ClassLoadingException e) {
            throw new MappingException("entity class not found: " + this.className, (Throwable)((Object)e));
        }
    }

    public Class getProxyInterface() {
        if (this.proxyInterfaceName == null) {
            return null;
        }
        try {
            if (this.proxyInterface == null) {
                this.proxyInterface = this.metadataBuildingContext.getBootstrapContext().getClassLoaderAccess().classForName(this.proxyInterfaceName);
            }
            return this.proxyInterface;
        }
        catch (ClassLoadingException e) {
            throw new MappingException("proxy class not found: " + this.proxyInterfaceName, (Throwable)((Object)e));
        }
    }

    public boolean useDynamicInsert() {
        return this.dynamicInsert;
    }

    abstract int nextSubclassId();

    public abstract int getSubclassId();

    public boolean useDynamicUpdate() {
        return this.dynamicUpdate;
    }

    public void setDynamicInsert(boolean dynamicInsert) {
        this.dynamicInsert = dynamicInsert;
    }

    public void setDynamicUpdate(boolean dynamicUpdate) {
        this.dynamicUpdate = dynamicUpdate;
    }

    public String getDiscriminatorValue() {
        return this.discriminatorValue;
    }

    public void addSubclass(Subclass subclass) throws MappingException {
        for (PersistentClass superclass = this.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            if (!subclass.getEntityName().equals(superclass.getEntityName())) continue;
            throw new MappingException("Circular inheritance mapping detected: " + subclass.getEntityName() + " will have it self as superclass when extending " + this.getEntityName());
        }
        this.subclasses.add(subclass);
    }

    public boolean hasSubclasses() {
        return this.subclasses.size() > 0;
    }

    public int getSubclassSpan() {
        int n = this.subclasses.size();
        for (Subclass subclass : this.subclasses) {
            n += subclass.getSubclassSpan();
        }
        return n;
    }

    public Iterator getSubclassIterator() {
        Iterator[] iters = new Iterator[this.subclasses.size() + 1];
        Iterator<Subclass> iter = this.subclasses.iterator();
        int i = 0;
        while (iter.hasNext()) {
            iters[i++] = iter.next().getSubclassIterator();
        }
        iters[i] = this.subclasses.iterator();
        return new JoinedIterator(iters);
    }

    public Iterator getSubclassClosureIterator() {
        ArrayList iters = new ArrayList();
        iters.add(new SingletonIterator<PersistentClass>(this));
        Iterator iter = this.getSubclassIterator();
        while (iter.hasNext()) {
            PersistentClass clazz = (PersistentClass)iter.next();
            iters.add(clazz.getSubclassClosureIterator());
        }
        return new JoinedIterator(iters);
    }

    public Table getIdentityTable() {
        return this.getRootTable();
    }

    public Iterator getDirectSubclasses() {
        return this.subclasses.iterator();
    }

    @Override
    public void addProperty(Property p) {
        this.properties.add(p);
        this.declaredProperties.add(p);
        p.setPersistentClass(this);
    }

    public abstract Table getTable();

    public String getEntityName() {
        return this.entityName;
    }

    public abstract boolean isMutable();

    public abstract boolean hasIdentifierProperty();

    public abstract Property getIdentifierProperty();

    public abstract Property getDeclaredIdentifierProperty();

    public abstract KeyValue getIdentifier();

    public abstract Property getVersion();

    public abstract Property getDeclaredVersion();

    public abstract Value getDiscriminator();

    public abstract boolean isInherited();

    public abstract boolean isPolymorphic();

    public abstract boolean isVersioned();

    public boolean isCached() {
        return this.isCached;
    }

    public void setCached(boolean cached) {
        this.isCached = cached;
    }

    @Deprecated
    public boolean isCachingExplicitlyRequested() {
        return this.isCached();
    }

    @Deprecated
    public void setCachingExplicitlyRequested(boolean cached) {
        this.setCached(cached);
    }

    public abstract String getCacheConcurrencyStrategy();

    public abstract String getNaturalIdCacheRegionName();

    public abstract PersistentClass getSuperclass();

    public abstract boolean isExplicitPolymorphism();

    public abstract boolean isDiscriminatorInsertable();

    public abstract Iterator getPropertyClosureIterator();

    public abstract Iterator getTableClosureIterator();

    public abstract Iterator getKeyClosureIterator();

    protected void addSubclassProperty(Property prop) {
        this.subclassProperties.add(prop);
    }

    protected void addSubclassJoin(Join join) {
        this.subclassJoins.add(join);
    }

    protected void addSubclassTable(Table subclassTable) {
        this.subclassTables.add(subclassTable);
    }

    public Iterator getSubclassPropertyClosureIterator() {
        ArrayList iters = new ArrayList();
        iters.add(this.getPropertyClosureIterator());
        iters.add(this.subclassProperties.iterator());
        for (int i = 0; i < this.subclassJoins.size(); ++i) {
            Join join = this.subclassJoins.get(i);
            iters.add(join.getPropertyIterator());
        }
        return new JoinedIterator(iters);
    }

    public Iterator getSubclassJoinClosureIterator() {
        return new JoinedIterator(this.getJoinClosureIterator(), this.subclassJoins.iterator());
    }

    public Iterator getSubclassTableClosureIterator() {
        return new JoinedIterator(this.getTableClosureIterator(), this.subclassTables.iterator());
    }

    public boolean isClassOrSuperclassJoin(Join join) {
        return this.joins.contains(join);
    }

    public boolean isClassOrSuperclassTable(Table closureTable) {
        return this.getTable() == closureTable;
    }

    public boolean isLazy() {
        return this.lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public abstract boolean hasEmbeddedIdentifier();

    public abstract Class getEntityPersisterClass();

    public abstract void setEntityPersisterClass(Class var1);

    public abstract Table getRootTable();

    public abstract RootClass getRootClass();

    public abstract KeyValue getKey();

    public void setDiscriminatorValue(String discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName == null ? null : entityName.intern();
    }

    public void createPrimaryKey() {
        Table table = this.getTable();
        PrimaryKey pk = new PrimaryKey(table);
        pk.setName(PK_ALIAS.toAliasString(table.getName()));
        table.setPrimaryKey(pk);
        pk.addColumns(this.getKey().getColumnIterator());
    }

    public abstract String getWhere();

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean hasSelectBeforeUpdate() {
        return this.selectBeforeUpdate;
    }

    public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
        this.selectBeforeUpdate = selectBeforeUpdate;
    }

    public Iterator getReferenceablePropertyIterator() {
        return this.getPropertyClosureIterator();
    }

    public Property getReferencedProperty(String propertyPath) throws MappingException {
        try {
            return this.getRecursiveProperty(propertyPath, this.getReferenceablePropertyIterator());
        }
        catch (MappingException e) {
            throw new MappingException("property-ref [" + propertyPath + "] not found on entity [" + this.getEntityName() + "]", (Throwable)((Object)e));
        }
    }

    public Property getRecursiveProperty(String propertyPath) throws MappingException {
        try {
            return this.getRecursiveProperty(propertyPath, this.getPropertyClosureIterator());
        }
        catch (MappingException e) {
            throw new MappingException("property [" + propertyPath + "] not found on entity [" + this.getEntityName() + "]", (Throwable)((Object)e));
        }
    }

    private Property getRecursiveProperty(String propertyPath, Iterator iter) throws MappingException {
        Property property = null;
        StringTokenizer st = new StringTokenizer(propertyPath, ".", false);
        try {
            while (st.hasMoreElements()) {
                String element = (String)st.nextElement();
                if (property == null) {
                    Property identifierProperty = this.getIdentifierProperty();
                    if (identifierProperty != null && identifierProperty.getName().equals(element)) {
                        property = identifierProperty;
                    } else if (identifierProperty == null && this.getIdentifierMapper() != null) {
                        try {
                            identifierProperty = this.getProperty(element, this.getIdentifierMapper().getPropertyIterator());
                            if (identifierProperty != null) {
                                property = identifierProperty;
                            }
                        }
                        catch (MappingException mappingException) {
                            // empty catch block
                        }
                    }
                    if (property != null) continue;
                    property = this.getProperty(element, iter);
                    continue;
                }
                property = ((Component)property.getValue()).getProperty(element);
            }
        }
        catch (MappingException e) {
            throw new MappingException("property [" + propertyPath + "] not found on entity [" + this.getEntityName() + "]");
        }
        return property;
    }

    private Property getProperty(String propertyName, Iterator iterator) throws MappingException {
        if (iterator.hasNext()) {
            String root = StringHelper.root(propertyName);
            while (iterator.hasNext()) {
                Property prop = (Property)iterator.next();
                if (!prop.getName().equals(root)) continue;
                return prop;
            }
        }
        throw new MappingException("property [" + propertyName + "] not found on entity [" + this.getEntityName() + "]");
    }

    public Property getProperty(String propertyName) throws MappingException {
        Iterator iter = this.getPropertyClosureIterator();
        Property identifierProperty = this.getIdentifierProperty();
        if (identifierProperty != null && identifierProperty.getName().equals(StringHelper.root(propertyName))) {
            return identifierProperty;
        }
        return this.getProperty(propertyName, iter);
    }

    public boolean hasProperty(String name) {
        Property identifierProperty = this.getIdentifierProperty();
        if (identifierProperty != null && identifierProperty.getName().equals(name)) {
            return true;
        }
        Iterator itr = this.getPropertyClosureIterator();
        while (itr.hasNext()) {
            Property property = (Property)itr.next();
            if (!property.getName().equals(name)) continue;
            return true;
        }
        return false;
    }

    public boolean isPropertyDefinedInSuperHierarchy(String name) {
        return this.getSuperclass() != null && this.getSuperclass().isPropertyDefinedInHierarchy(name);
    }

    public boolean isPropertyDefinedInHierarchy(String name) {
        if (this.hasProperty(name)) {
            return true;
        }
        if (this.getSuperMappedSuperclass() != null && this.getSuperMappedSuperclass().isPropertyDefinedInHierarchy(name)) {
            return true;
        }
        return this.getSuperclass() != null && this.getSuperclass().isPropertyDefinedInHierarchy(name);
    }

    @Deprecated
    public int getOptimisticLockMode() {
        return this.getOptimisticLockStyle().getOldCode();
    }

    @Deprecated
    public void setOptimisticLockMode(int optimisticLockMode) {
        this.setOptimisticLockStyle(OptimisticLockStyle.interpretOldCode(optimisticLockMode));
    }

    public OptimisticLockStyle getOptimisticLockStyle() {
        return this.optimisticLockStyle;
    }

    public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
        this.optimisticLockStyle = optimisticLockStyle;
    }

    public void validate(Mapping mapping) throws MappingException {
        Iterator iter = this.getPropertyIterator();
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            if (prop.isValid(mapping)) continue;
            throw new MappingException("property mapping has wrong number of columns: " + StringHelper.qualify(this.getEntityName(), prop.getName()) + " type: " + prop.getType().getName());
        }
        this.checkPropertyDuplication();
        this.checkColumnDuplication();
    }

    private void checkPropertyDuplication() throws MappingException {
        HashSet<String> names = new HashSet<String>();
        Iterator iter = this.getPropertyIterator();
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            if (names.add(prop.getName())) continue;
            throw new MappingException("Duplicate property mapping of " + prop.getName() + " found in " + this.getEntityName());
        }
    }

    public boolean isDiscriminatorValueNotNull() {
        return NOT_NULL_DISCRIMINATOR_MAPPING.equals(this.getDiscriminatorValue());
    }

    public boolean isDiscriminatorValueNull() {
        return NULL_DISCRIMINATOR_MAPPING.equals(this.getDiscriminatorValue());
    }

    @Override
    public Map getMetaAttributes() {
        return this.metaAttributes;
    }

    @Override
    public void setMetaAttributes(Map metas) {
        this.metaAttributes = metas;
    }

    @Override
    public MetaAttribute getMetaAttribute(String name) {
        return this.metaAttributes == null ? null : (MetaAttribute)this.metaAttributes.get(name);
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.getEntityName() + ')';
    }

    public Iterator getJoinIterator() {
        return this.joins.iterator();
    }

    public Iterator getJoinClosureIterator() {
        return this.joins.iterator();
    }

    public void addJoin(Join join) {
        this.joins.add(join);
        join.setPersistentClass(this);
    }

    public int getJoinClosureSpan() {
        return this.joins.size();
    }

    public int getPropertyClosureSpan() {
        int span = this.properties.size();
        for (Join join : this.joins) {
            span += join.getPropertySpan();
        }
        return span;
    }

    public int getJoinNumber(Property prop) {
        int result = 1;
        Iterator iter = this.getSubclassJoinClosureIterator();
        while (iter.hasNext()) {
            Join join = (Join)iter.next();
            if (join.containsProperty(prop)) {
                return result;
            }
            ++result;
        }
        return 0;
    }

    public Iterator getPropertyIterator() {
        ArrayList iterators = new ArrayList();
        iterators.add(this.properties.iterator());
        for (int i = 0; i < this.joins.size(); ++i) {
            Join join = this.joins.get(i);
            iterators.add(join.getPropertyIterator());
        }
        return new JoinedIterator(iterators);
    }

    public Iterator getUnjoinedPropertyIterator() {
        return this.properties.iterator();
    }

    public void setCustomSqlInsert(CustomSql customSql) {
        if (customSql != null) {
            this.setCustomSQLInsert(customSql.getSql(), customSql.isCallable(), customSql.getCheckStyle());
        }
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

    public void setCustomSqlUpdate(CustomSql customSql) {
        if (customSql != null) {
            this.setCustomSQLUpdate(customSql.getSql(), customSql.isCallable(), customSql.getCheckStyle());
        }
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

    public void setCustomSqlDelete(CustomSql customSql) {
        if (customSql != null) {
            this.setCustomSQLDelete(customSql.getSql(), customSql.isCallable(), customSql.getCheckStyle());
        }
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

    @Override
    public void addFilter(String name, String condition, boolean autoAliasInjection, Map<String, String> aliasTableMap, Map<String, String> aliasEntityMap) {
        this.filters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap, this));
    }

    @Override
    public List getFilters() {
        return this.filters;
    }

    public boolean isForceDiscriminator() {
        return false;
    }

    public abstract boolean isJoinedSubclass();

    public String getLoaderName() {
        return this.loaderName;
    }

    public void setLoaderName(String loaderName) {
        this.loaderName = loaderName == null ? null : loaderName.intern();
    }

    public abstract Set getSynchronizedTables();

    public void addSynchronizedTable(String table) {
        this.synchronizedTables.add(table);
    }

    public Boolean isAbstract() {
        return this.isAbstract;
    }

    public void setAbstract(Boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    protected void checkColumnDuplication(Set distinctColumns, Iterator columns) throws MappingException {
        while (columns.hasNext()) {
            Column col;
            Selectable columnOrFormula = (Selectable)columns.next();
            if (columnOrFormula.isFormula() || distinctColumns.add((col = (Column)columnOrFormula).getName())) continue;
            throw new MappingException("Repeated column in mapping for entity: " + this.getEntityName() + " column: " + col.getName() + " (should be mapped with insert=\"false\" update=\"false\")");
        }
    }

    protected void checkPropertyColumnDuplication(Set distinctColumns, Iterator properties) throws MappingException {
        while (properties.hasNext()) {
            Property prop = (Property)properties.next();
            if (prop.getValue() instanceof Component) {
                Component component = (Component)prop.getValue();
                this.checkPropertyColumnDuplication(distinctColumns, component.getPropertyIterator());
                continue;
            }
            if (!prop.isUpdateable() && !prop.isInsertable()) continue;
            this.checkColumnDuplication(distinctColumns, prop.getColumnIterator());
        }
    }

    protected Iterator getNonDuplicatedPropertyIterator() {
        return this.getUnjoinedPropertyIterator();
    }

    protected Iterator getDiscriminatorColumnIterator() {
        return Collections.emptyIterator();
    }

    protected void checkColumnDuplication() {
        HashSet cols = new HashSet();
        if (this.getIdentifierMapper() == null) {
            this.checkColumnDuplication(cols, this.getKey().getColumnIterator());
        }
        this.checkColumnDuplication(cols, this.getDiscriminatorColumnIterator());
        this.checkPropertyColumnDuplication(cols, this.getNonDuplicatedPropertyIterator());
        Iterator iter = this.getJoinIterator();
        while (iter.hasNext()) {
            cols.clear();
            Join join = (Join)iter.next();
            this.checkColumnDuplication(cols, join.getKey().getColumnIterator());
            this.checkPropertyColumnDuplication(cols, join.getPropertyIterator());
        }
    }

    public abstract Object accept(PersistentClassVisitor var1);

    public String getJpaEntityName() {
        return this.jpaEntityName;
    }

    public void setJpaEntityName(String jpaEntityName) {
        this.jpaEntityName = jpaEntityName;
    }

    public boolean hasPojoRepresentation() {
        return this.getClassName() != null;
    }

    public boolean hasSubselectLoadableCollections() {
        return this.hasSubselectLoadableCollections;
    }

    public void setSubselectLoadableCollections(boolean hasSubselectCollections) {
        this.hasSubselectLoadableCollections = hasSubselectCollections;
    }

    public Component getIdentifierMapper() {
        return this.identifierMapper;
    }

    public Component getDeclaredIdentifierMapper() {
        return this.declaredIdentifierMapper;
    }

    public void setDeclaredIdentifierMapper(Component declaredIdentifierMapper) {
        this.declaredIdentifierMapper = declaredIdentifierMapper;
    }

    public boolean hasIdentifierMapper() {
        return this.identifierMapper != null;
    }

    public void addCallbackDefinitions(List<CallbackDefinition> callbackDefinitions) {
        if (callbackDefinitions == null || callbackDefinitions.isEmpty()) {
            return;
        }
        if (this.callbackDefinitions == null) {
            this.callbackDefinitions = new ArrayList<CallbackDefinition>();
        }
        this.callbackDefinitions.addAll(callbackDefinitions);
    }

    public List<CallbackDefinition> getCallbackDefinitions() {
        if (this.callbackDefinitions == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.callbackDefinitions);
    }

    public void setIdentifierMapper(Component handle) {
        this.identifierMapper = handle;
    }

    public void addTuplizer(EntityMode entityMode, String implClassName) {
        if (this.tuplizerImpls == null) {
            this.tuplizerImpls = new HashMap();
        }
        this.tuplizerImpls.put(entityMode, implClassName);
    }

    public String getTuplizerImplClassName(EntityMode mode) {
        if (this.tuplizerImpls == null) {
            return null;
        }
        return (String)this.tuplizerImpls.get((Object)mode);
    }

    public Map getTuplizerMap() {
        if (this.tuplizerImpls == null) {
            return null;
        }
        return Collections.unmodifiableMap(this.tuplizerImpls);
    }

    public boolean hasNaturalId() {
        Iterator props = this.getRootClass().getPropertyIterator();
        while (props.hasNext()) {
            if (!((Property)props.next()).isNaturalIdentifier()) continue;
            return true;
        }
        return false;
    }

    public Iterator getDeclaredPropertyIterator() {
        ArrayList iterators = new ArrayList();
        iterators.add(this.declaredProperties.iterator());
        for (int i = 0; i < this.joins.size(); ++i) {
            Join join = this.joins.get(i);
            iterators.add(join.getDeclaredPropertyIterator());
        }
        return new JoinedIterator(iterators);
    }

    public void addMappedsuperclassProperty(Property p) {
        this.properties.add(p);
        p.setPersistentClass(this);
    }

    public MappedSuperclass getSuperMappedSuperclass() {
        return this.superMappedSuperclass;
    }

    public void setSuperMappedSuperclass(MappedSuperclass superMappedSuperclass) {
        this.superMappedSuperclass = superMappedSuperclass;
    }
}

