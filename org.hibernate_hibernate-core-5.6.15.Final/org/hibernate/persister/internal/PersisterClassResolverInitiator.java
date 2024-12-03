/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.internal;

import java.util.Map;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.persister.internal.StandardPersisterClassResolver;
import org.hibernate.persister.spi.PersisterClassResolver;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class PersisterClassResolverInitiator
implements StandardServiceInitiator<PersisterClassResolver> {
    public static final PersisterClassResolverInitiator INSTANCE = new PersisterClassResolverInitiator();
    public static final String IMPL_NAME = "hibernate.persister.resolver";

    @Override
    public Class<PersisterClassResolver> getServiceInitiated() {
        return PersisterClassResolver.class;
    }

    @Override
    public PersisterClassResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Object customImpl = configurationValues.get(IMPL_NAME);
        if (customImpl == null) {
            return new StandardPersisterClassResolver();
        }
        if (PersisterClassResolver.class.isInstance(customImpl)) {
            return (PersisterClassResolver)customImpl;
        }
        Class<? extends PersisterClassResolver> customImplClass = Class.class.isInstance(customImpl) ? (Class<? extends PersisterClassResolver>)customImpl : this.locate(registry, customImpl.toString());
        try {
            return customImplClass.newInstance();
        }
        catch (Exception e) {
            throw new ServiceException("Could not initialize custom PersisterClassResolver impl [" + customImplClass.getName() + "]", e);
        }
    }

    private Class<? extends PersisterClassResolver> locate(ServiceRegistryImplementor registry, String className) {
        return registry.getService(ClassLoaderService.class).classForName(className);
    }
}

