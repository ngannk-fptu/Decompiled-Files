/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.managedbeans;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.InitialContextHelper;
import com.sun.jersey.server.impl.managedbeans.ManagedBeanComponentProviderFactory;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class ManagedBeanComponentProviderFactoryInitilizer {
    private static final Logger LOGGER = Logger.getLogger(ManagedBeanComponentProviderFactoryInitilizer.class.getName());

    public static void initialize(ResourceConfig rc) {
        try {
            InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                return;
            }
            Object injectionMgr = ic.lookup("com.sun.enterprise.container.common.spi.util.InjectionManager");
            if (injectionMgr == null) {
                LOGGER.config("The managed beans injection manager API is not available. JAX-RS managed beans support is disabled.");
                return;
            }
            Method createManagedObjectMethod = injectionMgr.getClass().getMethod("createManagedObject", Class.class);
            Method destroyManagedObjectMethod = injectionMgr.getClass().getMethod("destroyManagedObject", Object.class);
            rc.getSingletons().add(new ManagedBeanComponentProviderFactory(injectionMgr, createManagedObjectMethod, destroyManagedObjectMethod));
        }
        catch (NamingException ex) {
            LOGGER.log(Level.CONFIG, "The managed beans injection manager API is not available. JAX-RS managed beans support is disabled.", ex);
        }
        catch (NoSuchMethodException ex) {
            LOGGER.log(Level.SEVERE, "The managed beans injection manager API does not conform to what is expected. JAX-RS managed beans support is disabled.", ex);
        }
        catch (SecurityException ex) {
            LOGGER.log(Level.SEVERE, "Security issue when configuring to use the managed beans injection manager API. JAX-RS managed beans support is disabled.", ex);
        }
        catch (LinkageError ex) {
            LOGGER.log(Level.SEVERE, "Linkage error when configuring to use the managed beans injection manager API. JAX-RS managed beans support is disabled.", ex);
        }
    }
}

