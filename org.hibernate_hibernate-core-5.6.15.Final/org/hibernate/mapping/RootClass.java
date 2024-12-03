/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.SingletonIterator;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.TableOwner;
import org.hibernate.mapping.Value;

public class RootClass
extends PersistentClass
implements TableOwner {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(RootClass.class);
    public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
    public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
    private Property identifierProperty;
    private KeyValue identifier;
    private Property version;
    private boolean polymorphic;
    private String cacheConcurrencyStrategy;
    private String cacheRegionName;
    private boolean lazyPropertiesCacheable = true;
    private String naturalIdCacheRegionName;
    private Value discriminator;
    private boolean mutable = true;
    private boolean embeddedIdentifier;
    private boolean explicitPolymorphism;
    private Class entityPersisterClass;
    private boolean forceDiscriminator;
    private String where;
    private Table table;
    private boolean discriminatorInsertable = true;
    private int nextSubclassId;
    private Property declaredIdentifierProperty;
    private Property declaredVersion;

    public RootClass(MetadataBuildingContext metadataBuildingContext) {
        super(metadataBuildingContext);
    }

    @Override
    int nextSubclassId() {
        return ++this.nextSubclassId;
    }

    @Override
    public int getSubclassId() {
        return 0;
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public Table getTable() {
        return this.table;
    }

    @Override
    public Property getIdentifierProperty() {
        return this.identifierProperty;
    }

    @Override
    public Property getDeclaredIdentifierProperty() {
        return this.declaredIdentifierProperty;
    }

    public void setDeclaredIdentifierProperty(Property declaredIdentifierProperty) {
        this.declaredIdentifierProperty = declaredIdentifierProperty;
    }

    @Override
    public KeyValue getIdentifier() {
        return this.identifier;
    }

    @Override
    public boolean hasIdentifierProperty() {
        return this.identifierProperty != null;
    }

    @Override
    public Value getDiscriminator() {
        return this.discriminator;
    }

    @Override
    public boolean isInherited() {
        return false;
    }

    @Override
    public boolean isPolymorphic() {
        return this.polymorphic;
    }

    public void setPolymorphic(boolean polymorphic) {
        this.polymorphic = polymorphic;
    }

    @Override
    public RootClass getRootClass() {
        return this;
    }

    @Override
    public Iterator getPropertyClosureIterator() {
        return this.getPropertyIterator();
    }

    @Override
    public Iterator getTableClosureIterator() {
        return new SingletonIterator<Table>(this.getTable());
    }

    @Override
    public Iterator getKeyClosureIterator() {
        return new SingletonIterator<KeyValue>(this.getKey());
    }

    @Override
    public void addSubclass(Subclass subclass) throws MappingException {
        super.addSubclass(subclass);
        this.setPolymorphic(true);
    }

    @Override
    public boolean isExplicitPolymorphism() {
        return this.explicitPolymorphism;
    }

    @Override
    public Property getVersion() {
        return this.version;
    }

    @Override
    public Property getDeclaredVersion() {
        return this.declaredVersion;
    }

    public void setDeclaredVersion(Property declaredVersion) {
        this.declaredVersion = declaredVersion;
    }

    public void setVersion(Property version) {
        this.version = version;
    }

    @Override
    public boolean isVersioned() {
        return this.version != null;
    }

    @Override
    public boolean isMutable() {
        return this.mutable;
    }

    @Override
    public boolean hasEmbeddedIdentifier() {
        return this.embeddedIdentifier;
    }

    @Override
    public Class getEntityPersisterClass() {
        return this.entityPersisterClass;
    }

    @Override
    public Table getRootTable() {
        return this.getTable();
    }

    @Override
    public void setEntityPersisterClass(Class persister) {
        this.entityPersisterClass = persister;
    }

    @Override
    public PersistentClass getSuperclass() {
        return null;
    }

    @Override
    public KeyValue getKey() {
        return this.getIdentifier();
    }

    public void setDiscriminator(Value discriminator) {
        this.discriminator = discriminator;
    }

    public void setEmbeddedIdentifier(boolean embeddedIdentifier) {
        this.embeddedIdentifier = embeddedIdentifier;
    }

    public void setExplicitPolymorphism(boolean explicitPolymorphism) {
        this.explicitPolymorphism = explicitPolymorphism;
    }

    public void setIdentifier(KeyValue identifier) {
        this.identifier = identifier;
    }

    public void setIdentifierProperty(Property identifierProperty) {
        this.identifierProperty = identifierProperty;
        identifierProperty.setPersistentClass(this);
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    @Override
    public boolean isDiscriminatorInsertable() {
        return this.discriminatorInsertable;
    }

    public void setDiscriminatorInsertable(boolean insertable) {
        this.discriminatorInsertable = insertable;
    }

    @Override
    public boolean isForceDiscriminator() {
        return this.forceDiscriminator;
    }

    public void setForceDiscriminator(boolean forceDiscriminator) {
        this.forceDiscriminator = forceDiscriminator;
    }

    @Override
    public String getWhere() {
        return this.where;
    }

    public void setWhere(String string) {
        this.where = string;
    }

    @Override
    public void validate(Mapping mapping) throws MappingException {
        super.validate(mapping);
        if (!this.getIdentifier().isValid(mapping)) {
            throw new MappingException("identifier mapping has wrong number of columns: " + this.getEntityName() + " type: " + this.getIdentifier().getType().getName());
        }
        this.checkCompositeIdentifier();
    }

    private void checkCompositeIdentifier() {
        Class idClass;
        Component id;
        if (this.getIdentifier() instanceof Component && !(id = (Component)this.getIdentifier()).isDynamic() && (idClass = id.getComponentClass()) != null) {
            String idComponentClassName = idClass.getName();
            if (!ReflectHelper.overridesEquals(idClass)) {
                LOG.compositeIdClassDoesNotOverrideEquals(idComponentClassName);
            }
            if (!ReflectHelper.overridesHashCode(idClass)) {
                LOG.compositeIdClassDoesNotOverrideHashCode(idComponentClassName);
            }
            if (!Serializable.class.isAssignableFrom(idClass)) {
                throw new MappingException("Composite-id class must implement Serializable: " + idComponentClassName);
            }
        }
    }

    @Override
    public String getCacheConcurrencyStrategy() {
        return this.cacheConcurrencyStrategy;
    }

    public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
        this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
    }

    public String getCacheRegionName() {
        return this.cacheRegionName == null ? this.getEntityName() : this.cacheRegionName;
    }

    public void setCacheRegionName(String cacheRegionName) {
        this.cacheRegionName = StringHelper.nullIfEmpty(cacheRegionName);
    }

    public boolean isLazyPropertiesCacheable() {
        return this.lazyPropertiesCacheable;
    }

    public void setLazyPropertiesCacheable(boolean lazyPropertiesCacheable) {
        this.lazyPropertiesCacheable = lazyPropertiesCacheable;
    }

    @Override
    public String getNaturalIdCacheRegionName() {
        return this.naturalIdCacheRegionName;
    }

    public void setNaturalIdCacheRegionName(String naturalIdCacheRegionName) {
        this.naturalIdCacheRegionName = naturalIdCacheRegionName;
    }

    @Override
    public boolean isJoinedSubclass() {
        return false;
    }

    @Override
    public Set getSynchronizedTables() {
        return this.synchronizedTables;
    }

    public Set<Table> getIdentityTables() {
        HashSet<Table> tables = new HashSet<Table>();
        Iterator iter = this.getSubclassClosureIterator();
        while (iter.hasNext()) {
            PersistentClass clazz = (PersistentClass)iter.next();
            if (clazz.isAbstract() != null && clazz.isAbstract().booleanValue()) continue;
            tables.add(clazz.getIdentityTable());
        }
        return tables;
    }

    @Override
    public Object accept(PersistentClassVisitor mv) {
        return mv.accept(this);
    }
}

