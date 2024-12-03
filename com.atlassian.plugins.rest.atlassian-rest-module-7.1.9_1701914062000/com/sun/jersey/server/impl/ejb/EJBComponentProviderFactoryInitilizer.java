/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.InitialContextHelper;
import com.sun.jersey.server.impl.ejb.EJBComponentProviderFactory;
import com.sun.jersey.server.impl.ejb.EJBExceptionMapper;
import com.sun.jersey.server.impl.ejb.EJBInjectionInterceptor;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class EJBComponentProviderFactoryInitilizer {
    private static final Logger LOGGER = Logger.getLogger(EJBComponentProviderFactoryInitilizer.class.getName());

    public static void initialize(ResourceConfig rc) {
        try {
            InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                return;
            }
            Object interceptorBinder = ic.lookup("java:org.glassfish.ejb.container.interceptor_binding_spi");
            if (interceptorBinder == null) {
                LOGGER.config("The EJB interceptor binding API is not available. JAX-RS EJB support is disabled.");
                return;
            }
            Method interceptorBinderMethod = interceptorBinder.getClass().getMethod("registerInterceptor", Object.class);
            EJBInjectionInterceptor interceptor = new EJBInjectionInterceptor();
            try {
                interceptorBinderMethod.invoke(interceptorBinder, interceptor);
            }
            catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error when configuring to use the EJB interceptor binding API. JAX-RS EJB support is disabled.", ex);
                return;
            }
            rc.getSingletons().add(new EJBComponentProviderFactory(interceptor));
            rc.getClasses().add(EJBExceptionMapper.class);
        }
        catch (NamingException ex) {
            LOGGER.log(Level.CONFIG, "The EJB interceptor binding API is not available. JAX-RS EJB support is disabled.", ex);
        }
        catch (NoSuchMethodException ex) {
            LOGGER.log(Level.SEVERE, "The EJB interceptor binding API does not conform to what is expected. JAX-RS EJB support is disabled.", ex);
        }
        catch (SecurityException ex) {
            LOGGER.log(Level.SEVERE, "Security issue when configuring to use the EJB interceptor binding API. JAX-RS EJB support is disabled.", ex);
        }
        catch (LinkageError ex) {
            LOGGER.log(Level.SEVERE, "Linkage error when configuring to use the EJB interceptor binding API. JAX-RS EJB support is disabled.", ex);
        }
    }
}

