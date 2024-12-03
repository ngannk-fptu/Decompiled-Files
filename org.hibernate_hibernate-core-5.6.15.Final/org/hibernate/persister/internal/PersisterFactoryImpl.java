/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.spi.PersisterClassResolver;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.persister.spi.PersisterFactory;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public final class PersisterFactoryImpl
implements PersisterFactory,
ServiceRegistryAwareService {
    public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS = new Class[]{PersistentClass.class, EntityDataAccess.class, NaturalIdDataAccess.class, PersisterCreationContext.class};
    public static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[]{Collection.class, CollectionDataAccess.class, PersisterCreationContext.class};
    private ServiceRegistryImplementor serviceRegistry;

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public EntityPersister createEntityPersister(PersistentClass entityBinding, EntityDataAccess entityCacheAccessStrategy, NaturalIdDataAccess naturalIdCacheAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
        Class<? extends EntityPersister> persisterClass = entityBinding.getEntityPersisterClass();
        if (persisterClass == null) {
            persisterClass = this.serviceRegistry.getService(PersisterClassResolver.class).getEntityPersisterClass(entityBinding);
        }
        return this.createEntityPersister(persisterClass, entityBinding, entityCacheAccessStrategy, naturalIdCacheAccessStrategy, creationContext);
    }

    private EntityPersister createEntityPersister(Class<? extends EntityPersister> persisterClass, PersistentClass entityBinding, EntityDataAccess entityCacheAccessStrategy, NaturalIdDataAccess naturalIdCacheAccessStrategy, PersisterCreationContext creationContext) {
        try {
            Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor(ENTITY_PERSISTER_CONSTRUCTOR_ARGS);
            try {
                return constructor.newInstance(entityBinding, entityCacheAccessStrategy, naturalIdCacheAccessStrategy, creationContext);
            }
            catch (MappingException e) {
                throw e;
            }
            catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();
                if (target instanceof HibernateException) {
                    throw (HibernateException)((Object)target);
                }
                throw new MappingException("Could not instantiate persister " + persisterClass.getName(), target);
            }
            catch (Exception e) {
                throw new MappingException("Could not instantiate persister " + persisterClass.getName(), e);
            }
        }
        catch (MappingException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MappingException("Could not get constructor for " + persisterClass.getName(), e);
        }
    }

    @Override
    public CollectionPersister createCollectionPersister(Collection collectionBinding, CollectionDataAccess cacheAccessStrategy, PersisterCreationContext creationContext) throws HibernateException {
        Class<? extends CollectionPersister> persisterClass = collectionBinding.getCollectionPersisterClass();
        if (persisterClass == null) {
            persisterClass = this.serviceRegistry.getService(PersisterClassResolver.class).getCollectionPersisterClass(collectionBinding);
        }
        return this.createCollectionPersister(persisterClass, collectionBinding, cacheAccessStrategy, creationContext);
    }

    private CollectionPersister createCollectionPersister(Class<? extends CollectionPersister> persisterClass, Collection collectionBinding, CollectionDataAccess cacheAccessStrategy, PersisterCreationContext creationContext) {
        try {
            Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor(COLLECTION_PERSISTER_CONSTRUCTOR_ARGS);
            try {
                return constructor.newInstance(collectionBinding, cacheAccessStrategy, creationContext);
            }
            catch (MappingException e) {
                throw e;
            }
            catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();
                if (target instanceof HibernateException) {
                    throw (HibernateException)((Object)target);
                }
                throw new MappingException("Could not instantiate collection persister " + persisterClass.getName(), target);
            }
            catch (Exception e) {
                throw new MappingException("Could not instantiate collection persister " + persisterClass.getName(), e);
            }
        }
        catch (MappingException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MappingException("Could not get constructor for " + persisterClass.getName(), e);
        }
    }
}

