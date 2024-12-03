/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.persister.internal.PersisterFactoryImpl;
import org.hibernate.persister.spi.PersisterFactory;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class PersisterFactoryInitiator
implements StandardServiceInitiator<PersisterFactory> {
    public static final PersisterFactoryInitiator INSTANCE = new PersisterFactoryInitiator();
    public static final String IMPL_NAME = "hibernate.persister.factory";

    @Override
    public Class<PersisterFactory> getServiceInitiated() {
        return PersisterFactory.class;
    }

    @Override
    public PersisterFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object customImpl = configurationValues.get(IMPL_NAME);
        if (customImpl == null) {
            return new PersisterFactoryImpl();
        }
        if (PersisterFactory.class.isInstance(customImpl)) {
            return (PersisterFactory)customImpl;
        }
        Class<? extends PersisterFactory> customImplClass = Class.class.isInstance(customImpl) ? (Class<? extends PersisterFactory>)customImpl : this.locate(registry, customImpl.toString());
        try {
            return customImplClass.newInstance();
        }
        catch (Exception e) {
            throw new ServiceException("Could not initialize custom PersisterFactory impl [" + customImplClass.getName() + "]", e);
        }
    }

    private Class<? extends PersisterFactory> locate(ServiceRegistryImplementor registry, String className) {
        return registry.getService(ClassLoaderService.class).classForName(className);
    }
}

