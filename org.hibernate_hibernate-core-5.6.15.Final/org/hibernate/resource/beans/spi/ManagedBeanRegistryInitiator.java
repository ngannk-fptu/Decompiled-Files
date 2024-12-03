/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.spi;

import java.util.Map;
import org.hibernate.InstantiationException;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.resource.beans.container.internal.CdiBeanContainerBuilder;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.internal.BeansMessageLogger;
import org.hibernate.resource.beans.internal.ManagedBeanRegistryImpl;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class ManagedBeanRegistryInitiator
implements StandardServiceInitiator<ManagedBeanRegistry> {
    public static final ManagedBeanRegistryInitiator INSTANCE = new ManagedBeanRegistryInitiator();

    @Override
    public Class<ManagedBeanRegistry> getServiceInitiated() {
        return ManagedBeanRegistry.class;
    }

    @Override
    public ManagedBeanRegistry initiateService(Map configurationValues, ServiceRegistryImplementor serviceRegistry) {
        return new ManagedBeanRegistryImpl(this.resolveBeanContainer(configurationValues, serviceRegistry));
    }

    private BeanContainer resolveBeanContainer(Map configurationValues, ServiceRegistryImplementor serviceRegistry) {
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        ConfigurationService cfgSvc = serviceRegistry.getService(ConfigurationService.class);
        Object explicitBeanContainer = configurationValues.get("hibernate.resource.beans.container");
        if (explicitBeanContainer != null) {
            return this.interpretExplicitBeanContainer(explicitBeanContainer, classLoaderService, serviceRegistry);
        }
        boolean isCdiAvailable = ManagedBeanRegistryInitiator.isCdiAvailable(classLoaderService);
        Object beanManagerRef = cfgSvc.getSettings().get("javax.persistence.bean.manager");
        if (beanManagerRef == null) {
            beanManagerRef = cfgSvc.getSettings().get("jakarta.persistence.bean.manager");
        }
        if (beanManagerRef != null) {
            if (!isCdiAvailable) {
                BeansMessageLogger.BEANS_LOGGER.beanManagerButCdiNotAvailable(beanManagerRef);
            }
            return CdiBeanContainerBuilder.fromBeanManagerReference(beanManagerRef, serviceRegistry);
        }
        if (isCdiAvailable) {
            BeansMessageLogger.BEANS_LOGGER.noBeanManagerButCdiAvailable();
        }
        return null;
    }

    private BeanContainer interpretExplicitBeanContainer(Object explicitSetting, ClassLoaderService classLoaderService, ServiceRegistryImplementor serviceRegistry) {
        Class<BeanContainer> containerClass;
        if (explicitSetting == null) {
            return null;
        }
        if (explicitSetting instanceof BeanContainer) {
            return (BeanContainer)explicitSetting;
        }
        if (explicitSetting instanceof Class) {
            containerClass = (Class<BeanContainer>)explicitSetting;
        } else {
            String name = explicitSetting.toString();
            Class<BeanContainer> selected = serviceRegistry.getService(StrategySelector.class).selectStrategyImplementor(BeanContainer.class, name);
            containerClass = selected != null ? selected : classLoaderService.classForName(name);
        }
        try {
            return (BeanContainer)containerClass.newInstance();
        }
        catch (Exception e) {
            throw new InstantiationException("Unable to instantiate specified BeanContainer : " + containerClass.getName(), containerClass, e);
        }
    }

    private static boolean isCdiAvailable(ClassLoaderService classLoaderService) {
        try {
            ManagedBeanRegistryInitiator.cdiBeanManagerClass(classLoaderService);
            return true;
        }
        catch (ClassLoadingException e) {
            return false;
        }
    }

    public static Class cdiBeanManagerClass(ClassLoaderService classLoaderService) throws ClassLoadingException {
        try {
            return classLoaderService.classForName("javax.enterprise.inject.spi.BeanManager");
        }
        catch (ClassLoadingException e) {
            return classLoaderService.classForName("jakarta.enterprise.inject.spi.BeanManager");
        }
    }
}

