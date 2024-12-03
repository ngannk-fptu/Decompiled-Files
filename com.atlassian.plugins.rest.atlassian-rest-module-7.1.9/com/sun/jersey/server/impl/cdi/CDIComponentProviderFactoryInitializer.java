/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.InitialContextHelper;
import com.sun.jersey.server.impl.cdi.CDIComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

public class CDIComponentProviderFactoryInitializer {
    private static final Logger LOGGER = Logger.getLogger(CDIComponentProviderFactoryInitializer.class.getName());
    private static final String BEAN_MANAGER_CLASS = "javax.enterprise.inject.spi.BeanManager";
    private static final String WELD_SERVLET_PACKAGE = "org.jboss.weld.environment.servlet";

    public static void initialize(WebConfig wc, ResourceConfig rc, WebApplication wa) {
        ServletContext sc = wc.getServletContext();
        Object beanManager = CDIComponentProviderFactoryInitializer.lookup(sc);
        if (beanManager == null) {
            LOGGER.config("The CDI BeanManager is not available. JAX-RS CDI support is disabled.");
            return;
        }
        rc.getSingletons().add(new CDIComponentProviderFactory(beanManager, rc, wa));
        LOGGER.info("CDI support is enabled");
    }

    private static Object lookup(ServletContext sc) {
        Object beanManager = null;
        beanManager = CDIComponentProviderFactoryInitializer.lookupInJndi("java:comp/BeanManager");
        if (beanManager != null) {
            return beanManager;
        }
        beanManager = CDIComponentProviderFactoryInitializer.lookupInServletContext(sc, BEAN_MANAGER_CLASS);
        if (beanManager != null) {
            return beanManager;
        }
        beanManager = CDIComponentProviderFactoryInitializer.lookupInServletContext(sc, "org.jboss.weld.environment.servlet.javax.enterprise.inject.spi.BeanManager");
        if (beanManager != null) {
            return beanManager;
        }
        return null;
    }

    private static Object lookupInJndi(String name) {
        try {
            InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                return null;
            }
            Object beanManager = ic.lookup(name);
            if (beanManager == null) {
                LOGGER.config("The CDI BeanManager is not available at " + name);
                return null;
            }
            LOGGER.config("The CDI BeanManager is at " + name);
            return beanManager;
        }
        catch (NamingException ex) {
            LOGGER.log(Level.CONFIG, "The CDI BeanManager is not available at " + name, ex);
            return null;
        }
    }

    private static Object lookupInServletContext(ServletContext sc, String name) {
        Object beanManager = sc.getAttribute(name);
        if (beanManager == null) {
            LOGGER.config("The CDI BeanManager is not available at " + name);
            return null;
        }
        LOGGER.config("The CDI BeanManager is at " + name);
        return beanManager;
    }
}

