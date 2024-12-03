/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 */
package com.terracotta.entity;

import com.terracotta.entity.ClusteredEntityState;
import com.terracotta.entity.EntityLockHandler;
import com.terracotta.entity.RootEntity;
import com.terracotta.entity.internal.InternalRootEntity;
import com.terracotta.entity.internal.LockingEntity;
import com.terracotta.entity.internal.ToolkitAwareEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

public class ClusteredEntityManager {
    private static final long TRY_LOCK_TIMEOUT_SECONDS = 2L;
    private final Toolkit toolkit;
    private final EntityLockHandler entityLockHandler;
    private volatile transient ConcurrentMap<Class, ToolkitMap<String, ? extends RootEntity>> entityMapsMap = new ConcurrentHashMap<Class, ToolkitMap<String, ? extends RootEntity>>();

    public ClusteredEntityManager(Toolkit toolkit) {
        this(toolkit, new EntityLockHandler(toolkit));
    }

    ClusteredEntityManager(Toolkit toolkit, EntityLockHandler entityLockHandler) {
        this.toolkit = toolkit;
        this.entityLockHandler = entityLockHandler;
    }

    public <T extends RootEntity> T getRootEntity(String name, Class<T> entityClass) {
        T entity = this.getRootEntityInternal(name, entityClass);
        if (entity != null && ClusteredEntityState.DESTROY_IN_PROGRESS.equals((Object)entity.getState())) {
            this.destroyRootEntitySilently(name, entityClass, entity);
            return null;
        }
        return entity;
    }

    public <T extends RootEntity> Map<String, T> getRootEntities(Class<T> entityClass) {
        HashMap resultMap = new HashMap();
        for (Map.Entry entry : this.getEntityMap(entityClass).entrySet()) {
            RootEntity entity = (RootEntity)entry.getValue();
            if (!ClusteredEntityState.DESTROY_IN_PROGRESS.equals((Object)entity.getState())) {
                resultMap.put(entry.getKey(), this.processEntity(entity));
                continue;
            }
            this.destroyRootEntitySilently((String)entry.getKey(), entityClass, entity);
        }
        return Collections.unmodifiableMap(resultMap);
    }

    public <T extends RootEntity> T addRootEntityIfAbsent(String name, Class<T> clusteredEntityClass, T clusteredEntity) {
        ToolkitMap<String, T> map = this.getEntityMap(clusteredEntityClass);
        RootEntity oldValue = (RootEntity)map.putIfAbsent((Object)name, clusteredEntity);
        if (oldValue != null) {
            return (T)this.processEntity(oldValue);
        }
        this.processEntity(clusteredEntity);
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public <T extends RootEntity> boolean destroyRootEntity(String name, Class<T> rootEntityClass, T controlEntity) {
        boolean e2;
        InternalRootEntity currentRootEntity = this.asInternalRootEntity(this.getRootEntityInternal(name, rootEntityClass));
        if (currentRootEntity == null) return false;
        ToolkitLock entityWriteLock = currentRootEntity.getEntityLock().writeLock();
        if (!entityWriteLock.tryLock(2L, TimeUnit.SECONDS)) throw new IllegalStateException(String.format("Unable to lock entity %s of type %s for destruction", name, rootEntityClass));
        try {
            if (!currentRootEntity.equals(controlEntity)) {
                throw new IllegalArgumentException(String.format("The specified entity named %s does not match the mapping known to this entity manager", name));
            }
            currentRootEntity.markDestroying();
            try {
                this.getEntityMap(rootEntityClass).put((Object)name, (Object)((RootEntity)((Object)currentRootEntity)));
            }
            catch (Exception e2) {
                currentRootEntity.alive();
                throw new UnsupportedOperationException(String.format("Unable to mark entity %s of type %s with destroy in progress", name, rootEntityClass), e2);
            }
            currentRootEntity.destroy();
            this.getEntityMap(rootEntityClass).remove((Object)name);
            e2 = true;
        }
        catch (Throwable throwable) {
            try {
                entityWriteLock.unlock();
                throw throwable;
            }
            catch (InterruptedException e3) {
                throw new IllegalStateException(String.format("Unable to lock entity %s of type %s for destruction", name, rootEntityClass), e3);
            }
        }
        entityWriteLock.unlock();
        return e2;
    }

    private <T extends RootEntity> InternalRootEntity asInternalRootEntity(T currentRootEntity) {
        return (InternalRootEntity)((Object)currentRootEntity);
    }

    public ToolkitReadWriteLock getEntityLock(String lockName) {
        return this.toolkit.getReadWriteLock(lockName);
    }

    public void dispose() {
        this.entityLockHandler.dispose();
    }

    private <T extends RootEntity> T getRootEntityInternal(String name, Class<T> rootEntityClass) {
        return (T)this.processEntity((RootEntity)this.getEntityMap(rootEntityClass).get((Object)name));
    }

    private <T extends RootEntity> void destroyRootEntitySilently(String name, Class<T> entityClass, T entity) {
        try {
            this.destroyRootEntity(name, entityClass, entity);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private <T extends RootEntity> T processEntity(T entity) {
        if (entity instanceof ToolkitAwareEntity) {
            ((ToolkitAwareEntity)((Object)entity)).setToolkit(this.toolkit);
        }
        if (entity instanceof LockingEntity) {
            ((LockingEntity)((Object)entity)).setEntityLockHandler(this.entityLockHandler);
        }
        return entity;
    }

    private <T extends RootEntity> ToolkitMap<String, T> getEntityMap(Class<T> entityClass) {
        ToolkitMap<String, ? extends RootEntity> installedMap;
        ToolkitMap<String, ? extends RootEntity> entityMap = (ToolkitMap<String, ? extends RootEntity>)this.entityMapsMap.get(entityClass);
        if (entityMap == null && (installedMap = this.entityMapsMap.putIfAbsent(entityClass, entityMap = this.toolkit.getMap(this.getMapName(entityClass), String.class, entityClass))) != null) {
            entityMap = installedMap;
        }
        return entityMap;
    }

    <T extends RootEntity> String getMapName(Class<T> entityClass) {
        return entityClass.getName();
    }
}

