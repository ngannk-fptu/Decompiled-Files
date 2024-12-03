/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerContextLoaderListener
 *  com.atlassian.spring.container.SpringContainerContext
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.config.bootstrap;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.spring.BootstrappedContainerContext;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.spring.container.ContainerContextLoaderListener;
import com.atlassian.spring.container.SpringContainerContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class BootstrappedContextLoaderListener
extends ContainerContextLoaderListener
implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(BootstrappedContextLoaderListener.class);

    public boolean canInitialiseContainer() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        if (bootstrapManager == null) {
            return false;
        }
        if (!bootstrapManager.isBootstrapped()) {
            return false;
        }
        boolean isHibernateSetUp = bootstrapManager.getHibernateConfig().isHibernateSetup();
        if (!isHibernateSetUp && BootstrapUtils.getBootstrapManager().isSetupComplete()) {
            log.error("Hibernate not yet set up, but setup is complete - can't initalise container - corrupt project.cfg.xml?");
        }
        return isHibernateSetUp;
    }

    protected ApplicationContext loadParentContext(ServletContext servletContext) throws BeansException {
        return BootstrapUtils.getBootstrapContext();
    }

    protected SpringContainerContext getNewSpringContainerContext() {
        return new BootstrappedContainerContext();
    }
}

