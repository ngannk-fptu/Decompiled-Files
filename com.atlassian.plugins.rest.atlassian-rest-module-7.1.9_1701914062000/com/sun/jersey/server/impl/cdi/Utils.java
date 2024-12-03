/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.context.ApplicationScoped
 *  javax.enterprise.context.spi.CreationalContext
 *  javax.enterprise.inject.AmbiguousResolutionException
 *  javax.enterprise.inject.spi.Bean
 *  javax.enterprise.inject.spi.BeanManager
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.CDIExtension;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

public class Utils {
    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    private Utils() {
    }

    public static Bean<?> getBean(BeanManager beanManager, Class<?> clazz) {
        Set beans = beanManager.getBeans(clazz, new Annotation[0]);
        if (beans.isEmpty()) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(String.format("No CDI beans found in bean manager, %s, for type %s", beanManager, clazz));
            }
            return null;
        }
        try {
            return beanManager.resolve(beans);
        }
        catch (AmbiguousResolutionException ex) {
            if (Utils.isSharedBaseClass(clazz, beans)) {
                try {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("Ambiguous resolution exception caught when resolving bean %s. Trying to resolve by the type %s", beans, clazz));
                    }
                    return beanManager.resolve(Utils.getBaseClassSubSet(clazz, beans));
                }
                catch (AmbiguousResolutionException ex2) {
                    return null;
                }
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(String.format("Failed to resolve bean %s.", beans));
            }
            return null;
        }
    }

    public static <T> T getInstance(BeanManager beanManager, Class<T> c) {
        Bean<?> bean = Utils.getBean(beanManager, c);
        if (bean == null) {
            return null;
        }
        CreationalContext creationalContext = beanManager.createCreationalContext(bean);
        Object result = beanManager.getReference(bean, c, creationalContext);
        return c.cast(result);
    }

    public static CDIExtension getCdiExtensionInstance(BeanManager beanManager) {
        Bean<?> bean = Utils.getBean(beanManager, CDIExtension.class);
        if (bean == null) {
            return null;
        }
        return (CDIExtension)beanManager.getContext(ApplicationScoped.class).get(bean);
    }

    private static boolean isSharedBaseClass(Class<?> clazz, Set<Bean<?>> beans) {
        for (Bean<?> bean : beans) {
            if (clazz.isAssignableFrom(bean.getBeanClass())) continue;
            return false;
        }
        return true;
    }

    private static Set<Bean<?>> getBaseClassSubSet(Class<?> clazz, Set<Bean<?>> beans) {
        for (Bean<?> bean : beans) {
            if (clazz != bean.getBeanClass()) continue;
            return Collections.singleton(bean);
        }
        return beans;
    }
}

