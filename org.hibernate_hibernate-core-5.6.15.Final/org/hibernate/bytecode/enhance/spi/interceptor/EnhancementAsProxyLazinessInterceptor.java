/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.bytecode.BytecodeLogging;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.bytecode.enhance.spi.interceptor.AbstractLazyLoadInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementHelper;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class EnhancementAsProxyLazinessInterceptor
extends AbstractLazyLoadInterceptor {
    private final Set<String> identifierAttributeNames;
    private final CompositeType nonAggregatedCidMapper;
    private final EntityKey entityKey;
    private final boolean inLineDirtyChecking;
    private Set<String> writtenFieldNames;
    private Set<String> collectionAttributeNames;
    private Status status;
    private final boolean initializeBeforeWrite;

    public EnhancementAsProxyLazinessInterceptor(String entityName, Set<String> identifierAttributeNames, CompositeType nonAggregatedCidMapper, EntityKey entityKey, SharedSessionContractImplementor session) {
        super(entityName, session);
        this.identifierAttributeNames = identifierAttributeNames;
        assert (identifierAttributeNames != null);
        this.nonAggregatedCidMapper = nonAggregatedCidMapper;
        assert (nonAggregatedCidMapper != null || identifierAttributeNames.size() == 1);
        this.entityKey = entityKey;
        EntityPersister entityPersister = session.getFactory().getMetamodel().entityPersister(entityName);
        if (entityPersister.hasCollections()) {
            Type[] propertyTypes = entityPersister.getPropertyTypes();
            this.collectionAttributeNames = new HashSet<String>();
            for (int i = 0; i < propertyTypes.length; ++i) {
                Type propertyType = propertyTypes[i];
                if (!propertyType.isCollectionType()) continue;
                this.collectionAttributeNames.add(entityPersister.getPropertyNames()[i]);
            }
        }
        this.inLineDirtyChecking = entityPersister.getEntityMode() == EntityMode.POJO && SelfDirtinessTracker.class.isAssignableFrom(entityPersister.getMappedClass());
        this.initializeBeforeWrite = !this.inLineDirtyChecking || !entityPersister.getEntityMetamodel().isDynamicUpdate() || entityPersister.isVersioned();
        this.status = Status.UNINITIALIZED;
    }

    public EntityKey getEntityKey() {
        return this.entityKey;
    }

    @Override
    protected Object handleRead(Object target, String attributeName, Object value) {
        if (this.isInitialized()) {
            throw new IllegalStateException("EnhancementAsProxyLazinessInterceptor interception on an initialized instance");
        }
        if (this.identifierAttributeNames.contains(attributeName)) {
            return this.extractIdValue(target, attributeName);
        }
        return EnhancementHelper.performWork(this, (session, isTempSession) -> {
            Object[] writtenValues;
            EntityPersister entityPersister = session.getFactory().getMetamodel().entityPersister(this.getEntityName());
            EntityTuplizer entityTuplizer = entityPersister.getEntityTuplizer();
            if (this.writtenFieldNames != null && !this.writtenFieldNames.isEmpty()) {
                if (this.writtenFieldNames.contains(attributeName)) {
                    return entityTuplizer.getPropertyValue(target, attributeName);
                }
                writtenValues = new Object[this.writtenFieldNames.size()];
                int index = 0;
                for (String writtenFieldName : this.writtenFieldNames) {
                    writtenValues[index] = entityTuplizer.getPropertyValue(target, writtenFieldName);
                    ++index;
                }
            } else {
                writtenValues = null;
            }
            Object initializedValue = this.forceInitialize(target, attributeName, (SharedSessionContractImplementor)session, (boolean)isTempSession);
            this.setInitialized();
            if (writtenValues != null) {
                int index = 0;
                for (String writtenFieldName : this.writtenFieldNames) {
                    entityTuplizer.setPropertyValue(target, writtenFieldName, writtenValues[index++]);
                }
                this.writtenFieldNames.clear();
            }
            return initializedValue;
        }, this.getEntityName(), attributeName);
    }

    private Object extractIdValue(Object target, String attributeName) {
        if (this.nonAggregatedCidMapper == null) {
            return this.getIdentifier();
        }
        return this.nonAggregatedCidMapper.getPropertyValue(target, this.nonAggregatedCidMapper.getPropertyIndex(attributeName), this.getLinkedSession());
    }

    public Object forceInitialize(Object target, String attributeName) {
        BytecodeLogging.LOGGER.tracef("EnhancementAsProxyLazinessInterceptor#forceInitialize : %s#%s -> %s )", (Object)this.entityKey.getEntityName(), (Object)this.entityKey.getIdentifier(), (Object)attributeName);
        return EnhancementHelper.performWork(this, (session, isTemporarySession) -> this.forceInitialize(target, attributeName, (SharedSessionContractImplementor)session, (boolean)isTemporarySession), this.getEntityName(), attributeName);
    }

    public Object forceInitialize(Object target, String attributeName, SharedSessionContractImplementor session, boolean isTemporarySession) {
        BytecodeLogging.LOGGER.tracef("EnhancementAsProxyLazinessInterceptor#forceInitialize : %s#%s -> %s )", (Object)this.entityKey.getEntityName(), (Object)this.entityKey.getIdentifier(), (Object)attributeName);
        EntityPersister persister = session.getFactory().getMetamodel().entityPersister(this.getEntityName());
        if (isTemporarySession) {
            session.getPersistenceContextInternal().addEntity(target, org.hibernate.engine.spi.Status.READ_ONLY, ArrayHelper.filledArray(LazyPropertyInitializer.UNFETCHED_PROPERTY, Object.class, persister.getPropertyTypes().length), this.entityKey, persister.getVersion(target), LockMode.NONE, true, persister, true);
        }
        return persister.initializeEnhancedEntityUsedAsProxy(target, attributeName, session);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object handleWrite(Object target, String attributeName, Object oldValue, Object newValue) {
        if (this.isInitialized()) {
            throw new IllegalStateException("EnhancementAsProxyLazinessInterceptor interception on an initialized instance");
        }
        if (this.identifierAttributeNames.contains(attributeName)) {
            boolean changed;
            if (this.nonAggregatedCidMapper == null) {
                changed = !this.entityKey.getPersister().getIdentifierType().isEqual(oldValue, newValue);
            } else {
                int subAttrIndex = this.nonAggregatedCidMapper.getPropertyIndex(attributeName);
                Type subAttrType = this.nonAggregatedCidMapper.getSubtypes()[subAttrIndex];
                boolean bl = changed = !subAttrType.isEqual(oldValue, newValue);
            }
            if (changed) {
                throw new HibernateException("identifier of an instance of " + this.entityKey.getEntityName() + " was altered from " + oldValue + " to " + newValue);
            }
            return newValue;
        }
        if (this.initializeBeforeWrite || this.collectionAttributeNames != null && this.collectionAttributeNames.contains(attributeName)) {
            try {
                this.forceInitialize(target, attributeName);
            }
            finally {
                this.setInitialized();
            }
            if (this.inLineDirtyChecking) {
                ((SelfDirtinessTracker)target).$$_hibernate_trackChange(attributeName);
            }
        } else {
            if (this.writtenFieldNames == null) {
                this.writtenFieldNames = new HashSet<String>();
            }
            this.writtenFieldNames.add(attributeName);
            ((SelfDirtinessTracker)target).$$_hibernate_trackChange(attributeName);
        }
        return newValue;
    }

    @Override
    public Set<String> getInitializedLazyAttributeNames() {
        return Collections.emptySet();
    }

    @Override
    public void attributeInitialized(String name) {
        if (this.status == Status.INITIALIZED) {
            throw new UnsupportedOperationException("Expected call to EnhancementAsProxyLazinessInterceptor#attributeInitialized");
        }
    }

    @Override
    public boolean isAttributeLoaded(String fieldName) {
        if (this.isInitialized()) {
            throw new UnsupportedOperationException("Call to EnhancementAsProxyLazinessInterceptor#isAttributeLoaded on an interceptor which is marked as initialized");
        }
        return this.identifierAttributeNames.contains(fieldName);
    }

    @Override
    public boolean hasAnyUninitializedAttributes() {
        if (this.isInitialized()) {
            throw new UnsupportedOperationException("Call to EnhancementAsProxyLazinessInterceptor#hasAnyUninitializedAttributes on an interceptor which is marked as initialized");
        }
        return true;
    }

    @Override
    public Object getIdentifier() {
        return this.entityKey.getIdentifier();
    }

    public boolean isInitializing() {
        return this.status == Status.INITIALIZING;
    }

    public void setInitializing() {
        this.status = Status.INITIALIZING;
    }

    public boolean isInitialized() {
        return this.status == Status.INITIALIZED;
    }

    private void setInitialized() {
        this.status = Status.INITIALIZED;
    }

    public boolean hasWrittenFieldNames() {
        return this.writtenFieldNames != null && this.writtenFieldNames.size() != 0;
    }

    private static enum Status {
        UNINITIALIZED,
        INITIALIZING,
        INITIALIZED;

    }
}

