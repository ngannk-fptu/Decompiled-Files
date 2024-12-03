/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.EntityEntryFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.ValueInclusion;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.persister.entity.MultiLoadOptions;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;

public interface EntityPersister
extends EntityDefinition {
    public static final String ENTITY_ID = "id";

    public void generateEntityDefinition();

    public void postInstantiate() throws MappingException;

    public SessionFactoryImplementor getFactory();

    public NavigableRole getNavigableRole();

    public EntityEntryFactory getEntityEntryFactory();

    public String getRootEntityName();

    public String getEntityName();

    public EntityMetamodel getEntityMetamodel();

    default public Object initializeEnhancedEntityUsedAsProxy(Object entity, String nameOfAttributeBeingAccessed, SharedSessionContractImplementor session) {
        throw new UnsupportedOperationException("Initialization of entity enhancement used to act like a proxy is not supported by this EntityPersister : " + this.getClass().getName());
    }

    public boolean isSubclassEntityName(String var1);

    public Serializable[] getPropertySpaces();

    public Serializable[] getQuerySpaces();

    public boolean hasProxy();

    public boolean hasCollections();

    public boolean hasMutableProperties();

    public boolean hasSubselectLoadableCollections();

    public boolean hasCascades();

    public boolean isMutable();

    public boolean isInherited();

    public boolean isIdentifierAssignedByInsert();

    public Type getPropertyType(String var1) throws MappingException;

    public int[] findDirty(Object[] var1, Object[] var2, Object var3, SharedSessionContractImplementor var4);

    public int[] findModified(Object[] var1, Object[] var2, Object var3, SharedSessionContractImplementor var4);

    public boolean hasIdentifierProperty();

    public boolean canExtractIdOutOfEntity();

    public boolean isVersioned();

    public VersionType getVersionType();

    public int getVersionProperty();

    public boolean hasNaturalIdentifier();

    public int[] getNaturalIdentifierProperties();

    public Object[] getNaturalIdentifierSnapshot(Serializable var1, SharedSessionContractImplementor var2);

    public IdentifierGenerator getIdentifierGenerator();

    public boolean hasLazyProperties();

    @Deprecated
    public Serializable loadEntityIdByNaturalId(Object[] var1, LockOptions var2, SharedSessionContractImplementor var3);

    public Object load(Serializable var1, Object var2, LockMode var3, SharedSessionContractImplementor var4) throws HibernateException;

    default public Object load(Serializable id, Object optionalObject, LockMode lockMode, SharedSessionContractImplementor session, Boolean readOnly) throws HibernateException {
        return this.load(id, optionalObject, lockMode, session);
    }

    public Object load(Serializable var1, Object var2, LockOptions var3, SharedSessionContractImplementor var4) throws HibernateException;

    default public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SharedSessionContractImplementor session, Boolean readOnly) throws HibernateException {
        return this.load(id, optionalObject, lockOptions, session);
    }

    public List multiLoad(Serializable[] var1, SharedSessionContractImplementor var2, MultiLoadOptions var3);

    public void lock(Serializable var1, Object var2, Object var3, LockMode var4, SharedSessionContractImplementor var5) throws HibernateException;

    public void lock(Serializable var1, Object var2, Object var3, LockOptions var4, SharedSessionContractImplementor var5) throws HibernateException;

    public void insert(Serializable var1, Object[] var2, Object var3, SharedSessionContractImplementor var4) throws HibernateException;

    public Serializable insert(Object[] var1, Object var2, SharedSessionContractImplementor var3) throws HibernateException;

    public void delete(Serializable var1, Object var2, Object var3, SharedSessionContractImplementor var4) throws HibernateException;

    public void update(Serializable var1, Object[] var2, int[] var3, boolean var4, Object[] var5, Object var6, Object var7, Object var8, SharedSessionContractImplementor var9) throws HibernateException;

    public Type[] getPropertyTypes();

    public String[] getPropertyNames();

    public boolean[] getPropertyInsertability();

    @Deprecated
    public ValueInclusion[] getPropertyInsertGenerationInclusions();

    @Deprecated
    public ValueInclusion[] getPropertyUpdateGenerationInclusions();

    public boolean[] getPropertyUpdateability();

    public boolean[] getPropertyCheckability();

    public boolean[] getPropertyNullability();

    public boolean[] getPropertyVersionability();

    public boolean[] getPropertyLaziness();

    public CascadeStyle[] getPropertyCascadeStyles();

    public Type getIdentifierType();

    public String getIdentifierPropertyName();

    public boolean isCacheInvalidationRequired();

    public boolean isLazyPropertiesCacheable();

    public boolean canReadFromCache();

    public boolean canWriteToCache();

    @Deprecated
    public boolean hasCache();

    public EntityDataAccess getCacheAccessStrategy();

    public CacheEntryStructure getCacheEntryStructure();

    public CacheEntry buildCacheEntry(Object var1, Object[] var2, Object var3, SharedSessionContractImplementor var4);

    public boolean hasNaturalIdCache();

    public NaturalIdDataAccess getNaturalIdCacheAccessStrategy();

    public ClassMetadata getClassMetadata();

    public boolean isBatchLoadable();

    public boolean isSelectBeforeUpdateRequired();

    public Object[] getDatabaseSnapshot(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;

    public Serializable getIdByUniqueKey(Serializable var1, String var2, SharedSessionContractImplementor var3);

    public Object getCurrentVersion(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;

    public Object forceVersionIncrement(Serializable var1, Object var2, SharedSessionContractImplementor var3) throws HibernateException;

    public boolean isInstrumented();

    public boolean hasInsertGeneratedProperties();

    public boolean hasUpdateGeneratedProperties();

    public boolean isVersionPropertyGenerated();

    public void afterInitialize(Object var1, SharedSessionContractImplementor var2);

    public void afterReassociate(Object var1, SharedSessionContractImplementor var2);

    public Object createProxy(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;

    public Boolean isTransient(Object var1, SharedSessionContractImplementor var2) throws HibernateException;

    public Object[] getPropertyValuesToInsert(Object var1, Map var2, SharedSessionContractImplementor var3) throws HibernateException;

    public void processInsertGeneratedProperties(Serializable var1, Object var2, Object[] var3, SharedSessionContractImplementor var4);

    public void processUpdateGeneratedProperties(Serializable var1, Object var2, Object[] var3, SharedSessionContractImplementor var4);

    public Class getMappedClass();

    public boolean implementsLifecycle();

    public Class getConcreteProxyClass();

    public void setPropertyValues(Object var1, Object[] var2);

    public void setPropertyValue(Object var1, int var2, Object var3);

    public Object[] getPropertyValues(Object var1);

    public Object getPropertyValue(Object var1, int var2) throws HibernateException;

    public Object getPropertyValue(Object var1, String var2);

    @Deprecated
    public Serializable getIdentifier(Object var1) throws HibernateException;

    public Serializable getIdentifier(Object var1, SharedSessionContractImplementor var2);

    public void setIdentifier(Object var1, Serializable var2, SharedSessionContractImplementor var3);

    public Object getVersion(Object var1) throws HibernateException;

    public Object instantiate(Serializable var1, SharedSessionContractImplementor var2);

    public boolean isInstance(Object var1);

    public boolean hasUninitializedLazyProperties(Object var1);

    public void resetIdentifier(Object var1, Serializable var2, Object var3, SharedSessionContractImplementor var4);

    public EntityPersister getSubclassEntityPersister(Object var1, SessionFactoryImplementor var2);

    public EntityMode getEntityMode();

    public EntityTuplizer getEntityTuplizer();

    public BytecodeEnhancementMetadata getInstrumentationMetadata();

    default public BytecodeEnhancementMetadata getBytecodeEnhancementMetadata() {
        return this.getInstrumentationMetadata();
    }

    public FilterAliasGenerator getFilterAliasGenerator(String var1);

    public int[] resolveAttributeIndexes(String[] var1);

    default public int[] resolveDirtyAttributeIndexes(Object[] values, Object[] loadedState, String[] attributeNames, SessionImplementor session) {
        return this.resolveAttributeIndexes(attributeNames);
    }

    public boolean canUseReferenceCacheEntries();

    @Deprecated
    default public boolean canIdentityInsertBeDelayed() {
        return false;
    }
}

