/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ConfigurableApplicationContext
 */
package com.atlassian.config.util;

import com.atlassian.config.HomeLocator;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.bootstrap.BootstrapException;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class BootstrapUtils {
    private static final Logger log = LoggerFactory.getLogger(BootstrapUtils.class);
    private static ApplicationContext bootstrapContext;
    private static AtlassianBootstrapManager bootstrapManager;

    public static void init(ApplicationContext bootstrapContext, ServletContext servletContext) throws BootstrapException {
        ((HomeLocator)bootstrapContext.getBean("homeLocator")).lookupServletHomeProperty(servletContext);
        BootstrapUtils.setBootstrapContext(bootstrapContext);
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        if (bootstrapManager == null) {
            throw new BootstrapException("Could not initialise boostrap manager");
        }
        bootstrapManager.init();
        if (!bootstrapManager.isBootstrapped()) {
            throw new BootstrapException("Unable to bootstrap application: " + bootstrapManager.getBootstrapFailureReason());
        }
    }

    public static ApplicationContext getBootstrapContext() {
        return bootstrapContext;
    }

    public static void setBootstrapContext(ApplicationContext bootstrapContext) {
        BootstrapUtils.bootstrapContext = bootstrapContext;
    }

    public static AtlassianBootstrapManager getBootstrapManager() {
        if (bootstrapManager == null && bootstrapContext != null) {
            bootstrapManager = (AtlassianBootstrapManager)bootstrapContext.getBean("bootstrapManager");
        }
        if (bootstrapManager == null) {
            Exception e = new Exception();
            String context = e.getStackTrace().length > 1 ? e.getStackTrace()[1].toString() : "Unknown caller";
            log.warn("Attempting to retrieve bootstrap manager before it is set up: " + context);
        }
        return bootstrapManager;
    }

    public static void setBootstrapManager(AtlassianBootstrapManager bootstrapManager) {
        BootstrapUtils.bootstrapManager = bootstrapManager;
    }

    public static void closeContext() {
        if (bootstrapContext != null && bootstrapContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext)bootstrapContext).close();
        }
        bootstrapContext = null;
        bootstrapManager = null;
    }
}

