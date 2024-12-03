/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.spi.ManagedBeanRegistryInitiator;
import org.hibernate.service.ServiceRegistry;

public class CdiBeanContainerBuilder {
    private static final String CONTAINER_FQN_IMMEDIATE = "org.hibernate.resource.beans.container.internal.CdiBeanContainerImmediateAccessImpl";
    private static final String CONTAINER_FQN_DELAYED = "org.hibernate.resource.beans.container.internal.CdiBeanContainerDelayedAccessImpl";
    private static final String CONTAINER_FQN_EXTENDED = "org.hibernate.resource.beans.container.internal.CdiBeanContainerExtendedAccessImpl";
    private static final String BEAN_MANAGER_EXTENSION_FQN = "org.hibernate.resource.beans.container.spi.ExtendedBeanManager";

    public static BeanContainer fromBeanManagerReference(Object beanManagerRef, ServiceRegistry serviceRegistry) {
        Class ctorArgType;
        Class containerClass;
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        Class beanManagerClass = ManagedBeanRegistryInitiator.cdiBeanManagerClass(classLoaderService);
        Class extendedBeanManagerClass = CdiBeanContainerBuilder.getHibernateClass(BEAN_MANAGER_EXTENSION_FQN);
        if (extendedBeanManagerClass.isInstance(beanManagerRef)) {
            containerClass = CdiBeanContainerBuilder.getHibernateClass(CONTAINER_FQN_EXTENDED);
            ctorArgType = extendedBeanManagerClass;
        } else {
            ctorArgType = beanManagerClass;
            ConfigurationService cfgService = serviceRegistry.getService(ConfigurationService.class);
            boolean delayAccessToCdi = (Boolean)NullnessHelper.coalesceSuppliedValues(() -> cfgService.getSetting("hibernate.delay_cdi_access", StandardConverters.BOOLEAN), () -> {
                Boolean oldSetting = cfgService.getSetting("hibernate.delay_cdi_access", StandardConverters.BOOLEAN);
                return oldSetting;
            }, () -> false);
            containerClass = delayAccessToCdi ? CdiBeanContainerBuilder.getHibernateClass(CONTAINER_FQN_DELAYED) : CdiBeanContainerBuilder.getHibernateClass(CONTAINER_FQN_IMMEDIATE);
        }
        try {
            Constructor ctor = containerClass.getDeclaredConstructor(ctorArgType);
            try {
                ReflectHelper.ensureAccessibility(ctor);
                return (BeanContainer)ctor.newInstance(ctorArgType.cast(beanManagerRef));
            }
            catch (InvocationTargetException e) {
                throw new HibernateException("Problem building " + containerClass.getName(), e.getCause());
            }
            catch (Exception e) {
                throw new HibernateException("Problem building " + containerClass.getName(), e);
            }
        }
        catch (NoSuchMethodException e) {
            throw new HibernateException(String.format(Locale.ENGLISH, "Could not locate proper %s constructor", containerClass.getName()), e);
        }
    }

    private static <T> Class<T> getHibernateClass(String fqn) {
        try {
            return Class.forName(fqn, true, CdiBeanContainerBuilder.class.getClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new HibernateException("Unable to locate Hibernate class by name via reflection : " + fqn, e);
        }
    }
}

