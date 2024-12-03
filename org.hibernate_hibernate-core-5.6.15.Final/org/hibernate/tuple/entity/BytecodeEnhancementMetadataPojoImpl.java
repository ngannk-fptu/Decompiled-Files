/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import java.io.Serializable;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributesMetadata;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.bytecode.spi.NotInstrumentedException;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.CompositeType;

public final class BytecodeEnhancementMetadataPojoImpl
implements BytecodeEnhancementMetadata {
    private final String entityName;
    private final Class entityClass;
    private final Set<String> identifierAttributeNames;
    private final CompositeType nonAggregatedCidMapper;
    private final boolean enhancedForLazyLoading;
    private final LazyAttributesMetadata lazyAttributesMetadata;

    public static BytecodeEnhancementMetadata from(PersistentClass persistentClass, Set<String> identifierAttributeNames, CompositeType nonAggregatedCidMapper, boolean collectionsInDefaultFetchGroupEnabled, PersisterCreationContext creationContext) {
        Class mappedClass = persistentClass.getMappedClass();
        boolean enhancedForLazyLoading = PersistentAttributeInterceptable.class.isAssignableFrom(mappedClass);
        LazyAttributesMetadata lazyAttributesMetadata = enhancedForLazyLoading ? LazyAttributesMetadata.from(persistentClass, true, collectionsInDefaultFetchGroupEnabled, creationContext) : LazyAttributesMetadata.nonEnhanced(persistentClass.getEntityName());
        return new BytecodeEnhancementMetadataPojoImpl(persistentClass.getEntityName(), mappedClass, identifierAttributeNames, nonAggregatedCidMapper, enhancedForLazyLoading, lazyAttributesMetadata);
    }

    protected BytecodeEnhancementMetadataPojoImpl(String entityName, Class entityClass, Set<String> identifierAttributeNames, CompositeType nonAggregatedCidMapper, boolean enhancedForLazyLoading, LazyAttributesMetadata lazyAttributesMetadata) {
        this.nonAggregatedCidMapper = nonAggregatedCidMapper;
        assert (identifierAttributeNames != null);
        assert (!identifierAttributeNames.isEmpty());
        this.entityName = entityName;
        this.entityClass = entityClass;
        this.identifierAttributeNames = identifierAttributeNames;
        this.enhancedForLazyLoading = enhancedForLazyLoading;
        this.lazyAttributesMetadata = lazyAttributesMetadata;
    }

    @Override
    public String getEntityName() {
        return this.entityName;
    }

    @Override
    public boolean isEnhancedForLazyLoading() {
        return this.enhancedForLazyLoading;
    }

    @Override
    public LazyAttributesMetadata getLazyAttributesMetadata() {
        return this.lazyAttributesMetadata;
    }

    @Override
    public boolean hasUnFetchedAttributes(Object entity) {
        if (!this.enhancedForLazyLoading) {
            return false;
        }
        BytecodeLazyAttributeInterceptor interceptor = this.extractLazyInterceptor(entity);
        if (interceptor instanceof LazyAttributeLoadingInterceptor) {
            return ((LazyAttributeLoadingInterceptor)interceptor).hasAnyUninitializedAttributes();
        }
        return interceptor instanceof EnhancementAsProxyLazinessInterceptor;
    }

    @Override
    public boolean isAttributeLoaded(Object entity, String attributeName) {
        if (!this.enhancedForLazyLoading) {
            return true;
        }
        BytecodeLazyAttributeInterceptor interceptor = this.extractLazyInterceptor(entity);
        if (interceptor instanceof LazyAttributeLoadingInterceptor) {
            return ((LazyAttributeLoadingInterceptor)interceptor).isAttributeLoaded(attributeName);
        }
        return true;
    }

    @Override
    public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
        return (LazyAttributeLoadingInterceptor)this.extractLazyInterceptor(entity);
    }

    @Override
    public PersistentAttributeInterceptable createEnhancedProxy(EntityKey entityKey, boolean addEmptyEntry, SharedSessionContractImplementor session) {
        EntityPersister persister = entityKey.getPersister();
        Serializable identifier = entityKey.getIdentifier();
        PersistenceContext persistenceContext = session.getPersistenceContext();
        EntityTuplizer entityTuplizer = persister.getEntityTuplizer();
        PersistentAttributeInterceptable entity = (PersistentAttributeInterceptable)entityTuplizer.instantiate(identifier, session);
        ManagedTypeHelper.processIfSelfDirtinessTracker(entity, SelfDirtinessTracker::$$_hibernate_clearDirtyAttributes);
        persistenceContext.addEnhancedProxy(entityKey, entity);
        if (addEmptyEntry) {
            persistenceContext.addEntry(entity, Status.MANAGED, null, null, identifier, null, LockMode.NONE, true, persister, true);
        }
        persister.getEntityMetamodel().getBytecodeEnhancementMetadata().injectEnhancedEntityAsProxyInterceptor(entity, entityKey, session);
        return entity;
    }

    @Override
    public LazyAttributeLoadingInterceptor injectInterceptor(Object entity, Object identifier, SharedSessionContractImplementor session) {
        if (!this.enhancedForLazyLoading) {
            throw new NotInstrumentedException("Entity class [" + this.entityClass.getName() + "] is not enhanced for lazy loading");
        }
        if (!this.entityClass.isInstance(entity)) {
            throw new IllegalArgumentException(String.format("Passed entity instance [%s] is not of expected type [%s]", entity, this.getEntityName()));
        }
        LazyAttributeLoadingInterceptor interceptor = new LazyAttributeLoadingInterceptor(this.getEntityName(), identifier, this.lazyAttributesMetadata.getLazyAttributeNames(), session);
        this.injectInterceptor(entity, interceptor, session);
        return interceptor;
    }

    @Override
    public void injectEnhancedEntityAsProxyInterceptor(Object entity, EntityKey entityKey, SharedSessionContractImplementor session) {
        this.injectInterceptor(entity, new EnhancementAsProxyLazinessInterceptor(this.entityName, this.identifierAttributeNames, this.nonAggregatedCidMapper, entityKey, session), session);
    }

    @Override
    public void injectInterceptor(Object entity, PersistentAttributeInterceptor interceptor, SharedSessionContractImplementor session) {
        if (!this.enhancedForLazyLoading) {
            throw new NotInstrumentedException("Entity class [" + this.entityClass.getName() + "] is not enhanced for lazy loading");
        }
        if (!this.entityClass.isInstance(entity)) {
            throw new IllegalArgumentException(String.format("Passed entity instance [%s] is not of expected type [%s]", entity, this.getEntityName()));
        }
        ((PersistentAttributeInterceptable)entity).$$_hibernate_setInterceptor(interceptor);
    }

    @Override
    public BytecodeLazyAttributeInterceptor extractLazyInterceptor(Object entity) throws NotInstrumentedException {
        if (!this.enhancedForLazyLoading) {
            throw new NotInstrumentedException("Entity class [" + this.entityClass.getName() + "] is not enhanced for lazy loading");
        }
        if (!this.entityClass.isInstance(entity)) {
            throw new IllegalArgumentException(String.format("Passed entity instance [%s] is not of expected type [%s]", entity, this.getEntityName()));
        }
        PersistentAttributeInterceptor interceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(entity).$$_hibernate_getInterceptor();
        if (interceptor == null) {
            return null;
        }
        return (BytecodeLazyAttributeInterceptor)interceptor;
    }
}

